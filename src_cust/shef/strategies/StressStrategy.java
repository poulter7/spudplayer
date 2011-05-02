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

	/*
	 * Values for testing effect of random results
	 */
	public static int valChanged=0;
	public static int valLooked = 0;
	
	public static void main(String[] args) throws IOException, RequestFormatException, SymbolFormatException, GdlFormatException, MetaGamingException, MoveSelectionException, TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		double[] vs = new double[]{0, 0.0001, 0.0005, 0.001, 0.005, 0.01, 0.05, 0.1, 0.15, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7};
		for(int i = 0; i < vs.length; i++){
//		Gamer st = new StrategyAlphaBeta();
		Gamer st = new StrategyUCTNeural(vs[i], false);
//		Gamer st = new StrategyUCTSimple();
		
		GamePlayer player = new GamePlayer(9000+i, st);
		
		RequestFactory rf = new RequestFactory();
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader("specs/skirmishfinal_strip.kif"));
		
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
		player.getGamer().metaGame(System.currentTimeMillis() + 600*1000);
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		System.out.println("R:"+(float)(valLooked-valChanged)/valLooked *100 + "% random of " + valLooked);
	}
//		player.getGamer().selectMove(System.currentTimeMillis() + 20*1000);
	}
}
