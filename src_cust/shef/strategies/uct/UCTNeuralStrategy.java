package shef.strategies.uct;

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.randomize.GaussianRandomizer;

import shef.network.CIL2PFactory;
import shef.network.CIL2PManager;
import shef.network.CIL2PNet;
import util.statemachine.MachineState;
import util.statemachine.exceptions.GoalDefinitionException;
import util.statemachine.exceptions.MoveDefinitionException;
import util.statemachine.exceptions.TransitionDefinitionException;

/**
 * 
 * 
 * @author jonathan poulter
 */
public class UCTNeuralStrategy extends UCTGamer {

	int MAX_ROLLDEPTH = 10;
	private CIL2PManager cil2pManager;
	protected static final float C = 50;
	private double sigmaOverTwo = 0.1; 
	private GaussianRandomizer e = new GaussianRandomizer(0, sigmaOverTwo*sigmaOverTwo);

	/**
	 * Setup the UCT game tree
	 */
	@Override
	public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		System.out.println("init: " + timeout);
		CIL2PNet net = CIL2PFactory.modeNetFromGame(getMatch().getGame());
		cil2pManager = new CIL2PManager(net);
		super.stateMachineMetaGame(timeout);
	}

	protected List<Double> completeRollout(MachineState from, int fromLvl) {
		int simDepth = fromLvl;
		int levelPlayer = (simDepth % roleCount);
		//        
		MachineState current = from;
		do { // play the best move for the current player
			try {
				
				List<MachineState> nextStats = theMachine.getNextStates(current);
				double bestChildScore = 0;
				int bestChildIndex = 0;
				int nextStateCount = nextStats.size();
				for (int i = 0; i < nextStateCount; i++) {
					double childScore = cil2pManager.getStateValue(nextStats.get(i), levelPlayer);
					if (childScore > bestChildScore) {
						bestChildScore = childScore;
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
		} while (!theMachine.isTerminal(current));
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
		return "Neural Gamer";
	}
	
	
	

}
