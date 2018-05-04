package daa.project.crvp.algorithms.test;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import daa.project.crvp.IO.ReaderFromFile;
import daa.project.crvp.algorithms.GRASP;
import daa.project.crvp.local_seach.FirstBetterNeighborLocalSearch;
import daa.project.crvp.local_search.BestNeighborLocalSearch;
import daa.project.crvp.local_search.LocalSearch;
import daa.project.crvp.metrics.TimeAndIterationsRecorder;
import daa.project.crvp.moves.InterrouteSwap;
import daa.project.crvp.moves.IntrarouteSwap;
import daa.project.crvp.moves.Move;
import daa.project.crvp.problem.CVRPSolution;
import daa.project.crvp.problem.CVRPSpecification;

public class GRASPTest {

	@Test(timeout = 100000)
	public void testGRASPSolution() throws FileNotFoundException, IOException {
		ReaderFromFile reader = new ReaderFromFile("input/test.vrp");
		CVRPSpecification problemSpecification = reader.getProblemSpecification();
		// TODO Falta el movimiento de reinserción.
		Move moves[] = { new IntrarouteSwap(), new InterrouteSwap() };

		LocalSearch localSearchs[] = { new BestNeighborLocalSearch(moves[0]),
				new BestNeighborLocalSearch(moves[1]),
				new FirstBetterNeighborLocalSearch(moves[0]),
				new FirstBetterNeighborLocalSearch(moves[1]) };

		final int GRASP_ITERATIONS = 1000;
		final int GRASP_MAX_ITERATIONS_WITHOUT_IMPROVE = 50;
		final int GRASP_MAX_RCL_SIZE = 8;
		CVRPSolution solution = null;
		for (LocalSearch localSearch : localSearchs) {
			for (int i = 2; i <= GRASP_MAX_RCL_SIZE; ++i) {
				solution = GRASP.grasp(problemSpecification, GRASP_ITERATIONS,
						GRASP_MAX_ITERATIONS_WITHOUT_IMPROVE, GRASP_MAX_RCL_SIZE,
                        localSearch, new TimeAndIterationsRecorder());
				}
				assertTrue(solution.isFeasible());
			}
		}
}
