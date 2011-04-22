package shef.strategies;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import player.GamePlayer;
import player.gamer.Gamer;
import player.gamer.exception.MetaGamingException;
import player.gamer.exception.MoveSelectionException;
import player.request.factory.RequestFactory;
import player.request.factory.exceptions.RequestFormatException;
import shef.strategies.ann.StrategyAlphaBeta;
import shef.strategies.uct.StrategyUCTNeural;
import shef.strategies.uct.StrategyUCTSimple;
import util.game.Game;
import util.gdl.factory.GdlFactory;
import util.gdl.factory.exceptions.GdlFormatException;
import util.gdl.grammar.GdlProposition;
import util.gdl.grammar.GdlRelation;
import util.match.Match;
import util.statemachine.exceptions.GoalDefinitionException;
import util.statemachine.exceptions.MoveDefinitionException;
import util.statemachine.exceptions.TransitionDefinitionException;
import util.symbol.factory.SymbolFactory;
import util.symbol.factory.exceptions.SymbolFormatException;
import util.symbol.grammar.SymbolList;


/**
 * Simple test class for stressing a strategy
 * @author jonathan
 *
 */
public final class StressStrategy {

	
	
	public static void main(String[] args) throws IOException, RequestFormatException, SymbolFormatException, GdlFormatException, MetaGamingException, MoveSelectionException, TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {

//		Gamer st = new StrategyAlphaBeta();
		Gamer st = new StrategyUCTNeural(0.05);
//		Gamer st = new StrategyUCTSimple();
		
		GamePlayer player = new GamePlayer(9000, st);
		
		RequestFactory rf = new RequestFactory();
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader("specs/connect4_strip.kif"));
		
		String s;
		while((s = br.readLine()) != null){
			sb.append(s+" ");
		}
		
		SymbolList list = (SymbolList) SymbolFactory.create("( "+ sb.toString()+" )");
		Game g = Game.createEphemeralGame(rf.parseDescription(list));
		Match m = new Match("stress", 0, 0, g);
		player.getGamer().setMatch(m);
		GdlRelation role = (GdlRelation) GdlFactory.create(list.get(0));
		GdlProposition prop =  (GdlProposition) role.getBody().get(0).toSentence();
		player.getGamer().setRoleName(prop);
		player.getGamer().metaGame(System.currentTimeMillis() + 5*1000);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		player.getGamer().selectMove(System.currentTimeMillis() + 20*1000);
	}
}
