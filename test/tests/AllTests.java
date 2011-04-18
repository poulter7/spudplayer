package tests;

import shef.instantiator.andortree.NodeDepthTests;
import shef.network.BipolarTest;
import shef.network.JudgementTestsTTT;
import shef.network.TestSuiteCorrectness;
import shef.network.TestSuiteJudgement;
import junit.framework.Test;
import junit.framework.TestSuite;

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
