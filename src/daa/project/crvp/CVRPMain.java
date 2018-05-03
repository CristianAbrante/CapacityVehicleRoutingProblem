package daa.project.crvp;

import java.io.FileNotFoundException;
import java.io.IOException;

import daa.project.crvp.IO.ReaderFromFile;
import daa.project.crvp.algorithms.ConstructiveDeterministic;
import daa.project.crvp.algorithms.GRASP;
import daa.project.crvp.algorithms.LargeNeighborhoodSearch;
import daa.project.crvp.algorithms.VariableNeighborhoodSearch;
import daa.project.crvp.graphic.CVRPGraphic;
import daa.project.crvp.local_search.BestNeighborLocalSearch;
import daa.project.crvp.local_search.LocalSearch;
import daa.project.crvp.local_search.VariableNeighborhoodDescent;
import daa.project.crvp.moves.InterrouteSwap;
import daa.project.crvp.moves.IntrarouteSwap;
import daa.project.crvp.moves.Move;
import daa.project.crvp.moves.Relocation;
import daa.project.crvp.moves.TwoOpt;
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
		
        Move[] moveList = new Move[] { new InterrouteSwap(), new Relocation(), new IntrarouteSwap(), new TwoOpt() };
        LocalSearch vnd = new VariableNeighborhoodDescent(moveList);
        
        // GRASP initial solution
        CVRPSolution solution = GRASP.grasp(problemSpecification, 100, 100, 3,
                new BestNeighborLocalSearch(new Relocation()));
//        CVRPSolution solution = ConstructiveDeterministic.constructDeterministicSolution(problemSpecification);

        System.out.println("GRASP. Initial solution total distance: " + solution.getTotalDistance());
        CVRPSolution optimized = LargeNeighborhoodSearch.run(problemSpecification, solution, vnd, 400, 10, 0.25);

        System.out.println("Is solution feasible after various runs of LNS?: " + solution.isFeasible());
        System.out.println("Total distance after various runs of LNS: " + solution.getTotalDistance());
//        CVRPGraphic window = new CVRPGraphic();
//        window.setSolution(optimized);
//        window.showSolution();
	}

}
