package shef.instantiator.andortree;

import java.util.Comparator;
/**
* 
* @author jonathan
*
*/
public class NodeDepthComparator implements Comparator<Node>{

		@Override
		public int compare(Node o1, Node o2) {
			// TODO Auto-generated method stub
			if (o1.getDepth() > o2.getDepth())
				return -1;
			else if (o1.getDepth() == o2.getDepth()){
				if(o1.nodetype == NodeType.TRUTH)
					return -1;
				else
					return 0;
			} else {
				return 1;
			}
		}
		
}
