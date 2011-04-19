package shef.strategies.ann;

import shef.network.CIL2PManager;
import util.statemachine.MachineState;
import util.statemachine.Move;
import util.statemachine.StateMachine;

/**
 * Tree node in an AlphaBeta search tree which is guided by a Neural Network
 * 
 * @author jonathan
 * 
 */
public class NeuralNetworkABNode implements IABNode<MachineState, Move> {
	static CIL2PManager cil2pManager;
	static StateMachine machine;
	private final MachineState gameState;
	private final Move move;

	private double gameValue;

	public NeuralNetworkABNode(MachineState node, Move move) {
		this.gameState = node;
		this.move = move;
	}

	/**
	 * Create a root node in the tree. This should only be called by the
	 * creating class
	 * 
	 * @param root
	 */
	public NeuralNetworkABNode(MachineState root) {
		this.gameState = root;
		this.move = null;
	}

	public MachineState getContents() {
		return this.gameState;
	}

	public Move getTraversal() {
		return move;
	}

	@Override
	public String toString() {
		return getValue() + " \t" + getContents();
	}

	/**
	 * Update the value of the game at this tree node
	 * 
	 * @param gameValue
	 */
	public void setGameValue(double gameValue) {
		this.gameValue = gameValue;
	}

	public double getValue() {
		return gameValue;
	}


	@Override
	public boolean isTerminal() {
		return machine.isTerminal(gameState);
	}

	@Override
	public void setValue(double val) {
		this.gameValue = val;
	}

	@Override
	public int compareTo(IABNode<MachineState, Move> o) {
		if (this.getValue() >= o.getValue())
			return 1;
		return -1;
	}

	@Override
	public double getHeuristicValue(int player) {
		return cil2pManager.getStateValue(gameState, player);
	}

}
