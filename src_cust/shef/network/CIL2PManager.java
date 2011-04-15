package shef.network;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.encog.mathutil.randomize.GaussianRandomizer;
import org.neuroph.core.Connection;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.nnet.comp.ThresholdNeuron;

import shef.instantiator.andortree.Node;
import shef.instantiator.andortree.Tuple;
import util.gdl.factory.GdlFactory;
import util.gdl.factory.exceptions.GdlFormatException;
import util.gdl.grammar.Gdl;
import util.gdl.grammar.GdlRelation;
import util.gdl.grammar.GdlSentence;
import util.statemachine.MachineState;
import util.symbol.factory.exceptions.SymbolFormatException;
import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.Expression;
import cs227b.teamIago.resolver.Predicate;
import cs227b.teamIago.resolver.Term;

/**
 * Instantiates a {@link CIL2PNet} shef.network and is able to query it
 * 
 * @author jonathan
 * 
 */
public class CIL2PManager {

    /** List of player names */
    public ArrayList<Term> playerList = new ArrayList<Term>();
    private ArrayList<Gdl> playerListGGP = new ArrayList<Gdl>();

    /**
     * The number of times calculate is called when output is requested from
     * <code>getOutput</code> calculate is called
     * 
     * This MAY not be necessary, but all sample code thus far suggests
     */
    public final int CALCULATE_MULTIPLE_TIMES = 10;

    /**
     * Network being managed
     */
    public CIL2PNet network;

    /**
     * Create a manager for this shef.network
     */
    public CIL2PManager(CIL2PNet network) {
        this.network = network;
        // generate player list
        for (Predicate g : network.goalHash.keySet()) {
            Term player = (Atom) g.secondOp();
            if (!playerList.contains(player)) {
                playerList.add(player);
                try {
					playerListGGP.add(GdlFactory.create("(role " + player.toString() +")"));
				} catch (GdlFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SymbolFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }

    }

    /**
     * Prints a set of goal nodes as multiple CIL2P trees
     * 
     * @param goalProofs
     */
    public static void printCIL2P_tree(Collection<Node> goalProofs) {

        for (Node goal : goalProofs) {
            System.out.println(goal.printTree());
        }

    }

    /**
     * Get the value of a given state by querying the CIL2P shef.network.
     * @param state
     * @param player
     */
    public List<Double> getStateValues(final MachineState state){
    	propagateInput(state);
        
        ArrayList<Double> scores = new ArrayList<Double>();
        for(Term playerName : playerList){
        	scores.add(getPlayerScore(playerName));
        }
    	return scores;
    }
    
    /**
     * Get the value of a given state by querying the CIL2P shef.network.
     * @param state
     * @param player
     */
    public double getStateValue(final MachineState state, int playerID){
    	propagateInput(state);
        return getPlayerScore(playerList.get(playerID));
    }
    
    private final double sigmaOverTwo = 0.00005;
    private final double sigmaOverTwoSq = sigmaOverTwo*sigmaOverTwo; 
	private final GaussianRandomizer gauss = new GaussianRandomizer(0, sigmaOverTwoSq);
	 /**
     * Get a state Gaussian
     * @param state
     * @param player
     * @return a Gaussian value for the state and player with a Gaussian random factor
     */
    public double getStateValueGaussian(final MachineState state, int playerID){
    	propagateInput(state);
    	double sc = getPlayerScore(playerList.get(playerID))/100d;
    	double gaussR = gauss.randomize(0);
        return sc + gaussR;
    }
	
    public void propagateInput(final MachineState state){
    	Set<GdlSentence> stateElements = state.getContents();
		
        // reset the state
    	network.n.reset();
    	
       	for (Entry<GdlRelation, Neuron> fact : network.queryHashGGPBase.entrySet()) {
       		if(stateElements.contains(fact.getKey())){
       			fact.getValue().setInput(1d);
       		} else {
       			fact.getValue().setInput(-1d);
       		}
       	}

        // calculate
        for (int i = 0; i < CALCULATE_MULTIPLE_TIMES; i++) {
            this.network.n.calculate();
        }
    }


	private double getPlayerScore(final Term playerName) {
        // get outputs
        double playerSum = 0f;
        double totalSum = 0f;
        for (Entry<Goal, ThresholdNeuron> goal : network.goalHash.entrySet()) {
            Goal goalClause = goal.getKey();
            ThresholdNeuron goalNeuron = goal.getValue();

            // modulate the neuron output between 0 and 1
            double vNeuron = (goalNeuron.getOutput() + 1d) / 2d;
            // should be modulated betwen 0 and 1
            assert vNeuron <= 1 && vNeuron >= 0;
            
            double nScore = vNeuron * goalClause.score;
            // keep a track of possible output
            totalSum += nScore;

            // if this neuron is attached to the player we're
            // looking at add the value to their score
            if (playerName.equals(goalClause.player)) {
                playerSum += nScore;
            }
        }
        double stateScore = (playerSum / totalSum) * 100f;
        return stateScore;
    }

    /*
     * TESTING AND DEBUG METHODS
     */
    /**
     * Find the output of the shef.network given an input vector of the state This
     * requires an ordered set of input
     * 
     * <b>This should be FAST</b> as this will be the function called by the UCT
     * functions to complete UCT rollouts
     * 
     * @param queryValues
     *            an ordered array of input values to each neuron
     * @return the output of the shef.network for the goal neurons
     */
    public double[] getOutput(double... queryValues) {

        double[] inputNeuronValues = new double[network.inputNeuronCount];
        double[] outputNeuronValues = new double[network.goalNeuronCount];
        NeuralNetwork net = network.n;

        // XXX this may be SLOW - may have to precompile all of this?
        for (int i = 0; i < network.queryNeuronCount; i++) {
            inputNeuronValues[network.queryNeuronIndices[i]] = queryValues[i];
        }

        net.setInput(inputNeuronValues);
        // XXX what makes it necessary to
        for (int i = 0; i < CALCULATE_MULTIPLE_TIMES; i++) {
            net.calculate();
        }
        double[] networkOut = net.getOutput();

        // return only goal neurons output
        for (int i = 0; i < network.goalNeuronCount; i++) {
            outputNeuronValues[i] = networkOut[network.goalNeuronDetails.get(i)
                    .getSecond()];
        }

        return outputNeuronValues;

    }

    /**
     * Print the output from <code>getOutput</code>
     * 
     * @param queryValues
     *            the input values passed into <code>getOutput</code>
     */
    public void printOutput(double... queryValues) {

        System.out.println(Arrays.toString(getOutput(queryValues)));

    }

    /**
     * Return a score of the current state for each player Each goal neuron has
     * activation [-1, 1] -> [0, 1] = v_n
     * 
     * @param queryValues
     * @return the summed score for each player
     */
    public double[] getAllPlayerScores(double... queryValues) {
        getOutput(queryValues);

        double[] player_V = new double[playerList.size()];
        for (int i = 0; i < playerList.size(); i++) {
            player_V[i] = getPlayerScore(playerList.get(i));
        }

        return player_V;

    }

    /**
     * print the scores returned by <code>getScores</code> given the
     * <code>queryValues</code> provided
     * 
     * @param queryValues
     *            input to shef.network
     */
    public void printScores(double... queryValues) {

        System.out.println(Arrays.toString(getAllPlayerScores(queryValues)));

    }

    /**
     * get overall neural layout
     * 
     * @return #input (preds), #hidden (clauses), #output (heads)
     */
    public int[] getInfo() {

        return new int[] { network.inputLayer.getNeurons().size(),
                network.hiddenLayer.getNeurons().size(),
                network.outputLayer.getNeurons().size() };

    }

    /**
     * print information about the overall neural layout<br/>
     * <code>#input units, [input unit expressions], #hidden units,
     * #output units, [output unit expressions]</code>
     */
    public void printInfo() {

        System.out
                .println("# input  units:"
                        + network.inputLayer.getNeurons().size()
                        + "(total predicates)");
        System.out.println(network.inputHash.keySet());
        System.out.println("# hidden units:"
                + network.hiddenLayer.getNeurons().size() + "(total clauses)");
        System.out.println("# output units:"
                + network.outputLayer.getNeurons().size()
                + "(total unique heads)");
        System.out.println(network.outputHash.keySet());

    }

    /**
     * get information about the most important neurons in the current shef.network
     * 
     * @return [#input (truths), #output (goals)]
     */
    public int[] getPlayInfo() {

        return new int[] { network.queryNeuronDetails.size(),
                network.goalNeuronDetails.size() };

    }

    /**
     * print information about the most important neurons in the current shef.network
     * <b>query neurons</b> and <b>goal neurons</b>
     */
    public void printPlayInfo() {

        System.out
                .println("# query units " + network.queryNeuronDetails.size());
        System.out.println(network.queryNeuronDetails);
        System.out.println("# goal units " + network.goalNeuronDetails.size());
        System.out.println(network.goalNeuronDetails);

    }

    /**
     * @return the value amin which is the minimum activation a node must have
     *         to consider its output true
     */
    public double getMaxError() {

        return network.Amin;

    }

    /**
     * for each node matching <code>(true ?term)</code> is an input node in the
     * graph, print: <br/>
     * <code>(expression, output)</code>
     */
    public void printQueryInputActivation() {

        for (Tuple<Expression, Integer> tup : network.queryNeuronDetails) {
            System.out.println(tup.getFirst() + "\t-> "
                    + network.inputHash.get(tup.getFirst()).getOutput());
        }

    }

    /**
     * for each node on the input layer print <br/>
     * <code>(expression, #inputs, input, #outputs, output)</code>
     */
    public void printInputActivation() {

        for (Entry<Expression, Neuron> tup : network.inputHash.entrySet()) {
            System.out.println(tup.getKey() + "\tin["
                    + tup.getValue().getInputConnections().size() + "]: "
                    + tup.getValue().getNetInput() + "out:["
                    + tup.getValue().getOutConnections().size() + "]: "
                    + tup.getValue().getOutput());
        }

    }

    /**
     * For each node matching <code>(GOAL ?player ?score)</code> print. <br/>
     * <code>(expression, output)</code>
     */
    public void printGoalOutputActivation() {

        for (Tuple<Expression, Integer> tup : network.goalNeuronDetails) {
            System.out.println(tup.getFirst() + "\t-> "
                    + network.outputHash.get(tup.getFirst()).getOutput());
        }

    }

    /**
     * Print details about the output layer of the shef.network. <br/>
     * <code>(predicate, output, input, #inputs, [input activations])</code>
     */
    public void printOutputActivation() {

        for (Entry<Expression, ThresholdNeuron> tup : network.outputHash
                .entrySet()) {
            String inps = "";
            for (Connection in : tup.getValue().getInputConnections()) {
                inps = inps + (in.getWeight().getValue() < 0 ? "N" : "")
                        + in.getFromNeuron().getOutput() + " ";
            }
            String out = tup.getKey() + "\t-> " + " " + " in["
                    + tup.getValue().getInputConnections().size() + "]: "
                    + tup.getValue().getNetInput() + " out:"
                    + tup.getValue().getOutput() + " ~ " + inps;
            System.out.println(out);
        }

    }

    /**
     * Print details about the hidden layer of the shef.network. <br/>
     * <code>(input, output)</code>
     */
    public void printHiddenActivation() {

        for (Neuron hidden : network.hiddenLayer.getNeurons()) {
            System.out.println("in:" + hidden.getNetInput() + "out:"
                    + hidden.getOutput());
        }

    }

    /**
     * Get query predicates in order they appear in the input layer.
     */
    public List<Expression> getQueryPredicates() {

        List<Expression> returnQs = new ArrayList<Expression>();
        for (Tuple<Expression, Integer> tup : network.queryNeuronDetails) {
            returnQs.add(tup.getFirst());
        }

        return returnQs;

    }

}
