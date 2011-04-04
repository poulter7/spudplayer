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
	 */
	protected List<Double> completeRollout(MachineState from, int fromLvl) {
		// TODO Auto-generated method stub
		try {
			MachineState terminal = theMachine.performDepthCharge(from, null);
			return theMachine.getDoubleGoals(terminal);
			
		} catch (TransitionDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MoveDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GoalDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	@Override
	public String getName() {
		return "Basic UCT Gamer";
	}
}
