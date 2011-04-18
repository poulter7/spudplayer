package shef.network;

import java.util.Arrays;

import org.junit.Test;

import cs227b.teamIago.resolver.Atom;

/**
 * Contains judgement tests present to verify the shef.network generates sensible
 * decisions when comparing two possible states
 * 
 * @author jonathan
 * 
 */
public class JudgementTestsTTT extends AbstractJudgementTests {
    private static final String gameLocation = "tictactoe";
    
    protected void setUp() throws Exception {
        CIL2PNet cn = CIL2PFactory.createGameNetworkFromFile(gameLocation);
        cil2p_manager = new CIL2PManager(cn, Arrays.asList(new Atom("XPLAYER"), new Atom("OPLAYER")));
        
        super.setUp();
    }
    
	@Test
	public void testNextMoveWinOne() {
		/*
		 * OOO   OO-
		 * --X > --X
		 * --X   O-X
		 */
		double[] scoresA = cil2p_manager.getAllPlayerScores(
				CorrectnessTestsTTT.translateTicTacToe("o o o b b x b b x", cil2p_manager));
		double[] scoresB = cil2p_manager.getAllPlayerScores(
				CorrectnessTestsTTT.translateTicTacToe("o o b b b x o b x", cil2p_manager));
		
		assertEquals(1, bestState(scoresA, scoresB, 1));
	}
	
	@Test
	public void testNextMoveWinTwo() {
		/*
		 * XXO   XX-
		 * -OX > OOX
		 * O-X   O-X
		 */
		double[] scoresA = cil2p_manager.getAllPlayerScores(
				CorrectnessTestsTTT.translateTicTacToe("x x b o o x o b x", cil2p_manager));
		double[] scoresB = cil2p_manager.getAllPlayerScores(
				CorrectnessTestsTTT.translateTicTacToe("x x o b o x o b x", cil2p_manager));
		
		assertEquals(-1, bestState(scoresA, scoresB, 1));
	}

	@Test
	public void testNextMoveOne() {
		/*
		 * XX-   X--
		 * --- > --X
		 * O--   O--
		 */
		double[] scoresA = cil2p_manager.getAllPlayerScores(
				CorrectnessTestsTTT.translateTicTacToe("x x b b b b b b b", cil2p_manager));
		double[] scoresB = cil2p_manager.getAllPlayerScores(
				CorrectnessTestsTTT.translateTicTacToe("x b b b b x b b b", cil2p_manager));
		
		assertEquals(1, bestState(scoresA, scoresB, 0));
	}
	
	@Test
	public void testNextMoveTwo() {
		/*
		 * XX-   XXO
		 * -O- < ---
		 * O--   O--
		 */
		double[] scoresA = cil2p_manager.getAllPlayerScores(
				CorrectnessTestsTTT.translateTicTacToe("x x b b o b o b b", cil2p_manager));
		double[] scoresB = cil2p_manager.getAllPlayerScores(
				CorrectnessTestsTTT.translateTicTacToe("x x o b b b o b b", cil2p_manager));
		// we expect the O score to be the same
		// as in a fluent-wise comparison they're almost identical
		// but minimizing the X score, we should prefer the second state
		assertEquals(-1, bestState(scoresA, scoresB, 0));
	}
	
}
