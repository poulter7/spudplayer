package shef.strategies.uct.tree;

import java.util.Arrays;
import java.util.List;

import util.statemachine.Move;

/**
 * Represents an arc on the UCT Tree
 * @author jonathan
 *
 */
public class StateActionPair {
	public final double[] VALUE;
	public final StateModel RESULT;
	public final List<Move> ACTION;
	private final int ROLECOUNT;
	
	public int timesExplored = 0;
	

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
