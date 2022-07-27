package generation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MazeBuilderBoruvkaTest extends MazeFactoryTest {

	private MazeBuilderBoruvka mb = new MazeBuilderBoruvka();
	
	@BeforeEach
	private void build() {
		mb.buildOrder(so);
	}
	
	@Test
	void testWeight() {
		/*
		 * test to see if each weight is different
		 */
		mb.createArray();
		int[] arr = mb.getArray();
		Set<Integer> s = new HashSet<Integer>();
		// we hash each value of the array
		for (int i : arr) {
			s.add(i);
		}
		// if each value was unique, the array and hashset
		// should be the same length
		assertEquals(s.size(),arr.length);
	}
	
	@Test
	void testWeightOrder() {
		/*
		 * test to see if the array that holds the
		 * weights has been rearranged so that the
		 * order is different
		 */
		
		// first we get the array before mixing
		mb.createArray();
		int[] arr1 = mb.getArray();
		
		// then we get the array after mixing
		mb.mixArray();
		int[] arr2 = mb.getArray();
		
		// if the two arrays are not equal, the test passes
		assertFalse(Arrays.equals(arr1, arr2));
	}
	
	@Test
	void testIsPreferredOverNull() {
		// create one edge and a null edge to compare it to
		Wallboard edge1 = new Wallboard(1,1,CardinalDirection.East);
		Wallboard edge2 = null;
		// since the other edge is null, edge1 should be preferred
		assertTrue(mb.is_preferred_over(edge1,edge2));
	}
	
	@Test
	void testIsPreferredOverLess() {
		// set the values of the two edges to 1 and 2, respectively
		mb.arr = new int[2];
		mb.arr[0] = 1;
		mb.arr[1] = 2;
		// we prefer the edge with the smaller weight, so edge1 should
		// be preferred
		Wallboard edge1 = new Wallboard(0,0,CardinalDirection.East);
		Wallboard edge2 = new Wallboard(0,0,CardinalDirection.South);
		assertTrue(mb.is_preferred_over(edge1, edge2));
	}
	
	@Test
	void testIsPreferredOverGreater() {
		// once again set the values of the two edges to 1 and 2
		// except switch the weights so that edge2 is preferred
		mb.arr = new int[2];
		mb.arr[0] = 2;
		mb.arr[1] = 1;
		Wallboard edge1 = new Wallboard(0,0,CardinalDirection.East);
		Wallboard edge2 = new Wallboard(0,0,CardinalDirection.South);
		assertFalse(mb.is_preferred_over(edge1, edge2));
	}

	@Test
	void testGetEdgeWeight() {
		/*
		 * test to see if getting the edge weight twice
		 * delivers the same value
		 */
		mb.createArray();
		Wallboard wb = new Wallboard(1,1,CardinalDirection.East);
		assertEquals(mb.getEdgeWeight(wb),mb.getEdgeWeight(wb));
	}
	
	@Test
	void testGetEdgeWeightDiff() {
		/*
		 * check to see if getting the same edge weight from
		 * two different cells delivers the same value
		 * 
		 */
		mb.createArray();
		Wallboard wb1 = new Wallboard(2,2,CardinalDirection.North);
		Wallboard wb2 = new Wallboard(2,1,CardinalDirection.South);
		assertEquals(mb.getEdgeWeight(wb1),mb.getEdgeWeight(wb2));
	}
	
	@Test
	void testGenerateVertices() {
		// manually set width and height
		mb.height = 3;
		mb.width = 3;
		// generate the vertices using width and height values
		mb.generate_vertices_and_edge_candidates();
		// create a template of what the array of vertices should look like
		ArrayList<int[]> vertices = new ArrayList<int[]>(9);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int[] temp = new int[2];
				temp[0] = j;
				temp[1] = i;
				vertices.add(temp);
			}
		}
		// for each vertex, ensure that it is equivalent to the corresponding
		// vertex in the template version
		for (int i = 0; i < vertices.size(); i++) {
			assertEquals(vertices.get(i)[0],mb.vertices.get(i)[0]);
			assertEquals(vertices.get(i)[1],mb.vertices.get(i)[1]);
		}
	}
	
	@Test
	void testAssignComponentsFirst() {
		/*
		 * the first time assign_components is run, there will be no edges yet
		 * so will will need to make each vertex its own component
		 */
		
		// initialize an arraylist of vertices
		ArrayList<int[]> vertices = new ArrayList<int[]>();
		
		// initialize 3 vertices and add them to the arrraylist
		int[] vertex1 = {1,1};
		int[] vertex2 = {2,1};
		int[] vertex3 = {1,2};
		vertices.add(vertex1);
		vertices.add(vertex2);
		vertices.add(vertex3);
		
		// set the mazebuilder's vertices to the arraylist
		mb.vertices = vertices;
		
		// create two arraylists, each for containing components
		// one is for the method and the other we will perform manually
		ArrayList<ArrayList<int[]>> forMethod = new ArrayList<ArrayList<int[]>>(1);
		ArrayList<ArrayList<int[]>> forManual = new ArrayList<ArrayList<int[]>>(1);
		
		// perform method
		mb.assign_components(forMethod);
		
		// manually assign the components using the vertices we created above
		ArrayList<int[]> manual1 = new ArrayList<int[]>();
		manual1.add(new int[] {vertex1[0],vertex1[1]});
		forManual.add(manual1);
		ArrayList<int[]> manual2 = new ArrayList<int[]>();
		manual2.add(new int[] {vertex2[0],vertex2[1]});
		forManual.add(manual2);
		ArrayList<int[]> manual3 = new ArrayList<int[]>();
		manual3.add(new int[] {vertex3[0],vertex3[1]});
		forManual.add(manual3);
		
		// the two arraylists should contain the same components
		assertEquals(forMethod.get(0).get(0)[0],forManual.get(0).get(0)[0]);
		assertEquals(forMethod.get(0).get(0)[1],forManual.get(0).get(0)[1]);
		assertEquals(forMethod.get(1).get(0)[0],forManual.get(1).get(0)[0]);
		assertEquals(forMethod.get(1).get(0)[1],forManual.get(1).get(0)[1]);
		assertEquals(forMethod.get(2).get(0)[0],forManual.get(2).get(0)[0]);
		assertEquals(forMethod.get(2).get(0)[1],forManual.get(2).get(0)[1]);
	}
	
	@Test
	void testAssignComponentsAfterBothNull() {
		/*
		 * if not the first time, there will be at least one edge for each
		 * vertex, so we will no longer have any single vertex components;
		 * the first case we check is if neither of the vertices have been
		 * added to components yet; if this is the case, we create a new 
		 * component with both vertices and add it to components
		 */
		
		// we add a singular edge to mazebuilder's edges
		mb.edges.add(new Wallboard(0,0,CardinalDirection.East));
		
		// like before we create to arraylists for comparing
		ArrayList<ArrayList<int[]>> forMethod = new ArrayList<ArrayList<int[]>>(mb.width*mb.height);
		ArrayList<ArrayList<int[]>> forManual = new ArrayList<ArrayList<int[]>>(mb.width*mb.height);
		
		// using method
		mb.assign_components(forMethod);
		
		// manually add the two corresponding vertices
		int[] pt1 = new int[] {0,0};
		int[] pt2 = new int[] {1,0};
		ArrayList<int[]> toAdd = new ArrayList<int[]>(2);
		toAdd.add(pt1);
		toAdd.add(pt2);
		forManual.add(toAdd);
		
		// verify that they are equivalent
		assertEquals(forMethod.get(0).get(0)[0],forManual.get(0).get(0)[0]);
		assertEquals(forMethod.get(0).get(0)[1],forManual.get(0).get(0)[0]);
		assertEquals(forMethod.get(0).get(1)[0],forManual.get(0).get(1)[0]);
		assertEquals(forMethod.get(0).get(1)[1],forManual.get(0).get(1)[1]);
	}
	
	@Test
	void testAssignComponentsAfterOneNull() {
		/*
		 * in the next case, there will be one vertex already in one of the components;
		 * because of that, all we need to do is add the other vertex belonging to the
		 * edge to the component that the first vertex belongs to
		 */
		
		// add our edge to edges
		mb.edges.add(new Wallboard(0,0,CardinalDirection.East));
		
		// create two arraylists for comparing
		ArrayList<ArrayList<int[]>> forMethod = new ArrayList<ArrayList<int[]>>(mb.width*mb.height);
		ArrayList<ArrayList<int[]>> forManual = new ArrayList<ArrayList<int[]>>(mb.width*mb.height);
		
		// these are the vertices corresponding to the edge
		int[] pt1 = new int[] {0,0};
		int[] pt2 = new int[] {1,0};
		
		// we add one of the vertices to a component and then add that
		// component to components
		ArrayList<int[]> toAddMethod = new ArrayList<int[]>(2);
		toAddMethod.add(pt1);
		forMethod.add(toAddMethod);
		
		// call the method
		mb.assign_components(forMethod);
		
		// we now do the same thing manually
		ArrayList<int[]> toAddManual = new ArrayList<int[]>(2);
		toAddManual.add(pt1);
		toAddManual.add(pt2);
		forManual.add(toAddManual);
		
		// compare arraylists
		assertEquals(forMethod.get(0).get(0)[0],forManual.get(0).get(0)[0]);
		assertEquals(forMethod.get(0).get(0)[1],forManual.get(0).get(0)[0]);
		assertEquals(forMethod.get(0).get(1)[0],forManual.get(0).get(1)[0]);
		assertEquals(forMethod.get(0).get(1)[1],forManual.get(0).get(1)[1]);
	}
	
	@Test
	void testAssignComponentsAfterNeitherNull() {
		/*
		 * our last case is when both vertices corresponding to the edge
		 * are already in components; now, we must remove both components from
		 * components and then combine each of their vertices together to
		 * create a singular merged component; we then add this back to components
		 */
		
		// add our edge
		mb.edges.add(new Wallboard(0,0,CardinalDirection.East));
		
		// create the two arraylists
		ArrayList<ArrayList<int[]>> forMethod = new ArrayList<ArrayList<int[]>>(mb.width*mb.height);
		ArrayList<ArrayList<int[]>> forManual = new ArrayList<ArrayList<int[]>>(mb.width*mb.height);
		
		// these are the two vertices we will be working with again
		int[] pt1 = new int[] {0,0};
		int[] pt2 = new int[] {1,0};
		
		// now we have to add each vertex to components but they must
		// belong to separate components;
		ArrayList<int[]> toAddMethod1 = new ArrayList<int[]>(2);
		ArrayList<int[]> toAddMethod2 = new ArrayList<int[]>(2);
		toAddMethod1.add(pt1);
		toAddMethod2.add(pt2);
		forMethod.add(toAddMethod1);
		forMethod.add(toAddMethod2);
		
		// call the method
		mb.assign_components(forMethod);
		
		// perform it manually
		ArrayList<int[]> toAddManual = new ArrayList<int[]>(2);
		toAddManual.add(pt1);
		toAddManual.add(pt2);
		forManual.add(toAddManual);
		
		// compare the arraylists
		assertEquals(forMethod.get(0),forManual.get(0));
	}
	
	/*
	 * the next four test our ability to find the corresponding cell to
	 * the current wallboard; since the wallboard must belong to a singular
	 * cell, we find the cell on the other side of the wallboard that 
	 * technically shares it; this checks all four directions
	 */
	
	@Test
	void testGetOtherCellNorth() {
		Wallboard wb = new Wallboard(1, 1, CardinalDirection.North);
		int[] pt = mb.getOtherCell(wb);
		assertEquals(pt[0],1);
		assertEquals(pt[1],0);
	}
	
	@Test
	void testGetOtherCellSouth() {
		Wallboard wb = new Wallboard(1, 1, CardinalDirection.South);
		int[] pt = mb.getOtherCell(wb);
		assertEquals(pt[0],1);
		assertEquals(pt[1],2);
	}
	
	@Test
	void testGetOtherCellEast() {
		Wallboard wb = new Wallboard(1, 1, CardinalDirection.East);
		int[] pt = mb.getOtherCell(wb);
		assertEquals(pt[0],2);
		assertEquals(pt[1],1);
	}
	
	@Test
	void testGetOtherCellWest() {
		Wallboard wb = new Wallboard(1, 1, CardinalDirection.West);
		int[] pt = mb.getOtherCell(wb);
		assertEquals(pt[0],0);
		assertEquals(pt[1],1);
	}

	@Test
	void testGetOtherWallboard() {
		// if all the getOtherCell()'s work, then we only need to test
		// getOtherWallboard() once
		Wallboard wb = new Wallboard(2,2,CardinalDirection.East);
		
		// we need to make sure that the x, y, and direction are all the same
		assertEquals(mb.getOtherWallboard(wb).getX(),new Wallboard(3,2,CardinalDirection.West).getX());
		assertEquals(mb.getOtherWallboard(wb).getY(),new Wallboard(3,2,CardinalDirection.West).getY());
		assertEquals(mb.getOtherWallboard(wb).getDirection(),new Wallboard(3,2,CardinalDirection.West).getDirection());
	}
	
	@Test
	void testGetIdx() {
		// we check to see if we can get the correct array index corresponding
		// to the 2d array of cells; this array holds the weights, so we need
		// to be able to access it to compare the weights of cells
		int x = 2;
		int y = 2;
		CardinalDirection cd = CardinalDirection.East;
		assertEquals(mb.getIdx(new Wallboard(x,y,cd)),2*x + 2*mb.width*y);
	}
}
