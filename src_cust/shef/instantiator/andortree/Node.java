package shef.instantiator.andortree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import cs227b.teamIago.resolver.Expression;
import cs227b.teamIago.resolver.Implication;
import cs227b.teamIago.resolver.Predicate;
import cs227b.teamIago.resolver.Substitution;

/**
 * A node in an AND OR tree
 * 
 * @author jonathan
 * 
 */
public class Node implements Cloneable {
	/** The implication this node represents */
	private Implication implication;

	/** Is this node negated in the AND/OR tree? */
	public boolean negated = false;

	/** The parent of this node */
	private Node parent;

	/** The depth of this node */
	private int depth;

	/** Children of the node */
	private List<Node> children = new ArrayList<Node>();

	/** The type of node */
	public NodeType nodetype;

	/** The expression which this node really fulfills */
	private Expression head;

	/**
	 * Create a new Node
	 * 
	 * @param imp
	 *            the implication to represent
	 * @param nodeType
	 *            the node type
	 */
	public Node(Implication imp, NodeType nodeType) {
		this.setImplication(imp);
		this.nodetype = nodeType;
	}

	/**
	 * Add a node to this node's children list update the child's parent and
	 * depth
	 */
	public void addChild(Node child) {
		child.parent = this;
		child.depth = this.depth + 1;
		children.add(child);
	}

	public Node clone() {
		Node n = new Node(this.getImplication(), this.getNodeType());
		List<Node> cs = n.getChildren();
		for (Node child : this.getChildren()) {
			cs.add(child.clone());
		}
		return n;
	}

	/**
	 * Creates a clone of the node, not including children which fail to map to
	 * the substitution
	 * 
	 * @param subs
	 * @return the node with sub applied
	 */
	public Node clone(Substitution subs) {
		// first apply the substitution
		Implication newImp = (Implication) this.getImplication().apply(subs);

		// create the new node which is going to be the container
		Node n = new Node(newImp, this.getNodeType());
		n.children = this.children;

//		// manual copy - removed for being SLOW
//		Expression newConseq = newImp.getConsequence();
//		List<Node> cs = n.getChildren();
//		for (Node child : this.getChildren()) {
//			// fill the node - but don't include the clauses which wouldn't map
//			Expression childConseq = child.getImplication().getConsequence();
//			if ((newConseq.mapTo(s, childConseq) != null))
//				cs.add(child.clone());
//		}
		return n;

	}

	public List<Node> getChildren() {
		return children;

	}

	/**
	 * Return how deep in the tree is this XXX this may return the wrong value
	 * don't rely on this
	 * 
	 * @return the best guess of depth
	 */
	public int getDepth() {
		return depth;
	}

	public Expression getHead() {
		return head;
	}

	public Implication getImplication() {
		return implication;
	}

	public int getChildCount() {
		return children.size();
	}

	/**
	 * Return the number of negative children this node has
	 */
	public int getNegChildCount() {
		return getChildCount() - getPosChildCount();
	}

	/**
	 * Return the node type
	 */
	public NodeType getNodeType() {
		return nodetype;
	}

	/**
	 * Returns the number of positive children this node has
	 * 
	 */
	public int getPosChildCount() {
		int p = 0;
		for (Node n : children) {
			if (!n.negated) {
				p++;
			}
		}
		return p;
	}

	/**
	 * Returns the parent node
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * Returns true if the head of every child has no variables
	 * 
	 */
	public boolean isCompleted() {
		// should only really be called on TRUE and AND nodes
		for (Node child : getChildren()) {
			// the node has some variables in it
			if (child.getHead().getVars().size() > 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Prune a node remove all the non-completed children
	 * 
	 * XXX this should be rendered obsolete by the introduction of actual ground
	 * instantiation
	 * 
	 * @param node
	 * @return true if the node is or contains 
	 */
	public static boolean prune(Node node) {
		if(node.nodetype == NodeType.DISTINCT){
			throw new RuntimeException("not sure how to prune a DISTINCT node");
		}
		
	    // this is a truth node can wipe its children
		if(node.nodetype == NodeType.TRUTH){
			node.children = new ArrayList<Node>();
			return true;
		}
		boolean hasTruthInChildren = false;
		Iterator<Node> it = node.children.iterator();
		while(it.hasNext()){
			Node n = it.next();
			if(!prune(n)){
				it.remove();
			} else {
				hasTruthInChildren = true;
			}
		}
		return hasTruthInChildren;
	
	}

	/**
	 * Output util - prints a node and calls chidren to be printed
	 * 
	 * @param lvl
	 * @return
	 */
	private String printNode(int lvl) {
		StringBuffer s = new StringBuffer();
		s.append(printSpacer(lvl) + " " + toString() + "\n");

		Iterator<Node> it = children.iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			s.append(node.printNode(lvl + 1));
		}
		return s.toString();
	}

	/**
	 * Output util - spaces printing
	 * 
	 * @param lvl
	 * @return
	 */
	private String printSpacer(int lvl) {
		StringBuffer sb = new StringBuffer(lvl);

		for (int j = 0; j < lvl; j++) {
			sb.append("    ");
		}
		return sb.toString();
	}

	/**
	 * Print the node as the root of a tree
	 */
	public String printTree() {
		return printNode(0);
	}

	/**
	 * Removes the supplied node from the list of children
	 * 
	 * @param toRemove
	 * @return true if list DID contain the node
	 */
	public boolean removeChild(Node toRemove) {
		return children.remove(toRemove);
	}

	/**
	 * Assigns the children of this node to a new ArrayList Note: the old list
	 * is left
	 */
	public void resetChildren() {
		children = new ArrayList<Node>();
	}

	/**
	 * Set a new implication for this node to represent
	 * 
	 * @param implication
	 */
	public void setImplication(Implication implication) {
		// XXX should this even be allowed?!
		this.implication = implication;
		this.head = implication.getConsequence();
	}

	public String toString() {
		return this.nodetype + (negated ? " not" : "") + " " + getHead();
	}
	
	
	/**
	 * Convert the implication of this node to the form <code>( TRUE (oldHead) )</code>
	 */
	public void becomeTruth() {
		// could be an atomic OR a predicate truth
		Expression truth = ((Predicate) this.getHead()).getOperands().get(0);
		
		this.setImplication(new Implication(truth, this.getImplication().getPremises()));
		this.nodetype = NodeType.TRUTH;

	}

	/**
	 * Add multiple child nodes to this
	 * @param children
	 */
	public void addChildren(Collection<Node> children) {
		for (Node n : children) {
			this.addChild(n);
		}

	}
}
