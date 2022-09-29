package gui;

import generation.Maze;

/**
 *  UnreliableSensor extends ReliableSensor, which is an interface for 
 *  DistanceSensor. It has the exact same functions that it can
 *  perform, checking for walls in each of the cardinal directions
 *  at the cost of energy. 
 *  
 *  However, this sensor is unreliable, so there may be times 
 *  where operations may fail and the sensor may not be functional.
 *  
 *  It works with Robot by allowing the robot to sense what its
 *  current surroundings are.
 * 
 * @author Matthew Cheng
 */

public class UnreliableSensor extends ReliableSensor implements Runnable {

	private boolean operational;
	private int meanTimeBetweenFailures = 4000;
	private int meanTimeToRepair = 2000;
	private Thread thread;
	private boolean processActive = true;
	
	public UnreliableSensor(Maze maze) {
		super(maze);
		operational = true;
	}
	
	/**
	 * Runs the thread that controls the failure and repair process. It begins by setting
	 * operational to false, then sleeping for the specified repair time to simulate the repair process.
	 * It then stops the process, meaning that the sensor is now operational again. The thread
	 * then sleeps again for the specified time between failures.
	 */
	@Override
	public void run() {
		while (processActive) {
			// stop the sensor from being operational
			operational = false;
			// simulate a repair by waiting the specified repair time
			try {
				Thread.sleep(meanTimeToRepair);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// re-enable the sensor now that it is repaired
			operational = true;
			// wait the mean time between failures before the thread finishes to
			// prevent the sensor from failing too early
			try {
				Thread.sleep(meanTimeBetweenFailures);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		stopFailureAndRepairProcess();
	}
	
	/**
	 * A method specific to UnreliableSensor() that returns whether or not the sensor
	 * is currently operational.
	 * @return boolean indicating if the sensor is operational
	 */
	public boolean isOperational() {
		return operational;
	}
	
	/**
	 * Sets the operational condition. This controls whether the sensor can sense.
	 * @param operational boolean indicating the condition of the sensor
	 */
	protected void setOperational(boolean operational) {
		this.operational = operational;
	}
	
	/**
	 * A method specific to UnreliableSensor() that returns whether or not the failure
	 * and repair process is currently active.
	 * @return boolean indicating if process is active
	 */
	public boolean isActive() {
		return processActive;
	}

	@Override
	public void startFailureAndRepairProcess(int meanTimeBetweenFailures, int meanTimeToRepair) {
		// set attributes
		this.meanTimeBetweenFailures = meanTimeBetweenFailures;
		this.meanTimeToRepair = meanTimeToRepair;
		// create and start thread
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void stopFailureAndRepairProcess() throws UnsupportedOperationException {
		// stop the thread loop and interrupt it
		processActive = false;
		thread.interrupt();
	}
	
}
