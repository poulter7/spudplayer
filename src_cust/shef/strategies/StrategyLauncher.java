package shef.strategies;

import java.io.IOException;

import player.GamePlayer;
import player.gamer.Gamer;
import player.gamer.statemachine.simple.SimpleMonteCarloGamer;
import shef.strategies.ann.StrategyAlphaBeta;
import shef.strategies.uct.StrategyUCTNeural;
import shef.strategies.uct.StrategyUCTSimple;

/**
 * Main entrance class for launching a strategy 
 * @author jonathan
 *
 */
public class StrategyLauncher {
	// Simple main function that starts a RandomGamer on a specified port.
	// It might make sense to factor this out into a separate app sometime,
	// so that the GamePlayer class doesn't have to import RandomGamer.
	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Usage: GamePlayer <port>");
			System.exit(1);
		}

		try {
			Gamer strat = null;
			int playerIndex = Integer.parseInt(args[1]);
			switch(playerIndex){
			case 1: strat = new StrategyUCTSimple(); 		break;
			case 2: strat = new StrategyUCTNeural(); 		break;
			case 3: strat = new SimpleMonteCarloGamer(); 	break;
			case 4: strat = new StrategyAlphaBeta();		break;
			default: throw new RuntimeException("Invalid player chosen cannot continue");
			}
			GamePlayer player = new GamePlayer(Integer.valueOf(args[0]), strat);
			player.run();
		} catch (NumberFormatException e) {
			System.err.println("Illegal port number: " + args[0]);
			e.printStackTrace();
			System.exit(2);
		} catch (IOException e) {
			System.err.println("IO Exception: " + e);
			e.printStackTrace();
			System.exit(3);
		}
	}
	
}
