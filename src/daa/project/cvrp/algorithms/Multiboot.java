package daa.project.cvrp.algorithms;

import java.util.ArrayList;
import java.util.Collections;

import daa.project.cvrp.local_search.LocalSearch;
import daa.project.cvrp.metrics.AlgorithmRecorder;
import daa.project.cvrp.problem.CVRPClient;
import daa.project.cvrp.problem.CVRPSolution;
import daa.project.cvrp.problem.CVRPSpecification;
import daa.project.cvrp.utils.DoubleCompare;
import daa.project.cvrp.utils.Random;

/**
 * This method tries to get the best solution of a problem by
 * generating a random solution and applying a local search
 * to get to a local optimum. If the solution is better than
 * the best found so far, update the best solution found.
 * And that is repeated that until a stop criteria is met
 * 
 * @author Carlos Dominguez Garcia (alu0100966589)
 * @version 1.0.0
 * @since 1.0.0 (Apr 22, 2018)
 * @file Multiboot.java
 *
 */
public class Multiboot {
    
    public static CVRPSolution multiboot(CVRPSpecification problemInfo, LocalSearch localSearch,
            int maxNumIterationsNoImprovement, AlgorithmRecorder recorder) {
        recorder.starting();
        CVRPSolution solution = constructRandomSolution(problemInfo);
        CVRPSolution bestSolutionFound = solution;
        int numIterationsNoImprovement = 0;
        recorder.foundBetterSolution(bestSolutionFound);
        
        while (numIterationsNoImprovement < maxNumIterationsNoImprovement) {
            recorder.aboutToDoNextIteration();
            solution = localSearch.findLocalOptimum(solution);
            if (DoubleCompare.lessThan(solution.getTotalDistance(), bestSolutionFound.getTotalDistance())) {
                bestSolutionFound = solution;
                numIterationsNoImprovement = -1;
                recorder.foundBetterSolution(bestSolutionFound);
            }
            solution = constructRandomSolution(problemInfo);
            numIterationsNoImprovement += 1;
        }
        
        recorder.finishing();
        return bestSolutionFound;
    }
    
    /**
     * Returns a random feasible solution
     * 
     * @param problemInfo   Information about the CVRP to create the solution for
     * @return  Feasible random solution for the given CVRP
     */
    public static CVRPSolution constructRandomSolution(CVRPSpecification problemInfo) {
        ArrayList<CVRPClient> clients = new ArrayList<>(problemInfo.getClients());
        ArrayList<Integer> solution = new ArrayList<>();
        int depotId = problemInfo.getDepotID();
        int remainingCapacityCurrentRoute = problemInfo.getCapacity();
        
        // Swap the chose client with i, so the i-th client can be picked lately
        Collections.swap(clients, 0, depotId);
        
        for (int i = 1; i < clients.size(); ++i) {
            // Generate random index in range [i, size)
            int randomIndex = Random.randomInt(i, clients.size());
            
            int clientId = problemInfo.getClients().indexOf(clients.get(randomIndex));
            // Add client to solution. If the demand is greater than what the
            // current vehicle can carry, then start a new route
            int clientDemand = problemInfo.getClients().get(clientId).getDemand();
            if (clientDemand < remainingCapacityCurrentRoute) {
                solution.add(clientId);
                remainingCapacityCurrentRoute -= clientDemand;
            } else {
                solution.add(CVRPSolution.SEPARATOR);
                solution.add(clientId);
                remainingCapacityCurrentRoute = problemInfo.getCapacity() - clientDemand;
            }
            
            // Swap the chose client with i, so the i-th client can be picked lately
            Collections.swap(clients, i, randomIndex);
        }
        solution.add(CVRPSolution.SEPARATOR);
        
        return new CVRPSolution(problemInfo, solution);
    }
    
}
