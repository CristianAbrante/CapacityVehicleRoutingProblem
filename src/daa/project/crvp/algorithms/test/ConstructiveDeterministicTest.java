package daa.project.crvp.algorithms.test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import daa.project.crvp.algorithms.ConstructiveDeterministic;
import daa.project.crvp.moves.IntrarouteSwap;
import daa.project.crvp.problem.CVRPClient;
import daa.project.crvp.problem.CVRPSolution;
import daa.project.crvp.problem.CVRPSpecification;

public class ConstructiveDeterministicTest {
	CVRPSpecification problemSpecification;
	ArrayList<CVRPClient> clients;
	CVRPSolution    solutionMustBe;
	CVRPSolution    solution;
   
   @Before
   public void initialize() {
       this.clients = new ArrayList<>(Arrays.asList(new CVRPClient[] { 
               new CVRPClient(0, 0, 0), // ID = 0, depot
               new CVRPClient(2, 2, 9), // ID = 1
               new CVRPClient(3, 3, 1), // ID = 2
               new CVRPClient(4, 4, 99), // ID = 4
       }));
       
       this.problemSpecification = new CVRPSpecification(this.clients, 0, 100, 1);
       ArrayList<Integer> expectedVehicleRoutes = new ArrayList<>(
               Arrays.asList(new Integer[] { 1, 2, CVRPSolution.SEPARATOR, 3, CVRPSolution.SEPARATOR }));
       this.solutionMustBe = new CVRPSolution(this.problemSpecification, expectedVehicleRoutes);
       this.solution = ConstructiveDeterministic.constructDeterministicSolution(problemSpecification);
   }

   @Test
   public void testAllClientsAreInSolution() {
   	assertEquals(solution.getNumberOfClients(), solutionMustBe.getNumberOfClients());
   }
   
   @Test
   public void testTotalDistance() {
   	assertEquals(solution.getTotalDistance(), solutionMustBe.getTotalDistance(), 0.001);
   }
   
   @Test
   public void testNumberOfRoutes() {
   	assertEquals(solution.getNumberOfRoutes(), solutionMustBe.getNumberOfRoutes());
   }
   
   @Test
   public void depositIsNotInSolution() {
   	assertEquals(solution.isFeasible(), solutionMustBe.isFeasible());
   }
}
