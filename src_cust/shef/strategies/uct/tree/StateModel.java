package shef.strategies.uct.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import util.statemachine.MachineState;
import util.statemachine.Move;

/**
 * A wrapper around <code>IGameState</code> which keeps a record of the possible
 * move from this state and their resulting new states represents move -> state
 * action pair
 * 
 * @author jonathan
 * 
 */
public class StateModel {

	public int timesExplored = 0;
	
	public final HashMap<List<Move>, StateActionPair> actionsPairs = new HashMap<List<Move>, StateActionPair>();
	public final MachineState state;
	public int depth;

	public StateModel(MachineState s, int i) {
		state = s;
		depth = i;
	}

	public void print(StringBuilder b) {
		b.append("\texp: " + timesExplored+"\n");
		for (Entry<List<Move>, StateActionPair> type: actionsPairs.entrySet()) {
			type.getValue().print(b);
		}
		
	}

}
