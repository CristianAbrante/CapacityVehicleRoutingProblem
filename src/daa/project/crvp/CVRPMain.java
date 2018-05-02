package daa.project.crvp;

import java.io.FileNotFoundException;
import java.io.IOException;

import daa.project.crvp.IO.ReaderFromFile;
import daa.project.crvp.algorithms.GRASP;
import daa.project.crvp.algorithms.Multiboot;
import daa.project.crvp.algorithms.VariableNeighborhoodSearch;
import daa.project.crvp.graphic.CVRPGraphic;
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

		// READ PROBLEM
		ReaderFromFile reader = new ReaderFromFile(args[0]);
		CVRPSpecification problemSpecification = reader.getProblemSpecification();

		// SHOW PROBLEM INFO
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

		// TEST ALGORITHMS
		TimeAndIterationsRecorder multibootRecorder = new TimeAndIterationsRecorder();
		Move[] moveList = new Move[] { new InterrouteSwap(), new Relocation(), new IntrarouteSwap(), new TwoOpt() };

		/** SOLUTION CHOOSER */
		CVRPSolution solution = Multiboot.constructRandomSolution(problemSpecification);

		int choosenSolutionGenerator = 2;
		Move choosenMove = new Relocation();
		switch (choosenSolutionGenerator) {
			case 0: // Grasp
				System.out.println("\t*** SOLUTION GENERATOR -> GRASP ***");
				solution = GRASP.grasp(problemSpecification, 100, 100, 3, new BestNeighborLocalSearch(choosenMove));
				System.out.println("Grasp. Initial solution total distance: " + solution.getTotalDistance());
				break;
			case 1: // Multiboot
				System.out.println("\t*** SOLUTION GENERATOR -> MULTIBOOT ***");
				solution = Multiboot.multiboot(problemSpecification, new BestNeighborLocalSearch(choosenMove), 100,
						multibootRecorder);
				System.out.println("Multiboot. Initial solution total distance: " + solution.getTotalDistance());
				System.out.println(multibootRecorder.toString());
				break;
			case 2: // Multiboot random
				System.out.println("\t*** SOLUTION GENERATOR -> MULTIBOOT RANDOM ***");
				solution = Multiboot.constructRandomSolution(problemSpecification);
				System.out.println("Random solution. Initial solution total distance: " + solution.getTotalDistance());
				break;
		}

		LocalSearch vnd = new VariableNeighborhoodDescent(moveList);

		int tabuTenure = (int) (0.2 * problemSpecification.getClients().size());
		boolean tabuVerbose = false;
		Move[] tabuMoveList = moveList; // new Move[] {new Relocation(), new IntrarouteSwap()};
		LocalSearch tabuSearch = new TabuSearch(tabuMoveList, tabuTenure, 10, tabuVerbose);

		/** ALGORITHM CHOOSER */
		int choosenAlgortihm = 2;
		switch (choosenAlgortihm) {
			case 0: // VNS
				System.out.println("\t*** ALGORITHM USED -> VNS + VND ***");

				for (int i = 1; i <= 100; ++i) {
					solution = VariableNeighborhoodSearch.run(solution, moveList, vnd);
					//System.out.println("VNS Solution #" + i + ": " + solution.getTotalDistance());
				}
				System.out.println("Is solution feasible after various runs of VNS?: " + solution.isFeasible());
				System.out.println("Total distance after various runs of VNS: " + solution.getTotalDistance());
				break;
			case 1: // VNS + TABU SEARCH
				System.out.println("\t*** ALGORITHM USED -> VNS + TABU SEARCH ***");
				System.out.println("Tabu Tenure:" + tabuTenure);

				for (int i = 1; i <= 100; ++i) {
					solution = VariableNeighborhoodSearch.run(solution, moveList, tabuSearch);
					//System.out.println("Tabu Solution #" + i + ": " + solution.getTotalDistance());
				}
				System.out.println("Total distance after multiple runs of Tabu Search: " + solution.getTotalDistance());
				break;
			case 2: // TABU
				System.out.println("\t*** ALGORITHM USED -> TABU SEARCH ***");
				System.out.println("Tabu Tenure:" + tabuTenure);

				for (int i = 1; i <= 100; ++i) {
					solution = tabuSearch.findLocalOptimum(solution);
					//System.out.println("Tabu Solution #" + i + ": " + solution.getTotalDistance());				
				}
				System.out.println("Total distance after multiple runs of Tabu Search: " + solution.getTotalDistance());
			default:
				break;
		}

		boolean verbose = true;
		if (verbose) {
			CVRPGraphic window = new CVRPGraphic();
			window.setSolution(solution);
			window.showSolution();
		}
	}

}
