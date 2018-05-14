/**
 * TabuSearch.java
 *
 * @author Ángel Igareta (angel@igareta.com)
 * @version 1.0
 * @since 01-05-2018
 */
package daa.project.cvrp.local_search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import daa.project.cvrp.metrics.TimeAndIterationsRecorder;
import daa.project.cvrp.moves.Move;
import daa.project.cvrp.moves.MoveState;
import daa.project.cvrp.problem.CVRPSolution;
import daa.project.cvrp.utils.DoubleCompare;

/**
 * TabuSearch is a metaheuristic that consists on a local search with tabu
 * tenures over the last best seen solutions.
 */
public class TabuSearch extends LocalSearch {

	private static final double PROBABILITY_TO_TAKE = 0.05;
	/** List of moves to use */
	private Move[] movesToUse;
	/** Tabu tenure to use. */
	private int tabuTenure;

	private int maxIterationsWithoutImprovement;

	/** POOL_SIZE */
	final int POOL_SIZE = 150;
	private boolean verbose;
	private TimeAndIterationsRecorder algorithmRecorder;

	/**
	 * @param algorithmRecorder
	 * @param iterationsWithoutImprovement
	 * @param moveToUse
	 */
	public TabuSearch(Move[] movesToUse, int tabuTenure, int maxIterationsWithoutImprovement, boolean verbose,
			TimeAndIterationsRecorder algorithmRecorder) {
		super(null);
		setMovesToUse(movesToUse);
		setTabuTenure(tabuTenure);
		setMaxIterationsWithoutImprovement(maxIterationsWithoutImprovement);
		setVerbose(verbose);
		setAlgorithmRecorder(algorithmRecorder);
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
		CVRPSolution nextSolution = baseSolution;
		CVRPSolution randomNextSolution = baseSolution;
		MoveState nextMoveState = null;
		double bestCost = baseSolution.getTotalDistance();

		boolean solutionImproved = false;
		boolean lookingForFeasible = false;
		int iterationsWithoutImprovement = 0;
		
		algorithmRecorder.starting();
		algorithmRecorder.foundBetterSolution(baseSolution);
		do {
			solutionImproved = false;

			// Finding local optimum of the pool of possible moves. And not tabu.
			int randomIndex = (int) (Math.random() * this.getMovesToUse().length);
			Move randomMove = this.getMovesToUse()[randomIndex];
			randomMove.setSolution(nextSolution);

			while (randomMove.hasMoreNeighbors()) {
				randomMove.nextNeighbor();
				double poolSolutionDistance = randomMove.getCurrentNeighborCost();
				MoveState randomMoveState = randomMove.getState();

				if (randomNextSolution.equals(nextSolution) || DoubleCompare.lessThan(Math.random(), PROBABILITY_TO_TAKE)) {
					randomNextSolution = randomMove.getCurrentNeighbor();
				}

				if (DoubleCompare.lessThan(poolSolutionDistance, nextSolution.getTotalDistance())
						&& (!tabuTenureMoveStates.containsKey(randomMoveState)
								|| DoubleCompare.lessThan(poolSolutionDistance, bestCost)) // Aspiration criteria
						&& (randomMove.isCurrentNeighborFeasible() || !lookingForFeasible)) { // With no feasible option
					nextMoveState = randomMove.getState();
					nextSolution = randomMove.getCurrentNeighbor();
					solutionImproved = true;
				}
			}

			// Found a better solution
			if (solutionImproved) { // Found Optimum local
				if (nextSolution.isFeasible() && DoubleCompare.lessThan(nextSolution.getTotalDistance(), bestCost)) {
					algorithmRecorder.foundBetterSolution(nextSolution);
					bestFeasibleSolution = nextSolution;
					bestCost = nextSolution.getTotalDistance();
				}

				lookingForFeasible = !nextSolution.isFeasible(); // Change looking method
				tabuTenureMoveStates.put(nextMoveState, getTabuTenure()); // Tabu the current state
				if (isVerbose()) {
					System.out.println("IMPROVED " + nextSolution.getTotalDistance());
					System.out.println("FEASIBLE " + nextSolution.isFeasible());
				}
			}
			else { // Another opportunity
				iterationsWithoutImprovement++;

				if (iterationsWithoutImprovement < maxIterationsWithoutImprovement) { // Choose next solution randomly.
					nextSolution = randomNextSolution;
					if (isVerbose()) {
						System.out.println("NEXT RANDOM: " + randomNextSolution.getTotalDistance());
						System.out.println("NO IMPROVED " + nextSolution.getTotalDistance());
						System.out.println("FEASIBLE " + nextSolution.isFeasible());
					}

					solutionImproved = true;
				}
			}
			updateTabuTenures(tabuTenureMoveStates);
		}
		while (solutionImproved);
		algorithmRecorder.finishing();
		
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
			if (verbose) {
				System.out.println("Tabu Tenure of: " + tabuTenure.getKey() + " -> " + tabuTenure.getValue());
			}
			if (tabuTenure.getValue() == 0) {
				movesToRemove.add(tabuTenure.getKey());
			}
		}

		// Releasing movements with 0 of tabu tenure
		for (MoveState moveState : movesToRemove) {
			tabuTenureMoveStates.remove(moveState);
		}
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

	public TimeAndIterationsRecorder getAlgorithmRecorder() {
		return algorithmRecorder;
	}

	public void setAlgorithmRecorder(TimeAndIterationsRecorder algorithmRecorder) {
		this.algorithmRecorder = algorithmRecorder;
	}

}
