package network.correctness;

import static org.junit.Assert.assertArrayEquals;
import junit.framework.TestCase;

import org.junit.Test;

import shef.network.CIL2PFactory;
import shef.network.CIL2PManager;
import shef.network.CIL2PNet;

/**
 * Simple tests for the CIL2P_Manager ranging from simple to more complex
 * instantiation
 * 
 * @author jonathan
 * 
 */
public class CIL2PTest extends TestCase {
    CIL2PManager cil2p_manager;

    public void testStacked() {
        CIL2PNet cn = CIL2PFactory.fromFileLocation("tests/stack");
        cil2p_manager = new CIL2PManager(cn);
        double maxError = 1 - cil2p_manager.getMaxError();
        // TODO expand this test
        assertEquals(1d, cil2p_manager.getOutput(1, 1)[0], maxError);
        assertEquals(-1d, cil2p_manager.getOutput(-1, -1)[0], maxError);

    }

    public void testStackedAnd() {
        CIL2PNet network = CIL2PFactory.fromFileLocation("tests/stackand");
        cil2p_manager = new CIL2PManager(network);
        double maxError = 1 - cil2p_manager.getMaxError();
        // TODO expand this test

        double[] scores = cil2p_manager.getOutput(1, 1, -1, -1);
        assertEquals(1d, scores[0], maxError);
        scores = cil2p_manager.getOutput(-1, -1, -1, -1);
        cil2p_manager.printInputActivation();
        System.out.println();
        cil2p_manager.printOutputActivation();
        assertEquals(-1d, scores[0], maxError);

    }

    /**
     * <pre>
     * (<= (goal W 100)
     * 	(B ?x ?y)
     * 	(C ?x ?y))
     * 
     * (<= (B ?c ?d )
     * 	(true (cell ?c))
     * 	(true (door ?d)))
     * 	
     * (<= (C ?c ?d)
     * 	(true (cell ?c))
     * 	(true (door ?d)))
     * </pre>
     */
    public void testStackedOr() {
        CIL2PNet cn = CIL2PFactory.fromFileLocation("tests/stack_or");
        cil2p_manager = new CIL2PManager(cn);
        cil2p_manager.printOutputActivation();
        double maxError = 1 - cil2p_manager.getMaxError();
        // TODO expand this test
        assertEquals(1d, cil2p_manager.getOutput(1, -1, 1, -1)[0], maxError);
        assertEquals(-1d, cil2p_manager.getOutput(-1, -1, -1, -1)[0], maxError);

    }

    /**
     * Test that variable instantiation is correct accross multiple clauses
     */
    public void testVarNet() {
        CIL2PNet cn = CIL2PFactory.fromFileLocation("tests/vars");
        cil2p_manager = new CIL2PManager(cn);
        assertEquals(6, cil2p_manager.getPlayInfo()[0]);
        assertEquals(1, cil2p_manager.getPlayInfo()[1]);
    }

    /**
     * This should make sure the
     */
    public void testVarNet2() {
       CIL2PNet cn = CIL2PFactory.fromFileLocation("tests/vars2");
       cil2p_manager = new CIL2PManager(cn);
        assertEquals(10, cil2p_manager.getPlayInfo()[0]);
        assertEquals(1, cil2p_manager.getPlayInfo()[1]);
    }

    public void testSingleTruth() {
        CIL2PNet net = CIL2PFactory.fromFileLocation("tests/singletruth");
        cil2p_manager = new CIL2PManager(net);
        double maxError = 1 - cil2p_manager.getMaxError();
        assertEquals(-1d, cil2p_manager.getOutput(-1, -1, -1, -1)[0], maxError);
        assertEquals(1d, cil2p_manager.getOutput(1, 1, 1, 1)[0], maxError);

    }

    /**
     * Simple test
     * 
     * <pre>
     * A &lt; -b
     * </pre>
     */
    @Test
    public void testSimpleX() {
        CIL2PNet net = CIL2PFactory.fromFileLocation("tests/x");
        cil2p_manager = new CIL2PManager(net);
        double maxError = 1 - net.Amin;
        // correct layout
        assertArrayEquals(new int[] { 1, 1, 1 }, cil2p_manager.getInfo());
        // correct processing of in/out
        assertArrayEquals(new double[] { 1 }, cil2p_manager.getOutput(1),
                maxError);
        assertArrayEquals(new double[] { -1 }, cil2p_manager.getOutput(-1),
                maxError);
    }

    /**
     * Test AND
     * 
     * <pre>
     * A <- b c
     * </pre>
     */
    @Test
    public void testSimpleAnd() {
        CIL2PNet cn = CIL2PFactory.fromFileLocation("tests/and");
        cil2p_manager = new CIL2PManager(cn);
        double maxError = 1 - cil2p_manager.network.Amin;
        assertArrayEquals(new int[] { 2, 1, 1 }, cil2p_manager.getInfo());
        assertEquals(1, cil2p_manager.getOutput(1, 1)[0], maxError); // B C : 1
        assertEquals(-1, cil2p_manager.getOutput(1, -1)[0], maxError); // B¬C :
                                                                       // -1
        assertEquals(-1, cil2p_manager.getOutput(-1, 1)[0], maxError); // ¬B C :
                                                                       // -1
        assertEquals(-1, cil2p_manager.getOutput(-1, -1)[0], maxError); // ¬B¬C
                                                                        // : -1
    }

    /**
     * Test OR
     * 
     * <pre>
     * A <- b
     * 	 	A <- c
     * </pre>
     */
    @Test
    public void testOr() {
        CIL2PNet cn = CIL2PFactory.fromFileLocation("tests/or");
        cil2p_manager = new CIL2PManager(cn);
        double maxError = 1 - cil2p_manager.getMaxError();
        assertArrayEquals(new int[] { 2, 2, 1 }, cil2p_manager.getInfo());
        assertEquals(1, cil2p_manager.getOutput(1, 1)[0], maxError); // B C : 1
        assertEquals(1, cil2p_manager.getOutput(1, -1)[0], maxError); // B¬C : 1
        assertEquals(1, cil2p_manager.getOutput(-1, 1)[0], maxError); // ¬B C :
                                                                      // 1
        assertEquals(-1, cil2p_manager.getOutput(-1, -1)[0], maxError); // ¬B¬C
                                                                        // : -1
    }

    /**
     * Test NOT
     * 
     * <pre>
     * A &lt; -&tilde;b
     * </pre>
     */
    @Test
    public void testSimpleNot() {
        CIL2PNet cn = CIL2PFactory.fromFileLocation("tests/not");
        cil2p_manager = new CIL2PManager(cn);
        double maxError = 1 - cil2p_manager.getMaxError();
        // correct layout
        assertArrayEquals(new int[] { 1, 1, 1 }, cil2p_manager.getInfo());
        // correct processing of in/out
        assertArrayEquals(new double[] { -1 }, cil2p_manager.getOutput(1),
                maxError);
        assertArrayEquals(new double[] { 1 }, cil2p_manager.getOutput(-1),
                maxError);
    }

    /**
     * 
     * <pre>
     * A <- Q c ~d
     * 	 	A <- e f
     * 	 	Q <- b
     * </pre>
     */
    @Test
    public void testNSLS() {
        CIL2PNet cn = CIL2PFactory.fromFileLocation("tests/nsls");
        cil2p_manager = new CIL2PManager(cn);
        double maxError = 1 - cil2p_manager.getMaxError();

        assertArrayEquals(new int[] { 6, 3, 2 }, cil2p_manager.getInfo());
        assertEquals(-1, cil2p_manager.getOutput(-1, -1, -1, -1, -1)[0],
                maxError); // ¬B¬C¬D ¬E¬F
        assertEquals(-1, cil2p_manager.getOutput(1, -1, -1, -1, -1)[0],
                maxError); // B¬C¬D ¬E¬F
        assertEquals(1, cil2p_manager.getOutput(1, 1, -1, -1, -1)[0], maxError); // B
                                                                                 // C¬D
                                                                                 // ¬E¬F
        assertEquals(-1, cil2p_manager.getOutput(1, 1, 1, -1, -1)[0], maxError); // B
                                                                                 // C
                                                                                 // D
                                                                                 // ¬E¬F
        assertEquals(-1, cil2p_manager.getOutput(-1, 1, -1, -1, -1)[0],
                maxError); // ¬B C¬D ¬E¬F
        assertEquals(-1, cil2p_manager.getOutput(-1, 1, -1, -1, -1)[0],
                maxError); // ¬B¬C D ¬E¬F
    }

    /**
     * Testing that the shef.network can handle correctly deep trees
     * 
     * <pre>
     * G <- a P
     * 		P <- b
     * 		P <- c
     *</pre>
     */
    @Test
    public void testDeepInstance() {
        CIL2PNet cn = CIL2PFactory.fromFileLocation("tests/deepTest");
        cil2p_manager = new CIL2PManager(cn);
        double maxError = 1 - cil2p_manager.getMaxError();

        assertArrayEquals(new int[] { 4, 3, 2 }, cil2p_manager.getInfo());
        assertEquals(1, cil2p_manager.getOutput(1, 1, 1)[0], maxError); // A B C
                                                                        // -> 1
        assertEquals(-1, cil2p_manager.getOutput(-1, 1, 1)[0], maxError); // ¬A
                                                                          // B C
                                                                          // ->
                                                                          // -1
        assertEquals(-1, cil2p_manager.getOutput(-1, -1, -1)[0], maxError); // ¬A¬B¬C
                                                                            // ->
                                                                            // -1
        assertEquals(1, cil2p_manager.getOutput(1, 1, -1)[0], maxError); // A
                                                                         // B¬C
                                                                         // -> 1
        assertEquals(1, cil2p_manager.getOutput(1, -1, 1)[0], maxError); // A¬B
                                                                         // C ->
                                                                         // 1
        assertEquals(-1, cil2p_manager.getOutput(1, -1, -1)[0], maxError); // A¬B¬C
                                                                           // ->
                                                                           // -1
    }
}
