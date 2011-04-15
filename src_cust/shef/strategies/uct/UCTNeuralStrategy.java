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
	 * Setup the UCT game tree
	 */
	@Override
	public void stateMachineMetaGame(final long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		System.out.println("init: " + timeout);
		CIL2PNet net = CIL2PFactory.modeNetFromGame(getMatch().getGame());
		cil2pManager = new CIL2PManager(net);
		super.stateMachineMetaGame(timeout);
	}

	Random r = new Random();
	protected List<Double> completeRollout(final MachineState from, final int fromLvl) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
		int simDepth = fromLvl;
		int levelPlayer = (simDepth % roleCount);
		
		MachineState terminal = from;
		do { // play the best move for the current player
			
			List<MachineState> nextStates = theMachine.getNextStates(terminal);
			int nextStateCount = nextStates.size();

			double bestChildScoreGAUSS = 0;
			int bestChildIndexGAUSS = 0;
			for (int i = 0; i < nextStateCount; i++) {
				double childScoreGAUSS = cil2pManager.getStateValue(nextStates.get(i), levelPlayer);
				if (childScoreGAUSS > bestChildScoreGAUSS) {
					bestChildScoreGAUSS = childScoreGAUSS;
					bestChildIndexGAUSS = i;
				}
				System.out.println(childScoreGAUSS );
			}
			terminal = nextStates.get(bestChildIndexGAUSS);
			simDepth++;
			levelPlayer = (simDepth % roleCount);
			System.out.println("roll");
		} while (!theMachine.isTerminal(terminal));
		System.out.println("--");
		// the node was terminal
		return theMachine.getDoubleGoals(terminal);
	}

	@Override
	public String getName() {
		return "Neural Gamer";
	}

}
