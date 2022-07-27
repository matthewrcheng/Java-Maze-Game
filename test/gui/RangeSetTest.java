package gui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
/**
 * The RangeSet class is used to represent a starting interval [lb,ub]
 * that is subsequently split into a set of smaller intervals 
 * but looking for intersections with other intervals [a,b] and 
 * removing such intervals.
 * 
 * A meaningful test for the RangeSet functionality combines most of its
 * methods. So we added a number of simple smoke tests that are trivial
 * to check if basic functionality works. If it does, more meaningful
 * test scenarios that combine methods can be performed, for example,
 * testing a scenario with an iterative removal of intervals.
 * 
 * @author Peter Kemper
 *
 */
class RangeSetTest {
	/**
	 * Simple smoke test with constructor and empty set.
	 */
	@Test
	final void testIsEmpty() {
		RangeSet sut = new RangeSet();
		assertTrue(sut.isEmpty());
	}

	/** 
	 * Simple smoke test with constructor and set method.
	 * Requires isEmpty() to work as well.
	 */
	@Test
	final void testSet() {
		RangeSet sut = new RangeSet();
		assertTrue(sut.isEmpty());
		sut.set(0, 100);
		assertFalse(sut.isEmpty());
	}
	/** 
	 * Simple smoke test with constructor, set, and remove method.
	 * Requires isEmpty() to work as well.
	 * Test is picky on the border cases for interval [lb,ub],
	 * to see if lb and ub values are properly handled.
	 */
	@Test
	final void testRemove() {
		RangeSet sut = new RangeSet();
		assertTrue(sut.isEmpty());
		// case 1: basic set and remove all, exact match of intervals
		sut.set(0, 100);
		assertFalse(sut.isEmpty());
		sut.remove(0, 100);
		assertTrue(sut.isEmpty());
		// case 2: see if we can repeat this or if sut remembers anything
		// basic set and remove all, exact match of intervals
		sut.set(0, 100);
		assertFalse(sut.isEmpty());
		sut.remove(0, 100);
		assertTrue(sut.isEmpty());
		// cases 3: try different overlaps for full interval
		// case 3: basic set and remove all, remove request is larger
		sut.set(0, 100);
		assertFalse(sut.isEmpty());
		sut.remove(-1, 101);
		assertTrue(sut.isEmpty());
		// case 4: basic set and remove some, remove request is to the left
		sut.set(0, 100);
		assertFalse(sut.isEmpty());
		sut.remove(-1, 1); // remove some on the left
		assertFalse(sut.isEmpty());
		sut.remove(2, 100); // remove left overs
		assertTrue(sut.isEmpty());
		// case 5: basic set and remove some, remove request is to the right
		sut.set(0, 100);
		assertFalse(sut.isEmpty());
		sut.remove(99, 101);// remove some on the right
		assertFalse(sut.isEmpty());
		sut.remove(0, 98); // remove left overs
		assertTrue(sut.isEmpty());
		// case 6: basic set and remove some, remove request is inside
		sut.set(0, 100);
		assertFalse(sut.isEmpty());
		sut.remove(49, 51);// remove some inside
		assertFalse(sut.isEmpty());
		sut.remove(0, 48); // remove left overs
		assertFalse(sut.isEmpty());
		sut.remove(52, 100); // remove left overs
		assertTrue(sut.isEmpty());
		// case 7: basic set and remove some, remove request is inside
		// remove several intervals with one request
		sut.set(0, 100);
		assertFalse(sut.isEmpty());
		sut.remove(49, 51);// remove some inside, implies split
		assertFalse(sut.isEmpty());
		sut.remove(-1, 101); // remove left overs
		assertTrue(sut.isEmpty());
		// case 8: basic set and remove none, remove request is outside, low
		sut.set(0, 100);
		assertFalse(sut.isEmpty());
		sut.remove(-2, -1);// remove some outside
		assertFalse(sut.isEmpty());
		// case 9: basic set and remove none, remove request is outside, high
		sut.set(0, 100);
		assertFalse(sut.isEmpty());
		sut.remove(101, 102);// remove some outside
		assertFalse(sut.isEmpty());
	}
	/** 
	 * Simple smoke test with constructor, set, and intersection method.
	 */
	@Test
	final void testGetIntersection() {
		RangeSet sut = new RangeSet();
		sut.set(0, 100);
		assertFalse(sut.isEmpty());
		int[] intersect = null;
		// case 1: no intersection below
		intersect = sut.getIntersection(-2, -1);
		assertNull(intersect);		
		// case 2: intersection overlaps on the left
		intersect = sut.getIntersection(-3, 0);
		assertTrue(0 == intersect[0] && 0 == intersect[1]);
		intersect = sut.getIntersection(-2, 1);
		assertTrue(0 == intersect[0] && 1 == intersect[1]);
		intersect = sut.getIntersection(-1, 50);
		assertTrue(0 == intersect[0] && 50 == intersect[1]);
		// case 3: intersection is inside
		intersect = sut.getIntersection(0, 0);
		assertTrue(0 == intersect[0] && 0 == intersect[1]);
		intersect = sut.getIntersection(0, 1);
		assertTrue(0 == intersect[0] && 1 == intersect[1]);
		intersect = sut.getIntersection(25, 50);
		assertTrue(25 == intersect[0] && 50 == intersect[1]);
		intersect = sut.getIntersection(0, 100);
		assertTrue(0 == intersect[0] && 100 == intersect[1]);
		intersect = sut.getIntersection(98, 99);
		assertTrue(98 == intersect[0] && 99 == intersect[1]);
		intersect = sut.getIntersection(95, 100);
		assertTrue(95 == intersect[0] && 100 == intersect[1]);
		intersect = sut.getIntersection(100, 100);
		assertTrue(100 == intersect[0] && 100 == intersect[1]);
		// case 4: intersection is on the right
		intersect = sut.getIntersection(100, 103);
		assertTrue(100 == intersect[0] && 100 == intersect[1]);
		intersect = sut.getIntersection(99, 101);
		assertTrue(99 == intersect[0] && 100 == intersect[1]);
		intersect = sut.getIntersection(50, 130);
		assertTrue(50 == intersect[0] && 100 == intersect[1]);
		intersect = sut.getIntersection(50, 100);
		assertTrue(50 == intersect[0] && 100 == intersect[1]);
		// case 5: no intersection above
		intersect = sut.getIntersection(101, 120);
		assertNull(intersect);	
	}
	/**
	 * Serious test for correct interaction of several methods.
	 * Scenario: we create a range of values [0,100] and successively
	 * look for intersections, remove them, verify that they are gone
	 * and keep doing so till the set is empty.
	 */
	@Test
	final void testScenarioWithIterativeRemovals() {
		RangeSet sut = new RangeSet();
		sut.set(1, 100);
		assertFalse(sut.isEmpty());
		int[] intersect = null;
		int lb;
		int ub;
		// case 1: remove disjoint, adjacent intervals in sequence
		// consider [1,10], [11,20], [21,30], ..., [91,100] 
		for (int i=0; i<10; i++) {
			lb = i*10+1;
			ub = i*10+10;
			// check that interval is in the set
			intersect = sut.getIntersection(lb, ub);
			assertNotNull(intersect);
			assertTrue(lb == intersect[0] && ub == intersect[1]);
			// remove interval
			sut.remove(lb, ub);
			// check that interval is not in the set anymore
			intersect = sut.getIntersection(lb, ub);
			assertNull(intersect);
		}
		assertTrue(sut.isEmpty());
		// reset sut
		sut.set(1, 100);
		assertFalse(sut.isEmpty());
		// case 2: same as 1 but in reverse order
		// consider [1,10], [11,20], [21,30], ..., [91,100] 
		for (int i=9; i>=0; i--) {
			lb = i*10+1;
			ub = i*10+10;
			// check that interval is in the set
			intersect = sut.getIntersection(lb, ub);
			assertNotNull(intersect);
			assertTrue(lb == intersect[0] && ub == intersect[1]);
			// remove interval
			sut.remove(lb, ub);
			// check that interval is not in the set anymore
			intersect = sut.getIntersection(lb, ub);
			assertNull(intersect);
		}
		assertTrue(sut.isEmpty());
		// reset sut
		sut.set(1, 100);
		assertFalse(sut.isEmpty());
		// case 3: remove overlapping intervals in sequence
		// consider [-1,13], [9,23], [19,33], ..., [89,103] 
		for (int i=0; i<10; i++) {
			lb = i*10-1;
			ub = i*10+13;
			// check that interval is in the set
			intersect = sut.getIntersection(lb, ub);
			assertNotNull(intersect);
			assertTrue(lb <= intersect[0] && ub >= intersect[1]);
			// remove interval
			sut.remove(lb, ub);
			// check that interval is not in the set anymore
			intersect = sut.getIntersection(lb, ub);
			assertNull(intersect);
		}
		assertTrue(sut.isEmpty());
		// reset sut
		sut.set(1, 100);
		assertFalse(sut.isEmpty());
		// case 4: same as 3 but in reverse order
		// consider [-1,13], [9,23], [19,33], ..., [89,103]
		for (int i=9; i>=0; i--) {
			lb = i*10-1;
			ub = i*10+13;
			// check that interval is in the set
			intersect = sut.getIntersection(lb, ub);
			assertNotNull(intersect);
			assertTrue(lb <= intersect[0] && ub >= intersect[1]);
			// remove interval
			sut.remove(lb, ub);
			// check that interval is not in the set anymore
			intersect = sut.getIntersection(lb, ub);
			assertNull(intersect);
		}
		assertTrue(sut.isEmpty());
	}
}
