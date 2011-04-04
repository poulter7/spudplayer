package network.correctness;

import java.util.Arrays;

import shef.network.CIL2PFactory;
import shef.network.CIL2PManager;
import shef.network.CIL2PNet;




public class CIL2PConnect4Tests extends AbstractCIL2PCorrectnessTests {

	private static final String gameLocation = "connect4";
	
	@Override
	protected void setUp() throws Exception {

        CIL2PNet cn = CIL2PFactory.fromFileLocation(gameLocation);
        cil2p_manager = new CIL2PManager(cn);
	    super.setUp();
	}
	
	@Override
	public void testTopology() {
		assertEquals(202, cil2p_manager.getPlayInfo()[0]);
		assertEquals(8, cil2p_manager.getPlayInfo()[1]);
		
		cil2p_manager.printInfo();
		cil2p_manager.printPlayInfo();

	}

	/**
	 * test blank board
	 */
	public void testBlank(){
        
		double[] output = translateConnect4(
				"D D D D D D D D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " +
				"B B B B B B B D " +
				"B B B B B B B D " +
				"B B B B B B B D " + 
				"D D D D D D D D", cil2p_manager
		);
		double[] scores = cil2p_manager.getAllPlayerScores(output);
		assertEquals(scores[0], scores[1]);
	}
	
	/**
	 * test simple horizontal win
	 * RED winner
	 */
	public void testHorizWin(){

		double[] output = translateConnect4(
				"D D D D D D D D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " +
				"B B B B B B B D " +
				"B B B B B B B D " +
				"R R R R B B B D " + 
				"D D D D D D D D", cil2p_manager
		);
		double[] scores = cil2p_manager.getAllPlayerScores(output);
		assertTrue(scores[0]>scores[1]);
	}
	
	/**
	 * test horizontal win with a bit more noise going on
	 * RED winner
	 */
	public void testHorizNoiseWin(){

        CIL2PNet cn = CIL2PFactory.fromFileLocation(gameLocation);
        cil2p_manager = new CIL2PManager(cn);
		
		double[] output = translateConnect4(
				"D D D D D D D D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B B B W W W D " + 
				"B B B R R R R D " +
				"B B B W R W R D " +
				"B B B R W W W D " +
				"B B W R W W R D " + 
				"D D D D D D D D", cil2p_manager
		);
		double[] scores = cil2p_manager.getAllPlayerScores(output);
		
		assertTrue(scores[0]>scores[1]);
	}
	
	/**
	 * test vertical win with a bit more noise going on
	 * RED winner
	 */
	public void testVerticalNoiseWin(){
		
		double[] output = translateConnect4(
				"D D D D D D D D " + 
				"B B B B B B B D " + 
				"B B B B B B B D " + 
				"B B R B W W W D " + 
				"B B R W R R R D " +
				"B B R W R W R D " +
				"B B R R W W W D " +
				"B B W R W W R D " + 
				"D D D D D D D D", cil2p_manager
		);
		double[] scores = cil2p_manager.getAllPlayerScores(output);
		assertTrue(scores[0]>scores[1]);
	}
	
	/**
	 * test diag win with a bit more noise going on
	 * WHITE win
	 */
	public void testDiagonalNoiseWin(){
		
		double[] output = translateConnect4(
				"D D D D D D D D " + 
				"B B B B B B B D " + 
				"B B B B B W B D " + 
				"B B R B W W W D " + 
				"B B R W R R R D " +
				"B B W W R W R D " +
				"B B R R W W W D " +
				"B B W R W W R D " + 
				"D D D D D D D D", cil2p_manager
		);
		double[] scores = cil2p_manager.getAllPlayerScores(output);
		assertTrue(scores[0]<scores[1]);
	}
	
	/**
	 * Translate a simple connect4 string into a shef.network input
	 * @param in the string rep of the board
	 * @return shef.network input
	 */
	public static double[] translateConnect4(String input, CIL2PManager manager){
		String qString = (manager.getQueryPredicates().toString());
		String sString = qString.substring(qString.indexOf("CELL") + 5, qString.lastIndexOf(']')-2);
		String[] strings = sString.split("\\ \\), \\(CELL\\ ");
		double[] out = new double[strings.length];
		String[] in = input.split("\\s");
		for(int i = 0; i < strings.length; i++){
			// get row, col, val 
			String[] key = strings[i].split("\\s");
			// get index of string ref
			int refIndex = 72 - ((Integer.parseInt(key[1])*8) + (9-Integer.parseInt(key[0])));
			if(key[2].equalsIgnoreCase(in[refIndex])){
				out[i]=1;
			}else{
				out[i]=-1;
			}
		}
		return out;
	}
}
