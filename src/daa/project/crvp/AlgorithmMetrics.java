package daa.project.crvp;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import daa.project.crvp.IO.ReaderFromFile;
import daa.project.crvp.algorithms.GRASP;
import daa.project.crvp.local_seach.FirstBetterNeighborLocalSearch;
import daa.project.crvp.local_search.BestNeighborLocalSearch;
import daa.project.crvp.local_search.LocalSearch;
import daa.project.crvp.metrics.TimeAndIterationsRecorder;
import daa.project.crvp.moves.InterrouteSwap;
import daa.project.crvp.moves.IntrarouteSwap;
import daa.project.crvp.moves.Relocation;
import daa.project.crvp.moves.TwoOpt;
import daa.project.crvp.problem.CVRPSpecification;

public class AlgorithmMetrics {

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
        
        //        GRASP;3;10;BN + Relocation;4;0;11;0;229;0.0;
        //        for (int i = 0; i < 10; ++i) {
        //            TimeAndIterationsRecorder recorder = new TimeAndIterationsRecorder();
        //            GRASP.grasp(problemSpecification, 1000000, 10, 3, new BestNeighborLocalSearch(new Relocation()), recorder);
        //            System.out.println(recorder);            
        //        }
        
        String results = graspMetrics(new CVRPSpecification[] { problemSpecification }, 10);
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("grasp_results.txt")));
        writer.print(results);
        writer.flush();
        
        //		// SHOW PROBLEM INFO
        //		System.out.println("Depot ID: " + problemSpecification.getDepotID());
        //		System.out.println("Capacity: " + problemSpecification.getCapacity());
        //		System.out.println("Client Number: " + problemSpecification.getClients().size());
        //		System.out.println("Minum vehicles: " + problemSpecification.getMinimunVehicles());
        //
        //		System.out.println("Client list: ");
        //		int totalDemand = 0;
        //		for (CVRPClient client : problemSpecification.getClients()) {
        //			System.out.println(client);
        //			totalDemand += client.getDemand();
        //		}
        //
        //		System.out.println("Total Demand: " + totalDemand);
        //
        //		// TEST ALGORITHMS
        //		TimeAndIterationsRecorder algorithmRecorder = new TimeAndIterationsRecorder();
        //		Move[] moveList = new Move[] { new InterrouteSwap(), new Relocation(), new IntrarouteSwap(), new TwoOpt() };
        //
        //		/** SOLUTION CHOOSER */
        //		CVRPSolution solution = Multiboot.constructRandomSolution(problemSpecification);
        //
        //        int choosenSolutionGenerator = 0;
        //		Move choosenMove = new Relocation();
        //		switch (choosenSolutionGenerator)
        //		{
        //			case 0: // Grasp
        //				System.out.println("\t*** SOLUTION GENERATOR -> GRASP ***");
        //                solution = GRASP.grasp(problemSpecification, 100, 100, 3, new BestNeighborLocalSearch(choosenMove),
        //                        algorithmRecorder);
        //				System.out.println("Grasp. Initial solution total distance: " + solution.getTotalDistance());
        //				break;
        //			case 1: // Multiboot
        //				System.out.println("\t*** SOLUTION GENERATOR -> MULTIBOOT ***");
        //				solution = Multiboot.multiboot(problemSpecification, new BestNeighborLocalSearch(choosenMove), 100,
        //						algorithmRecorder);
        //				System.out.println("Multiboot. Initial solution total distance: " + solution.getTotalDistance());
        //				System.out.println(algorithmRecorder.toString());
        //				break;
        //			case 2: // Multiboot random
        //				System.out.println("\t*** SOLUTION GENERATOR -> MULTIBOOT RANDOM ***");
        //				solution = Multiboot.constructRandomSolution(problemSpecification);
        //				System.out.println("Random solution. Initial solution total distance: " + solution.getTotalDistance());
        //				break;
        //			case 3: // Multiboot random
        //				System.out.println("\t*** SOLUTION GENERATOR -> CONSTRUCTIVE DETERMINISTIC ***");
        //				solution = ConstructiveDeterministic.constructDeterministicSolution(problemSpecification);
        //				System.out.println("Deterministic solution. Initial solution total distance: " + solution.getTotalDistance());
        //				break;
        //		}

        //		LocalSearch vnd = new VariableNeighborhoodDescent(moveList);
        //
        //		/** TABU PARAMS */
        //		int tabuTenure = (int) (0.2 * problemSpecification.getClients().size());
        //		boolean tabuVerbose = false;
        //		Move[] tabuMoveList = moveList; // new Move[] {new Relocation(), new IntrarouteSwap()};
        //		LocalSearch tabuSearch = new TabuSearch(tabuMoveList, tabuTenure, 10, tabuVerbose, algorithmRecorder);
        //
        //		/** LNS PARAMS */
        //		int maxReconstructions = 10;
        //        int minDiffLocalSearch = 5;
        //		double destructionPercentage = 0.25;
        //
        //		/** ALGORITHM CHOOSER */
        //
        //        int choosenAlgortihm = 3;
        //        long maximumSeconds = 1000;
        //		CVRPSolution bestSolution = solution;
        //		switch (choosenAlgortihm)
        //		{
        //			case 0: // VNS
        //				System.out.println("\t*** ALGORITHM USED -> VNS + VND ***");
        //				algorithmRecorder.starting();
        //
        //				while (algorithmRecorder.getCurrentTime() < maximumSeconds) {
        //					algorithmRecorder.aboutToDoNextIteration();
        //					solution = VariableNeighborhoodSearch.run(solution, moveList, vnd);
        //					if (DoubleCompare.lessThan(solution.getTotalDistance(), bestSolution.getTotalDistance())) {
        //						bestSolution = solution;
        //						algorithmRecorder.foundBetterSolution(bestSolution);
        //					}
        //				}
        //				algorithmRecorder.finishing();
        //				System.out.println("Is solution feasible after various runs of VNS?: " + solution.isFeasible());
        //				System.out.println("Total distance after various runs of VNS: " + solution.getTotalDistance());
        //				break;
        //			case 1: // VNS + TABU SEARCH
        //				System.out.println("\t*** ALGORITHM USED -> VNS + TABU SEARCH ***");
        //				System.out.println("Tabu Tenure:" + tabuTenure);
        //				algorithmRecorder.starting();
        //
        //				while (algorithmRecorder.getCurrentTime() < maximumSeconds) {
        //					algorithmRecorder.aboutToDoNextIteration();
        //					solution = VariableNeighborhoodSearch.run(solution, moveList, tabuSearch);
        //					if (DoubleCompare.lessThan(solution.getTotalDistance(), bestSolution.getTotalDistance())) {
        //						bestSolution = solution;
        //						algorithmRecorder.foundBetterSolution(bestSolution);
        //					}
        //				}
        //				algorithmRecorder.finishing();
        //				System.out.println("Total distance after multiple runs of Tabu Search: " + solution.getTotalDistance());
        //				break;
        //			case 2: // TABU
        //				System.out.println("\t*** ALGORITHM USED -> TABU SEARCH ***");
        //				System.out.println("Tabu Tenure:" + tabuTenure);
        //				algorithmRecorder.starting();
        //
        //				while (algorithmRecorder.getCurrentTime() < maximumSeconds) {
        //					algorithmRecorder.aboutToDoNextIteration();
        //					solution = tabuSearch.findLocalOptimum(solution);
        //					if (DoubleCompare.lessThan(solution.getTotalDistance(), bestSolution.getTotalDistance())) {
        //						bestSolution = solution;
        //						algorithmRecorder.foundBetterSolution(bestSolution);
        //					}
        //				}
        //				algorithmRecorder.finishing();				
        //				System.out.println("Total distance after multiple runs of Tabu Search: " + solution.getTotalDistance());
        //				break;
        //
        //			case 3: // LNS
        //				System.out.println("\t*** ALGORITHM USED -> LNS + VND ***");
        //				System.out.println("LNS maxReconstructions: " + maxReconstructions);
        //				System.out.println("LNS minDiffLocalSearch: " + minDiffLocalSearch);
        //				System.out.println("LNS destructionPercentage: " + destructionPercentage);
        //
        //				algorithmRecorder.starting();
        //
        //				while (algorithmRecorder.getCurrentTime() < maximumSeconds) {
        //					algorithmRecorder.aboutToDoNextIteration();
        //					solution = LargeNeighborhoodSearch.run(problemSpecification, solution, vnd, maxReconstructions,
        //							minDiffLocalSearch, destructionPercentage);
        //					if (DoubleCompare.lessThan(solution.getTotalDistance(), bestSolution.getTotalDistance())) {
        //						bestSolution = solution;
        //						algorithmRecorder.foundBetterSolution(bestSolution);
        //					}
        //				}
        //				algorithmRecorder.finishing();
        //				
        //				System.out.println("Total distance after run LNS search: " + solution.getTotalDistance());
        //			default:
        //				break;
        //		}
        //		System.out.println(algorithmRecorder);
        //
        //		boolean verbose = false;
        //		if (verbose) {
        //			CVRPGraphic window = new CVRPGraphic();
        //			window.setSolution(solution);
        //			window.showSolution();
        //		}
	}
    
    public static String graspMetrics(CVRPSpecification[] problemSpecifications, int numTests) {
        final int MAX_NUM_ITERATIONS = 1000000;
        final int NUM_SAMPLES = 6;
        
        int restrictedCandidateListNumbers[] = { 3, 5 };
        int iterationsWithNoImprovement[] = { 10, 50, 100 };
        LocalSearch localSearches[] = {
                new BestNeighborLocalSearch(new Relocation()), 
                new BestNeighborLocalSearch(new InterrouteSwap()), 
                new BestNeighborLocalSearch(new IntrarouteSwap()),
                new BestNeighborLocalSearch(new TwoOpt()),
                new FirstBetterNeighborLocalSearch(new Relocation()),
                new FirstBetterNeighborLocalSearch(new InterrouteSwap()),
                new FirstBetterNeighborLocalSearch(new IntrarouteSwap()),
                new FirstBetterNeighborLocalSearch(new TwoOpt()),
        };
        String localSearchesnames[] = {
                "BN + Relocation", 
                "BN + Interroute", 
                "BN + IntrarouteSwap",
                "BN + TwoOpt",
                "FBN + Relocation",
                "FBN + InterrouteSwap",
                "FBN + IntrarouteSwap",
                "FBN + TwoOpt",
        };
        
        String sampleNames[] = {
                "S-N32-K5.vrp",
                "S-N43-K6.vrp",
                "S-N50-K7.vrp",
                "S-N57-K9.vrp",
                "S-N62-K8.vrp",
                "S-N80-K10.vrp",
        };

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
                + TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
                + TimeAndIterationsRecorder.CSV_SEPARATOR);
        for (int i = 0; i < NUM_SAMPLES; ++i) {
            stringBuilder.append(sampleNames[i].split("\\.")[0] + TimeAndIterationsRecorder.CSV_SEPARATOR
                    + TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
                    + TimeAndIterationsRecorder.CSV_SEPARATOR);
        }
        stringBuilder.append("ALGORITHM" + TimeAndIterationsRecorder.CSV_SEPARATOR
                                + "R.C.L" + TimeAndIterationsRecorder.CSV_SEPARATOR
                                + "I.W.I" + TimeAndIterationsRecorder.CSV_SEPARATOR
                                + "L.S" + TimeAndIterationsRecorder.CSV_SEPARATOR
                                + "ITERATION" + TimeAndIterationsRecorder.CSV_SEPARATOR
        );
        for (int i = 0; i < NUM_SAMPLES; ++i) {
            stringBuilder.append("I.W.F" + TimeAndIterationsRecorder.CSV_SEPARATOR
                                + "T.N.O.I" + TimeAndIterationsRecorder.CSV_SEPARATOR
                                + "E.T.F" + TimeAndIterationsRecorder.CSV_SEPARATOR
                                + "T.E.T" + TimeAndIterationsRecorder.CSV_SEPARATOR
                                + "SOL." + TimeAndIterationsRecorder.CSV_SEPARATOR
            );
        }
        
        for (int rcl : restrictedCandidateListNumbers) {
            for (int numIterations : iterationsWithNoImprovement) {
                for (int localSearchPos = 0; localSearchPos < localSearches.length; ++localSearchPos) {
                    for (int i = 1; i <= numTests; ++i) {
                        stringBuilder.append(
                                "GRASP" + TimeAndIterationsRecorder.CSV_SEPARATOR
                                + rcl + TimeAndIterationsRecorder.CSV_SEPARATOR
                                + numIterations + TimeAndIterationsRecorder.CSV_SEPARATOR
                                + localSearchesnames[localSearchPos] + TimeAndIterationsRecorder.CSV_SEPARATOR
                                + i + TimeAndIterationsRecorder.CSV_SEPARATOR
                        );
                        System.out.print("GRASP" + TimeAndIterationsRecorder.CSV_SEPARATOR + rcl
                                + TimeAndIterationsRecorder.CSV_SEPARATOR + numIterations
                                + TimeAndIterationsRecorder.CSV_SEPARATOR + localSearchesnames[localSearchPos]
                                + TimeAndIterationsRecorder.CSV_SEPARATOR + i
                                + TimeAndIterationsRecorder.CSV_SEPARATOR);
                        for (CVRPSpecification problemSpecification : problemSpecifications) {
                            TimeAndIterationsRecorder algorithmRecorder = new TimeAndIterationsRecorder();
                            GRASP.grasp(problemSpecification, MAX_NUM_ITERATIONS, numIterations, rcl, localSearches[localSearchPos], algorithmRecorder);
                            stringBuilder.append(algorithmRecorder.toString() + TimeAndIterationsRecorder.CSV_SEPARATOR);
                            System.out.print(algorithmRecorder.toString() + TimeAndIterationsRecorder.CSV_SEPARATOR);
                        }
                        stringBuilder.append("\n");
                        System.out.println();
                    }
                }
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

}