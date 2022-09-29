package gui;

import generation.CardinalDirection;
import gui.Constants.UserInput;

/**
 * ReliableRobot implements Robot. This robot is reliable,
 * so it never has any failures in terms of operations. This robot
 * is capable of 90 degree left or right turns, sensing surrounding
 * walls, and moving forwards. It also has a battery level that gets
 * depleted through operations, with some costing more energy than
 * others. This robot will also stop if it hits an obstacle.
 * 
 * In order to perform turns, it works with RobotDriver to perform
 * turns and forward movements.
 * It relies on its DistanceSensor to sense its surroundings.
 * 
 * @author Matthew Cheng
 */

public class ReliableRobot implements Robot {
	
	protected Controller controller;
	
	private DistanceSensor forwardSensor = null;
	private DistanceSensor backwardSensor = null;
	private DistanceSensor leftSensor = null;
	private DistanceSensor rightSensor = null;
	
	private float[] batteryLevel = new float[1];
	
	private int odometer;
	
	// constructor
	public ReliableRobot(Controller c) {
		// initialize the battery level and odometer, set the controller
		batteryLevel[0] = c.battery;
		odometer = 0;
		setController(c);
		// add each of the robot's four sensors
		addDistanceSensor(new ReliableSensor(controller.getMazeConfiguration()),Direction.FORWARD);
		addDistanceSensor(new ReliableSensor(controller.getMazeConfiguration()),Direction.BACKWARD);
		addDistanceSensor(new ReliableSensor(controller.getMazeConfiguration()),Direction.LEFT);
		addDistanceSensor(new ReliableSensor(controller.getMazeConfiguration()),Direction.RIGHT);
	}
	
	/**
	 * Returns the robot's controller that it uses to solve the maze.
	 * @return controller that robot uses
	 */
	protected Controller getController() {
		return controller;
	}
	
	/**
	 * Allows access to the robot's specified sensor.
	 * @param direction Direction that the desired sensor is facing
	 * @return Sensor facing the given direction
	 */
	public DistanceSensor getSensor(Direction direction) {
		switch (direction) {
		case FORWARD:
			return forwardSensor;
		case LEFT:
			return leftSensor;
		case RIGHT:
			return rightSensor;
		case BACKWARD:
			return backwardSensor;
		default:
			return null;
		}
	}
	
	///////////////////////////////////////////////////////////////////
	/////////////////// Initial configuration of a robot   ////////////
	///////////////////////////////////////////////////////////////////
	
	@Override
	public void setController(Controller controller) {
		this.controller = controller;
	}

	@Override
	public void addDistanceSensor(DistanceSensor sensor, Direction mountedDirection) {
		// set the sensor's direction to be the specified direction
		sensor.setSensorDirection(mountedDirection);
		// then set the ReliableRobot's sensor to be the specified sensor
		switch (mountedDirection) {
		case RIGHT:
			rightSensor = sensor;
			break;
		case LEFT:
			leftSensor = sensor;
			break;
		case FORWARD:
			forwardSensor = sensor;
			break;
		case BACKWARD:
			backwardSensor = sensor;
			break;
		default:
			break;
		}
			
	}

	///////////////////////////////////////////////////////////////////
	/////////////////// Current location in game   ////////////////////
	///////////////////////////////////////////////////////////////////
	
	@Override
	public int[] getCurrentPosition() throws Exception {
		// returns the Robot's current position unless it is outside the maze
		int[] pos = controller.getCurrentPosition();
		if (!controller.getMazeConfiguration().isValidPosition(pos[0], pos[1])) {
			throw new Exception("Position is outside of maze.");
		}
		return pos;
	}

	@Override
	public CardinalDirection getCurrentDirection() {
		// returns the Robot's current direction
		return controller.getCurrentDirection();
	}

	///////////////////////////////////////////////////////////////////
	/////////////////// Battery and Energy consumption ////////////////
	///////////////////////////////////////////////////////////////////
	
	@Override
	public float getBatteryLevel() {
		return batteryLevel[0];
	}

	@Override
	public void setBatteryLevel(float level) {
		batteryLevel[0] = level;
	}

	@Override
	public float getEnergyForFullRotation() {
		return 12;
	}

	@Override
	public float getEnergyForStepForward() {
		return 6;
	}

	///////////////////////////////////////////////////////////////////
	/////////////////// Odometer, distance traveled    ////////////////
	///////////////////////////////////////////////////////////////////
	
	@Override
	public int getOdometerReading() {
		return odometer;
	}

	@Override
	public void resetOdometer() {
		odometer = 0;
	}

	///////////////////////////////////////////////////////////////////
	/////////////////// Actuators /////////////////////////////////////
	///////////////////////////////////////////////////////////////////
	
	@Override
	public void rotate(Turn turn) {
		
		/** 
		 * check to see if the robot has enough energy to perform a singular 90 degree turn
		 * if it does, proceed
		 */
		if (batteryLevel[0] >= getEnergyForFullRotation()/4) {
			// if a left turn...
			if (turn == Turn.LEFT) {
				// simulates the left arrow press
				controller.keyDown(UserInput.LEFT, 0);
				// and then subtracts the energy cost of turning
				batteryLevel[0] = batteryLevel[0] - (getEnergyForFullRotation()/4);
				
			}
			// if a right turn...
			else if(turn == Turn.RIGHT) {
				// simulates the right arrow press
				controller.keyDown(UserInput.RIGHT, 0);
				// and then subtracts the energy cost of turning
				batteryLevel[0] = batteryLevel[0] - (getEnergyForFullRotation()/4);
			}
			// if a half turn...
			else if(turn ==Turn.AROUND) {
				// simulates the first right arrow press
				controller.keyDown(UserInput.RIGHT, 0);
				// then subtracts the energy cost of turning
				batteryLevel[0] = batteryLevel[0] - (getEnergyForFullRotation()/4);
				// if the robot still has the energy to make another turn...
				if (batteryLevel[0] >= getEnergyForFullRotation()/4) {
					// repeats to turn a second time
					controller.keyDown(UserInput.RIGHT, 0);
					batteryLevel[0] = batteryLevel[0] - (getEnergyForFullRotation()/4);
				}
				else {
					// if not, then the robot must have stopped
					// hasStopped();
				}
			}
		}
		else {
			// if not, then the robot must have stopped
			// hasStopped();
		}		
	}

	@Override
	public void move(int distance) {

		// cannot move negative distance
		if (distance < 0) {
			throw new IllegalArgumentException("Distance to move cannot be negative.");
		}
		
		/**
		 * move the robot forwards one step at a time
		 * after each step, check the energy to make sure another step can be taken
		 * also check to make sure that the robot has not stopped
		 * 
		 * continue this loop until the robot is out of energy, is stopped by a wall,
		 * or completes its desired amount of steps
		 */
		for (int i = 0; i < distance; i++) {
			if (!hasStopped() & batteryLevel[0] > getEnergyForStepForward()-1) {
				odometer++;
				batteryLevel[0] = batteryLevel[0] - getEnergyForStepForward();
				controller.keyDown(UserInput.UP, 0);
			}
			else {
				break;
			}
		}
	}

	@Override
	public void jump() {
		/**
		 * checks to see if the Robot has enough energy to perform the jump
		 * or if it is stopped
		 * if not, stop the robot
		 */ 
		if (batteryLevel[0] < 40) {
			//hasStopped();
			return;
		}
		
		// get the current position of the robot
		int[] coordinates = {0,0};
		try {
			coordinates = getCurrentPosition();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int x = coordinates[0];
		int y = coordinates[1];
		
		// get the current direction of the robot
		int[] direction = getCurrentDirection().getDirection();
		int dx = direction[0];
		int dy = direction[1];
		 
		// if not an exterior wall, then we can safely perform the jump (provided an inner wall)
		if (controller.getMazeConfiguration().hasWall(x, y, getCurrentDirection()) && controller.getMazeConfiguration().isValidPosition(x+dx, y+dy)) {
			// Uses jump to hop over the wall
			controller.keyDown(UserInput.JUMP, 0);
			// Subtracts the energy usage
			batteryLevel[0] = batteryLevel[0] - 40;
			// Increments the odometer
			odometer++;
		}
		
		//  if no wall at all, move instead
		else if (this.controller.getMazeConfiguration().hasWall(x, y, getCurrentDirection()) == false) {
			this.move(1);
		}
	}

	///////////////////////////////////////////////////////////////////
	/////////////////// Sensors   /////////////////////////////////////
	///////////////////////////////////////////////////////////////////
	
	@Override
	public boolean isAtExit() {
		// gets the exit position
		int[] exit = controller.getMazeConfiguration().getExitPosition();
		int[] cur = {-1,-1};
		// gets current position
		try {
			cur = getCurrentPosition();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// return true if the positions are the same
		if (exit[0] == cur[0] & exit[1] == cur[1]) {
			return true;
		}
		// false otherwise
		return false;
	}
	
	@Override
	public boolean isInsideRoom() {
		// using the maze, check to see if the cell is inside a room
		// return true if it is
		try {
			return controller.getMazeConfiguration().isInRoom(getCurrentPosition()[0], getCurrentPosition()[1]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// otherwise, return false
		return false;
	}

	@Override
	public boolean hasStopped() {
		// get the coordinates of the robot
		int[] coordinates = {0,0};
		try {
			coordinates = getCurrentPosition();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int x = coordinates[0];
		int y = coordinates[1];
		
		// true if battery is 0 or lower
		if (batteryLevel[0] <= 0) {
			// robot has run out of energy, so the player has now lost
			// this.controller.switchFromPlayingToWinning(-2);
			return true;
		}
		
		// true if there is a wall in front of the robot
		if(this.controller.getMazeConfiguration().getFloorplan().hasWall(x, y, this.getCurrentDirection())) {
			// robot has crashed into a wall, so the player has now lost
			// this.controller.switchFromPlayingToWinning(-2);
			return true;
		}
		
		// since we have reached this point, must be false 
		return false;
	}

	@Override
	public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {
		// makes sure that the sensor exists before use
		// uses ReliableSensor to get the distance to an obstacle in the desired direction
		int distance = -1;
		switch (direction) {
		case FORWARD:
			if (forwardSensor == null) {
				throw new UnsupportedOperationException("Robot has no forward sensor.");
			}
			try {
				distance = forwardSensor.distanceToObstacle(controller.getCurrentPosition(), getCurrentDirection(), batteryLevel);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case BACKWARD:
			if (backwardSensor == null) {
				throw new UnsupportedOperationException("Robot has no backward sensor.");
			}
			try {
				distance = backwardSensor.distanceToObstacle(getCurrentPosition(), getCurrentDirection(), batteryLevel);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case LEFT:
			if (leftSensor == null) {
				throw new UnsupportedOperationException("Robot has no left sensor.");
			}
			try {
				distance = leftSensor.distanceToObstacle(getCurrentPosition(), getCurrentDirection(), batteryLevel);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case RIGHT:
			if (rightSensor == null) {
				throw new UnsupportedOperationException("Robot has no right sensor.");
			}
			try {
				distance = rightSensor.distanceToObstacle(getCurrentPosition(), getCurrentDirection(), batteryLevel);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		return distance;
	}

	@Override
	public boolean canSeeThroughTheExitIntoEternity(Direction direction) throws UnsupportedOperationException {
		// if the distance to an obstacle in the direction is the max Integer, then return true
		if (forwardSensor == null) {
			throw new UnsupportedOperationException("Robot has no forward sensor.");
		}
		
		try {
			if (forwardSensor.distanceToObstacle(getCurrentPosition(), getCurrentDirection(), batteryLevel) == Integer.MAX_VALUE) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// otherwise, return false
		return false;
	}

	@Override
	public void startFailureAndRepairProcess(Direction direction, int meanTimeBetweenFailures, int meanTimeToRepair)
			throws UnsupportedOperationException {
		if (false) throw new UnsupportedOperationException("ReliableRobot does not have a failure and repair process.");
	}

	@Override
	public void stopFailureAndRepairProcess(Direction direction) throws UnsupportedOperationException {
		if (false) throw new UnsupportedOperationException("ReliableRobot does not have a failure and repair process.");
	}

}
