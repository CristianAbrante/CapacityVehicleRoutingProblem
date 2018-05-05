/**
 * InterrouteSwapTest.java
 *
 * @author Ã�ngel Igareta (angel@igareta.com)
 * @version 1.0
 * @since 21-04-2018
 */
package daa.project.crvp.moves.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import daa.project.crvp.IO.ReaderFromFile;
import daa.project.crvp.moves.InterrouteSwap;
import daa.project.crvp.problem.CVRPSolution;
import daa.project.crvp.problem.CVRPSpecification;

/**
 * Test designed to check if the interroute movements are correct.
 */
public class InterrouteSwapTest {

	/** Interroute instance. */
	private static InterrouteSwap move;
	/** Problem specification for test. */
	private static CVRPSpecification specification;
	/** Contains the solution */
	private static CVRPSolution solution;
	/** File from where we will get the problem specification. */
	private static final String TEST_FILENAME = "input/test_graphic.vrp";

	private final double EPS = 0.3;

	/**
	 * Method to generate a random specification.
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		move = new InterrouteSwap();

		ReaderFromFile reader = new ReaderFromFile(TEST_FILENAME);
		specification = reader.getProblemSpecification();

		ArrayList<Integer> vehicleRoutes = new ArrayList<Integer>(Arrays.asList(2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1));
		solution = new CVRPSolution(specification, vehicleRoutes);
	}

	@Test
	public void testNextNeighbor() { // Test if the next neighbor movement is correct.
		move.setSolution(solution); // {2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1}

		assertEquals(2, solution.getClientId(0));
		assertEquals(4, solution.getClientId(4));

		move.nextNeighbor();
		CVRPSolution newSolution = move.getCurrentNeighbor();

		assertEquals(4, newSolution.getClientId(0));
		assertEquals(2, newSolution.getClientId(4));
	}

	@Test
	public void testNextNeighborAndSetSolution() { // Check two swaps
		move.setSolution(solution); // {2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1}

		assertEquals(2, solution.getClientId(0));
		assertEquals(4, solution.getClientId(4));

		move.nextNeighbor();
		CVRPSolution newSolution = move.getCurrentNeighbor();
		move.setSolution(newSolution); // {4, 1, 3, -1, 2, 5, 6, -1, 8, 7, 0, -1}

		assertEquals(4, newSolution.getClientId(0));
		assertEquals(2, newSolution.getClientId(4));

		move.nextNeighbor(); // {2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1}
		assertEquals(solution, move.getCurrentNeighbor());
	}

	@Test
	public void testNextNeighborAtEnd() { // Check last move
		move.setSolution(solution); // {2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1}

		while (move.hasMoreNeighbors()) {
			move.nextNeighbor();
		}

		CVRPSolution lastSolution = move.getCurrentNeighbor(); // {2, 1, 3, -1, 4, 5, 0, -1, 8, 7, 6, -1}

		assertEquals(lastSolution.getClientId(0, 0), 2);
		assertEquals(lastSolution.getClientId(0, 1), 1);
		assertEquals(lastSolution.getClientId(0, 2), 3);
		assertEquals(lastSolution.getClientId(1, 0), 4);
		assertEquals(lastSolution.getClientId(1, 1), 5);
		assertEquals(lastSolution.getClientId(1, 2), 0);
		assertEquals(lastSolution.getClientId(2, 0), 8);
		assertEquals(lastSolution.getClientId(2, 1), 7);
		assertEquals(lastSolution.getClientId(2, 2), 6);
	}

	@Test
	public void testNumberOfMovements() { // Check possible number of movements with k = 3 and V = 9
		move.setSolution(solution); // {2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1}

		int movementCounter = 0;
		while (move.hasMoreNeighbors()) {
			move.nextNeighbor();
			if (move.hasMoreNeighbors()) {
				movementCounter++;
			}
		}

		assertEquals(27, movementCounter);
	}

	@Test
	public void shouldDoNothingWithoutNeigbor() { // Should do nothing.
		move.setSolution(solution); // {2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1}

		while (move.hasMoreNeighbors()) {
			move.nextNeighbor();
		}

		CVRPSolution lastSolution = move.getCurrentNeighbor(); // {2, 1, 3, -1, 4, 5, 0, -1, 8, 7, 6, -1}
		assertEquals(lastSolution.getClientId(1, 2), 0);
		assertEquals(lastSolution.getClientId(2, 2), 6);

		lastSolution = move.getCurrentNeighbor();
		assertEquals(lastSolution.getClientId(1, 2), 0);
		assertEquals(lastSolution.getClientId(2, 2), 6);

		lastSolution = move.getCurrentNeighbor();
		assertEquals(lastSolution.getClientId(1, 2), 0);
		assertEquals(lastSolution.getClientId(2, 2), 6);
	}

	@Test
	public void testHasMoreNeighborsOneRoute() { // Should Not have neighbor
		ArrayList<Integer> vehicleRoutes = new ArrayList<Integer>(Arrays.asList(2, 1, 3, -1, -1, -1));
		CVRPSolution newSolution = new CVRPSolution(specification, vehicleRoutes);
		move.setSolution(newSolution);

		assert (!move.hasMoreNeighbors());
	}

	@Test
	public void testNextNeighborOneRoute() { // Should Return base solution
		ArrayList<Integer> vehicleRoutes = new ArrayList<Integer>(Arrays.asList(2, 1, 3, -1, -1));
		CVRPSolution newSolution = new CVRPSolution(specification, vehicleRoutes);
		move.setSolution(newSolution);

		CVRPSolution nextSolution = move.getCurrentNeighbor(); // {2, 1, 3}
		assertEquals(nextSolution, newSolution);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNextNeighborNoRoute() { // Should Throw Error
		ArrayList<Integer> vehicleRoutes = new ArrayList<Integer>(Arrays.asList(-1, -1, -1, -1));
		CVRPSolution newSolution = new CVRPSolution(specification, vehicleRoutes);
		move.setSolution(newSolution);

		assert (!move.hasMoreNeighbors());
		move.getCurrentNeighbor();
	}

	@Test
	public void testNextNeighborWithoutOneRouteInMiddle() { // Should jump to next route
		ArrayList<Integer> vehicleRoutes = new ArrayList<Integer>(Arrays.asList(2, 1, 3, -1, -1, 8, 7, 6, -1));
		CVRPSolution newSolution = new CVRPSolution(specification, vehicleRoutes);
		move.setSolution(newSolution);

		assertEquals(newSolution.getClientId(0, 0), 2);
		assertEquals(newSolution.getClientId(2, 0), 8);

		CVRPSolution nextSolution = move.getCurrentNeighbor(); // {2, 1, 3, -1, -1, 8, 7, 6, -1}
		assertEquals(newSolution.getClientId(0, 0), 2);
		assertEquals(newSolution.getClientId(2, 0), 8);

		move.nextNeighbor();
		nextSolution = move.getCurrentNeighbor(); // {2, 1, 3, -1, 4, 5, 0, -1, 8, 7, 6, -1}
		assertEquals(nextSolution.getClientId(0, 0), 8);
		assertEquals(nextSolution.getClientId(2, 0), 2);
	}

	@Test
	public void testNextNeighborWithoutTwoRoutesInMiddle() { // Should jump ToRoute next route
		ArrayList<Integer> vehicleRoutes = new ArrayList<Integer>(Arrays.asList(2, 1, 3, -1, 5, -1, -1, 7, -1));
		CVRPSolution newSolution = new CVRPSolution(specification, vehicleRoutes);
		move.setSolution(newSolution);

		assertEquals(newSolution.getClientId(0, 0), 2);
		assertEquals(newSolution.getClientId(1, 0), 5);

		move.nextNeighbor();
		CVRPSolution nextSolution = move.getCurrentNeighbor();
		assertEquals(nextSolution.getClientId(0, 0), 5);
		assertEquals(nextSolution.getClientId(1, 0), 2);

		move.nextNeighbor();
		nextSolution = move.getCurrentNeighbor();
		assertEquals(nextSolution.getClientId(0, 0), 7);
		assertEquals(nextSolution.getClientId(3, 0), 2);
	}

	@Test
	public void testMovementCost() { // Test if the next neighbor movement is correct.
		move.setSolution(solution); // {2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1}
		assertTrue(Math.abs(move.getCurrentNeighborCost() - 555.472) < EPS);

		move.nextNeighbor(); // {4, 1, 3, -1, 2, 5, 6, -1, 8, 7, 0, -1}
		CVRPSolution newSolution = move.getCurrentNeighbor();

		assertEquals(4, newSolution.getClientId(0));
		assertEquals(2, newSolution.getClientId(4));

		assertTrue(Math.abs(move.getCurrentNeighborCost() - 579.586) < EPS);
		assertTrue(Math.abs(move.getLastMoveCost() - 24.114) < EPS);
	}

	@Test
	public void testIsFeasible() { // Test if the next neighbor movement is correct.
		move.setSolution(solution); // {2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1}
		move.nextNeighbor(); // {4, 1, 3, -1, 2, 5, 6, -1, 8, 7, 0, -1}

		ArrayList<Boolean> feasibleArray = new ArrayList<Boolean>();
		System.out.print("{ ");
		while (move.hasMoreNeighbors()) {
			//System.out.print(move.isCurrentNeighborFeasible() + ", ");
			feasibleArray.add(move.isCurrentNeighborFeasible());
			move.nextNeighbor();
		}
		System.out.print(" }");
		
		// Array got creating solution.
		ArrayList<Boolean> correctFeasibleArray = new ArrayList<Boolean>(Arrays
				.asList(new Boolean[] { false, false, false, false, false, false, false, false, false, false, false, false,
						false, false, false, false, false, false, false, true, true, false, true, true, false, true, false }));
		
		assertEquals(feasibleArray, correctFeasibleArray);
	}
}