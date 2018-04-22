package daa.project.crvp.local_seach;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import daa.project.crvp.local_search.BestNeighborLocalSearch;
import daa.project.crvp.moves.IntrarouteSwap;
import daa.project.crvp.problem.CVRPClient;
import daa.project.crvp.problem.CVRPSolution;
import daa.project.crvp.problem.CVRPSpecification;
import daa.project.crvp.utils.DoubleCompare;


public class BestNeighborLocalSearchTest {
    
    static final double     EPSILON = DoubleCompare.EPSILON;
    ArrayList<CVRPClient>   clients;
    CVRPSpecification       problemInfo;
    ArrayList<Integer>      solutionCodification;
    CVRPSolution            solution;
    IntrarouteSwap          move;
    BestNeighborLocalSearch uut;
    
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
        this.uut = new BestNeighborLocalSearch(move);
    }
    
    //    @Test(timeout = 1000)
    //    public void test() {
    //        this.uut.setBaseSolution(this.solution);
    //        CVRPSolution localOptimum = this.uut.findLocalOptimum();
    //        CVRPSolution expectedOptimum = new CVRPSolution(this.problemInfo, new ArrayList<>(
    //                Arrays.asList(new Integer[] { 1, 2, 3, CVRPSolution.SEPARATOR, 4, 6, 5, CVRPSolution.SEPARATOR })));
    //        
    //        assertEquals(expectedOptimum, localOptimum);
    //        
    //        for (int i = 0; i < localOptimum.getNumberOfRoutes(); ++i) {
    //            for (int j = 0; j < localOptimum.getNumberOfClientsInRoute(i); ++j) {
    //                System.out.print(localOptimum.getClientId(i, j) + " ");
    //            }
    //            System.out.print("\t");
    //        }
    //        System.err.println("\t" + localOptimum.getTotalDistance());
    //    }
    
    @Test
    public void shouldReturnSameSolutionAsOptimumForSingleRouteEmptyBaseSolution() {
        CVRPSolution solution = new CVRPSolution(this.problemInfo,
                new ArrayList<>(Arrays.asList(new Integer[] { CVRPSolution.SEPARATOR })));
        this.uut.setBaseSolution(solution);
        CVRPSolution localOptimum = this.uut.findLocalOptimum();
        
        assertEquals(solution, localOptimum);
    }
    
    @Test
    public void shouldReturnSameSolutionAsOptimumForSingleRouteSingleClientBaseSolution() {
        CVRPSolution solution = new CVRPSolution(this.problemInfo,
                new ArrayList<>(Arrays.asList(new Integer[] { 3, CVRPSolution.SEPARATOR })));
        this.uut.setBaseSolution(solution);
        CVRPSolution localOptimum = this.uut.findLocalOptimum();
        
        assertEquals(solution, localOptimum);
    }
    
    @Test
    public void shouldReturnSameSolutionAsOptimumForMultipleRoutesSingleClientBaseSolution() {
        CVRPSolution solution = new CVRPSolution(this.problemInfo, new ArrayList<>(Arrays.asList(
                new Integer[] { 1, CVRPSolution.SEPARATOR, 2, CVRPSolution.SEPARATOR, 3, CVRPSolution.SEPARATOR })));
        this.uut.setBaseSolution(solution);
        CVRPSolution localOptimum = this.uut.findLocalOptimum();
        
        assertEquals(solution, localOptimum);
    }
}
