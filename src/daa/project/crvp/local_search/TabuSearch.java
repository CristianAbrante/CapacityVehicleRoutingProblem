/**
 * TabuSearch.java
 *
 * @author Ángel Igareta (angel@igareta.com)
 * @version 1.0
 * @since 01-05-2018
 */
package daa.project.crvp.local_search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import daa.project.crvp.moves.Move;
import daa.project.crvp.moves.MoveState;
import daa.project.crvp.problem.CVRPSolution;
import daa.project.crvp.utils.DoubleCompare;

/**
 * TabuSearch is a metaheuristic that consists on a local search with tabu
 * tenures over the last best seen solutions.
 */
public class TabuSearch extends LocalSearch {

	/** List of moves to use */
	private Move[] movesToUse;
	/** Tabu tenure to use. */
	private int tabuTenure;

	private int maxIterationsWithoutImprovement;

	/** POOL_SIZE */
	final int POOL_SIZE = 150;
	private boolean verbose;

	/**
	 * @param iterationsWithoutImprovement
	 * @param moveToUse
	 */
	public TabuSearch(Move[] movesToUse, int tabuTenure, int maxIterationsWithoutImprovement, boolean verbose) {
		super(null);
		setMovesToUse(movesToUse);
		setTabuTenure(tabuTenure);
		setMaxIterationsWithoutImprovement(maxIterationsWithoutImprovement);
		setVerbose(verbose);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * daa.project.crvp.local_search.LocalSearch#findLocalOptimum(daa.project.crvp.
	 * problem.CVRPSolution)
	 */
	@Override
	public CVRPSolution findLocalOptimum(CVRPSolution baseSolution) {
		if (baseSolution == null || !baseSolution.isFeasible()) {
			throw new IllegalAccessError("invalid initial solution, it is null or unfeasible");
		}
		// Step 1 : Initialization
		HashMap<MoveState, Integer> tabuTenureMoveStates = new HashMap<MoveState, Integer>();
		CVRPSolution bestFeasibleSolution = baseSolution;
		double bestCost = baseSolution.getTotalDistance();

		boolean solutionImproved = false;
		boolean lookingForFeasible = false;
		int iterationsWithoutImprovement = 0;
		MoveState nextMoveState = new MoveState(null, null, baseSolution);
		do {
			solutionImproved = false;

			// Finding local optimum of the pool of possible moves. And not tabu.
			ArrayList<MoveState> nextMoveStatePool = generateNextMoveStatePool(nextMoveState.getMoveSolution());
			for (MoveState poolNextMoveState : nextMoveStatePool) {
				double poolSolutionDistance = poolNextMoveState.getMoveSolution().getTotalDistance();

				if (DoubleCompare.lessThan(poolSolutionDistance, nextMoveState.getMoveSolution().getTotalDistance())
						&& (!tabuTenureMoveStates.containsKey(poolNextMoveState)
								|| DoubleCompare.lessThan(poolSolutionDistance, bestCost)) // Aspiration criteria
						&& (poolNextMoveState.getMoveSolution().isFeasible() || !lookingForFeasible)) { // With no feasible option
					nextMoveState = poolNextMoveState;
					solutionImproved = true;
				}
			}

			// Found a better solution
			if (solutionImproved) { // Found Optimum local
				if (nextMoveState.getMoveSolution().isFeasible()
						&& DoubleCompare.lessThan(nextMoveState.getMoveSolution().getTotalDistance(), bestCost)) {
					bestFeasibleSolution = nextMoveState.getMoveSolution();
					bestCost = nextMoveState.getMoveSolution().getTotalDistance();
				}

				lookingForFeasible = !nextMoveState.getMoveSolution().isFeasible(); // Change looking method
				tabuTenureMoveStates.put(nextMoveState, getTabuTenure()); // Tabu the current state
				if (isVerbose()) {
					System.out.println("IMPROVED " + nextMoveState.getMoveSolution().getTotalDistance());
					System.out.println("FEASIBLE " + nextMoveState.getMoveSolution().isFeasible());
				}
			}
			else { // Another opportunity
				iterationsWithoutImprovement++;

				if (iterationsWithoutImprovement < maxIterationsWithoutImprovement) { // Choose next solution randomly.
					nextMoveState = nextMoveStatePool.get((int) (Math.random() * nextMoveStatePool.size()));
					if (isVerbose()) {
						System.out.println("NO IMPROVED " + nextMoveState.getMoveSolution().getTotalDistance());
						System.out.println("FEASIBLE " + nextMoveState.getMoveSolution().isFeasible());
					}

					solutionImproved = true;
				}
			}
			updateTabuTenures(tabuTenureMoveStates);
		}
		while (solutionImproved);

		return bestFeasibleSolution;
	}

	/**
	 * @param tabuTenureMoveStates
	 */
	private void updateTabuTenures(HashMap<MoveState, Integer> tabuTenureMoveStates) {
		// System.out.println("Updating tabu tenures...");
		ArrayList<MoveState> movesToRemove = new ArrayList<MoveState>();

		// Subtracting 1 of tabu tenure and determine the moves to remove.
		for (Map.Entry<MoveState, Integer> tabuTenure : tabuTenureMoveStates.entrySet()) {
			tabuTenure.setValue(tabuTenure.getValue() - 1);
			// System.out.println("Tabu Tenure of: " + tabuTenure.getKey() + " -> " +
			// tabuTenure.getValue());
			if (tabuTenure.getValue() == 0) {
				movesToRemove.add(tabuTenure.getKey());
			}
		}

		// Releasing movements with 0 of tabu tenure
		for (MoveState moveState : movesToRemove) {
			tabuTenureMoveStates.remove(moveState);
		}
	}

	/**
	 * Method that generates a pool of solutions combining the avaliable movements
	 * randomly.
	 * 
	 * @param currentSolution
	 * @return
	 */
	private ArrayList<MoveState> generateNextMoveStatePool(CVRPSolution currentSolution) {
		ArrayList<MoveState> nextMoveStatePool = new ArrayList<MoveState>();

		// Initializing avaliableMoves
		for (Move move : getMovesToUse()) {
			move.setSolution(currentSolution);
		}

		// Fill the pool with solutions of random moves.
		if (false) { // IN ORDER TO TEST -> THIS IS WORSE BUT..
			for (int i = 0; i < POOL_SIZE; ++i) {
				int randomIndex = (int) (Math.random() * this.getMovesToUse().length);
				Move randomMove = this.getMovesToUse()[randomIndex];

				if (randomMove.hasMoreNeighbors()) {
					randomMove.nextNeighbor();
					nextMoveStatePool.add(randomMove.getState());
				}
			}
		}
		else {
			int randomIndex = (int) (Math.random() * this.getMovesToUse().length);
			Move randomMove = this.getMovesToUse()[randomIndex];
			while (randomMove.hasMoreNeighbors()) {
				randomMove.nextNeighbor();
				nextMoveStatePool.add(randomMove.getState());
			}
		}

		return nextMoveStatePool;
	}

	/** @return The moves to use */
	public Move[] getMovesToUse() {
		return this.movesToUse;
	}

	/**
	 * @param moves
	 *          The moves to use
	 */
	private void setMovesToUse(Move[] moves) {
		if (moves.length <= 0) {
			throw new IllegalArgumentException("invalid number of moves \"" + moves.length + "\"");
		}
		this.movesToUse = moves;
	}

	/**
	 * @return the tabuTenure
	 */
	public int getTabuTenure() {
		return tabuTenure;
	}

	/**
	 * @param tabuTenure
	 *          the tabuTenure to set
	 */
	public void setTabuTenure(int tabuTenure) {
		if (tabuTenure < 0) {
			throw new IllegalArgumentException("Invalid tabu tenure: \"" + tabuTenure + "\"");
		}
		this.tabuTenure = tabuTenure;
	}

	/**
	 * @return the iterationsWithoutImprovement
	 */
	public int getMaxIterationsWithoutImprovement() {
		return maxIterationsWithoutImprovement;
	}

	/**
	 * @param iterationsWithoutImprovement
	 *          the iterationsWithoutImprovement to set
	 */
	public void setMaxIterationsWithoutImprovement(int maxIterationsWithoutImprovement) {
		this.maxIterationsWithoutImprovement = maxIterationsWithoutImprovement;
	}

	/**
	 * @return the verbose
	 */
	public boolean isVerbose() {
		return verbose;
	}

	/**
	 * @param verbose
	 *          the verbose to set
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

}
