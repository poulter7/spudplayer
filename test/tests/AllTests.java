package tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import network.BipolarTest;
import network.CIL2PJudgementTicTacToeTests;
import network.TestSuiteJudgement;
import network.correctness.TestSuiteCorrectness;
import dataStructures.NodeDepthTests;

/**
 * main test class.
 * @author jonathan
 *
 */
public class AllTests extends TestSuite{

	public static Test suite() {
		TestSuite suite = new TestSuite("All tests");
		//$JUnit-BEGIN$
		suite.addTest(TestSuiteCorrectness.suite());
		suite.addTest(TestSuiteJudgement.suite());
		suite.addTestSuite(BipolarTest.class);
		suite.addTestSuite(NodeDepthTests.class);
		//$JUnit-END$
		return suite;
	}
}
