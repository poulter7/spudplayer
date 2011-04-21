package shef.strategies.ann;

import util.statemachine.Role;


/**
 * A node capable of taking part in an alpha beta search
 * @author jonathan
 *
 * @param <T> State representation
 * @param <L> Travelsal representation
 */
public interface IABNode<T, L> extends Comparable<IABNode<T, L>>{

	/** get the value of this node */
	double getValue();
	/** get a heuristic value for this node */
	double getHeuristicValue(Role player);
	/** does this node represent a terminal state? */
	boolean isTerminal();
	/** set the value of this node */
	void setValue(double val);
	/** the game state this node represents */
	T getContents();
	/** the action take to get to this state */
	L getTraversal();
	
}
