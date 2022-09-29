package gui;

import java.util.ArrayList;

import generation.Maze;
import gui.Robot.Direction;
import gui.Robot.Turn;

/**
 * This class is one of the two possible driver algorithms that the user may select
 * to run an automated game.
 * WallFollower implements Robot Driver.
 * RobotDriver works together with Robot to automatically move the robot through
 * the maze with the intention of solving it.
 * RobotDriver also gets its maze information from Maze.
 *  
 * WallFollower attempts to cling to the left edge of the maze in order to 
 * eventually find the exit. It relies on its sensors to determine where the walls
 * are in order to turn and follow them whenever possible.
 * 
 * Unlike Wizard, this is not an efficient algorithm.
 *  
 * @author Matthew Cheng
 */

public class WallFollower implements RobotDriver {

	private Robot robot;
	
	private Maze maze;
	
	private float startBattery;
	
	// this remembers each position that the robot has visited
	private ArrayList<ArrayList<Integer>> history = new ArrayList<ArrayList<Integer>>();
	
	// this remembers duplicates, one duplicate is allowed in a room, but never more
	private ArrayList<ArrayList<Integer>> duplicates = new ArrayList<ArrayList<Integer>>();
	
	@Override
	public void setRobot(Robot r) {
		robot = r;
		startBattery = robot.getBatteryLevel();
	}
	
	@Override
	public Robot getRobot() {
		return robot;
	}

	@Override
	public void setMaze(Maze maze) {
		this.maze = maze;
	}
	
	@Override
	public Maze getMaze() {
		return maze;
	}

	/**
	 * If the robot has enough energy, WallFollower uses its sensors to follow the
	 * left wall from its current position to the exit of the maze.
	 * When the robot has reached the exit position and its forward
	 * direction points to the exit, the search terminates and 
	 * the method returns true.
	 * If the robot failed due to lack of energy or crashed, the method
	 * throws an exception.
	 * If the method determines that it is not capable of finding the
	 * exit it returns false, for instance, if it determines it runs
	 * in a cycle and can't resolve this.
	 * @return true if driver successfully reaches the exit, false otherwise
	 * @throws Exception thrown if robot stopped due to some problem, e.g. lack of energy
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		/** 
		 * drive 1 step to the exit until the exit position is reached, the robot
		 * runs out of battery, or the robot reaches a loop (cell has been visited)
		 */
		try {
			while (robot.getBatteryLevel() > 0 & drive1Step2Exit()) {	
			}
		} catch (InfiniteLoopException i) {
			throw new InfiniteLoopException("Unable to reach exit due to infinite loop.");
		} catch (Exception e) {
			throw new Exception("Not enough battery to reach exit");
		}
	
		// if we have reached this point, we can move forward 1 to complete the maze
		if (robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD)) {
			this.robot.move(1);
			return true;
		}
		else {
			throw new Exception("Not enough battery to reach exit");
		}
	}

	/**
	 * Drives the robot one step towards the exit following
	 * its solution strategy given the robot's energy supply lasts
	 * long enough. It returns true if the driver successfully moved
	 * the robot from its current location to an adjacent
	 * location.
	 * At the exit position, it rotates the robot 
	 * such that if faces the exit in its forward direction
	 * and returns false. 
	 * If the robot failed due to lack of energy or crashed, the method
	 * throws an exception. 
	 * @return true if it moved the robot to an adjacent cell, false otherwise
	 * @throws Exception thrown if robot stopped due to some problem, e.g. lack of energy
	 */
	@Override
	public boolean drive1Step2Exit() throws Exception {
		// check to see if the current position has been visited twice before
		// if it has and is in a room, then there must be a loop, so an exception is thrown
		ArrayList<Integer> curPos = new ArrayList<Integer>(2);
		curPos.add(robot.getCurrentPosition()[0]);
		curPos.add(robot.getCurrentPosition()[1]);
		if (history.contains(curPos)) {
			if (maze.isInRoom(robot.getCurrentPosition()[0],robot.getCurrentPosition()[1])) {
				if (duplicates.contains(curPos)) {
					throw new InfiniteLoopException("Unable to reach exit due to infinite loop.");
				}
				else {
					duplicates.add(curPos);
				}
			}
		}
		else {
			history.add(curPos);
		}
		
		// check to see if robot is at exit
		if (robot.isAtExit()) {
			stopProcesses();
			// if so, check to see if the robot is facing the exit
			if (this.robot.distanceToObstacle(Direction.FORWARD) != Integer.MAX_VALUE) {
				// if not, orient the robot so that it is facing the exit
				if (this.robot.distanceToObstacle(Direction.RIGHT) == Integer.MAX_VALUE) {
					this.robot.rotate(Turn.RIGHT);
				}
				else if (this.robot.distanceToObstacle(Direction.LEFT) == Integer.MAX_VALUE) {
					this.robot.rotate(Turn.LEFT);
				}
				else if (this.robot.distanceToObstacle(Direction.BACKWARD) == Integer.MAX_VALUE) {
					this.robot.rotate(Turn.AROUND);
				}
			}
			
			// if not enough energy to turn, throw an exception
			if (!robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD)) {
				throw new Exception("Not enough energy to turn to exit.");
			}
				
			// since we have reached this point with no exceptions, we can now safely return false
			return false;
		}
		
		// if not at the exit, check to see if the robot has enough energy to move 1 space
		if (robot.getBatteryLevel() < 6) {
			// if not, throw an exception
			throw new Exception("Robot does not have enough energy to drive to the next step.");
		}
		
		// if no wall, then turn left and then move a space
		if (robot.distanceToObstacle(Direction.LEFT) != 0) {
			robot.rotate(Turn.LEFT);
		}
		while (robot.distanceToObstacle(Direction.FORWARD) == 0) {
			robot.rotate(Turn.RIGHT);
		}
		
		// now that we know we have a left wall and forward space...
		// move 1 space, remember the space, and return true
		robot.move(1);
		
		return true;
	}

	/**
	 * This method attempts to stop the failure and repair processes for each of the 
	 * robot's sensors. If the sensor is unreliable, it will stop the process and if
	 * it is reliable, then the exception will be caught.
	 */
	private void stopProcesses() {
		try {
			robot.stopFailureAndRepairProcess(Direction.FORWARD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			robot.stopFailureAndRepairProcess(Direction.BACKWARD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			robot.stopFailureAndRepairProcess(Direction.LEFT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			robot.stopFailureAndRepairProcess(Direction.RIGHT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public float getEnergyConsumption() {
		return startBattery-robot.getBatteryLevel();
	}

	@Override
	public int getPathLength() {
		return robot.getOdometerReading();
	}

}
