package daa.project.crvp.moves;

import daa.project.crvp.problem.CVRPSolution;

/**
 * Swap interoute. Move for VRP where the order of visiting two clients in the
 * same route is swapped.
 * 
 * @author Carlos Dominguez Garcia (alu0100966589)
 * @version 1.0.0
 * @since 1.0.0 (Apr 18, 2018)
 * @file InterouteSwap.java
 *
 */
public class IntrarouteSwap extends Move {

	private int currentFirstPosition = 0;
	private int currentSecondPosition = 1;

	@Override
	public void setSolution(CVRPSolution solution) {
		super.setSolution(solution);
		this.currentFirstPosition = 0;
		this.currentSecondPosition = 1;
	}

	@Override
	public void nextNeighbor() {
		// TODO Update state
		// TODO First check that we have work to do

	}

	@Override
	public int getLastMoveCost() {
		// TODO Return the cost of the move
		/*
		 * [i, , j] MoveCost = -cost(i-1, j) -cost(i, i+1) -cost(j-1, j) -cost(j, j+1)
		 * +cost(i-1, j) +cost(j, i+1) +cost(j-1, i) +cost(i, j+1)
		 */
		return 0;
	}

	@Override
	public int getCost() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isCurrentNeighborFeasible() {
		// FIXME: only true if the base solution is feasible
		// Always true because we are only swapping the order
		// to visit clients in the same route
		return true;
	}

	@Override
	public CVRPSolution getCurrentNeighbor() {
		// TODO Generate solution and return it
		return null;
	}
}
