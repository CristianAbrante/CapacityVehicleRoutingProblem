/**
 * 
 */
package daa.project.cvrp.moves.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import daa.project.cvrp.IO.ReaderFromFile;
import daa.project.cvrp.moves.IntrarouteSwap;
import daa.project.cvrp.moves.TwoOpt;
import daa.project.cvrp.problem.CVRPClient;
import daa.project.cvrp.problem.CVRPSolution;
import daa.project.cvrp.problem.CVRPSpecification;

/**
 * <h2>TwoOptTest</h2>
 * 
 * @author	Cristian Abrante Dorta
 * @company	University Of La Laguna
 * @date 		21/04/2018
 * @version 1.0.0
 */
public class TwoOptTest1 {
  
  static final double   EPSILON = 10E-6;
  ArrayList<CVRPClient> clients;
  CVRPSpecification     problemInfo;
  ArrayList<Integer>    solutionCodification;
  CVRPSolution          solution;
  TwoOpt                move;
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
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
    this.move = new TwoOpt();
    /*
    reader = new ReaderFromFile("input/test.vrp");
    problemSpecification = reader.getProblemSpecification();
    
    ArrayList<Integer> solutionNum = new ArrayList<Integer>(Arrays.asList(1, 4, 9, 10, 11, -1, 2, 3, 5, -1, 12, 13, 14, -1, 6, 7, -1, 9, -1));
    
    solution = new CVRPSolution(problemSpecification, solutionNum);
    twoOpt = new TwoOpt(solution);*/
  }
  
  @Test
  public void shouldHaveMoreNeighbors() {
      move.setSolution(this.solution);
      assertTrue(move.hasMoreNeighbors());
  }
  
  @Test
  public void shouldHaveLastMoveCostZeroForBasicSolution() {
      move.setSolution(this.solution);
      assertEquals(0.0, move.getLastMoveCost(), EPSILON);
  }
  
  @Test
  public void shouldHaveLastMoveCostZeroForFirstNeighbor() {
      // [1, 2, -1, 3, -1] => [2, 1, -1, 3, -1] 
      move.setSolution(this.solution);
      move.nextNeighbor();
      assertEquals(0.0, move.getLastMoveCost(), EPSILON);
  }
  
  @Test
  public void shouldHaveNeighborCostForFirstNeighborSameAsBaseSolution() {
      move.setSolution(this.solution);
      move.nextNeighbor();
      assertEquals(this.solution.getTotalDistance(), move.getCurrentNeighborCost(), EPSILON);
  }
  
  @Test
  public void shouldNotHaveMoreNeighborsAfterFirstNeighbor() {
      // [1, 2, -1, 3, -1] => [2, 1, -1, 3, -1] 
      move.setSolution(this.solution);
      move.nextNeighbor();
      assertFalse(move.hasMoreNeighbors());
  }
  
  @Test
  public void nextNeighborCallAfterReachedLastNeighborShouldDoNothing() {
      move.setSolution(this.solution);
      move.nextNeighbor();
      CVRPSolution firstNeighborSolution = move.getCurrentNeighbor();
      assertFalse(move.hasMoreNeighbors());
      
      move.nextNeighbor();
      CVRPSolution neighborSolutionAfterFinish = move.getCurrentNeighbor();
      assertEquals(firstNeighborSolution, neighborSolutionAfterFinish);
      
      move.nextNeighbor();
      neighborSolutionAfterFinish = move.getCurrentNeighbor();
      assertEquals(firstNeighborSolution, neighborSolutionAfterFinish);
  }
  
  
  
  /*
  @Test
  public void setSolutionTest() {
    twoOpt.setSolution(null);
    twoOpt.getCurrentNeighbor();
  }
  
  @Test
  public void neighborCostTest() {
    
    twoOpt.nextNeighbor();
    System.out.println(twoOpt.getCurrentNeighborCost());
    assertTrue(Math.abs(twoOpt.getCurrentNeighborCost() - 943.6696648) < EPS);
    
    twoOpt.nextNeighbor();
    assertTrue(twoOpt.hasMoreNeighbors());
    assertTrue(Math.abs(twoOpt.getCurrentNeighborCost() - 944.212704) < EPS);
    
    twoOpt.nextNeighbor();
    assertTrue(twoOpt.hasMoreNeighbors());
    assertTrue(Math.abs(twoOpt.getCurrentNeighborCost() - 905.4028917) < EPS);
    
    twoOpt.nextNeighbor();
    assertTrue(twoOpt.hasMoreNeighbors());
    assertTrue(Math.abs(twoOpt.getCurrentNeighborCost() - 949.236619) < EPS);
    
    twoOpt.nextNeighbor();
    assertTrue(twoOpt.hasMoreNeighbors());
    assertTrue(Math.abs(twoOpt.getCurrentNeighborCost() - 972.269710) < EPS);
    
    twoOpt.nextNeighbor();
    
    twoOpt.nextNeighbor();
    assertTrue(twoOpt.hasMoreNeighbors());
    assertTrue(Math.abs(twoOpt.getCurrentNeighborCost() - 856.526709) < EPS);
  }
  
  @Test
  public void neighborGenerationTest() {
    for (int i = 0; i < 5; ++i) {
      twoOpt.nextNeighbor();
    }
    
    CVRPSolution solu = twoOpt.getCurrentNeighbor();
    assertTrue(solu.getClientId(0,0) == 1);
    assertTrue(solu.getClientId(0,1) == 9);
    assertTrue(solu.getClientId(0,2) == 4);
    assertTrue(solu.getClientId(0,3) == 10);
    assertTrue(solu.getClientId(0,4) == 11);
    assertTrue(solu.getClientId(1,0) == 2);
  }
  
  @Test
  public void moreNeighboursTest() {
    ArrayList<Integer> solutionNum = new ArrayList<Integer>();
    solutionNum.add(2);
    solutionNum.add(-1);
    CVRPSolution smallSolution = new CVRPSolution(solution.getProblemInfo(), solutionNum);
    TwoOpt opt = new TwoOpt(smallSolution);
    
    assertTrue(opt.hasMoreNeighbors());
    opt.nextNeighbor();
    opt.nextNeighbor();
    assertFalse(opt.hasMoreNeighbors());
  }
  
  @Test
  public void NeighboursTest() {
    ArrayList<Integer> vehicleRoutes = new ArrayList<Integer>(Arrays.asList(2, 1, 3, -1, -1, -1));
    CVRPSolution newSolution = new CVRPSolution(problemSpecification, vehicleRoutes);
    twoOpt.setSolution(newSolution);

    assert (!twoOpt.hasMoreNeighbors());
  }*/
}
