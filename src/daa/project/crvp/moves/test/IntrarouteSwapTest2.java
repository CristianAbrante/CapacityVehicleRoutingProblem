package daa.project.crvp.moves.test;

import static org.junit.Assert.assertEquals;
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
 * Test for a solution with 6 neighbors
 * 
 * @author Carlos Dominguez Garcia (alu0100966589)
 * @version 1.0.0
 * @since 1.0.0 (Apr 22, 2018)
 * @file IntrarouteSwapTest2.java
 *
 */
public class IntrarouteSwapTest2 {
    
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
                new CVRPClient(3, 4, 1), // ID = 1
                new CVRPClient(4, 3, 1), // ID = 2
                new CVRPClient(6, 1, 3), // ID = 3
                new CVRPClient(0, 1, 2), // ID = 4
                new CVRPClient(1, 0, 1), // ID = 5
                new CVRPClient(2, 2, 9), // ID = 6
        }));
        
        this.problemInfo = new CVRPSpecification(this.clients, 0, 100, 1);
        this.solutionCodification = new ArrayList<>(
                Arrays.asList(new Integer[] { 1, 2, 3, CVRPSolution.SEPARATOR, 4, 5, 6, CVRPSolution.SEPARATOR }));
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
    public void solutionShouldHave6Neighbors() {
        int counter = 0;
        this.uut.setSolution(this.solution);
        while (this.uut.hasMoreNeighbors()) {
            counter += 1;
            this.uut.nextNeighbor();
        }
        assertEquals(6, counter);
    }
    
    @Test
    public void shouldCalculateCorrectFirstNeighbor() {
        // [1, 2, 3, -1, 4, 5, 6 -1] => [2, 1, 3, -1, 4, 5, 6 -1] 
        ArrayList<Integer> neighborSolCodification = new ArrayList<>(
                Arrays.asList(new Integer[] { 2, 1, 3, CVRPSolution.SEPARATOR, 4, 5, 6, CVRPSolution.SEPARATOR }));
        CVRPSolution expectedNeighborSolution = new CVRPSolution(this.problemInfo, neighborSolCodification);
        
        this.uut.setSolution(this.solution);
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        assertEquals(expectedNeighborSolution, this.uut.getCurrentNeighbor());
    }
    
    @Test
    public void shouldHaveRightCostForFirstNeighbor() {
        // [1, 2, 3, -1, 4, 5, 6 -1] => [2, 1, 3, -1, 4, 5, 6 -1] 
        double expectedNewFirstRouteCost = CVRPClient.euclideanDistance(clients.get(0), clients.get(2))
                + CVRPClient.euclideanDistance(clients.get(2), clients.get(1))
                + CVRPClient.euclideanDistance(clients.get(1), clients.get(3))
                + CVRPClient.euclideanDistance(clients.get(3), clients.get(0));
        double expectedNewSecondRouteCost = CVRPClient.euclideanDistance(clients.get(0), clients.get(4))
                + CVRPClient.euclideanDistance(clients.get(4), clients.get(5))
                + CVRPClient.euclideanDistance(clients.get(5), clients.get(6))
                + CVRPClient.euclideanDistance(clients.get(6), clients.get(0));
        double expectedNewSolutionCost = expectedNewFirstRouteCost + expectedNewSecondRouteCost;
        this.uut.setSolution(this.solution);
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        assertEquals(expectedNewSolutionCost, this.uut.getCurrentNeighborCost(), EPSILON);
    }
    
    @Test
    public void shouldHaveRightMoveCostForFirstNeighbor() {
        // [1, 2, 3, -1, 4, 5, 6 -1] => [2, 1, 3, -1, 4, 5, 6 -1] 
        double expectedMoveCost = CVRPClient.euclideanDistance(clients.get(0), clients.get(2))
                + CVRPClient.euclideanDistance(clients.get(2), clients.get(1))
                + CVRPClient.euclideanDistance(clients.get(2), clients.get(1))
                + CVRPClient.euclideanDistance(clients.get(1), clients.get(3))
                - CVRPClient.euclideanDistance(clients.get(0), clients.get(1))
                - CVRPClient.euclideanDistance(clients.get(1), clients.get(2))
                - CVRPClient.euclideanDistance(clients.get(1), clients.get(2))
                - CVRPClient.euclideanDistance(clients.get(2), clients.get(3));
        this.uut.setSolution(this.solution);
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        assertEquals(expectedMoveCost, this.uut.getLastMoveCost(), EPSILON);
    }
    
    @Test
    public void shouldCalculateCorrectSecondNeighbor() {
        // [1, 2, 3, -1, 4, 5, 6 -1] => [3, 2, 1, -1, 4, 5, 6 -1] 
        ArrayList<Integer> neighborSolCodification = new ArrayList<>(
                Arrays.asList(new Integer[] { 3, 2, 1, CVRPSolution.SEPARATOR, 4, 5, 6, CVRPSolution.SEPARATOR }));
        CVRPSolution expectedNeighborSolution = new CVRPSolution(this.problemInfo, neighborSolCodification);
        
        this.uut.setSolution(this.solution);
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        assertEquals(expectedNeighborSolution, this.uut.getCurrentNeighbor());
    }
    
    @Test
    public void shouldHaveRightCostForSecondNeighbor() {
        // [1, 2, 3, -1, 4, 5, 6 -1] => [3, 2, 1, -1, 4, 5, 6 -1] 
        double expectedNewFirstRouteCost = CVRPClient.euclideanDistance(clients.get(0), clients.get(3))
                + CVRPClient.euclideanDistance(clients.get(3), clients.get(2))
                + CVRPClient.euclideanDistance(clients.get(2), clients.get(1))
                + CVRPClient.euclideanDistance(clients.get(1), clients.get(0));
        double expectedNewSecondRouteCost = CVRPClient.euclideanDistance(clients.get(0), clients.get(4))
                + CVRPClient.euclideanDistance(clients.get(4), clients.get(5))
                + CVRPClient.euclideanDistance(clients.get(5), clients.get(6))
                + CVRPClient.euclideanDistance(clients.get(6), clients.get(0));
        double expectedNewSolutionCost = expectedNewFirstRouteCost + expectedNewSecondRouteCost;
        
        this.uut.setSolution(this.solution);
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        assertEquals(expectedNewSolutionCost, this.uut.getCurrentNeighborCost(), EPSILON);
    }
    
    @Test
    public void shouldHaveRightMoveCostForSecondNeighbor() {
        // [1, 2, 3, -1, 4, 5, 6 -1] => [3, 2, 1, -1, 4, 5, 6 -1] 
        double expectedMoveCost = CVRPClient.euclideanDistance(clients.get(0), clients.get(3))
                + CVRPClient.euclideanDistance(clients.get(3), clients.get(2))
                + CVRPClient.euclideanDistance(clients.get(2), clients.get(1))
                + CVRPClient.euclideanDistance(clients.get(1), clients.get(0))
                - CVRPClient.euclideanDistance(clients.get(0), clients.get(1))
                - CVRPClient.euclideanDistance(clients.get(1), clients.get(2))
                - CVRPClient.euclideanDistance(clients.get(2), clients.get(3))
                - CVRPClient.euclideanDistance(clients.get(3), clients.get(0));
        this.uut.setSolution(this.solution);
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        assertEquals(expectedMoveCost, this.uut.getLastMoveCost(), EPSILON);
    }
    
    @Test
    public void shouldCalculateCorrectThirdNeighbor() {
        // [1, 2, 3, -1, 4, 5, 6 -1] => [1, 3, 2, -1, 4, 5, 6 -1] 
        ArrayList<Integer> neighborSolCodification = new ArrayList<>(
                Arrays.asList(new Integer[] { 1, 3, 2, CVRPSolution.SEPARATOR, 4, 5, 6, CVRPSolution.SEPARATOR }));
        CVRPSolution expectedNeighborSolution = new CVRPSolution(this.problemInfo, neighborSolCodification);
        
        this.uut.setSolution(this.solution);
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        assertEquals(expectedNeighborSolution, this.uut.getCurrentNeighbor());
    }
    
    @Test
    public void shouldHaveRightCostForThirdNeighbor() {
        // [1, 2, 3, -1, 4, 5, 6 -1] => [1, 3, 2, -1, 4, 5, 6 -1] 
        double expectedNewFirstRouteCost = CVRPClient.euclideanDistance(clients.get(0), clients.get(1))
                + CVRPClient.euclideanDistance(clients.get(1), clients.get(3))
                + CVRPClient.euclideanDistance(clients.get(3), clients.get(2))
                + CVRPClient.euclideanDistance(clients.get(2), clients.get(0));
        double expectedNewSecondRouteCost = CVRPClient.euclideanDistance(clients.get(0), clients.get(4))
                + CVRPClient.euclideanDistance(clients.get(4), clients.get(5))
                + CVRPClient.euclideanDistance(clients.get(5), clients.get(6))
                + CVRPClient.euclideanDistance(clients.get(6), clients.get(0));
        double expectedNewSolutionCost = expectedNewFirstRouteCost + expectedNewSecondRouteCost;
        
        this.uut.setSolution(this.solution);
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        assertEquals(expectedNewSolutionCost, this.uut.getCurrentNeighborCost(), EPSILON);
    }
    
    @Test
    public void shouldHaveRightMoveCostForThirdNeighbor() {
        // [1, 2, 3, -1, 4, 5, 6 -1] => [1, 3, 2, -1, 4, 5, 6 -1] 
        double expectedMoveCost = CVRPClient.euclideanDistance(clients.get(1), clients.get(3))
                + CVRPClient.euclideanDistance(clients.get(3), clients.get(2))
                + CVRPClient.euclideanDistance(clients.get(3), clients.get(2))
                + CVRPClient.euclideanDistance(clients.get(2), clients.get(0))
                - CVRPClient.euclideanDistance(clients.get(1), clients.get(2))
                - CVRPClient.euclideanDistance(clients.get(2), clients.get(3))
                - CVRPClient.euclideanDistance(clients.get(2), clients.get(3))
                - CVRPClient.euclideanDistance(clients.get(3), clients.get(0));
        this.uut.setSolution(this.solution);
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        assertEquals(expectedMoveCost, this.uut.getLastMoveCost(), EPSILON);
    }
    
    @Test
    public void shouldCalculateCorrectFourthNeighbor() {
        // [1, 2, 3, -1, 4, 5, 6 -1] => [1, 2, 3, -1, 5, 4, 6 -1] 
        ArrayList<Integer> neighborSolCodification = new ArrayList<>(
                Arrays.asList(new Integer[] { 1, 2, 3, CVRPSolution.SEPARATOR, 5, 4, 6, CVRPSolution.SEPARATOR }));
        CVRPSolution expectedNeighborSolution = new CVRPSolution(this.problemInfo, neighborSolCodification);
        
        this.uut.setSolution(this.solution);
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        assertEquals(expectedNeighborSolution, this.uut.getCurrentNeighbor());
    }
    
    @Test
    public void shouldHaveRightCostForFourthNeighbor() {
        // [1, 2, 3, -1, 4, 5, 6 -1] => [1, 2, 3, -1, 5, 4, 6 -1] 
        double expectedNewFirstRouteCost = CVRPClient.euclideanDistance(clients.get(0), clients.get(1))
                + CVRPClient.euclideanDistance(clients.get(1), clients.get(2))
                + CVRPClient.euclideanDistance(clients.get(2), clients.get(3))
                + CVRPClient.euclideanDistance(clients.get(3), clients.get(0));
        double expectedNewSecondRouteCost = CVRPClient.euclideanDistance(clients.get(0), clients.get(5))
                + CVRPClient.euclideanDistance(clients.get(5), clients.get(4))
                + CVRPClient.euclideanDistance(clients.get(4), clients.get(6))
                + CVRPClient.euclideanDistance(clients.get(6), clients.get(0));
        double expectedNewSolutionCost = expectedNewFirstRouteCost + expectedNewSecondRouteCost;
        
        this.uut.setSolution(this.solution);
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        assertEquals(expectedNewSolutionCost, this.uut.getCurrentNeighborCost(), EPSILON);
    }
    
    @Test
    public void shouldHaveRightMoveCostForFourthNeighbor() {
        // [1, 2, 3, -1, 4, 5, 6 -1] => [1, 2, 3, -1, 5, 4, 6 -1] 
        double expectedMoveCost = CVRPClient.euclideanDistance(clients.get(0), clients.get(5))
                + CVRPClient.euclideanDistance(clients.get(5), clients.get(4))
                + CVRPClient.euclideanDistance(clients.get(5), clients.get(4))
                + CVRPClient.euclideanDistance(clients.get(4), clients.get(6))
                - CVRPClient.euclideanDistance(clients.get(0), clients.get(4))
                - CVRPClient.euclideanDistance(clients.get(4), clients.get(5))
                - CVRPClient.euclideanDistance(clients.get(4), clients.get(5))
                - CVRPClient.euclideanDistance(clients.get(5), clients.get(6));
        this.uut.setSolution(this.solution);
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        this.uut.nextNeighbor();
        assertTrue(this.uut.hasMoreNeighbors());
        assertEquals(expectedMoveCost, this.uut.getLastMoveCost(), EPSILON);
    }
    
}
