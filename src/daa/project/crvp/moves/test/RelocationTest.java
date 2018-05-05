package daa.project.crvp.moves.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import daa.project.crvp.IO.ReaderFromFile;
import daa.project.crvp.moves.Move;
import daa.project.crvp.moves.Relocation;
import daa.project.crvp.problem.CVRPSolution;
import daa.project.crvp.problem.CVRPSpecification;
import daa.project.crvp.utils.DoubleCompare;

public class RelocationTest {

	/** Relocation instance. */
	private static Move move;
	/** Problem specification for test. */
	private static CVRPSpecification specification;
	/** Contains the solution */
	private static CVRPSolution solution;
	/** File from where we will get the problem specification. */
	private static final String TEST_FILENAME = "input/test_graphic.vrp";

	/**
	 * Method to generate a random specification.
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		move = new Relocation();

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
		CVRPSolution newSolution = move.getCurrentNeighbor(); // {1, 3, -1, 2, 4, 5, 6, -1, 8, 7, 0, -1}

		assertEquals(1, newSolution.getClientId(0));
		assertEquals(2, newSolution.getClientId(3));

		move.nextNeighbor();
		newSolution = move.getCurrentNeighbor(); // {1, 3, -1, 4, 2, 5, 6, -1, 8, 7, 0, -1}

		assertEquals(1, newSolution.getClientId(0));
		assertEquals(2, newSolution.getClientId(4));

		move.nextNeighbor();
		newSolution = move.getCurrentNeighbor(); // {1, 3, -1, 4, 2, 5, 6, -1, 8, 7, 0, -1}

		assertEquals(1, newSolution.getClientId(0));
		assertEquals(2, newSolution.getClientId(5));
	}

	@Test
	public void testNextNeighborAndSetSolution() { // Check two swaps
		move.setSolution(solution); // {2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1}

		assertEquals(2, solution.getClientId(0));
		assertEquals(4, solution.getClientId(4));

		move.nextNeighbor();
		CVRPSolution newSolution = move.getCurrentNeighbor(); // {1, 3, -1, 2, 4, 5, 6, -1, 8, 7, 0, -1}
		assertEquals(1, newSolution.getClientId(0));
		assertEquals(2, newSolution.getClientId(3));

		move.setSolution(newSolution);
		move.nextNeighbor();
		newSolution = move.getCurrentNeighbor(); // {3, -1, 1, 2, 4, 5, 6, -1, 8, 7, 0, -1}
		assertEquals(3, newSolution.getClientId(0));
		assertEquals(1, newSolution.getClientId(2));

		move.setSolution(newSolution);
		move.nextNeighbor();
		newSolution = move.getCurrentNeighbor(); // {-1, 3, 1 ,4, 2, 5, 6, -1, 8, 7, 0, -1}
		assertEquals(-1, newSolution.getClientId(0));
		assertEquals(3, newSolution.getClientId(1));
	}

	@Test
	public void testNextNeighborAtEnd() { // Check last move
		move.setSolution(solution); // {2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1}

		move.nextNeighbor();
		CVRPSolution lastSolution = move.getCurrentNeighbor(); // {1, 3, -1, 2, 4, 5, 6, -1, 8, 7, 0, -1}
		assertEquals(lastSolution.getClientId(0, 0), 1);
		assertEquals(lastSolution.getClientId(0, 1), 3);
		assertEquals(lastSolution.getClientId(1, 0), 2);
		assertEquals(lastSolution.getClientId(1, 1), 4);
		assertEquals(lastSolution.getClientId(1, 2), 5);
		assertEquals(lastSolution.getClientId(1, 3), 6);
		assertEquals(lastSolution.getClientId(2, 0), 8);
		assertEquals(lastSolution.getClientId(2, 1), 7);
		assertEquals(lastSolution.getClientId(2, 2), 0);

		while (move.hasMoreNeighbors()) {
			move.nextNeighbor();
		}

		lastSolution = move.getCurrentNeighbor(); // {2, 1, 3, 0, -1, 4, 5, 6, -1, 8, 7, -1}

		assertEquals(lastSolution.getClientId(0, 0), 2);
		assertEquals(lastSolution.getClientId(0, 1), 1);
		assertEquals(lastSolution.getClientId(0, 2), 3);
		assertEquals(lastSolution.getClientId(0, 3), 0);
		assertEquals(lastSolution.getClientId(1, 0), 4);
		assertEquals(lastSolution.getClientId(1, 1), 5);
		assertEquals(lastSolution.getClientId(1, 2), 6);
		assertEquals(lastSolution.getClientId(2, 0), 8);
		assertEquals(lastSolution.getClientId(2, 1), 7);

	}

	@Test
	public void testNumberOfMovements() { // Check possible number of movements with k = 3 and V = 9
		move.setSolution(solution); // {2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1}

		int movementCounter = 0;
		while (move.hasMoreNeighbors()) {
			move.nextNeighbor();
			movementCounter++;
		}

		assertEquals(73, movementCounter);
	}

	@Test
	public void shouldDoNothingWithoutNeighbor() { // Should do nothing.
		move.setSolution(solution); // {2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1}

		while (move.hasMoreNeighbors()) {
			move.nextNeighbor();
		}

		CVRPSolution lastSolution = move.getCurrentNeighbor(); // {2, 1, 3, 0, -1, 4, 5, 6, -1, 8, 7, -1}
		assertEquals(lastSolution.getClientId(0, 0), 2);
		assertEquals(lastSolution.getClientId(0, 1), 1);
		assertEquals(lastSolution.getClientId(0, 2), 3);
		assertEquals(lastSolution.getClientId(0, 3), 0);

		lastSolution = move.getCurrentNeighbor();
		assertEquals(lastSolution.getClientId(0, 0), 2);
		assertEquals(lastSolution.getClientId(0, 1), 1);
		assertEquals(lastSolution.getClientId(0, 2), 3);
		assertEquals(lastSolution.getClientId(0, 3), 0);

		lastSolution = move.getCurrentNeighbor();
		assertEquals(lastSolution.getClientId(0, 0), 2);
		assertEquals(lastSolution.getClientId(0, 1), 1);
		assertEquals(lastSolution.getClientId(0, 2), 3);
		assertEquals(lastSolution.getClientId(0, 3), 0);
	}

	@Test
	public void testHasMoreNeighborsOneRoute() { // Should Not have neighbor
		ArrayList<Integer> vehicleRoutes = new ArrayList<Integer>(Arrays.asList(2, 1, 3, -1, -1, -1));
		CVRPSolution newSolution = new CVRPSolution(specification, vehicleRoutes);
		move.setSolution(newSolution);

		assert (move.hasMoreNeighbors());
	}

	@Test
	public void testNextNeighborOneRoute() { // Should Return base solution
		ArrayList<Integer> vehicleRoutes = new ArrayList<Integer>(Arrays.asList(2, 1, 3, -1, -1));
		CVRPSolution newSolution = new CVRPSolution(specification, vehicleRoutes);
		move.setSolution(newSolution);

		assert (move.hasMoreNeighbors());
		assertEquals(newSolution.getClientId(0, 0), 2);
		assertEquals(newSolution.getClientId(0, 1), 1);
		assertEquals(newSolution.getClientId(0, 2), 3);

		move.nextNeighbor();
		CVRPSolution nextSolution = move.getCurrentNeighbor(); // { 1, 3, -1, 2 }

		assert (move.hasMoreNeighbors());
		assertEquals(nextSolution.getClientId(0, 0), 1);
		assertEquals(nextSolution.getClientId(0, 1), 3);
		assertEquals(nextSolution.getClientId(1, 0), 2);
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

		move.nextNeighbor();
		CVRPSolution nextSolution = move.getCurrentNeighbor(); // {2, 1, 3, -1, 4, 5, 0, -1, 8, 7, 6, -1}
		assertEquals(nextSolution.getClientId(0, 0), 1);
		assertEquals(nextSolution.getClientId(1, 0), 2);
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
		assertEquals(nextSolution.getClientId(0, 0), 1);
		assertEquals(nextSolution.getClientId(1, 0), 2);

		move.nextNeighbor();
		nextSolution = move.getCurrentNeighbor();
		assertEquals(nextSolution.getClientId(0, 0), 1);
		assertEquals(nextSolution.getClientId(1, 1), 2);
	}

	@Test
	public void testIsFeasible() { // Test if the next neighbor movement is correct.
		move.setSolution(solution); // {2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1}
		move.nextNeighbor(); // {1, 3, -1, 2, 4, 5, 6, -1, 8, 7, 0, -1}

		ArrayList<Boolean> feasibleArray = new ArrayList<Boolean>();
		// System.out.print("{ ");
		while (move.hasMoreNeighbors()) {
			// System.out.print(move.isCurrentNeighborFeasible() + ", ");
			feasibleArray.add(move.isCurrentNeighborFeasible());
			move.nextNeighbor();
		}
		// System.out.print(" }");

		// Array got creating solution.
		ArrayList<Boolean> correctFeasibleArray = new ArrayList<Boolean>(Arrays
				.asList(new Boolean[] { false, false, false, false, false, false, false, false, false, false, false, false,
						false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
						false, true, true, true, true, false, false, false, false, true, true, true, true, false, false, false,
						false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
						false, false, false, false, false, false, false, false, false, false, false, false, false, false, }));

		assertEquals(feasibleArray, correctFeasibleArray);
	}

	@Test
	public void testGetCost() { // Test if the next neighbor movement is correct.
		move.setSolution(solution); // {2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1}
		move.nextNeighbor(); // {1, 3, -1, 2, 4, 5, 6, -1, 8, 7, 0, -1}

		// Array got creating solution.
		ArrayList<Double> correctCostArray = new ArrayList<Double>(Arrays.asList(new Double[] { 556.8337144354491,
				570.0629040247096, 581.0695263010895, 548.5366087502691, 550.2452188907854, 598.8331167848822,
				548.2478204762056, 570.9360891072932, 514.1590912807119, 480.7801062423082, 432.06894324256405,
				446.144200319408, 429.35214505655114, 434.89199034653547, 453.0021119924138, 518.7384332276139,
				564.0885685098453, 575.8204881833462, 578.6486294827313, 547.6129818476705, 548.3661128111763,
				587.6891420729256, 548.0429930874708, 579.9961303509059, 556.6154331493613, 556.6154331493613,
				567.4595613391286, 574.7144154135249, 568.6960771596927, 639.3159986865512, 570.0615628228793,
				570.7178078212053, 602.2997099744773, 602.2997099744773, 569.1390062221615, 576.7017775500145,
				563.3959158332027, 602.830761437121, 572.434388314532, 604.2757092357981, 567.622055425741, 567.622055425741,
				510.4512926542044, 509.5276657516058, 511.2671685674107, 509.57899199622716, 517.0571926863971,
				590.021535782765, 606.0979152793005, 606.0979152793005, 532.1350972449071, 530.255991165298, 595.1587679560788,
				624.7670548342958, 599.0924502822817, 541.6635416899996, 604.1005168647207, 604.1005168647207,
				553.7876657661899, 553.5828383774551, 594.8356482323732, 626.1325404974823, 609.4964084267975,
				556.4920382903155, 555.5126189706239, 555.5126189706239, 570.9360891072931, 579.9961303509058,
				555.5126189706239, 555.5126189706239, 570.7178078212052, 590.021535782765 }));

		int i = 0;
		ArrayList<Double> costArray = new ArrayList<Double>();
		System.out.print("{ ");
		while (move.hasMoreNeighbors()) {
			System.out.print(move.getCurrentNeighborCost() + ", ");
			assertEquals(move.getCurrentNeighborCost(), (double) correctCostArray.get(i), DoubleCompare.EPSILON);
			costArray.add(move.getCurrentNeighborCost());
			move.nextNeighbor();
			i++;
		}
		System.out.print(" }");

		assertEquals(costArray, correctCostArray);
	}
}
