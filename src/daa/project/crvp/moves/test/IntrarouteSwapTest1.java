package daa.project.crvp.moves.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import daa.project.crvp.moves.IntrarouteSwap;
import daa.project.crvp.problem.CVRPClient;
import daa.project.crvp.problem.CVRPSolution;
import daa.project.crvp.problem.CVRPSpecification;

/**
 * Tests using a simple solution
 * 
 * @author Carlos Dominguez Garcia (alu0100966589)
 * @version 1.0.0
 * @since 1.0.0 (Apr 22, 2018)
 * @file IntrarouteSwapTest.java
 *
 */
public class IntrarouteSwapTest1 {
    
    static final double   EPSILON = 10E-6;
    ArrayList<CVRPClient> clients;
    CVRPSpecification     problemInfo;
    ArrayList<Integer>    solutionCodification;
    CVRPSolution          solution;
    IntrarouteSwap        uut;
    
    @Before
    public void initialize() {
        this.clients = new ArrayList<>(Arrays.asList(new CVRPClient[] { 
                new CVRPClient(0, 0, 0), // ID = 0, depot
                new CVRPClient(2, 2, 9), // ID = 1
                new CVRPClient(3, 3, 1), // ID = 2
                new CVRPClient(4, 4, 99), // ID = 3
        }));
        
        this.problemInfo = new CVRPSpecification(this.clients, 0, 100, 1);
        this.solutionCodification = new ArrayList<>(
                Arrays.asList(new Integer[] { 1, 2, CVRPSolution.SEPARATOR, 3, CVRPSolution.SEPARATOR }));
        this.solution = new CVRPSolution(this.problemInfo, this.solutionCodification);
        this.uut = new IntrarouteSwap();
    }
    
    @Test
    public void shouldHaveMoreNeighbors() {
        this.uut.setSolution(this.solution);
        assertTrue(this.uut.hasMoreNeighbors());
    }
    
    @Test
    public void shouldHaveLastMoveCostZeroForBasicSolution() {
        this.uut.setSolution(this.solution);
        assertEquals(0.0, this.uut.getLastMoveCost(), EPSILON);
    }
    
    @Test
    public void shouldHaveLastMoveCostZeroForFirstNeighbor() {
        // [1, 2, -1, 3, -1] => [2, 1, -1, 3, -1] 
        this.uut.setSolution(this.solution);
        this.uut.nextNeighbor();
        assertEquals(0.0, this.uut.getLastMoveCost(), EPSILON);
    }
    
    @Test
    public void shouldHaveNeighborCostForFirstNeighborSameAsBaseSolution() {
        this.uut.setSolution(this.solution);
        this.uut.nextNeighbor();
        assertEquals(this.solution.getTotalDistance(), this.uut.getCurrentNeighborCost(), EPSILON);
    }
    
    @Test
    public void shouldNotHaveMoreNeighborsAfterFirstNeighbor() {
        this.uut.setSolution(this.solution);
        this.uut.nextNeighbor();
        assertFalse(this.uut.hasMoreNeighbors());
    }
    
    @Test
    public void nextNeighborCallAfterReachedLastNeighborShouldDoNothing() {
        this.uut.setSolution(this.solution);
        this.uut.nextNeighbor();
        CVRPSolution firstNeighborSolution = this.uut.getCurrentNeighbor();
        assertFalse(this.uut.hasMoreNeighbors());
        
        this.uut.nextNeighbor();
        CVRPSolution neighborSolutionAfterFinish = this.uut.getCurrentNeighbor();
        assertEquals(firstNeighborSolution, neighborSolutionAfterFinish);
        
        this.uut.nextNeighbor();
        neighborSolutionAfterFinish = this.uut.getCurrentNeighbor();
        assertEquals(firstNeighborSolution, neighborSolutionAfterFinish);
    }
}
