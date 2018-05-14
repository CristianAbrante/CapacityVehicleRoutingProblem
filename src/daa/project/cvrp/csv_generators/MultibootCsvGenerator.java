package daa.project.cvrp.csv_generators;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import daa.project.cvrp.AlgorithmMetrics;
import daa.project.cvrp.algorithms.Multiboot;
import daa.project.cvrp.local_search.BestNeighborLocalSearch;
import daa.project.cvrp.local_search.FirstBetterNeighborLocalSearch;
import daa.project.cvrp.local_search.LocalSearch;
import daa.project.cvrp.metrics.TimeAndIterationsRecorder;
import daa.project.cvrp.moves.InterrouteSwap;
import daa.project.cvrp.moves.IntrarouteSwap;
import daa.project.cvrp.moves.Relocation;
import daa.project.cvrp.moves.TwoOpt;
import daa.project.cvrp.problem.CVRPSpecification;
import daa.project.cvrp.utils.DoubleFormatter;

public class MultibootCsvGenerator extends Thread {
    
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
            "BN + Relocation", 
            "BN + Interroute", 
            "BN + IntrarouteSwap", 
            "BN + TwoOpt",
            "FBN + Relocation", 
            "FBN + InterrouteSwap", 
            "FBN + IntrarouteSwap", 
            "FBN + TwoOpt", 
    };
    
    private int numIterationsWithNoImprovement;
    private CVRPSpecification[] problemSpecifications;
    private int numTests;
    private String filePath;

    public MultibootCsvGenerator(CVRPSpecification[] problemSpecifications, int numTests, int numIterationsWithNoImprovement) {
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
                writer.append("MULTIBOOT" + TimeAndIterationsRecorder.CSV_SEPARATOR 
                        + this.numIterationsWithNoImprovement + TimeAndIterationsRecorder.CSV_SEPARATOR 
                        + LOCAL_SEARCHES_NAMES[localSearchPos] + TimeAndIterationsRecorder.CSV_SEPARATOR 
                );
                for (CVRPSpecification problemSpecification : problemSpecifications) {
                    long timeSum = 0;
                    long minTime = Long.MAX_VALUE;
                    double sumObjectiveValues = 0;
                    double minObjectiveValue = Double.MAX_VALUE;
                    for (int i = 1; i <= numTests; ++i) {
                        TimeAndIterationsRecorder algorithmRecorder = new TimeAndIterationsRecorder();
                        Multiboot.multiboot(problemSpecification, LOCAL_SEARCHES[localSearchPos], this.numIterationsWithNoImprovement, algorithmRecorder);
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
                    System.out.println("MULTIBOOT"
                            + " Num its no improvement: " + this.numIterationsWithNoImprovement
                            + " Local search: " + LOCAL_SEARCHES_NAMES[localSearchPos]
                            + " Avg time: " + DoubleFormatter.format(timeSum)
                            + " Avg obj value: " + DoubleFormatter.format(sumObjectiveValues)
                            + " Min time: " + DoubleFormatter.format(minTime)
                            + " Min objetive value: " + DoubleFormatter.format(minObjectiveValue)
                    );
                }

                writer.println();
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
        );
        for (int i = 0; i < AlgorithmMetrics.NUM_SAMPLES; ++i) {
            writer.append(AlgorithmMetrics.sampleNames[i].split("\\.")[0]
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
        );
        for (int i = 0; i < AlgorithmMetrics.NUM_SAMPLES; ++i) {
            writer.append(
                    "AvgTime" + TimeAndIterationsRecorder.CSV_SEPARATOR
                    + "AvgSol" + TimeAndIterationsRecorder.CSV_SEPARATOR
                    + "MinTime" + TimeAndIterationsRecorder.CSV_SEPARATOR
                    + "MinSol" + TimeAndIterationsRecorder.CSV_SEPARATOR
            );
        }
        writer.append("\n");
        
        return writer.toString();
    }
    
}
