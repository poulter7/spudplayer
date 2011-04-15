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
	protected static final float C = 50;

	/**
	 * Setup the UCT game tree
	 */
	@Override
	public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		System.out.println("init: " + timeout);
		CIL2PNet net = CIL2PFactory.modeNetFromGame(getMatch().getGame());
		cil2pManager = new CIL2PManager(net);
		super.stateMachineMetaGame(timeout);
	}

	protected List<Double> completeRollout(MachineState from, int fromLvl) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
		int simDepth = fromLvl;
		int levelPlayer = (simDepth % roleCount);
		//        
		MachineState current = from;
		do { // play the best move for the current player

			List<MachineState> nextStats = theMachine.getNextStates(current);
			int nextStateCount = nextStats.size();

			double bestChildScoreGAUSS = 0;
			int bestChildIndexGAUSS = 0;
			for (int i = 0; i < nextStateCount; i++) {
				double childScoreGAUSS = cil2pManager.getStateValueGaussian(nextStats.get(i), levelPlayer);
				if (childScoreGAUSS > bestChildScoreGAUSS) {
					bestChildScoreGAUSS = childScoreGAUSS;
					bestChildIndexGAUSS = i;
				}
			}
			current = nextStats.get(bestChildIndexGAUSS);
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
