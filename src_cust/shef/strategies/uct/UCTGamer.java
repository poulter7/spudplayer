package shef.strategies.uct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import player.gamer.statemachine.StateMachineGamer;
import player.gamer.statemachine.reflex.event.ReflexMoveSelectionEvent;
import player.gamer.statemachine.reflex.gui.ReflexDetailPanel;
import shef.strategies.uct.tree.Level;
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
public abstract class UCTGamer extends StateMachineGamer {

	/**
	 * C in the UCT equation, this alters the balance between exploration and
	 * exploitation
	 */
	private static final float C = 50;

	/** Role of the player */
	private Role myRole;

	/** Index of the player's role in the player list */
	private int myRoleID;

	/** Total number of players */
	protected int roleCount;

	/** UCT tree */
	private Tree tree;

	/** Number of roles played */
	private int moveCount;

	/** Handle to the StateMachine governing this player */
	protected StateMachine theMachine;

	/** */
	protected List<Role> roles;

	/**
	 * Uses a CachedProverStateMachine
	 */
	@Override
	public StateMachine getInitialStateMachine() {
		return new CachedProverStateMachine();
	}

	/**
	 * Setup the UCT game tree
	 */
	@Override
	public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		System.out.println("init");
		long finishBy = timeout - 1000;

		theMachine = getStateMachine();
		myRole = getRole();
		roles = theMachine.getRoles();
		myRoleID = roles.indexOf(myRole);
		roleCount = roles.size();
		moveCount = 0;
		try {
			tree = new Tree(getCurrentState(), this, roleCount);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StateModel currentSM = tree.getStateLists().get(moveCount).states.get(getStateMachine().getInitialState());
		int rollCount = 0;
		System.out.println("beginning rollouts");
		while (System.currentTimeMillis() < finishBy) {
			rollout(currentSM);
			rollCount++;
		}
		System.out.println(rollCount + " initial");
	}

	/**
	 * As many times as possible in the time available perform rollouts from the
	 * current state
	 * 
	 * @return the move attributed to the most promising {@link StateActionPair}
	 */
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		long start = System.currentTimeMillis();
		long finishBy = timeout - 1000;
		MachineState cState = getCurrentState();
		StateModel currentSM = tree.getStateLists().get(moveCount).states.get(cState);

		List<Move> moves = theMachine.getLegalMoves(cState, myRole);
		Move selection = moves.get(0);
		int rollCount = 0;

		while (true) {
			if (System.currentTimeMillis() > finishBy) {
				// select best move!
				double maxVal = Float.NEGATIVE_INFINITY;
				List<Move> maxMove = null;
				HashMap<List<Move>, StateActionPair> saps = currentSM.actionsPairs;
				for (Entry<List<Move>, StateActionPair> sap : saps.entrySet()) {
					System.out.println("Move " + sap.getKey() + " explored " + sap.getValue().timesExplored + " " + Arrays.toString(sap.getValue().value));
					double v = sap.getValue().value[myRoleID];
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
		long stop = System.currentTimeMillis();


		moveCount++;
		// StringBuilder sb = new StringBuilder();
		// tree.print(sb);
		// System.out.println(sb.toString());
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
	private List<StateActionPair> backupSAPs;
	private List<StateModel> backupStates;
	private void rollout(StateModel rolloutRootSM) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
		StateModel traverser = rolloutRootSM;
		List<StateActionPair> actions = new ArrayList<StateActionPair>(traverser.actionsPairs.values());
		backupSAPs = new ArrayList<StateActionPair>();
		backupStates = new ArrayList<StateModel>();

		boolean expandLeaf = true;
		int lvl = 0;
		
		while (!actions.isEmpty()) {
			List<Move> toPlay = new ArrayList<Move>();
			// for each player
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
						v[i] = (float) (sap.value[p] + C * uctBonus);
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
				toPlay.add(actions.get(index).action.get(p));
			}
			backupStates.add(traverser);

			StateActionPair chosenSAP = traverser.actionsPairs.get(toPlay);
			backupSAPs.add(chosenSAP);
			traverser = chosenSAP.result;
			actions = new ArrayList<StateActionPair>(traverser.actionsPairs.values());
			lvl++;

		}

		backupStates.add(traverser);
		if (expandLeaf && !theMachine.isTerminal(traverser.state)) {
			tree.expandNode(traverser);
			Level cur = tree.getStateLists().get(traverser.depth + 1);
			traverser = cur.states.get(theMachine.getRandomNextState(traverser.state));
		}

		List<Double> outcome;
		if (!theMachine.isTerminal(traverser.state)) {
			outcome = completeRollout(traverser.state, lvl);
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
	 */
	protected abstract List<Double> completeRollout(MachineState from, int fromLvl);

	/**
	 * Update every state visited in this path and update its average
	 * 
	 * @param backupStatesPairs
	 * @param outcome
	 */
	private void backpropogate(final List<StateActionPair> backupStatesPairs, final List<StateModel> backupStates, List<Double> outcome) {
		// degrade reward to prefer earlier wins
		for (StateModel m : backupStates) {
			m.timesExplored++;
		}
		for (StateActionPair s : backupStatesPairs) {
			s.updateAverage(outcome);
		}
	}

	@Override
	public DetailPanel getDetailPanel() {
		return new ReflexDetailPanel();
	}

}
