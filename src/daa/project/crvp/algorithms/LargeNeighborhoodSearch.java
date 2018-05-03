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
		
//		for(int i = 0; i < initialSolution.getNumberOfRoutes() + initialSolution.getNumberOfClients(); i++) {
//			System.out.print(initialSolution.getClientId(i) + ", ");
//		}
//		System.out.println();
//		System.out.println(initialSolution.getNumberOfRoutes());
//		System.out.println(initialSolution.getNumberOfClientsInRoute(4));
		
		LargeNeighborhoodSearch.removedClients = new ArrayList<>();
		LargeNeighborhoodSearch.problemSpecification = problemSpecification;
		LargeNeighborhoodSearch.localSearch = localSearch;
		LargeNeighborhoodSearch.initialSolution = new CVRPSolution(initialSolution);
		
		
//			System.out.println();
//			System.out.println("INITSOL");
//			for(int j = 0; j < initialSolution.getNumberOfClients() + initialSolution.getNumberOfRoutes(); j++) {
//				System.out.print(initialSolution.getClientId(j) + ", ");
//			}
			CVRPSolution bestConstructedSol = new CVRPSolution(initialSolution);
		
			for(int i = 0; i < 400; i++) {
			
			CVRPSolution destroyedSolution = new CVRPSolution(getDestroyedSolution(initialSolution, destructionPercentage));

					System.out.println("REMOVED CLIENTS");
					for(int j = 0; j < removedClients.size(); j++) {
						System.out.print(removedClients.get(j) + ", ");
					}
					System.out.println(" ");
			CVRPSolution actualConstructedSol = new CVRPSolution(constructNewSolution(destroyedSolution));
			
			if(actualConstructedSol.getTotalDistance() < bestConstructedSol.getTotalDistance()) {
				bestConstructedSol = new CVRPSolution(actualConstructedSol);
			}
			
			
			destroyedSolution = new CVRPSolution(initialSolution);
			removedClients.clear();
		
			}
			
		
		
		return bestConstructedSol;
	}
	
	private static CVRPSolution constructNewSolution(CVRPSolution destroyedSolution) {
		CVRPSolution init = new CVRPSolution(destroyedSolution);
		CVRPSolution bestSolution = null;
		Random rand = new Random();
		double vehicleRemaining;
		
		
		destroyedSolution.addClientToRoute(0, removedClients.get(0));
		vehicleRemaining = destroyedSolution.getTotalDistance();
		bestSolution = new CVRPSolution(destroyedSolution);
		
		destroyedSolution = new CVRPSolution(init);
		
		for(int j = 0; j < removedClients.size(); j++) {
			destroyedSolution.addClientToRoute(0, removedClients.get(j));
			vehicleRemaining = destroyedSolution.getTotalDistance();
			bestSolution = new CVRPSolution(destroyedSolution);
			
			for(int i = 0; i < destroyedSolution.getNumberOfRoutes(); i++) {
				destroyedSolution.addClientToRoute(i, removedClients.get(j));
//				System.out.println("RUTA : " +  i + " -> añadiendo " + removedClients.get(0) + " da: " + destroyedSolution.getTotalDistance() + " y la mejor es: " + vehicleRemaining);
				if(destroyedSolution.isFeasible() && DoubleCompare.lessThan(destroyedSolution.getTotalDistance(), vehicleRemaining)) {
					vehicleRemaining = destroyedSolution.getTotalDistance();
					bestSolution = new CVRPSolution(destroyedSolution);
				}
				destroyedSolution = new CVRPSolution(init);
			}
			init = new CVRPSolution(bestSolution);
			destroyedSolution = new CVRPSolution(bestSolution);
		}
		
//		for(int i = 0; i < bestSolution.getNumberOfRoutes() + bestSolution.getNumberOfClients(); i++) {
//			System.err.print(bestSolution.getClientId(i) + ", ");
//		}
//		System.err.println(bestSolution.getNumberOfRoutes() + bestSolution.getNumberOfClients());
		
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
