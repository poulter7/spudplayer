package network;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import network.correctness.CIL2PConnect4Tests;

import org.junit.Test;

import shef.network.CIL2PFactory;
import shef.network.CIL2PManager;
import shef.network.CIL2PNet;
import cs227b.teamIago.resolver.Atom;

public class CIL2PJudgementConnect4Tests extends AbstractCIL2PJudgementTests {
	
	private static final String gameLocation = "connect4";
	private static final List<Atom> playerList = Arrays.asList(new Atom("WHITE"), new Atom("RED"));
	
	@Override
	protected void setUp() throws Exception {
	    CIL2PNet cn = CIL2PFactory.modNetFromFile(gameLocation);
	    cil2p_manager = new CIL2PManager(cn, playerList);
	    
	    super.setUp();
	}
	
	@Test
	public void testNextMoveWinOne() {
		
		double[] output1 = CIL2PConnect4Tests.translateConnect4(
				"D D D D D D D D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " +
				"B B B R B B B D " +
				"B B B W W B B D " +
				"B B B R R B B D " + 
				"D D D D D D D D", cil2p_manager
		);
		double[] scoresA = cil2p_manager.getAllPlayerScores(output1);
		cil2p_manager.printGoalOutputActivation();
		System.out.println();
		double[] output2 = CIL2PConnect4Tests.translateConnect4(
				"D D D D D D D D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " +
				"B B B B B B B D " +
				"B B B W W B B D " +
				"B B R R R B B D " + 
				"D D D D D D D D", cil2p_manager
		);
		double[] scoresB = cil2p_manager.getAllPlayerScores(output2);
		cil2p_manager.printGoalOutputActivation();
		System.out.println(cil2p_manager.playerList);
		assertEquals(-1, bestState(scoresA, scoresB, 1));
		
	}
	
	@Test
	public void testNextMoveWinTwo() {
		
		double[] output1 = CIL2PConnect4Tests.translateConnect4(
				"D D D D D D D D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " +
				"B B B R W B B D " +
				"B B B W W B B D " +
				"B B W R R B B D " + 
				"D D D D D D D D", cil2p_manager
		);
		double[] scoresA = cil2p_manager.getAllPlayerScores(output1);
		cil2p_manager.printGoalOutputActivation();
		System.out.println();
		double[] output2 = CIL2PConnect4Tests.translateConnect4(
				"D D D D D D D D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " +
				"B B B R B B B D " +
				"B B B W W B B D " +
				"B B W R R B W D " + 
				"D D D D D D D D", cil2p_manager
		);
		double[] scoresB = cil2p_manager.getAllPlayerScores(output2);
		cil2p_manager.printGoalOutputActivation();
		assertEquals(1, bestState(scoresA, scoresB, 0));
		
	}
	
}
