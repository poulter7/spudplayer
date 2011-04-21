package shef.network;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import player.request.factory.RequestFactory;
import util.game.Game;
import util.gdl.factory.GdlFactory;
import util.gdl.factory.exceptions.GdlFormatException;
import util.gdl.grammar.GdlProposition;
import util.statemachine.MachineState;
import util.statemachine.Role;
import util.statemachine.implementation.prover.ProverStateMachine;
import util.symbol.factory.SymbolFactory;
import util.symbol.factory.exceptions.SymbolFormatException;
import util.symbol.grammar.SymbolList;
import cs227b.teamIago.resolver.Atom;

/**
 * A test class to stress some networks
 * @author jonathan
 *
 */
public class NetworkStress{

	
	private final long stressTime = 5000;
	private final String loc = "specs/connect4_strip.kif";
	private boolean cont = true;

	public static void main(String[] args) {
		new NetworkStress();
	}

	
	public NetworkStress() {
		CIL2PManager cil2pManager;
		ProverStateMachine p;
		List<Atom> players = Arrays.asList(new Atom("WHITE"), new Atom("RED"));
		List<Role> playerList = new ArrayList<Role>();
		for (Atom r : (List<Atom>)players) {
			GdlProposition playerProp;
			try {
				playerProp = (GdlProposition) GdlFactory.create(r.toString().toLowerCase());
				playerList.add(new Role(playerProp));
			} catch (GdlFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SymbolFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {

			RequestFactory rf = new RequestFactory();
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new FileReader(loc));

			String s;
			while ((s = br.readLine()) != null) {
				sb.append(s + " ");
			}

			SymbolList list = (SymbolList) SymbolFactory.create("( " + sb.toString() + " )");
			Game g = Game.createEphemeralGame(rf.parseDescription(list));
			p = new ProverStateMachine();
			p.initialize(rf.parseDescription(list));

			CIL2PNet net = CIL2PFactory.createGameNetworkFromGame(g);
			cil2pManager = new CIL2PManager(net, players);
		} catch (Exception e) {
			throw new RuntimeException("Failed to launch");
		}
		if (cil2pManager != null && p !=null) {
			MachineState init = p.getInitialState();
			
			new Thread(new StopTimer(this, stressTime)).start();
			int eval = 0;
			
			while (cont ) {
				cil2pManager.getStateValue(init, playerList.get(0));
				eval++;
			}
			System.out.println("ran evaluation " +eval + " times.");
		}
		System.out.println("done");
	}
	

	public void halt() {
		this.cont = false;
		
	}


	/**
	 * Will interrupt NetworkStres after a certain time.
	 * @author jonathan
	 *
	 */
	class StopTimer implements Runnable {
		
		NetworkStress stopThis;
		long timeToLeave; 
		
		public StopTimer(NetworkStress s, long time) {
			stopThis = s;
			timeToLeave = time;
		}

		@Override
		public void run() {
			System.out.println("starting");
			try {
				Thread.sleep(timeToLeave);
				stopThis.halt();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
}
