package shef.instantiator;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;

import shef.instantiator.andortree.Node;
import shef.instantiator.andortree.NodeDepthComparator;
import shef.instantiator.andortree.NodeType;

import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Expression;
import cs227b.teamIago.resolver.Implication;
import cs227b.teamIago.resolver.Predicate;
import cs227b.teamIago.resolver.Substitution;
import cs227b.teamIago.resolver.Theory;
import cs227b.teamIago.resolver.Variable;

/*
 * 
 *  ~ ~  __|_\_
 * ~ ~  =]_.-.__D>
 *  ~ ~    /_/
 *
 */
/**
 * Manages building an AND/OR tree which represents a GDL file
 * 
 * @author jonathan
 * 
 */
public class Instantiator {
    
    // debug constants
    /** Will print refinement steps  */
    private static final boolean VIEW_TREE_STEPS = false;
	// atom constants
	private static final Atom TRUTH = new Atom("true");
	private static final Atom goal = new Atom("goal");
	private static final Atom next = new Atom("next");
	private static final Atom init = new Atom("init");
	private static final Substitution s = new Substitution();

	/** For hashes for {@link Node} memoization */ 
	protected Hashtable<Expression, Node> orMemory = new Hashtable<Expression, Node>();
	/** For hashes for {@link Node} memoization */
	protected Hashtable<Expression, Node> andMemory = new Hashtable<Expression, Node>();
	protected Theory theory;

	private Queue<Node> varCausingNodes = new PriorityQueue<Node>(1, new NodeDepthComparator());
	private Hashtable<Expression, Node> goalHash = new Hashtable<Expression, Node>();

	@SuppressWarnings("unchecked")
	public Instantiator(Theory t, Properties p) {

		this.theory = t;
		// init facts
		List<Expression> initFacts = theory.getCandidates(init).toArrayList();
		for (Expression fact : initFacts) {
			if (fact instanceof Implication) {
				Implication initFact = (Implication) fact;
				Predicate conseqFact = (Predicate) initFact.getConsequence();
				Implication iFact = new Implication(conseqFact.getOperands().get(0), initFact.getPremises());
				theory.add(iFact);
			} else if (fact instanceof Predicate) {
				Predicate pFact = (Predicate) fact;
				theory.add(pFact.getOperands());
			}
		}
		theory.buildVolatile();

	}

	public Instantiator(Theory theory2) {
		this(theory2, new Properties());
	}

	public List<Node> getProofTrees(int depth) {
		// fill state space
		System.out.println("adding extra info");
		addExtraInfo();

		// create the initial goal trees
		// this creates the correct structure
		System.out.println("creating goal trees");
		createGoalTrees(depth);

		// instantiate variables
		System.out.println("filling in variables");
		fillVariables();

		System.out.println("clean up the tree");
		cleanTrees();

		return new ArrayList<Node>(goalHash.values());
	}

	/**
	 * remove any unnecessary clauses - can prune anything which isn't part
	 */
	private void cleanTrees() {
		for (Node goalNode : goalHash.values()) {
			Node.prune(goalNode);
		}
	}

	@SuppressWarnings("unchecked")
	private void addExtraInfo() {
		List<NextClause> toConsiderClauses = getNextClauses(theory);

		Set<Expression> expsToAdd = new HashSet<Expression>();

		int last_c = 0;
		int newC = 0;
		do {
			last_c = newC;
			for (NextClause cl : toConsiderClauses) {
				List<Predicate> insts = new ArrayList<Predicate>();
				Predicate next = cl.getNext();
				insts.add(next);

				for (Expression premise : cl.getPremises()) {
					if (premise.firstOp().equals(TRUTH))
						premise = ((Predicate) premise).getOperands().get(0);

					List<Expression> results = (List<Expression>) theory.getCandidates(premise).toArrayList();
					List<Predicate> newPs = new ArrayList<Predicate>();
					boolean replaceList = false;
					for (Expression e : results) {
						Substitution nS = premise.mapTo(s, e);

						if (nS != null) {
							boolean varInstantiate = false;
							for (Variable v : (ArrayList<Variable>) insts.get(0).getVars().toArrayList()) {
								if (nS.assigns(v)) {
									varInstantiate = true;
									replaceList = true;
									break;
								}
							}

							if (varInstantiate) {
								for (Predicate pr : insts) {
									newPs.add((Predicate) pr.apply(nS));
								}
							}
						}
					}
					if (replaceList)
						insts = newPs;

					// add non-duplicate completed predicates to the theory
					for (Predicate p : insts) {
						if (p.getVars().size() == 0) {
							boolean already = expsToAdd.add(p);
							if (!already)
								theory.add(p);
						}
					}
				}
			}
			// check at least something has been added
			newC = expsToAdd.size();
		} while (last_c != newC);
	}

	@SuppressWarnings("unchecked")
	/**
	 * Returns a list clauses representing (NEXT (X)) statements
	 */
	private List<NextClause> getNextClauses(Theory theory) {
		List<Expression> facts = (ArrayList<Expression>) theory.getCandidates(theory.generateVar()).toArrayList();
		List<NextClause> nextClauses = new ArrayList<NextClause>();
		// find the (NEXT (X) ) statements
		for (Expression fact : facts) {
			if (fact instanceof Implication) {
				Implication impFact = (Implication) fact;
				Expression con = impFact.getConsequence();
				if (con instanceof Predicate && con.firstOp().equals(next)) {
					List<Expression> prems = impFact.getPremises().toArrayList();
					NextClause cl = new NextClause((Predicate) ((Predicate) con).getOperands().get(0), prems);
					nextClauses.add(cl);
				}

			}
		}
		return nextClauses;
	}

	/**
	 * Create the AND/OR trees which represent the game these will be and or
	 * structures created by fulfilling goal states
	 * 
	 * @param depth
	 *            how deep the tree will go
	 */
	@SuppressWarnings("unchecked")
	private Hashtable<Expression, Node> createGoalTrees(int depth) {
		goalHash = new Hashtable<Expression, Node>();
		// goal predicate
		Variable vRole = theory.generateVar();
		Variable vScore = theory.generateVar();
		Predicate goalPred = new Predicate(goal, new ExpList(new Expression[] { vRole, vScore }));
		ExpList goalImps = theory.getCandidates(goalPred);
		ExpList roles = theory.getCandidates(new Predicate("role", new Expression[] { theory.generateVar() }));

		for (Implication goalImp : (ArrayList<Implication>) goalImps.toArrayList()) {

			List<Node> toProcess = new LinkedList<Node>();

			ExpList goalOps = ((Predicate) (goalImp.getConsequence())).getOperands();
			if (goalOps.get(0) instanceof Variable) {

				// hack to give goals all the same variable values
				for (Expression role : (ArrayList<Expression>) roles.toArrayList()) {
					Implication goalInstImp = new Implication(new Predicate("goal", new ExpList(new Expression[] { goalPred.secondOp(), goalOps.get(1) })), goalImp.getPremises());

					Substitution s = new Substitution();
					s.addAssoc((Variable) goalOps.get(0), goalPred.secondOp());
					s.addAssoc((Variable) goalPred.secondOp(), role.secondOp());
					// apply twice
					goalInstImp = (Implication) goalInstImp.apply(s);
					goalInstImp = (Implication) goalInstImp.apply(s);

					addGoalTree(goalInstImp, toProcess);
				}
			} else {
				addGoalTree(goalImp, toProcess);
			}

			ProcessTree p = new ProcessTree(toProcess, this);
			p.run();
			varCausingNodes.addAll(p.varCausingNodes);
		}
		return goalHash;
	}

	private void addGoalTree(Implication goalInstImp, List<Node> toProcess) {
		Node goalNode = goalHash.get(goalInstImp.getConsequence());
		if (goalNode == null) {
			goalNode = new Node(goalInstImp, NodeType.ROOT);
			goalHash.put(goalInstImp.getConsequence(), goalNode);
			toProcess.add(goalNode);
		}
	}

	/**
	 * Should be a list of OR or VAR_OR nodes
	 * 
	 * @param varCausingNodes
	 */
	private void fillVariables() {
		while (!varCausingNodes.isEmpty()) {
		    Node node;
		    if(VIEW_TREE_STEPS){
		        node = varCausingNodes.peek();
		        System.out.println("ch: "+ node);
		        System.out.println("fr: "+ varCausingNodes);
		        System.out.println(new
		                ArrayList<Node>(goalHash.values()).get(0).printTree());
		        varCausingNodes.poll();
		        
		    } else {
		        node = varCausingNodes.poll();
		    }


			assert node.nodetype == NodeType.OR || node.nodetype == NodeType.VAR_OR;

			List<Node> children = node.getChildren();

			// select a node which has all of its children completed
			// pass these instantiations up
			Node completed = null;
			for (Node n : children) {
				// instantiate if it can be else it may need folding up
				if ((n.getNodeType() == NodeType.TRUTH || n.getNodeType() == NodeType.AND) && n.getHead().getVars().size() > 0 && n.isCompleted() && n.getChildCount() > 0) {
					node.resetChildren();
					completed = n;

					break;
				}
			}

			/*
			 * Replace a node which has no free variables in its children by an
			 * instantiation for each of those possibilities as described in
			 */
			if (completed != null) {
				Implication varImp = node.getImplication();
				Node varAnd = new Node(varImp, NodeType.VAR_AND);
				if (completed.negated) {
					System.err.println("neg node somewhere");
				}
				node.addChild(varAnd);
				List<Node> completedChildren = completed.getChildren();
				// TODO check there isn't anything in the facts which fulfils
				for (Node compChild : completedChildren) {
					Node varOr = new Node(varImp, NodeType.VAR_OR);
					varAnd.addChild(varOr);
					Substitution s = new Substitution();
					s = completed.getImplication().getConsequence().mgu(compChild.getHead(), s, theory);
					if (s != null) {
						for (Node child : children) {
							Node newAnd = child.clone(s);
							varOr.addChild(newAnd);
						}
					}
//					else {
//					    throw new RuntimeErrorException(null, "Fail mapping");
//					}
//					if (varOr.getChildCount() == 0) {
//						varAnd.removeChild(varOr);
//					}
					// replace these new VAR_OR nodes with fully instantiated
					// nodes by mapping to the consequent
					// shouldn't remove any children here as there may be truths
					if (varOr.isCompleted()) {
						ExpList varOrPremises = varOr.getImplication().getPremises();
						List<Node> varOrChildren = varOr.getChildren();
						ExpList varOrExpList = new ExpList();
						for (Node varOrChild : varOrChildren) {
							// if its a truth have to build the truth back in
							if (varOrChild.getNodeType() == NodeType.TRUTH) {
								varOrExpList.add(new Predicate("true", new ExpList(new Expression[] { varOrChild.getHead() })));
							} else {
								varOrExpList.add(varOrChild.getHead());
							}
						}
						Substitution sub = new Substitution();
						sub = varOrPremises.mgu(varOrExpList, sub, theory);
						// now have a definite instantiation for the VAR_OR
						varOr.setImplication((Implication) varOr.getImplication().apply(sub));
					}
				}
				varCausingNodes.add(node);
			} else {
				// if there is nothing which has free variables then everything
				// below here must be instantiated so flatten it
				Node parent = node.getParent();
				if (node.getChildren().size() > 0) {
					Node firstChild = node.getChildren().get(0);

					// if the first child is a VAR_AND then can collapse all
					// VAR_ORs
					if (firstChild.getNodeType() == NodeType.VAR_AND) {
						List<Node> subs = firstChild.getChildren();
						parent.removeChild(node);
						for (Node n : subs) {
							n.nodetype = NodeType.OR;
							parent.addChild(n);
							boolean add = !n.isCompleted() || n.getHead().getVars().size() > 0;
							if (add) {
								varCausingNodes.add(n);
							}
						}
					}
				}
			}

			// System.out.println("nfr:" + varCausingNodes);
			// System.out.println("-------");
		}
	}
}
