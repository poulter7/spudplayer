package shef.instantiator.andortree;

public enum NodeType {
	AND, QUERY, VAR_AND, Fact, OR, UNDEF, ROOT, DOR, DAND, VAR_OR, TRUTH, DISTINCT;

//	public String toString() {
//		switch (this) {
//		case AND: 	return "AND";
//		case OR:	return "OR ";
//		case ROOT:	return "GOAL";
//		case QUERY:	return "TRUE";
//		case DOR:	return "DOR";
//		case DAND:	return "DAND";
//
//		default:
//			return "X";
//		}
//	}
}
