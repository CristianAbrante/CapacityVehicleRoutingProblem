package daa.project.crvp.csv_generators;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import daa.project.crvp.AlgorithmMetrics;
import daa.project.crvp.algorithms.GRASP;
import daa.project.crvp.local_search.LocalSearch;
import daa.project.crvp.local_search.TabuSearch;
import daa.project.crvp.metrics.TimeAndIterationsRecorder;
import daa.project.crvp.moves.InterrouteSwap;
import daa.project.crvp.moves.IntrarouteSwap;
import daa.project.crvp.moves.Move;
import daa.project.crvp.moves.Relocation;
import daa.project.crvp.moves.TwoOpt;
import daa.project.crvp.problem.CVRPSolution;
import daa.project.crvp.problem.CVRPSpecification;
import daa.project.crvp.utils.DoubleCompare;
import daa.project.crvp.utils.DoubleFormatter;

public class TabuConstructiveCsvGenerator extends Thread {
	private final int MAX_NUM_ITERATIONS = 1000;

	private final String FILE_PATH_PREFIX = AlgorithmMetrics.OUTPUT_DIR + "/ts_improved_results";
	private final String FILE_PATH_SUFIX = ".csv";

	Move tabuMoves[][] = { { new IntrarouteSwap(), new TwoOpt() },
			{ new IntrarouteSwap(), new InterrouteSwap(), new Relocation(), new TwoOpt() },
			{ new InterrouteSwap(), new Relocation(), new IntrarouteSwap(), new TwoOpt() } };

	final String tabuMoveNames[] = { "Intraroute + TwoOpt", "Intraroute + Interroute + Relocation + TwoOpt",
			"Interroute + Relocation + Intraroute + TwoOpt", };

	private CVRPSpecification[] problemSpecifications;
	private int numTests;

	private String filePath;
	private int tabuTenurePercentage;
	private int rclSize;
	private int timeToImprove;

	private LocalSearch graspLocalSearch;

	private int numIterationsWithNoImprovement;

	/**
	 * @param timeToImprove2
	 * @param readProblemSpecificationFromSamples
	 * @param numberTests
	 * @param tabuTenurePercentage2
	 * @param timeToImprove2
	 * @param moves
	 * @param string
	 */
	public TabuConstructiveCsvGenerator(CVRPSpecification[] problemSpecifications, int numTests, LocalSearch localSearch,
			int rclSize, int numIterationsWithNoImprovement, int tabuTenurePercentage, int timeToImprove) {
		super();
		this.numTests = numTests;
		this.graspLocalSearch = localSearch;
		this.rclSize = rclSize;
		this.numIterationsWithNoImprovement = numIterationsWithNoImprovement;

		this.tabuTenurePercentage = tabuTenurePercentage;
		this.timeToImprove = timeToImprove;

		this.problemSpecifications = problemSpecifications;
		this.filePath = FILE_PATH_PREFIX + "_tt_" + tabuTenurePercentage + "_mti_" + timeToImprove + FILE_PATH_SUFIX;
	}

	@Override
	public void run() {
		super.run();
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(this.filePath)), true)) {
			writer.append(getCsvHeader());

			for (int movesPos = 0; movesPos < tabuMoves.length; ++movesPos) {
				writer.append("TABU GRASP IMPROVED" + TimeAndIterationsRecorder.CSV_SEPARATOR + this.tabuTenurePercentage
						+ TimeAndIterationsRecorder.CSV_SEPARATOR + this.timeToImprove + TimeAndIterationsRecorder.CSV_SEPARATOR
						+ tabuMoveNames[movesPos] + TimeAndIterationsRecorder.CSV_SEPARATOR);

				for (CVRPSpecification problemSpecification : problemSpecifications) {
					long timeSum = 0;
					long minTime = Long.MAX_VALUE;
					double sumObjectiveValues = 0;
					double minObjectiveValue = Double.MAX_VALUE;

					for (int i = 1; i <= numTests; ++i) {
						TimeAndIterationsRecorder algorithmRecorder = new TimeAndIterationsRecorder();
						CVRPSolution currentSolution = GRASP.grasp(problemSpecification, MAX_NUM_ITERATIONS,
								this.numIterationsWithNoImprovement, this.rclSize, this.graspLocalSearch, algorithmRecorder);

						CVRPSolution lastSolution = currentSolution;
						LocalSearch tabuSearch = new TabuSearch(tabuMoves[movesPos], this.tabuTenurePercentage, MAX_NUM_ITERATIONS,
								false, algorithmRecorder);

						algorithmRecorder.starting();
						algorithmRecorder.foundBetterSolution(currentSolution);
						while (algorithmRecorder.getCurrentTime() < this.timeToImprove) {
							algorithmRecorder.aboutToDoNextIteration();
							currentSolution = tabuSearch.findLocalOptimum(currentSolution);

							if (DoubleCompare.lessThan(currentSolution.getTotalDistance(), lastSolution.getTotalDistance())) {
								lastSolution = currentSolution;
								algorithmRecorder.foundBetterSolution(currentSolution);
							}
						}
						algorithmRecorder.finishing();

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
					writer.flush();
				}
				writer.append("\n");
				System.out.println("Tabu Tenure: " + tabuTenurePercentage + " Time to Improve: " + timeToImprove + " Move "
						+ tabuMoveNames[movesPos] + " finished.");
			}
			writer.close();
		}
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private String getCsvHeader() {
		StringBuilder writer = new StringBuilder();

		writer.append(TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
				+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
				+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR);
		for (int i = 0; i < AlgorithmMetrics.NUM_SAMPLES; ++i) {
			writer.append(AlgorithmMetrics.sampleNames[i].split("\\.")[0] + TimeAndIterationsRecorder.CSV_SEPARATOR
					+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR
					+ TimeAndIterationsRecorder.CSV_SEPARATOR + TimeAndIterationsRecorder.CSV_SEPARATOR);
		}
		writer.append("\n");

		writer.append(
				"ALGORITHM" + TimeAndIterationsRecorder.CSV_SEPARATOR + "T.T.P" + TimeAndIterationsRecorder.CSV_SEPARATOR
						+ "M.T.I" + TimeAndIterationsRecorder.CSV_SEPARATOR + "MOVES" + TimeAndIterationsRecorder.CSV_SEPARATOR);

		for (int i = 0; i < AlgorithmMetrics.NUM_SAMPLES; ++i) {
			writer.append("AvgTime" + TimeAndIterationsRecorder.CSV_SEPARATOR + "AvgSol"
					+ TimeAndIterationsRecorder.CSV_SEPARATOR + "MinTime" + TimeAndIterationsRecorder.CSV_SEPARATOR + "MinSol"
					+ TimeAndIterationsRecorder.CSV_SEPARATOR);
		}
		writer.append("\n");

		return writer.toString();
	}
}
