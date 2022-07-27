package gui;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import generation.CardinalDirection;
import generation.Maze;
import generation.MazeFactory;
import generation.Order;
import generation.Stuborder;
import gui.Robot.Direction;
import gui.Robot.Turn;
import generation.Order.Builder;

/**
 * ReliableRobotTest runs a series of tests on ReliableRobot to ensure that its methods work properly. It works together
 * with Wizard, Controller, CardinalDirection, Maze, MazeFactory, MazeApplication, Order, Stuborder,and Robot to
 * accomplish this.
 * 
 * @author Matthew Cheng
 *
 */

public class ReliableRobotTest {
	// declare all variables that we may use for tests
	Maze maze;
	String[] comlinearg = {"-d","Wizard"};
	MazeFactory factory;
	int skill = 2;
    boolean perfect = false;
    Builder builder = Order.Builder.DFS;
    MazeApplication testApp;
    Controller controller;
    ReliableRobot robot;
    Wizard wizard;
	
    /**
     * Initialize each of our variables before running each test as to not repeat code
     */
	@BeforeEach
	public final void SetUp() {
		// instantiate MazeFactory
	    factory = new MazeFactory();
		Stuborder stuborder = new Stuborder(10, skill, perfect, builder);
		factory.order(stuborder);
		factory.waitTillDelivered();
		// get maze configuration 
		maze = stuborder.maze;
		// get maze app and controller in test mode
		testApp = new MazeApplication(comlinearg);
		controller = testApp.createController(comlinearg);
		controller.turnOffGraphics();
		controller.test = true;
		controller.switchFromGeneratingToPlaying(maze);
		// get maze and wizard
		robot = new ReliableRobot(controller);
		wizard = new Wizard(); 
    	wizard.setRobot(robot);
    	wizard.setMaze(maze);
	}
	
	///////////////////////////////////////////////////////////////////
	/////////////////// Initial configuration of a robot   ////////////
	///////////////////////////////////////////////////////////////////
	
	/**
	 * Tests to see if setController() works properly.
	 */
	@Test
	public final void testSetController() {
		assertTrue(robot.getController() != null);
	}
	
	/**
	 * Tests to see if addDistanceSensor() initialized sensors properly.
	 */
	@Test
	public final void testAddDistanceSensors() {
		assertTrue(robot.getSensor(Direction.FORWARD) != null);
		assertTrue(robot.getSensor(Direction.BACKWARD) != null);
		assertTrue(robot.getSensor(Direction.LEFT) != null);
		assertTrue(robot.getSensor(Direction.RIGHT) != null);
	}
	
	///////////////////////////////////////////////////////////////////
	/////////////////// Current location in game   ////////////////////
	///////////////////////////////////////////////////////////////////
	
	/*
	 * Since both methods use the respective methods from controller, we do not need to
	 * test them here.
	 */
	
	///////////////////////////////////////////////////////////////////
	/////////////////// Battery and Energy consumption ////////////////
	///////////////////////////////////////////////////////////////////
	
	/**
	 * Tests if the getBatteryLevel returns the current battery level.
	 * It starts at 3500, so driving 1 step should cause this to drop below 3500.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testGetBatteryLevel() throws Exception {
		assertEquals(robot.getBatteryLevel(),3500);
		wizard.drive1Step2Exit();
		assertTrue(robot.getBatteryLevel() < 3500);
	}
	
	/**
	 * Tests to see if we can set the battery level to a specified value.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testSetBatteryLevel() throws Exception {
		assertEquals(robot.getBatteryLevel(), 3500);
		robot.setBatteryLevel(1000);
		assertEquals(robot.getBatteryLevel(), 1000);
	}

	/**
	 * Tests to make sure each type of turn costs the correct amount of energy. This relies
	 * on rotate also working properly.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testEnergyTurnCostAndRotate() throws Exception {
		assertEquals(robot.getBatteryLevel(), 3500);
		robot.rotate(Turn.LEFT);
		assertEquals(robot.getBatteryLevel(), 3500 - (robot.getEnergyForFullRotation()/4));
		robot.rotate(Turn.RIGHT);
		assertEquals(robot.getBatteryLevel(), 3500 - (robot.getEnergyForFullRotation()/2));
		robot.rotate(Turn.AROUND);
		assertEquals(robot.getBatteryLevel(), 3500 - robot.getEnergyForFullRotation());
	}
	
	/**
	 * Tests to make sure moving 1 step costs the correct amount of energy.
	 * This is accomplished by positioning the robot so that it has at least 1
	 * space ahead of it to move into and then checking the battery level after 
	 * the move.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testEnergyMoveCost() throws Exception {
		while(robot.distanceToObstacle(Direction.FORWARD) < 1) {
			wizard.drive1Step2Exit();
		}
		robot.setBatteryLevel(3500);
		robot.move(1);
		assertEquals(robot.getBatteryLevel(), 3500 - robot.getEnergyForStepForward());
	}
	
	///////////////////////////////////////////////////////////////////
	/////////////////// Odometer, distance traveled    ////////////////
	///////////////////////////////////////////////////////////////////
	
	/**
	 * Tests to see if the odometer increments correctly as well as if
	 * getOdometerReading actually works. The wizard drives the robot
	 * 1 step and then we check to see if the odometer recognized the step.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testOdometerWorks() throws Exception {
		// jump since moving may fail if a wall is in the way
    	wizard.drive1Step2Exit();
		assertEquals(robot.getOdometerReading(), 1);
	}
	
	/**
	 * Tests to see if resetting the odometer works correctly. We have
	 * the wizard drive the robot 1 step and then we reset the odometer.
	 * If it worked properly, instead of reading 1 as a result of the move,
	 * it reads 0.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testResetOdometer() throws Exception {
		wizard.drive1Step2Exit();
		robot.resetOdometer();
		assertEquals(robot.getOdometerReading(), 0);
	}
	
	///////////////////////////////////////////////////////////////////
	/////////////////// Actuators /////////////////////////////////////
	///////////////////////////////////////////////////////////////////
	
	/**
	 * Tests to see if turning right actually makes the robot face right
	 * from its previous direction (North). It is important to note that
	 * since north and south are switched, turning right should orient 
	 * west instead of east.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testRotateRight() throws Exception {
		while (robot.getCurrentDirection() != CardinalDirection.North) {
			robot.rotate(Turn.RIGHT);
		}
		robot.rotate(Turn.RIGHT);
		assertEquals(robot.getCurrentDirection(), CardinalDirection.West);
	}
	
	/**
	 * Tests to see if turning left actually makes the robot face left
	 * from its previous direction (North). It is important to note that
	 * since north and south are switched, turning left should orient 
	 * east instead of west.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testRotateLeft() throws Exception {
		while (robot.getCurrentDirection() != CardinalDirection.North) {
			robot.rotate(Turn.RIGHT);
		}
		robot.rotate(Turn.LEFT);
		assertEquals(robot.getCurrentDirection(), CardinalDirection.East);
	}
	
	/**
	 * Tests to see if turning right actually makes the robot face right
	 * from its previous direction (North).
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testRotateAround() throws Exception {
		while (robot.getCurrentDirection() != CardinalDirection.North) {
			robot.rotate(Turn.RIGHT);
		}
		robot.rotate(Turn.AROUND);
		assertEquals(robot.getCurrentDirection(), CardinalDirection.South);
	}
	
	/**
	 * Tests to see if moving changes the current position of the robot.
	 * It first ensures that the robot has space to move, then moves, and 
	 * lastly checks to see if the position has changed.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testIfMoveWorks() throws Exception {
		while(robot.distanceToObstacle(Direction.FORWARD) < 1) {
			wizard.drive1Step2Exit();
		}
    	int[] start_pos = robot.getCurrentPosition();
    	robot.move(1);
    	int[] cur_pos = robot.getCurrentPosition();
		assertTrue(start_pos[0] != cur_pos[0] | start_pos[1] != cur_pos[1]);
	}
	
	/**
	 * Tests to make sure an exception is thrown if the robot tries to move a negative distance.
	 * @throws Exception
	 */
	@Test
	public final void testMoveNegative() throws Exception {
		Exception exception = assertThrows(Exception.class, () -> robot.move(-1));
		assertEquals("java.lang.IllegalArgumentException: Distance to move cannot be negative.", exception.toString());
	}
	
	/**
	 * Tests to make sure the robot does not actually jump if the battery is too low.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testJumpFail() throws Exception {
		robot.setBatteryLevel(39);
		int[] start_pos = robot.getCurrentPosition();
    	robot.jump();
    	int[] cur_pos = robot.getCurrentPosition();
		assertTrue(start_pos[0] == cur_pos[0] & start_pos[1] == cur_pos[1]);
	}
	
	/**
	 * Tests to see if jumping with a wall in front changes the current position of the robot.
	 * First, the robot is moved until it is facing an interior wall. Next, store the current position.
	 * Then, have the robot jump. Lastly, get the current position and check to see if it is different
	 * from the starting position.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testIfJumpWorksWithWall() throws Exception {
		robot.move(robot.distanceToObstacle(Direction.FORWARD));
		if (maze.getFloorplan().hasBorder(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1], robot.getCurrentDirection().getDirection()[0], robot.getCurrentDirection().getDirection()[1])) {
			robot.move(robot.distanceToObstacle(Direction.FORWARD));
		}
		int[] start_pos = robot.getCurrentPosition();
    	robot.jump();
    	int[] cur_pos = robot.getCurrentPosition();
		assertTrue(start_pos[0] != cur_pos[0] | start_pos[1] != cur_pos[1]);
	}
	
	/**
	 * Tests to see if jumping without a wall in front changes the current position of the robot.
	 * First, we drive until the robot has space to move. Next, we get the current position and store it.
	 * Then, we call jump (which should call move(1)). Last, we check to ensure that the position has changed.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testIfJumpWorksWithoutWall() throws Exception {
		while(robot.distanceToObstacle(Direction.FORWARD) < 1) {
			wizard.drive1Step2Exit();
		}
    	int[] start_pos = robot.getCurrentPosition();
    	robot.jump();
    	int[] cur_pos = robot.getCurrentPosition();
		assertTrue(start_pos[0] != cur_pos[0] | start_pos[1] != cur_pos[1]);
	}
	
	/**
	 * Tests to see if jumping actually consumes exactly 40 energy.
	 * This is done by moving the robot until it is facing an interior wall, checking the battery,
	 * and then jumping. If the battery level is different from what it was before, the test passes.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testJumpEnergyConsumption() throws Exception {
		robot.move(robot.distanceToObstacle(Direction.FORWARD));
		if (maze.getFloorplan().hasBorder(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1], robot.getCurrentDirection().getDirection()[0], robot.getCurrentDirection().getDirection()[1])) {
			robot.move(robot.distanceToObstacle(Direction.FORWARD));
		}
		robot.setBatteryLevel(3500);
		robot.jump();
		assertEquals(robot.getBatteryLevel(), 3460);
	}
	
	///////////////////////////////////////////////////////////////////
	/////////////////// Sensors   /////////////////////////////////////
	///////////////////////////////////////////////////////////////////

	/**
	 * Tests to see if the robot is not actually at the exit.
	 * Since the robot will be at the starting position, it will never be at the exit.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testIsAtExitFalse() throws Exception {
		assertFalse(robot.isAtExit());
	}
	
	/**
	 * Tests to see if the robot is actually at the exit.
	 * The wizard drives the robot to the exit and then checks.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testIsAtExitTrue() throws Exception {
		while (wizard.drive1Step2Exit());
		assertTrue(robot.isAtExit());
	}
	
	/*
	 * Testing to see if the robot is in a room is not necessary, as it relies on
	 * maze.isInRoom() and getCurrentPosition() only.
	 */
	
	/**
	 * Tests to see if the robot is stopped when it is out of energy.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testHasStoppedNoEnergy() throws Exception {
    	robot.setBatteryLevel(0);
		assertTrue(robot.hasStopped());
	}	
	
	/**
	 * Tests to see if the robot is stopped when it is facing a wall.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testHasStoppedWall() throws Exception {
    	robot.move(robot.distanceToObstacle(Direction.FORWARD));
		assertTrue(robot.hasStopped());
	}	
	
	/**
	 * Tests to see if a robot at (and facing) the exit can correctly see out into eternity.
	 * The wizard drives the robot to the end, orients it to face outside, and then performs the check.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testCanSeeThroughTheExitIntoEternityTrue() throws Exception {
		while (wizard.drive1Step2Exit());
		assertTrue(robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD));
	}
	
	/**
	 * Tests to see if a robot that is not at the exit can see out into eternity.
	 * Should return false.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testCanSeeThroughTheExitIntoEternityFalse() throws Exception {
		assertFalse(robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD));
	}
}
