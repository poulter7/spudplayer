package shef.strategies.uct;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import player.GamePlayer;
import player.gamer.Gamer;
import player.gamer.exception.MetaGamingException;
import player.gamer.exception.MoveSelectionException;
import player.request.factory.RequestFactory;
import player.request.factory.exceptions.RequestFormatException;
import shef.strategies.uct.tree.StateActionPair;
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


public class StrategyStress {

	
	
	public static void main(String[] args) throws IOException, RequestFormatException, SymbolFormatException, GdlFormatException, MetaGamingException, MoveSelectionException, TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {

//		Gamer st = new UCTNeuralStrategy();
		Gamer st = new UCTSimpleStrategy();
		
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
		player.getGamer().selectMove(System.currentTimeMillis() + 10*1000);
	}
}
