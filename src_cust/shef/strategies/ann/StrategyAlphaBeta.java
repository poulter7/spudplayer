package shef.strategies.ann;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import shef.network.CIL2PFactory;
import shef.network.CIL2PManager;
import shef.network.CIL2PNet;
import shef.strategies.BaseGamer;
import util.statemachine.MachineState;
import util.statemachine.Move;
import util.statemachine.exceptions.GoalDefinitionException;
import util.statemachine.exceptions.MoveDefinitionException;
import util.statemachine.exceptions.TransitionDefinitionException;

/**
 * Basic ANN straregy, performs tree creation and network generation The
 * underlying search algorithm is a basic min-max search
 * 
 * @author jonathan
 * 
 */
public final class StrategyAlphaBeta extends BaseGamer implements ICIL2PUser {

	/** Depth of the Alpha-Beta search */
	private static final int AB_DEPTH = 5;

	/** Handle to the network manager */
	private CIL2PManager cil2pManager;
	private List<TreeNodeAlphaBeta> rootMoves;
	private long currentTimeout;

	/**
	 * Create the network which will guide the AlphaBetaStrategy
	 * 
	 * @param timeout
	 *            the time this must be completed by
	 */
	@Override
	public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		// setup essential items
		super.stateMachineMetaGame(timeout);

		// create network
		final CIL2PNet net = CIL2PFactory.createGameNetworkFromGame(getMatch().getGame());
		cil2pManager = new CIL2PManager(net, roles);
	}

	/**
	 * Runs the alpha beta search strategy guided by the generated Neural
	 * Network
	 * 
	 * @param timeout
	 *            the time this must be completed by
	 * @return the selected move
	 */
	@Override
	public Move stateMachineSelectMove(final long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		currentTimeout = timeout - 1000;
		Move chosenMove = null;
		
		rootMoves = new ArrayList<TreeNodeAlphaBeta>();
		
		final TreeNodeAlphaBeta mmRoot = new TreeNodeAlphaBeta(getCurrentState());
		alphaBeta(mmRoot, AB_DEPTH, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, myRoleID);
		for (TreeNodeAlphaBeta m : rootMoves) {
			System.out.println(m);
		}
		
		// return the highest value node
		TreeNodeAlphaBeta m = Collections.max(rootMoves);
		System.out.println("-> " + m);
		chosenMove = m.getMove();
		return chosenMove;

	}

	/**
	 * Performs alpha-beta search
	 * 
	 * The initial way of calling this is likely to be alphaBeta(root, 0,
	 * Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1);
	 * 
	 * @param node
	 *            the game state at this point
	 * @param depth
	 *            node depth
	 * @param alpha
	 *            minimum score that MAX is guaranteed to get
	 * @param beta
	 *            maximum score that MIN is guaranteed to get
	 * @param player
	 *            whether playing as MAX (1) or MIN (-1)
	 * @return
	 * @throws InterruptedException
	 * @throws MoveDefinitionException
	 * @throws TransitionDefinitionException
	 */
	double alphaBeta(final TreeNodeAlphaBeta node, final int depth, double alpha, double beta, final int player) throws MoveDefinitionException, TransitionDefinitionException {

		final int nextPlayer = (player + 1) % roleCount;
		final MachineState curGS = node.getState();
		// if the lookahead limit is reached, the node is terminal or
		// it is necessary to return a value as time is up
		if (depth == 0 || theMachine.isTerminal(curGS) || System.currentTimeMillis() > currentTimeout) {
			// heuristic value of node
			final double val = cil2pManager.getStateValue(node.getState(), player);
			if (depth == AB_DEPTH - 1) {
				rootMoves.add(node);
			}
			node.setGameValue(val);
			return val;
		}

		final List<List<Move>> moves = theMachine.getLegalJointMoves(node.getState());
		if (player == roleCount) { // MAX
			for (List<Move> childMove : moves) { // ALPHA cut

				final MachineState childState = theMachine.getNextState(curGS, childMove);
				final TreeNodeAlphaBeta child = new TreeNodeAlphaBeta(childState, childMove.get(myRoleID));
				final double abResult = alphaBeta(child, depth - 1, alpha, beta, nextPlayer);
				alpha = Math.max(alpha, abResult);
				if (beta <= alpha)
					break;
			}
			if (depth == AB_DEPTH - 1)
				rootMoves.add(node);
			node.setGameValue(alpha);
			return alpha;
		} else {
			for (List<Move> childMove : moves) {
				MachineState childState = theMachine.getNextState(node.getState(), childMove);
				TreeNodeAlphaBeta child = new TreeNodeAlphaBeta(childState, childMove.get(myRoleID));
				double abResult = alphaBeta(child, depth - 1, alpha, beta, nextPlayer);
				beta = Math.min(beta, abResult);
				if (beta <= alpha) {
					break;
				}
			}
			if (depth == AB_DEPTH - 1)
				rootMoves.add(node);
			node.setGameValue(beta);
			return beta;
		}

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Alpha beta ANN";
	}

}
