package daa.project.crvp.problem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for a simple feasible solution with two routes, the first one with two clients and the second
 * one with one
 * 
 * @author Carlos Dominguez Garcia (alu0100966589)
 * @version 1.0.0
 * @since 1.0.0 (Apr 21, 2018)
 * @file CVRPSolutionTest1.java
 *
 */
public class CVRPSolutionTest1 {
    static final double   EPSILON = 10E-6;
    ArrayList<CVRPClient> clients;
    CVRPSpecification     problemInfo;
    ArrayList<Integer>    solutionCodification;
    CVRPSolution          uut;  // Unit Under Test
    
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
        this.uut = new CVRPSolution(this.problemInfo, this.solutionCodification);
    }
    
    @Test
    public void solutionProblemInfoShouldReferToTheSamePassed() {
        assertEquals(this.problemInfo, this.uut.getProblemInfo());
    }
    
    @Test
    public void solutionShouldBeFeasible() {
        assertTrue(this.uut.isFeasible());
    }
    
    @Test
    public void solutionShouldHave3Clients() {
        assertEquals(3, this.uut.getNumberOfClients());
    }
    
    @Test
    public void solutionShouldHaveTwoRoutes() {
        assertEquals(2, this.uut.getNumberOfRoutes());
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void routeRemainingCapacityShouldThrowWithNegativeRoute() {
        this.uut.getVehicleRemainingCapacity(-1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void routeRemainingCapacityShouldThrowWithARouteOutOfBounds() {
        this.uut.getVehicleRemainingCapacity(2);
    }
    
    @Test
    public void solutionFirstRouteShouldHave90RemainingCapacity() {
        assertEquals(90, this.uut.getVehicleRemainingCapacity(0));
    }
    
    @Test
    public void solutionSecondRouteShouldHave1RemainingCapacity() {
        assertEquals(1, this.uut.getVehicleRemainingCapacity(1));
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void routeStartingIndexShouldThrowWithNegativeRoute() {
        this.uut.getRouteStartingIndex(-1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void routeStartingIndexShouldThrowWithRouteOutOfBounds() {
        this.uut.getRouteStartingIndex(2);
    }
    
    @Test
    public void solutionFirstRouteStartingIndexShouldBe0() {
        assertEquals(0, this.uut.getRouteStartingIndex(0));
    }
    
    @Test
    public void solutionSecondRouteStartingIndexShouldBe3() {
        assertEquals(3, this.uut.getRouteStartingIndex(1));
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void routeNumberOfClientsShouldThrowWithNegativeRoute() {
        this.uut.getNumberOfClientsInRoute(-1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void routeNumberOfClientsShouldThrowWithRouteOutOfBounds() {
        this.uut.getNumberOfClientsInRoute(2);
    }
    
    @Test
    public void solutionFirstRouteNumberOfClientsShouldBe2() {
        assertEquals(2, this.uut.getNumberOfClientsInRoute(0));
    }
    
    @Test
    public void solutionSecondRouteNumberOfClientsShouldBe1() {
        assertEquals(1, this.uut.getNumberOfClientsInRoute(1));
    }
    
    @Test
    public void solutionShouldHaveTheRightTotalDistance() {
        double expectedFirstRouteDistance = 2 * Math.hypot(3 - 0, 3 - 0); // * 2 Because it has to go back to the depot
        double expectedSecondRouteDistance = 2 * Math.hypot(4 - 0, 4 - 0);
        double expectedTotalDistance = expectedFirstRouteDistance + expectedSecondRouteDistance;
        assertEquals(expectedTotalDistance, this.uut.getTotalDistance(), EPSILON);
    }
    
    @Test
    public void getClientWithRouteShouldReturnClientId1ForRoute0Client0() {
        assertEquals(this.clients.get(1), this.uut.getClient(0, 0));
    }
    
    @Test
    public void getClientWithRouteShouldReturnClientId2ForRoute0Client1() {
        assertEquals(this.clients.get(2), this.uut.getClient(0, 1));
    }
    
    @Test
    public void getClientWithRouteShouldReturnClientId3ForRoute1Client0() {
        assertEquals(this.clients.get(3), this.uut.getClient(1, 0));
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void getClientWithRouteShouldThrowErrorForRoute0Client2() {
        this.uut.getClient(0, 2);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void getClientWithRouteShouldThrowErrorForNegativeRouteClient0() {
        this.uut.getClient(-1, 0);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void getClientWithRouteShouldThrowErrorForRoute1Client1() {
        this.uut.getClient(1, 1);
    }
    
    @Test
    public void getClientShouldReturnClientId1ForPosition0() {
        assertEquals(this.clients.get(1), this.uut.getClient(0));
    }
    
    @Test
    public void getClientShouldReturnClientId2ForPosition1() {
        assertEquals(this.clients.get(2), this.uut.getClient(1));
    }
    
    @Test
    public void getClientShouldReturnNullForPosition2() {
        assertEquals(null, this.uut.getClient(2));
    }
    
    @Test
    public void getClientShouldReturnClientId3ForPosition3() {
        assertEquals(this.clients.get(3), this.uut.getClient(3));
    }
    
    @Test
    public void getClientShouldReturnNullForPosition4() {
        assertEquals(null, this.uut.getClient(4));
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void getClientShouldThrowForPosition5() {
        this.uut.getClient(5);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void getClientShouldThrowForNegativePosition() {
        this.uut.getClient(-1);
    }
    
  	@Test
  	public void swapBetweenRoutes() {
  		assertEquals(1, uut.getClientId(0));
  		assertEquals(3, uut.getClientId(3));

  		CVRPSolution newSolution = new CVRPSolution(problemInfo,
  				CVRPSolution.generateSwappedSolution(uut, 0, 0, 1, 0));

  		assertEquals(3, newSolution.getClientId(0));
  		assertEquals(1, newSolution.getClientId(3));
  	}
  	
  	@Test
  	public void swapInsideRoutes() {
  		assertEquals(1, uut.getClientId(0));
  		assertEquals(2, uut.getClientId(1));

  		CVRPSolution newSolution = new CVRPSolution(problemInfo,
  				CVRPSolution.generateSwappedSolution(uut, 0, 0, 0, 1));

  		assertEquals(2, newSolution.getClientId(0));
  		assertEquals(1, newSolution.getClientId(1));
  	}
  	
  	@Test
  	public void swapSameClient() {
  		assertEquals(1, uut.getClientId(0));

  		CVRPSolution newSolution = new CVRPSolution(problemInfo,
  				CVRPSolution.generateSwappedSolution(uut, 0, 0, 0, 0));

  		assertEquals(1, newSolution.getClientId(0));
  	}
}
