package daa.project.cvrp.local_search;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import daa.project.cvrp.moves.IntrarouteSwap;
import daa.project.cvrp.moves.Move;
import daa.project.cvrp.problem.CVRPClient;
import daa.project.cvrp.problem.CVRPSolution;
import daa.project.cvrp.problem.CVRPSpecification;
import daa.project.cvrp.utils.DoubleCompare;

public class VariableNeighborhoodDescentTest {
    
    static final double            EPSILON = DoubleCompare.EPSILON;
    ArrayList<CVRPClient>          clients;
    CVRPSpecification              problemInfo;
    ArrayList<Integer>             solutionCodification;
    CVRPSolution                   solution;
    IntrarouteSwap                 move;
    VariableNeighborhoodDescent uut;
    
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
        this.solutionCodification = new ArrayList<>(Arrays.asList(new Integer[] { CVRPSolution.SEPARATOR }));
        this.solution = new CVRPSolution(this.problemInfo, this.solutionCodification);
        this.move = new IntrarouteSwap();
        this.uut = new VariableNeighborhoodDescent(new Move[] { move });
    }
    
    @Test
    public void shouldReturnSameSolutionAsOptimumForSingleRouteEmptyBaseSolution() {
        CVRPSolution solution = new CVRPSolution(this.problemInfo,
                new ArrayList<>(Arrays.asList(new Integer[] { CVRPSolution.SEPARATOR })));
        CVRPSolution localOptimum = this.uut.findLocalOptimum(solution);
        
        assertEquals(solution, localOptimum);
    }
    
    @Test
    public void shouldReturnSameSolutionAsOptimumForSingleRouteSingleClientBaseSolution() {
        CVRPSolution solution = new CVRPSolution(this.problemInfo,
                new ArrayList<>(Arrays.asList(new Integer[] { 3, CVRPSolution.SEPARATOR })));
        CVRPSolution localOptimum = this.uut.findLocalOptimum(solution);
        
        assertEquals(solution, localOptimum);
    }
    
    @Test
    public void shouldReturnSameSolutionAsOptimumForMultipleRoutesSingleClientBaseSolution() {
        CVRPSolution solution = new CVRPSolution(this.problemInfo, new ArrayList<>(Arrays.asList(
                new Integer[] { 1, CVRPSolution.SEPARATOR, 2, CVRPSolution.SEPARATOR, 3, CVRPSolution.SEPARATOR })));
        CVRPSolution localOptimum = this.uut.findLocalOptimum(solution);
        
        assertEquals(solution, localOptimum);
    }
    
}
