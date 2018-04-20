package daa.project.crvp.problem;

import java.util.ArrayList;

/**
 * TODO: DESCRIPTION
 * 
 * @author Carlos Dominguez Garcia (alu0100966589)
 * @version 1.0.0
 * @since 1.0.0 (Apr 18, 2018)
 * @file InterouteSwap.java
 *
 */
public class CVRPSolution {
    
    /**
     * Separator between routes
     */
    public static final int SEPARATOR = -1;
    
    /**
     * Reference to a data structure holding the information
     * of the specific problem that this solution is for.
     */
    private CVRPSpecification problemInfo;
    
    /**
     * Solution representation. The codification is k sequences of numbers
     * separated by a SEPARATOR. The i-th sequence represents the sequence
     * of clients that the i-th vehicle have to send supplies to. Being each
     * element of the sequence the ID of the client to visit
     */
    private ArrayList<Integer> vehicleRoutes;
    
    /**
     * List of k numbers representing the index at vehicleRoutes where
     * the i-th route starts
     */
    private ArrayList<Integer> routesStartingIndexes;

    /**
     * In the i-th position is stored the remaining capacity that
     * the i-th vehicle can carry
     */
    private ArrayList<Integer> vehicleRemainingCapacities;
    
    /**
     * Total distance that have to travel to get to every client
     */
    private int totalDistance;
    
    /**
     * Whether this solution is feasible or not
     */
    private boolean isFeasible;
    
    /**
     * TODO: build object from vehicleRoutes
     */
    public CVRPSolution(CVRPSpecification problemInfo, ArrayList<Integer> vehicleRoutes) {
        setProblemInfo(problemInfo);
        setVehicleRoutes(vehicleRoutes);
    }
    
    /**
     * Returns the client information in the specified position of the solution.
     * Or null if at the specified position there is not a valid client ID
     * 
     * @param position  Index in the solution array
     * @return  Client information
     */
    public CVRPClient getClient(int position) {
        if (position < 0 || position >= getVehicleRoutes().size()) {
            throw new IllegalArgumentException("Invalid solution index \"" + position
                    + "\" Expected index to be 0 <= index < " + getVehicleRoutes().size());
        }
        int clientId = getVehicleRoutes().get(position);
        if (clientId == CVRPSolution.SEPARATOR) {
            return null;
        }
        return getProblemInfo().getClient(clientId);
    }
    
    /**
     * 
     * @param route
     * @param positionInRoute
     * @return
     */
    public CVRPClient getClient(int route, int positionInRoute) {
        // TODO
        return null;
    }
    
    /** @return the problemInfo */
    public CVRPSpecification getProblemInfo() {
        return problemInfo;
    }
    
    /** @return the totalDistance */
    public int getTotalDistance() {
        return totalDistance;
    }
    
    /** @return the isFeasible */
    public boolean isFeasible() {
        return isFeasible;
    }
    
    /** @return the vehicleRoutes */
    private ArrayList<Integer> getVehicleRoutes() {
        return vehicleRoutes;
    }

    /** @return the routesStartingIndexes */
    private ArrayList<Integer> getRoutesStartingIndexes() {
        return routesStartingIndexes;
    }

    /** @return the vehicleRemainingCapacities */
    private ArrayList<Integer> getVehicleRemainingCapacities() {
        return vehicleRemainingCapacities;
    }
    
    /** @param problemInfo the problemInfo to set */
    private void setProblemInfo(CVRPSpecification problemInfo) {
        this.problemInfo = problemInfo;
    }
    
    /** @param vehicleRoutes the vehicleRoutes to set */
    private void setVehicleRoutes(ArrayList<Integer> vehicleRoutes) {
        this.vehicleRoutes = vehicleRoutes;
    }
    
    /** @param routesStartingIndexes the routesStartingIndexes to set */
    private void setRoutesStartingIndexes(ArrayList<Integer> routesStartingIndexes) {
        this.routesStartingIndexes = routesStartingIndexes;
    }
    
    /** @param vehicleRemainingCapacities the vehicleRemainingCapacities to set */
    private void setVehicleRemainingCapacities(ArrayList<Integer> vehicleRemainingCapacities) {
        this.vehicleRemainingCapacities = vehicleRemainingCapacities;
    }
    
    /** @param totalDistance the totalDistance to set */
    private void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }
    
    /** @param isFeasible the isFeasible to set */
    private void setFeasible(boolean isFeasible) {
        this.isFeasible = isFeasible;
    }
    
}
