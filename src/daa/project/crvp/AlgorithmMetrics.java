package daa.project.crvp;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import daa.project.crvp.IO.ReaderFromFile;
import daa.project.crvp.algorithms.GRASP;
import daa.project.crvp.algorithms.LargeNeighborhoodSearch;
import daa.project.crvp.algorithms.Multiboot;
import daa.project.crvp.local_seach.FirstBetterNeighborLocalSearch;
import daa.project.crvp.local_search.BestNeighborLocalSearch;
import daa.project.crvp.local_search.LocalSearch;
import daa.project.crvp.metrics.TimeAndIterationsRecorder;
import daa.project.crvp.moves.InterrouteSwap;
import daa.project.crvp.moves.IntrarouteSwap;
import daa.project.crvp.moves.Relocation;
import daa.project.crvp.moves.TwoOpt;
import daa.project.crvp.problem.CVRPSpecification;
import daa.project.crvp.utils.DoubleCompare;

/**
 * Class designed to measure the different algorithms of the VRP and to export
 * the results in csv format.
 */
public class AlgorithmMetrics {
	private static final String OUTPUT_DIR = "output/algorithms/";
	private static final String INPUT_DIR = "input/samples/";
	private static final String sampleNames[] = { "S-N32-K5.vrp", "S-N43-K6.vrp", "S-N50-K7.vrp", "S-N57-K9.vrp",
			"S-N62-K8.vrp", "S-N80-K10.vrp" };
	private static final int NUM_SAMPLES = 6;

	private static CVRPSpecification[] readProblemSpecificationFromSamples() throws FileNotFoundException, IOException {
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
		// READ SAMPLES
		CVRPSpecification[] problemSpecifications = readProblemSpecificationFromSamples();

		int numberOfIterations = 10;
		int algorithmOption = 0;

		switch (algorithmOption)
		{
			case 0: // GRASP
				graspMetrics(problemSpecifications, numberOfIterations);
				break;
			case 1: // MULTIBOOT
				multibootMetrics(problemSpecifications, numberOfIterations);
				break;
			case 2: // VNS
				break;
			case 3: // TABU
				break;
			case 4: // LNS
				largeNeighborhoodSearchMetrics(problemSpecifications, numberOfIterations);
				break;
		}
	}

	public static void graspMetrics(CVRPSpecification[] problemSpecifications, int numTests) throws IOException {
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("grasp_results.csv")), true);
		final int MAX_NUM_ITERATIONS = 1000000;

		int restrictedCandidateListNumbers[] = { 3, 5 };
		int iterationsWithNoImprovement[] = { 10, 50, 100 };
		LocalSearch localSearches[] = { new BestNeighborLocalSearch(new Relocation()),
				new BestNeighborLocalSearch(new InterrouteSwap()), new BestNeighborLocalSearch(new IntrarouteSwap()),
				new BestNeighborLocalSearch(new TwoOpt()), new FirstBetterNeighborLocalSearch(new Relocation()),
				new FirstBetterNeighborLocalSearch(new InterrouteSwap()),
				new FirstBetterNeighborLocalSearch(new IntrarouteSwap()), new FirstBetterNeighborLocalSearch(new TwoOpt()), };
		String localSearchesnames[] = { "BN + Relocation", "BN + Interroute", "BN + IntrarouteSwap", "BN + TwoOpt",
				"FBN + Relocation", "FBN + InterrouteSwap", "FBN + IntrarouteSwap", "FBN + TwoOpt", };

		writer.append(TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
				+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
				+ TimeAndIterationsRecorder.CSV_SEPARATOR);
		for (int i = 0; i < NUM_SAMPLES; ++i) {
			writer.append(sampleNames[i].split("\\.")[0] + TimeAndIterationsRecorder.CSV_SEPARATOR
					+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
					+ TimeAndIterationsRecorder.CSV_SEPARATOR);
		}
		writer.append("ALGORITHM" + TimeAndIterationsRecorder.CSV_SEPARATOR + "R.C.L"
				+ TimeAndIterationsRecorder.CSV_SEPARATOR + "I.W.I" + TimeAndIterationsRecorder.CSV_SEPARATOR + "L.S"
				+ TimeAndIterationsRecorder.CSV_SEPARATOR + "ITERATION" + TimeAndIterationsRecorder.CSV_SEPARATOR);
		for (int i = 0; i < NUM_SAMPLES; ++i) {
			writer.append("I.W.F" + TimeAndIterationsRecorder.CSV_SEPARATOR + "T.N.O.I"
					+ TimeAndIterationsRecorder.CSV_SEPARATOR + "E.T.F" + TimeAndIterationsRecorder.CSV_SEPARATOR + "T.E.T"
					+ TimeAndIterationsRecorder.CSV_SEPARATOR + "SOL." + TimeAndIterationsRecorder.CSV_SEPARATOR);
		}

		for (int rcl : restrictedCandidateListNumbers) {
			for (int numIterations : iterationsWithNoImprovement) {
				for (int localSearchPos = 0; localSearchPos < localSearches.length; ++localSearchPos) {
					for (int i = 1; i <= numTests; ++i) {
						writer.append(
								"GRASP" + TimeAndIterationsRecorder.CSV_SEPARATOR + rcl + TimeAndIterationsRecorder.CSV_SEPARATOR
										+ numIterations + TimeAndIterationsRecorder.CSV_SEPARATOR + localSearchesnames[localSearchPos]
										+ TimeAndIterationsRecorder.CSV_SEPARATOR + i + TimeAndIterationsRecorder.CSV_SEPARATOR);
						for (CVRPSpecification problemSpecification : problemSpecifications) {
							TimeAndIterationsRecorder algorithmRecorder = new TimeAndIterationsRecorder();
							GRASP.grasp(problemSpecification, MAX_NUM_ITERATIONS, numIterations, rcl, localSearches[localSearchPos],
									algorithmRecorder);
							writer.append(algorithmRecorder.toString() + TimeAndIterationsRecorder.CSV_SEPARATOR);
							// System.out.print(algorithmRecorder.toString() +
							// TimeAndIterationsRecorder.CSV_SEPARATOR);
						}
						writer.append("\n");
						writer.flush();
					}
				}
			}
		}
		writer.close();
	}

	public static void multibootMetrics(CVRPSpecification[] problemSpecifications, int numTests) throws IOException {
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(OUTPUT_DIR + "multiboot_results.csv")),
				true);
		final int NUM_SAMPLES = 6;

		int iterationsWithNoImprovement[] = { 10, 50, 100 };
		LocalSearch localSearches[] = { new BestNeighborLocalSearch(new Relocation()),
				new BestNeighborLocalSearch(new InterrouteSwap()), new BestNeighborLocalSearch(new IntrarouteSwap()),
				new BestNeighborLocalSearch(new TwoOpt()), new FirstBetterNeighborLocalSearch(new Relocation()),
				new FirstBetterNeighborLocalSearch(new InterrouteSwap()),
				new FirstBetterNeighborLocalSearch(new IntrarouteSwap()), new FirstBetterNeighborLocalSearch(new TwoOpt()), };
		String localSearchesnames[] = { "BN + Relocation", "BN + Interroute", "BN + IntrarouteSwap", "BN + TwoOpt",
				"FBN + Relocation", "FBN + InterrouteSwap", "FBN + IntrarouteSwap", "FBN + TwoOpt", };

		writer.append(TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
				+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
				+ TimeAndIterationsRecorder.CSV_SEPARATOR);
		for (int i = 0; i < NUM_SAMPLES; ++i) {
			writer.append(sampleNames[i].split("\\.")[0] + TimeAndIterationsRecorder.CSV_SEPARATOR
					+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
					+ TimeAndIterationsRecorder.CSV_SEPARATOR);
		}
		writer.append("ALGORITHM" + TimeAndIterationsRecorder.CSV_SEPARATOR + "I.W.I"
				+ TimeAndIterationsRecorder.CSV_SEPARATOR + "ITERATION" + TimeAndIterationsRecorder.CSV_SEPARATOR);
		// COMMON
		for (int i = 0; i < NUM_SAMPLES; ++i) {
			writer.append("I.W.F" + TimeAndIterationsRecorder.CSV_SEPARATOR + "T.N.O.I"
					+ TimeAndIterationsRecorder.CSV_SEPARATOR + "E.T.F" + TimeAndIterationsRecorder.CSV_SEPARATOR + "T.E.T"
					+ TimeAndIterationsRecorder.CSV_SEPARATOR + "SOL." + TimeAndIterationsRecorder.CSV_SEPARATOR);
		}

		for (int numIterations : iterationsWithNoImprovement) {
			for (int localSearchPos = 0; localSearchPos < localSearches.length; ++localSearchPos) {
				for (int i = 1; i <= numTests; ++i) {
					writer.append("MULTIBOOT" + TimeAndIterationsRecorder.CSV_SEPARATOR + numIterations
							+ TimeAndIterationsRecorder.CSV_SEPARATOR + localSearchesnames[localSearchPos]
							+ TimeAndIterationsRecorder.CSV_SEPARATOR + i + TimeAndIterationsRecorder.CSV_SEPARATOR);
					for (CVRPSpecification problemSpecification : problemSpecifications) {
						TimeAndIterationsRecorder algorithmRecorder = new TimeAndIterationsRecorder();
						Multiboot.multiboot(problemSpecification, localSearches[localSearchPos], 100, algorithmRecorder);
						writer.append(algorithmRecorder.toString() + TimeAndIterationsRecorder.CSV_SEPARATOR);
						// System.out.print(algorithmRecorder.toString() +
						// TimeAndIterationsRecorder.CSV_SEPARATOR);
					}
					writer.append("\n");
					writer.flush();
				}
			}
		}
		writer.close();
	}
	
	public static void largeNeighborhoodSearchMetrics(CVRPSpecification[] problemSpecifications, int numTests) throws IOException {
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(OUTPUT_DIR + "multiboot_results.csv")),
				true);
		final int NUM_SAMPLES = 6;

		int destroyPercentages[] = { 20, 25, 30, 40 };
		LocalSearch localSearches[] = { new BestNeighborLocalSearch(new Relocation()),
				new BestNeighborLocalSearch(new InterrouteSwap()), new BestNeighborLocalSearch(new IntrarouteSwap()),
				new BestNeighborLocalSearch(new TwoOpt()), new FirstBetterNeighborLocalSearch(new Relocation()),
				new FirstBetterNeighborLocalSearch(new InterrouteSwap()),
				new FirstBetterNeighborLocalSearch(new IntrarouteSwap()), new FirstBetterNeighborLocalSearch(new TwoOpt()), };
		String localSearchesnames[] = { "BN + Relocation", "BN + Interroute", "BN + IntrarouteSwap", "BN + TwoOpt",
				"FBN + Relocation", "FBN + InterrouteSwap", "FBN + IntrarouteSwap", "FBN + TwoOpt", };

		writer.append(TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
				+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
				+ TimeAndIterationsRecorder.CSV_SEPARATOR);
		for (int i = 0; i < NUM_SAMPLES; ++i) {
			writer.append(sampleNames[i].split("\\.")[0] + TimeAndIterationsRecorder.CSV_SEPARATOR
					+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
					+ TimeAndIterationsRecorder.CSV_SEPARATOR);
		}
		writer.append("ALGORITHM" + TimeAndIterationsRecorder.CSV_SEPARATOR + "D.P"
				+ TimeAndIterationsRecorder.CSV_SEPARATOR + "ITERATION" + TimeAndIterationsRecorder.CSV_SEPARATOR);
		// COMMON
		for (int i = 0; i < NUM_SAMPLES; ++i) {
			writer.append("I.W.F" + TimeAndIterationsRecorder.CSV_SEPARATOR + "T.N.O.I"
					+ TimeAndIterationsRecorder.CSV_SEPARATOR + "E.T.F" + TimeAndIterationsRecorder.CSV_SEPARATOR + "T.E.T"
					+ TimeAndIterationsRecorder.CSV_SEPARATOR + "SOL." + TimeAndIterationsRecorder.CSV_SEPARATOR);
		}

		for (int destroyPercentage : destroyPercentages) {
			for (int localSearchPos = 0; localSearchPos < localSearches.length; ++localSearchPos) {
				for (int i = 1; i <= numTests; ++i) {
					writer.append("LNS" + TimeAndIterationsRecorder.CSV_SEPARATOR + destroyPercentage
							+ TimeAndIterationsRecorder.CSV_SEPARATOR + localSearchesnames[localSearchPos]
							+ TimeAndIterationsRecorder.CSV_SEPARATOR + i + TimeAndIterationsRecorder.CSV_SEPARATOR);
					for (CVRPSpecification problemSpecification : problemSpecifications) {
						TimeAndIterationsRecorder algorithmRecorder = new TimeAndIterationsRecorder();
						
						// TODO -> Execute LNS with different base solutions...
						
						// System.out.print(algorithmRecorder.toString() +
						// TimeAndIterationsRecorder.CSV_SEPARATOR);
					}
					writer.append("\n");
					writer.flush();
				}
			}
		}
		writer.close();
	}

}