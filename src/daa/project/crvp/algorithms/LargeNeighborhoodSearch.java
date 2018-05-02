package daa.project.crvp.algorithms;

import java.util.ArrayList;

import daa.project.crvp.local_search.LocalSearch;
import daa.project.crvp.moves.Move;
import daa.project.crvp.problem.CVRPSolution;
import daa.project.crvp.problem.CVRPSpecification;
import daa.project.crvp.utils.DoubleCompare;
import daa.project.crvp.utils.Random;

public class LargeNeighborhoodSearch {
	
	private static CVRPSpecification problemSpecification;
	private static ArrayList<Integer> removedClients;
	
	public static CVRPSolution run(CVRPSpecification problemSpecification, 
	                               CVRPSolution initialSolution,
	                               Move[] moves,
	                               double destructionPercentage) {
		if (moves.length < 1) {
			throw new IllegalArgumentException("invalid list of moves for VNS");
		}
		if (!initialSolution.isFeasible()) {
			throw new IllegalArgumentException("initial solution for VNS is not feasible");
		}
		
		LargeNeighborhoodSearch.removedClients = new ArrayList<>();
		LargeNeighborhoodSearch.problemSpecification = problemSpecification;
		
		CVRPSolution currentSolution = initialSolution;
		CVRPSolution destroyedSolution = getDestroyedSolution(initialSolution, destructionPercentage);
		
		constructNewSolution(destroyedSolution);

		return initialSolution;
	}
	
	private static CVRPSolution constructNewSolution(CVRPSolution destroyedSolution) {
//		destroyedSolution.addClientToRoute(3, 2);
//		for(int i = 0 ; i < destroyedSolution.getNumberOfClients() + destroyedSolution.getNumberOfRoutes(); i++) {
//			System.out.print(destroyedSolution.getClientId(i) + ", ");
//		}
		return destroyedSolution;
	}

	private static CVRPSolution getDestroyedSolution(CVRPSolution initialSolution, double destructionPercentage) {
		int vehicleRoutesSize = initialSolution.getNumberOfClients() + initialSolution.getNumberOfRoutes();
		int numberOfRemovedClients = (int) (vehicleRoutesSize * destructionPercentage);
		int indexToRemove;

		for(int i = 0; i < numberOfRemovedClients; i++) {
			indexToRemove = Random.randomInt(0, vehicleRoutesSize);
			int clientToRemove = initialSolution.getClientId(indexToRemove);

			// If there is a separator or the client is repeated, dont stop until find another valid client
			while(clientToRemove == CVRPSolution.SEPARATOR && !removedClients.contains(clientToRemove)) {
				clientToRemove = Random.randomInt(0, vehicleRoutesSize);
			}
			removedClients.add(clientToRemove);
		}

		ArrayList<Integer> remainingClients = new ArrayList<>();

		boolean foundInRemoved = false;
		for(int i = 0; i < vehicleRoutesSize; i++) {
			for(int j = 0; j < removedClients.size(); j++) {
				if(initialSolution.getClientId(i) == removedClients.get(j)) {
					foundInRemoved = true;
				}
			}
			if(foundInRemoved == false) {
				remainingClients.add(initialSolution.getClientId(i));
			}
			foundInRemoved = false;
		}
		return new CVRPSolution(problemSpecification, remainingClients);
	}
}
