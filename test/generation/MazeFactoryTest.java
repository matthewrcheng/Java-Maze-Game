//package generation;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gui.Constants;

class MazeFactoryTest {

	// private variables
	protected MazeFactory mazefactory = new MazeFactory();
	
	protected Stuborder dummy = new Stuborder(10,5,true,Order.Builder.Boruvka);
	
	// This can easily be altered to testing needs, such as making build
	// order Prim instead of DFS
	protected int seed = 10;
	protected int skill = 2;
	protected boolean perfect = true;
	protected Stuborder.Builder build = Order.Builder.Boruvka;
	protected Stuborder so = new Stuborder(seed,skill,perfect,build);
		
	// Allows for us to test what each program prints
	protected final PrintStream standardOut = System.out;
	protected final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
	
	@BeforeEach
	public void setUp() {
		System.setOut(new PrintStream(outputStreamCaptor));
	}
	
	@AfterEach
	public void tearDown() {
	    System.setOut(standardOut);
	}
	
	@Test
	void testOrderBuildThreadRunning() {
	/*
	 * we have a buildThread that is already running so we assert that
	 * the return value for order() is false
	 */
		mazefactory.order(dummy);
		assertFalse(mazefactory.order(so));
	}
	
	@Test
	void testOrderBuilder() {
	/* no buildThread is running so we proceed; order's builder that
	 * we get from order.getBuilder() is DFS, so we assert that the 
	 * return value is true
	 */
		assertTrue(mazefactory.order(so));
	}
	
	@Test
	void testMazeBuilderWidth() {
	/* create an order with a specified skill level and verify that 
	 * the resulting maze has the correct width
	 * this order should have a DFS builder
	 */
		mazefactory.order(so);
		mazefactory.waitTillDelivered();
		int width = Constants.SKILL_X[skill];
		assertEquals(so.maze.getWidth(),width);
	}
	
	@Test
	void testMazeBuilderHeight() {
	/* create an order with a specified skill level and verify that 
	 * the resulting maze has the correct height
	 * this order should have a DFS builder
	 */
		mazefactory.order(so);
		mazefactory.waitTillDelivered();
		int height = Constants.SKILL_Y[skill];
		assertEquals(so.maze.getHeight(),height);
	} 
	
	@Test
	void testMazeBuilderSeed() {
	/* we create two orders with different seeds; the resulting 
	 * floorplans should be different as a result
	 * this order should have a DFS builder
	 */
		mazefactory.order(so);
		mazefactory.waitTillDelivered();
		Stuborder temp = new Stuborder(seed + 10,skill,perfect,build);
		mazefactory.order(temp);
		mazefactory.waitTillDelivered();
		assertNotSame(so.maze.getFloorplan(),temp.maze.getFloorplan());
	}
	
	@Test
	void testOrderOtherBuilderReturn() {
	/* no buildThread is running so we proceed; order's builder that
	 * we get from order.getBuilder() is Kruskal (or another builder)
	 * so we assert that the return value is false
	 */ 
		Stuborder temp = new Stuborder(seed,skill,perfect,Order.Builder.Kruskal);
		assertFalse(mazefactory.order(temp));
	}
	
	@Test
	void testCancelExists() {
	/* have a pre-existing thread; make sure that the thread was interrupted
	 */ 
		mazefactory.order(so);
		mazefactory.cancel();
		assertNotEquals("MazeFactory.cancel: no thread to cancel",outputStreamCaptor.toString().trim());
	}
	
	@Test
	void testCancelNoneExists() {
	/* have no pre-existing threads; assert that the output is 
	 * "MazeFactory.cancel: no thread to cancel"
	 */ 
		mazefactory.cancel();
		int len = outputStreamCaptor.toString().trim().split("\n").length - 1;
		assertEquals("MazeFactory.cancel: no thread to cancel",outputStreamCaptor.toString().trim().split("\n")[len]);
	}
	
	@Test
	void testWaitTillDeliveredTry() {
	/* have a pre-existing thread; the threads should join so that the
	 * current thread must finish before the joined one may start
	 * we do this by calling a sample thread, then verify that the thread
	 * is still running (isAlive() is true) after we return from the join
	 */ 
		mazefactory.order(so);
		mazefactory.waitTillDelivered();
		assertNotEquals("MazeBuilder.waitTillDelivered: no thread to wait for",outputStreamCaptor.toString().trim());
	}
	
	@Test
	void testWaitTillDeliveredNoneExists() {
	/* have no pre-existing thread; assert that the output is
	 * "MazeBuilder.waitTillDelivered: no thread to wait for"
	 */
		mazefactory.waitTillDelivered();
		int len = outputStreamCaptor.toString().trim().split("\n").length - 1;
		assertEquals("MazeBuilder.waitTillDelivered: no thread to wait for",outputStreamCaptor.toString().trim().split("\n")[len]);
	}
	
	@Test
	void testValidExit() {
	/*
	 * check to see that the exit position is a valid position
	 * this not only ensures that it is valid but also confirms
	 * that one has been specified
	 */
		mazefactory.order(so);
		mazefactory.waitTillDelivered();
		int[] pos = so.maze.getExitPosition();
		assertTrue(so.maze.isValidPosition(pos[0],pos[1]));
	}
	
	@Test
	void testValidStart() {
	/*
	 * check to see that the start position is a valid position
	 * this not only ensures that it is valid but also confirms
	 * that one has been specified
	 */
		mazefactory.order(so);
		mazefactory.waitTillDelivered();
		int[] pos = so.maze.getStartingPosition();
		assertTrue(so.maze.isValidPosition(pos[0],pos[1]));
	}
	
	@Test
	void testDistFromEachSpace() {
	/*
	 * this test obtains all distances of each space
	 * from the exit and ensures that they are both positive
	 * and at most equal to the total amount of spaces
	 * this means that  each space can find the exit
	 */
		mazefactory.order(so);
		mazefactory.waitTillDelivered();
		boolean valid = true;
		int[][] temp = so.maze.getMazedists().getAllDistanceValues();
		int dim = so.maze.getWidth();
		dim *= so.maze.getHeight();
		for (int[] i: temp) {
			for (int j: i) {
				if (j < 0 | j > dim+1) {
					valid = false;
				}
			}
		}
		assertTrue(valid);
	}
	
	@Test
	void testExitIsSmallest() {
	/*
	 * this test obtains all distances of each space
	 * from the exit and then finds the smallest one, if
	 * this matches the exit position, and its value is
	 * equal to 1, then it passes the test
	 */
		mazefactory.order(so);
		mazefactory.waitTillDelivered();
		int[][] dists = so.maze.getMazedists().getAllDistanceValues();
		int[] exit = so.maze.getExitPosition();
		int[] loc = new int[2];
		int min = 85000;
		int temp;
		for (int i = 0; i < dists.length; i++) {
			for (int j = 0; j < dists[0].length; j++) {
				temp = so.maze.getDistanceToExit(j, i);
				if (temp < min) {
					min = temp;
					loc[0] = j;
					loc[1] = i;
				}
			}
		}
		assertTrue(loc[0] == exit[0] & loc[1] == exit[1] & min == 1);
	}
	
	@Test
	void testCloserNeighbor() {
	/*
	 * this test finds the closer neighbor for each cell
	 * in the maze to make sure that it exists; the exit
	 * cell is skipped, as it has no closer neighbor
	 */
		mazefactory.order(so);
		mazefactory.waitTillDelivered();
		boolean exists = true;
		int w = so.maze.getWidth();
		int h = so.maze.getHeight();
		int[] exit = so.maze.getExitPosition();
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (j != exit[0] & i != exit[1]) {
					if (so.maze.getNeighborCloserToExit(j, i) == null) {
						exists = false;
						break;
					}
				}
			}
			if (exists == false) {
				break;
			}
		}
		assertTrue(exists);
	}
	
	@Test
	void testCorrectNumberOfWalls() {
	/*
	 * this test iterates through the 2d array of cells
	 * and checks to see if each cell has a wall on the
	 * left as well as on the top; it then increments the
	 * wall counter accordingly; this leaves the entire 
	 * outside right edge and bottom so we iterate a second
	 * time but only check the right edge of the far right
	 * cells and then the bottom edge of the lowest cells;
	 * if perfect, we check to make sure the number of walls
	 * is exactly correct and if not perfect, we make sure the
	 * number of walls is below the expected amount
	 * a perfect maze will have wh + w = h sides, where m and
	 * n are the width and height
	 */
		mazefactory.order(so);
		mazefactory.waitTillDelivered();
		int w = so.maze.getWidth();
		int h = so.maze.getHeight();
		int sides = w*h + w + h;
		int walls = 0;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (so.maze.hasWall(j, i, CardinalDirection.North)) {
					walls++;
				}
				if (so.maze.hasWall(j, i, CardinalDirection.West)) {
					walls++;
				}
				if (i == h-1) {
					if (so.maze.hasWall(j, i, CardinalDirection.South)) {
						walls++;
					}
				}
				if (j == w-1) {
					if (so.maze.hasWall(j, i, CardinalDirection.East)) {
						walls++;
					}
				}
			}
		}
		if (perfect) {
			assertEquals(sides,walls);
		}
		else {
			assertTrue(sides > walls);
		}
	}
}
