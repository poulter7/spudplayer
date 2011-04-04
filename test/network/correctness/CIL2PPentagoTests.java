package network.correctness;

import java.util.Arrays;


import org.junit.Test;

import shef.network.CIL2PFactory;
import shef.network.CIL2PManager;
import shef.network.CIL2PNet;

/**
 * Test class for Pentago
 * Will check that pentago instantiates properly
 * @author jonathan
 *
 */
public class CIL2PPentagoTests extends AbstractCIL2PCorrectnessTests{
    
    private static final String gameLocation = "pentago";
    
    @Override
    protected void setUp() throws Exception {
        CIL2PNet cn = CIL2PFactory.fromFileLocation(gameLocation);
        cil2p_manager = new CIL2PManager(cn);
        
        super.setUp();
    }
    
	@Override
	public void testTopology() {
		/*
		 * INPUTS
		 * (CELLHOLDS ?QUAD ?ROW ?COL ?COLOR)
		 * ?QUAD  : [1 -> 4]
		 * ?ROW   : [1 -> 3]
		 * ?COL   : [1 -> 3]
		 * ?COLOR : RED/BLACK 
		 */
		assertEquals(72, cil2p_manager.getPlayInfo()[0]);
		/*
		 * GOALS
		 * Should be six possible end conditions
		 * W/L/D for both players
		 */
		assertEquals(6, cil2p_manager.getPlayInfo()[1]);	
		cil2p_manager.printPlayInfo();
		cil2p_manager.printInfo();
	}
	
	@Test
	public void testObviousWin(){
		double maxError= 1 - cil2p_manager.getMaxError();
		
		double[] scores;
		scores = cil2p_manager.getAllPlayerScores(translatePentago(
				"R R R R R R " +
				"R R R R R R " +
				"R R R R R R " +
				"R R R R R R " +
				"R R R R R R " +
				"R R R R R R "));
		System.out.println(maxError);
//		assertTrue(scores[0] == scores[1] );
		cil2p_manager.printInputActivation();
		System.out.println();
		cil2p_manager.printOutputActivation();
		System.out.println(Arrays.toString(scores));
	}
	
	@Test
	public void testEmpty(){
		double maxError= 1 - cil2p_manager.getMaxError();
		
		double[] scores;
		scores = cil2p_manager.getAllPlayerScores(translatePentago(
				"- - - - - - " +
				"- - - - - - " +
				"- - - - - - " +
				"- - - - - - " +
				"- - - - - - " +
		"- - - - - - "));
		assertEquals(scores[0], scores[1], maxError);
	}
	
	
	/**
	 * Translate a simple pentago string into a shef.network input
	 * 
	 * A given cellholds query (CELLHOLDS ?q ?x ?y)
	 * tranlates to a given bored index 1:36 by
	 * (?x -1) * 6 + (c-1) + ((q-1)/2)*18 + ((q-1)%2)*3
	 * @param in the string rep of the board
	 * @return shef.network input
	 */
	public double[] translatePentago(String input){
		String qString = (cil2p_manager.getQueryPredicates().toString());
		System.out.println(qString);
		String cut = qString.substring(qString.indexOf("CELLHOLDS") + 10, qString.lastIndexOf(']')-2);
		System.out.println(cut);
		String[] strings = cut.split("\\ \\),\\ \\(CELLHOLDS\\ ");
		double[] out = new double[strings.length];
		System.out.println(Arrays.toString(strings));
		String[] in = input.split("\\s");
		assert in.length == 36;
		
		for(int i = 0; i < strings.length; i++){
			// get row, col, val 
			String[] key = strings[i].split("\\s");
			// get index of string ref
			int refIndex = (Integer.parseInt( key[1] ) - 1) * 6 + (Integer.parseInt( key[2] ) - 1) + ((Integer.parseInt( key[0] ) -1 ) / 2) * 18 + ((Integer.parseInt( key[0] ) -1 ) % 2) * 3;
			String inp = in[refIndex];
			if(key[3].substring(0, 1).equalsIgnoreCase(inp)){
				out[i]=1;
			}else{
				out[i]=-1;
			}
		}
		System.out.println(Arrays.toString(out));
		return out;
	}
}
