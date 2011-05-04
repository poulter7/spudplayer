package shef.network;

import junit.framework.TestSuite;

public class TestSuiteJudgement extends TestSuite{
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite("Judgement tests for CIL2P shef.network");
		//$JUnit-BEGIN$
		suite.addTestSuite(JudgementTestsTTT.class);
		suite.addTestSuite(JudgementTestsConnect4.class);
		
		//$JUnit-END$
		return suite;
	}

}
