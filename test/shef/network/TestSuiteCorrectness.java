package shef.network;

import junit.framework.TestSuite;

public class TestSuiteCorrectness extends TestSuite{

	public static TestSuite suite() {
		TestSuite suite = new TestSuite("Correctness tests for CIL2P shef.network");
		//$JUnit-BEGIN$
		suite.addTestSuite(CorrectnessTestsGeneral.class);
		suite.addTestSuite(CorrectnessTestsTTT.class);
		suite.addTestSuite(CorrectnessTestsConnect4.class);
		
		//$JUnit-END$
		return suite;
	}

}
