package daa.project.crvp.local_search;

import daa.project.crvp.moves.Move;
import daa.project.crvp.problem.CVRPSolution;
import daa.project.crvp.utils.DoubleCompare;

/**
 * Local search algorithm that searches all neighbors of a solution for the best one
 * and continues until the current neighbor is the best of the neighborhood (local optimum)
 * 
 * @author Carlos Dominguez Garcia (alu0100966589)
 * @version 1.0.0
 * @since 1.0.0 (Apr 22, 2018)
 * @file BestNeighborLocalSearch.java
 *
 */
public class BestNeighborLocalSearch extends LocalSearch {
    
    public BestNeighborLocalSearch(Move moveToUse) {
        super(moveToUse);
    }
    
    @Override
    public CVRPSolution findLocalOptimum() {
        boolean isLocalOptimum = true;
        Move move = getMove();
        CVRPSolution currentBestSolution = getBaseSolution();
        
        if (currentBestSolution == null) {
            throw new IllegalAccessError("trying to find a local optimum without setting a base solution");
        }
        
        // While the solution is not locally an optimum, find the best neighbor
        // and try again with that neighbor
        do {
            isLocalOptimum = true;
            move.setSolution(currentBestSolution);
            while (move.hasMoreNeighbors()) {
                move.nextNeighbor();
                if (move.isCurrentNeighborFeasible() && DoubleCompare.lessThan(move.getCurrentNeighborCost(), currentBestSolution.getTotalDistance())) {
                    isLocalOptimum = false;
                    currentBestSolution = move.getCurrentNeighbor();
                }
            }
        } while (!isLocalOptimum);
        
        return currentBestSolution;
    }
    
    @Override
    public void setBaseSolution(CVRPSolution baseSolution) {
    	// TODO Auto-generated method stub
    	super.setBaseSolution(baseSolution);
    	if (!getBaseSolution().isFeasible()) {
    		throw new IllegalAccessError("cannot set unfeasible initial solution");
    	}
    }
    
}
