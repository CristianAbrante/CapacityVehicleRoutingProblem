package daa.project.crvp;

import java.io.FileNotFoundException;
import java.io.IOException;

import daa.project.crvp.IO.ReaderFromFile;
import daa.project.crvp.algorithms.ConstructiveDeterministic;
import daa.project.crvp.algorithms.GRASP;
import daa.project.crvp.algorithms.LargeNeighborhoodSearch;
import daa.project.crvp.algorithms.VariableNeighborhoodSearch;
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
//        CVRPSolution solution = GRASP.grasp(problemSpecification, 100, 100, 3,
//                new BestNeighborLocalSearch(new Relocation()));
        CVRPSolution solution = ConstructiveDeterministic.constructDeterministicSolution(problemSpecification);

        System.out.println("GRASP. Initial solution total distance: " + solution.getTotalDistance());
        
        // Multiboot initial solution
        //        CVRPSolution solution = Multiboot.multiboot(problemSpecification, new BestNeighborLocalSearch(new Relocation()),
        //                100);
        //        System.out.println("Multiboot. Initial solution total distance: " + solution.getTotalDistance());
        
        // Random initial solution
//        CVRPSolution solution = Multiboot.constructRandomSolution(problemSpecification);
//        System.out.println("Random solution. Initial solution total distance: " + solution.getTotalDistance());
        
       
        CVRPSolution optimized = LargeNeighborhoodSearch.run(problemSpecification, solution, vnd, 0.2);
//            
//        optimized = vnd.findLocalOptimum(optimized);
//      		System.err.println("BEST SOLUTION");
//      		for(int i = 0; i < optimized.getNumberOfClients() + optimized.getNumberOfRoutes(); i++) {
//      			System.out.print(optimized.getClientId(i) + ", ");
//      		}
//      		
//      		System.out.println(optimized.getTotalDistance());
//      		System.out.println(" ");
////      		
//      		System.err.println("INITIAL SOLUTION");
//      		for(int i = 0; i < solution.getNumberOfClients() + solution.getNumberOfRoutes(); i++) {
//      			System.out.print(solution.getClientId(i) + ", ");
//      		}
//      		System.out.println(solution.getTotalDistance());
        
//        System.out.println("Is solution feasible after various runs of VNS?: " + solution.isFeasible());
//        System.out.println("Total distance after various runs of VNS: " + solution.getTotalDistance());
        //        CVRPGraphic window = new CVRPGraphic();
        //        window.setSolution(solution);
        //        window.showSolution();
	}

}
