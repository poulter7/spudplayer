package shef.strategies.uct;

import java.util.ArrayList;
import java.util.List;

import shef.network.CIL2PFactory;
import shef.network.CIL2PManager;
import shef.network.CIL2PNet;
import shef.strategies.uct.tree.StateActionPair;
import shef.strategies.uct.tree.StateModel;
import util.statemachine.MachineState;
import util.statemachine.Move;
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
	private static final boolean PRINT_EXPAND = false;
	private static final boolean SEE_OUT_OUT = true;
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
	public MachineState outOfTreeRollout(final MachineState from)
			throws MoveDefinitionException, TransitionDefinitionException,
			GoalDefinitionException {
		MachineState terminal = from;

		do {
			// play the move which results
			// in the highest amount of reward in the next state for
			// the current player
			// get the current player
			List<List<Move>> movePairs = theMachine
					.getLegalJointMoves(terminal);
			List<Move> played = horizonStatePair(movePairs, terminal);
			terminal = theMachine.getNextState(terminal, played);

		} while (!theMachine.isTerminal(terminal));
		// the node was terminal
		return terminal;
	}

	@Override
	public String getName() {
		return "Neural Gamer";
	}

	@Override
	public List<Move> horizonStatePair(List<List<Move>> movePairs,
			MachineState from) throws MoveDefinitionException, TransitionDefinitionException {
		int movePairCount = movePairs.size();
		double[][] rewardPairs = new double[movePairCount][roleCount];

		for (int i = 0; i < movePairCount; i++) {
			rewardPairs[i] = cil2pManager
					.getStateValueGaussianPlayers(theMachine
							.getNextStateDestructively(from, movePairs.get(i)));
		}

		int[] bestIndex = new int[roleCount];
		double[] bestValue = new double[roleCount];

		for (int i = 0; i < roleCount; i++) {
			for (int j = 0; j < movePairCount; j++) {
				if (bestValue[i] < rewardPairs[j][i]) {
					bestValue[i] = rewardPairs[j][i];
					bestIndex[i] = j;
				}
			}
		}

		// chosen the action which gave you the best result IF it was your turn
		// what would you play
		List<Move> played = new ArrayList<Move>(roleCount);
		for (int i = 0; i < roleCount; i++) {
			played.add(movePairs.get(bestIndex[i]).get(i));
		}
		return played;
	}

}
