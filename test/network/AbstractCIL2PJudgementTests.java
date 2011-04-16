package network;

import java.util.Arrays;

import shef.network.CIL2PManager;

import junit.framework.TestCase;

public class AbstractCIL2PJudgementTests extends TestCase {
	CIL2PManager cil2p_manager;
    

	/**
	 * returns of two possible states which has the highest
	 * 
	 * @param outcomeA
	 *            the scores for each player for state A
	 * @param outcomeB
	 *            the scores for each player for state B
	 * @param player
	 *            the player index to compare
	 * @return 1 if A[player] > B[player], -1 if A[player] < B[player] else 0
	 */
	public int bestState(double[] outcomeA, double[] outcomeB, int player) {
		System.out.println(Arrays.toString(outcomeA));
		System.out.println(Arrays.toString(outcomeB));

		System.out.println("Gain" + (outcomeA[1]-outcomeA[0]));
		System.out.println("Gain" + (outcomeB[1]-outcomeB[0]));
		if (outcomeA[player] > outcomeB[player])
			return 1;
		else if (outcomeA[player] < outcomeB[player]) {
			return -1;
		} else {
			return 0;
		}
	}
	
	/**
	 * returns of which player has the highest gain
	 * 
	 * @param outcomeA
	 *            the scores for each player for state A
	 * @param outcomeB
	 *            the scores for each player for state B
	 * @param player
	 *            the player index to compare
	 * @return 1 if A[player] > B[player], -1 if A[player] < B[player] else 0
	 */
	public int highestGain(double[] outcomeA, double[] outcomeB, int player) {
		System.out.println(outcomeA[player%2] - outcomeB[player%2]);
		System.out.println(player);
		System.out.println(outcomeA[(player+1)%2] - outcomeB[(player+1)%2]);
		
		if (outcomeA[player%2] - outcomeB[player%2]  > outcomeA[(player+1)%2] - outcomeB[(player+1)%2])
			return 1;
		else if (outcomeA[player%2] - outcomeB[player%2]  < outcomeA[(player+1)%2] - outcomeB[(player+1)%2]) {
			return -1;
		} else {
			return 0;
		}
	}
}
