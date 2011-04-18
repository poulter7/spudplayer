package shef.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.encog.mathutil.randomize.GaussianRandomizer;
import org.neuroph.core.Connection;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.nnet.comp.ThresholdNeuron;

import shef.instantiator.andortree.Node;
import util.gdl.factory.GdlFactory;
import util.gdl.factory.exceptions.GdlFormatException;
import util.gdl.grammar.GdlProposition;
import util.gdl.grammar.GdlRelation;
import util.gdl.grammar.GdlSentence;
import util.statemachine.MachineState;
import util.statemachine.Role;
import util.symbol.factory.exceptions.SymbolFormatException;
import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.Expression;

/**
 * Instantiates a {@link CIL2PNet} shef.network and is able to query it
 * 
 * @author jonathan
 * 
 */
public class CIL2PManager {

	/** List of player names */
	public final List<Role> playerList = new ArrayList<Role>();

	/**
	 * The number of times calculate is called when output is requested from
	 * <code>getOutput</code> calculate is called
	 * 
	 * This MAY not be necessary, but all sample code thus far suggests
	 */
	public final int CALCULATE_MULTIPLE_TIMES = 8;

	/** Network being managed */
	public final CIL2PNet network;

	private final double sigmaOverTwo = 0.005;
	private final double sigmaOverTwoSq = sigmaOverTwo * sigmaOverTwo;
	private final GaussianRandomizer gauss = new GaussianRandomizer(0,
			sigmaOverTwoSq);

	/**
	 * Create a manager for this network
	 * 
	 * @param <E>
	 *            either Role or Atom
	 * @param network
	 */
	@SuppressWarnings("unchecked")
	public <E> CIL2PManager(final CIL2PNet network, final List<E> orderedRole) {
		this.network = network;
		if (orderedRole.get(0) instanceof Atom) {
			try {
				for (E r : orderedRole) {
					GdlProposition playerProp = (GdlProposition) GdlFactory.create(r.toString().toUpperCase());
					playerList.add(new Role(playerProp));
				}
			} catch (GdlFormatException e) {
				e.printStackTrace();
			} catch (SymbolFormatException e) {
				e.printStackTrace();
			}
		} else if (orderedRole.get(0) instanceof Role) {
			this.playerList.addAll((List<Role>) orderedRole);
		} else {
			throw new RuntimeException(
					"List of player roles not given as an excepted Class,"
							+ " use either cs227b.teamIago.resolver.Atom or util.statemachine.Role");
		}
	}

	/**
	 * Create a manager for this network, this provided inspectability for
	 * output but checking all player scores will be meaningless
	 * 
	 * @param network
	 */
	CIL2PManager(CIL2PNet network) {
		this.network = network;
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
	 * 
	 * @param state
	 * @param player
	 */
	public List<Double> getStateValues(final MachineState state) {
		inputMachineState(state);

		ArrayList<Double> scores = new ArrayList<Double>();
		for (Role playerName : playerList) {
			scores.add(getPlayerScore(playerName));
		}
		return scores;
	}

	/**
	 * Get the value of a given state by querying the CIL2P shef.network.
	 * 
	 * @param state
	 * @param player
	 */
	public double getStateValue(final MachineState state, int playerID) {
		inputMachineState(state);
		return getPlayerScore(playerList.get(playerID));
	}

	/**
	 * Get a state Gaussian
	 * 
	 * @param state
	 * @param player
	 * @return a Gaussian value for the state and player with a Gaussian random
	 *         factor
	 */
	public double getStateValueGaussian(final MachineState state, int playerID) {
		inputMachineState(state);
		double sc = getPlayerScore(playerList.get(playerID)) / 100d;
		double gaussR = gauss.randomize(0);
		return sc + gaussR;
	}

	/**
	 * Input the current MachineState into the network a turn the crank
	 * 
	 * @param state
	 *            the machine state to evaluate
	 */
	private void inputMachineState(final MachineState state) {
		Set<GdlSentence> stateElements = state.getContents();

		// reset the state
		network.n.reset();

		for (Entry<GdlRelation, Neuron> fact : network.queryHashGGPBase
				.entrySet()) {
			if (stateElements.contains(fact.getKey())) {
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

	/**
	 * Get the scores for a single player
	 * 
	 * @Deprecated
	 * @param role
	 * @return their score
	 */
	private double getPlayerScore(final Role role) {
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
			if (role.equals(goalClause.player)) {
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
	 * Find the output of the shef.network given an input vector of the state
	 * This requires an ordered set of input
	 * 
	 * <b>This should only be used in testing</b> not optimized
	 * 
	 * @param queryValues
	 *            an ordered array of input values to each neuron
	 * @return the output of the shef.network for the goal neurons
	 */
	double[] getOutput(double... queryValues) {

		double[] inputNeuronValues = new double[network.getInputLayer().getNeuronsCount()];
		double[] outputNeuronValues = new double[network.getOutputLayer().getNeuronsCount()];
		NeuralNetwork net = network.n;

		// XXX this may be SLOW - may have to precompile all of this?
		// set the input
		for (int i = 0; i < network.getQueryNeuronDetails().size(); i++) {
			int refIndex = network.getQueryNeuronDetails().get(i).getValue();
			inputNeuronValues[refIndex] = queryValues[i];
		}

		net.setInput(inputNeuronValues);
		// XXX what makes it necessary to
		for (int i = 0; i < CALCULATE_MULTIPLE_TIMES; i++) {
			net.calculate();
		}
		double[] networkOut = net.getOutput();

		// return only goal neurons output
		for (int i = 0; i < network.getGoalNeuronDetails().size(); i++) {
			outputNeuronValues[i] = networkOut[network.getGoalNeuronDetails().get(i)
					.getValue()];
		}

		return outputNeuronValues;

	}

	/**
	 * Print the output from <code>getOutput</code>
	 * 
	 * @param queryValues
	 *            the input values passed into <code>getOutput</code>
	 */
	void printOutput(double... queryValues) {

		System.out.println(Arrays.toString(getOutput(queryValues)));

	}

	/**
	 * Return a score of the current state for each player Each goal neuron has
	 * activation [-1, 1] -> [0, 1] = v_n
	 * 
	 * @param queryValues
	 * @return the summed score for each player
	 */
	double[] getAllPlayerScores(double... queryValues) {
		getOutput(queryValues);

		double[] player_V = new double[playerList.size()];
		for (int i = 0; i < playerList.size(); i++) {
			player_V[i] = getPlayerScore(playerList.get(i));
		}

		return player_V;

	}

	/**
	 * get overall neural layout
	 * 
	 * @return #input (preds), #hidden (clauses), #output (heads)
	 */
	int[] getInfo() {

		return new int[] { network.getInputLayer().getNeurons().size(),
				network.getHiddenLayer().getNeurons().size(),
				network.getOutputLayer().getNeurons().size() };

	}

	/**
	 * get information about the most important neurons in the current
	 * shef.network
	 * 
	 * @return [#input (truths), #output (goals)]
	 */
	int[] getPlayInfo() {

		return new int[] { network.getQueryNeuronDetails().size(),
				network.getGoalNeuronDetails().size() };

	}

	/**
	 * @return the value amin which is the minimum activation a node must have
	 *         to consider its output true
	 */
	double getMaxError() {
		return network.getAMIN();
	}

	/**
	 * Get query predicates in order they appear in the input layer.
	 */
	List<Expression> getQueryPredicates() {

		List<Expression> returnQs = new ArrayList<Expression>();
		for (Entry<Expression, Integer> tup : network.getQueryNeuronDetails()) {
			returnQs.add(tup.getKey());
		}

		return returnQs;

	}

	/**
	 * print information about the overall neural layout<br/>
	 * <code>#input units, [input unit expressions], #hidden units,
	 * #output units, [output unit expressions]</code>
	 */
	void printInfo() {

		System.out
				.println("# input  units:"
						+ network.getInputLayer().getNeurons().size()
						+ "(total predicates)");
		System.out.println(network.getInputHash().keySet());
		System.out.println("# hidden units:"
				+ network.getHiddenLayer().getNeurons().size() + "(total clauses)");
		System.out.println("# output units:"
				+ network.getOutputLayer().getNeurons().size()
				+ "(total unique heads)");
		System.out.println(network.getOutputHash().keySet());

	}

	/**
	 * print information about the most important neurons in the current
	 * shef.network <b>query neurons</b> and <b>goal neurons</b>
	 */
	void printPlayInfo() {

		System.out.println("# query units "
				+ network.getQueryNeuronDetails().size());
		System.out.println(network.getQueryNeuronDetails());
		System.out.println("# goal units "
				+ network.getGoalNeuronDetails().size());
		System.out.println(network.getGoalNeuronDetails());

	}

	/**
	 * for each node on the input layer print <br/>
	 * <code>(expression, #inputs, input, #outputs, output)</code>
	 */
	void printActivationAllInput() {

		for (Entry<Expression, Neuron> tup : network.getInputHash().entrySet()) {
			System.out.println(tup.getKey() + "\tin["
					+ tup.getValue().getInputConnections().size() + "]: "
					+ tup.getValue().getNetInput() + "out:["
					+ tup.getValue().getOutConnections().size() + "]: "
					+ tup.getValue().getOutput());
		}

	}

	/**
	 * Print details about the output layer of the shef.network. <br/>
	 * <code>(predicate, output, input, #inputs, [input activations])</code>
	 */
	void printActivationAllOutput() {

		for (Entry<Expression, ThresholdNeuron> tup : network.getOutputHash()
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
	 * for each node matching <code>(true ?term)</code> is an input node in the
	 * graph, print: <br/>
	 * <code>(expression, output)</code>
	 */
	void printActivationQueryInput() {

		for (Entry<Expression, Integer> tup : network.getQueryNeuronDetails()) {
			System.out.println(tup.getValue() + "\t-> "
					+ network.getInputHash().get(tup.getValue()).getOutput());
		}

	}

	/**
	 * For each node matching <code>(GOAL ?player ?score)</code> print. <br/>
	 * <code>(expression, output)</code>
	 */
	void printActivationGoalOutput() {

		for (Entry<Expression, Integer> tup : network.getGoalNeuronDetails()) {
			System.out.println(tup.getValue() + "\t-> "
					+ network.getOutputHash().get(tup.getKey()).getOutput());
		}

	}

	/**
	 * Print details about the hidden layer of the shef.network. <br/>
	 * <code>(input, output)</code>
	 */
	void printActivationHidden() {

		for (Neuron hidden : network.getHiddenLayer().getNeurons()) {
			System.out.println("in:" + hidden.getNetInput() + "out:"
					+ hidden.getOutput());
		}

	}

	/**
	 * print the scores returned by <code>getScores</code> given the
	 * <code>queryValues</code> provided
	 * 
	 * @param queryValues
	 *            input to shef.network
	 */
	void printScores(double... queryValues) {

		System.out.println(Arrays.toString(getAllPlayerScores(queryValues)));

	}

}
