package shef.network;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import shef.instantiator.Instantiator;
import shef.instantiator.andortree.Node;
import util.game.Game;
import cs227b.teamIago.parser.Parser;
import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Predicate;
import cs227b.teamIago.resolver.Term;
import cs227b.teamIago.resolver.Theory;

/**
 * A factory class which generates instances of {@link CIL2PNet}
 * Conforms to factory pattern
 * @author jonathan
 *
 */
public final class CIL2PFactory {

    private static final String PRINT_TREES_STRING = "print-trees";
    private static final String GAME_LOCATION_STRING = "game-location";
    private static final String PRINT_STATS_STRING = "print-play-info";
   
    /**
     * Overriden default constructor for utility class
     */
    private CIL2PFactory() {}
    
    
    /**
     * the default directory
     */
    public static final String DEFAULT_DIR = "specs/";
    
    /*
     * GAME Network creation method
     */
    
    /**
     * Constructs a full CIL2P shef.network using the CIL2P algorithm.
     * 
     * This is the method to use when <b>playing a game</b>
     * 
     * This method limits the weights of interconnecting neurons to one instead of 
     * calculating a value for W, this is Michulke's optimisation
     * 
     * First the goal proof trees are creates using the {@link Instantiator}
     * class these are sequentially added to the shef.network building a the input
     * and output layers.
     * 
     * Finally the shef.network is <i>finalised</i>, this involves connecting the
     * inputs and outputs via a layer of hidden nodes and setting the relevant
     * weights and activations
     * 
     * @param game the game this network will represent the goal states for
     * @return the created shef.network
     */
    public static CIL2PNet createGameNetworkFromGame(Game game) {
    	Theory t = getTheoryFromGame(game);
    	return createCIL2PNetwork(t, true, true);
    }
    
    /**
     * Builds the same shef.network as <code>modNetFromFile(Game)</code>
     * @param game the game to be translated
     * @return the created shef.network
     */
    static CIL2PNet createGameNetworkFromFile(String gameLocation) {
        return createNetworkfromFileLocationOptions(gameLocation, false, false, true);
    }



    /**
     * Builds a shef.network
     * 
     * @param gameLocation
     *            the location of the game description
     * @return the generated shef.network
     */
    static CIL2PNet createNetworkFromFileLocation(String gameLocation) {
        return createNetworkfromFileLocationOptions(gameLocation, false, false, false);
    }
    
    /**
	 * create a shef.network based on passed params
	 * 
	 * @param gameLocation
	 * @param printTrees
	 *            prints the tree after construction if true
	 * @param asStateEval if true the use Michulke's optimization
	 * @return the generated network
	 */
	static CIL2PNet createNetworkfromFileLocationOptions(String gameLocation,
	        boolean printTrees, boolean printStats, boolean asStateEval) {
	    Properties prop = new Properties();
	    prop.setProperty(GAME_LOCATION_STRING, gameLocation);
	    prop.setProperty(PRINT_TREES_STRING, printTrees + "");
	    prop.setProperty(PRINT_STATS_STRING, printStats + "");
	    return createCIL2PNetwork(prop, asStateEval);
	}

	/**
	 * Constructs a full CIL2P shef.network using the CIL2P algorithm
	 * 
	 * First the goal proof trees are creates using the {@link Instantiator}
	 * class these are sequentially added to the shef.network building a the input
	 * and output layers.
	 * 
	 * Finally the shef.network is <i>finalised</i>, this involves connecting the
	 * inputs and outputs via a layer of hidden nodes and setting the relavent
	 * weights and activations
	 * 
	 * @return the created shef.network
	 */
	private static CIL2PNet createCIL2PNetwork(Properties prop, boolean asStateEval) {
	    String GAME_LOCATION = prop.getProperty(GAME_LOCATION_STRING);
	
	    System.out.println("Game Location: " + GAME_LOCATION);
	    // create new theory and load the game file
	    Theory theoryObj = getTheoryFromFile(DEFAULT_DIR + GAME_LOCATION);
	
	    return createCIL2PNetwork(theoryObj, asStateEval, false);
	
	}

	/**
	 * As {@link createCIL2PNetwork(Properties, boolean)}, but with the given theory
	 * @param theory
	 * @param gameNet
	 * @return
	 */
	private static CIL2PNet createCIL2PNetwork(Theory theory, boolean weight_one, boolean gameNet) {
	
	    // instantiate goal trees
	    Instantiator inst = new Instantiator(theory);
	    List<Node> goalProofs = inst.getProofTrees(-1);
	
	    // initialise input and output part of shef.network structure
	    System.out.println("creating network");
	    CIL2PNet network = new CIL2PNet(goalProofs, weight_one, gameNet);
	    
	    return network;
	
	}

	/**
     * Return the list of roles to a game from the file
     * @param gameLocation
     * @return
     */
    static List<Term> getRolesFromFile(String gameLocation){
    	Theory t = getTheoryFromFile(DEFAULT_DIR + gameLocation);
    	ExpList e = t.getCandidates(new Atom("role"));
    	ArrayList<Term> terms = new ArrayList<Term>();
    	for(Object p : e.toArrayList()){
    		terms.add(new Atom(((Predicate)p).getOperands().get(0).toString()));
    	}
    	return terms;
    }

    /**
     * Generate a theory based on the given source location
     * 
     * @param gameLocation
     * @return a theory file representing the game
     */
    static Theory getTheoryFromFile(String gameLocation) {
        Theory t = new Theory(false, false);

        // add each expression in the game file
        ExpList exps = Parser.parseFile(gameLocation + ".kif");
        t.add(exps);
        return t;
    }

    /**
     * Generate a theory based on the given game
     * 
     * @param gameLocation
     * @return a theory file representing the game
     */
    static Theory getTheoryFromGame(Game game) {
        Theory t = new Theory(false, false);

        // add each expression in the game file
        String sub = game.getRules().toString();
        sub = sub.replace(',', ' ');
        ExpList exps = Parser.parseExpList(sub);
        t.add(exps);
        return t;
    }

    /**
     * Test method will create a network from the top level properties file
     * @param args
     */
    public static void main(String[] args) {
    	 Properties prop = new Properties();
         try {
             prop.load(new FileInputStream("settings.properties"));
         } catch (IOException e) {
             e.printStackTrace();
         }
    }
}
