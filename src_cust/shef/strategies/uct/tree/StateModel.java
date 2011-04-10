package shef.strategies.uct.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
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

	public final HashMap<List<Move>, StateActionPair> actionsPairs = new HashMap<List<Move>, StateActionPair>(50);
	public final MachineState state;

	public int timesExplored = 0;

	public StateModel(MachineState s) {
		state = s;
	}

	public void print(StringBuilder b) {
		for (Iterator<Entry<List<Move>, StateActionPair>> iterator = actionsPairs.entrySet().iterator(); iterator.hasNext();) {
			Entry<List<Move>, StateActionPair> type = iterator.next();
			List<Move> moveDesc = type.getKey();
			StateActionPair state = type.getValue();
			b.append("\t"+moveDesc + " " + state.exploreCount + " " +state.value +"\n");
		}
		
	}

}
