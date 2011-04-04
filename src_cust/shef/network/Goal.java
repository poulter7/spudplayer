package shef.network;

import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.Expression;
import cs227b.teamIago.resolver.Predicate;

public class Goal extends Predicate {

    Atom player;
    int score;

    public Goal(Atom player, Atom score) {
        super("GOAL", new Expression[] { player, score });
        this.player = player;
        this.score = Integer.parseInt(score.toString());
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
