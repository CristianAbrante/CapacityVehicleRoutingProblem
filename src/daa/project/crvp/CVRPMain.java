package daa.project.crvp;

import java.io.FileNotFoundException;
import java.io.IOException;

import daa.project.crvp.IO.ReaderFromFile;
import daa.project.crvp.algorithms.Multiboot;
import daa.project.crvp.local_seach.FirstBetterNeighborLocalSearch;
import daa.project.crvp.moves.IntrarouteSwap;
import daa.project.crvp.problem.CVRPClient;
import daa.project.crvp.problem.CVRPSolution;
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
		
        //		CVRPGraphic window = new CVRPGraphic();
        //		window.setSolution(GRASP.grasp(problemSpecification, 100, 100, 5, new BestNeighborLocalSearch(new IntrarouteSwap())));
        //		window.showSolution();

        //		CVRPGraphic window2 = new CVRPGraphic();
        //		window2.setSolution(ConstructiveDeterministic.constructDeterministicSolution(problemSpecification));
        //		window2.showSolution();
        
        //        CVRPGraphic window2 = new CVRPGraphic();
        CVRPSolution solution = Multiboot.multiboot(problemSpecification,
                new FirstBetterNeighborLocalSearch(new IntrarouteSwap()), 100000);
        //        window2.setSolution(solution);
        //        window2.showSolution();
        System.out.println(solution.isFeasible());
        System.out.println(solution.getTotalDistance());
        
//		ArrayList<Integer> sol = ConstructiveDeterministic.constructDeterministicSolution(problemSpecification).getVehicleRoutes();
//		
//		for(Integer route : sol) {
//			System.out.println(route);
//		}
		
		
	}

}
