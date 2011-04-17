package shef.strategies.uct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import player.gamer.statemachine.StateMachineGamer;
import player.gamer.statemachine.reflex.event.ReflexMoveSelectionEvent;
import player.gamer.statemachine.reflex.gui.ReflexDetailPanel;
import shef.strategies.uct.tree.StateActionPair;
import shef.strategies.uct.tree.StateModel;
import shef.strategies.uct.tree.Tree;
import util.statemachine.MachineState;
import util.statemachine.Move;
import util.statemachine.Role;
import util.statemachine.StateMachine;
import util.statemachine.exceptions.GoalDefinitionException;
import util.statemachine.exceptions.MoveDefinitionException;
import util.statemachine.exceptions.TransitionDefinitionException;
import util.statemachine.implementation.prover.cache.CachedProverStateMachine;
import apps.player.detail.DetailPanel;

/**
 * A base class for any player based on the UCT and changing the out of tree
 * exploration.
 * 
 * Creates an UCT tree and performs expansion on nodes based on the UCT
 * algorithm. <cite>
 * 
 * @author jonathan
 * 
 */
public abstract class UCTBaseGamer extends StateMachineGamer {

	/**
	 * C in the UCT equation, this alters the balance between exploration and
	 * exploitation
	 */
	protected static final float C = 50;

	/** Role of the player */
	private Role myRole;

	/** Index of the player's role in the player list */
	private int myRoleID;

	/** Total number of players */
	protected int roleCount;

	/** UCT tree */
	private Tree tree;

	/** Number of moves played */
	private int moveCount;

	/** Handle to the StateMachine governing this player */
	protected StateMachine theMachine;

	/** The named player roles of this game */
	protected List<Role> roles;

	/**
	 * Uses a CachedProverStateMachine
	 */
	@Override
	public StateMachine getInitialStateMachine() {
		return new CachedProverStateMachine();
	}

	/**
	 * Setup the UCT game tree and perform rollouts for as long as possible.
	 * 
	 * @param timeout
	 *            time in ms this meta game stage should be finished by
	 */
	@Override
	public void stateMachineMetaGame(final long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		// setup essential items
		final long finishBy = timeout - 1000;

		theMachine = getStateMachine();
		myRole = getRole();
		roles = theMachine.getRoles();
		myRoleID = roles.indexOf(myRole);
		roleCount = roles.size();
		moveCount = 0;
		System.out.println("init " + this.getClass() + "\nas player... " + myRole +" (" + myRoleID + ")");
		try {
			tree = new Tree(getCurrentState(), this, roleCount);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// implementation specific setup
		strategyMetaSetup();
		
		// begin rollouts with time left
		final StateModel currentSM = tree.getStateLists().get(moveCount).states.get(getStateMachine().getInitialState());
		
		int rollCount = 0;
		System.out.println("beginning rollouts");
		while (System.currentTimeMillis() < finishBy) {
			rollout(currentSM);
			rollCount++;
		}
		System.out.println(rollCount + " initial");
		
	}
	
	public abstract void strategyMetaSetup();

	/**
	 * As many times as possible in the time available perform rollouts from the
	 * current state
	 * 
	 * @param timeout
	 *            when in ms this move selection should be completed by
	 * @return the move attributed to the most promising {@link StateActionPair}
	 */
	@Override
	public Move stateMachineSelectMove(final long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		final long start = System.currentTimeMillis();
		final long finishBy = timeout - 1000;
		final MachineState cState = getCurrentState();
		final StateModel currentSM = tree.getStateLists().get(moveCount).states.get(cState);
		final List<Move> moves = theMachine.getLegalMoves(cState, myRole);
		
		Move selection = moves.get(0);
		int rollCount = 0;

		while (true) {
			if (System.currentTimeMillis() > finishBy) {
				// select best move!
				double maxVal = Float.NEGATIVE_INFINITY;
				List<Move> maxMove = null;
				HashMap<List<Move>, StateActionPair> saps = currentSM.actionsPairs;
				for (Entry<List<Move>, StateActionPair> sap : saps.entrySet()) {
					System.out.println("Move " + sap.getKey() + " explored " + sap.getValue().timesExplored + " " + Arrays.toString(sap.getValue().VALUE));
					double v = sap.getValue().VALUE[myRoleID];
					if (v > maxVal || maxMove == null) {
						maxMove = sap.getKey();
						maxVal = v;
					}
				}
				selection = maxMove.get(myRoleID);
				break;
			}

			rollout(currentSM);
			rollCount++;

		}
		final long stop = System.currentTimeMillis();
		moveCount++;
//		 StringBuilder sb = new StringBuilder();
//		 tree.print(sb);
//		 System.out.println(sb.toString());
		notifyObservers(new ReflexMoveSelectionEvent(moves, selection, stop - start));
		System.out.println(rollCount + " " + selection);
		return selection;
	}

	

	/**
	 * Perform a single UCT rollout
	 * 
	 * @param rolloutRootSM
	 *            state to begin rollout from
	 * @throws MoveDefinitionException
	 * @throws TransitionDefinitionException
	 * @throws GoalDefinitionException
	 */
	private void rollout(final StateModel rolloutRootSM) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
		StateModel traverser = rolloutRootSM;
		
		// get all of the actions which can be performed from this move 
		ArrayList<StateActionPair> actions = new ArrayList<StateActionPair>(traverser.actionsPairs.values());
		ArrayList<StateActionPair> backupSAPs = new ArrayList<StateActionPair>();
		ArrayList<StateModel> backupStates = new ArrayList<StateModel>();

		boolean expandLeaf = true;

		while (!actions.isEmpty()) {
			List<Move> toPlay = new ArrayList<Move>();
			
			// for each player discover the best move
			for (int p = 0; p < roleCount; p++) {
				expandLeaf = true;
				int i = 0;
				float[] v = new float[actions.size()];
				for (StateActionPair sap : actions) {
					if (sap.timesExplored == 0) {
						v[i] = Float.POSITIVE_INFINITY;
						expandLeaf = false;
					} else {
						float uctBonus = (float) Math.sqrt(Math.log(traverser.timesExplored) / (float) sap.timesExplored);
						v[i] = (float) (sap.VALUE[p] + C * uctBonus);
					}
					i++;
				}

				// index of highest valued node
				int index = 0;
				float lowest = Integer.MIN_VALUE;
				for (int j = 0; j < v.length; j++) {
					if (v[j] > lowest) {
						index = j;
						lowest = v[j];
					}
				}
				toPlay.add(actions.get(index).ACTION.get(p));
			}
			backupStates.add(traverser);

			StateActionPair chosenSAP = traverser.actionsPairs.get(toPlay);
			backupSAPs.add(chosenSAP);
			traverser = chosenSAP.RESULT;
			actions = new ArrayList<StateActionPair>(traverser.actionsPairs.values());

		}
		
		// include the last state visited
		backupStates.add(traverser);
		
		if (expandLeaf && !theMachine.isTerminal(traverser.state)) {
			StateActionPair nextAction = tree.expandNodeAndReturnRandom(traverser);
			// add the randomly chosen action
			traverser = nextAction.RESULT;
			backupStates.add(traverser);
			backupSAPs.add(nextAction);
		}
		
		List<Double> outcome;
		if (!theMachine.isTerminal(traverser.state)) {
			// complete the rollouts past this UCT horizon
			outcome = completeRollout(traverser.state, traverser.depth+1);
		} else {
			outcome = theMachine.getDoubleGoals(traverser.state);
		}

		// distribute goal to each player
		backpropogate(backupSAPs, backupStates, outcome);
	}

	/**
	 * Complete the rest of this UCT rollout past the UCT horizon
	 * 
	 * @param from
	 *            the state to complete rollout from
	 * @return the terminal state reached
	 * @throws MoveDefinitionException 
	 * @throws TransitionDefinitionException 
	 * @throws GoalDefinitionException 
	 */
	protected abstract List<Double> completeRollout(final MachineState from, final int fromLvl) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException;

	/**
	 * Discount factor applied to each backup of the reward. The reward should
	 * have a great effect on the states close to it and less to those further
	 * away.
	 */
	private static final double discountFactor = 0.95;

	/**
	 * Update every state visited in this path and update its average. Applying
	 * a discount factor to the result at every stage.
	 * 
	 * @param backupStatesPairs the state pairs visited
	 * @param backupStates the states visited
	 * @param outcome the resulting reqard from the terminal state reach on rollout
	 */
	private void backpropogate(final List<StateActionPair> backupStatesPairs, final List<StateModel> backupStates, List<Double> outcome) {
		for (StateModel m : backupStates) {
			m.timesExplored++;
		}
		
		// the last entry recieves highest weight of credit
		Collections.reverse(backupStatesPairs);
		for(StateActionPair sap : backupStatesPairs){
			sap.updateAverage(outcome);
			
			// degrade reward to prefer earlier wins
			for (int i = 0; i < roleCount; i++) {
				outcome.set(i, outcome.get(i) * discountFactor);
			}
		}
	}

	@Override
	public DetailPanel getDetailPanel() {
		return new ReflexDetailPanel();
	}

}
