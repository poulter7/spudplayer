package network;

import junit.framework.TestSuite;

public class TestSuiteJudgement extends TestSuite{
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite("Judgement tests for CIL2P shef.network");
		//$JUnit-BEGIN$
		suite.addTestSuite(CIL2PJudgementTicTacToeTests.class);
		suite.addTestSuite(CIL2PJudgementConnect4Tests.class);
		
		//$JUnit-END$
		return suite;
	}

}
