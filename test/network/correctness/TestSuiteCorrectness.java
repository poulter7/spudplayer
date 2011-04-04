package network.correctness;

import junit.framework.TestSuite;

public class TestSuiteCorrectness extends TestSuite{

	public static TestSuite suite() {
		TestSuite suite = new TestSuite("Correctness tests for CIL2P shef.network");
		//$JUnit-BEGIN$
		suite.addTestSuite(CIL2PTest.class);
		suite.addTestSuite(CIL2PTicTacToeTests.class);
		suite.addTestSuite(CIL2PConnect4Tests.class);
		
		//$JUnit-END$
		return suite;
	}

}
