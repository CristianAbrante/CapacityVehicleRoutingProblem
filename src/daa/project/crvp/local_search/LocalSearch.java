package daa.project.crvp.local_search;

import daa.project.crvp.moves.Move;
import daa.project.crvp.problem.CVRPSolution;

/**
 * Representation of a general local search: algorithm that given a solution
 * and a move, searches the neighbors of that solution for a local optimum
 * 
 * @author Carlos Dominguez Garcia (alu0100966589)
 * @version 1.0.0
 * @since 1.0.0 (Apr 22, 2018)
 * @file LocalSearch.java
 *
 */
public abstract class LocalSearch {
    /** Move used to explore the neighbors of the solutions */
    private Move moveToUse;
    /** Starting solution from which the local search has to find a local optimum */
    private CVRPSolution baseSolution;
    
    /**
     * Create a local search with the given move used to explore
     * neighbor solutions
     * 
     * @param moveToUse Move to use to explore neighbor solutions
     */
    public LocalSearch(Move moveToUse) {
        setMoveToUse(moveToUse);
    }
    
    /**
     * Return the local optimum found using the base solution set 
     * in setMoveToUse and the move specified in the construction
     * of this instance
     * 
     * @return A solution that is local optimum
     */
    public abstract CVRPSolution findLocalOptimum();
    
    /**
     * Returns the base solution
     * 
     * @return The base solution
     */
    public CVRPSolution getBaseSolution() {
        return this.baseSolution;
    }
    
    /**
     * Sets the base solution
     * 
     * @param baseSolution Base solution to set
     */
    public void setBaseSolution(CVRPSolution baseSolution) {
        this.baseSolution = baseSolution;
    }
    
    /** @return the moveToUse */
    public Move getMove() {
        return this.moveToUse;
    }
    
    /** @param moveToUse the moveToUse to set */
    private void setMoveToUse(Move moveToUse) {
        this.moveToUse = moveToUse;
    }
    
}