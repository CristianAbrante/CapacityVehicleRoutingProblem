package daa.project.crvp.algorithms;

import java.util.ArrayList;

import daa.project.crvp.local_search.LocalSearch;
import daa.project.crvp.moves.Move;
import daa.project.crvp.problem.CVRPSolution;
import daa.project.crvp.problem.CVRPSpecification;
import daa.project.crvp.utils.DoubleCompare;
import java.util.*;

public class LargeNeighborhoodSearch {
	
	private static CVRPSolution initialSolution;
	private static CVRPSpecification problemSpecification;
	private static ArrayList<Integer> removedClients;
	private static LocalSearch localSearch;
	
	public static CVRPSolution run(CVRPSpecification problemSpecification, 
	                               CVRPSolution initialSolution,
	                               LocalSearch localSearch,
	                               double destructionPercentage) {
		
		if (!initialSolution.isFeasible()) {
			throw new IllegalArgumentException("initial solution for LNS is not feasible");
		}
		
		LargeNeighborhoodSearch.removedClients = new ArrayList<>();
		LargeNeighborhoodSearch.problemSpecification = problemSpecification;
		LargeNeighborhoodSearch.localSearch = localSearch;
		LargeNeighborhoodSearch.initialSolution = new CVRPSolution(initialSolution);
		CVRPSolution bestSol = new CVRPSolution(initialSolution);
		
		
//			System.out.println();
//			System.out.println("INITSOL");
//			for(int j = 0; j < initialSolution.getNumberOfClients() + initialSolution.getNumberOfRoutes(); j++) {
//				System.out.print(initialSolution.getClientId(j) + ", ");
//			}
			
			CVRPSolution destroyedSolution = new CVRPSolution(getDestroyedSolution(initialSolution, destructionPercentage));

//					System.out.println("REMOVED CLIENTS");
//					for(int j = 0; j < removedClients.size(); j++) {
//						System.err.print(removedClients.get(j) + ", ");
//					}
//					System.err.println();
			CVRPSolution candidate = new CVRPSolution(constructNewSolution(destroyedSolution));
			
			if(candidate.getTotalDistance() < bestSol.getTotalDistance())
				bestSol = new CVRPSolution(candidate);
			
//			for(int i = 0; i < bestSol.getNumberOfRoutes() + bestSol.getNumberOfClients(); i++) {
//				System.err.print(bestSol.getClientId(i) + ", ");
//			}
//			System.err.println(bestSol.getNumberOfRoutes() + bestSol.getNumberOfClients());
			
			destroyedSolution = initialSolution;
			removedClients.clear();
		
			
		
		
		return bestSol;
	}
	
	private static CVRPSolution constructNewSolution(CVRPSolution destroyedSolution) {
		CVRPSolution init = new CVRPSolution(destroyedSolution);
		CVRPSolution bestSolution = destroyedSolution;
		Random rand = new Random();
		double vehicleRemaining = Double.MAX_VALUE;
		
		for(int i = 0; i < removedClients.size(); i++) {
			for(int j = 0; j < destroyedSolution.getNumberOfRoutes(); j++) {
				destroyedSolution.addClientToRoute(j, removedClients.get(i));
				if(destroyedSolution.isFeasible() && DoubleCompare.lessThan(destroyedSolution.getVehicleRemainingCapacity(j), vehicleRemaining)) {
					vehicleRemaining = destroyedSolution.getTotalDistance();
					bestSolution = new CVRPSolution(destroyedSolution);
				}
			}
			destroyedSolution = new CVRPSolution(bestSolution);
			vehicleRemaining = Double.MAX_VALUE;
		}
		

		
		return bestSolution;
	}


	private static CVRPSolution getDestroyedSolution(CVRPSolution initialSolution, double destructionPercentage) {
		int vehicleRoutesSize = initialSolution.getNumberOfClients() + initialSolution.getNumberOfRoutes();
		int numberOfRemovedClients = (int) (vehicleRoutesSize * destructionPercentage);
		int indexToRemove;
		Random rand = new Random();

		for(int i = 0; i < numberOfRemovedClients; i++) {
			indexToRemove = rand.nextInt(vehicleRoutesSize);
			int clientToRemove = initialSolution.getClientId(indexToRemove);
			
			// If there is a separator or the client is repeated, dont stop until find another valid client
			while(clientToRemove == CVRPSolution.SEPARATOR || removedClients.contains(clientToRemove)) {
				indexToRemove = rand.nextInt(vehicleRoutesSize);
				clientToRemove = initialSolution.getClientId(indexToRemove);
//				System.err.println(indexToRemove);
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
