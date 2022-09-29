/**
 * 
 */
package gui;

import generation.Order;

import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JFrame;


/**
 * This class is a wrapper class to startup the Maze game as a Java application
 * 
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 * 
 * TODO: use logger for output instead of Sys.out
 */
public class MazeApplication extends JFrame {

	// not used, just to make the compiler, static code checker happy
	private static final long serialVersionUID = 1L;
	
	// developments vs production version
	// for development it is more convenient if we produce the same maze over an over again
	// by setting the following constant to false, the maze will only vary with skill level and algorithm
	// but not on its own
	// for production version it is desirable that we never play the same maze 
	// so even if the algorithm and skill level are the same, the generated maze should look different
	// which is achieved with some random initialization
	private static final boolean DEVELOPMENT_VERSION_WITH_DETERMINISTIC_MAZE_GENERATION = false;

	/**
	 * Constructor
	 */
	public MazeApplication() {
		init(null);
	}

	/**
	 * Constructor that loads a maze from a given file or uses a particular method to generate a maze
	 * @param parameter can identify a generation method (Prim, Kruskal, Eller)
     * or a filename that stores an already generated maze that is then loaded, or can be null
	 */
	public MazeApplication(String[] parameter) {
		init(parameter);
	}

	/**
	 * Instantiates a controller with settings according to the given parameter.
	 * @param parameter can identify a generation method (Prim, Kruskal, Eller)
	 * or a filename that contains a generated maze that is then loaded,
	 * or can be null
	 * @return the newly instantiated and configured controller
	 */
	 Controller createController(String[] parameter) {
	    // need to instantiate a controller to return as a result in any case
	    Controller result = new Controller() ;
	    // can decide if user repeatedly plays the same mazes or 
	    // if mazes are different each and every time
	    // set to true for testing purposes
	    // set to false for playing the game
	    if (DEVELOPMENT_VERSION_WITH_DETERMINISTIC_MAZE_GENERATION)
	    	result.setDeterministic(true);
	    else
	    	result.setDeterministic(false);
	    String msg = null; // message for feedback
	    if (parameter == null) {
	    	msg = "MazeApplication: generating random maze in default mode.";
	    }
	    else {
	    	// Case 1: no input
		    for (int r = 0; r < parameter.length; r++) {
		    	if ("-g".equalsIgnoreCase(parameter[r])) {
		    		if ("Prim".equalsIgnoreCase(parameter[r+1]))
				    {
				        msg = "MazeApplication: generating random maze with Prim's algorithm.";
				        result.setBuilder(Order.Builder.Prim);
				    }
				    // Case 3 a and b: Eller, Kruskal, Boruvka or some other generation algorithm
				    else if ("Kruskal".equalsIgnoreCase(parameter[r+1]))
				    {
				    	// TODO: for P2 assignment, please add code to set the builder accordingly
				        throw new RuntimeException("Don't know anybody named Kruskal ...");
				    }
				    else if ("Eller".equalsIgnoreCase(parameter[r+1]))
				    {
				    	// TODO: for P2 assignment, please add code to set the builder accordingly
				        throw new RuntimeException("Don't know anybody named Eller ...");
				    }
				    else if ("Boruvka".equalsIgnoreCase(parameter[r+1]))
				    {
				    	// TODO: for P2 assignment, please add code to set the builder accordingly
				    	msg = "MazeApplication: generating random maze with Boruvka's algorithm.";
				    	result.setBuilder(Order.Builder.Boruvka);
				    }
				    // Case 4: a file
				    else {
				    	File f = new File(parameter[r+1]) ;
				        if (f.exists() && f.canRead())
				        {
				            msg = "MazeApplication: loading maze from file: " + parameter[r+1];
				            result.setFileName(parameter[r+1]);
				            return result;
				        }
				        else {
				            // None of the predefined strings and not a filename either: 
				            msg = "MazeApplication: unknown parameter value: " + parameter[r+1] + " ignored, operating in default mode.";
				        }
				    }
		    		System.out.println(msg);
		    	}
		    	if ("-d".equalsIgnoreCase(parameter[r])) {
		    		if ("Wizard".equalsIgnoreCase(parameter[r+1])) {
			    		System.out.println("Robot will use Wizard to solve maze.");
			    		result.wizardDriver = true;
			    	}
			    	else if ("WallFollower".equalsIgnoreCase(parameter[r+1])) {
			    		System.out.println("Robot will use WallFollower to solve maze.");
			    		result.wfDriver = true;
			    	}
			    	else {
			    		System.out.println("Player will solve maze manually.");
			    	}
		    	}
		    	if ("-r".equalsIgnoreCase(parameter[r])) {
		    		String toCheck = parameter[r+1];
		    		if ("r".equalsIgnoreCase(Character.toString(toCheck.charAt(0)))) {
		    			System.out.println("Robot has a reliable forward sensor");
		    			// must implement this still
		    			result.setReliableForward(true);
		    		}
		    		else {
		    			System.out.println("Robot has an unreliable forward sensor");
		    			// must implement this still
		    			result.setReliableForward(false);
		    		}
		    		if ("r".equalsIgnoreCase(Character.toString(toCheck.charAt(1)))) {
		    			System.out.println("Robot has a reliable left sensor");
		    			// must implement this still
		    			result.setReliableLeft(true);
		    		}
		    		else {
		    			System.out.println("Robot has an unreliable left sensor");
		    			// must implement this still
		    			result.setReliableLeft(false);
		    		}
		    		if ("r".equalsIgnoreCase(Character.toString(toCheck.charAt(2)))) {
		    			System.out.println("Robot has a reliable right sensor");
		    			// must implement this still
		    			result.setReliableRight(true);
		    		}
		    		else {
		    			System.out.println("Robot has an unreliable right sensor");
		    			// must implement this still
		    			result.setReliableRight(false);
		    		}
		    		if ("r".equalsIgnoreCase(Character.toString(toCheck.charAt(3)))) {
		    			System.out.println("Robot has a reliable backward sensor");
		    			// must implement this still
		    			result.setReliableBackward(true);
		    		}
		    		else {
		    			System.out.println("Robot has an unreliable backward sensor");
		    			// must implement this still
		    			result.setReliableBackward(false);
		    		}
		    	}
				if ("-b".equalsIgnoreCase(parameter[r])) {
					try {
						result.battery = Integer.parseInt(parameter[r+1]);
					} catch (Exception e) {
						System.out.println("Could not recognize battery level, defaulting to 3500");
					}
				}
		    }
	    }
	    // controller instanted and attributes set according to given input parameter
	    // output message and return controller
	    return result;
	}

	/**
	 * Initializes some internals and puts the game on display.
	 * @param parameter can identify a generation method (Prim, Kruskal, Eller)
     * or a filename that contains a generated maze that is then loaded, or can be null
	 */
	private void init(String[] parameter) {
	    // instantiate a game controller and add it to the JFrame
	    Controller controller = createController(parameter);
		add(controller.getPanel()) ;
		// instantiate a key listener that feeds keyboard input into the controller
		// and add it to the JFrame
		KeyListener kl = new SimpleKeyListener(this, controller) ;
		addKeyListener(kl) ;
		// set the frame to a fixed size for its width and height and put it on display
		setSize(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT+22) ;
		setVisible(true) ;
		// focus should be on the JFrame of the MazeApplication and not on the maze panel
		// such that the SimpleKeyListener kl is used
		setFocusable(true) ;
		// start the game, hand over control to the game controller
		controller.start();
	}
	
	/**
	 * Main method to launch Maze game as a java application.
	 * The application can be operated in three ways. 
	 * 1) The intended normal operation is to provide no parameters
	 * and the maze will be generated by a randomized DFS algorithm (default). 
	 * 2) If a filename is given that contains a maze stored in xml format. 
	 * The maze will be loaded from that file. 
	 * This option is useful during development to test with a particular maze.
	 * 3) A predefined constant string is given to select a maze
	 * generation algorithm, currently supported is "Prim".
	 * @param args is optional, first string can be a fixed constant like Prim or
	 * the name of a file that stores a maze in XML format
	 */
	public static void main(String[] args) {
	    JFrame app ; 
		if (args.length > 0) {
			app = new MazeApplication(args) ;
		}
		else {
			app = new MazeApplication();
		}
		
		app.repaint() ;
	}

}
