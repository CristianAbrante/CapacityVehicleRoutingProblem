package daa.project.cvrp.local_search;

import daa.project.cvrp.moves.Move;
import daa.project.cvrp.problem.CVRPSolution;
import daa.project.cvrp.utils.DoubleCompare;

/**
 * VND implementation. The idea of this local search is that a local optimum in a neighborhood structure
 * is not necessary a local optimum in another structure. And a global optimum is an optimum in every
 * structure. So this local search uses a list of neighborhood structures and finds a local optimum
 * that is an optimum in those structures 
 * 
 * @author Carlos Dominguez Garcia (alu0100966589)
 * @version 1.0.0
 * @since 1.0.0 (Apr 30, 2018)
 * @file VariableNeighborhoodDescent.java
 *
 */
public class VariableNeighborhoodDescent extends LocalSearch {
    
    /** List of moves to use */
    private Move[] movesToUse;
    
    /**
     * Creates an instance ofa VND local search
     * 
     * @param movesToUse    List of moves that generate the neighborhood structures.
     * The algorithm has to check that the resulting solution is an optimum in all
     * those structures
     */
    public VariableNeighborhoodDescent(Move[] movesToUse) {
        super(null);
        setMovesToUse(movesToUse);
    }
    
    @Override
    public CVRPSolution findLocalOptimum(CVRPSolution baseSolution) {
        if (baseSolution == null || !baseSolution.isFeasible()) {
            throw new IllegalAccessError("invalid initial solution, it is null or unfeasible");
        }
        int currentMove = 0;
        Move[] movesToUse = getMovesToUse();
        BestNeighborLocalSearch bestNeighborFinder;
        CVRPSolution bestSolutionFoundSoFar = baseSolution;
        
        while (currentMove < movesToUse.length) {
            // Find the local optimum of the current solution with the current neighbor structure
            bestNeighborFinder = new BestNeighborLocalSearch(movesToUse[currentMove]);
            CVRPSolution localOptimumSolution = bestNeighborFinder.findLocalOptimum(baseSolution);
            
            // If the solution found is better than the best found so far -> update it
            // and reset the neighbor structure to use to the first one
            if (DoubleCompare.lessThan(localOptimumSolution.getTotalDistance(), bestSolutionFoundSoFar.getTotalDistance())) {
                bestSolutionFoundSoFar = localOptimumSolution;
                currentMove = 0;
            } else {
                currentMove += 1;
            }
        }
        
        return bestSolutionFoundSoFar;
    }
    
    /** @return The moves to use */
    public Move[] getMovesToUse() {
        return this.movesToUse;
    }
    
    /** @param moves The moves to use*/
    private void setMovesToUse(Move[] moves) {
        if (moves.length <= 0) {
            throw new IllegalArgumentException("invalid number of moves \"" + moves.length + "\"");
        }
        this.movesToUse = moves;
    }
}
