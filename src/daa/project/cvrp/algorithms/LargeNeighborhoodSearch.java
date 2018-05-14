package daa.project.cvrp.algorithms;

import java.util.ArrayList;
import java.util.Random;

import daa.project.cvrp.local_search.LocalSearch;
import daa.project.cvrp.metrics.AlgorithmRecorder;
import daa.project.cvrp.problem.CVRPSolution;
import daa.project.cvrp.problem.CVRPSpecification;
import daa.project.cvrp.utils.DoubleCompare;


public class LargeNeighborhoodSearch {

    //	private static CVRPSolution initialSolution;
    //	private static CVRPSpecification problemSpecification;
    //	private static ArrayList<Integer> removedClients;

	/**
	 * Runs the LargeNeighborhoodSearch algorithm
	 * 
	 * @param problemSpecification
	 *           Problem specification data
	 * @param initialSolution
	 *           Base solution
	 * @param localSearch
	 *           Local search that will be applied
	 * @param maxReconstructions
	 *           Maximum destructions and reconstructions
	 * @param minDiffLocalSearch
	 *           Minimum difference distance between one solution and another one
	 *           to apply local search
	 * @param destructionPercentage
	 *           Base solution destruction percentage
	 * @return New better solution
	 */
	public static CVRPSolution
	       run(CVRPSpecification problemSpecification,
	           CVRPSolution initialSolution, LocalSearch localSearch,
	           int maxReconstructions, int minDiffLocalSearch,
	           double destructionPercentage, AlgorithmRecorder recorder) {

		if (!initialSolution.isFeasible()) {
			throw new IllegalArgumentException(
			      "initial solution for LNS is not feasible"
			);
		}
		recorder.starting();
		
		ArrayList<Integer> removedClients = new ArrayList<>();
      CVRPSolution bestConstructedSol = new CVRPSolution(initialSolution);
      recorder.foundBetterSolution(bestConstructedSol);

		for (int i = 0; i < maxReconstructions; i++) {
			recorder.aboutToDoNextIteration();
			CVRPSolution destroyedSolution = new CVRPSolution(getDestroyedSolution(initialSolution, destructionPercentage, removedClients, problemSpecification));
			CVRPSolution actualConstructedSol = new CVRPSolution(constructNewSolution(destroyedSolution, removedClients));

			if (actualConstructedSol.isFeasible() && actualConstructedSol
			      .getTotalDistance() < bestConstructedSol.getTotalDistance()) {

				// If there is a difference of minDiffLocalSearch to apply local
				// search
				if (bestConstructedSol.getTotalDistance() - actualConstructedSol
				      .getTotalDistance() > minDiffLocalSearch) {
					bestConstructedSol = new CVRPSolution(
					      localSearch.findLocalOptimum(actualConstructedSol));
							} else {
					bestConstructedSol = new CVRPSolution(actualConstructedSol);
				}
				destroyedSolution = new CVRPSolution(initialSolution);
				recorder.foundBetterSolution(bestConstructedSol);
			} else {
				destroyedSolution = new CVRPSolution(initialSolution);
			}
            destroyedSolution = new CVRPSolution(initialSolution);
			removedClients.clear();
		}

		bestConstructedSol = new CVRPSolution(localSearch.findLocalOptimum(bestConstructedSol));
		recorder.foundBetterSolution(bestConstructedSol);
		recorder.finishing();
		
		return new CVRPSolution(bestConstructedSol);
	}

	private static CVRPSolution constructNewSolution(CVRPSolution destroyedSolution, ArrayList<Integer> removedClients) {

		CVRPSolution init = new CVRPSolution(destroyedSolution);
		CVRPSolution bestSolution = null;
		double vehicleRemaining;

		destroyedSolution.addClientToRoute(0, removedClients.get(0));
		vehicleRemaining = destroyedSolution.getTotalDistance();
		bestSolution = new CVRPSolution(destroyedSolution);

		destroyedSolution = new CVRPSolution(init);

		// For each removed client, we will insert it in the route where makes the
		// total distance minimum
		for (int j = 0; j < removedClients.size(); j++) {
			destroyedSolution.addClientToRoute(0, removedClients.get(j));
			vehicleRemaining = destroyedSolution.getTotalDistance();
			bestSolution = new CVRPSolution(destroyedSolution);

			for (int i = 0; i < destroyedSolution.getNumberOfRoutes(); i++) {
				destroyedSolution.addClientToRoute(i, removedClients.get(j));
				if (destroyedSolution.isFeasible() && DoubleCompare.lessThan(
				      destroyedSolution.getTotalDistance(), vehicleRemaining)) {
						vehicleRemaining = destroyedSolution.getTotalDistance();
					bestSolution = new CVRPSolution(destroyedSolution);
				}
				destroyedSolution = new CVRPSolution(init);
			}
			init = new CVRPSolution(bestSolution);
			destroyedSolution = new CVRPSolution(bestSolution);
		}
		return bestSolution;
	}

	/**
	 * Method to destroy a solution percentage
	 * 
	 * @param initialSolution
	 *           Base solution
	 * @param destructionPercentage
	 *           Percentage of destruction
	 * @return New destructed solution
	 */
	private static CVRPSolution
	        getDestroyedSolution(CVRPSolution initialSolution,
                    double destructionPercentage, ArrayList<Integer> removedClients,
                    CVRPSpecification problemSpecification) {

		int vehicleRoutesSize = initialSolution.getNumberOfClients()
		      + initialSolution.getNumberOfRoutes();
		int numberOfClientsToRemove =
		      (int) (vehicleRoutesSize * destructionPercentage);
		ArrayList<Integer> initialVehiclesRoutes = new ArrayList<>();
		Random rand = new Random();
		int removeClientIndex;

		// Fill the array to get all elements of the route
		for (int i = 0; i < vehicleRoutesSize; i++) {
			initialVehiclesRoutes.add(initialSolution.getClientId(i));
		}

		// Iterates to remove the number of clients specified and checks if the
		// client is separator, is already removed or the route will be empty if
		// this client is removed
		for (int i = 0; i < numberOfClientsToRemove; i++) {
			removeClientIndex = rand.nextInt(initialVehiclesRoutes.size());
			int clientToRemove = initialVehiclesRoutes.get(removeClientIndex);

			while (clientToRemove == CVRPSolution.SEPARATOR || removedClients
			      .contains(clientToRemove) || checkRouteEmpty(removeClientIndex,
			            initialVehiclesRoutes)) {
							removeClientIndex =
							      rand.nextInt(initialVehiclesRoutes.size());
				clientToRemove = initialVehiclesRoutes.get(removeClientIndex);
			}
			initialVehiclesRoutes
			      .remove(initialVehiclesRoutes.indexOf(clientToRemove
			));
			removedClients.add(clientToRemove);
		}
		return new CVRPSolution(problemSpecification, initialVehiclesRoutes);
	}

	/**
	 * Checks if it's a empty route to avoid delete all clients of a route
	 * 
	 * @param indexToRemove
	 *           Client to remove
	 * @param previousVehiclesRoutes
	 *           Array of routes
	 * @return boolean, true if the route will be empty otherwise false
	 */
	private static boolean
	        checkRouteEmpty(int indexToRemove,
	                        ArrayList<Integer> previousVehiclesRoutes) {

		int previousClient;
		int client;
		int nextClient;

		if (indexToRemove >= 1
		      && indexToRemove <= previousVehiclesRoutes.size() - 1) {
			previousClient = previousVehiclesRoutes.get(indexToRemove - 1);
			client = previousVehiclesRoutes.get(indexToRemove);
			nextClient = previousVehiclesRoutes.get(indexToRemove + 1);

			if ((previousClient != CVRPSolution.SEPARATOR)
			      && (nextClient != CVRPSolution.SEPARATOR)
			      && (client != CVRPSolution.SEPARATOR)) {
				return false;
			}
		}

		if (indexToRemove == 0) {
			client = previousVehiclesRoutes.get(indexToRemove);
			nextClient = previousVehiclesRoutes.get(indexToRemove + 1);

			if ((nextClient != CVRPSolution.SEPARATOR)
			      && (client != CVRPSolution.SEPARATOR)) {
				return false;
			}
		}

		if (indexToRemove == previousVehiclesRoutes.size() - 1) {
			previousClient = previousVehiclesRoutes.get(indexToRemove - 1);
			client = previousVehiclesRoutes.get(indexToRemove);

			if ((previousClient != CVRPSolution.SEPARATOR)
			      && (client != CVRPSolution.SEPARATOR)) {
				return false;
			}
		}
		return true;
	}
}
