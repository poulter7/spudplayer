package shef.network;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import org.encog.engine.EncogEngine;
import org.encog.engine.StatusReportable;
import org.encog.util.benchmark.EncogBenchmark;
import org.neuroph.nnet.Neuroph;

import player.request.factory.RequestFactory;
import util.game.Game;
import util.statemachine.MachineState;
import util.statemachine.implementation.prover.ProverStateMachine;
import util.symbol.factory.SymbolFactory;
import util.symbol.grammar.SymbolList;
import cs227b.teamIago.resolver.Atom;

public class NetworkStress extends StressTest{

	
	private final long stressTime = 5000;

	public static void main(String[] args) {
		new NetworkStress();
	}

	public NetworkStress() {
		CIL2PManager cil2pManager;
		ProverStateMachine p;
		List<Atom> players = Arrays.asList(new Atom("WHITE"), new Atom("RED"));
		try {

			RequestFactory rf = new RequestFactory();
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new FileReader("specs/connect4_strip.kif"));

			String s;
			while ((s = br.readLine()) != null) {
				sb.append(s + " ");
			}

			SymbolList list = (SymbolList) SymbolFactory.create("( " + sb.toString() + " )");
			Game g = Game.createEphemeralGame(rf.parseDescription(list));
			p = new ProverStateMachine();
			p.initialize(rf.parseDescription(list));

			CIL2PNet net = CIL2PFactory.modeNetFromGame(g);
			cil2pManager = new CIL2PManager(net, players);
		} catch (Exception e) {
			throw new RuntimeException("Failed to launch");
		}
		if (cil2pManager != null && p !=null) {
			MachineState init = p.getInitialState();
			
			new Thread(new StopTimer(this, stressTime)).start();
			int eval = 0;
			
			while (cont) {
				cil2pManager.getStateValue(init, 0);
				eval++;
			}
			System.out.println("ran evaluation " +eval + " times.");
		}
		System.out.println("done");
	}
	
	
}
