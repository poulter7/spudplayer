package shef.strategies.uct;

import java.util.List;

import shef.strategies.uct.tree.StateActionPair;
import shef.strategies.uct.tree.StateModel;
import util.statemachine.MachineState;
import util.statemachine.Move;
import util.statemachine.exceptions.GoalDefinitionException;
import util.statemachine.exceptions.MoveDefinitionException;
import util.statemachine.exceptions.TransitionDefinitionException;

/**
 * A pattern for any UCT gamer
 * @author jonathan
 *
 */
public interface IUCTStrategy {
	
	/**
	 * UCT_NOVELTY_C in the UCT equation, this alters the balance between exploration and
	 * exploitation in a UCT rollout
	 */
	static final float UCT_NOVELTY_C = 50;
	
	/**
	 * Perform a single UCT rollout
	 * 
	 * @param rolloutRootSM
	 *            state to begin rollout from
	 * @throws MoveDefinitionException
	 * @throws TransitionDefinitionException
	 * @throws GoalDefinitionException
	 */
	void inTreeRollout(final StateModel rolloutRootSM) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException;
	
	/**
	 * Complete the rest of this UCT rollout past the UCT horizon
	 * 
	 * @param from
	 *            the state to complete rollout from
	 * @return the terminal state reached
	 * @throws MoveDefinitionException 
	 * @throws TransitionDefinitionException 
	 * @throws GoalDefinitionException 
	 */
	MachineState outOfTreeRollout(final MachineState from) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException;
	
	/**
	 * Return the state action pair chosen from those just expanded on the horizon
	 * @throws TransitionDefinitionException 
	 * @throws MoveDefinitionException 
	 */
	List<Move> horizonStatePair(final List<List<Move>> newStateActionPairs, MachineState currentState) throws MoveDefinitionException, TransitionDefinitionException;
}
