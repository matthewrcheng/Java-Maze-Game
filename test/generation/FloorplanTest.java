package generation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests individual methods of the Floorplan class. 
 * 
 * 
 * @author Peter Kemper
 *
 */
public class FloorplanTest{

	// private variables
	private int width = 4;
	private int height = 4;
	private Floorplan floorplan;  // setup makes this a width x height cells object
	private Floorplan floorplan1; // setup makes this a 1x1 cells object 
	
	/**
	 * We create a default (width x height) floorplan object that is not initialized and a (1x1) floorplan1 object.
	 */
	@Before
	public void setUp() {
		floorplan = new Floorplan(width, height);
		floorplan1 = new Floorplan(1, 1);
	}

	/**
	 * Nothing needed to clean up variables after each test
	 * @throws Exception
	 */
	/*
	@After
	public void tearDown() throws Exception {
	}
	*/
	/**
	 * Test case: See if constructor used in setUp delivers anything
	 * <p>
	 * Method under test: own set up
	 * <p>
	 * It is correct if the floorplan field is not null.
	 */
	@Test
	public final void testFloorplan() {
		assertNotNull(floorplan) ;
		assertNotNull(floorplan1) ;
	}

	/**
	 * Test case: Check if constructor that takes existing array really 
	 * copies values and resets values with the initialize method.
	 * <p>
	 * Method under test: Floorplan(int[][] input), getValueOfCell(int i, int j)
	 * <p>
	 * Correct behavior: constructor delivers a floorplan object where
	 * all internal positions are set as given. After initialization
	 * those values must be set differently.
	 */
	@Test
	public final void testFloorplanConstructorWithArray() {
		// constructor with arrays should use initial values from array
		// in this case, values are set to specific numbers
		int[][] a = new int[width][height] ;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				a[i][j] = i*height+j ;
			}
		}
		floorplan = new Floorplan(a) ;
		assertTrue(floorplan != null) ;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				assertTrue(i*height+j == floorplan.getValueOfCell(i, j)) ;
			}
		}
		// initialize method should reset values such that wallboards are up everywhere
		// means old values are gone, new values can not be 0
		floorplan.initialize();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				assertFalse(i*height+j == floorplan.getValueOfCell(i, j)) ;
				assertFalse(0 == floorplan.getValueOfCell(i, j)) ;
			}
		}
	}
	/**
	 * Test case: See if the two constructor methods work in a consistent manner
	 * <p>
	 * Method under test: Floorplan(int[][] input), Floorplan(width,height), equals(Object other)
	 * <p>
	 * Correct behavior:
	 * It is correct if each constructor delivers a floorplan object and that both 
	 * are equal if of same dimension and of same content
	 */
	@Test
	public final void testFloorplanBothConstructors() {
		// constructor with arrays should use initial values from array
		// in this case, values are set to specific numbers
		floorplan = new Floorplan(new int[width][height]) ;
		assertTrue(floorplan != null) ;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				assertTrue(0 == floorplan.getValueOfCell(i, j)) ;
			}
		}
		// constructor with width and height
		// should have values for outside bounds being set and 
		// inner wallboards being up, such that values can not be 0 anywhere after initialization
		floorplan1 = new Floorplan(width,height) ;
		assertTrue(floorplan1 != null) ;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				assertTrue(0 == floorplan.getValueOfCell(i, j)) ;
			}
		}
		// both constructor methods should deliver same maze before initialization
		assertTrue(floorplan1.equals(floorplan)) ;
		// let's initialize one floorplan object and see if values change
		floorplan.initialize();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				assertFalse(0 == floorplan.getValueOfCell(i, j)) ;
			}
		}
		// both floorplans should be different as floorplan1 is not initialized yet
		assertFalse(floorplan1.equals(floorplan)) ;
		floorplan1.initialize() ;
		assertTrue(0 != floorplan1.getValueOfCell(0, 0)) ;
		// check equals method
		assertTrue(floorplan1.equals(floorplan1)) ; // reflexive
		assertFalse(floorplan1.equals(null)) ; // by definition, false for null
		assertFalse(floorplan1.equals(this)) ; // by definition, false for different types
		// both constructor methods should deliver same maze after initialization
		assertTrue(floorplan1.equals(floorplan)) ;
		// check if dimensions matter, floorplans of different dimensions can not be equal
		floorplan1 = new Floorplan(new int[width+1][height+1]) ;
		assertFalse(floorplan1.equals(floorplan)) ;
		floorplan1.initialize() ;
		assertFalse(floorplan1.equals(floorplan)) ;
	}


	/** 
	 * Test case: Correctness of the canTearDown method 
	 * <p>
	 * Method under test: canTearDown(int x, int y, CardinalDirection dir) 
	 * <p>
	 * Correct behavior: 
	 * checks if adjacent cells, (x,y) and its neighbor (x+dx,y+dy), are not separated by a border
	 * and (x+dx,y+dy) has not been visited before.
	 */
	@Test
	public final void testCanTearDown() {
		assertTrue(width > 1) ;
		assertTrue(height > 1) ;
		// the initial 4x4 floorplans have wallboards up everywhere, but borders only on the outside
		// canTearDown is expected to be ok as the visited flags is not set yet and
		// there are no borders set internally
		floorplan.initialize();
		// origin (0,0) is at top left corner
		// x coordinate grows to the right in horizontal direction
		// y coordinate grows to the bottom in the vertical direction
		// at origin, we can not go up or left but down or right
		Wallboard wallboard = new Wallboard(0,0,CardinalDirection.East) ;
		assertTrue(floorplan.canTearDown(wallboard)); // right
		wallboard.setLocationDirection(0,0,CardinalDirection.South);
		assertTrue(floorplan.canTearDown(wallboard)); // down
		wallboard.setLocationDirection(0,0,CardinalDirection.West);
		assertFalse(floorplan.canTearDown(wallboard)); // left
		wallboard.setLocationDirection(0,0,CardinalDirection.North);
		assertFalse(floorplan.canTearDown(wallboard)); // up
		// at position (1,1) we can go in each direction
		wallboard.setLocationDirection(1,1,CardinalDirection.East);
		assertTrue(floorplan.canTearDown(wallboard));
		wallboard.setLocationDirection(1,1,CardinalDirection.South);
		assertTrue(floorplan.canTearDown(wallboard));
		wallboard.setLocationDirection(1,1,CardinalDirection.West);
		assertTrue(floorplan.canTearDown(wallboard));
		wallboard.setLocationDirection(1,1,CardinalDirection.North);
		assertTrue(floorplan.canTearDown(wallboard));
		// at the bottom right corner we can go North or West only
		wallboard.setLocationDirection(width-1,height-1,CardinalDirection.East);
		assertFalse(floorplan.canTearDown(wallboard));
		wallboard.setLocationDirection(width-1,height-1,CardinalDirection.South);
		assertFalse(floorplan.canTearDown(wallboard));
		wallboard.setLocationDirection(width-1,height-1,CardinalDirection.West);
		assertTrue(floorplan.canTearDown(wallboard));
		wallboard.setLocationDirection(width-1,height-1,CardinalDirection.North);
		assertTrue(floorplan.canTearDown(wallboard));
		// check if visited flag impacts canTearDown decision
		// at position (1,1) to right (2,1) we can still go
		// but not anymore if we set the visited flag
		floorplan.setCellAsVisited(2, 1);
		wallboard.setLocationDirection(1,1,CardinalDirection.East);
		assertFalse(floorplan.canTearDown(wallboard)); 
		floorplan.setCellAsVisited(0, 1);
		wallboard.setLocationDirection(1,1,CardinalDirection.West);
		assertFalse(floorplan.canTearDown(wallboard)); 
		floorplan.setCellAsVisited(1, 2);
		wallboard.setLocationDirection(1,1,CardinalDirection.South);
		assertFalse(floorplan.canTearDown(wallboard)); 
		floorplan.setCellAsVisited(1, 0);
		wallboard.setLocationDirection(1,1,CardinalDirection.North);
		assertFalse(floorplan.canTearDown(wallboard)); 	
	}
	/** 
	 * Test case: Correctness of methods for exit position 
	 * <p>
	 * Method under test: setExitPosition(int x, int y) and isExitPosition(int x, int y, int bit)
	 * <p>
	 * Correct behavior: 
	 * it sets a given bit to zero in a given cell
	 */
	@Test
	public final void testExitPosition() {
		floorplan.initialize();
		// top left corner
		assertFalse(floorplan.isExitPosition(0, 0));
		floorplan.setExitPosition(0,0);
		assertTrue(floorplan.isExitPosition(0, 0));
		// top right corner
		assertFalse(floorplan.isExitPosition(width-1, 0));
		floorplan.setExitPosition(width-1,0);
		assertTrue(floorplan.isExitPosition(width-1, 0));
		// bottom left corner
		assertFalse(floorplan.isExitPosition(0, height-1));
		floorplan.setExitPosition(0, height-1);
		assertTrue(floorplan.isExitPosition(0, height-1));
		// bottom right corner
		assertFalse(floorplan.isExitPosition(width-1, height-1));
		floorplan.setExitPosition(width-1, height-1);
		assertTrue(floorplan.isExitPosition(width-1, height-1));
		// top middle position
		assertFalse(floorplan.isExitPosition(0, 2));
		floorplan.setExitPosition(0, 2);
		assertTrue(floorplan.isExitPosition(0, 2));
		// side middle position
		assertFalse(floorplan.isExitPosition(2, 0));
		floorplan.setExitPosition(2, 0);
		assertTrue(floorplan.isExitPosition(2, 0));
		// side middle position
		assertFalse(floorplan.isExitPosition(2, height-1));
		floorplan.setExitPosition(2, height-1);
		assertTrue(floorplan.isExitPosition(2, height-1));
		// wrong position
		assertFalse(floorplan.isExitPosition(2, 2));
		floorplan.setExitPosition(2, 2);
		assertFalse(floorplan.isExitPosition(2, 2));
		
	}

	/** 
	 * Test case: Correctness of the setCellAsVisited method 
	 * <p>
	 * Method under test: setCellAsVisited(int x, int y) 
	 * <p>
	 * Correct behavior: 
	 * Method sets visited flag to zero for a given cell.
	 */
	@Test
	public final void testSetCellAsVisitedToZero() {
		// initial values are 0
		assertTrue(floorplan.hasMaskedBitsFalse(0, 0, Floorplan.CW_VISITED));
		//floorplan.setBitToOne(0, 0, Constants.CW_VISITED);
		floorplan.initialize(); // sets all visited flags to 1
		assertTrue(floorplan.hasMaskedBitsTrue(0, 0, Floorplan.CW_VISITED));

		floorplan.setCellAsVisited(0, 0);
		assertTrue(floorplan.hasMaskedBitsFalse(0, 0, Floorplan.CW_VISITED));
		
		//floorplan.setBitToOne(0, 0, Constants.CW_VISITED);
		floorplan.initialize(); // sets all visited flags to 1
		assertTrue(floorplan.hasMaskedBitsTrue(0, 0, Floorplan.CW_VISITED));
	}
	


	/** 
	 * Test case: Correctness of the setInRoomToOne method 
	 * <p>
	 * Method under test: setInRoomToOne(int x, int y) 
	 * <p>
	 * Correct behavior: 
	 * it sets the InRoom bit to one for a given cell and direction
	 */
	@Test
	public final void testSetInRoomToOne() {
		assertFalse(floorplan.isInRoom(1, 1));
		floorplan.setInRoomToOne(1,1);
		assertTrue(floorplan.isInRoom(1, 1));
	}


	/** 
	 * Test case: Correctness of the initialize method 
	 * <p>
	 * Method under test: initialize() 
	 * <p>
	 * Correct behavior: 
	 * Initialize maze such that all cells have not been visited (CW_VISITED), all wallboards are up (CW_ALL),
	 * and borders are set as a rectangle (CW_*_BOUND).
	 */
	@Test
	public final void testInitialize() {
		floorplan.initialize();
		assertTrue(floorplan.hasMaskedBitsTrue(0, 0, floorplan.getBoundForBit(Floorplan.CW_TOP))); 
		assertTrue(floorplan.hasMaskedBitsTrue(0, 0, floorplan.getBoundForBit(Floorplan.CW_LEFT)));
		assertFalse(floorplan.hasMaskedBitsTrue(0, 0, floorplan.getBoundForBit(Floorplan.CW_BOT)));
		assertFalse(floorplan.hasMaskedBitsTrue(0, 0, floorplan.getBoundForBit(Floorplan.CW_RIGHT)));
		//assertTrue(floorplan.hasMaskedBitsTrue(0, 0, Floorplan.CW_ALL_BOUNDS));
		assertTrue(floorplan.hasMaskedBitsTrue(0, 0, Floorplan.CW_ALL));
		assertTrue(floorplan.hasMaskedBitsTrue(0, 0, Floorplan.CW_VISITED));
		
		Floorplan cell3 = new Floorplan(0,0);//tests empty grid
		cell3.initialize();
		assertNotNull(cell3);//above line did not throw error
	}

	/** 
	 * Test case: Correctness of the areaOverlapsWithRoom method 
	 * <p>
	 * Method under test: areaOverlapsWithRoom(int rx, int ry, int rxl, int ryl) 
	 * <p>
	 * Correct behavior: 
	 * Checks if there is a cell in the given area that belongs to a room.
	 * The first corner is at the upper left position, the second corner is at the lower right position.
	 */
	@Test
	public final void testAreaOverlapsWithRoom() {
		floorplan.initialize();
		assertFalse(floorplan.areaOverlapsWithRoom(1,1,0,1));
	}

	/** 
	 * Test case: Correctness of the addWall, deleteWall methods 
	 * <p>
	 * Method under test: deleteWall(int x, int y, CardinalDirection cd) 
	 * Method under test: addWall(int x, int y, CardinalDirection cd) 
	 * <p>
	 * Correct behavior: 
	 * it deletes a wallboard between to adjacent cells (x,y) and (x+dx,y+dy).
	 */
	@Test
	public final void testAddAndDeleteWall() {
		// initially all wallboards are up
		floorplan.initialize();
		assertTrue(floorplan.hasMaskedBitsTrue(0,0,Floorplan.CW_RIGHT));
		assertTrue(floorplan.hasMaskedBitsTrue(1,0,Floorplan.CW_LEFT));
		
		// define wallboard between (0,0) and (1,0) which is east from (0,0)
		// deleteWall removes wallboard in both cells (0,0) and (1,0)
		Wallboard wallboard = new Wallboard(0, 0, CardinalDirection.East) ;
		floorplan.deleteWallboard(wallboard);
		assertTrue(floorplan.hasMaskedBitsFalse(0,0,Floorplan.CW_RIGHT));
		assertTrue(floorplan.hasMaskedBitsFalse(1,0,Floorplan.CW_LEFT));
		
		// addWall as internal wallboard adds it to both cells (0,0) and (1,0)
		floorplan.addWallboard(wallboard, true);
		assertTrue(floorplan.hasMaskedBitsTrue(0,0,Floorplan.CW_RIGHT));
		assertTrue(floorplan.hasMaskedBitsTrue(1,0,Floorplan.CW_LEFT));
		
		// deleteWall removes wallboard in both cells (0,0) and (1,0)
		floorplan.deleteWallboard(wallboard);
		assertTrue(floorplan.hasMaskedBitsFalse(0,0,Floorplan.CW_RIGHT));
		assertTrue(floorplan.hasMaskedBitsFalse(1,0,Floorplan.CW_LEFT));
		
		// addWall but not as internal wallboard adds it only to cells (0,0) 
		floorplan.addWallboard(wallboard, false);
		assertTrue(floorplan.hasMaskedBitsTrue(0,0,Floorplan.CW_RIGHT));
		assertTrue(floorplan.hasMaskedBitsFalse(1,0,Floorplan.CW_LEFT));
		
		// deleteWall removes wallboard in both cells (0,0) and (1,0)
		// robust against deleting non-existing wallboard at cell(1,0) 
		floorplan.deleteWallboard(wallboard);
		assertTrue(floorplan.hasMaskedBitsFalse(0,0,Floorplan.CW_RIGHT));
		assertTrue(floorplan.hasMaskedBitsFalse(1,0,Floorplan.CW_LEFT));


	}

	
	/** 
	 * Test case: Correctness of the markAreaAsRoom method 
	 * <p>
	 * Method under test: markAreaAsRoom(int rw, int rh, int rx, int ry, int rxl, int ryl, Random r) 
	 * Method under test: isInRoom(int x, int y) 
	 * <p>
	 * Correct behavior: 
	 * it marks a given area as a room on the maze and positions up to five doors randomly.
	 * The first corner is at the upper left position, the second corner is at the lower right position.
	 * Assumes that given area is located on the map and does not intersect with any existing room.
	 * The wallboards of a room are declared as borders to prevent the generation mechanism from tearing them down.
	 * rw is the room width, rh is the room height, rx is 1st corner, x coordinate, ry is 1st corner, y coordinate, 
	 * rxl is 2nd corner, x coordinate, ryl is 2nd corner, y coordinate
	 */
	@Test
	public final void testMarkAreaAsRoom() {
		Floorplan C = new Floorplan(10,10);
		C.initialize();
		C.markAreaAsRoom(4,4, 1,1, 4,4);
		assertTrue(C.areaOverlapsWithRoom(1,1,5,5));
		assertFalse(C.areaOverlapsWithRoom(6,6,8,8));
		assertTrue(C.isInRoom(3, 3));
		assertFalse(C.isInRoom(4, 8));

		Floorplan cell1 = new Floorplan(10, 10);
		cell1.markAreaAsRoom(5, 5, 2, 2, 7, 7);
		for(int x = 2; x < 8; x++){
			for(int y = 2; y < 8; y++){
				assertTrue(cell1.isInRoom(x, y));
			}
		}
		assertFalse(cell1.isInRoom(9, 9));

		Floorplan cell2 = new Floorplan(10, 10);
		cell2.initialize();
		cell2.markAreaAsRoom(5, 5, 1, 1, 6, 6);
		Wallboard wallboard = new Wallboard(5, 5, CardinalDirection.getDirection(0, 1)) ;
		assertTrue(cell2.canTearDown(wallboard));
		wallboard.setLocationDirection(5, 5, CardinalDirection.South);
		assertTrue(cell2.canTearDown(wallboard));
	}

	/** 
	 * Test case: Correctness of the hasMaskedBitsTrue method 
	 * <p>
	 * Method under test: hasMaskedBitsTrue(int x, int y, int bitmask) 
	 * <p>
	 * Correct behavior: 
	 * it gets methods (is..., has...) for various flags
	 */
	@Test
	public final void testHasMaskedBitsTrue() {
		floorplan.initialize();
		assertTrue(floorplan.hasMaskedBitsTrue(0, 0, Floorplan.CW_VISITED));
		
		floorplan.setCellAsVisited(0, 0);
		assertFalse(floorplan.hasMaskedBitsTrue(0, 0, Floorplan.CW_VISITED));
	}

	/** 
	 * Test case: Correctness of the isInRoom method 
	 * <p>
	 * Method under test: isInRoom(int x, int y) 
	 * <p>
	 * Correct behavior: 
	 * it tells if InRoom flag is set for given cell
	 */
	@Test
	public final void testIsInRoom() {
		// initial setting must be such that position is not inside a room
		assertFalse(floorplan.isInRoom(0, 0));
	}

	/** 
	 * Test case: Correctness of the hasWall, hasNoWall methods 
	 * <p>
	 * Method under test: hasWall(int x, int y, CardinalDirection d) 
	 * Method under test: hasNoWall(int x, int y, CardinalDirection) 
	 * <p>
	 * Correct behavior: 
	 * it tells if cell has a wallboard in the given direction
	 */ 
	@Test
	public final void testHasWall() {
		floorplan.initialize();
		assertTrue(floorplan.hasWall(0, 0, CardinalDirection.North));
		assertTrue(floorplan.hasWall(0, 0, CardinalDirection.East));
		assertTrue(floorplan.hasWall(0, 0, CardinalDirection.South));
		assertTrue(floorplan.hasWall(0, 0, CardinalDirection.West));
		assertFalse(floorplan.hasNoWall(0, 0, CardinalDirection.North));
		assertFalse(floorplan.hasNoWall(0, 0, CardinalDirection.East));
		assertFalse(floorplan.hasNoWall(0, 0, CardinalDirection.South));
		assertFalse(floorplan.hasNoWall(0, 0, CardinalDirection.West));
		
		Wallboard wallboard = new Wallboard(0,0,CardinalDirection.East);
		floorplan.deleteWallboard(wallboard);
		assertFalse(floorplan.hasWall(0, 0, CardinalDirection.East));
		assertTrue(floorplan.hasNoWall(0, 0, CardinalDirection.East));
		assertFalse(floorplan.hasWall(1, 0, CardinalDirection.West));
		assertTrue(floorplan.hasNoWall(1, 0, CardinalDirection.West));

		wallboard.setLocationDirection(0,0,CardinalDirection.South);
		floorplan.deleteWallboard(wallboard);
		assertFalse(floorplan.hasWall(0, 0, CardinalDirection.South));
		assertTrue(floorplan.hasNoWall(0, 0, CardinalDirection.South));
		assertFalse(floorplan.hasWall(0, 1, CardinalDirection.North));
		assertTrue(floorplan.hasNoWall(0, 1, CardinalDirection.North));
		
	}

	/** 
	 * Test case: Correctness of the hasMaskedBitsFalse method 
	 * <p>
	 * Method under test: hasMaskedBitsFalse(int x, int y,int bitmask) 
	 * <p>
	 * Correct behavior: 
	 * it tells if masked bit is false
	 */
	@Test
	public final void testHasMaskedBitsFalse() {
		floorplan.initialize();
		assertFalse(floorplan.hasMaskedBitsFalse(0, 0, Floorplan.CW_RIGHT));
		
		Wallboard wallboard = new Wallboard(0,0,CardinalDirection.East);
		floorplan.deleteWallboard(wallboard);
		assertTrue(floorplan.hasMaskedBitsFalse(0, 0, Floorplan.CW_RIGHT));
	}

	/** 
	 * Test case: Correctness of the toString method 
	 * <p>
	 * Method under test: toString() 
	 * <p>
	 * Correct behavior: 
	 * it dumps internal data into a string, intended usage is for debugging purposes. 
	 * Maze is represent as a matrix of integer values.
	 */
	@Test
	public final void testToString() {
		Floorplan cell1 = new Floorplan(1, 1);
		assertEquals(cell1.toString(), cell1.toString(), " i:0 j:0=0\n"); 
		cell1.initialize();
		assertEquals(cell1.toString(), cell1.toString(), " i:0 j:0=511\n"); 
	}
	/**
	 * Test method for {@link generation.CardinalDirection#getCWConstantForDirection()}.
	 */
	@Test
	public void testGetCWConstantForDirection() {
		Floorplan cell1 = new Floorplan(1, 1);
		assertEquals(cell1.getCWConstantForDirection(CardinalDirection.East),Floorplan.CW_RIGHT);
		assertEquals(cell1.getCWConstantForDirection(CardinalDirection.West),Floorplan.CW_LEFT);
		assertEquals(cell1.getCWConstantForDirection(CardinalDirection.South),Floorplan.CW_BOT);
		assertEquals(cell1.getCWConstantForDirection(CardinalDirection.North),Floorplan.CW_TOP);
	}

}
