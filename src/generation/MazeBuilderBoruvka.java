package generation;

import java.util.ArrayList;

/**
 * This class has the responsibility to create a maze of given dimensions (width, height) 
* together with a solution based on a distance matrix.
* The MazeBuilder implements Runnable such that it can be run a separate thread.
* The MazeFactory has a MazeBuilder and handles the thread management.   

* 
* The maze is built with a randomized version of Boruvka's algorithm. 
* This means that we iterate through each of the cells and tear down the
* cheapest walls in order to make components (groups of cells) until there
* is one component where all cells are connected. This is the same as
* generating the cheapest spanning tree.
*   
* @author Matthew Cheng
*/

public class MazeBuilderBoruvka extends MazeBuilder implements Runnable {
	
	// array for holding the weights of each wallboard
	protected int[] arr;
	
	// keeps track of the vertices in the minimum spanning tree
	protected ArrayList<int[]> vertices = new ArrayList<int[]>();
	
	// keeps track of the edges in the minimum spanning tree (E')
	protected ArrayList<Wallboard> edges = new ArrayList<Wallboard>();
	
	// keeps track of all other edges that could belong to the 
	// minimum spanning tree once we have finished the algorithm
	protected ArrayList<Wallboard> edge_candidates = new ArrayList<Wallboard>();
	
	// constructor
	public MazeBuilderBoruvka() {
		super();
		System.out.println("MazeBuilderBoruvka uses Boruvka's algorithm to generate maze.");
		
	}
	
	// overrides generatePathways so that we can generate the pathways 
	// using Boruvka's algorithm
	@Override
	protected void generatePathways() {
		
		// create the array for weights
		createArray();
		
		// mix the array so that all of the weights are in a random order
		mixArray();
		
		// initialize a forest F to (V,E') where E' = {}
		generate_vertices_and_edge_candidates();
		
		// start out unfinished
		boolean completed = false;
		
		int co = 0;
		// while not all vertices belong to the same component...
		while (!completed) {
			System.out.println("Iteration: " + co);
			co++;
			
			// find the connected components and assign to each vertex its component
			ArrayList<ArrayList<int []>> components = new ArrayList<ArrayList<int []>>(this.width*this.height);
			assign_components(components);
			System.out.println(components);
			System.out.println(components.size());
			
			// initialize the cheapest edge for each component to none
			Wallboard[]cheapest = new Wallboard[components.size()];
			for (int i = 0; i < cheapest.length; i++) {
				cheapest[i] = null;
			}
			System.out.println(cheapest);
			//System.out.println(new int[]{cheapest[0].getX(),cheapest[0].getY()});
			
			// for each edge uv in E where u and v are in different components of F
			for (Wallboard candidate : this.edge_candidates) {
				Wallboard edge = null;
				int[] pt1 = new int[]{candidate.getX(),candidate.getY()};
				int[] pt2 = getOtherCell(candidate);
				ArrayList<int[]> pt1_comp = null;
				ArrayList<int[]> pt2_comp = null;
				// we look through each component in components (does not run if components is empty)
				// if either vertex belonging to uv is found, we remember which component it was in
				for (ArrayList<int[]> comp : components) {
					for (int[] point : comp) {
						if (point[0] == pt1[0] & point[1] == pt1[1]) {
							pt1_comp = comp;
						}
					}
					for (int[] point : comp) {
						if (point[0] == pt2[0] & point[1] == pt2[1]) {
							pt2_comp = comp;
						}
					}
					//if (comp.contains(pt1)) {pt1_comp = comp;}
					//if (comp.contains(pt2)) {pt2_comp = comp;}
				}
				// if the two components are not the same, then we remember that edge
				if (pt1_comp != pt2_comp) {
					edge = candidate;
				}
				if (edge != null) {
					// let wx be the cheapest edge for the component of u
					// if (is-preferred-over(uv,wx) then
						//set uv as the cheapest edge for the component of u
					int idx1 = components.indexOf(pt1_comp);
					if (is_preferred_over(edge,cheapest[idx1])) {
						cheapest[idx1] = edge;
					}
					// let yz be the cheapest edge for the component of v 
					// if (is-preferred-over(uv,yz) then
						//set uv as the cheapest edge for the component of v
					int idx2 = components.indexOf(pt2_comp);
					if (is_preferred_over(edge,cheapest[idx2])) {
						cheapest[idx2] = edge;
					}
				}
			}
			// we count how many of the cheapest edges are still not null
			int count_not_null = 0;
			for (Wallboard wb : cheapest) {
				if (wb != null) {
					count_not_null++;
				}
			}
			// if all components have cheapest edge set to null, we have finished
			if (count_not_null == 0) {
				completed = true;
			}
			// else
			else {
				// for each component whose cheapest edge is not none
				// add its cheapest edge to E'
				completed = false;
				for (Wallboard wb : cheapest) {
					if (wb != null) {
						this.edge_candidates.remove(wb);
						this.edges.add(wb);
					}
				}
			}
		}
		// now that we have the minimum spanning tree, tear down corresponding walls
		for (Wallboard edge : this.edges) {
			this.floorplan.deleteWallboard(edge);
		}
	}
	
	void createArray() {
		// we create an array that corresponds to all of the 
		// south and east walls in the maze
		// initially, the weight is equivalent to the index
		this.arr = new int[2 * this.width * this.height];
		for (int i = 0; i < this.arr.length; i++) {
			this.arr[i] = i;
		}
	}
	
	void mixArray() {
		// since each weight is equivalent to its index, we must now mix
		// it so that all of the values are random (but the same seed will
		// produce the same random outcomes)
		int randomIndex;
		int randomValue;
		
		// we swap each index's weight with a different random index
		for (int i = 0; i < this.arr.length; i++) {
			randomIndex = this.random.nextIntWithinInterval(0,this.arr.length-1);
			
			randomValue = this.arr[randomIndex];
			this.arr[randomIndex] = this.arr[i];
			this.arr[i] = randomValue;
		}
	}
	
	public int[] getArray() {
		// this returns a clone of the weight array so that the actual array
		// cannot be altered this way
		return this.arr.clone();
	}
	
	boolean is_preferred_over(Wallboard edge1, Wallboard edge2) {
		// this checks to see if one edge is preferred over another
		// of the other edge is null, edge1 must be preferred
		if (edge2 == null) {
			return true;
		}
		// if not, we check the weights of both and if edge1 has a lower
		// weight, then it is cheaper so it is preferred
		else if (getEdgeWeight(edge1) < getEdgeWeight(edge2)) {
			return true;
		}
		return false;
	}
	
	public int getEdgeWeight(Wallboard w) {
		// returns the edge weight of a particular wallboard
		// this value is always the same, as weights are stored
		// in the array
		return this.arr[getIdx(w)];
	}
	
	protected void generate_vertices_and_edge_candidates() {
		// in order to avoid any repeated walls, only the south and east
		// wallboards are considered, as this will include all possible
		// wallboards except for the north and west edges, which cannot
		// be torn down
		CardinalDirection[] cardinals = {CardinalDirection.East,CardinalDirection.South};
		
		// we iterate through each set of x,y pairs that make vertices in the
		// 2d array representing the maze
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				// we create an array to represent each vertex and then add it to vertices
				int[] to_add = {x,y};
				this.vertices.add(to_add);
				// we then check the south and east edges of the specific vertex
				for (CardinalDirection cd : cardinals) {
					Wallboard wb = new Wallboard(x,y,cd);
					// if that edge can be torn down, then we add it to the list of 
					// potential candidates for edges
					if (this.floorplan.canTearDown(wb)) {
						this.edge_candidates.add(wb);
					}
				} 
			}
		}
	}
	
	protected void assign_components(ArrayList<ArrayList<int []>> components) {
		// this prepares an arraylist of each component at the beginning of each iteration
		
		// we begin by assuming that this is the first call of the method
		boolean first = true;
		
		// we look through each edge that exists in edges and find the two
		// corresponding points
		for (Wallboard edge : this.edges) {
			int x = edge.getX();
			int y = edge.getY();
			int[] cur_pt = new int[]{x,y};
			int[] alt_pt = getOtherCell(edge);
			// we begin with the component belonging to the vertex equaling null
			ArrayList<int[]> cur_comp = null;
			ArrayList<int[]> alt_comp = null;
			
			// now we iterate through each of the components and check to see if
			// either vertex can be found
			for (ArrayList<int[]> comp : components) {
				// if there are no components yet, then this will never run, so 
				// we skip to the end
				first = false;
				// if found, then we remember which component the vertex was in
				for (int[] point : comp) {
					if (point[0] == cur_pt[0] & point[1] == cur_pt[1]) {
						cur_comp = comp;
					}
				}
				for (int[] point : comp) {
					if (point[0] == alt_pt[0] & point[1] == alt_pt[1]) {
						alt_comp = comp;
					}
				}
				//if (comp.contains(cur_pt)) {cur_comp = comp;}
				//if (comp.contains(alt_pt)) {alt_comp = comp;}
			}
			// if both components were null, then we know that neither is in a component yet
			// we may add them both to a new component and then add that to components
			if (cur_comp == null & alt_comp == null) {
				ArrayList<int[]> new_comp = new ArrayList<int[]>(2);
				new_comp.add(cur_pt);
				new_comp.add(alt_pt);
				components.add(new_comp);
			}
			// if only the current vertex does not have a component yet, then we just add the
			// vertex to the other vertex's component
			else if (cur_comp == null) {
				alt_comp.add(cur_pt);
			}
			// likewise, if only the other vertex does not have a component, we add it to the 
			// current vertex's component
			else if (alt_comp == null) {
				cur_comp.add(alt_pt);
			}
			// if both have a component, then one of the components is removed from components
			// and all of its vertices are added to the other component
			else if (cur_comp != alt_comp) {
				components.remove(alt_comp);
				for (int[] pt : alt_comp) {
					cur_comp.add(pt);
				}
			}
			
		}
		// if instead this was the first iteraion, no edges exist yet, so we just make each
		// vertex its own component
		if (first) {
			for (int[] vertex : this.vertices) {
				int[] point = new int[]{vertex[0],vertex[1]};
				ArrayList<int[]> single_comp = new ArrayList<int[]>(1);
				single_comp.add(point);
				components.add(single_comp);
			}
		}
	}
	
	protected int[] getOtherCell(Wallboard wb) {
		// given a wallboard, it returns the other cell that shares the wallboard
		int x;
		int y;
		x = wb.getX();
		y = wb.getY();
		// the wallboard's direction will determine which of the surrounding
		// cells share the same wallboard
		switch (wb.getDirection()) {
		case East:
			x++;
			break;
		case North:
			y--;
			break;
		case South:
			y++;
			break;
		case West:
			x--;
			break;
		default:
			break;
		}
		// we return a point representing the cell
		return new int[]{x,y};
	}
	
	protected Wallboard getOtherWallboard(Wallboard wb) {
		// given a wallboard, it returns other equivalent wallboard
		
		// we use other cell to find the other cell that shares
		// the wallboard
		int[] cell = getOtherCell(wb);
		// then using the wallboard's direction, we determine that the
		// other identity of the specific wallboard is
		CardinalDirection cd = wb.getDirection();
		switch (cd) {
		case East:
			cd = CardinalDirection.West;
			break;
		case North:
			cd = CardinalDirection.South;
			break;
		case West:
			cd = CardinalDirection.East;
			break;
		case South:
			cd = CardinalDirection.North;
			break;
		default:
			break;
		}
		return new Wallboard(cell[0],cell[1],cd);
	}
	
	protected int getIdx(Wallboard wb) {
		// returns the index of the wallboard's weight in the array
		
		// we must convert the coordinates from the 2d array into indices for
		// the 1d array that stores the weights
		// since we are only dealing with south and east, we convert any north and west
		// wallboards to the other equivalent wallboard first
		if (wb.getDirection() == CardinalDirection.North | wb.getDirection() == CardinalDirection.West) {
			wb = getOtherWallboard(wb);
		}
		// we essentially unwrap the 2d array, merging the ends of each together
		int idx = 2*wb.getX() + 2*wb.getY()*this.width;
		// since there are two wallboards for every one cell, each cell has the east
		// wallboard's weight first, and then the south wallboard's weight second
		// that means that we must add 1 to the index if the direction is south
		if (wb.getDirection() == CardinalDirection.South) {
			idx++;
		}
		return idx;
	}
}
