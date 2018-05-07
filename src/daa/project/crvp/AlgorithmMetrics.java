package daa.project.crvp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import daa.project.crvp.IO.ReaderFromFile;
import daa.project.crvp.csv_generators.GraspCsvGenerator;
import daa.project.crvp.csv_generators.MultibootCsvGenerator;
import daa.project.crvp.csv_generators.VnsGraspConstructiveCsvGenerator;
import daa.project.crvp.csv_generators.VnsRandomConstructiveCsvGenerator;
import daa.project.crvp.problem.CVRPSpecification;

/**
 * Class designed to measure the different algorithms of the VRP and to export
 * the results in csv format.
 */
public class AlgorithmMetrics extends Thread {
	public static final String OUTPUT_DIR = "output/algorithms/";
	public static final String INPUT_DIR = "input/samples/";
	public static final String sampleNames[] = { "S-N32-K5.vrp", "S-N43-K6.vrp", "S-N50-K7.vrp", "S-N57-K9.vrp",
			"S-N62-K8.vrp", "S-N80-K10.vrp" };
	public static final int NUM_SAMPLES = 6;

    public static CVRPSpecification[] readProblemSpecificationFromSamples() throws FileNotFoundException, IOException {
		CVRPSpecification[] problemSpecificationArray = new CVRPSpecification[NUM_SAMPLES];

		for (int i = 0; i < NUM_SAMPLES; ++i) {
			ReaderFromFile reader = new ReaderFromFile(INPUT_DIR + sampleNames[i]);
			problemSpecificationArray[i] = reader.getProblemSpecification();
		}

		return problemSpecificationArray;
	}

	/**
	 * Main method that measure the algorithm chosen.
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
        ArrayList<Thread> threads = new ArrayList<>();
        int restrictedCandidateListNumbers[] = { 3, 5 };
        int iterationsWithNoImprovement[] = { 5, 10, 25 };
        int numberTests = 5;
        
        int algorithmOption = 0;

		switch (algorithmOption)
		{
			case 0: // GRASP
                for (int rclSize : restrictedCandidateListNumbers) {
                    for (int numIts : iterationsWithNoImprovement) {
                        threads.add(new GraspCsvGenerator(readProblemSpecificationFromSamples(), numberTests, rclSize, numIts));
                    }
                }
				break;
			case 1: // MULTIBOOT
                for (int numIts : iterationsWithNoImprovement) {
                    threads.add(new MultibootCsvGenerator(readProblemSpecificationFromSamples(), numberTests, numIts));
                }
				break;
			case 2: // VNS with constructive phase of GRASP as initial solution
			    for (int rclSize : restrictedCandidateListNumbers) {
			        for (int numIts : iterationsWithNoImprovement) {
			            threads.add(new VnsGraspConstructiveCsvGenerator(readProblemSpecificationFromSamples(), numberTests, rclSize, numIts));
			        }
			    }
			    break;
            case 3: // VNS with random initial solution
                    for (int numIts : iterationsWithNoImprovement) {
                        threads.add(new VnsRandomConstructiveCsvGenerator(readProblemSpecificationFromSamples(), numberTests, numIts));
                    }
                break;
			case 5: // TABU
				break;
			case 6: // LNS
                // largeNeighborhoodSearchMetrics(problemSpecifications, numberOfIterations);
				break;
		}
        
        // Start the threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for the threads to stop
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println("Error joining threads in AlgorithmMetrics: " + e.getMessage());
                e.printStackTrace();
            }
        }
	}
    


    //	public static void graspMetrics(CVRPSpecification[] problemSpecifications, int numTests) throws IOException {
    //		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("grasp_results.csv")), true);
    //		final int MAX_NUM_ITERATIONS = 1000000;
    //
    //		int restrictedCandidateListNumbers[] = { 3, 5 };
    //		int iterationsWithNoImprovement[] = { 10, 50, 100 };
    //		LocalSearch localSearches[] = { new BestNeighborLocalSearch(new Relocation()),
    //				new BestNeighborLocalSearch(new InterrouteSwap()), new BestNeighborLocalSearch(new IntrarouteSwap()),
    //				new BestNeighborLocalSearch(new TwoOpt()), new FirstBetterNeighborLocalSearch(new Relocation()),
    //				new FirstBetterNeighborLocalSearch(new InterrouteSwap()),
    //				new FirstBetterNeighborLocalSearch(new IntrarouteSwap()), new FirstBetterNeighborLocalSearch(new TwoOpt()), };
    //		String localSearchesnames[] = { "BN + Relocation", "BN + Interroute", "BN + IntrarouteSwap", "BN + TwoOpt",
    //				"FBN + Relocation", "FBN + InterrouteSwap", "FBN + IntrarouteSwap", "FBN + TwoOpt", };
    //
    //		writer.append(TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
    //				+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
    //				+ TimeAndIterationsRecorder.CSV_SEPARATOR);
    //		for (int i = 0; i < NUM_SAMPLES; ++i) {
    //			writer.append(sampleNames[i].split("\\.")[0] + TimeAndIterationsRecorder.CSV_SEPARATOR
    //					+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
    //					+ TimeAndIterationsRecorder.CSV_SEPARATOR);
    //		}
    //		writer.append("ALGORITHM" + TimeAndIterationsRecorder.CSV_SEPARATOR + "R.C.L"
    //				+ TimeAndIterationsRecorder.CSV_SEPARATOR + "I.W.I" + TimeAndIterationsRecorder.CSV_SEPARATOR + "L.S"
    //				+ TimeAndIterationsRecorder.CSV_SEPARATOR + "ITERATION" + TimeAndIterationsRecorder.CSV_SEPARATOR);
    //		for (int i = 0; i < NUM_SAMPLES; ++i) {
    //			writer.append("I.W.F" + TimeAndIterationsRecorder.CSV_SEPARATOR + "T.N.O.I"
    //					+ TimeAndIterationsRecorder.CSV_SEPARATOR + "E.T.F" + TimeAndIterationsRecorder.CSV_SEPARATOR + "T.E.T"
    //					+ TimeAndIterationsRecorder.CSV_SEPARATOR + "SOL." + TimeAndIterationsRecorder.CSV_SEPARATOR);
    //		}
    //
    //		for (int rcl : restrictedCandidateListNumbers) {
    //			for (int numIterations : iterationsWithNoImprovement) {
    //				for (int localSearchPos = 0; localSearchPos < localSearches.length; ++localSearchPos) {
    //					for (int i = 1; i <= numTests; ++i) {
    //						writer.append(
    //								"GRASP" + TimeAndIterationsRecorder.CSV_SEPARATOR + rcl + TimeAndIterationsRecorder.CSV_SEPARATOR
    //										+ numIterations + TimeAndIterationsRecorder.CSV_SEPARATOR + localSearchesnames[localSearchPos]
    //										+ TimeAndIterationsRecorder.CSV_SEPARATOR + i + TimeAndIterationsRecorder.CSV_SEPARATOR);
    //						for (CVRPSpecification problemSpecification : problemSpecifications) {
    //							TimeAndIterationsRecorder algorithmRecorder = new TimeAndIterationsRecorder();
    //							GRASP.grasp(problemSpecification, MAX_NUM_ITERATIONS, numIterations, rcl, localSearches[localSearchPos],
    //									algorithmRecorder);
    //							writer.append(algorithmRecorder.toString() + TimeAndIterationsRecorder.CSV_SEPARATOR);
    //							// System.out.print(algorithmRecorder.toString() +
    //							// TimeAndIterationsRecorder.CSV_SEPARATOR);
    //						}
    //						writer.append("\n");
    //						writer.flush();
    //					}
    //				}
    //			}
    //		}
    //		writer.close();
    //	}
    
    //    public static void vnsConstructiveInitialSolutionMetrics(CVRPSpecification[] problemSpecifications, int numTests)
    //            throws IOException {
    //        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(OUTPUT_DIR + "vns_results.csv")), true);
    //        
    //        int restrictedCandidateListNumbers[] = { 3, 5 };
    //        int iterationsWithNoImprovement[] = { 10, 50, 100 };
    //        Move movesList[][] = { 
    //                { new InterrouteSwap(), new IntrarouteSwap(), new TwoOpt() },
    //                { new IntrarouteSwap(), new InterrouteSwap(), new Relocation(), new TwoOpt() },
    //                { new InterrouteSwap(), new Relocation(), new IntrarouteSwap(), new TwoOpt() },
    //        };
    //        String movesNames[] = {
    //                "Interroute + Intraroute + TwoOpt",
    //                "Intraroute + Interroute + Relocation + TwoOpt",
    //                "Interroute + Relocation + Intraroute + TwoOpt",
    //        };
    //        LocalSearch localSearches[] = { 
    //                new BestNeighborLocalSearch(new Relocation()),
    //                new BestNeighborLocalSearch(new InterrouteSwap()), 
    //                new BestNeighborLocalSearch(new IntrarouteSwap()),
    //                new BestNeighborLocalSearch(new TwoOpt()), 
    //                new FirstBetterNeighborLocalSearch(new Relocation()),
    //                new FirstBetterNeighborLocalSearch(new InterrouteSwap()),
    //                new FirstBetterNeighborLocalSearch(new IntrarouteSwap()),
    //                new FirstBetterNeighborLocalSearch(new TwoOpt()), 
    //                new VariableNeighborhoodDescent(movesList[0]),
    //                new VariableNeighborhoodDescent(movesList[1]),
    //                new VariableNeighborhoodDescent(movesList[2]),
    //        };
    //        String localSearchesNames[] = {
    //                "BN + Relocation", 
    //                "BN + Interroute", 
    //                "BN + IntrarouteSwap", 
    //                "BN + TwoOpt",
    //                "FBN + Relocation", 
    //                "FBN + InterrouteSwap", 
    //                "FBN + IntrarouteSwap", 
    //                "FBN + TwoOpt",
    //                "VND + " + movesNames[0],
    //                "VND + " + movesNames[1],
    //                "VND + " + movesNames[2],
    //        };
    //        
    //        writer.append(TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
    //                + TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
    //                + TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR);
    //        for (int i = 0; i < NUM_SAMPLES; ++i) {
    //            writer.append(sampleNames[i].split("\\.")[0] + TimeAndIterationsRecorder.CSV_SEPARATOR
    //                    + TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
    //                    + TimeAndIterationsRecorder.CSV_SEPARATOR);
    //        }
    //        writer.append("ALGORITHM" + TimeAndIterationsRecorder.CSV_SEPARATOR 
    //                + "R.C.L" + TimeAndIterationsRecorder.CSV_SEPARATOR 
    //                + "I.W.I" + TimeAndIterationsRecorder.CSV_SEPARATOR 
    //                + "L.S" + TimeAndIterationsRecorder.CSV_SEPARATOR 
    //                + "M" + TimeAndIterationsRecorder.CSV_SEPARATOR 
    //                + "ITERATION" + TimeAndIterationsRecorder.CSV_SEPARATOR);
    //        
    //        for (int i = 0; i < NUM_SAMPLES; ++i) {
    //            writer.append("I.W.F" + TimeAndIterationsRecorder.CSV_SEPARATOR 
    //                    + "T.N.O.I" + TimeAndIterationsRecorder.CSV_SEPARATOR 
    //                    + "E.T.F" + TimeAndIterationsRecorder.CSV_SEPARATOR
    //                    + "T.E.T" + TimeAndIterationsRecorder.CSV_SEPARATOR 
    //                    + "SOL." + TimeAndIterationsRecorder.CSV_SEPARATOR);
    //        }
    //        
    //        for (int rcl : restrictedCandidateListNumbers) {
    //            for (int numIterations : iterationsWithNoImprovement) {
    //                for (int localSearchPos = 0; localSearchPos < localSearches.length; ++localSearchPos) {
    //                    for (int movePos = 0; movePos < movesList.length; ++movePos) {
    //                        for (int i = 1; i <= numTests; ++i) {
    //                            writer.append("Constructive initial VNS" + TimeAndIterationsRecorder.CSV_SEPARATOR
    //                                    + rcl + TimeAndIterationsRecorder.CSV_SEPARATOR 
    //                                    + numIterations + TimeAndIterationsRecorder.CSV_SEPARATOR 
    //                                    + localSearchesNames[localSearchPos] + TimeAndIterationsRecorder.CSV_SEPARATOR 
    //                                    + movesNames[movePos] + TimeAndIterationsRecorder.CSV_SEPARATOR 
    //                                    + i + TimeAndIterationsRecorder.CSV_SEPARATOR);
    //                            System.out.print("Constructive initial VNS" + TimeAndIterationsRecorder.CSV_SEPARATOR 
    //                                    + rcl + TimeAndIterationsRecorder.CSV_SEPARATOR 
    //                                    + numIterations + TimeAndIterationsRecorder.CSV_SEPARATOR 
    //                                    + localSearchesNames[localSearchPos] + TimeAndIterationsRecorder.CSV_SEPARATOR 
    //                                    + movesNames[movePos] + TimeAndIterationsRecorder.CSV_SEPARATOR 
    //                                    + i + TimeAndIterationsRecorder.CSV_SEPARATOR);
    //                            for (CVRPSpecification problemSpecification : problemSpecifications) {
    //                                TimeAndIterationsRecorder algorithmRecorder = new TimeAndIterationsRecorder();
    //                                CVRPSolution initialSolution = GRASP.constructGreedyRandomizedSolution(problemSpecification, rcl);
    //                                VariableNeighborhoodSearch.run(initialSolution, movesList[movePos], localSearches[localSearchPos], numIterations, algorithmRecorder);
    //                                writer.append(algorithmRecorder.toString() + TimeAndIterationsRecorder.CSV_SEPARATOR);
    //                                System.out.print(algorithmRecorder.toString() + TimeAndIterationsRecorder.CSV_SEPARATOR);
    //                            }
    //                            writer.append("\n");
    //                            writer.flush();
    //                            System.out.println();
    //                        }
    //                    }
    //                }
    //            }
    //        }
    //        writer.close();
    //    }
    //
    //	public static void multibootMetrics(CVRPSpecification[] problemSpecifications, int numTests) throws IOException {
    //		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(OUTPUT_DIR + "multiboot_results.csv")),
    //				true);
    //		final int NUM_SAMPLES = 6;
    //
    //		int iterationsWithNoImprovement[] = { 10, 50, 100 };
    //		LocalSearch localSearches[] = { new BestNeighborLocalSearch(new Relocation()),
    //				new BestNeighborLocalSearch(new InterrouteSwap()), new BestNeighborLocalSearch(new IntrarouteSwap()),
    //				new BestNeighborLocalSearch(new TwoOpt()), new FirstBetterNeighborLocalSearch(new Relocation()),
    //				new FirstBetterNeighborLocalSearch(new InterrouteSwap()),
    //				new FirstBetterNeighborLocalSearch(new IntrarouteSwap()), new FirstBetterNeighborLocalSearch(new TwoOpt()), };
    //		String localSearchesnames[] = { "BN + Relocation", "BN + Interroute", "BN + IntrarouteSwap", "BN + TwoOpt",
    //				"FBN + Relocation", "FBN + InterrouteSwap", "FBN + IntrarouteSwap", "FBN + TwoOpt", };
    //
    //		writer.append(TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
    //				+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
    //				+ TimeAndIterationsRecorder.CSV_SEPARATOR);
    //		for (int i = 0; i < NUM_SAMPLES; ++i) {
    //			writer.append(sampleNames[i].split("\\.")[0] + TimeAndIterationsRecorder.CSV_SEPARATOR
    //					+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
    //					+ TimeAndIterationsRecorder.CSV_SEPARATOR);
    //		}
    //		writer.append("ALGORITHM" + TimeAndIterationsRecorder.CSV_SEPARATOR + "I.W.I"
    //				+ TimeAndIterationsRecorder.CSV_SEPARATOR + "ITERATION" + TimeAndIterationsRecorder.CSV_SEPARATOR);
    //		// COMMON
    //		for (int i = 0; i < NUM_SAMPLES; ++i) {
    //			writer.append("I.W.F" + TimeAndIterationsRecorder.CSV_SEPARATOR + "T.N.O.I"
    //					+ TimeAndIterationsRecorder.CSV_SEPARATOR + "E.T.F" + TimeAndIterationsRecorder.CSV_SEPARATOR + "T.E.T"
    //					+ TimeAndIterationsRecorder.CSV_SEPARATOR + "SOL." + TimeAndIterationsRecorder.CSV_SEPARATOR);
    //		}
    //
    //		for (int numIterations : iterationsWithNoImprovement) {
    //			for (int localSearchPos = 0; localSearchPos < localSearches.length; ++localSearchPos) {
    //				for (int i = 1; i <= numTests; ++i) {
    //					writer.append("MULTIBOOT" + TimeAndIterationsRecorder.CSV_SEPARATOR + numIterations
    //							+ TimeAndIterationsRecorder.CSV_SEPARATOR + localSearchesnames[localSearchPos]
    //							+ TimeAndIterationsRecorder.CSV_SEPARATOR + i + TimeAndIterationsRecorder.CSV_SEPARATOR);
    //					for (CVRPSpecification problemSpecification : problemSpecifications) {
    //						TimeAndIterationsRecorder algorithmRecorder = new TimeAndIterationsRecorder();
    //						Multiboot.multiboot(problemSpecification, localSearches[localSearchPos], 100, algorithmRecorder);
    //						writer.append(algorithmRecorder.toString() + TimeAndIterationsRecorder.CSV_SEPARATOR);
    //						// System.out.print(algorithmRecorder.toString() +
    //						// TimeAndIterationsRecorder.CSV_SEPARATOR);
    //					}
    //					writer.append("\n");
    //					writer.flush();
    //				}
    //			}
    //		}
    //		writer.close();
    //	}
    //	
    //	public static void largeNeighborhoodSearchMetrics(CVRPSpecification[] problemSpecifications, int numTests) throws IOException {
    //		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(OUTPUT_DIR + "multiboot_results.csv")),
    //				true);
    //		final int NUM_SAMPLES = 6;
    //
    //		int destroyPercentages[] = { 20, 25, 30, 40 };
    //		LocalSearch localSearches[] = { new BestNeighborLocalSearch(new Relocation()),
    //				new BestNeighborLocalSearch(new InterrouteSwap()), new BestNeighborLocalSearch(new IntrarouteSwap()),
    //				new BestNeighborLocalSearch(new TwoOpt()), new FirstBetterNeighborLocalSearch(new Relocation()),
    //				new FirstBetterNeighborLocalSearch(new InterrouteSwap()),
    //				new FirstBetterNeighborLocalSearch(new IntrarouteSwap()), new FirstBetterNeighborLocalSearch(new TwoOpt()), };
    //		String localSearchesnames[] = { "BN + Relocation", "BN + Interroute", "BN + IntrarouteSwap", "BN + TwoOpt",
    //				"FBN + Relocation", "FBN + InterrouteSwap", "FBN + IntrarouteSwap", "FBN + TwoOpt", };
    //
    //		writer.append(TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
    //				+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
    //				+ TimeAndIterationsRecorder.CSV_SEPARATOR);
    //		for (int i = 0; i < NUM_SAMPLES; ++i) {
    //			writer.append(sampleNames[i].split("\\.")[0] + TimeAndIterationsRecorder.CSV_SEPARATOR
    //					+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
    //					+ TimeAndIterationsRecorder.CSV_SEPARATOR);
    //		}
    //		writer.append("ALGORITHM" + TimeAndIterationsRecorder.CSV_SEPARATOR + "D.P"
    //				+ TimeAndIterationsRecorder.CSV_SEPARATOR + "ITERATION" + TimeAndIterationsRecorder.CSV_SEPARATOR);
    //		// COMMON
    //		for (int i = 0; i < NUM_SAMPLES; ++i) {
    //			writer.append("I.W.F" + TimeAndIterationsRecorder.CSV_SEPARATOR + "T.N.O.I"
    //					+ TimeAndIterationsRecorder.CSV_SEPARATOR + "E.T.F" + TimeAndIterationsRecorder.CSV_SEPARATOR + "T.E.T"
    //					+ TimeAndIterationsRecorder.CSV_SEPARATOR + "SOL." + TimeAndIterationsRecorder.CSV_SEPARATOR);
    //		}
    //
    //		for (int destroyPercentage : destroyPercentages) {
    //			for (int localSearchPos = 0; localSearchPos < localSearches.length; ++localSearchPos) {
    //				for (int i = 1; i <= numTests; ++i) {
    //					writer.append("LNS" + TimeAndIterationsRecorder.CSV_SEPARATOR + destroyPercentage
    //							+ TimeAndIterationsRecorder.CSV_SEPARATOR + localSearchesnames[localSearchPos]
    //							+ TimeAndIterationsRecorder.CSV_SEPARATOR + i + TimeAndIterationsRecorder.CSV_SEPARATOR);
    //					for (CVRPSpecification problemSpecification : problemSpecifications) {
    //						TimeAndIterationsRecorder algorithmRecorder = new TimeAndIterationsRecorder();
    //						
    //						// TODO -> Execute LNS with different base solutions...
    //						
    //						// System.out.print(algorithmRecorder.toString() +
    //						// TimeAndIterationsRecorder.CSV_SEPARATOR);
    //					}
    //					writer.append("\n");
    //					writer.flush();
    //				}
    //			}
    //		}
    //		writer.close();
    //	}

}