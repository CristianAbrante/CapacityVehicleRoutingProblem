/**
 * File containing the GRASP entity definition. 
 */

package daa.project.cvrp.algorithms;

import java.util.ArrayList;
import java.util.Random;

import daa.project.cvrp.local_search.LocalSearch;
import daa.project.cvrp.metrics.AlgorithmRecorder;
import daa.project.cvrp.problem.CVRPClient;
import daa.project.cvrp.problem.CVRPSolution;
import daa.project.cvrp.problem.CVRPSpecification;

/**
 * @author Daute Rodríguez Rodríguez (alu0100973914@ull.edu.es)
 * @version 1.0
 * @since 22 abr. 2018
 */
public class GRASP {

	/**
	 * Greedy Randomized Adaptive Search Procedure. A GRASP is an iterative
	 * process, with each GRASP iteration consisting of two phases, a construction
	 * phase and a local search phase. The best overall solution is kept as the
	 * result.
	 * 
	 * @param problemSpecification
	 *          Information about the problem.
	 * @param maxIterations
	 *          Maximum amount of iterations.
	 * @param maxIterationsWithoutImprovement
	 *          Maximum amount of iterations without improve allowed.
	 * @param restrictedCandidateListSize
	 *          Size of the restricted candidate list used in the construct phase
	 *          of GRASP.
	 * @param localSearchStrategy
	 * 					Local search strategy to apply in the local search phase.
	 * @return Better solution found.
	 */
	public static CVRPSolution grasp(CVRPSpecification problemSpecification,
			int maxIterations, int maxIterationsWithoutImprovement,
            int restrictedCandidateListSize, LocalSearch localSearchStrategy, AlgorithmRecorder recorder) {

		if (maxIterations < 1) {
			throw new IllegalArgumentException("Invalid number of iterations");
		}

		int iterationsWithoutImprovement = 0;
		CVRPSolution bestSolution = null;
		int iterations = 0;
        
        recorder.starting();

		// Apply construction phase and local search phase maxIterations times.
		while (iterations < maxIterations) {
            recorder.aboutToDoNextIteration();
			// Construction of a new solution, this new solution or one of it's
			// neighbours can replace the current best solution. This corresponds with
            // the construction phase.
            CVRPSolution newSolution = constructGreedyRandomizedSolution(problemSpecification,
                    restrictedCandidateListSize);
			
			if (!newSolution.isFeasible()) {
				System.err.println("GRASP error");
			}

			// Explore the neighborhood of the new constructed solution searching for
			// a better solution.
			// This corresponds with the local search phase.
            newSolution = localSearchStrategy.findLocalOptimum(newSolution);
			
			if (!newSolution.isFeasible()) {
				System.err.println("Local search error");
			}

			// Solution update. If the new solution or one of it's neighbours it's
            // better than the current best solution, replace it.
			if (bestSolution == null) {
				bestSolution = newSolution;
                recorder.foundBetterSolution(bestSolution);
            } else if (bestSolution.getTotalDistance() > newSolution.getTotalDistance()) {
				bestSolution = newSolution;
                recorder.foundBetterSolution(bestSolution);
				iterationsWithoutImprovement = 0;
			} else {
				iterationsWithoutImprovement++;
			}
			// Stop criteria evaluation. If the stop condition is met, the loop stops.
			if (iterationsWithoutImprovement >= maxIterationsWithoutImprovement) {
				break;
			}

			iterations++;
		}
        
        recorder.finishing();

		// Return the better solution found.
		return bestSolution;
	}

	/**
     * Constructs and returns a solution with the given problem specification. The
     * solution is constructed following a greedy strategy with a random
     * component.
     * 
     * @param problemSpecification Problem specification.
     * @param restrictedCandidateListSize Size of the restricted candidate list.
     * 
     * @return Constructed solution.
     */
    public static CVRPSolution constructGreedyRandomizedSolution(CVRPSpecification problemSpecification, int restrictedCandidateListSize) {
        // Restricted candidate list.
        ArrayList<CVRPClient> restrictedCandidateList = new ArrayList<>();
        
		// Solution codification.
		ArrayList<Integer> solution = new ArrayList<>();
        
		// Remaining clients to serve, remove the depot.
        ArrayList<CVRPClient> remainingClients = new ArrayList<>(problemSpecification.getClients());
        remainingClients.remove(problemSpecification.getDepot());
        
		// Start from the depot.
        CVRPClient currentClient = problemSpecification.getDepot();
        
		// Establishes the remaining capacity of the current vehicle or route.
        int remainingVehicleCapacity = problemSpecification.getCapacity();
        
		// This object will generate a random number which will specify the
		// position in the restricted candidate list of the next client to serve
		// in the current route.
		Random randomNumberGenerator = new Random();

		// Keep iterating until no clients left.
		while (!remainingClients.isEmpty()) {

			// Create or update the restricted candidate list exploring
			// each client.
			for (CVRPClient client : remainingClients) {
				// If the client is already in the restricted candidate list: next
				if (restrictedCandidateList.contains(client)) {
					continue;
				}
				// If the candidate list has available space: introduce properly the
				// client.
                else if (restrictedCandidateList.size() < restrictedCandidateListSize
						&& client.getDemand() <= remainingVehicleCapacity) {
                    insertCandidate(restrictedCandidateList, currentClient, client);
					// If the client is closest to the current client that the last/worst
					// candidate: introduce properly the client in the candidate list.
				} else if (CVRPClient.euclideanDistance(currentClient, client) < 
						       CVRPClient.euclideanDistance(currentClient, restrictedCandidateList.get(restrictedCandidateList.size() - 1)) && 
						       client.getDemand() <= remainingVehicleCapacity) {
					restrictedCandidateList.remove(restrictedCandidateList.size() - 1);
                    insertCandidate(restrictedCandidateList, currentClient, client);
				}
			}

			// If there are clients in the candidate list, select randomly one and
			// introduce it in the current route. Subtract the demand of the new
			// client to the remaining capacity of the vehicle and remove the
			// introduced
			// client from the candidate list and the remaining clients list.
			if (!restrictedCandidateList.isEmpty()) {
				currentClient = restrictedCandidateList
						.get(randomNumberGenerator.nextInt(restrictedCandidateList.size()));
				remainingVehicleCapacity -= currentClient.getDemand();
				remainingClients.remove(currentClient);
				restrictedCandidateList.remove(currentClient);
				solution.add(
                        problemSpecification.getClients().indexOf(currentClient));
                updateRestrictedCandidateList(restrictedCandidateList, remainingVehicleCapacity);
				
			}

			// If the vehicle is "full" or there aren't remaining clients to serve or
			// the restricted candidate list is empty: create a new route, set the
			// current client as the depot and restart the remaining capacity.
			if (remainingVehicleCapacity == 0 || remainingClients.isEmpty()
					|| restrictedCandidateList.isEmpty()) {
				solution.add(CVRPSolution.SEPARATOR);
                currentClient = problemSpecification.getDepot();
                remainingVehicleCapacity = problemSpecification.getCapacity();
			}
		}

		// Return the generated solution.
        return new CVRPSolution(problemSpecification, solution);
	}

	/**
	 * Updates the restricted candidate list. After inserting a client in a route,
	 * the remaining vehicle capacity changes and that client is removed from the
	 * restricted candidate list. If some client of the restricted candidate list
	 * has a demand higher than the new remaining capacity that client must be
	 * removed from the list.
	 */
    private static void updateRestrictedCandidateList(ArrayList<CVRPClient> restrictedCandidateList, int remainingVehicleCapacity) {
		ArrayList<CVRPClient> clientsToRemove = new ArrayList<>();
		for (CVRPClient client : restrictedCandidateList) {
			if (remainingVehicleCapacity < client.getDemand()) {
				clientsToRemove.add(client);
			}
		}
		restrictedCandidateList.removeAll(clientsToRemove);
	}

	/**
	 * Inserts a candidate preserving the order of the restricted candidate list.
	 * 
	 * @param newCandidate
	 *          Candidate to insert.
	 */
	private static void insertCandidate(ArrayList<CVRPClient> restrictedCandidateList, CVRPClient currentClient, CVRPClient newCandidate) {
		for (int i = 0; i < restrictedCandidateList.size(); ++i) {
			if (CVRPClient.euclideanDistance(currentClient, newCandidate) < CVRPClient.euclideanDistance(currentClient, restrictedCandidateList.get(i))) {
				restrictedCandidateList.add(i, newCandidate);
				return;
			}
		}
		restrictedCandidateList.add(newCandidate);
	}
}
