package shef.strategies.uct.tree;

import java.util.Arrays;
import java.util.List;

import util.statemachine.Move;

/**
 * Represents an arc on the UCT UCTTree
 * @author jonathan
 *
 */
public class StateActionPair {
	/** An estimated reward for every player */
	public final double[] VALUE;
	/** The resulting state visited by traversing this action */
	public final StateModel RESULT;
	/** The representation of the action itself, which is a list of moves */
	public final List<Move> ACTION;
	/** Number of players in this game */
	private final int ROLECOUNT;
	
	/** The number of times this state action pair has been visted */
	public int timesExplored = 0;
	

	/**
	 * Default constructor
	 * @param result the resulting state
	 * @param action the moves taken
	 * @param roleCount number of players in this game
	 */
	public StateActionPair(StateModel result, List<Move> action, int roleCount) {
		this.VALUE = new double[roleCount];
		this.ROLECOUNT = roleCount;
		this.ACTION = action;
		this.RESULT = result;
	}

	/**
	 * update the rolling average score of the node
	 * 
	 * @param outcome
	 */
	public void updateAverage(List<Double> outcome) {
		for (int i=0; i < ROLECOUNT; i++) {
			VALUE[i] = ((VALUE[i] * timesExplored) + outcome.get(i)) / (float) (timesExplored + 1);
		}
		timesExplored++;
	}

	public void print(StringBuilder sb) {
		sb.append("\t"+ACTION + " " + timesExplored + " " +Arrays.toString(VALUE) +"\n");
	}
}
