package generation;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

public class FloorplanTestIterator {
   
    /**
     * Test case: See if iterator works on floorplan with no wallboards
     * <p>
     * Method under test: iterator
     * <p>
     * If there are no wallboards, iterator should provide no sequences.
     * Test uses fixed width and height but iterates through
     * all positions on the maze and exercises all directions.
     */
    @Test
    public final void testIteratorEmptyFloorplan() {
        // create a floorplan object we can test the iterator on
        int width = 4;
        int height = 5;
        Floorplan floorplan = new Floorplan(width,height);
        // check vertical wallboard, y coordinate changes, x stays
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (CardinalDirection cd: CardinalDirection.values()) {
                    Iterator<int[]> it = floorplan.iterator(x, y, cd);
                    assertFalse(it.hasNext()); // no wallboards, no sequences
                }
            }
        }
        
        
    }
    /**
     * Test case: See if iterator works on floorplan with all wallboards up
     * <p>
     * Method under test: iterator
     * <p>
     * If all wallboards are up, iterator should return sequences of length 1
     * because we hit a crossing wallboard after each step.
     * Test uses fixed width and height but iterates through
     * all positions on the maze and exercises all directions.
     */
    @Test
    public final void testIteratorFloorplanAllWallsUp() {
        // create a floorplan object we can test the iterator on
        int width = 4;
        int height = 5;
        Floorplan floorplan = new Floorplan(width,height);
        floorplan.initialize();
        // check vertical wallboard, y coordinate changes, x stays
        CardinalDirection cdBlocked;
        int limit;
        for (CardinalDirection cd: CardinalDirection.values()) {
            // pick the wallboard that would create a corner such that iterator stops
            // set the limit for the coordinate that changes
            if (cd == CardinalDirection.East || cd == CardinalDirection.West) {
                cdBlocked = CardinalDirection.North;
                limit = height;
            }
            else {
                cdBlocked = CardinalDirection.West;
                limit = width;
            }
            // check iterator for each possible starting location
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Iterator<int[]> it = floorplan.iterator(x, y, cd);
                    assertTrue(it.hasNext()); // there must be at least one sequence even at outmost position
                    int[] seq;
                    while (it.hasNext()) {
                        seq = it.next();
                        if (cd == CardinalDirection.East || cd == CardinalDirection.West) {
                            isSequenceOfVerticalWalls(floorplan, cdBlocked, limit, cd, x, seq);
                        }
                        else {
                            isSequenceOfHorizontalWalls(floorplan, cdBlocked, limit, cd, y, seq);
                        }
                        // length should be 1
                        assertEquals(seq[0]+1,seq[1]);
                    }
                }
            }
        }


    }
    /**
     * Checks if the given interval seq describes a continuous sequence of wallboards
     * @param floorplan that carry the wallboards
     * @param cdBlocked the direction we could see a blocking wallboard (perpendicular)
     * @param limit the max value for the end position for the y coordinate
     * @param cd the direction we expect to have a wallboard
     * @param x coordinate, the column in floorplan
     * @param seq gives two y coordinate values, start and end
     */
    private void isSequenceOfVerticalWalls(Floorplan floorplan, CardinalDirection cdBlocked,
            int limit, CardinalDirection cd, int x, int[] seq) {
        // for east, there should be a wallboard at (x,start)
        assertTrue(floorplan.hasWall(x,seq[0],cd));
        for (int i = seq[0]; i < seq[1]; i++) {
            assertTrue(floorplan.hasWall(x,i,cd));
        }
        // we either end at an index that is 
        // width or has no wallboard in this direction
        // or has a blocking wallboard
        // TODO: check if this makes sense, corner should be at seq[1]-1
        // note: adjusted by picked opposite direction for seq[1] for cdBlocked
        // as wallboards are symmetric (represented in both adjacent cells)
        assertTrue(seq[1] == limit ||
                floorplan.hasNoWall(x,seq[1], cd) ||
                (floorplan.hasWall(x, seq[1], cdBlocked) &&
                        floorplan.hasWall(x, seq[1]-1, cdBlocked.oppositeDirection())));
    }
    /**
     * Checks if the given interval seq describes a continuous sequence of wallboards
     * @param floorplan carries the wallboards
     * @param cdBlocked the direction we could see a blocking wallboard (perpendicular)
     * @param limit the max value for the end position for the y coordinate
     * @param cd the direction we expect to have a wallboard
     * @param y coordinate, the row in floorplan
     * @param seq gives two x coordinate values, start and end
     */
    private void isSequenceOfHorizontalWalls(Floorplan floorplan, CardinalDirection cdBlocked,
            int limit, CardinalDirection cd, int y, int[] seq) {
        // for east, there should be a wallboard at (x,start)
        assertTrue(floorplan.hasWall(seq[0],y,cd));
        for (int i = seq[0]; i < seq[1]; i++) {
            assertTrue(floorplan.hasWall(i,y,cd));
        }
        // we either end at an index that is 
        // width or has no wallboard in this direction
        // or has a blocking wallboard
        // TODO: check if this makes sense, corner should be at seq[1]-1
        // note: adjusted by picked opposite direction for seq[1] for cdBlocked
        // as wallboards are symmetric (represented in both adjacent cells)
        assertTrue(seq[1] == limit ||
                floorplan.hasNoWall(seq[1],y, cd) ||
                (floorplan.hasWall(seq[1],y, cdBlocked) &&
                        floorplan.hasWall(seq[1]-1,y, cdBlocked.oppositeDirection())));
    }
    
    
    /**
     * Test case: See if iterator works on floorplan with sequences of same length
     * <p>
     * Method under test: iterator
     * <p>
     * Test starts with a cell where all wallboards are up and then deletes
     * wallboards in a regular manner such that all sequence should be at the same
     * fixed length.
     * The gap between sequences is always of same length as well.
     * Test uses fixed width and height but iterates through
     * all positions on the maze and exercises all directions.
     */
    @Test
    public final void testIteratorFloorplanAllSequencesSameVertical() {
        // create a floorplan object we can test the iterator on
        final int seqLength = 3;
        final int gapLength = 2;
        final int total = 3;
        // height must == (seqLength+gapLength)* (#gaps) + seqLength
        final int height = (seqLength+gapLength)*total + seqLength;
        final int width = 4;
        final Floorplan floorplan = new Floorplan(width,height);
        floorplan.initialize();
        // to start with a sequence of wallboards and end with a sequence of wallboards
        punchHolesEast(floorplan,width,height,seqLength,gapLength);
        // check vertical wallboard, y coordinate changes, x stays
        CardinalDirection cd = CardinalDirection.East;
        // pick the wallboard that would create a corner such that iterator stops
        CardinalDirection cdBlocked = CardinalDirection.North;
        // set the limit for the coordinate that changes
        int limit = height;
        
        // check iterator for each possible starting location
        for (int x = 0; x < width-1; x++) {
            int y = 0; // start y always at 0
            int c = 0; // count the sequences
            Iterator<int[]> it = floorplan.iterator(x, y, cd);
            assertTrue(it.hasNext()); // there must be at least one sequence
            int[] seq;
            while (it.hasNext()) {
                seq = it.next();
                c++;
                isSequenceOfVerticalWalls(floorplan, cdBlocked, limit, cd, x, seq);
                // length should be same and as given
                assertEquals(seqLength,seq[1]-seq[0]);
            }
            assertEquals("Total number of sequences per column",total+1,c);
        }
        // test same property for opposite direction, here west
        cd = cd.oppositeDirection();
        // note: bounds for iteration change
        // column 0 has border on west, column width-1 has still
        // horizontal walls
        for (int x = 1; x < width-1; x++) {
            int y = 0; // start y always at 0
            int c = 0; // count the sequences
            Iterator<int[]> it = floorplan.iterator(x, y, cd);
            assertTrue(it.hasNext()); // there must be at least one sequence
            int[] seq;
            while (it.hasNext()) {
                seq = it.next();
                c++;
                isSequenceOfVerticalWalls(floorplan, cdBlocked, limit, cd, x, seq);
                // length should be same and as given
                assertEquals("x="+x+",y="+y+",s0="+seq[0]+",s1="+seq[1], seqLength,seq[1]-seq[0]);
            }
            assertEquals("Total number of sequences per column",total+1,c);
        }
    }
    /**
     * All columns treated the same, start with a sequence of wallboards,
     * then a gap, then a sequence and so forth.
     * @param floorplan
     * @param width
     * @param height
     * @param seqLength
     * @param gapLength
     */
    private void punchHolesEast(final Floorplan floorplan, final int width, final int height, final int seqLength, final int gapLength) {
        int c = 0;
        boolean gap = false;
        Wallboard wallboard = new Wallboard(0,0,CardinalDirection.East); // initial values don't matter 
        for (int x = 0; x < width-1; x++) {
            c = 0;
            gap = false;
            for (int y = 0; y < height; y++) {
                // remove all horizontal wallboards
                if (y < height-1) {
                    wallboard.setLocationDirection(x, y, CardinalDirection.South);
                    floorplan.deleteWallboard(wallboard);
                }
                // remove some vertical wallboards
                if (gap) {
                    if (c < gapLength) {
                        wallboard.setLocationDirection(x, y, CardinalDirection.East);
                        floorplan.deleteWallboard(wallboard);
                        c++;
                    }
                    else {
                        c=1;
                        gap=false;
                    }
                }
                else {
                    if (c < seqLength) {
                        c++;
                    }
                    else {
                        c=1;
                        gap=true;
                        wallboard.setLocationDirection(x, y, CardinalDirection.East);
                        floorplan.deleteWallboard(wallboard);
                    }
                }
            }
        }   
    }

}
