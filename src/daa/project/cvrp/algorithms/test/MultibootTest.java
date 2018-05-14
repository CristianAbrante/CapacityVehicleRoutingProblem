package daa.project.cvrp.algorithms.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import daa.project.cvrp.algorithms.Multiboot;
import daa.project.cvrp.local_search.BestNeighborLocalSearch;
import daa.project.cvrp.moves.IntrarouteSwap;
import daa.project.cvrp.problem.CVRPClient;
import daa.project.cvrp.problem.CVRPSolution;
import daa.project.cvrp.problem.CVRPSpecification;
import daa.project.cvrp.utils.DoubleCompare;

public class MultibootTest {
    
    static final double     EPSILON = DoubleCompare.EPSILON;
    ArrayList<CVRPClient>   clients;
    CVRPSpecification       problemInfo;
    ArrayList<Integer>      solutionCodification;
    CVRPSolution            solution;
    IntrarouteSwap          move;
    BestNeighborLocalSearch localSearch;
    Multiboot               uut;
    
    @Before
    public void initialize() {
        this.clients = new ArrayList<>(Arrays.asList(new CVRPClient[] { new CVRPClient(0, 0, 0), // ID = 0, depot
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
        this.move = new IntrarouteSwap();
        this.localSearch = new BestNeighborLocalSearch(move);
        //        this.uut = new Multiboot();
    }

    @Test
    public void constructionStepShouldNotModifyProblemSpecificationNodeList() {
        ArrayList<CVRPClient> clients = new ArrayList<CVRPClient>(this.problemInfo.getClients());
        Multiboot.constructRandomSolution(this.problemInfo);
        assertEquals(clients, this.problemInfo.getClients());
    }
    
    @Test
    public void constructionStepSolutionShouldNotHaveTheDepotNode() {
        int depotId = this.problemInfo.getDepotID();
        CVRPSolution solution = Multiboot.constructRandomSolution(this.problemInfo);
        for (int route = 0; route < solution.getNumberOfRoutes(); ++route) {
            for (int clientPos = 0; clientPos < solution.getNumberOfClientsInRoute(route); ++clientPos) {
                assertNotEquals(solution.getClient(route, clientPos), depotId);
            }
        }
    }
    
    @Test
    public void constructionStepShouldReturnFeasibleSolution() {
        ArrayList<CVRPClient> clients = new ArrayList<CVRPClient>(this.problemInfo.getClients());
        CVRPSolution solution = Multiboot.constructRandomSolution(this.problemInfo);
        assertTrue(solution.isFeasible());
    }
}
