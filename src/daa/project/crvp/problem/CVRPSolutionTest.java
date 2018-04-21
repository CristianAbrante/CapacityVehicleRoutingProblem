package daa.project.crvp.problem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class CVRPSolutionTest {
    static final double   EPSILON = 10E-6;
    ArrayList<CVRPClient> clients;
    CVRPSpecification     problemInfo;
    ArrayList<Integer>    firstSolutionCodification;
    CVRPSolution          firstUut; // First Unit Under Test
    
    @Before
    public void initialize() {
        this.clients = new ArrayList<>(Arrays.asList(new CVRPClient[] { 
                new CVRPClient(0, 0, 0), // ID = 0, depot
                new CVRPClient(2, 2, 9), // ID = 1
                new CVRPClient(3, 3, 1), // ID = 2
                new CVRPClient(4, 4, 99), // ID = 3
        }));
        
        this.problemInfo = new CVRPSpecification(this.clients, 0, 100, 1);
        this.firstSolutionCodification = new ArrayList<>(
                Arrays.asList(new Integer[] { 1, 2, CVRPSolution.SEPARATOR, 3, CVRPSolution.SEPARATOR }));
        this.firstUut = new CVRPSolution(this.problemInfo, this.firstSolutionCodification);
    }
    
    @Test
    public void firstSolutionShouldBeFeasible() {
        assertTrue(this.firstUut.isFeasible());
    }
    
    @Test
    public void firstSolutionShouldHaveTwoRoutes() {
        assertEquals(2, this.firstUut.getNumberOfRoutes());
    }
    
    @Test
    public void firstSolutionFirstRouteShouldHave90RemainingCapacity() {
        assertEquals(90, this.firstUut.getVehicleRemainingCapacity(0));
    }
    
    @Test
    public void firstSolutionSecondRouteShouldHave1RemainingCapacity() {
        assertEquals(1, this.firstUut.getVehicleRemainingCapacity(1));
    }
    
    @Test
    public void firstSolutionFirstRouteStartingIndexShouldBe0() {
        assertEquals(0, this.firstUut.getRouteStartingIndex(0));
    }
    
    @Test
    public void firstSolutionSecondRouteStartingIndexShouldBe3() {
        assertEquals(3, this.firstUut.getRouteStartingIndex(1));
    }
    
    @Test
    public void firstSolutionFirstRouteNumberOfClientsShouldBe2() {
        assertEquals(2, this.firstUut.getNumberOfClientsInRoute(0));
    }
    
    @Test
    public void firstSolutionSecondRouteNumberOfClientsShouldBe1() {
        assertEquals(1, this.firstUut.getNumberOfClientsInRoute(1));
    }
    
    @Test
    public void firstSolutionShouldHaveTheRightTotalDistance() {
        double expectedFirstRouteDistance = 2 * Math.hypot(3 - 0, 3 - 0); // * 2 Because it has to go back to the depot
        double expectedSecondRouteDistance = 2 * Math.hypot(4 - 0, 4 - 0);
        double expectedTotalDistance = expectedFirstRouteDistance + expectedSecondRouteDistance;
        assertEquals(expectedTotalDistance, this.firstUut.getTotalDistance(), EPSILON);
    }
}
