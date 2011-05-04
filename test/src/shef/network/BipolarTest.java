package shef.network;

import junit.framework.TestCase;

import org.junit.Test;

import shef.network.Bipolar;

/**
 * Loosely tests the behaviour of the Bipolar activation function<br>
 * <em>n.b.</em> this is really just a sanity check 
 * @author jonathan
 *
 */
public class BipolarTest extends TestCase{
	Bipolar b = new Bipolar(1);
	
	@Test public void testOrigin() {
		assertEquals(0, b.getOutput(0), 0.0);
	}
	@Test
	public void testUpperLimit() {
		assertEquals(1, b.getOutput(10), 0.0005);
	}
	@Test
	public void testLowerLimit() {
		assertEquals(-1, b.getOutput(-10), 0.0005);
	}

}
