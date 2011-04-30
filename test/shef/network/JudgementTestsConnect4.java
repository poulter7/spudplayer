package shef.network;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import cs227b.teamIago.resolver.Atom;

public class JudgementTestsConnect4 extends AbstractJudgementTests {
	
	private static final String gameLocation = "connect4";
//	private static final String gameLocation = "connect4";
	private static final List<Atom> playerList = Arrays.asList(new Atom("white"), new Atom("red"));
	
	@Override
	protected void setUp() throws Exception {
	    CIL2PNet cn = CIL2PFactory.createGameNetworkFromFile(gameLocation);
	    cil2p_manager = new CIL2PManager(cn, playerList);
	    
	    super.setUp();
	}
	
	@Test
	public void testNextMoveWinOne() {
		
		double[] output1 = CorrectnessTestsConnect4.translateConnect4(
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
		cil2p_manager.printActivationGoalOutput();
		
		double[] output2 = CorrectnessTestsConnect4.translateConnect4(
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
		cil2p_manager.printActivationGoalOutput();
		
		assertEquals(-1, bestState(scoresA, scoresB, 1));
		
	}
	
	@Test
	public void testNextMoveWinTwo() {
		
		double[] output1 = CorrectnessTestsConnect4.translateConnect4(
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
		cil2p_manager.printActivationGoalOutput();
		System.out.println();
		double[] output2 = CorrectnessTestsConnect4.translateConnect4(
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
		cil2p_manager.printActivationGoalOutput();
		assertEquals(1, bestState(scoresA, scoresB, 0));
		
	}
	
	@Test
	public void testStateCompare() {
		
		double[] output1 = CorrectnessTestsConnect4.translateConnect4(
				"D D D D D D D D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"R B B B B B B D " +
				"W B B B B B B D " +
				"W W R B B B B D " +
				"W R W R B B B D " + 
				"D D D D D D D D", cil2p_manager
		);
		double[] scoresA = cil2p_manager.getAllPlayerScores(output1);
		System.out.println();
		double[] output2 = CorrectnessTestsConnect4.translateConnect4(
				"D D D D D D D D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " +
				"W R B B B B B D " +
				"W W R B B B B D " +
				"W R W R B B B D " + 
				"D D D D D D D D", cil2p_manager
		);
		double[] scoresB = cil2p_manager.getAllPlayerScores(output2);
		double[] output3 = CorrectnessTestsConnect4.translateConnect4(
				"D D D D D D D D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " +
				"W B R B B B B D " +
				"W W R B B B B D " +
				"W R W R B B B D " + 
				"D D D D D D D D", cil2p_manager
		);
		double[] scoresC = cil2p_manager.getAllPlayerScores(output3);
		double[] output4 = CorrectnessTestsConnect4.translateConnect4(
				"D D D D D D D D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " +
				"W B B B B B B D " +
				"W W R R B B B D " +
				"W R W R B B B D " + 
				"D D D D D D D D", cil2p_manager
		);
		double[] scoresD = cil2p_manager.getAllPlayerScores(output4);
		double[] output5 = CorrectnessTestsConnect4.translateConnect4(
				"D D D D D D D D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " +
				"W B B B B B B D " +
				"W W R B B B B D " +
				"W R W R R B B D " + 
				"D D D D D D D D", cil2p_manager
		);
		double[] scoresE = cil2p_manager.getAllPlayerScores(output5);
		double[] output6 = CorrectnessTestsConnect4.translateConnect4(
				"D D D D D D D D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " +
				"W B B B B B B D " +
				"W W R B B B B D " +
				"W R W R B R B D " + 
				"D D D D D D D D", cil2p_manager
		);
		double[] scoresF = cil2p_manager.getAllPlayerScores(output6);
		double[] output7 = CorrectnessTestsConnect4.translateConnect4(
				"D D D D D D D D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " +
				"W B B B B B B D " +
				"W W R B B B B D " +
				"W R W R B B R D " + 
				"D D D D D D D D", cil2p_manager
		);
		double[] scoresG = cil2p_manager.getAllPlayerScores(output7);
		System.out.println(Arrays.toString(scoresA));
		System.out.println(Arrays.toString(scoresB));
		System.out.println(Arrays.toString(scoresC));
		System.out.println(Arrays.toString(scoresD));
		System.out.println(Arrays.toString(scoresE));
		System.out.println(Arrays.toString(scoresF));
		System.out.println(Arrays.toString(scoresG));
		
	}
	
}
