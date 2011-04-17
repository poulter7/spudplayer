package shef.strategies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import player.gamer.statemachine.StateMachineGamer;
import player.gamer.statemachine.reflex.event.ReflexMoveSelectionEvent;
import player.gamer.statemachine.reflex.gui.ReflexDetailPanel;
import shef.network.Bipolar;
import shef.strategies.uct.tree.StateActionPair;
import shef.strategies.uct.tree.StateModel;
import shef.strategies.uct.tree.UCTTree;
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
 * A base class for any player sets up information that the 
 * play is likely to need
 * 
 * @author jonathan
 * 
 */
public abstract class BaseGamer extends StateMachineGamer {

	

	/** Role of the player */
	protected Role myRole;

	/** Index of the player's role in the player list */
	protected int myRoleID;

	/** Total number of players */
	protected int roleCount;

	/** Number of moves played */
	protected int moveCount;

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
	
	@Override
	public DetailPanel getDetailPanel() {
		return new ReflexDetailPanel();
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
		theMachine = getStateMachine();
		myRole = getRole();
		roles = theMachine.getRoles();
		myRoleID = roles.indexOf(myRole);
		roleCount = roles.size();
		moveCount = 0;
		System.out.println("init " + this.getClass() + "\nas player... " + myRole +" (" + myRoleID + ")");
		
	}
	
}
