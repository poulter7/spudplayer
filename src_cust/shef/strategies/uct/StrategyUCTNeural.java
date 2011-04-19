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
 * UCT Gamer which creates a neural network
 * and completes its rollouts using that
 * 
 * @author jonathan poulter
 */
public final class StrategyUCTNeural extends BaseGamerUCT {

	/** Method of interacting with the network */
	protected CIL2PManager cil2pManager;

	/**
	 * Create the network which will be guiding the out of tree play
	 */
	public void strategyMetaSetup(final long timeout) {
		// create network
		CIL2PNet net = CIL2PFactory.createGameNetworkFromGame(getMatch().getGame());
		cil2pManager = new CIL2PManager(net, roles);
		
	}

	/**
	 * 
	 * 
	 * @param from the state to rollout from
	 * @param fromLvl the level this rollout takes place from
	 * 
	 * @throws MoveDefinitionException 
	 * @throws TransitionDefinitionException 
	 * @throws GoalDefinitionException 
	 */
	public MachineState outOfTreeRollout(final MachineState from, final int fromLvl) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
		int simDepth = fromLvl;
		int levelPlayer = (simDepth % roleCount);
		
		
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
		return terminal;
	}

	@Override
	public String getName() {
		return "Neural Gamer";
	}


}
