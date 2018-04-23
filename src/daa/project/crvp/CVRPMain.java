package daa.project.crvp;

import java.io.FileNotFoundException;
import java.io.IOException;

import daa.project.crvp.IO.ReaderFromFile;
import daa.project.crvp.algorithms.ConstructiveDeterministic;
import daa.project.crvp.algorithms.GRASP;
import daa.project.crvp.graphic.CVRPGraphic;
import daa.project.crvp.local_search.BestNeighborLocalSearch;
import daa.project.crvp.moves.IntrarouteSwap;
import daa.project.crvp.problem.CVRPClient;
import daa.project.crvp.problem.CVRPSpecification;

public class CVRPMain {

	/**
	 * Simple main method to show the problem specification from a file.
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		if (args.length != 1) {
			throw new IllegalArgumentException("You must specify the CRVP problem specification file. ");
		}		
		
		ReaderFromFile reader = new ReaderFromFile(args[0]);
		CVRPSpecification problemSpecification = reader.getProblemSpecification();
		
		System.out.println("Depot ID: " + problemSpecification.getDepotID());
		System.out.println("Capacity: " + problemSpecification.getCapacity());
		System.out.println("Client Number: " + problemSpecification.getClients().size());
		System.out.println("Minum vehicles: " + problemSpecification.getMinimunVehicles());
		
		System.out.println("Client list: ");
		int totalDemand = 0;
		for (CVRPClient client : problemSpecification.getClients()) {
			System.out.println(client);
			totalDemand += client.getDemand();
		}
		
		System.out.println("Total Demand: " + totalDemand);
		
		
		
		CVRPGraphic window = new CVRPGraphic();
		window.setSolution(GRASP.grasp(problemSpecification, 100, 100, 5, new BestNeighborLocalSearch(new IntrarouteSwap())));
		window.showSolution();
		
		CVRPGraphic window2 = new CVRPGraphic();
		window2.setSolution(ConstructiveDeterministic.constructDeterministicSolution(problemSpecification));
		window2.showSolution();
	}

}
