package shef.network;

import java.util.Arrays;

import org.junit.Test;

import shef.network.CIL2PFactory;
import shef.network.CIL2PManager;
import shef.network.CIL2PNet;
import cs227b.teamIago.resolver.Atom;

/**
 * More complicated tests on CIL2P_Manager and CIL2P_Net
 * testing is against Connect4 game
 * @author jonathan
 *
 */
public class CorrectnessTestsTTT extends AbstractCorrectnessTests{


	static final String gameLocation = "tictactoe";
	/**
	 * Check for the correct instantiation of tictactoe shef.network.
	 */
	@Test
	public void testTopology(){
	    CIL2PNet network = CIL2PFactory.createNetworkFromFileLocation(gameLocation);
        cil2p_manager =  new CIL2PManager(network);
		/*
		 * INPUTS
		 * (1,1,X) -> (3,3,X)
		 * (1,1,O) -> (3,3,O)
		 * 9 grid cells X or O
		 */
		assertEquals(27, cil2p_manager.getPlayInfo()[0]);
		/*
		 * GOALS
		 * Should be six possible end conditions
		 * W/L/D for both players
		 */
		assertEquals(6, cil2p_manager.getPlayInfo()[1]);
		cil2p_manager.printInfo();
		cil2p_manager.printActivationAllOutput();
	}
	
	/**
	 * Check for the correct instantiation of tictactoe shef.network
	 */
	@Test
	public void testInitialisation1(){
		CIL2PNet network = CIL2PFactory.createNetworkFromFileLocation("tictactoe-init1");
        cil2p_manager =  new CIL2PManager(network);
		/*
		 * INPUTS
		 * (1,1,X) -> (3,3,X)
		 * (1,1,O) -> (3,3,O)
		 * 9 grid cells X or O
		 */
		assertEquals(23, cil2p_manager.getPlayInfo()[0]);
		/*
		 * GOALS
		 * Should be six possible end conditions
		 * W/L/D for both players
		 */
		assertEquals(6, cil2p_manager.getPlayInfo()[1]);
	}
	
	/**
	 * Check if evaluation of TTT is correct
	 */
	@Test
	public void testTicTacToeWinningOne(){
		testTicTacToeWinning(gameLocation);
	}
	
	/**
	 * different initialisation of TTT
	 */
	@Test
	public void testTicTacToeWinningTwo(){
		testTicTacToeWinning("tictactoe-init1");
	}
	
	
	/**
	 * Checking the results of the CIL2P shef.network. Various X wins and O wins
	 * layout of input (providing <code>testTicTacToeInstantiation</code> passes) is:
	 * <pre>
	# goal units 6
	[(GOAL OPLAYER 0 ),  (GOAL XPLAYER 0 ),
	 (GOAL OPLAYER 100 ),(GOAL XPLAYER 100 ),
	 (GOAL OPLAYER 50 ), (GOAL XPLAYER 50 )]
	 *  </pre>
	 */
	public void testTicTacToeWinning(String ttt){
		CIL2PNet network = CIL2PFactory.createNetworkFromFileLocation(ttt);
		cil2p_manager =  new CIL2PManager(network, Arrays.asList(new Atom("xplayer"), new Atom("oplayer")));
		double[] scores = cil2p_manager.getAllPlayerScores(translateTicTacToe( "b b b b b b b b b", cil2p_manager));
		
//		assertEquals(2, scores.length, 0);
		assertEquals(scores[0], scores[1], 1);	// start
		
		scores = cil2p_manager.getAllPlayerScores(translateTicTacToe("o b b b b b b b b", cil2p_manager));
		assertEquals(scores[0], scores[1], 1);				// win for O
		
		scores = cil2p_manager.getAllPlayerScores(translateTicTacToe("x x b o o x o o x", cil2p_manager));
		assertEquals(scores[0], scores[1], 1);				// win for X
		
		
		scores = cil2p_manager.getAllPlayerScores(translateTicTacToe("x x b b o b o b b", cil2p_manager));
		assertEquals(scores[0], scores[1], 1);			// draw
		
		scores = cil2p_manager.getAllPlayerScores(translateTicTacToe("x o x o o x x x o", cil2p_manager));
		assertEquals(scores[0], scores[1], 1);			// draw
		
		scores = cil2p_manager.getAllPlayerScores(translateTicTacToe("x x o o o x x x o", cil2p_manager));
		assertEquals(scores[0], scores[1], 1);			// draw
		
		
		scores = cil2p_manager.getAllPlayerScores(translateTicTacToe("x x x o o x b b b", cil2p_manager));
		cil2p_manager.printActivationGoalOutput();
		assertTrue(scores[0] > scores[1]);				// win for X
	
		scores = cil2p_manager.getAllPlayerScores(translateTicTacToe("x x x x o o x o o", cil2p_manager));
		assertTrue(scores[0] > scores[1]);				// win for X
		
		scores = cil2p_manager.getAllPlayerScores(translateTicTacToe("o x x x o x x x o", cil2p_manager));
		System.out.println(Arrays.toString(scores));
		assertTrue(scores[0] < scores[1]);				// win for O
		
	}

	/**
	 * Translate a simple tictactoe string into a shef.network input
	 * @param in the string rep of the board
	 * @return shef.network input
	 */
	public static double[] translateTicTacToe(String input, CIL2PManager manager){
		String qString = (manager.getQueryPredicates().toString());
		String sString = qString.substring(qString.indexOf("CELL") + 5, qString.lastIndexOf(']')-2);
		String[] strings = sString.split("\\ \\), \\(CELL\\ ");
		double[] out = new double[strings.length];
		String[] in = input.split("\\s");
		assert in.length == 9;
		for(int i = 0; i < strings.length; i++){
			// get row, col, val 
			String[] key = strings[i].split("\\s");
			// get index of string ref
			int refIndex = (Integer.parseInt(key[0]) - 1)*3 + (Integer.parseInt(key[1]) - 1);
			if(key[2].equalsIgnoreCase(in[refIndex])){
				out[i]=1;
			}else{
				out[i]=-1;
			}
		}
		return out;
	}
	
}
