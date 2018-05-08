package daa.project.crvp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import daa.project.crvp.IO.ReaderFromFile;
import daa.project.crvp.csv_generators.GraspCsvGenerator;
import daa.project.crvp.csv_generators.LNSConstructiveCsvGenerator;
import daa.project.crvp.csv_generators.MultibootCsvGenerator;
import daa.project.crvp.csv_generators.TabuConstructiveCsvGenerator;
import daa.project.crvp.csv_generators.VnsGraspConstructiveCsvGenerator;
import daa.project.crvp.csv_generators.VnsGraspImprovedCsvGenerator;
import daa.project.crvp.local_search.BestNeighborLocalSearch;
import daa.project.crvp.local_search.LocalSearch;
import daa.project.crvp.moves.InterrouteSwap;
import daa.project.crvp.moves.IntrarouteSwap;
import daa.project.crvp.moves.Move;
import daa.project.crvp.moves.Relocation;
import daa.project.crvp.moves.TwoOpt;
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
		int iterationsWithNoImprovement[] = { 10, 50 };
		int numberTests = 5;

		int tabuTenurePercentages[] = { 15, 20, 25 };
		int timesToImprove[] = { 500, 1000, 2000 };

		final String shakingMoveNames[] = { "Intraroute + TwoOpt", "Intraroute + Interroute + Relocation + TwoOpt",
				"Interroute + Relocation + Intraroute + TwoOpt", };

		int algorithmOption = 4;

		switch (algorithmOption) {
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
						Move shakingMoveList[][] = { { new IntrarouteSwap(), new TwoOpt() },
								{ new IntrarouteSwap(), new InterrouteSwap(), new Relocation(), new TwoOpt() },
								{ new InterrouteSwap(), new Relocation(), new IntrarouteSwap(), new TwoOpt() } };

						for (int movesPos = 0; movesPos < shakingMoveList.length; ++movesPos) {
							threads.add(new VnsGraspConstructiveCsvGenerator(readProblemSpecificationFromSamples(), numberTests,
									rclSize, numIts, shakingMoveList[movesPos], shakingMoveNames[movesPos]));
						}
					}
				}
				break;
			case 3: // VNS with GRASP optim initial solution
				Move shakingMoveList[][] = { { new IntrarouteSwap(), new TwoOpt() },
						{ new IntrarouteSwap(), new InterrouteSwap(), new Relocation(), new TwoOpt() },
						{ new InterrouteSwap(), new Relocation(), new IntrarouteSwap(), new TwoOpt() } };

				for (int movesPos = 0; movesPos < shakingMoveList.length; ++movesPos) {
					LocalSearch bestLocalSearch = new BestNeighborLocalSearch(new Relocation());
					int bestRclSize = 3;
					int bestIterationsWithNoImprovement = 10;

					threads
							.add(new VnsGraspImprovedCsvGenerator(readProblemSpecificationFromSamples(), numberTests, bestLocalSearch,
									bestRclSize, bestIterationsWithNoImprovement, shakingMoveList[movesPos], shakingMoveNames[movesPos]));
				}

				break;
			case 4: // TABU with GRASP optim initial solution
				for (int timeToImprove : timesToImprove) {
					for (int tabuTenurePercentage : tabuTenurePercentages) {
						LocalSearch bestLocalSearch = new BestNeighborLocalSearch(new Relocation());
						int bestRclSize = 3;
						int bestIterationsWithNoImprovement = 10;

						threads.add(new TabuConstructiveCsvGenerator(readProblemSpecificationFromSamples(), numberTests,
								bestLocalSearch, bestRclSize, bestIterationsWithNoImprovement, tabuTenurePercentage, timeToImprove));
					}
				}
				break;
			case 6: // LNS
                int graspBestRclSize = 5;
                int graspBestNumItsNoImprovement = 10;
				int minDiffLocalSearch = 5;
				double destructionPercentage[] = {0.25, 0.30};
				
                for (int k = 0; k < destructionPercentage.length; k++) {
					threads.add(new LNSConstructiveCsvGenerator(readProblemSpecificationFromSamples(),
					        numberTests,
					        new BestNeighborLocalSearch(new Relocation()),
                            "BN + Relocation",
                            graspBestRclSize,
                            graspBestNumItsNoImprovement,
							100,
                            minDiffLocalSearch,
                            destructionPercentage[k])
			        );
                }
				break;
		}

		System.out.println("Number of threads: " + threads.size());

		// Start the threads
		for (Thread thread : threads) {
			thread.start();
		}

		// Wait for the threads to stop
		for (Thread thread : threads) {
			try {
				thread.join();
			}
			catch (InterruptedException e) {
				System.err.println("Error joining threads in AlgorithmMetrics: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}