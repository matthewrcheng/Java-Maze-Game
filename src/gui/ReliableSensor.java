package gui;

import generation.CardinalDirection;
import generation.Maze;
import gui.Robot.Direction;

/**
 *  ReliableSensor implements DistanceSensor such that it can
 *  check for walls in all four cardinal directions. Of course, this
 *  also consumes energy, so it calculates the amount of energy each
 *  action takes up.
 *  
 *  As a reliable sensor, this will always be operational, so we 
 *  never have to worry about it failing.
 *  
 *  It works with Robot by allowing the robot to sense what its
 *  current surroundings are.
 * 
 * @author Matthew Cheng
 */

public class ReliableSensor implements DistanceSensor {
	
	private Maze maze;
	private Direction direction;
	
	public ReliableSensor(Maze maze) {
    	setMaze(maze);
        direction = Direction.FORWARD;
	}
	
	@Override
	public int distanceToObstacle(int[] currentPosition, CardinalDirection currentDirection, float[] powersupply)
			throws Exception {
		// if any of the parameters are null, throw an exception
		if (currentPosition == null | currentDirection == null | powersupply == null) {
			throw new IllegalArgumentException("Cannot accept null parameters");
		}
		
		// if the position is not valid, throw an exception
		if (!maze.isValidPosition(currentPosition[0], currentPosition[1])) {
			throw new IllegalArgumentException("Cannot accept invalid position");
		}
		
		// if the power supply is insufficient for the operation (less than 1), also throw an exception.
		if (powersupply[0] <= getEnergyConsumptionForSensing()) {
			throw new Exception("PowerFailure");
		}
		
		// we may proceed, so subtract the energy required to sense
		powersupply[0] = powersupply[0] - getEnergyConsumptionForSensing();
		
		// gets the position
		int distance = 0;
		int[] position = currentPosition;
		int x = position[0];
		int y = position[1];
		
		switch(direction) {
			case FORWARD:
				break;
			case LEFT:
				currentDirection = currentDirection.rotateClockwise();
				break;
			case RIGHT:
				currentDirection = currentDirection.rotateClockwise().oppositeDirection();
				break;
			case BACKWARD:
				currentDirection = currentDirection.oppositeDirection();
				break;
		}
		
		// gets the cardinal direction
		int[] Direction = currentDirection.getDirection();
		int dx = Direction[0];
		int dy = Direction[1];
		
		// if we are at the exit and facing it, then return the max valued integer
		if (maze.getFloorplan().isExitPosition(x, y) && !maze.isValidPosition(x+dx,y+dy) && maze.getFloorplan().hasNoWall(x, y, currentDirection)) {
			return Integer.MAX_VALUE;
		}
		
		// while current position doesn't have wall in front or a border and there is enough energy
		while (!maze.hasWall(x, y, currentDirection) && !maze.getFloorplan().hasBorder(x, y, dx, dy) && powersupply[0] >= 6) {
			// sense the spot forward from the robot
			x=x+dx;
			y=y+dy;
			distance++;
			// account for how much energy the robot would spend
			powersupply[0] = powersupply[0] - 6;
		}
		// if the robot runs out of power to move before reaching the destination, throw an exception
		if (powersupply[0] < 6) {
			throw new IndexOutOfBoundsException("Power supply is out of range.");
		}
		
		// otherwise, we have made it and can return distance to obstacle from current cell
		return distance;
	}

	@Override
	public void setMaze(Maze maze) {
		this.maze = maze;
	}

	@Override
	public void setSensorDirection(Direction mountedDirection) {
		direction = mountedDirection;
	}

	@Override
	public float getEnergyConsumptionForSensing() {
		return 1;
	}

	@Override
	public boolean isOperational() {
		return true;
	}

	@Override
	public void startFailureAndRepairProcess(int meanTimeBetweenFailures, int meanTimeToRepair)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException("ReliableSensor does not have a failure and repair process.");
	}

	@Override
	public void stopFailureAndRepairProcess() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("ReliableSensor does not have a failure and repair process.");
	}

}
