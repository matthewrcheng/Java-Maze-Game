//package gui;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import generation.Maze;
import generation.MazeFactory;
import generation.Order;
import generation.Stuborder;
import generation.Order.Builder;
import gui.Robot.Direction;
import gui.Robot.Turn;

/**
 * Class:WizardTest
 * Responsibilities: Tests if Wizard's commands work properly
 * Collaborators: ReliableRobot, Controller, CardinalDirection, Floorplan, MazeFactory, and Stuborder
 * 
 * 
 * 
 * @author Matthew Cheng
 *
 */
class WizardTest {
	// declare all variables that we may use for tests
	Maze maze;
	String[] testcontroller;
	MazeFactory factory;
	int skill = 2;
    boolean perfect = false;
    Builder builder = Order.Builder.DFS;
    MazeApplication testApp;
    Controller controller;
    Robot robot;
    Wizard wizard;
    DistanceSensor sensor;
	
    /**
     * Initialize each of our variables before running each test as to not repeat code
     */
	@BeforeEach
	public final void SetUp() {
		//instantiate MazeFactory
	    factory = new MazeFactory();
		Stuborder stuborder = new Stuborder(10, skill, perfect, builder);
		factory.order(stuborder);
		factory.waitTillDelivered();
		//get maze configuration 
		maze = stuborder.maze;
		String[] testcontroller = {"-d","Wizard"};
		testApp = new MazeApplication(testcontroller);
		controller = testApp.createController(testcontroller);
		controller.turnOffGraphics();
		controller.test = true;
		controller.switchFromGeneratingToPlaying(maze);
		robot = new ReliableRobot(controller);
		wizard = new Wizard(); 
    	wizard.setRobot(robot);
    	wizard.setMaze(maze);
    	sensor = new ReliableSensor(maze);
	}
	
	/**
	 * Tests to make sure that setRobot() actually sets the robot and that it is no longer null.
	 */
	@Test
	public final void testSetRobot() {
		assertNotEquals(wizard.getRobot(),null);
	}
	
	/**
	 * Tests to make sure that setMaze() actually sets the maze and that it is no longer null.
	 */
	@Test
	public final void testSetMaze() {
		assertNotEquals(wizard.getMaze(),null);
	}

	/**
	 * Tests to make sure that the robot can drive to the exit and then return true.
	 * The robot should now be one step outside the maze, so we check to see if the 
	 * position is invalid.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testDrive2Exit() throws Exception {
		assertTrue(wizard.drive2Exit());
		Exception exception = assertThrows(Exception.class, () -> maze.isValidPosition(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1]));
		assertEquals("java.lang.Exception: Position is outside of maze.", exception.toString());
	}
	
	/**
	 * Tests to make sure the robot cannot successfully drive to the exit and throws an exception.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testDrive2ExitNoEnergy() throws Exception {
		robot.setBatteryLevel(0);
		Exception exception = assertThrows(Exception.class, () -> wizard.drive2Exit());
		assertEquals("java.lang.Exception: Not enough battery to reach exit", exception.toString());
	}

	/**
	 * Tests to see if the robot will change position and increment the odometer if facing forwards.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testDrive1StepForwards() throws Exception {
    	int[] start_pos = controller.getMazeConfiguration().getStartingPosition();
    	wizard.drive1Step2Exit();
    	int[] cur_pos = robot.getCurrentPosition();
		assertTrue(start_pos[0] != cur_pos[0] || start_pos[1] != cur_pos[1]);
		assertEquals(robot.getOdometerReading(),1);
	}
	
	/**
	 * Tests to see if the robot will change position and increment the odometer if facing right.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testDrive1StepRight() throws Exception {
		robot.rotate(Turn.RIGHT);
    	int[] start_pos = controller.getMazeConfiguration().getStartingPosition();
    	wizard.drive1Step2Exit();
    	int[] cur_pos = robot.getCurrentPosition();
		assertTrue(start_pos[0] != cur_pos[0] || start_pos[1] != cur_pos[1]);
		assertEquals(robot.getOdometerReading(),1);
	}
	
	/**
	 * Tests to see if the robot will change position and increment the odometer if facing left.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testDrive1StepLeft() throws Exception {
		robot.rotate(Turn.LEFT);
    	int[] start_pos = controller.getMazeConfiguration().getStartingPosition();
    	wizard.drive1Step2Exit();
    	int[] cur_pos = robot.getCurrentPosition();
		assertTrue(start_pos[0] != cur_pos[0] || start_pos[1] != cur_pos[1]);
		assertEquals(robot.getOdometerReading(),1);
	}
	
	/**
	 * Tests to see if the robot will change position and increment the odometer if facing backwards.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testDrive1StepBackwards() throws Exception {
		robot.rotate(Turn.AROUND);
    	int[] start_pos = controller.getMazeConfiguration().getStartingPosition();
    	wizard.drive1Step2Exit();
    	int[] cur_pos = robot.getCurrentPosition();
		assertTrue(start_pos[0] != cur_pos[0] || start_pos[1] != cur_pos[1]);
		assertEquals(robot.getOdometerReading(),1);
	}
	
	/**
	 * Tests to see if the wizard can correctly drive 1 step to the exit until it gets there.
	 * Then, makes sure that the robot is actually at the exit and is oriented correctly.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testDrive1Step2Exit() throws Exception {
		while (wizard.drive1Step2Exit());
		assertTrue(robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD));
	}
	
	/**
	 * Tests to see if the wizard is unable to turn to the exit when it is out of energy 
	 * and then correctly throws an exception.
	 * @throws Exception
	 */
	@Test
	public final void testDrive1Step2ExitCantTurn() throws Exception {
		while (wizard.drive1Step2Exit());
		robot.rotate(Turn.AROUND);
		robot.setBatteryLevel(0);
		Exception exception = assertThrows(Exception.class, () -> wizard.drive1Step2Exit());
		assertEquals("java.lang.Exception: Not enough energy to turn to exit.", exception.toString());
	}
	
	/**
	 * Regardless of orientation, has the robot, has the robot move 1 space and then spin around,
	 * costing 12 energy. Then, tests to make sure getEnergyConsumption correctly returns the
	 * amount of energy consumed (12). This covers consumption for moving and turning.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testGetEnergyConsumption() throws Exception {
		if (robot.distanceToObstacle(Direction.FORWARD) >= 1) {
			robot.setBatteryLevel(3500);
			robot.move(1);
		}
		else if (robot.distanceToObstacle(Direction.LEFT) >= 1) {
			robot.rotate(Turn.LEFT);
			robot.setBatteryLevel(3500);
			robot.move(1);
		}
		else if (robot.distanceToObstacle(Direction.RIGHT) >= 1) {
			robot.rotate(Turn.RIGHT);
			robot.setBatteryLevel(3500);
			robot.move(1);
		}
		else {
			robot.rotate(Turn.AROUND);
			robot.setBatteryLevel(3500);
			robot.move(1);
		}
		robot.rotate(Turn.AROUND);
		assertEquals(wizard.getEnergyConsumption(), 12);
	}
	
	/**
	 * Tests to see if getPathLength correctly returns the same reading that the odometer has
	 * after solving the maze.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testGetPathLengthMove() throws Exception {
    	wizard.drive2Exit();
		assertEquals(wizard.getPathLength(), robot.getOdometerReading());
	}
}