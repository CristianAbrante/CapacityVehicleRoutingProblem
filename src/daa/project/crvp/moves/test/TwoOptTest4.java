/**
 * 
 */
package daa.project.crvp.moves.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import daa.project.crvp.IO.ReaderFromFile;
import daa.project.crvp.moves.TwoOpt;
import daa.project.crvp.problem.CVRPClient;
import daa.project.crvp.problem.CVRPSolution;
import daa.project.crvp.problem.CVRPSpecification;

/**
 * <h2>TwoOptTest4</h2>
 * 
 * @author	Cristian Abrante Dorta
 * @company	University Of La Laguna
 * @date 		27/04/2018
 * @version 1.0.0
 */
public class TwoOptTest4 {
  
  static final double   EPS = 10E-6;
  ArrayList<CVRPClient> clients;
  CVRPSpecification     problemInfo;
  ArrayList<Integer>    solutionCodification;
  CVRPSolution          solution;
  TwoOpt                move;
  ReaderFromFile        reader;
  
  @Before
  public void setUp() throws Exception {
    reader = new ReaderFromFile("input/test.vrp");
    problemInfo = reader.getProblemSpecification();
    
    ArrayList<Integer> solutionNum = new ArrayList<Integer>(Arrays.asList(1, 4, 9, 10, 11, -1, 2, 3, 5, -1, 12, 13, 14, -1, 6, 7, -1, 9, -1));
    
    solution = new CVRPSolution(problemInfo, solutionNum);
    move = new TwoOpt();
  }
  
  @Test(expected = IllegalAccessError.class)
  public void setSolutionTest() {
    move.setSolution(null);
    move.getCurrentNeighbor();
  }
  
  @Test
  public void neighborCostTest() {
    move.setSolution(solution);
    
    move.nextNeighbor();
    assertTrue(Math.abs(move.getCurrentNeighborCost() - 943.6696648) < EPS);
    
    move.nextNeighbor();
    assertTrue(move.hasMoreNeighbors());
    assertTrue(Math.abs(move.getCurrentNeighborCost() - 944.212704) < EPS);
    
    move.nextNeighbor();
    assertTrue(move.hasMoreNeighbors());
    assertTrue(Math.abs(move.getCurrentNeighborCost() - 905.4028917) < EPS);
    
    move.nextNeighbor();
    assertTrue(move.hasMoreNeighbors());
    assertTrue(Math.abs(move.getCurrentNeighborCost() - 949.236619) < EPS);
    
    move.nextNeighbor();
    assertTrue(move.hasMoreNeighbors());
    assertTrue(Math.abs(move.getCurrentNeighborCost() - 972.269710) < EPS);
    
    move.nextNeighbor();
    
    move.nextNeighbor();
    assertTrue(move.hasMoreNeighbors());
    assertTrue(Math.abs(move.getCurrentNeighborCost() - 856.526709) < EPS);
  }
  
  @Test
  public void neighborGenerationTest() {
    move.setSolution(solution);
    for (int i = 0; i < 5; ++i) {
      move.nextNeighbor();
    }
    
    CVRPSolution solu = move.getCurrentNeighbor();
    assertTrue(solu.getClientId(0,0) == 1);
    assertTrue(solu.getClientId(0,1) == 9);
    assertTrue(solu.getClientId(0,2) == 4);
    assertTrue(solu.getClientId(0,3) == 10);
    assertTrue(solu.getClientId(0,4) == 11);
    assertTrue(solu.getClientId(1,0) == 2);
  }
  
  @Test
  public void moreNeighboursTest() {
    ArrayList<Integer> solutionNum = new ArrayList<Integer>(Arrays.asList(2, 3, -1));
    CVRPSolution smallSolution = new CVRPSolution(solution.getProblemInfo(), solutionNum);
    move.setSolution(smallSolution);
    
    assertTrue(move.hasMoreNeighbors());
    move.nextNeighbor();
    assertFalse(move.hasMoreNeighbors());
    move.nextNeighbor();
    assertFalse(move.hasMoreNeighbors());
  }
  
  @Test
  public void NeighboursTest() {
    ArrayList<Integer> vehicleRoutes = new ArrayList<Integer>(Arrays.asList(2, 1, 3, -1, -1, -1));
    CVRPSolution newSolution = new CVRPSolution(problemInfo, vehicleRoutes);
    move.setSolution(newSolution);

    assertTrue(move.hasMoreNeighbors());
  }
}

