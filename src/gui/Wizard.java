package gui;

import generation.CardinalDirection;
import generation.Maze;
import gui.Robot.Turn;
import gui.Robot.Direction;

/**
 * This class is one of the two possible driver algorithms that the user may select
 * to run an automated game.
 * Wizard implements Robot Driver.
 * RobotDriver works together with Robot to automatically move the robot through
 * the maze with the intention of solving it.
 * RobotDriver also gets its maze information from Maze.
 *  
 * Wizard attempts to get out of the maze as quickly as possible by using the
 * information from the Maze's setMaze to determine where to move in order to 
 * get to the exit. Because of this, it also does not need to use a sensor to
 * decide what to do, but it must not drive into a wall. This serves as a
 * baseline algorithm to see how efficiently algorithms can perform in terms 
 * of energy consumption and path length. (from Project 3 directions)
 *  
 * @author Matthew Cheng
 */

public class Wizard implements RobotDriver {

	private Robot robot;
	
	private Maze maze;
	
	private float startBattery;
	
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
	 * If the robot has enough energy, Wizard uses its map
	 * to drive the robot from its current position to the 
	 * exit of the maze (providing it exists).
	 * When the robot reached the exit position and its forward
	 * direction points to the exit the search terminates and 
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
		 * check to see if the exit is reachable
		 * if not, return false
		 */
		if (getPathLength() == Integer.MAX_VALUE) {
			return false;
		}
		/** 
		 * if so, drive 1 step to the exit until the exit position is reached
		 * or until the robot runs out of battery
		 */
		try {
			while (robot.getBatteryLevel() > 0 & drive1Step2Exit()) {	
			}
		} catch (Exception e) {
			throw new Exception("Not enough battery to reach exit");
		}
		// Move forward 1 to complete the maze
		if (robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD)) {
			robot.move(1);
			return true;
		}
		else {
			throw new Exception("Not enough battery to reach exit");
		}
	}

	/**
	 * Drives the robot one step towards the exit following
	 * its solution strategy and given the exists and 
	 * given the robot's energy supply lasts long enough.
	 * It returns true if the driver successfully moved
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
		
		//checks to see if the robot is at the exit position
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
		
		else {
			// if not at the exit position, check to see if the robot has enough energy to move 1 space
			if (robot.getBatteryLevel() < 6) {
				// if not, throw an exception
				throw new Exception("Robot does not have enough energy to drive to the next step.");
			}
			
			// get the current position of the robot
			int[] position = robot.getCurrentPosition();
			int x = position[0];
			int y = position[1];
			
			// get the difference between current and closer neighbor
			int[] closer_cell = maze.getNeighborCloserToExit(x, y);
			int dx = closer_cell[0]-x;
			int dy = closer_cell[1]-y;
			
			// using this difference, get the direction of the closer neighbor
			CardinalDirection new_direction = CardinalDirection.getDirection(dx, dy);
			
			// set robot to face the neighbor
			while (robot.getCurrentDirection()!= new_direction) {
				// depending on the current direction, the robot may need to turn right or left
				// we check the cheapest way in order to conserve energy
				switch(robot.getCurrentDirection()) {
				case North:
					if (new_direction == CardinalDirection.East) {
						robot.rotate(Turn.LEFT);
					}
					else {
						robot.rotate(Turn.RIGHT);
					}
					break;
				case East:
					if (new_direction == CardinalDirection.South) {
						robot.rotate(Turn.LEFT);
					}
					else {
						robot.rotate(Turn.RIGHT);
					}
					break;
				case South:
					if (new_direction == CardinalDirection.West) {
						robot.rotate(Turn.LEFT);
					}
					else {
						robot.rotate(Turn.RIGHT);
					}
					break;
				case West:
					if (new_direction == CardinalDirection.North) {
						robot.rotate(Turn.LEFT);
					}
					else {
						robot.rotate(Turn.RIGHT);
					}
					break;
				default:
					break;
				}
			}
			
			// now that we are oriented correctly, move 1 space and return true
			robot.move(1);
			return true;
		}
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
		return maze.getDistanceToExit(maze.getStartingPosition()[0], maze.getStartingPosition()[1]);
	}
}
