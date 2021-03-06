package shef.strategies.uct.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import player.gamer.statemachine.StateMachineGamer;
import util.statemachine.MachineState;
import util.statemachine.Move;
import util.statemachine.StateMachine;
import util.statemachine.exceptions.MoveDefinitionException;
import util.statemachine.exceptions.TransitionDefinitionException;

/**
 * Contains every level of the tree in an ordered list
 * 
 * @author jonathan
 * 
 */
public final class UCTTree {
	/** Every level within the game tree */
	private final ArrayList<TreeLevel> stateLists = new ArrayList<TreeLevel>(100);
	/** The state machine which guides game progression */
	private final StateMachine sm;
	/** Number of palyers in this game */
	private final int num_players;

	/**
	 * Add an action pair to the hashmap
	 * 
	 * @param initMatch
	 * @throws InterruptedException
	 * @throws TransitionDefinitionException
	 * @throws MoveDefinitionException
	 * @throws TransitionDefinitionException
	 * @throws MoveDefinitionException
	 */
	public UCTTree(MachineState initMatch, StateMachineGamer smg, int num_players) throws InterruptedException, MoveDefinitionException, TransitionDefinitionException {
		this.sm = smg.getStateMachine();
		this.num_players = num_players;
		TreeLevel rootLevel = new TreeLevel();
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
			TreeLevel childLevel = null;
			if(stateLists.size() <= nodeModel.depth+1){
				childLevel = new TreeLevel();
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
	
	/**
	 * Expand the UCT from this current node
	 * @param nodeModel
	 * @throws MoveDefinitionException
	 * @throws TransitionDefinitionException
	 * @return StateActionPair list of added STAPs 
	 */
	public List<List<Move>> expandNode(StateModel nodeModel) throws MoveDefinitionException, TransitionDefinitionException {
		final List<List<Move>> legalMoves = sm.getLegalJointMoves(nodeModel.state);
		final List<StateActionPair> added = new ArrayList<StateActionPair>();
		final int moveCount =  legalMoves.size();
		
		for(int i = 0; i < moveCount; i++){
			List<Move> m = legalMoves.get(i);
			MachineState nextState = sm.getNextState(nodeModel.state, m);
			StateModel nextStateModel = new StateModel( nextState, nodeModel.depth+1);
			TreeLevel childLevel = null;
			if(stateLists.size() <= nodeModel.depth+1){
				childLevel = new TreeLevel();
				stateLists.add(childLevel);
			} else {
				childLevel = stateLists.get(nodeModel.depth+1);
			}
			childLevel.states.put(nextState, nextStateModel);
			
			// make a connection between the two
			StateActionPair act = new StateActionPair(nextStateModel, m, num_players);
			nodeModel.actionsPairs.put(m, act);
			added.add(act);
			
		}
		return legalMoves;
	}

	public void print(StringBuilder b) {
		b.append("UCTTree\n");
		for (int i = 0; i < stateLists.size(); i++) { // for
			b.append("level " + i + " \n");
			stateLists.get(i).print(b);
			b.append("\n");
			b.append("-----");
		}
	}

	public ArrayList<TreeLevel> getStateLists() {
		return stateLists;
	}
}
