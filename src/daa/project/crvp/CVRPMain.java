package daa.project.crvp;

import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import daa.project.crvp.IO.ReaderFromFile;
import daa.project.crvp.algorithms.ConstructiveDeterministic;
import daa.project.crvp.algorithms.GRASP;
import daa.project.crvp.algorithms.LargeNeighborhoodSearch;
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
import daa.project.crvp.utils.DoubleCompare;

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
		System.out.println("Global Objective function: " + problemSpecification.getOptimalValue());

		// TEST ALGORITHMS
		TimeAndIterationsRecorder algorithmRecorder = new TimeAndIterationsRecorder();
		Move[] moveList = new Move[] { new InterrouteSwap(), new Relocation(), new IntrarouteSwap(), new TwoOpt() };

		/** SOLUTION CHOOSER */
		CVRPSolution solution = Multiboot.constructRandomSolution(problemSpecification);

		int choosenSolutionGenerator = 2;
		Move choosenMove = new Relocation();
		switch (choosenSolutionGenerator) {
			case 0: // Grasp
				System.out.println("\t*** SOLUTION GENERATOR -> GRASP ***");
				solution = GRASP.grasp(problemSpecification, 100, 100, 3, new BestNeighborLocalSearch(choosenMove),
						algorithmRecorder);
				System.out.println("Grasp. Initial solution total distance: " + solution.getTotalDistance());
				break;
			case 1: // Multiboot
				System.out.println("\t*** SOLUTION GENERATOR -> MULTIBOOT ***");
				solution = Multiboot.multiboot(problemSpecification, new BestNeighborLocalSearch(choosenMove), 100,
						algorithmRecorder);
				System.out.println("Multiboot. Initial solution total distance: " + solution.getTotalDistance());
				System.out.println(algorithmRecorder.toString());
				break;
			case 2: // Multiboot random
				System.out.println("\t*** SOLUTION GENERATOR -> MULTIBOOT RANDOM ***");
				solution = Multiboot.constructRandomSolution(problemSpecification);
				System.out.println("Random solution. Initial solution total distance: " + solution.getTotalDistance());
				break;
			case 3: // Multiboot random
				System.out.println("\t*** SOLUTION GENERATOR -> CONSTRUCTIVE DETERMINISTIC ***");
				solution = ConstructiveDeterministic.constructDeterministicSolution(problemSpecification);
				System.out.println("Deterministic solution. Initial solution total distance: " + solution.getTotalDistance());
				break;
		}

		LocalSearch vnd = new VariableNeighborhoodDescent(moveList);

		/** TABU PARAMS */
		int tabuTenure = (int) (0.2 * problemSpecification.getClients().size());
		boolean tabuVerbose = false;
		Move[] tabuMoveList = moveList; // new Move[] {new Relocation(), new IntrarouteSwap()};
		LocalSearch tabuSearch = new TabuSearch(tabuMoveList, tabuTenure, 10, tabuVerbose, algorithmRecorder);

		/** LNS PARAMS */
		int maxReconstructions = 10;
		int minDiffLocalSearch = 100;
		double destructionPercentage = 0.25;

		/** ALGORITHM CHOOSER */

		int choosenAlgortihm = 2;
		long maximumSeconds = 1000;
		CVRPSolution bestSolution = solution;
		switch (choosenAlgortihm) {
			case 0: // VNS
				System.out.println("\t*** ALGORITHM USED -> VNS + VND ***");
				algorithmRecorder.starting();

				while (algorithmRecorder.getCurrentTime() < maximumSeconds) {
					algorithmRecorder.aboutToDoNextIteration();
					solution = VariableNeighborhoodSearch.run(solution, moveList, vnd);
					if (DoubleCompare.lessThan(solution.getTotalDistance(), bestSolution.getTotalDistance())) {
						bestSolution = solution;
						algorithmRecorder.foundBetterSolution(bestSolution);
					}
				}
				algorithmRecorder.finishing();
				System.out.println("Is solution feasible after various runs of VNS?: " + solution.isFeasible());
				System.out.println("Total distance after various runs of VNS: " + solution.getTotalDistance());
				break;
			case 1: // VNS + TABU SEARCH
				System.out.println("\t*** ALGORITHM USED -> VNS + TABU SEARCH ***");
				System.out.println("Tabu Tenure:" + tabuTenure);
				algorithmRecorder.starting();

				while (algorithmRecorder.getCurrentTime() < maximumSeconds) {
					algorithmRecorder.aboutToDoNextIteration();
					solution = VariableNeighborhoodSearch.run(solution, moveList, tabuSearch);
					if (DoubleCompare.lessThan(solution.getTotalDistance(), bestSolution.getTotalDistance())) {
						bestSolution = solution;
						algorithmRecorder.foundBetterSolution(bestSolution);
					}
				}
				algorithmRecorder.finishing();
				System.out.println("Total distance after multiple runs of Tabu Search: " + solution.getTotalDistance());
				break;
			case 2: // TABU
				System.out.println("\t*** ALGORITHM USED -> TABU SEARCH ***");
				System.out.println("Tabu Tenure:" + tabuTenure);
				algorithmRecorder.starting();

				while (algorithmRecorder.getCurrentTime() < maximumSeconds) {
					algorithmRecorder.aboutToDoNextIteration();
					solution = tabuSearch.findLocalOptimum(solution);
					if (DoubleCompare.lessThan(solution.getTotalDistance(), bestSolution.getTotalDistance())) {
						bestSolution = solution;
						algorithmRecorder.foundBetterSolution(bestSolution);
					}
				}
				algorithmRecorder.finishing();
				System.out.println("Total distance after multiple runs of Tabu Search: " + solution.getTotalDistance());
				break;

			case 3: // LNS
				System.out.println("\t*** ALGORITHM USED -> LNS + VND ***");
				System.out.println("LNS maxReconstructions: " + maxReconstructions);
				System.out.println("LNS minDiffLocalSearch: " + minDiffLocalSearch);
				System.out.println("LNS destructionPercentage: " + destructionPercentage);

				algorithmRecorder.starting();

				while (algorithmRecorder.getCurrentTime() < maximumSeconds) {
					algorithmRecorder.aboutToDoNextIteration();
					solution = LargeNeighborhoodSearch.run(problemSpecification, solution, vnd, maxReconstructions,
							minDiffLocalSearch, destructionPercentage);
					if (DoubleCompare.lessThan(solution.getTotalDistance(), bestSolution.getTotalDistance())) {
						bestSolution = solution;
						algorithmRecorder.foundBetterSolution(bestSolution);
					}
				}
				algorithmRecorder.finishing();

				System.out.println("Total distance after run LNS search: " + solution.getTotalDistance());
			default:
				break;
		}
		System.out.println(algorithmRecorder);

		boolean verbose = true;
		if (verbose) {
			savePaint(solution, "example.png");
		}
	}

	public static void savePaint(CVRPSolution solution, String algorithmName) {
		CVRPGraphic window = new CVRPGraphic();
		window.setSolution(solution);
		window.showSolution();

		Rectangle rect = window.getBounds();
		try {
			BufferedImage captureImage = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);
			window.paint(captureImage.getGraphics());
			ImageIO.write(captureImage, "png", new File(algorithmName));
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}

		window.setVisible(false);
		window.dispose();
	}

}
