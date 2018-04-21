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
 * <h2>TwoOptTest</h2>
 * 
 * @author	Cristian Abrante Dorta
 * @company	University Of La Laguna
 * @date 		21/04/2018
 * @version 1.0.0
 */
public class TwoOptTest {
  
  private final double EPS = 0.000001;
  
  CVRPSpecification problemSpecification;
  ReaderFromFile reader;
  CVRPSolution solution;
  TwoOpt twoOpt;
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    reader = new ReaderFromFile("input/test.vrp");
    problemSpecification = reader.getProblemSpecification();
    
    ArrayList<Integer> solutionNum = new ArrayList<Integer>(Arrays.asList(1, 4, 9, 10, 11, -1, 2, 3, 5, -1, 12, 13, 14, -1, 6, 7, -1, 9, -1));
    
    solution = new CVRPSolution(problemSpecification, solutionNum);
    twoOpt = new TwoOpt(solution);
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
}
