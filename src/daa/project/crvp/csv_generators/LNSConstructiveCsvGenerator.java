package daa.project.crvp.csv_generators;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import daa.project.crvp.AlgorithmMetrics;
import daa.project.crvp.algorithms.GRASP;
import daa.project.crvp.algorithms.LargeNeighborhoodSearch;
import daa.project.crvp.algorithms.VariableNeighborhoodSearch;
import daa.project.crvp.local_seach.FirstBetterNeighborLocalSearch;
import daa.project.crvp.local_search.BestNeighborLocalSearch;
import daa.project.crvp.local_search.LocalSearch;
import daa.project.crvp.local_search.VariableNeighborhoodDescent;
import daa.project.crvp.metrics.TimeAndIterationsRecorder;
import daa.project.crvp.moves.InterrouteSwap;
import daa.project.crvp.moves.IntrarouteSwap;
import daa.project.crvp.moves.Move;
import daa.project.crvp.moves.Relocation;
import daa.project.crvp.moves.TwoOpt;
import daa.project.crvp.problem.CVRPSolution;
import daa.project.crvp.problem.CVRPSpecification;
import daa.project.crvp.utils.DoubleFormatter;

public class LNSConstructiveCsvGenerator extends Thread {
		private final int MAX_NUM_ITERATIONS = 1000;

		private final String FILE_PATH_PREFIX = AlgorithmMetrics.OUTPUT_DIR + "/lns";
		private final String FILE_PATH_SUFIX = ".csv";

		private LocalSearch LOCAL_SEARCHES[] = { new BestNeighborLocalSearch(new Relocation()),
				new BestNeighborLocalSearch(new InterrouteSwap()), new BestNeighborLocalSearch(new IntrarouteSwap()),
				new BestNeighborLocalSearch(new TwoOpt()), new FirstBetterNeighborLocalSearch(new Relocation()),
				new FirstBetterNeighborLocalSearch(new InterrouteSwap()),
				new FirstBetterNeighborLocalSearch(new IntrarouteSwap()), new FirstBetterNeighborLocalSearch(new TwoOpt()) };

		private final String LOCAL_SEARCHES_NAMES[] = { "BN + Relocation", "BN + Interroute", "BN + IntrarouteSwap",
				"BN + TwoOpt", "FBN + Relocation", "FBN + InterrouteSwap", "FBN + IntrarouteSwap", "FBN + TwoOpt" };

		private int graspRclSize;
		private int graspNumIterationsWithNoImprovement;
		private LocalSearch graspLocalSearch;
		private CVRPSpecification[] problemSpecifications;
		private int numTests;
		private String filePath;
		private int numIterations;
		private int maxReconstructions;
		private int minDiffLocalSearch;
		private double destructionPercentage;

		public LNSConstructiveCsvGenerator(CVRPSpecification[] problemSpecifications, int numTests, LocalSearch graspLocalSearch,
				int graspRclSize, int graspNumIterations, int maxReconstructions, int minDiffLocalSearch,
	           double destructionPercentage) {
			super();
			this.problemSpecifications = problemSpecifications;
			this.numTests = numTests;
			
			this.graspRclSize = graspRclSize;
			this.graspNumIterationsWithNoImprovement = numIterations;
			this.graspLocalSearch = graspLocalSearch;
			
			this.numIterations = numIterations;
			this.maxReconstructions = maxReconstructions;
			this.minDiffLocalSearch = minDiffLocalSearch;
			this.destructionPercentage = destructionPercentage;

			this.filePath = FILE_PATH_PREFIX + "_numIts_" + numIterations + "_max_reconstructions_" + maxReconstructions 
					+ "_min_diff_" + minDiffLocalSearch + "_destruction_" + destructionPercentage + FILE_PATH_SUFIX;
		}

		@Override
		public void run() {
			super.run();
			try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(this.filePath)), true)) {
				writer.append(getCsvHeader());

				for (int localSearchPos = 0; localSearchPos < LOCAL_SEARCHES.length; ++localSearchPos) {
					writer.append(
							"LNS GRASP CONSTRUCTIVE" + TimeAndIterationsRecorder.CSV_SEPARATOR
							+ this.graspRclSize + TimeAndIterationsRecorder.CSV_SEPARATOR
							+ this.graspNumIterationsWithNoImprovement + TimeAndIterationsRecorder.CSV_SEPARATOR
							+ "GraspLocalSearch" + TimeAndIterationsRecorder.CSV_SEPARATOR
							+ this.numIterations + TimeAndIterationsRecorder.CSV_SEPARATOR
							+ this.maxReconstructions + TimeAndIterationsRecorder.CSV_SEPARATOR
							+ this.minDiffLocalSearch + TimeAndIterationsRecorder.CSV_SEPARATOR
							+ DoubleFormatter.format(this.destructionPercentage) + TimeAndIterationsRecorder.CSV_SEPARATOR
							+ LOCAL_SEARCHES_NAMES[localSearchPos] + TimeAndIterationsRecorder.CSV_SEPARATOR
					);

					for (CVRPSpecification problemSpecification : problemSpecifications) {
						long timeSum = 0;
						long minTime = Long.MAX_VALUE;
						double sumObjectiveValues = 0;
						double minObjectiveValue = Double.MAX_VALUE;

						for (int i = 1; i <= numTests; ++i) {
							TimeAndIterationsRecorder algorithmRecorder = new TimeAndIterationsRecorder();
							CVRPSolution initialSolution = GRASP.grasp(problemSpecification, MAX_NUM_ITERATIONS, this.graspNumIterationsWithNoImprovement, this.graspRclSize, this.graspLocalSearch, algorithmRecorder);

							LargeNeighborhoodSearch.run(problemSpecification, initialSolution, LOCAL_SEARCHES[localSearchPos], maxReconstructions, minDiffLocalSearch, destructionPercentage, algorithmRecorder);

							timeSum += algorithmRecorder.getElapsedTime();
							sumObjectiveValues += algorithmRecorder.getSolutionsTotalDistance();
							minTime = Math.min(minTime, algorithmRecorder.getElapsedTime());
							minObjectiveValue = Math.min(minObjectiveValue, algorithmRecorder.getSolutionsTotalDistance());
						}

						timeSum /= numTests;
						sumObjectiveValues /= numTests;
						writer.append(DoubleFormatter.format(timeSum) + TimeAndIterationsRecorder.CSV_SEPARATOR
								+ DoubleFormatter.format(sumObjectiveValues) + TimeAndIterationsRecorder.CSV_SEPARATOR
								+ DoubleFormatter.format(minTime) + TimeAndIterationsRecorder.CSV_SEPARATOR
								+ DoubleFormatter.format(minObjectiveValue) + TimeAndIterationsRecorder.CSV_SEPARATOR);
					}
					System.out
							.println("LNS GRASP IMPROVED " + "RCL: " + this.graspRclSize + " I.W.I: " + this.graspNumIterationsWithNoImprovement
									+ "LS: " + LOCAL_SEARCHES_NAMES[localSearchPos] + " finished.");

					writer.println();
					writer.flush();
				}

				writer.close();
			}
			catch (

			Exception e) {
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace();
			}
		}

		private String getCsvHeader() {
			StringBuilder writer = new StringBuilder();

			writer.append(TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
					+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
					+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
					+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
					+ TimeAndIterationsRecorder.CSV_SEPARATOR);
			for (int i = 0; i < AlgorithmMetrics.NUM_SAMPLES; ++i) {
				writer.append(AlgorithmMetrics.sampleNames[i].split("\\.")[0] + TimeAndIterationsRecorder.CSV_SEPARATOR
						+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
						+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR);
			}
			writer.append("\n");

			writer.append("ALGORITHM" + TimeAndIterationsRecorder.CSV_SEPARATOR 
					+ "GRASP.R.C.L" + TimeAndIterationsRecorder.CSV_SEPARATOR
					+ "GRASP.I.W.I" + TimeAndIterationsRecorder.CSV_SEPARATOR
					+ "GRASP.LocalSearch" + TimeAndIterationsRecorder.CSV_SEPARATOR
					+ "Num.Iterations" + TimeAndIterationsRecorder.CSV_SEPARATOR
					+ "MaxReconstruction" + TimeAndIterationsRecorder.CSV_SEPARATOR
					+ "MinDiff" + TimeAndIterationsRecorder.CSV_SEPARATOR
					+ "DestructionPercent" + TimeAndIterationsRecorder.CSV_SEPARATOR
					+ "LocalSearch" + TimeAndIterationsRecorder.CSV_SEPARATOR
			);

			for (int i = 0; i < AlgorithmMetrics.NUM_SAMPLES; ++i) {
				writer.append("AvgTime" + TimeAndIterationsRecorder.CSV_SEPARATOR + "AvgSol"
						+ TimeAndIterationsRecorder.CSV_SEPARATOR + "MinTime" + TimeAndIterationsRecorder.CSV_SEPARATOR + "MinSol"
						+ TimeAndIterationsRecorder.CSV_SEPARATOR);
			}
			writer.append("\n");

			return writer.toString();
		}
	}

