package daa.project.crvp;

import java.io.FileNotFoundException;
import java.io.IOException;

import daa.project.crvp.IO.ReaderFromFile;
import daa.project.crvp.algorithms.Multiboot;
import daa.project.crvp.algorithms.VariableNeighborhoodSearch;
import daa.project.crvp.local_search.BestNeighborLocalSearch;
import daa.project.crvp.local_search.LocalSearch;
import daa.project.crvp.local_search.VariableNeighborhoodDescent;
import daa.project.crvp.moves.InterrouteSwap;
import daa.project.crvp.moves.IntrarouteSwap;
import daa.project.crvp.moves.Move;
import daa.project.crvp.moves.Relocation;
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
		
		LocalSearch vnd = new VariableNeighborhoodDescent(new Move[] {
                new InterrouteSwap(),
                new Relocation(),
                new IntrarouteSwap()
		});
        //        CVRPSolution solution = GRASP.grasp(problemSpecification, 500, 500, 5, new BestNeighborLocalSearch(new Relocation()));
        CVRPSolution solution = Multiboot.multiboot(problemSpecification, new BestNeighborLocalSearch(new Relocation()),
                50);
        solution = VariableNeighborhoodSearch.run(solution, new Move[] { 
                new InterrouteSwap(),
                new Relocation(),
                new IntrarouteSwap(),
        }, vnd);
        
        System.out.println(solution.isFeasible());
        System.out.println(solution.getTotalDistance());
        //        CVRPGraphic window = new CVRPGraphic();
        //        window.setSolution(solution);
        //        window.showSolution();
	}

}
