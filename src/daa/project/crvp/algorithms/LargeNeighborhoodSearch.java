package daa.project.crvp.algorithms;

import java.util.ArrayList;

import daa.project.crvp.local_search.LocalSearch;
import daa.project.crvp.moves.Move;
import daa.project.crvp.problem.CVRPSolution;
import daa.project.crvp.problem.CVRPSpecification;
import daa.project.crvp.utils.DoubleCompare;
import daa.project.crvp.utils.Random;

public class LargeNeighborhoodSearch {
	
	private static CVRPSolution initialSolution;
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
		
		LargeNeighborhoodSearch.initialSolution = initialSolution;
		
		for(int i = 0; i < 1000; i++) {
			CVRPSolution destroyedSolution = getDestroyedSolution(initialSolution, destructionPercentage);

			//		System.out.println("REMOVED CLIENTS");
			//		for(int i = 0; i < removedClients.size(); i++) {
			//			System.err.print(removedClients.get(i) + ", ");
			//		}
			//		System.err.println();
			CVRPSolution candidate = constructNewSolution(destroyedSolution);
			if(candidate.getTotalDistance() < LargeNeighborhoodSearch.initialSolution.getTotalDistance())
				return candidate;
		}
		
		return initialSolution;
	}
	
	private static CVRPSolution constructNewSolution(CVRPSolution destroyedSolution) {
		int vehicleRoutesSize = destroyedSolution.getNumberOfClients() + destroyedSolution.getNumberOfRoutes();
//		for(int i = 0; i < destroyedSolution.getNumberOfClients() + destroyedSolution.getNumberOfRoutes(); i++) {
//			System.out.print(destroyedSolution.getClientId(i) + ", ");
//		}
//		
		double vehicleRemaining = Double.MAX_VALUE;
		CVRPSolution currentSolution = destroyedSolution;
		CVRPSolution bestSolution = null;
		boolean improve = false;
		
		for(int i = 0; i < removedClients.size(); i++) {
			for(int j = 0; j < currentSolution.getNumberOfRoutes(); j++) {
				CVRPSolution actualSolution = new CVRPSolution(currentSolution);
				actualSolution.addClientToRoute(j, removedClients.get(i));
				if(actualSolution.isFeasible() && DoubleCompare.lessThan(actualSolution.getVehicleRemainingCapacity(j), vehicleRemaining)) {
					vehicleRemaining = actualSolution.getTotalDistance();
					bestSolution = actualSolution;
				}
				
//				System.err.println(actualSolution.getTotalDistance());
//				for(int i = 0; i < actualSolution.getNumberOfClients() + actualSolution.getNumberOfRoutes(); i++) {
//					System.out.print(actualSolution.getClientId(i) + ", ");
//				}
//				System.err.println();
			}
			currentSolution = bestSolution;
			vehicleRemaining = Double.MAX_VALUE;
		}
		
//		System.err.println("BEST SOLUTION");
//		for(int i = 0; i < bestSolution.getNumberOfClients() + bestSolution.getNumberOfRoutes(); i++) {
//			System.out.print(bestSolution.getClientId(i) + ", ");
//		}
//		System.out.println(bestSolution.getTotalDistance() + "\n");
//		
//		System.err.println("INITIAL SOLUTION");
//		for(int i = 0; i < initialSolution.getNumberOfClients() + initialSolution.getNumberOfRoutes(); i++) {
//			System.out.print(initialSolution.getClientId(i) + ", ");
//		}
//		System.out.println(initialSolution.getTotalDistance() + "\n");
		return bestSolution;
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
