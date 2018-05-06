package daa.project.crvp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

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

public class MultibootMetrics extends Thread {
    
    private final String FILE_PATH_PREFIX = AlgorithmMetrics.OUTPUT_DIR + "/multiboot_results";
    private final String FILE_PATH_SUFIX = ".csv";
    private final LocalSearch LOCAL_SEARCHES[] = { 
            new BestNeighborLocalSearch(new Relocation()),
            new BestNeighborLocalSearch(new InterrouteSwap()),
            new BestNeighborLocalSearch(new IntrarouteSwap()),
            new BestNeighborLocalSearch(new TwoOpt()),
            new FirstBetterNeighborLocalSearch(new Relocation()),
            new FirstBetterNeighborLocalSearch(new InterrouteSwap()),
            new FirstBetterNeighborLocalSearch(new IntrarouteSwap()),
            new FirstBetterNeighborLocalSearch(new TwoOpt()), 
    };
    private final String LOCAL_SEARCHES_NAMES[] = { 
            "BN + Relocation", "BN + Interroute",
            "BN + IntrarouteSwap", 
            "BN + TwoOpt",
            "FBN + Relocation", "FBN + InterrouteSwap", 
            "FBN + IntrarouteSwap", 
            "FBN + TwoOpt", 
    };
    
    private int numIterationsWithNoImprovement;
    private CVRPSpecification[] problemSpecifications;
    private int numTests;
    private String filePath;

    public MultibootMetrics(CVRPSpecification[] problemSpecifications, int numTests, int numIterationsWithNoImprovement) {
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
                for (int i = 1; i <= numTests; ++i) {
                    writer.append("MULTIBOOT" + TimeAndIterationsRecorder.CSV_SEPARATOR
                            + this.numIterationsWithNoImprovement + TimeAndIterationsRecorder.CSV_SEPARATOR 
                            + LOCAL_SEARCHES_NAMES[localSearchPos] + TimeAndIterationsRecorder.CSV_SEPARATOR 
                            + i + TimeAndIterationsRecorder.CSV_SEPARATOR
                    );
                    for (CVRPSpecification problemSpecification : problemSpecifications) {
                        TimeAndIterationsRecorder algorithmRecorder = new TimeAndIterationsRecorder();
                        Multiboot.multiboot(problemSpecification, LOCAL_SEARCHES[localSearchPos], this.numIterationsWithNoImprovement, algorithmRecorder);
                        writer.append(algorithmRecorder.toString() + TimeAndIterationsRecorder.CSV_SEPARATOR);
                        System.out.println("MULTIBOOT "
                                + " Num iterations no improvement: " + this.numIterationsWithNoImprovement 
                                + " " + LOCAL_SEARCHES_NAMES[localSearchPos] 
                                + " Test number: " + i
                                + " Recorder info: " + algorithmRecorder.toString() + TimeAndIterationsRecorder.CSV_SEPARATOR
                                );
                    }
                    writer.append("\n");
                    writer.flush();
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
