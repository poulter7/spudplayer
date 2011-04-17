package shef.strategies.ann;

import util.statemachine.MachineState;

public final class TreeNodeAlphaBeta implements Comparable<TreeNodeAlphaBeta> {
    private MachineState gameState; 
    private double gameValue;
    
    public TreeNodeAlphaBeta(MachineState node) {
        this.gameState = node;
    }

    public MachineState getState() {
      return this.gameState;
    }
    
    @Override
    public String toString() {
       return getGameValue() + "\t" +gameState;
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
    
    

}
