package shef.network;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.input.InputFunction;
import org.neuroph.core.transfer.Linear;
import org.neuroph.core.transfer.TransferFunction;
import org.neuroph.nnet.comp.ThresholdNeuron;

import shef.instantiator.andortree.Node;
import shef.instantiator.andortree.Tuple;
import util.gdl.factory.GdlFactory;
import util.gdl.factory.exceptions.GdlFormatException;
import util.gdl.grammar.GdlRelation;
import util.symbol.factory.exceptions.SymbolFormatException;
import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Expression;
import cs227b.teamIago.resolver.Predicate;

public class CIL2PNet {

	private static final int RECURRENT_WEIGHT = 1;

	final NeuralNetwork n = new NeuralNetwork();
	final Layer inputLayer = new Layer();
	final Layer outputLayer = new Layer();
	final Layer hiddenLayer = new Layer();

	private final HashMap<Expression, Neuron> inputHash = new HashMap<Expression, Neuron>();
	private final HashMap<Expression, ThresholdNeuron> outputHash = new HashMap<Expression, ThresholdNeuron>();
	
	private final HashMap<Predicate, Neuron> queryHash = new HashMap<Predicate, Neuron>();
	

	private final List<Tuple<Expression, Integer>> queryNeuronDetails = new ArrayList<Tuple<Expression,Integer>>();
	private final HashMap<Expression, Integer> sameConsequent = new HashMap<Expression, Integer>();
	private final HashMap<Expression, Set<ExpList>> clauseUniq= new HashMap<Expression, Set<ExpList>>();

	final List<Tuple<Expression, Integer>> goalNeuronDetails = new ArrayList<Tuple<Expression,Integer>>();
	final HashMap<GdlRelation, Neuron> queryHashGGPBase = new HashMap<GdlRelation, Neuron>();
	final HashMap<Goal, ThresholdNeuron> goalHash = new HashMap<Goal, ThresholdNeuron>();


	

	private static final double BETA = 1;
	private static final double ALPHA_SHIFT = 0.2;
	private static final double AMIN_SHIFT = 0.0003;
	private static final TransferFunction g = new Linear();
	private static final TransferFunction h = new Bipolar(BETA);
	private static final InputFunction inputNeuronIn = new InputFunction();
	private static final InputFunction outputNeuronIn = new InputFunction();


	private final List<Node> clause = new ArrayList<Node>();
	
	
	private final boolean weightOne;

	public double Amin;
	int inputNeuronCount;
	int goalNeuronCount;
	int queryNeuronCount;
	int[] queryNeuronIndices = null;

	/**
	 * This produces a neural shef.network which represents a set 
	 * 
	 * @param p
	 * @param weightOne 
	 */
	public CIL2PNet(boolean weightOne) {
		n.addLayer(outputLayer);
		n.addLayer(hiddenLayer);
		n.addLayer(inputLayer);
		n.setInputNeurons(inputLayer.getNeurons());
		n.setOutputNeurons(outputLayer.getNeurons());
		this.weightOne = weightOne;
	}


	/**
	 * Create input and output nodes for this shef.network
	 * 
	 * @param node
	 */
	public void addTree(Node node) {
		Expression key = node.getHead();
		switch (node.getNodeType()) {
		case OR:
		case VAR_OR:
			// add this neuron IF no node with the same head and
			// premise set has been seen before
			addOutputNeuronPremiseCheck(node);
			break;
		case TRUTH:
			addInputNeuron(key, key.getVars().size() == 0);
			break;	
		case AND:
		case VAR_AND:
			addInputNeuron(key, false);
			break;
		default:
		    break;
		}
		
		for (Node chNode : node.getChildren()) {
			addTree(chNode);
		}
	}

	private void addOutputNeuronPremiseCheck(Node node) {
		// if the head hasn't been seen before start keeping a set of
		// premises which fulfil it
		Set<ExpList> fulfils = clauseUniq.get(node.getHead());
		if(fulfils == null){
			fulfils = new HashSet<ExpList>();
			clauseUniq.put(node.getHead(), fulfils);
		} 
		// generate the ExpList to check if it has been seen before
		ExpList premises = new ExpList();
		for(Node child : node.getChildren()){
			if(child.negated){
				premises.add(new Predicate("not", new Expression[]{child.getHead()}));
			}else{
				premises.add(child.getHead());
			}
		}
		if(!fulfils.contains(premises)){
			fulfils.add(premises);
			clause.add(node);
			addOutputNeuron(node.getHead());
		}
		
	}


	/**
	 * setup hidden nodes and set all relevant weights
	 */
	public void finaliseNetwork() {

		// find a value for MAXP of this shef.network
		int MAXP = 0;
		for (Node n : clause) {
			int p = n.getChildCount();
			if (p > MAXP) {
				MAXP = p;
			}
		}
		
		for (Integer i : sameConsequent.values()) {
			if (i > MAXP) {
				MAXP = i;
			}
		}


		double Amin_gt = ((double) (MAXP - 1)) / ((double) MAXP + 1);
		double W = 0;
		// Michulke's optimisation
		if(weightOne){
			W = 1;
			Amin = (Amin_gt - 1)*ALPHA_SHIFT + 1;
			
		} else {
			// compute Amin and W
			// Amin > f this is a strict inequality
			// W >= g this is a non-strict inequality
			// manually increase Amin and W based on game complexity
			Amin = Amin_gt + AMIN_SHIFT; // TODO fix manual adjust
			
			// compute W
			double W_num = Math.log(1 + Amin) - Math.log(1 - Amin);
			double W_den = MAXP * (Amin - 1) + Amin + 1;
			
			double W_gte = (2.0 / BETA) * (W_num / W_den);
			// this is just a manual adjust
			W = W_gte; 
		}
		System.out.println(W);

		assert Amin > 0 && Amin < 1;
		assert W > 0;

		/*
		 * Process each clause - add a hidden neuron to N - connect each
		 * necessary input unit - connect to the necessary output neuron -
		 * assign threshold theta_L
		 */
		for (Node n : clause) {

			ThresholdNeuron hiddenNeuron = new ThresholdNeuron(new InputFunction(), h);
			for (Node chNode : n.getChildren()) {
				double conW = chNode.negated ? -W : W;
				hiddenNeuron.addInputConnection(getInputHash().get(chNode.getHead()), conW);
			}
			ThresholdNeuron out = getOutputHash().get(n.getHead());
			out.addInputConnection(hiddenNeuron, W);
			double thresh = ((1 + Amin) * (n.getChildCount() - 1)) * (W / 2.0);
			hiddenNeuron.setThresh(thresh);
			hiddenLayer.addNeuron(hiddenNeuron);
		}

		/*
		 * Setup the output unit thresholds and connect them to their respective
		 * input neuron. This gives the partially recurrent nature to the CIL2P
		 * algorithm
		 */
		for (Entry<Expression, ThresholdNeuron> e : getOutputHash().entrySet()) {
			Expression key = e.getKey();
			ThresholdNeuron outNeuron = e.getValue();
			if (getInputHash().containsKey(key)) {
				// a match exists for key, add a recurrent input
				Neuron inNeuron = getInputHash().get(e.getKey());
				inNeuron.addInputConnection(outNeuron, RECURRENT_WEIGHT);
			}

			int repeats = sameConsequent.get(key);
			double thetaANum = (1 + Amin) * (1 - repeats);
			double thresh = (thetaANum / 2.0) * W;
			outNeuron.setThresh(thresh);
		}
		// save the query neuron indices so it is easy to query
		queryNeuronCount = getQueryNeuronDetails().size();
		queryNeuronIndices = new int[queryNeuronCount];
		for (int i = 0; i < queryNeuronCount; i++) {
			queryNeuronIndices[i] = getQueryNeuronDetails().get(i).getSecond();
		}

		inputNeuronCount = getInputHash().size();
		goalNeuronCount = getGoalNeuronDetails().size();
		// save the shef.network externally to view in EasyNeuron
		n.save("test6.nnet");
		
		// query neurons should have their expression rebuilt to (true (p))
		// also make a copy which is accessible using GGP bases's syntax
		for(Tuple<Expression, Integer> t: getQueryNeuronDetails()){
		    try {
		    	Predicate truePred = new Predicate("true", new Expression[]{t.getFirst()});
		    	GdlRelation gdlTruePred = (GdlRelation) GdlFactory.create(truePred.toString().toLowerCase());		    	
		    	Neuron trueNeuron = inputLayer.getNeuronAt(t.getSecond());
		    	
		    	queryHash.put(truePred, trueNeuron);
				queryHashGGPBase.put(gdlTruePred, trueNeuron);
			} catch (GdlFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SymbolFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	/**
	 * Add an input neuron if necessary this method also populates a record of
	 * neurons which will be input as part of the query space
	 * 
	 * @param key
	 * @return
	 */
	private void addInputNeuron(Expression key, boolean asQueryNode) {
		Neuron inNeuron = getInputHash().get(key);

		// add a reference 
		// add a new neuron to the hash if necessary
		if (inNeuron == null) {
			inNeuron = new Neuron(inputNeuronIn, g);
			inputLayer.addNeuron(inNeuron);
			getInputHash().put(key, inNeuron);
			
			if(asQueryNode){
				getQueryNeuronDetails().add(	new Tuple<Expression, Integer>(key, inputLayer.getNeuronsCount() - 1));
			}
		}
	}

	/**
	 * Add an output neuron if necessary, also keep track of how many times it
	 * is used
	 * 
	 * @param key
	 */
	private void addOutputNeuron(Expression key) {
		ThresholdNeuron outNeuron = getOutputHash().get(key);
		if (outNeuron == null) {
			outNeuron = new ThresholdNeuron(outputNeuronIn, h);
			outputLayer.addNeuron(outNeuron);
			getOutputHash().put(key, outNeuron);
			sameConsequent.put(key, 1);
			
			if (key instanceof Predicate 
					&& ((Predicate) key).firstOp().toString().equalsIgnoreCase("GOAL")) {
				getGoalNeuronDetails().add(
						new Tuple<Expression, Integer>( key, outputLayer.getNeuronsCount() - 1)
						);
				Predicate goalPredicate = (Predicate) key;
				Goal goal = new Goal((Atom)goalPredicate.getOperands().get(0), 
				        (Atom)goalPredicate.getOperands().get(1));
				goalHash.put(goal, outNeuron);
			}
		} else {
			Integer i = sameConsequent.get(key);
			sameConsequent.put(key, i + 1);
		}
	}


	public List<Tuple<Expression, Integer>> getGoalNeuronDetails() {
		return goalNeuronDetails;
	}


	public HashMap<Expression, Neuron> getInputHash() {
		return inputHash;
	}


	public List<Tuple<Expression, Integer>> getQueryNeuronDetails() {
		return queryNeuronDetails;
	}


	public HashMap<Expression, ThresholdNeuron> getOutputHash() {
		return outputHash;
	}
}
