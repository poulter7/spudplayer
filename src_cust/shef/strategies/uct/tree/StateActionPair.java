package shef.strategies.uct.tree;

import java.util.List;

import shef.strategies.uct.UCTGamer;
import util.statemachine.Move;

/**
 * Represents an arc on the UCT Tree
 * @author jonathan
 *
 */
public class StateActionPair {
	public final StateModel result;
	public final List<Move> action;
	public final double[] value;
	public static int numOfMe = 0;
	
	public int exploreCount = 0;

	public StateActionPair(StateModel result, List<Move> action) {
		this.value 	= new double[UCTGamer.roleCount];
		this.action = action;
		this.result = result;
		numOfMe++;
	}

	/**
	 * update the rolling average score of the node
	 * 
	 * @param outcome
	 */
	public void updateAverage(List<Double> outcome) {
		for (int i=0; i < UCTGamer.roleCount; i++) {
			value[i] = ((value[i] * exploreCount) + outcome.get(i)) / (exploreCount + 1d);
		}
		exploreCount++;
	}

}
