package daa.project.cvrp.metrics;

import daa.project.cvrp.problem.CVRPSolution;

/**
 * Set of methods that are called during the execution of an algorithm.
 * It is used to take note of some metrics like the time the algorithm
 * takes, number of iterations, etc. 
 * 
 * @author Carlos Dominguez Garcia (alu0100966589)
 * @version 1.0.0
 * @since 1.0.0 (May 1, 2018)
 * @file AlgorithmRecorder.java
 *
 */
public interface AlgorithmRecorder {
    /**
     * Called when the algorithm is about to start
     */
    public void starting();
    
    /**
     * Called when the algorithm is about to finish
     */
    public void finishing();
    
    /**
     * Called when the algorithm is about to do a new iteration
     */
    public void aboutToDoNextIteration();
    
    /**
     * Called when the algorithm finds a better solution than the one that it
     * currently has
     * 
     * @param betterSolution    New better solution
     */
    public void foundBetterSolution(CVRPSolution betterSolution);
}
