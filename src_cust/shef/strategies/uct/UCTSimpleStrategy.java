package shef.strategies.uct;

import java.util.List;

import shef.strategies.uct.UCTGamer;
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
public final class UCTSimpleStrategy extends UCTGamer {

	/**
	 * Random rollout to a terminal state
	 * @throws MoveDefinitionException 
	 * @throws TransitionDefinitionException 
	 * @throws GoalDefinitionException 
	 */
	protected List<Double> completeRollout(MachineState from, int fromLvl) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		MachineState terminal = theMachine.performDepthCharge(from, null);
		return theMachine.getDoubleGoals(terminal);

	}

	@Override
	public String getName() {
		return "Basic UCT Gamer";
	}
}
