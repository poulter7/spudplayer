package shef.strategies.uct;

import java.util.ArrayList;
import java.util.List;

import shef.network.CIL2PFactory;
import shef.network.CIL2PManager;
import shef.network.CIL2PNet;
import shef.strategies.uct.tree.StateActionPair;
import shef.strategies.uct.tree.StateModel;
import util.statemachine.MachineState;
import util.statemachine.Role;
import util.statemachine.exceptions.GoalDefinitionException;
import util.statemachine.exceptions.MoveDefinitionException;
import util.statemachine.exceptions.TransitionDefinitionException;

/**
 * UCT Gamer which creates a neural network and completes its rollouts using
 * that
 * 
 * @author jonathan poulter
 */
public final class StrategyUCTNeural extends BaseGamerUCT {

	private static final boolean PRINT_GAUSS_EFFECT = false;
	private static final boolean PRINT_EXPAND = true;
	/** Method of interacting with the network */
	protected CIL2PManager cil2pManager;
	private double sigma;

	public StrategyUCTNeural(double sigma) {
		this.sigma = sigma;
	}

	/**
	 * Create the network which will be guiding the out of tree play
	 */
	public void strategyMetaSetup(final long timeout) {
		// create network
		CIL2PNet net = CIL2PFactory.createGameNetworkFromGame(getMatch()
				.getGame());
		cil2pManager = new CIL2PManager(net, roles, sigma);

	}

	/**
	 * 
	 * 
	 * @param from
	 *            the state to rollout from
	 * @param fromLvl
	 *            the level this rollout takes place from
	 * 
	 * @throws MoveDefinitionException
	 * @throws TransitionDefinitionException
	 * @throws GoalDefinitionException
	 */
	public MachineState outOfTreeRollout(final MachineState from,
			final int fromLvl) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException {
		int simDepth = fromLvl + 1;
		MachineState terminal = from;

		do {
			// play the move which results
			// in the highest amount of reward in the next state for
			// the current player
			// get the current player
			int levelPlayerID = (simDepth % roleCount);
			Role levelPlayer = roles.get(levelPlayerID);

			// next states
			List<MachineState> nextStates = theMachine.getNextStates(terminal);
			int nextStateCount = nextStates.size();
			double bestChildScoreGAUSS = 0;
			int bestChildIndexGAUSS = 0;
			for (int i = 0; i < nextStateCount; i++) {
				double childScoreGAUSS = cil2pManager.getStateValueGaussian(
						nextStates.get(i), levelPlayer);
				if (childScoreGAUSS > bestChildScoreGAUSS) {
					bestChildScoreGAUSS = childScoreGAUSS;
					bestChildIndexGAUSS = i;
				}
			}
			if (PRINT_GAUSS_EFFECT) {
				double bestChildScore = 0;
				int bestChildIndex = 0;
				for (int i = 0; i < nextStateCount; i++) {
					double childScore = cil2pManager.getStateValue(nextStates
							.get(i), levelPlayer);
					if (childScore > bestChildScore) {
						bestChildScore = childScore;
						bestChildIndex = i;
					}
				}
				System.out.println(bestChildIndexGAUSS == bestChildIndex);
				System.out.println(levelPlayer + " "
						+ theMachine.getLegalMoves(terminal, levelPlayer));
			}
			terminal = nextStates.get(bestChildIndexGAUSS);
			simDepth++;
			levelPlayerID = (simDepth % roleCount);
		} while (!theMachine.isTerminal(terminal));
		// the node was terminal
		return terminal;
	}

	@Override
	public String getName() {
		return "Neural Gamer";
	}

	@Override
	public StateActionPair horizonStatePair(
			List<StateActionPair> newStateActionPairs, int level)
			throws MoveDefinitionException, TransitionDefinitionException {

		int nextStateCount = newStateActionPairs.size();
		List<List<Double>> returns = new ArrayList<List<Double>>(nextStateCount);
		for (StateActionPair stap : newStateActionPairs) {
			returns.add(cil2pManager
					.getStateValueGaussianPlayers(stap.RESULT.state));
		}

		Role levelPlayer = roles.get(level % roleCount);

		double bestChildScoreGAUSS = 0;
		int bestChildIndexGAUSS = 0;
		for (int i = 0; i < nextStateCount; i++) {
			double childScoreGAUSS = cil2pManager.getStateValueGaussian(
					newStateActionPairs.get(i).RESULT.state, levelPlayer);
			if (childScoreGAUSS > bestChildScoreGAUSS) {
				bestChildScoreGAUSS = childScoreGAUSS;
				bestChildIndexGAUSS = i;
			}
		}
		if (PRINT_EXPAND) {
			System.out.println("EXPAND lvl:" + level + " as " + levelPlayer + " " +
					(newStateActionPairs.get(bestChildIndexGAUSS).ACTION)
					);
			System.out.println(roles);
		}
		return newStateActionPairs.get(bestChildIndexGAUSS);
	}

}
