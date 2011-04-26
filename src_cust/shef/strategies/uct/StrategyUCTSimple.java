package shef.strategies.uct;

import java.util.List;
import java.util.Random;

import shef.strategies.uct.tree.StateActionPair;
import util.statemachine.MachineState;
import util.statemachine.Move;
import util.statemachine.exceptions.GoalDefinitionException;
import util.statemachine.exceptions.MoveDefinitionException;
import util.statemachine.exceptions.TransitionDefinitionException;

/**
 * UCT player which completes the rollout by perform a random rollout
 * 
 * @author jonathan
 * 
 */
public final class StrategyUCTSimple extends BaseGamerUCT {

	/**
	 * Nothing specific for this simple strategy
	 */
	public void strategyMetaSetup(final long timeout) {};
	
	/**
	 * Random rollout to a terminal state
	 * 
	 * @param from the state to rollout from
	 * @param fromLvl the level this rollout takes place from
	 * 
	 * @throws MoveDefinitionException 
	 * @throws TransitionDefinitionException 
	 * @throws GoalDefinitionException 
	 */
	public MachineState outOfTreeRollout(MachineState from) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		MachineState terminal = from;
		do{
            terminal = theMachine.getNextState(terminal, theMachine.getRandomJointMove(terminal));
        }
		while(!theMachine.isTerminal(terminal));
		return terminal;

	}

	@Override
	public String getName() {
		return "Basic UCT Gamer";
	}


	Random r = new Random();
	
	/**
	 * Returns a random state pair
	 * @param newStateActionPairs the recently expanded state pairs
	 * @return a random state pair
	 */
	public List<Move> horizonStatePair(List<List<Move>> newStateActionPairs, MachineState currentState)
			throws MoveDefinitionException, TransitionDefinitionException {
		int size = newStateActionPairs.size();
		return newStateActionPairs.get(r.nextInt(size));
	}

	@Override
	void strategyCleanUp() {
		// TODO Auto-generated method stub
		
	}

}
