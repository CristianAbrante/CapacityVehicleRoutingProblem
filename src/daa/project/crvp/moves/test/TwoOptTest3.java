/**
 * 
 */
package daa.project.crvp.moves.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import daa.project.crvp.IO.ReaderFromFile;
import daa.project.crvp.moves.InterrouteSwap;
import daa.project.crvp.moves.TwoOpt;
import daa.project.crvp.problem.CVRPClient;
import daa.project.crvp.problem.CVRPSolution;
import daa.project.crvp.problem.CVRPSpecification;

/**
 * <h2>TwoOptTest3</h2>
 * 
 * @author	Cristian Abrante Dorta
 * @company	University Of La Laguna
 * @date 		23/04/2018
 * @version 1.0.0
 */
public class TwoOptTest3 {
  
  private static final String TEST_FILENAME = "input/test_graphic.vrp";
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
    move = new TwoOpt();

    ReaderFromFile reader = new ReaderFromFile(TEST_FILENAME);
    problemInfo = reader.getProblemSpecification();

    ArrayList<Integer> vehicleRoutes = new ArrayList<Integer>(Arrays.asList(2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1));
    solution = new CVRPSolution(problemInfo, vehicleRoutes);
  }

  @Test
  public void testNextNeighbor() { // Test if the next neighbor movement is correct.
    move.setSolution(solution); 
    assertEquals(2, solution.getClientId(0));
    assertEquals(1, solution.getClientId(1));

    // [2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1] -> [1, 2, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1]
    move.nextNeighbor();
    CVRPSolution newSolution = move.getCurrentNeighbor();

    assertEquals(1, newSolution.getClientId(0));
    assertEquals(2, newSolution.getClientId(1));
    assertEquals(3, newSolution.getClientId(2));
  }
  
  @Test
  public void testNextNeighborAndSetSolution() { // Check two swaps
    move.setSolution(solution); // {2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1}
    assertEquals(2, solution.getClientId(0));
    assertEquals(1, solution.getClientId(1));
    
    // [2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1] -> [1, 2, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1]
    move.nextNeighbor();
    CVRPSolution newSolution = move.getCurrentNeighbor();
    
    // [1, 2, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1]
    move.setSolution(newSolution);
    assertEquals(1, newSolution.getClientId(0));
    assertEquals(2, newSolution.getClientId(1));
    
    // [1, 2, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1] -> [2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1]
    move.nextNeighbor();
    assertEquals(solution, move.getCurrentNeighbor());
  }
  

  @Test
  public void testNextNeighborAtEnd() { // Check last move
    move.setSolution(solution); // {2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1}

    while (move.hasMoreNeighbors()) {
      move.nextNeighbor();
    }

    CVRPSolution lastSolution = move.getCurrentNeighbor(); // {2, 1, 3, -1, 4, 5, 6, -1, 8, 0, 7, -1}

    assertEquals(lastSolution.getClientId(0, 0), 2);
    assertEquals(lastSolution.getClientId(0, 1), 1);
    assertEquals(lastSolution.getClientId(0, 2), 3);
    assertEquals(lastSolution.getClientId(1, 0), 4);
    assertEquals(lastSolution.getClientId(1, 1), 5);
    assertEquals(lastSolution.getClientId(1, 2), 6);
    assertEquals(lastSolution.getClientId(2, 0), 8);
    assertEquals(lastSolution.getClientId(2, 1), 0);
    assertEquals(lastSolution.getClientId(2, 2), 7);
  }

  @Test
  public void testNumberOfMovements() { // Check possible number of movements with k = 3 and V = 9
    move.setSolution(solution); // {2, 1, 3, -1, 4, 5, 6, -1, 8, 7, 0, -1}

    int movementCounter = 0;
    while (move.hasMoreNeighbors()) {
      move.nextNeighbor();
      movementCounter++;
    }
    assertEquals(9, movementCounter);
  }
}
