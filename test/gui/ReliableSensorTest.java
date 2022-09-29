//package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import generation.Maze;
import generation.MazeFactory;
import generation.Order;
import generation.Stuborder;
import gui.Robot.Turn;
import generation.Order.Builder;

/**
 * ReliableSensorTest runs a series of tests on ReliableSensor to ensure that its methods work properly. It works together
 * with Wizard, ReliableRobot, Controller, MazeFactory, MazeApplication, Order, Stuborder, Maze, and Robot to accomplish this.
 * 
 * @author Matthew Cheng
 *
 */

public class ReliableSensorTest {
	
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
    ReliableSensor sensor;
	
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
    	// get sensor
    	sensor = new ReliableSensor(maze);
	}
		
	/**
	 * Tests for distance to a forward obstacle
	 * Tries to move robot forward the specified distance and checks to see if
	 * a. it did move the correct amount of spaces without hitting a wall and 
	 * b. it is now facing a wall
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDistancetoObstacle() throws Exception {
		int dist = sensor.distanceToObstacle(robot.getCurrentPosition(),robot.getCurrentDirection(),new float[] {robot.getBatteryLevel()});
		robot.setBatteryLevel(3500);
		robot.move(dist);
		assertEquals(robot.getBatteryLevel(), 3500 - robot.getEnergyForStepForward()*dist);
		assertTrue(robot.hasStopped());
	}
	
	/**
	 * Tests for distance to obstacle on the right.
	 * Turns robot to the right and then tries to move robot forward the specified distance and checks to see if
	 * a. it did move the correct amount of spaces without hitting a wall and 
	 * b. it is now facing a wall
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDistancetoObstacleClockwise() throws Exception {
		int dist = sensor.distanceToObstacle(robot.getCurrentPosition(),robot.getCurrentDirection().rotateClockwise().oppositeDirection(),new float[] {robot.getBatteryLevel()});
		robot.rotate(Turn.RIGHT);
		robot.setBatteryLevel(3500);
		robot.move(dist);
		assertEquals(robot.getBatteryLevel(), 3500 - robot.getEnergyForStepForward()*dist);
		assertTrue(robot.hasStopped());
	}
	
	/**
	 * Tests for distance to obstacle on the left.
	 * Turns robot to the left and then tries to move robot forward the specified distance and checks to see if
	 * a. it did move the correct amount of spaces without hitting a wall and 
	 * b. it is now facing a wall
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDistancetoObstacleCounterClockwise() throws Exception {
		int dist = sensor.distanceToObstacle(robot.getCurrentPosition(),robot.getCurrentDirection().rotateClockwise(),new float[] {robot.getBatteryLevel()});
		robot.rotate(Turn.LEFT);
		robot.setBatteryLevel(3500);
		robot.move(dist);
		assertEquals(robot.getBatteryLevel(), 3500 - robot.getEnergyForStepForward()*dist);
		assertTrue(robot.hasStopped());
	}

	/**
	 * Tests for the correct return value of distanceToObstacle()
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testCostofDistanceToObstacle() throws Exception {
		assertTrue(sensor.getEnergyConsumptionForSensing() == 1);
	}
	
	/**
	 * Tests to see if the correct PowerFailure exception is thrown when the
	 * robot does not have enough energy to sense for an obstacle.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testDistanceToObstacleNoEnergy() throws Exception {
		robot.setBatteryLevel(0);
		Exception exception = assertThrows(Exception.class, () -> sensor.distanceToObstacle(robot.getCurrentPosition(), robot.getCurrentDirection(), new float[] {robot.getBatteryLevel()}));
		assertEquals("java.lang.Exception: PowerFailure", exception.toString());
	}
	
	/**
	 *  Tests to see if the correct IndexOutOfBoundsException is thrown when
	 *  the robot has enough energy to sense for an obstacle, but does not
	 *  have enough energy to actually move.
	 *  
	 * @throws Exception
	 */
	@Test
	public final void testDistanceToObstacleNoEnergyToWalk() throws Exception {
		robot.setBatteryLevel(6);
		Exception exception = assertThrows(Exception.class, () -> sensor.distanceToObstacle(robot.getCurrentPosition(), robot.getCurrentDirection(), new float[] {robot.getBatteryLevel()}));
		assertEquals("java.lang.IndexOutOfBoundsException: Power supply is out of range.", exception.toString());
	}
	
	/**
	 *  Tests if an IllegalArgumentException is thrown when one of the parameters is null.
	 *  
	 * @throws Exception
	 */
	@Test
	public final void testDistanceToObstacleNull() throws Exception {
		Exception exception = assertThrows(Exception.class, () -> sensor.distanceToObstacle(null, robot.getCurrentDirection(), new float[] {robot.getBatteryLevel()}));
		assertEquals("java.lang.IllegalArgumentException: Cannot accept null parameters", exception.toString());
	}
	
	/**
	 * Tests if an IllegalArgumentException is thrown when the position is invalid.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testDistanceToObstacleInvalid() throws Exception {
		int[] invalid = {-1,-1};
		Exception exception = assertThrows(Exception.class, () -> sensor.distanceToObstacle(invalid, robot.getCurrentDirection(), new float[] {robot.getBatteryLevel()}));
		assertEquals("java.lang.IllegalArgumentException: Cannot accept invalid position", exception.toString());
	}
	
	/**
	 * Tests to see if distance is max integer value when looking out into infinity.
	 * First, moves the robot to the exit position and orients it so that it is looking
	 * through the exit. Then, has the robot sense in the forward direction to get the distance.
	 * @throws Exception
	 */
	@Test
	public final void testDistanceToObstacleInfinity() throws Exception {
		robot.setBatteryLevel(Integer.MAX_VALUE);
		while (wizard.drive1Step2Exit());
		assertEquals(sensor.distanceToObstacle(robot.getCurrentPosition(), robot.getCurrentDirection(), new float[] {robot.getBatteryLevel()}), Integer.MAX_VALUE);
	}
}
