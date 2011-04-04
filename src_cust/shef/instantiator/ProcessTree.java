package shef.instantiator;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import shef.instantiator.andortree.Node;
import shef.instantiator.andortree.NodeType;

import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Expression;
import cs227b.teamIago.resolver.Implication;
import cs227b.teamIago.resolver.NotOp;
import cs227b.teamIago.resolver.OrOp;
import cs227b.teamIago.resolver.Predicate;
import cs227b.teamIago.resolver.Substitution;
import cs227b.teamIago.resolver.Term;

public class ProcessTree  {

	private static final Atom TRUTH = new Atom("true");
	private static final Substitution blankSub = new Substitution();
	
	protected List<Node> varCausingNodes = new ArrayList<Node>();
	
	private List<Node> toProcess;
	private Instantiator ins;
	
	public ProcessTree(List<Node> toProcess, Instantiator ins) {
		this.toProcess = toProcess;
		this.ins = ins;
	}

	public void run() {
		// Construct the basic tree
		while (!toProcess.isEmpty()) {
			// get the first in the tree and remove it from the to process list
			Node n = toProcess.get(0);
			toProcess.remove(0);
			List<Node> re = instantiateOneLevel(n);
			toProcess.addAll(re);
		}
	}

	/**
	 * Build this node one level down
	 * 
	 * @param processNode
	 *            node to build from
	 * @return every new node which should be searched
	 */
	@SuppressWarnings("unchecked")
	protected List<Node> instantiateOneLevel(Node processNode) {
		LinkedList<Node> list = new LinkedList<Node>();
		Expression originalExpression = processNode.getHead();
		// return everything which would match this
		ExpList candidates = ins.theory.getCandidates(originalExpression);
		// should only appear for atoms
		if (candidates.size() == 0) {
			if (originalExpression.firstOp().equals(TRUTH)) {
				processNode.becomeTruth();
			}
		} else {
			if (candidates.size() > 0) {
				// pre filter the list to predicates and implications
				List<Expression> candidateList = candidates.toArrayList();
				List<Implication> candidateListImps = new ArrayList<Implication>();
				List<Predicate> candidateListPreds = new ArrayList<Predicate>();

				for (Expression e : candidateList) {
					if (e instanceof Implication) {
						candidateListImps.add((Implication) e);
					} else if (e instanceof Predicate) {
						candidateListPreds.add((Predicate) e);
					}
				}

				candidateListPreds = mapPredicates(originalExpression, candidateListPreds);
				candidateListImps = mapExpressions(originalExpression, candidateListImps);

				// add them to the parent node
				list.addAll(processPredicates(processNode, candidateListPreds));
				list.addAll(processImplications(processNode, candidateListImps));

			}
		}
		return list;
	}

	/**
	 * Add a predicates which match the node
	 * 
	 * @param parent
	 * @param candidatePredicates
	 * @return
	 */
	private List<Node> processPredicates(Node parent, List<Predicate> candidatePredicates) {
		LinkedList<Node> newAnds = new LinkedList<Node>();
		// process Predicates by adding the predicate OR and AND
		NodeType baseType = parent.nodetype;
		for (Predicate instPredicate : candidatePredicates) {
			Implication impP = new Implication(instPredicate, new ExpList());
			// Node newOrNode = new Node(impP, NodeType.OR);
			Node newOrNode = ins.orMemory.get(impP);
			if (newOrNode == null) {
				newOrNode = new Node(impP, NodeType.OR);
				ins.orMemory.put(impP, newOrNode);

				parent.addChild(newOrNode);

				Node newAndNode = ins.andMemory.get(impP);

				if (newAndNode == null) {
					newAndNode = new Node(impP, baseType);
				}
				newOrNode.addChild(newAndNode);
				recordVarCausing(newOrNode);
			} else {
				parent.addChild(newOrNode);
			}
		}
		return newAnds;
	}

	/**
	 * Add implications which match the node
	 * 
	 * @param parent
	 * @param candidateListImps
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Node> processImplications(Node parent, List<Implication> candidateListImps) {
		LinkedList<Node> newAnds = new LinkedList<Node>();
		for (Implication instImplication : candidateListImps) {

			// Node newOrNode = new Node(instImplication, NodeType.OR);
			Node newOrNode = ins.orMemory.get(instImplication);
			if (newOrNode == null) {
				newOrNode = new Node(instImplication, NodeType.OR);
				ins.orMemory.put(instImplication, newOrNode);
				// if the implication doesn't already exist make it and fill it
				// out
				parent.addChild(newOrNode);
				for (Expression cons : (ArrayList<Expression>) instImplication.getPremises().toArrayList()) {

					Implication newImpl = new Implication(cons, new ExpList());
					Node newAndNode = ins.andMemory.get(newImpl);

					if (newAndNode == null) {
						newAndNode = new Node(newImpl, NodeType.AND);
						if (cons instanceof Predicate) {
							Predicate pred = (Predicate) cons;
							Term firstOp = pred.firstOp();
							if (firstOp.equals(TRUTH)) {
								newAndNode.becomeTruth();
							}
							
						} else if (cons instanceof NotOp) {
							// instantiate a not
							newAndNode.negated = true;
							newAndNode.setImplication(new Implication((((NotOp) cons).getOperands().get(0)), instImplication.getPremises()));
						} else if (cons.getClass().equals(OrOp.class)) {
							newAndNode.nodetype = NodeType.DISTINCT;
							// XXX have to do the equals OrOp.class strange library
							// incompatibility between expression and OrOp
							OrOp or = (OrOp) cons;
							List<Predicate> lP = (ArrayList<Predicate>)or.getOperands().toArrayList();
							for (Predicate p : lP) {
								Implication iP = new Implication(p, new ExpList());
								Node orOr = new Node(iP, NodeType.OR);
								Node orAnd = new Node(iP, NodeType.AND);
								newAndNode.addChild(orOr);
								orOr.addChild(orAnd);
								newAnds.add(orAnd);
								
							}
						}
						
						newAnds.add(newAndNode);
					}
					newOrNode.addChild(newAndNode);
				}
				recordVarCausing(newOrNode);
			} else {
				parent.addChild(newOrNode);
			}
		}

		return newAnds;
	}

	private boolean recordVarCausing(Node goalNode) {
		List<Node> children = goalNode.getChildren();
		if (goalNode.getHead().getVars().size() > 0) {
			varCausingNodes.add(goalNode);
			return true;
		}
		for (Node child : children) {
			if (child.getHead().getVars().size() > 0) {
				varCausingNodes.add(goalNode);
				return true;
			}
		}
		return false;
	}

	/**
	 * Take the implications which possible instantiate this predicate and map
	 * the variables to the implication which generated them
	 * 
	 * @param oldExp
	 * @param newUninstExps
	 * @return
	 */
	protected List<Implication> mapExpressions(Expression oldExp, List<Implication> newUninstExps) {
		List<Implication> newInstImps = new ArrayList<Implication>();
		final Substitution freshSub = new Substitution();
		for (Implication newUninstExp : newUninstExps) {
			Expression uninstantiatedExp = newUninstExp.getConsequence();

			Substitution sMgu = uninstantiatedExp.mgu(oldExp, freshSub, ins.theory);
			if (sMgu != null) {

				// nothing which doesn't unify should be here
				// worst is they match so can map and apply
				Implication newinstExp = (Implication) newUninstExp.apply(sMgu);

				/*
				 * should either be equal now OR newinstExp will be a refinement
				 * of oldExp (A ?x 1) (A 1 ?y) -> (A 1 1) (A ?x 1) (A ?y 1) ->
				 * (A ?y 1) there may be variables left over MAP THEM
				 */
				Substitution sMap = uninstantiatedExp.mapTo(freshSub, oldExp);
				newinstExp = (Implication) newUninstExp.apply(sMap);
				newInstImps.add(newinstExp);
			}
		}
		return newInstImps;
	}

	protected List<Predicate> mapPredicates(Expression oldExpression, List<Predicate> newPreds) {
		List<Predicate> newInstImps = new ArrayList<Predicate>();

		for (Predicate newUninstExp : newPreds) {
			Substitution sMgu = newUninstExp.mgu(oldExpression, blankSub, ins.theory);
			if (sMgu != null) {
				Predicate newinstExp = (Predicate) newUninstExp.apply(sMgu);
				newInstImps.add(newinstExp);
			}
		}
		return newInstImps;
	}

}
