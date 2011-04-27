package shef.network;

import util.gdl.factory.GdlFactory;
import util.gdl.factory.exceptions.GdlFormatException;
import util.gdl.grammar.GdlProposition;
import util.statemachine.Role;
import util.symbol.factory.exceptions.SymbolFormatException;

/**
 * A GOAL mapping of player -> score
 * @author jonathan
 *
 */
public class Goal {

	private static final long serialVersionUID = 1L;
	final Role player;
    final int score;

    public Goal(String player, String score) {
    	GdlProposition playerProp = null;
		try {
			playerProp = (GdlProposition) GdlFactory.create(player.toLowerCase());
		} catch (GdlFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SymbolFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.player = new Role(playerProp);
		this.score = Integer.parseInt(score.toString());
    }

    public String toString() {
        return super.toString();
    }
}
