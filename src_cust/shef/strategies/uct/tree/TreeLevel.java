package shef.strategies.uct.tree;

import java.util.HashMap;
import java.util.Map.Entry;

import util.statemachine.MachineState;

/**
 * Container for a set of states at a given level in a UCT tree
 * @author jonathan
 *
 */
public class TreeLevel {
	/**
	 * A mapping of MachineState to the model which represents auxiliary information about it
	 */
	public final HashMap<MachineState, StateModel> states = new HashMap<MachineState, StateModel>(50);

	public void print(StringBuilder b) {
		for (Entry<MachineState, StateModel> ent : states.entrySet()) {
			ent.getValue().print(b);
		}		
	}
	

}
