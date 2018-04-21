package daa.project.crvp.problem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for a solution with a single route with no clients
 * 
 * @author Carlos Dominguez Garcia (alu0100966589)
 * @version 1.0.0
 * @since 1.0.0 (Apr 21, 2018)
 * @file CVRPSolutionTest2.java
 *
 */
public class CVRPSolutionTest2 {
    static final double   EPSILON = 10E-6;
    ArrayList<CVRPClient> clients;
    CVRPSpecification     problemInfo;
    ArrayList<Integer>    solutionCodification;
    CVRPSolution          uut;                 // Unit Under Test
    
    @Before
    public void initialize() {
        this.clients = new ArrayList<>(Arrays.asList(new CVRPClient[] { new CVRPClient(0, 0, 0), // ID = 0, depot
                new CVRPClient(2, 2, 9), // ID = 1
                new CVRPClient(3, 3, 1), // ID = 2
                new CVRPClient(4, 4, 99), // ID = 3
        }));
        
        this.problemInfo = new CVRPSpecification(this.clients, 0, 100, 1);
        this.solutionCodification = new ArrayList<>(
                Arrays.asList(new Integer[] { CVRPSolution.SEPARATOR }));
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
    public void solutionShouldHave0Clients() {
        assertEquals(0, this.uut.getNumberOfClients());
    }
    
    @Test
    public void solutionShouldHaveOneRoute() {
        assertEquals(1, this.uut.getNumberOfRoutes());
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void routeRemainingCapacityShouldThrowWithNegativeRoute() {
        this.uut.getVehicleRemainingCapacity(-1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void routeRemainingCapacityShouldThrowWithARouteOutOfBounds() {
        this.uut.getVehicleRemainingCapacity(1);
    }
    
    @Test
    public void solutionOnlyRouteShouldHave100RemainingCapacity() {
        assertEquals(100, this.uut.getVehicleRemainingCapacity(0));
    }
    
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void routeStartingIndexShouldThrowWithNegativeRoute() {
        this.uut.getRouteStartingIndex(-1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void routeStartingIndexShouldThrowWithRouteOutOfBounds() {
        this.uut.getRouteStartingIndex(1);
    }
    
    @Test
    public void solutionOnlyRouteStartingIndexShouldBe0() {
        assertEquals(0, this.uut.getRouteStartingIndex(0));
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void routeNumberOfClientsShouldThrowWithNegativeRoute() {
        this.uut.getNumberOfClientsInRoute(-1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void routeNumberOfClientsShouldThrowWithRouteOutOfBounds() {
        this.uut.getNumberOfClientsInRoute(1);
    }
    
    @Test
    public void solutionOnlyRouteNumberOfClientsShouldBe0() {
        assertEquals(0, this.uut.getNumberOfClientsInRoute(0));
    }
    
    @Test
    public void solutionShouldHave0TotalDistance() {
        assertEquals(0.0, this.uut.getTotalDistance(), EPSILON);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void getClientWithRouteShouldThrowForRoute0Client0() {
        this.uut.getClient(0, 0);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void getClientWithRouteShouldThrowForRoute0Client1() {
        this.uut.getClient(0, 1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void getClientWithRouteShouldThrowForRoute1Client0() {
        this.uut.getClient(1, 0);
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
    public void getClientShouldReturnNullForPosition0() {
        assertEquals(null, this.uut.getClient(0));
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void getClientShouldThrowForPosition1() {
        this.uut.getClient(1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void getClientShouldThrowForNegativePosition() {
        this.uut.getClient(-1);
    }
}
