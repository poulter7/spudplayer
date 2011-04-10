package shef.strategies.uct.tree;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

import util.statemachine.MachineState;

/**
 * Container for a set of states at a given level in a UCT tree
 * @author jonathan
 *
 */
public class Level {
	public final HashMap<MachineState, StateModel> states = new HashMap<MachineState, StateModel>(50);

	public void print(StringBuilder b) {
		for (Iterator<Entry<MachineState, StateModel>> iterator = states.entrySet().iterator(); iterator.hasNext();) {
			Entry<MachineState, StateModel> type = (Entry<MachineState, StateModel>) iterator.next();
			MachineState stateDesc = type.getKey();
			StateModel state = type.getValue();
			b.append("\texp: " + state.timesExplored+"\n");
			state.print(b);
		}		
	}
	

}
