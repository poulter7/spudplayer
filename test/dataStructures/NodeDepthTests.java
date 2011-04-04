package dataStructures;

import junit.framework.TestCase;

import org.junit.Test;

import shef.instantiator.andortree.Node;
import shef.instantiator.andortree.NodeType;

import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Implication;

public class NodeDepthTests extends TestCase{

	@Test
	public void testAddChild(){
		Node n = new Node(new Implication(new Atom("t"), new ExpList()), NodeType.AND);
		Node n1 = new Node(new Implication(new Atom("t"), new ExpList()), NodeType.AND);
		n.addChild(n1);
		assertEquals("depth increased", n.getDepth() + 1, n1.getDepth());
		assertTrue("contained in children", n.getChildren().contains(n1));
	}
	
	@Test
	public void testGetDepth() {
		// two unassigned nodes have the same depth
		Node n = new Node(new Implication(new Atom("t"), new ExpList()), NodeType.AND);
		Node n1 = new Node(new Implication(new Atom("t"), new ExpList()), NodeType.AND);
		assertEquals("same depth", n.getDepth(), n1.getDepth());
	}

}
