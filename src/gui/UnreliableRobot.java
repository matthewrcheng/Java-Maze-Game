package gui;

/**
 * UnreliableRobot extends ReliableRobot which implements Robot. This
 * robot is unreliable, so it sometimes encounters operational failures.
 * When this happens, it must either adapt or pause and repair itself.
 * This robot is capable of 90 degree left or right turns, sensing surrounding
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

public class UnreliableRobot extends ReliableRobot {

	public UnreliableRobot(Controller c) {
		// initialize the battery level and odometer, set the controller
		super(c);
		// add each of the robot's four sensors
		// if the sensor is reliable, it has already been added by super()
		// if the sensor is unreliable, it must be added to replace the reliable one
		// we also wait 1.3 seconds in between if they're unreliable
		System.out.println("Robot is preparing sensors.");
		if (!getController().hasReliableForward()) {
			addDistanceSensor(new UnreliableSensor(getController().getMazeConfiguration()),Direction.FORWARD);
			startFailureAndRepairProcess(Direction.FORWARD,4000,2000);
			try {
				Thread.sleep(1300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!getController().hasReliableLeft()) {
			addDistanceSensor(new UnreliableSensor(getController().getMazeConfiguration()),Direction.LEFT);
			startFailureAndRepairProcess(Direction.LEFT,4000,2000);
			try {
				Thread.sleep(1300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!getController().hasReliableRight()) {
			addDistanceSensor(new UnreliableSensor(getController().getMazeConfiguration()),Direction.RIGHT);
			startFailureAndRepairProcess(Direction.RIGHT,4000,2000);
			try {
				Thread.sleep(1300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!getController().hasReliableBackward()) {
			addDistanceSensor(new UnreliableSensor(getController().getMazeConfiguration()),Direction.BACKWARD);
			startFailureAndRepairProcess(Direction.BACKWARD,4000,2000);
		}
		//controller.keyDown(Constants.UserInput.ZOOMIN,0);
	}
	
	@Override
	public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {
		
		// makes sure that the sensor exists before use
		// uses ReliableSensor to get the distance to an obstacle in the desired direction
		int distance = -1;
		switch (direction) {
		case FORWARD:
			if (getSensor(Direction.FORWARD) == null) {
				throw new UnsupportedOperationException("Robot has no forward sensor.");
			}
			// check if the forward sensor is not working
			if (!getController().hasReliableForward()) {
				if (!((UnreliableSensor) getSensor(Direction.FORWARD)).isOperational()) {
					// if not, check the other sensors
					// left or right first doesn't matter, but they are both preferable to backward
					if (((UnreliableSensor) getSensor(Direction.LEFT)).isOperational()) {
						distance = checkRight(Direction.LEFT);
					}
					else if (((UnreliableSensor) getSensor(Direction.RIGHT)).isOperational()) {
						distance = checkLeft(Direction.RIGHT);
					}
					// if neither side sensor, we will resort to the more expensive backward sensor
					else if (((UnreliableSensor) getSensor(Direction.BACKWARD)).isOperational()) {
						distance = checkAround(Direction.BACKWARD);
					}
					// if no sensors are working, we will wait until a sensor is responsive
					else {
						while (!((UnreliableSensor) getSensor(Direction.FORWARD)).isOperational());
						distance = checkFront(Direction.FORWARD);
					}
				}
				else {
					distance = checkFront(Direction.FORWARD);
				}
			}
			// if the forward sensor is working, then we can proceed normally
			else {
				distance = checkFront(Direction.FORWARD);
			}
			break;
		case BACKWARD:
			if (getSensor(Direction.BACKWARD) == null) {
				throw new UnsupportedOperationException("Robot has no backward sensor.");
			}
			// check if the backward sensor is not working
			if (!getController().hasReliableBackward()) {
				if (!((UnreliableSensor) getSensor(Direction.BACKWARD)).isOperational()) {
					// if not, check the other sensors
					// left or right first doesn't matter, but they are both preferable to forward
					if (((UnreliableSensor) getSensor(Direction.LEFT)).isOperational()) {
						distance = checkLeft(Direction.LEFT);
					}
					else if (((UnreliableSensor) getSensor(Direction.RIGHT)).isOperational()) {
						distance = checkRight(Direction.RIGHT);
					}
					// if neither side sensor, we will resort to the more expensive forward sensor
					else if (((UnreliableSensor) getSensor(Direction.FORWARD)).isOperational()) {
						distance = checkAround(Direction.FORWARD);
					}
					// if no sensors are working, we will wait until a sensor is responsive
					else {
						while (!((UnreliableSensor) getSensor(Direction.BACKWARD)).isOperational());
						distance = checkFront(Direction.BACKWARD);
					}
				}
				else {
					distance = checkFront(Direction.BACKWARD);
				}
			}
			
			// if the backward sensor is working, then we can proceed normally
			else {
				distance = checkFront(Direction.BACKWARD);
			}
			break;
		case LEFT:
			if (getSensor(Direction.LEFT) == null) {
				throw new UnsupportedOperationException("Robot has no left sensor.");
			}
			if (!getController().hasReliableLeft()) {
				// check if the left sensor is not working
				if (!((UnreliableSensor) getSensor(Direction.LEFT)).isOperational()) {
					// if not, check the other sensors
					// forward or backward first doesn't matter, but they are both preferable to right
					if (((UnreliableSensor) getSensor(Direction.FORWARD)).isOperational()) {
						distance = checkLeft(Direction.FORWARD);
					}
					else if (((UnreliableSensor) getSensor(Direction.BACKWARD)).isOperational()) {
						distance = checkRight(Direction.BACKWARD);
					}
					// if neither side sensor, we will resort to the more expensive right sensor
					else if (((UnreliableSensor) getSensor(Direction.RIGHT)).isOperational()) {
						distance = checkAround(Direction.RIGHT);
					}
					// if no sensors are working, we will wait until a sensor is responsive
					else {
						while (!((UnreliableSensor) getSensor(Direction.LEFT)).isOperational());
						distance = checkFront(Direction.LEFT);
					}
				}
				// if the left sensor is working then we can proceed normally
				else {
					distance = checkFront(Direction.LEFT);
				}
			}
			// if the left sensor is working then we can proceed normally
			else {
				distance = checkFront(Direction.LEFT);
			}
			break;
		case RIGHT:
			if (getSensor(Direction.RIGHT) == null) {
				throw new UnsupportedOperationException("Robot has no right sensor.");
			}
			if (!getController().hasReliableRight()) {
				// check if the right sensor is not working
				if (!((UnreliableSensor) getSensor(Direction.RIGHT)).isOperational()) {
					// if not, check the other sensors
					// forward or backward first doesn't matter, but they are both preferable to left
					if (((UnreliableSensor) getSensor(Direction.FORWARD)).isOperational()) {
						distance = checkRight(Direction.FORWARD);
					}
					else if (((UnreliableSensor) getSensor(Direction.BACKWARD)).isOperational()) {
						distance = checkLeft(Direction.BACKWARD);
					}
					// if neither side sensor, we will resort to the more expensive left sensor
					else if (((UnreliableSensor) getSensor(Direction.LEFT)).isOperational()) {
						distance = checkAround(Direction.LEFT);
					}
					// if no sensors are working, we will wait until a sensor is responsive
					else {
						while (!((UnreliableSensor) getSensor(Direction.RIGHT)).isOperational());
						distance = checkFront(Direction.RIGHT);
					}
				}
				else {
					distance = checkFront(Direction.RIGHT);
				}
			}
			else {
				distance = checkFront(Direction.RIGHT);
			}
			break;
		default:
			break;
		}
		return distance;
	}
	
	/**
	 * Gets the distance to the object in front of the specified sensor.
	 * @param direction the direction of the sensor
	 * @return distance to obstacle
	 */
	private int checkFront(Direction direction) {
		int distance = -1;
		try {
			distance = getSensor(direction).distanceToObstacle(getCurrentPosition(), getCurrentDirection(), new float[] {getBatteryLevel()});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return distance;
	}
	
	/**
	 * Orients the robot to get the distance to the object to the left of 
	 * the specified sensor and then rotates the robot back to its original direction.
	 * @param direction the direction of the sensor
	 * @return distance to obstacle
	 */
	private int checkLeft(Direction direction) {
		int distance = -1;
		rotate(Turn.LEFT);
		try {
			distance = getSensor(direction).distanceToObstacle(getCurrentPosition(), getCurrentDirection(), new float[] {getBatteryLevel()});
		} catch (Exception e) {
			e.printStackTrace();
		}
		rotate(Turn.RIGHT);
		return distance;
	}
	
	/**
	 * Orients the robot to get the distance to the object to the right of 
	 * the specified sensor and then rotates the robot back to its original direction.
	 * @param direction the direction of the sensor
	 * @return distance to obstacle
	 */
	private int checkRight(Direction direction) {
		int distance = -1;
		rotate(Turn.RIGHT);
		try {
			distance = getSensor(direction).distanceToObstacle(getCurrentPosition(), getCurrentDirection(), new float[] {getBatteryLevel()});
		} catch (Exception e) {
			e.printStackTrace();
		}
		rotate(Turn.LEFT);
		return distance;
	}
	
	/**
	 * Orients the robot to get the distance to the object to opposite of 
	 * the specified sensor and then rotates the robot back to its original direction.
	 * @param direction the direction of the sensor
	 * @return distance to obstacle
	 */
	private int checkAround(Direction direction) {
		int distance = -1;
		rotate(Turn.AROUND);
		try {
			distance = getSensor(direction).distanceToObstacle(getCurrentPosition(), getCurrentDirection(), new float[] {getBatteryLevel()});
		} catch (Exception e) {
			e.printStackTrace();
		}
		rotate(Turn.AROUND);
		return distance;
	}
	
	@Override
	public void startFailureAndRepairProcess(Direction direction, int meanTimeBetweenFailures, int meanTimeToRepair)
			throws UnsupportedOperationException {
		// start the failure and repair process for the sensor responsible for the specified direction
		switch(direction) {
		case FORWARD:
			getSensor(Direction.FORWARD).startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		case LEFT:
			getSensor(Direction.LEFT).startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		case RIGHT:
			getSensor(Direction.RIGHT).startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		case BACKWARD:
			getSensor(Direction.BACKWARD).startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		}
	}

	@Override
	public void stopFailureAndRepairProcess(Direction direction) throws UnsupportedOperationException {
		// stop the failure and repair process for the sensor responsible for the specified direction
		switch(direction) {
		case FORWARD:
			getSensor(Direction.FORWARD).stopFailureAndRepairProcess();
			break;
		case LEFT:
			getSensor(Direction.LEFT).stopFailureAndRepairProcess();
			break;
		case RIGHT:
			getSensor(Direction.RIGHT).stopFailureAndRepairProcess();
			break;
		case BACKWARD:
			getSensor(Direction.BACKWARD).stopFailureAndRepairProcess();
			break;
		}
	}

}
