package shef.strategies.ann;

import util.statemachine.MachineState;
import util.statemachine.Move;

public final class TreeNodeAlphaBeta implements Comparable<TreeNodeAlphaBeta> {
    private final MachineState gameState; 
    private final Move move;
    
    private double gameValue;
    
    public TreeNodeAlphaBeta(MachineState node, Move move) {
        this.gameState = node;
        this.move = move;
    }

    public TreeNodeAlphaBeta(MachineState root) {
		this.gameState = root;
		this.move = null;
	}

	public MachineState getState() {
      return this.gameState;
    }
    
    @Override
    public String toString() {
       return getGameValue() + " \t" + getMove();
    }

    @Override
    public int compareTo(TreeNodeAlphaBeta o) {
      if(this.getGameValue() >= o.getGameValue())
          return 1;
      return -1;
    }

    public void setGameValue(double gameValue) {
        this.gameValue = gameValue;
    }

    public double getGameValue() {
        return gameValue;
    }

	public Move getMove() {
		return move;
	}
    
    

}
