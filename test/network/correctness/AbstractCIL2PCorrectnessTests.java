package network.correctness;

import shef.network.CIL2PManager;
import junit.framework.TestCase;

public abstract class AbstractCIL2PCorrectnessTests extends TestCase{
	protected CIL2PManager cil2p_manager;

	public abstract void testTopology();
}
