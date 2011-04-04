package shef.instantiator;

import java.util.List;

import cs227b.teamIago.resolver.Expression;
import cs227b.teamIago.resolver.Predicate;

/**
 * Provides a representation for next clauses in GDL
 * @author jonathan
 *
 */
public class NextClause {

    /** The {@link Predicate} which will be true next */
    private Predicate next;
	/** 
	 * List of {@link Expression} which must be 
	 * true for this clause to be valid 
	 * */
	private List<Expression> premises;
	
	/**
	 * Default constructor
	 * @param next
	 * @param premises
	 */
	public NextClause(Predicate next, List<Expression> premises) {
		this.next = next;
		this.premises = premises;
	}

	/**
	 * Return the possibly true predicate
	 */
	public Predicate getNext(){
		return next;
	}
	
	/**
     * 
	 * @return the premise which must be fulfilled 
	 * 
	 */
	public List<Expression> getPremises(){
		return premises;
	}
	
	public String toString(){
		return next + " : " + premises;
	}
}
