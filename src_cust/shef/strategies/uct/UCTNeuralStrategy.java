package shef.strategies.uct;

import java.util.List;

import shef.network.CIL2PFactory;
import shef.network.CIL2PManager;
import shef.network.CIL2PNet;
import util.statemachine.MachineState;
import util.statemachine.exceptions.GoalDefinitionException;
import util.statemachine.exceptions.MoveDefinitionException;
import util.statemachine.exceptions.TransitionDefinitionException;

/**
 * 
 * 
 * @author jonathan poulter
 */
public class UCTNeuralStrategy extends UCTGamer {

	private CIL2PManager cil2pManager;

	/**
	 * Setup the UCT game tree
	 */
	@Override
	public void stateMachineMetaGame(final long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		System.out.println("init: " + timeout);
		CIL2PNet net = CIL2PFactory.modeNetFromGame(getMatch().getGame());
		cil2pManager = new CIL2PManager(net);
		super.stateMachineMetaGame(timeout);
	}

	protected List<Double> completeRollout(final MachineState from, final int fromLvl) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
		int simDepth = fromLvl;
		int levelPlayer = (simDepth % roleCount);
		
		MachineState current = from;
		do { // play the best move for the current player

			List<MachineState> nextStates = theMachine.getNextStates(current);
			int nextStateCount = nextStates.size();

			double bestChildScoreGAUSS = 0;
			int bestChildIndexGAUSS = 0;
			for (int i = 0; i < nextStateCount; i++) {
				double childScoreGAUSS = cil2pManager.getStateValueGaussian(nextStates.get(i), levelPlayer);
				if (childScoreGAUSS > bestChildScoreGAUSS) {
					bestChildScoreGAUSS = childScoreGAUSS;
					bestChildIndexGAUSS = i;
				}
			}
			current = nextStates.get(bestChildIndexGAUSS);
			simDepth++;

			levelPlayer = (simDepth % roleCount);
		} while (!theMachine.isTerminal(current));
		// the node was terminal
		return theMachine.getDoubleGoals(current);
	}

	@Override
	public String getName() {
		return "Neural Gamer";
	}

}
