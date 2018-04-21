package daa.project.crvp.moves;

import java.util.ArrayList;

import daa.project.crvp.problem.CVRPClient;
import daa.project.crvp.problem.CVRPSolution;

/**
 * Swap interoute. Move for VRP where the order of visiting two clients in the
 * same route is swapped.
 * 
 * @author Carlos Dominguez Garcia (alu0100966589)
 * @version 1.0.0
 * @since 1.0.0 (Apr 18, 2018)
 * @file InterouteSwap.java
 *
 */
public class IntrarouteSwap extends Move {

    private int     numberRoutes     = -1;
    private boolean hasMoreNeighbors = false;
    
    // Variables to hold current state
    private int currentRoute               = -1;
    private int numberClientsCurrentRoute  = -1;
    private int currentRouteFirstPosition  = -1;
    private int currentRouteSecondPosition = -1;
    
    // Variables to hold next state
    private int nextRoute               = -1;
    private int numberClientsNextRoute  = -1;
    private int nextRouteFirstPosition  = -1;
    private int nextRouteSecondPosition = -1;
    
	@Override
	public void setSolution(CVRPSolution solution) {
		super.setSolution(solution);
        initialize();
	}

	@Override
	public void nextNeighbor() {
        if (this.hasMoreNeighbors) {
            // Set current state to next state
            this.currentRoute = this.nextRoute;
            this.numberClientsCurrentRoute = this.numberClientsNextRoute;
            this.currentRouteFirstPosition = this.nextRouteFirstPosition;
            this.currentRouteSecondPosition = this.nextRouteSecondPosition;
            
            // Update next state
            updateNextState();
        }
	}

	@Override
    public double getLastMoveCost() {
		/*
         * [..., i, ..., j, ...] 
         * MoveCost = 
         *    -cost(i-1, i) 
         *    -cost(i, i+1) 
         *    -cost(j-1, j) 
         *    -cost(j, j+1)
         *    +cost(i-1, j) 
         *    +cost(j, i+1) 
         *    +cost(j-1, i) 
         *    +cost(i, j+1)
         *    
         * [..., i, j, ...] 
         * MoveCost = 
         *    -cost(i-1, i) 
         *    -cost(j, j+1)
         *    +cost(i-1, j) 
         *    +cost(i, j+1)
         */
        CVRPClient first = getSolution().getClient(this.currentRoute, this.currentRouteFirstPosition);
        CVRPClient second = getSolution().getClient(this.currentRoute, this.currentRouteSecondPosition);
	    
        CVRPClient prevFirst = this.currentRouteFirstPosition > 0
                ? getSolution().getClient(this.currentRoute, this.currentRouteFirstPosition - 1)
                : getSolution().getProblemInfo().getDepot();
        
        CVRPClient postSecond = this.currentRouteSecondPosition < (this.numberClientsCurrentRoute - 1)
                ? getSolution().getClient(this.currentRoute, this.currentRouteSecondPosition + 1)
                : getSolution().getProblemInfo().getDepot();
        
        if (this.currentRouteFirstPosition == (this.currentRouteSecondPosition - 1)) {
            return CVRPClient.euclideanDistance(first, postSecond) + CVRPClient.euclideanDistance(prevFirst, second)
                    - CVRPClient.euclideanDistance(second, postSecond) - CVRPClient.euclideanDistance(prevFirst, first);
        }
        
        CVRPClient postFirst = this.currentRouteFirstPosition < (this.numberClientsCurrentRoute - 1)
                ? getSolution().getClient(this.currentRoute, this.currentRouteFirstPosition + 1)
                : getSolution().getProblemInfo().getDepot();
        
        CVRPClient prevSecond = this.currentRouteSecondPosition > 0
                ? getSolution().getClient(this.currentRoute, this.currentRouteSecondPosition - 1)
                : getSolution().getProblemInfo().getDepot();

        return CVRPClient.euclideanDistance(first, postSecond) + CVRPClient.euclideanDistance(prevSecond, first)
                + CVRPClient.euclideanDistance(second, postFirst) + CVRPClient.euclideanDistance(prevFirst, second)
                - CVRPClient.euclideanDistance(second, postSecond) - CVRPClient.euclideanDistance(prevSecond, second)
                - CVRPClient.euclideanDistance(first, postFirst) - CVRPClient.euclideanDistance(prevFirst, first);
	}

	@Override
    public double getCurrentNeighborCost() {
        return this.getSolution().getTotalDistance() + this.getLastMoveCost();
	}

	@Override
	public boolean isCurrentNeighborFeasible() {
        // We are only swapping the order to visit clients in the same route
        return getSolution().isFeasible();
	}

	@Override
	public CVRPSolution getCurrentNeighbor() {
        ArrayList<Integer> newSolutionCodification = CVRPSolution.generateSwappedSolution(getSolution(),
                this.currentRoute, this.currentRouteFirstPosition, this.currentRoute, this.currentRouteSecondPosition);
        return new CVRPSolution(getSolution().getProblemInfo(), newSolutionCodification);
	}

	@Override
	public boolean hasMoreNeighbors() {
        return this.hasMoreNeighbors;
	}
    
    /**
     * Initializes the state of this move
     */
    private void initialize() {
        this.hasMoreNeighbors = true;
        this.currentRoute = 0;
        this.currentRouteFirstPosition = 0;
        this.currentRouteSecondPosition = 0;
        this.nextRoute = 0;
        this.nextRouteFirstPosition = 0;
        this.nextRouteSecondPosition = 0;
        
        // Check that there is at least one route
        this.numberRoutes = getSolution().getNumberOfRoutes();
        if (this.numberRoutes == 0) {
            throw new IllegalArgumentException("Cannot perform moves on solution with no routes");
        }
        
        this.numberClientsCurrentRoute = getSolution().getNumberOfClientsInRoute(this.currentRoute);
        this.numberClientsNextRoute = this.numberClientsCurrentRoute;
        
        updateNextState();
    }
    
    public void updateNextState() {
        // The goal of this move is to iterate through all possible swaps of two elements in each route.
        // What we want to do is a iteration of the following loop each time this method is called
        //
        //        for (int i = 0; i < this.numberRoutes; ++i)
        //            this.numberClientsCurrentRoute = getSolution().getNumberOfClientsInRoute(i);
        //            for (int j = 0; j < (this.numberClientsCurrentRoute - 1); ++j)
        //                for (int k = j + 1; k < this.numberClientsCurrentRoute; ++k)
        //
        // The following code imitates an iteration of the previous loop
        if (this.hasMoreNeighbors) {
            this.nextRouteSecondPosition += 1;
            while (this.nextRouteSecondPosition >= this.numberClientsNextRoute) {
                this.nextRouteFirstPosition += 1;
                this.nextRouteSecondPosition = this.nextRouteFirstPosition + 1;
                while (this.nextRouteFirstPosition >= (this.numberClientsNextRoute - 1)) {
                    this.nextRoute += 1;
                    this.nextRouteFirstPosition = 0;
                    this.nextRouteSecondPosition = 1;
                    if (this.nextRoute == this.numberRoutes) {
                        this.hasMoreNeighbors = false;
                        this.nextRoute = -1;
                        this.nextRouteFirstPosition = -1;
                        this.nextRouteSecondPosition = -1;
                    } else {
                        this.numberClientsNextRoute = getSolution().getNumberOfClientsInRoute(this.nextRoute);
                    }
                }
            }
        }
    }
}
