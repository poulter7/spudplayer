package shef.strategies.uct;

import util.statemachine.MachineState;
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
	 * @param the level this rollout takes place from
	 * 
	 * @throws MoveDefinitionException 
	 * @throws TransitionDefinitionException 
	 * @throws GoalDefinitionException 
	 */
	public MachineState outOfTreeRollout(MachineState from, int fromLvl) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
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

}
