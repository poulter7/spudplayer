package shef.strategies.uct;

import java.util.List;
import java.util.Random;

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
	 * Create the network which will be guiding the out of tree play
	 */
	@Override
	public void strategyMetaSetup() {
		// create network
		CIL2PNet net = CIL2PFactory.modeNetFromGame(getMatch().getGame());
		cil2pManager = new CIL2PManager(net, roles);
		
	}

	protected List<Double> completeRollout(final MachineState from, final int fromLvl) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
		int simDepth = fromLvl;
		int levelPlayer = (simDepth % roleCount);
//		System.out.println(fromLvl);
		
		
		MachineState terminal = from;
		do { // play the best move for the current player
			
			List<MachineState> nextStates = theMachine.getNextStates(terminal);
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
//			double bestChildScore = 0;
//			int bestChildIndex= 0;
//			for (int i = 0; i < nextStateCount; i++) {
//				double childScore= cil2pManager.getStateValue(nextStates.get(i), levelPlayer);
//				if (childScore> bestChildScore) {
//					bestChildScore= childScore;
//					bestChildIndex= i;
//				}
//			}
//			System.out.println(bestChildIndexGAUSS == bestChildIndex);
			terminal = nextStates.get(bestChildIndexGAUSS);
			simDepth++;
			levelPlayer = (simDepth % roleCount);
		} while (!theMachine.isTerminal(terminal));
		// the node was terminal
		return theMachine.getDoubleGoals(terminal);
	}

	@Override
	public String getName() {
		return "Neural Gamer";
	}


}
