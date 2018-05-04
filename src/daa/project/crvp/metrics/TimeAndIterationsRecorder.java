package daa.project.crvp.metrics;

import daa.project.crvp.problem.CVRPSolution;

/**
 * Class to take note of the time and number iterations needed for an algorithm
 * to finish. Also records the times and iteration number when a better solution
 * was found
 * 
 * @author Carlos Dominguez Garcia (alu0100966589)
 * @version 1.0.0
 * @since 1.0.0 (May 1, 2018)
 * @file TimeAndIterationsRecorder.java
 *
 */
public class TimeAndIterationsRecorder implements AlgorithmRecorder {
    private long startTime  = 0;
    private long finishTime = 0;
    private int numIterations = 0;
    private int    iterationsWhereBetterSolutionFound = 0;
    private long   timesWhenBestSolutionWasSeen       = 0;
    private double solutionsTotalDistance             = 0.0;
    
    public TimeAndIterationsRecorder() {
        reset();
    }
    
    @Override
    public void starting() {
        reset();
        this.startTime = System.currentTimeMillis();
    }
    
    @Override
    public void finishing() {
        this.finishTime = System.currentTimeMillis();
    }
    
    @Override
    public void aboutToDoNextIteration() {
        this.numIterations += 1;
    }
    
    @Override
    public void foundBetterSolution(CVRPSolution betterSolution) {
        solutionsTotalDistance = betterSolution.getTotalDistance();
        iterationsWhereBetterSolutionFound = this.numIterations;
        timesWhenBestSolutionWasSeen = getCurrentTime();
    }
    
    public static final String CSV_SEPARATOR = ";";
    @Override
    public String toString() {
        return (getIterationsWhenBestSolutionWasFound() + CSV_SEPARATOR 
                + getNumIterations() + CSV_SEPARATOR
                + getTimesWhenBestSolutionWasFound() + CSV_SEPARATOR
                + getElapsedTime() + CSV_SEPARATOR
                + getSolutionsTotalDistance()
        );
    }
    
    public long getElapsedTime() {
        return this.finishTime - this.startTime;
    }
    
    public long getCurrentTime() {
      return System.currentTimeMillis() - this.startTime;
    }
    
    public int getNumIterations() {
        return this.numIterations;
    }
    
    public int getIterationsWhenBestSolutionWasFound() {
        return this.iterationsWhereBetterSolutionFound;
    }
    
    public long getTimesWhenBestSolutionWasFound() {
        return this.timesWhenBestSolutionWasSeen;
    }
    
    public double getSolutionsTotalDistance() {
        return this.solutionsTotalDistance;
    }
    
    private void reset() {
        this.startTime = 0;
        this.finishTime = 0;
        this.numIterations = 0;
        this.iterationsWhereBetterSolutionFound = 0;
        this.timesWhenBestSolutionWasSeen = 0;
        this.solutionsTotalDistance = 0.0;
    }
    
}
