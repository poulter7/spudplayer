package shef.network;

import shef.network.CIL2PManager;
import junit.framework.TestCase;

public abstract class AbstractCorrectnessTests extends TestCase{
	protected CIL2PManager cil2p_manager;

	public abstract void testTopology();
}
