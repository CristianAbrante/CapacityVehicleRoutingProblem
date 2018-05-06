package daa.project.crvp.csv_generators;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import daa.project.crvp.AlgorithmMetrics;
import daa.project.crvp.algorithms.Multiboot;
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

public class VnsRandomConstructiveCsvGenerator extends Thread {
    
    private final String FILE_PATH_PREFIX = AlgorithmMetrics.OUTPUT_DIR + "/vns_random_constructive_results";
    private final String FILE_PATH_SUFIX = ".csv";
    private final Move movesList[][] = {
            { new InterrouteSwap(), new IntrarouteSwap(), new TwoOpt() },
            { new IntrarouteSwap(), new InterrouteSwap(), new Relocation(), new TwoOpt() },
            { new InterrouteSwap(), new Relocation(), new IntrarouteSwap(), new TwoOpt() },
    };
    private final String movesNames[] = {
            "Interroute + Intraroute + TwoOpt",
            "Intraroute + Interroute + Relocation + TwoOpt",
            "Interroute + Relocation + Intraroute + TwoOpt",
    };
    private final LocalSearch LOCAL_SEARCHES[] = { 
            new BestNeighborLocalSearch(new Relocation()),
            new BestNeighborLocalSearch(new InterrouteSwap()),
            new BestNeighborLocalSearch(new IntrarouteSwap()), 
            new BestNeighborLocalSearch(new TwoOpt()),
            new FirstBetterNeighborLocalSearch(new Relocation()),
            new FirstBetterNeighborLocalSearch(new InterrouteSwap()),
            new FirstBetterNeighborLocalSearch(new IntrarouteSwap()),
            new FirstBetterNeighborLocalSearch(new TwoOpt()),
            new VariableNeighborhoodDescent(movesList[0]),
            new VariableNeighborhoodDescent(movesList[1]),
            new VariableNeighborhoodDescent(movesList[2]),
    };
    private final String LOCAL_SEARCHES_NAMES[] = { 
            "BN + Relocation", 
            "BN + Interroute", 
            "BN + IntrarouteSwap", 
            "BN + TwoOpt",
            "FBN + Relocation", 
            "FBN + InterrouteSwap", 
            "FBN + IntrarouteSwap", 
            "FBN + TwoOpt",
            "VND + " + movesNames[0],
            "VND + " + movesNames[1],
            "VND + " + movesNames[2],
    };
    private int numIterationsWithNoImprovement;
    private CVRPSpecification[] problemSpecifications;
    private int numTests;
    private String filePath;

    public VnsRandomConstructiveCsvGenerator(CVRPSpecification[] problemSpecifications, int numTests, int numIterationsWithNoImprovement) {
        super();
        this.problemSpecifications = problemSpecifications;
        this.numTests = numTests;
        this.numIterationsWithNoImprovement = numIterationsWithNoImprovement;
        this.filePath = FILE_PATH_PREFIX + "_numIts_" + numIterationsWithNoImprovement + FILE_PATH_SUFIX;
    }
    
    @Override
    public void run() {
        super.run();
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(this.filePath)), true)) {
            writer.append(getCsvHeader());
            
            for (int localSearchPos = 0; localSearchPos < LOCAL_SEARCHES.length; ++localSearchPos) {
                for (int movesPos = 0; movesPos < movesList.length; ++movesPos) {
                    for (int i = 1; i <= numTests; ++i) {
                        writer.append("VNS random constructive " + TimeAndIterationsRecorder.CSV_SEPARATOR
                                + this.numIterationsWithNoImprovement + TimeAndIterationsRecorder.CSV_SEPARATOR 
                                + LOCAL_SEARCHES_NAMES[localSearchPos] + TimeAndIterationsRecorder.CSV_SEPARATOR 
                                + movesNames[movesPos] + TimeAndIterationsRecorder.CSV_SEPARATOR 
                                + i + TimeAndIterationsRecorder.CSV_SEPARATOR
                                );
                        for (CVRPSpecification problemSpecification : problemSpecifications) {
                            CVRPSolution initialSolution = Multiboot.constructRandomSolution(problemSpecification);
                            TimeAndIterationsRecorder algorithmRecorder = new TimeAndIterationsRecorder();
                            VariableNeighborhoodSearch.run(initialSolution, movesList[movesPos], LOCAL_SEARCHES[localSearchPos], this.numIterationsWithNoImprovement, algorithmRecorder);
                            writer.append(algorithmRecorder.toString() + TimeAndIterationsRecorder.CSV_SEPARATOR);
                            System.out.println("VNS random constructive "
                                    + " Num iterations no improvement: " + this.numIterationsWithNoImprovement 
                                    + " " + LOCAL_SEARCHES_NAMES[localSearchPos] 
                                    + " " + movesNames[movesPos] + TimeAndIterationsRecorder.CSV_SEPARATOR 
                                    + " Test number: " + i
                                    + " Recorder info: " + algorithmRecorder.toString() + TimeAndIterationsRecorder.CSV_SEPARATOR
                            );
                        }
                        writer.append("\n");
                        writer.flush();
                    }
                }
            }
            
            writer.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String getCsvHeader() {
        StringBuilder writer = new StringBuilder();
        
        writer.append(TimeAndIterationsRecorder.CSV_SEPARATOR 
                + TimeAndIterationsRecorder.CSV_SEPARATOR 
                + TimeAndIterationsRecorder.CSV_SEPARATOR
                + TimeAndIterationsRecorder.CSV_SEPARATOR
                + TimeAndIterationsRecorder.CSV_SEPARATOR
        );
        for (int i = 0; i < AlgorithmMetrics.NUM_SAMPLES; ++i) {
            writer.append(AlgorithmMetrics.sampleNames[i].split("\\.")[0] 
                    + TimeAndIterationsRecorder.CSV_SEPARATOR
                    + TimeAndIterationsRecorder.CSV_SEPARATOR 
                    + TimeAndIterationsRecorder.CSV_SEPARATOR
                    + TimeAndIterationsRecorder.CSV_SEPARATOR
                    + TimeAndIterationsRecorder.CSV_SEPARATOR
            );
        }
        writer.append("\n");
        
        writer.append("ALGORITHM" + TimeAndIterationsRecorder.CSV_SEPARATOR 
                + "I.W.I" + TimeAndIterationsRecorder.CSV_SEPARATOR
                + "L.S" + TimeAndIterationsRecorder.CSV_SEPARATOR 
                + "MOVES" + TimeAndIterationsRecorder.CSV_SEPARATOR 
                + "ITERATION" + TimeAndIterationsRecorder.CSV_SEPARATOR
        );
        for (int i = 0; i < AlgorithmMetrics.NUM_SAMPLES; ++i) {
            writer.append("I.W.F" + TimeAndIterationsRecorder.CSV_SEPARATOR 
                    + "T.N.O.I" + TimeAndIterationsRecorder.CSV_SEPARATOR 
                    + "E.T.F" + TimeAndIterationsRecorder.CSV_SEPARATOR
                    + "T.E.T" + TimeAndIterationsRecorder.CSV_SEPARATOR 
                    + "SOL." + TimeAndIterationsRecorder.CSV_SEPARATOR
            );
        }
        writer.append("\n");
        
        return writer.toString();
    }

}
