package shef.strategies.uct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import shef.network.CIL2PFactory;
import shef.network.CIL2PManager;
import shef.network.CIL2PNet;
import util.statemachine.MachineState;
import util.statemachine.Move;
import util.statemachine.exceptions.GoalDefinitionException;
import util.statemachine.exceptions.MoveDefinitionException;
import util.statemachine.exceptions.TransitionDefinitionException;

/**
 * 
 * 
 * @author jonathan poulter
 */
public class UCTNeuralStrategy extends UCTGamer {

	int MAX_ROLLDEPTH = 5;
	private CIL2PManager cil2pManager;

	/**
	 * Setup the UCT game tree
	 */
	@Override
	public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		long start = System.currentTimeMillis();
		CIL2PNet net = CIL2PFactory.modeNetFromGame(getMatch().getGame());
		cil2pManager = new CIL2PManager(net);
		long duration = System.currentTimeMillis() - start;

		super.stateMachineMetaGame(duration);
	}

	protected List<Double> completeRollout(MachineState from, int fromLvl) {
		int simDepth = fromLvl;
		int levelPlayer = (simDepth % roleCount);
		//        
		MachineState current = from;
		do { // play the best move for the current player
			try {
				
				List<MachineState> nextStats = theMachine.getNextStates(current);
				float bestChildScore = 0;
				int bestChildIndex = 0;
				int nextStateCount = nextStats.size();
				for (int i = 0; i < nextStateCount; i++) {
					List<Double> svs = cil2pManager.getStateValues(nextStats.get(i));
					if (svs.get(levelPlayer) > bestChildScore) {
						bestChildIndex = i;
					}
				}
				current = nextStats.get(bestChildIndex);
				simDepth++;
				
				levelPlayer = (simDepth %roleCount);
			} catch (MoveDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransitionDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (!theMachine.isTerminal(current) && simDepth < MAX_ROLLDEPTH);
		//        
		if (!theMachine.isTerminal(current)) {
			// TODO return the cil2p result
			List<Double> go = cil2pManager.getStateValues(current);
			System.out.println("returning state value from NN" + go);
			return go;
		}
		// the node was terminal
		try {
			return theMachine.getDoubleGoals(current);
		} catch (GoalDefinitionException e) {
			// goal definition failed
			return new ArrayList<Double>();
		}
	}

	@Override
	public String getName() {
		return "Neural UCT Gamer";
	}

}
