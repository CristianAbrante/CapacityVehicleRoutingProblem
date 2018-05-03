package daa.project.crvp.problem;

import java.util.ArrayList;

import java.util.*;

/**
 * Represents all the information of a solution for 
 * a Capacitated Vehicle Routing Problem (CVRP)
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
	 * Reference to a data structure holding the information of the specific problem
	 * that this solution is for.
	 */
	private CVRPSpecification problemInfo;

	/**
     * Solution representation. The codification is k sequences of numbers ending
     * by SEPARATOR. The i-th sequence represents the sequence of clients that the
     * i-th vehicle have to send supplies to. Being each element of the sequence the
     * ID of the client to visit
     */
	private ArrayList<Integer> vehicleRoutes = new ArrayList<>();

	/**
	 * List of k numbers representing the index at vehicleRoutes where the i-th
	 * route starts
	 */
	private ArrayList<Integer> routesStartingIndexes = new ArrayList<>();

	/**
	 * In the i-th position is stored the remaining capacity that the i-th vehicle
	 * can carry
	 */
	private ArrayList<Integer> vehicleRemainingCapacities = new ArrayList<>();

	/**
	 * Total distance that have to travel to get to every client
	 */
    private double totalDistance;

	/**
	 * Whether this solution is feasible or not
	 */
	private boolean isFeasible;

	/**
     * Create a solution given the information of the problem and the solution
     * codification. This constructor interpret the codification to extract
     * more information: whether the solution is feasible or not, the
     * total distance of the solution, the remaining capacities of the
     * vehicles and the positions where the routes start.
     * 
     * Note that the codification of the solution is as follows: a list of
     * routes with a SEPARATOR at the end of each route. Each route is in
     * turn, a sequence of client IDs and represents the sequence of clients
     * that the vehicle assigned to that route has to visit.
     * 
     * @param problemInfo  Data structure that has the information of the CVRP 
     * that this solution is for
     * @param vehicleRoutes    Codification of the solution
     */
	public CVRPSolution(CVRPSpecification problemInfo, ArrayList<Integer> vehicleRoutes) {
		setProblemInfo(problemInfo);
		setVehicleRoutes(vehicleRoutes);

		final int vehiclesCapacity = getProblemInfo().getCapacity();
		int currentRouteStartingIndex = 0;
		int currentRouteDemand = 0;
        double totalDistance = 0;
		setFeasible(true);
		CVRPClient prevClientOfTheRoute = getProblemInfo().getDepot();
		CVRPClient currentClientOfTheRoute = null;

        for (int pos = 0; pos < getVehicleRoutes().size(); ++pos) {
            int clientId = getVehicleRoutes().get(pos);
            if (clientId == CVRPSolution.SEPARATOR) {
                // Add remaining capacity for the previous route. And set the demand for the
                // next potential route
                addVehicleRemainingCapacity(vehiclesCapacity - currentRouteDemand);
                currentRouteDemand = 0;
                
                // Add starting index for the previous route. And set the index for the next
                // potential route
                addRoutesStartingIndex(currentRouteStartingIndex);
                currentRouteStartingIndex = pos + 1;
                
                // Update total distance. From the last node we have to count the distance 
                // to go back to the depot
                totalDistance += CVRPClient.euclideanDistance(prevClientOfTheRoute, getProblemInfo().getDepot());
                
                // Update lastClientOfTheRoute to be depot (so next route starts fresh from
                // depot)
                prevClientOfTheRoute = getProblemInfo().getDepot();
            } else {
                // Get current client
                currentClientOfTheRoute = getProblemInfo().getClient(clientId);
                
                // Update demand and feasibility if current vehicle has to satisfy more demand
                // than it can
                currentRouteDemand += currentClientOfTheRoute.getDemand();
                if (currentRouteDemand > vehiclesCapacity) {
                    setFeasible(false);
                }
                
                // Update total distance
                totalDistance += CVRPClient.euclideanDistance(prevClientOfTheRoute, currentClientOfTheRoute);
                
                // Update last client of the route to be the current one
                prevClientOfTheRoute = currentClientOfTheRoute;
            }
        }
        
        setTotalDistance(totalDistance);
	}
	
	/**
	 * Copy constructor.
	 * @param copySolution Source CVRP Solution to copy.
	 */
	public CVRPSolution(CVRPSolution copySolution) {
		this(copySolution.getProblemInfo(), new ArrayList<Integer>(copySolution.getVehicleRoutes()));
	}
    
    @Override
    public boolean equals(Object thatObj) {
        return getVehicleRoutes().equals(((CVRPSolution) thatObj).getVehicleRoutes());
    }
    
    @Override
    public int hashCode() {
        return getVehicleRoutes().hashCode();
    }

	/**
	 * Returns the client ID in the specified position or SEPARATOR if there is no
	 * client in that position. Throws if the position is off limits
	 * 
	 * @param position
	 *          Position in the solution vector specifying the client ID to return
	 * @return Client ID of the client in the specified position of SEPARATOR
	 */
	public int getClientId(int position) {
		if (position < 0 || position >= getVehicleRoutes().size()) {
			throw new IndexOutOfBoundsException(
					"invalid solution index \"" + position + "\" Expected index to be 0 <= index < " + getVehicleRoutes().size());
		}
		return getVehicleRoutes().get(position);
	}

    /**
     * Returns the client ID in the specified position in the specified route.
     * Throws if the position is off limits of the route or the route is not valid
     * 
     * @param route
     *          Route to get the client ID from
     * @param positionInRoute
     *          Position of the client ID to get inside the specified route
     * @return ID of the specified client     
     */
    public int getClientId(int route, int positionInRoute) {
        int routeStartingIndex = getRouteStartingIndex(route);
        if (positionInRoute < 0 || positionInRoute >= getNumberOfClientsInRoute(route)) {
            throw new IndexOutOfBoundsException(
                    "invalid solution index \"" + positionInRoute + "\" For route \"" + route
                    + "\". Index should be 0 <= index < " + getNumberOfClientsInRoute(route));
        }
        return getVehicleRoutes().get(routeStartingIndex + positionInRoute);
    }
    
    /**
     * Adds a new client to route, recalculating the routes starting indexes,
     * vehicle remaining capacities and total distance. Also, if the new remaining
     * value is negative the feasibility will be settled as false
     * 
     * @param route Route where new client will be added
     * @param clientId Client that will be added in route route
     */
    public void addClientToRoute(int route, int clientId) {
       	if (route < 0 || route >= getNumberOfRoutes()) {
       		throw new IndexOutOfBoundsException(
                   	"trying adding client " + clientId + " on an invalid route: " + route); 
       	}
       
       	int startingIndexOfRoute = getRouteStartingIndex(route);
       	ArrayList<Integer> newVehiclesRoutes = (ArrayList<Integer>) getVehicleRoutes().clone();
       	newVehiclesRoutes.subList(startingIndexOfRoute, vehicleRoutes.size()).clear();
       	CVRPClient clientToMove = problemInfo.getClient(getClientId(startingIndexOfRoute));
       	
       	setTotalDistance(getTotalDistance() - CVRPClient.euclideanDistance(clientToMove, getProblemInfo().getDepot()));
       	newVehiclesRoutes.add(clientId);
       	newVehiclesRoutes.addAll(
       			getVehicleRoutes().subList(startingIndexOfRoute, getVehicleRoutes().size())
       	);
       	
       	setVehicleRoutes(newVehiclesRoutes);
       	updateVehicleRemainingCapacity(
 		      getVehicleRemainingCapacity(route) - problemInfo.getClient(clientId).getDemand(), 
 		      route
       	);
       	
       	if(route + 1 < getNumberOfRoutes()) {
       		updateRoutesStartingIndex(route + 1, getRouteStartingIndex(route + 1) + 1);
       	}else {
//       		if(getRouteStartingIndex(route) + 1 != CVRPSolution.SEPARATOR)
       			updateRoutesStartingIndex(route, getRouteStartingIndex(route) + 1);
       	}
       	
       	double distanceToClient = CVRPClient.euclideanDistance(
       			problemInfo.getClient(clientId), getProblemInfo().getDepot()
       	);
       	
       	setTotalDistance(getTotalDistance() + distanceToClient);
   }
    
	/**
	 * Returns the client information in the specified position of the solution. Or
	 * null if at the specified position there is not a valid client ID. Or throws
	 * if the position specified is off limits.
	 * 
	 * @param position
	 *          Index in the solution array
	 * @return Client information
	 */
	public CVRPClient getClient(int position) {
		int clientId = getClientId(position);
		if (clientId == CVRPSolution.SEPARATOR) {
			return null;
		}
		return getProblemInfo().getClient(clientId);
	}

	/**
	 * Returns the information of the j-th client in the i-th route. Where j is the
	 * positionInRoute and i the route. If there is no valid client at the position
	 * specified, null is returned. Exception is thrown if any position is off
	 * limits
	 * 
	 * @param route
	 *          Route to get the client information from
	 * @param positionInRoute
	 *          Position of the client information to get inside the specified route
	 * @return Information of the specified client
	 */
	public CVRPClient getClient(int route, int positionInRoute) {
        int clientId = getClientId(route, positionInRoute);
        return getProblemInfo().getClient(clientId);
	}

	/** @return the problemInfo */
	public CVRPSpecification getProblemInfo() {
		return problemInfo;
	}

	/** @return the totalDistance */
    public double getTotalDistance() {
		return totalDistance;
	}

	/** @return the isFeasible */
	public boolean isFeasible() {
		return isFeasible;
	}

	/**
	 * Returns the starting index of the specified route in this solution
	 * 
	 * @param route
	 *          Route number to get the starting index for
	 * @return The starting index of the route in this solution
	 */
	public int getRouteStartingIndex(int route) {
		return getRoutesStartingIndexes().get(route);
	}

	/**
	 * Returns the number of routes
	 * 
	 * @return the number of routes
	 */
	public int getNumberOfRoutes() {
		return getRoutesStartingIndexes().size();
	}

	/**
	 * Returns the number of clients that the specified route has
	 * 
	 * @param route
	 *          Route number to get its number of vehicles
	 * @return Number of vehicles that the specified route has
	 */
	public int getNumberOfClientsInRoute(int route) {
		int routeStartingIndex = getRoutesStartingIndexes().get(route);
        // If the route specified is not the last route...
        if (route < (getNumberOfRoutes() - 1)) {
            int nextRouteStartingIndex = getRoutesStartingIndexes().get(route + 1);
            return nextRouteStartingIndex - routeStartingIndex - 1;
		}
		else {
            // If the route is the last one, we have to return the difference between the solution
            // array length and the position. -1 because be don't want the SEPARATOR to be counted
            return getVehicleRoutes().size() - routeStartingIndex - 1;
		}
	}

    /**
     * Each route represents a sequence of clients that a vehicle has to visit
     * to satisfy their demands. Each vehicle can carry a maximum
     * number of items (the capacity).
     * 
     * This method returns the remaining capacity of the vehicle
     * associated to the given route.
     * 
     * @param route Route whose vehicle remaining capacity has to be returned
     * @return  Remaining capacity of the vehicle of the given route
     */
    public int getVehicleRemainingCapacity(int route) {
        return getVehicleRemainingCapacities().get(route);
    }
    
	/**
     * Returns the total number of clients considering every route.
     * 
     * @return Total number of clients
     */
	public int getNumberOfClients() {
        return getVehicleRoutes().size() - getNumberOfRoutes();
	}
    
	/** @return the vehicleRoutes */
	private ArrayList<Integer> getVehicleRoutes() {
		return vehicleRoutes;
	}
	
	/**
	 * Updates the routes index
	 * @param route Route where we will update the index
	 * @param newIndex New index of the route route
	 */
	private void updateRoutesStartingIndex(int route, int newIndex) {
		int increase = newIndex - routesStartingIndexes.get(route);
		for(int i = route; i < getNumberOfRoutes(); i++) {
			routesStartingIndexes.set(i, routesStartingIndexes.get(i) + increase);
		}
	}

	/** @return the routesStartingIndexes */
	private ArrayList<Integer> getRoutesStartingIndexes() {
        return this.routesStartingIndexes;
	}

	/** @return the vehicleRemainingCapacities */
	private ArrayList<Integer> getVehicleRemainingCapacities() {
		return vehicleRemainingCapacities;
	}

	/**
	 * @param problemInfo
	 *          the problemInfo to set
	 */
	private void setProblemInfo(CVRPSpecification problemInfo) {
		this.problemInfo = problemInfo;
	}

	/**
	 * @param vehicleRoutes
	 *          the vehicleRoutes to set
	 */
	private void setVehicleRoutes(ArrayList<Integer> vehicleRoutes) {
		this.vehicleRoutes = vehicleRoutes;
	}

    /**
     * Add a starting index for a new route
     * 
     * @param newRouteStartingIndex
     */
	private void addRoutesStartingIndex(int newRouteStartingIndex) {
        this.routesStartingIndexes.add(newRouteStartingIndex);
	}

    /**
     * Add the remaining capacity for the vehicle of a new route
     * 
     * @param newVehicleRemainingCapacity
     */
	private void addVehicleRemainingCapacity(int newVehicleRemainingCapacity) {
		this.vehicleRemainingCapacities.add(newVehicleRemainingCapacity);
	}
	
	 /**
    * Update the remaining capacity for the specified vehicle
    * 
    * @param newCapacity New capacity for vehicle vehicle
    * @param vehicle Vehicle whose capacity will be updated
    */
	private void updateVehicleRemainingCapacity(int newCapacity, int vehicle) {
		if(newCapacity < 0) {
			setFeasible(false);
		}
		
		vehicleRemainingCapacities.set(vehicle, newCapacity);
	}

	/**
	 * @param totalDistance
	 *          the totalDistance to set
	 */
    private void setTotalDistance(double totalDistance) {
		this.totalDistance = totalDistance;
	}

	/**
	 * @param isFeasible
	 *          the isFeasible to set
	 */
	private void setFeasible(boolean isFeasible) {
		this.isFeasible = isFeasible;
	}
	
	/**
	 * Method that swap two clients inside the vehicle routes and return a new routes array.
	 * 
	 * @param currentFromRoutePosition Current index inside the source route.
	 * @param currentFromRoute Index of the current route from where the exchange is produced.
	 * @param currentToRoutePosition  Current index inside the destiny route.
	 * @param currentToRoute Index of the current route to where the exchange is produced.
	 * @return New routes array with the elements swapped..
	 */
	public static ArrayList<Integer> generateSwappedSolution(CVRPSolution currentSolution, int currentFromRoute, int currentFromRoutePosition,
			int currentToRoute, int currentToRoutePosition) {
		int firstSwapIndex = currentSolution.getRouteStartingIndex(currentFromRoute) + currentFromRoutePosition;
		int secondSwapIndex = currentSolution.getRouteStartingIndex(currentToRoute) + currentToRoutePosition;		
		ArrayList<Integer> newVehicleRoutes = new ArrayList<Integer>(currentSolution.getVehicleRoutes());
		
		java.util.Collections.swap(newVehicleRoutes, firstSwapIndex, secondSwapIndex);
		return newVehicleRoutes;
	}
	
	/**
	 * Method that move one client inside the vehicle routes to another position. 
	 * It returns the routes array with the positions moved.
	 * 
	 * @param currentFromRoutePosition Current index inside the source route.
	 * @param currentFromRoute Index of the current route from where the exchange is produced.
	 * @param currentToRoutePosition  Current index inside the destiny route.
	 * @param currentToRoute Index of the current route to where the exchange is produced.
	 * @return New routes array with the elements moved.
	 */
  public static ArrayList<Integer> generateMovedSolution(CVRPSolution currentSolution, int currentFromRoute, int currentFromRoutePosition,
			int currentToRoute, int currentToRoutePosition) {
  	int firstIndex = currentSolution.getRouteStartingIndex(currentFromRoute) + currentFromRoutePosition;
		int secondIndex = currentSolution.getRouteStartingIndex(currentToRoute) + currentToRoutePosition;		
		ArrayList<Integer> newVehicleRoutes = new ArrayList<Integer>(currentSolution.getVehicleRoutes());
		
		if (newVehicleRoutes.get(firstIndex) == SEPARATOR) {
			throw new IllegalArgumentException("Trying to move an unknown element. First Index: " 
					+ firstIndex + " Second Index: " + secondIndex);
		}
		int copyElement = newVehicleRoutes.remove(firstIndex);
		if (secondIndex - 1 >= 0) {
			secondIndex -= 1;
		}
		
		newVehicleRoutes.add(secondIndex, copyElement);
		
		return newVehicleRoutes;
  }
  

}
