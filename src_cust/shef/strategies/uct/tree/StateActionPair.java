package shef.strategies.uct.tree;

import java.util.List;

import util.statemachine.Move;

/**
 * Represents an arc on the UCT Tree
 * @author jonathan
 *
 */
public class StateActionPair {
	public int timesExplored = 0;
//	public float[] runningTotal;
	public double[] value;
	public StateModel result;
	public List<Move> action;

	public StateActionPair(StateModel result, List<Move> action, int roleCount) {
		value = new double[roleCount];
		this.action = action;
		this.result = result;
	}

	/**
	 * update the rolling average score of the node
	 * 
	 * @param outcome
	 */
	public void updateAverage(List<Double> outcome) {
		for (int i=0; i < outcome.size(); i++) {
			value[i] = ((value[i] * timesExplored) + outcome.get(i)) / (float) (timesExplored + 1);
		}
		timesExplored++;
	}
}
