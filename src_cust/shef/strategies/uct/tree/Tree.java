package shef.strategies.uct.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

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
		StateModel rootNodeModel = new StateModel(initMatch, 0);
		stateLists.add(rootLevel);
		
		// add the root
		MachineState rootNode = initMatch;
		rootLevel.states.put(rootNode, rootNodeModel);
		
		// expand the root
		expandNodeAndReturnRandom(rootNodeModel);
	}
	
	Random random = new Random();
	
	public StateActionPair expandNodeAndReturnRandom(StateModel nodeModel) throws MoveDefinitionException, TransitionDefinitionException {
		final List<List<Move>> legalMoves = sm.getLegalJointMoves(nodeModel.state);
		final int moveCount =  legalMoves.size();
		
		StateActionPair randomNext = null;
		int randomIndex = random.nextInt(moveCount);
		
		for(int i = 0; i < moveCount; i++){
			List<Move> m = legalMoves.get(i);
			MachineState nextState = sm.getNextState(nodeModel.state, m);
			StateModel nextStateModel = new StateModel( nextState, nodeModel.depth+1);
			Level childLevel = null;
			if(stateLists.size() <= nodeModel.depth+1){
				childLevel = new Level();
				stateLists.add(childLevel);
			} else {
				childLevel = stateLists.get(nodeModel.depth+1);
			}
			childLevel.states.put(nextState, nextStateModel);
			
			// make a connection between the two
			StateActionPair act = new StateActionPair(nextStateModel, m, num_players);
			nodeModel.actionsPairs.put(m, act);
			if(i == randomIndex){
				randomNext = act;
			}
			
		}
		return randomNext;
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
