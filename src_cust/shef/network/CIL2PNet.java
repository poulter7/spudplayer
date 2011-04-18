package shef.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.input.InputFunction;
import org.neuroph.core.transfer.Linear;
import org.neuroph.core.transfer.TransferFunction;
import org.neuroph.nnet.comp.ThresholdNeuron;

import shef.instantiator.andortree.Node;
import util.gdl.factory.GdlFactory;
import util.gdl.factory.exceptions.GdlFormatException;
import util.gdl.grammar.GdlRelation;
import util.symbol.factory.exceptions.SymbolFormatException;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Expression;
import cs227b.teamIago.resolver.Predicate;

/**
 * 
 * @author jonathan
 * 
 */
public class CIL2PNet {
	/** will save the network using Neuroph's save feature if true */
	private boolean SAVE_NETWORK = false;
	
	/*
	 * Once the network is finalized these are the main access points for querying the network
	 */
	/** Map of GDL relation (truths) to Neuron in input layer */
	final HashMap<GdlRelation, Neuron> queryHashGGPBase = new HashMap<GdlRelation, Neuron>();
	/** Map of Goal to Neuron in output layer */
	final HashMap<Goal, ThresholdNeuron> goalHash = new HashMap<Goal, ThresholdNeuron>();
	/** The network itself */
	final NeuralNetwork n = new NeuralNetwork();

	/*
	 *  Dispensible datastructures used in building the network
	 *  if the game is considered a GAME_NETWORK then these are made null
	 *  after finalising.
	 */
	private HashMap<Predicate, Neuron> queryHash = new HashMap<Predicate, Neuron>();
	private HashMap<Expression, Neuron> inputHash = new HashMap<Expression, Neuron>();
	private HashMap<Expression, ThresholdNeuron> outputHash = new HashMap<Expression, ThresholdNeuron>();
	private HashMap<Expression, Integer> sameConsequent = new HashMap<Expression, Integer>();
	private HashMap<Expression, Set<ExpList>> clauseUniq = new HashMap<Expression, Set<ExpList>>();
	private List<Node> clauses = new ArrayList<Node>();
	private List<SimpleImmutableEntry<Expression, Integer>> queryNeuronDetails = new ArrayList<SimpleImmutableEntry<Expression, Integer>>();
	private List<SimpleImmutableEntry<Expression, Integer>> goalNeuronDetails = new ArrayList<SimpleImmutableEntry<Expression, Integer>>();
	private Layer inputLayer = new Layer();
	private Layer outputLayer = new Layer();
	private Layer hiddenLayer = new Layer();
	
	/*
	 * Network constants
	 */
	private static final int RECURRENT_WEIGHT = 1;
	private static final double BETA = 1;
	private static final double ALPHA_SHIFT = 0.2;
	private static final double AMIN_SHIFT = 0.0003;
	private static final TransferFunction g = new Linear();
	private static final TransferFunction h = new Bipolar(BETA);
	private static final InputFunction inputNeuronIn = new InputFunction();
	private static final InputFunction outputNeuronIn = new InputFunction();
	
	/*
	 * Network properties 
	 */
	private final boolean weightOne;
	private final boolean GAME_NETWORK;

	/** Minimum activation of a neuron for it to be TRUE */
	private double Amin;


	/**
	 * This produces a neural shef.network which represents a set
	 * 
	 * @param p
	 * @param weightOne
	 */
	public CIL2PNet(List<Node> goalProofs, boolean weightOne, boolean gamenet) {
		n.addLayer(getOutputLayer());
		n.addLayer(getHiddenLayer());
		n.addLayer(inputLayer);
		n.setInputNeurons(inputLayer.getNeurons());
		n.setOutputNeurons(getOutputLayer().getNeurons());
		this.weightOne = weightOne;
		this.GAME_NETWORK = gamenet;

		for (int i = 0; i < goalProofs.size(); i++) {
			addTree(goalProofs.get(i));
		}
		// setup hidden nodes and activation functions
		finaliseNetwork();
	}

	/**
	 * Create input and output nodes for this shef.network
	 * 
	 * @param node
	 */
	private void addTree(Node node) {
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
		if (fulfils == null) {
			fulfils = new HashSet<ExpList>();
			clauseUniq.put(node.getHead(), fulfils);
		}
		// generate the ExpList to check if it has been seen before
		ExpList premises = new ExpList();
		for (Node child : node.getChildren()) {
			if (child.negated) {
				premises.add(new Predicate("not", new Expression[] { child
						.getHead() }));
			} else {
				premises.add(child.getHead());
			}
		}
		if (!fulfils.contains(premises)) {
			fulfils.add(premises);
			clauses.add(node);
			addOutputNeuron(node.getHead());
		}

	}

	/**
	 * setup hidden nodes and set all relevant weights
	 */
	private void finaliseNetwork() {

		// find a value for MAXP of this shef.network
		int MAXP = 0;

		for (Node n : clauses) {
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

		final double Amin_gt = ((double) (MAXP - 1)) / ((double) MAXP + 1);
		final double W;
		// Michulke's optimisation
		if (weightOne) {
			Amin = (Amin_gt - 1) * ALPHA_SHIFT + 1;
			W = 1;

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

		assert Amin > 0 && Amin < 1;
		assert W > 0;

		/*
		 * Process each clause - add a hidden neuron to N - connect each
		 * necessary input unit - connect to the necessary output neuron -
		 * assign threshold theta_L
		 */
		for (Node n : clauses) {

			ThresholdNeuron hiddenNeuron = new ThresholdNeuron(
					new InputFunction(), h);
			for (Node chNode : n.getChildren()) {
				double conW = chNode.negated ? -W : W;
				hiddenNeuron.addInputConnection(getInputHash().get(
						chNode.getHead()), conW);
			}
			ThresholdNeuron out = getOutputHash().get(n.getHead());
			out.addInputConnection(hiddenNeuron, W);
			double thresh = ((1 + Amin) * (n.getChildCount() - 1)) * (W / 2.0);
			hiddenNeuron.setThresh(thresh);
			getHiddenLayer().addNeuron(hiddenNeuron);
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
		
		// save the shef.network externally to view in EasyNeuron
		if (SAVE_NETWORK) {
			n.save("network.nnet");
		}

		// query neurons should have their expression rebuilt to (true (p))
		// also make a copy which is accessible using GGP bases's syntax
		for (SimpleImmutableEntry<Expression, Integer> t : getQueryNeuronDetails()) {
			try {
				Predicate truePred = new Predicate("true", new Expression[] { t
						.getKey() });
				GdlRelation gdlTruePred = (GdlRelation) GdlFactory
						.create(truePred.toString().toLowerCase());
				Neuron trueNeuron = inputLayer.getNeuronAt(t.getValue());
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

		// not more modifications can be made
		// so free up some memory removing all of the unnecessary hashes
		if (GAME_NETWORK) {
			queryHash = null;
			inputHash = null;
			outputHash = null;
			sameConsequent = null;
			clauseUniq = null;
			clauses = null;
			queryNeuronDetails = null;
			goalNeuronDetails = null;
			inputLayer = null;
			outputLayer = null;
			hiddenLayer = null;
		}

	}

	/**
	 * Add an input neuron if necessary this method also populates a record of
	 * neurons which will be input as part of the query space
	 * 
	 * @param key
	 */
	private void addInputNeuron(Expression key, boolean asQueryNode) {
		Neuron inNeuron = getInputHash().get(key);

		// add a reference
		// add a new neuron to the hash if necessary
		if (inNeuron == null) {
			inNeuron = new Neuron(inputNeuronIn, g);
			inputLayer.addNeuron(inNeuron);
			getInputHash().put(key, inNeuron);

			if (asQueryNode) {
				SimpleImmutableEntry<Expression, Integer> indexMap = new SimpleImmutableEntry<Expression, Integer>(
						key, inputLayer.getNeuronsCount() - 1);
				getQueryNeuronDetails().add(indexMap);
			}
		}
	}

	/**
	 * Add an output neuron if necessary, also keep track of how many times it
	 * is used
	 * 
	 * @param key
	 *            the expresssion to add to the output
	 */
	private void addOutputNeuron(Expression key) {
		ThresholdNeuron outNeuron = getOutputHash().get(key);
		if (outNeuron == null) {
			// if it doesn't exist add it
			outNeuron = new ThresholdNeuron(outputNeuronIn, h);
			getOutputLayer().addNeuron(outNeuron);
			getOutputHash().put(key, outNeuron);
			sameConsequent.put(key, 1);

			// if it is a goal record it to the GOAL list
			if (key instanceof Predicate
					&& ((Predicate) key).firstOp().toString().equalsIgnoreCase(
							"GOAL")) {
				getGoalNeuronDetails().add(
						new SimpleImmutableEntry<Expression, Integer>(key,
								getOutputLayer().getNeuronsCount() - 1));
				Predicate goalPredicate = (Predicate) key;
				Goal goal = new Goal(goalPredicate.getOperands().get(0)
						.toString(), goalPredicate.getOperands().get(1)
						.toString());
				goalHash.put(goal, outNeuron);
			}
		} else {
			Integer i = sameConsequent.get(key);
			sameConsequent.put(key, i + 1);
		}
	}

	/**
	 * Return the indices in the output later at which the goal neurons sit.
	 * 
	 * @return indices of goal neurons
	 */
	List<SimpleImmutableEntry<Expression, Integer>> getGoalNeuronDetails() {
		return goalNeuronDetails;
	}

	/**
	 * Return mapping of expression to neuron for inputs.
	 * 
	 * @return the mapping
	 */
	HashMap<Expression, Neuron> getInputHash() {
		return inputHash;
	}

	/**
	 * Return the indices in the input layer at which query neurons sit.
	 * 
	 * @return indices of query neurons
	 */
	List<SimpleImmutableEntry<Expression, Integer>> getQueryNeuronDetails() {
		return queryNeuronDetails;
	}

	/**
	 * Return mapping of expression to neuron for outputs.
	 * 
	 * @return
	 */
	HashMap<Expression, ThresholdNeuron> getOutputHash() {
		return outputHash;
	}

	Layer getInputLayer() {
		return inputLayer;
	}

	Layer getHiddenLayer() {
		return hiddenLayer;
	}

	Layer getOutputLayer() {
		return outputLayer;
	}

	double getAMIN() {
		return Amin;
	}
}
