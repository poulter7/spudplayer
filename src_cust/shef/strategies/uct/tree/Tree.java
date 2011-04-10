package shef.strategies.uct.tree;

import java.util.ArrayList;
import java.util.List;

import player.gamer.statemachine.StateMachineGamer;
import util.statemachine.MachineState;
import util.statemachine.Move;
import util.statemachine.Role;
import util.statemachine.StateMachine;
import util.statemachine.exceptions.MoveDefinitionException;
import util.statemachine.exceptions.TransitionDefinitionException;

/**
 * Contains every level of the tree in an ordered list
 * 
 * @author jonathan
 * 
 */
public final class Tree {
	private final ArrayList<Level> stateLists = new ArrayList<Level>(100);
	private final StateMachine sm;
	private final int num_players;

	/**
	 * Add an action pair to the hashmap
	 * 
	 * @param game
	 * @param initMatch
	 * @throws InterruptedException
	 * @throws TransitionDefinitionException
	 * @throws MoveDefinitionException
	 * @throws TransitionDefinitionException
	 * @throws MoveDefinitionException
	 */
	public Tree(MachineState initMatch, StateMachineGamer smg, int num_players) throws InterruptedException, MoveDefinitionException, TransitionDefinitionException {
		this.sm = smg.getStateMachine();
		this.num_players = num_players;
		Level rootLevel = new Level();
		StateModel rootNodeModel = new StateModel(initMatch);
		stateLists.add(rootLevel);
		
		// add the root
		MachineState rootNode = initMatch;
		rootLevel.states.put(rootNode, rootNodeModel);
		
		// expand the root
		expandNode(rootNodeModel, 0);
	}

	public void expandNode(StateModel nodeModel, int lvl) throws MoveDefinitionException, TransitionDefinitionException {
		List<List<Move>> legalMoves = sm.getLegalJointMoves(nodeModel.state);
		MachineState cState = nodeModel.state;
		int depthN = lvl+1;
		for(List<Move> m: legalMoves){
			MachineState nextState = sm.getNextState(cState, m);
			StateModel nextStateModel = new StateModel( nextState);
			Level childLevel = null;
			if(stateLists.size() <= depthN){
				childLevel = new Level();
				stateLists.add(childLevel);
				
			} else {
				childLevel = stateLists.get(depthN);
			}
			childLevel.states.put(nextState, nextStateModel);
			
			// make a connection between the two
			nodeModel.actionsPairs.put(m, new StateActionPair(nextStateModel, m));

		}
		
	}

	public void print(StringBuilder b) {
		b.append("Tree\n");
		for (int i = 0; i < stateLists.size(); i++) { // for
			b.append("level " + i + " \n");
			stateLists.get(i).print(b);
			b.append("\n");
			b.append("-----");
		}
	}

	public ArrayList<Level> getStateLists() {
		return stateLists;
	}
}
