package daa.project.crvp;

import java.io.FileNotFoundException;
import java.io.IOException;

import daa.project.crvp.IO.ReaderFromFile;
import daa.project.crvp.algorithms.GRASP;
import daa.project.crvp.algorithms.Multiboot;
import daa.project.crvp.algorithms.VariableNeighborhoodSearch;
import daa.project.crvp.local_search.BestNeighborLocalSearch;
import daa.project.crvp.local_search.LocalSearch;
import daa.project.crvp.local_search.TabuSearch;
import daa.project.crvp.local_search.VariableNeighborhoodDescent;
import daa.project.crvp.metrics.TimeAndIterationsRecorder;
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

		//
		// Algorithms tests
		//

		TimeAndIterationsRecorder multibootRecorder = new TimeAndIterationsRecorder();

		Move[] moveList = new Move[] { new InterrouteSwap(), new Relocation(), new IntrarouteSwap(), new TwoOpt() };
		LocalSearch vnd = new VariableNeighborhoodDescent(moveList);

		// GRASP initial solution
		// CVRPSolution solution = GRASP.grasp(problemSpecification, 100, 100, 3,
		// new BestNeighborLocalSearch(new Relocation()));
		// System.out.println("GRASP. Initial solution total distance: " +
		// solution.getTotalDistance());

		// Multiboot initial solution
		// CVRPSolution solution = Multiboot.multiboot(problemSpecification, new
		// BestNeighborLocalSearch(new Relocation()),
		// 100, multibootRecorder);
		// System.out.println("Multiboot. Initial solution total distance: " +
		// solution.getTotalDistance());
		// System.out.println(multibootRecorder.toString());
		//
		// // Random initial solution
		//// CVRPSolution solution =
		// Multiboot.constructRandomSolution(problemSpecification);
		//// System.out.println("Random solution. Initial solution total distance: " +
		// solution.getTotalDistance());
		//
		// for (int i = 0; i < 100; ++i) {
		// solution = VariableNeighborhoodSearch.run(solution, moveList, vnd);
		// }
		// System.out.println("Is solution feasible after various runs of VNS?: " +
		// solution.isFeasible());
		// System.out.println("Total distance after various runs of VNS: " +
		// solution.getTotalDistance());
		// CVRPGraphic window = new CVRPGraphic();
		// window.setSolution(solution);
		// window.showSolution();

		CVRPSolution solution = Multiboot.multiboot(problemSpecification, new BestNeighborLocalSearch(new Relocation()),
				100, multibootRecorder);
//		CVRPSolution solution = Multiboot.constructRandomSolution(problemSpecification);
		System.out.println("Multiboot. Initial solution total distance: " + solution.getTotalDistance());
		
		TabuSearch tabuSearch = new TabuSearch(new Move[] { new InterrouteSwap() }, 3, 1000);
		solution = tabuSearch.findLocalOptimum(solution);
		System.out.println("Total distance after various runs of Tabu Search: " + solution.getTotalDistance());
	}

}
