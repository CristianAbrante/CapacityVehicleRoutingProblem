package daa.project.crvp.metrics;

import java.util.ArrayList;
import java.util.stream.Collectors;

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
    private ArrayList<Integer> iterationsWhereBetterSolutionFound = new ArrayList<>();
    private ArrayList<Long>    timesWhenBestSolutionWasSeen       = new ArrayList<>();
    private ArrayList<Double>  SolutionsTotalDistance             = new ArrayList<>();
    
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
        SolutionsTotalDistance.add(betterSolution.getTotalDistance());
        iterationsWhereBetterSolutionFound.add(this.numIterations);
        timesWhenBestSolutionWasSeen.add(System.currentTimeMillis());
    }
    
    @Override
    public String toString() {
        return ("\n\tTime: "
                + getElapsedTime() 
                + "\n\tNumber iterations: "
                + getNumIterations()
                + "\n\tIterations when best solution was found: ["
                + getIterationsWhenBestSolutionWasFound().stream().map(Object::toString).collect(Collectors.joining(", "))
                + "]\n\tTimes when best solution was found: ["
                + getTimesWhenBestSolutionWasFound().stream().map(Object::toString).collect(Collectors.joining(", "))
                + "]\n\tSolutions total distance: ["
                + getSolutionsTotalDistance().stream().map(Object::toString).collect(Collectors.joining(", ")) 
                + "]\n"
        );
    }
    
    public long getElapsedTime() {
        return this.finishTime - this.startTime;
    }
    
    public int getNumIterations() {
        return this.numIterations;
    }
    
    public ArrayList<Integer> getIterationsWhenBestSolutionWasFound() {
        return this.iterationsWhereBetterSolutionFound;
    }
    
    public ArrayList<Long> getTimesWhenBestSolutionWasFound() {
        return this.timesWhenBestSolutionWasSeen;
    }
    
    public ArrayList<Double> getSolutionsTotalDistance() {
        return this.SolutionsTotalDistance;
    }
    
    private void reset() {
        this.startTime = 0;
        this.finishTime = 0;
        this.numIterations = 0;
        this.iterationsWhereBetterSolutionFound = new ArrayList<>();
        this.timesWhenBestSolutionWasSeen = new ArrayList<>();
        this.SolutionsTotalDistance = new ArrayList<>();
    }
    
}
