/**
 * InterrouteSwapTest.java
 *
 * @author Ángel Igareta (angel@igareta.com)
 * @version 1.0
 * @since 21-04-2018
 */
package daa.project.crvp.moves.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import daa.project.crvp.moves.InterrouteSwap;
import daa.project.crvp.problem.CVRPClient;
import daa.project.crvp.problem.CVRPSolution;
import daa.project.crvp.problem.CVRPSpecification;

/**
 * Test designed to check if the interroute movements are correct.
 */
public class InterrouteSwapTest {

	/** Interroute instance. */
	private InterrouteSwap move;
	/** Problem specification for test. */
	private CVRPSpecification specification;

	/** SPECIFICIATION PARAMETERS */
	private final int NUMBER_OF_CLIENTS = 9;
	private final int NUMBER_OF_ROUTES = 3;
	private final int CAPACITY = 50;
	private final int MAX_COORDINATE = 100;

	/**
	 * Method to generate a random specification.
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	public void setUp() throws Exception {
		this.move = new InterrouteSwap();
		this.specification = generateRandomSpecification(NUMBER_OF_CLIENTS, NUMBER_OF_ROUTES, CAPACITY);
	}

	/**
	 * Method that generate a random specification depending on the parameters
	 * received.
	 * 
	 * @param numberOfClients
	 *          Number of clients in the problem.
	 * @param numberOfRoutes
	 *          Number of routes in the problem.
	 * @param capacity
	 *          Capacity of the vehicles.
	 * @return New problem specification.
	 */
	public CVRPSpecification generateRandomSpecification(int numberOfClients, int numberOfRoutes, int capacity) {
		ArrayList<CVRPClient> clientArray = new ArrayList<CVRPClient>();
		int demandPerClient = capacity / (numberOfClients / numberOfRoutes); // All equal demand

		for (int i = 0; i < numberOfClients; ++i) {
			Random rand = new Random();
			clientArray.add(new CVRPClient(rand.nextInt(MAX_COORDINATE), rand.nextInt(MAX_COORDINATE), demandPerClient));
		}

		return new CVRPSpecification(clientArray, 0, capacity, numberOfRoutes);
	}

	/**
	 * Test if the next neighbor movement is correct.
	 * 
	 * Test method for {@link daa.project.crvp.moves.InterrouteSwap#nextNeighbor()}.
	 */
	@Test
	void testNextNeighbor() {
		ArrayList<Integer> vehicleRoutes = new ArrayList<Integer>(Arrays.asList(2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1));
		CVRPSolution solution = new CVRPSolution(this.specification, vehicleRoutes);

		assertEquals(2, solution.getClientId(0));
		assertEquals(4, solution.getClientId(4));

		move.setSolution(solution);
		move.nextNeighbor();
		CVRPSolution newSolution = move.getCurrentNeighbor();

		assertEquals(4, newSolution.getClientId(0));
		assertEquals(2, newSolution.getClientId(4));
	}

	/**
	 * Test if the possible number of movements with 3 routes and 9 clients is
	 * correct.
	 */
	@Test
	void testNumberOfMovements() {
		ArrayList<Integer> vehicleRoutes = new ArrayList<Integer>(Arrays.asList(2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1));
		CVRPSolution solution = new CVRPSolution(this.specification, vehicleRoutes);
		move.setSolution(solution);

		int movementCounter = 0;
		while (move.hasMoreNeighbors()) {
			move.nextNeighbor();
			if (move.hasMoreNeighbors()) {
				movementCounter++;
			}
		}

		assertEquals(27, movementCounter);
	}

	/**
	 * Test method for
	 * {@link daa.project.crvp.moves.InterrouteSwap#getLastMoveCost()}.
	 */
	@Test
	void testGetLastMoveCost() {
	}

	/**
	 * Test method for {@link daa.project.crvp.moves.InterrouteSwap#getCost()}.
	 */
	@Test
	void testGetCost() {
	}

	/**
	 * Test method for
	 * {@link daa.project.crvp.moves.InterrouteSwap#isCurrentNeighborFeasible()}.
	 */
	@Test
	void testIsCurrentNeighborFeasible() {
	}

	/**
	 * Test method for
	 * {@link daa.project.crvp.moves.InterrouteSwap#getCurrentNeighbor()}.
	 */
	@Test
	void testGetCurrentNeighbor() {
	}

}
