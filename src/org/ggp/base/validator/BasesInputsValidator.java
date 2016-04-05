package org.ggp.base.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ggp.base.util.game.CloudGameRepository;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.game.GameRepository;
import org.ggp.base.util.gdl.grammar.GdlConstant;
import org.ggp.base.util.gdl.grammar.GdlPool;
import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.gdl.grammar.GdlTerm;
import org.ggp.base.util.gdl.grammar.GdlVariable;
import org.ggp.base.util.prover.aima.AimaProver;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;


public class BasesInputsValidator implements GameValidator {
	private static final GdlConstant BASE = GdlPool.getConstant("base");
	private static final GdlConstant INPUT = GdlPool.getConstant("input");
	private static final GdlConstant TRUE = GdlPool.getConstant("true");
	private static final GdlConstant LEGAL = GdlPool.getConstant("legal");
	private static final GdlVariable X = GdlPool.getVariable("?x");
	private static final GdlVariable Y = GdlPool.getVariable("?y");

	private int millisecondsToTest;
	public BasesInputsValidator(int millisecondsToTest) {
		this.millisecondsToTest = millisecondsToTest;
	}

	@Override
	public void checkValidity(Game theGame) throws ValidatorException {
		try {
			StateMachine sm = new ProverStateMachine();
			sm.initialize(theGame.getRules());

			AimaProver prover = new AimaProver(theGame.getRules());
			GdlSentence basesQuery = GdlPool.getRelation(BASE, new GdlTerm[] {X});
			Set<GdlSentence> bases = prover.askAll(basesQuery, Collections.<GdlSentence>emptySet());
			GdlSentence inputsQuery = GdlPool.getRelation(INPUT, new GdlTerm[] {X, Y});
			Set<GdlSentence> inputs = prover.askAll(inputsQuery, Collections.<GdlSentence>emptySet());
			Set<GdlSentence> truesFromBases = new HashSet<GdlSentence>();
			for (GdlSentence base : bases) {
				truesFromBases.add(GdlPool.getRelation(TRUE, base.getBody()));
			}
			Set<GdlSentence> legalsFromInputs = new HashSet<GdlSentence>();
			for (GdlSentence input : inputs) {
				legalsFromInputs.add(GdlPool.getRelation(LEGAL, input.getBody()));
			}

			if (truesFromBases.isEmpty() && legalsFromInputs.isEmpty()) {
				return;
			}

			MachineState initialState = sm.getInitialState();
			MachineState state = initialState;
			long startTime = System.currentTimeMillis();
			while (System.currentTimeMillis() < startTime + millisecondsToTest) {
				//Check state against bases, inputs
				if (!truesFromBases.isEmpty()) {
					if (!truesFromBases.containsAll(state.getContents())) {
						Set<GdlSentence> missingBases = new HashSet<GdlSentence>();
						missingBases.addAll(state.getContents());
						missingBases.removeAll(truesFromBases);
						throw new ValidatorException("Found missing bases: " + missingBases);
					}
				}

				if (!legalsFromInputs.isEmpty()) {
					List<GdlSentence> legalSentences = new ArrayList<GdlSentence>();
					for (Role role : sm.getRoles()) {
						List<Move> legalMoves = sm.getLegalMoves(state, role);
						for (Move move : legalMoves) {
							legalSentences.add(GdlPool.getRelation(LEGAL, new GdlTerm[] {role.getName(), move.getContents()}));
						}
					}
					if (!legalsFromInputs.containsAll(legalSentences)) {
						Set<GdlSentence> missingInputs = new HashSet<GdlSentence>();
						missingInputs.addAll(legalSentences);
						missingInputs.removeAll(legalsFromInputs);
						throw new ValidatorException("Found missing inputs: " + missingInputs);
					}
				}

				state = sm.getRandomNextState(state);
				if (sm.isTerminal(state)) {
					state = initialState;
				}
			}
		} catch (MoveDefinitionException mde) {
			throw new ValidatorException("Could not find legal moves while simulating: " + mde);
		} catch (TransitionDefinitionException tde) {
			throw new ValidatorException("Could not find transition definition while simulating: " + tde);
		} catch (RuntimeException e) {
			throw new ValidatorException("Ran into a runtime exception while simulating: " + e);
		} catch (StackOverflowError e) {
			throw new ValidatorException("Ran into a stack overflow while simulating: " + e);
		} catch (OutOfMemoryError e) {
			throw new ValidatorException("Ran out of memory while simulating: " + e);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		GameRepository gameRepo = new CloudGameRepository("http://games.ggp.org/stanford/");

		for (String gameKey : gameRepo.getGameKeys()) {
			if (!gameKey.equals("amazons") //Skip games that currently result in out-of-memory errors
					&& !gameKey.equals("alexChess")) {
				try {
					new BasesInputsValidator(20000).checkValidity(gameRepo.getGame(gameKey));
					System.out.println("Game " + gameKey + " has valid base/input propositions.");
				} catch (ValidatorException ve) {
					System.out.println("Game " + gameKey + " is invalid: " + ve.getMessage());
				}
			}
		}
	}
}