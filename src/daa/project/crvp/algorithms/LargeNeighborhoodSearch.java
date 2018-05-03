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
		
		
			
			
			CVRPSolution bestConstructedSol = new CVRPSolution(initialSolution);
			
			
//			System.out.println(initialSolution.getRouteFromClient(-1));
			
			for(int i = 0; i < 400; i++) {
			
//				System.out.println("INITSOL");
//				for(int j = 0; j < initialSolution.getNumberOfClients() + initialSolution.getNumberOfRoutes(); j++) {
//					System.out.print(initialSolution.getClientId(j) + ", ");
//				}
//				System.out.println();
				
			CVRPSolution destroyedSolution = new CVRPSolution(getDestroyedSolution(initialSolution, destructionPercentage));
			
//			System.out.println("REMOVED CLIENTS");
//			for(int j = 0; j < removedClients.size(); j++) {
//				System.out.print(removedClients.get(j) + ", ");
//			}
//			System.out.println(" ");
//			
//			System.out.println("destroyedSolution");
//			for(int j = 0; j < destroyedSolution.getNumberOfClients()+destroyedSolution.getNumberOfRoutes(); j++) {
//				System.out.print(destroyedSolution.getClientId(j) + ", ");
//			}
//			System.out.println();
					
			CVRPSolution actualConstructedSol = new CVRPSolution(constructNewSolution(destroyedSolution));
			

			if(actualConstructedSol.isFeasible() && actualConstructedSol.getTotalDistance() < bestConstructedSol.getTotalDistance()) {
				if(bestConstructedSol.getTotalDistance() - actualConstructedSol.getTotalDistance() > 10.0) {
					bestConstructedSol = new CVRPSolution(localSearch.findLocalOptimum(actualConstructedSol));
				}else {
					bestConstructedSol = new CVRPSolution(actualConstructedSol);
				}
				
				destroyedSolution = new CVRPSolution(initialSolution);
			}else {
				destroyedSolution = new CVRPSolution(initialSolution);
			}
			
			
			
//			System.out.println("initialSolution");
//			for(int j = 0; j < initialSolution.getNumberOfClients()+initialSolution.getNumberOfRoutes(); j++) {
//				System.out.print(initialSolution.getClientId(j) + ", ");
//			}
//			System.out.println();
			
			
			destroyedSolution = new CVRPSolution(LargeNeighborhoodSearch.initialSolution);
			removedClients.clear();
		
			}
			
		
		
		return new CVRPSolution(localSearch.findLocalOptimum(bestConstructedSol));
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

		ArrayList<Integer> previousVehiclesRoutes = new ArrayList<>();
		
		for(int i = 0; i < vehicleRoutesSize; i++) {
			previousVehiclesRoutes.add(initialSolution.getClientId(i));
		}
			
		for(int i = 0; i < numberOfRemovedClients; i++) {
			indexToRemove = rand.nextInt(previousVehiclesRoutes.size());
			int clientToRemove = previousVehiclesRoutes.get(indexToRemove);
			
			while(clientToRemove == CVRPSolution.SEPARATOR || removedClients.contains(clientToRemove) || checkRouteEmpty(indexToRemove, previousVehiclesRoutes)) {
				indexToRemove = rand.nextInt(previousVehiclesRoutes.size());
				clientToRemove = previousVehiclesRoutes.get(indexToRemove);
			}
			previousVehiclesRoutes.remove(previousVehiclesRoutes.indexOf(clientToRemove));
			removedClients.add(clientToRemove);
		}
	
//		for(int i = 0; i < previousVehiclesRoutes.size(); i++) {
//			System.out.print(previousVehiclesRoutes.get(i) + ", ");
//		}
//		
//		System.out.println();
		return new CVRPSolution(problemSpecification, previousVehiclesRoutes);
	}
	
	private static boolean checkRouteEmpty(int indexToRemove, ArrayList<Integer> previousVehiclesRoutes) {
		int previousClient;
		int client;
		int nextClient;
		
		if(indexToRemove >= 1 && indexToRemove <= previousVehiclesRoutes.size() - 1) {
			previousClient = previousVehiclesRoutes.get(indexToRemove - 1);
			client = previousVehiclesRoutes.get(indexToRemove);
			nextClient = previousVehiclesRoutes.get(indexToRemove + 1);
		
			if((previousClient != CVRPSolution.SEPARATOR) && (nextClient != CVRPSolution.SEPARATOR) && (client != CVRPSolution.SEPARATOR)) {
				return false;
			}
		}
		
		if(indexToRemove == 0) {
			client = previousVehiclesRoutes.get(indexToRemove);
			nextClient = previousVehiclesRoutes.get(indexToRemove + 1);
			
			if((nextClient != CVRPSolution.SEPARATOR) && (client != CVRPSolution.SEPARATOR)) {
				return false;
			}
		}
		
		if(indexToRemove == previousVehiclesRoutes.size() - 1) {
			previousClient = previousVehiclesRoutes.get(indexToRemove - 1);
			client = previousVehiclesRoutes.get(indexToRemove);
			
			if((previousClient != CVRPSolution.SEPARATOR) && (client != CVRPSolution.SEPARATOR)) {
				return false;
			}
		}
		
		return true;
	}
}
