/**
 * InterrouteSwap is an special movement that exchange nodes between routes,
 * avoiding repetitions.
 * 
 * @author Ã�ngel Igareta (angel@igareta.com)
 * @version 1.0
 * @since 20-04-2018
 */
package daa.project.crvp.moves;

import daa.project.crvp.problem.CVRPClient;
import daa.project.crvp.problem.CVRPSolution;

/**
 * InterrouteSwap is an special movement that exchange nodes between routes,
 * avoiding repetitions.
 */
public class InterrouteSwap extends Move {

	/** Default value of route indicates that doesn't exist. */
	private int DEFAULT_ROUTE_VALUE = -1;

	/** Index of the current route from where the exchange is produced. */
	private int currentFromRoute = DEFAULT_ROUTE_VALUE;
	/** Index of the current route to where the exchange is produced. */
	private int currentToRoute = DEFAULT_ROUTE_VALUE;
	/** Current index inside the source route. */
	private int currentFromRoutePosition = 0;
	/** Current index inside the destiny route. */
	private int currentToRoutePosition = 0;
	/** Boolean that denotes If the algorithm has started. */
	private boolean started = false;
	/** Boolean that denotes if there are more movements. */
	private boolean canAdvance = true;
	/** Cost of the last movement. */
	private double lastMoveCost = 0.0;

	/*
	 * Method to Update the current solution.
	 * 
	 * @see daa.project.crvp.moves.Move#setSolution(daa.project.crvp.problem.
	 * CVRPSolution)
	 */
	@Override
	public void setSolution(CVRPSolution solution) {
		super.setSolution(solution);

		this.started = false;
		this.canAdvance = true;
		this.lastMoveCost = 0.0;

		this.currentFromRoutePosition = 0;
		this.currentFromRoute = getNextRouteOf(-1);

		this.currentToRoutePosition = 0;
		this.currentToRoute = getNextRouteOf(this.currentFromRoute);

		if (this.currentFromRoute == DEFAULT_ROUTE_VALUE || this.currentToRoute == DEFAULT_ROUTE_VALUE) {
			this.canAdvance = false;
		}
	}

	/**
	 * Method that looks for the first non-empty route in the solution after the
	 * current route and return it.
	 */
	private int getNextRouteOf(int currentRoute) {
		for (int i = currentRoute + 1; i < getSolution().getNumberOfRoutes(); ++i) {
			if (getSolution().getNumberOfClientsInRoute(i) > 0) {
				return i;
			}
		}
		return DEFAULT_ROUTE_VALUE;
	}

	/*
	 * If possible, advance next Neighbor, starting moving the ToPosition.
	 * 
	 * @see daa.project.crvp.moves.Move#nextNeighbor()
	 */
	@Override
	public void nextNeighbor() {
		if (getSolution() == null) {
			throw new IllegalAccessError("trying to use move with no base solution set");
		}
		if (canAdvance) {
			if (started) {
				canAdvance = advanceToPosition();
			}
			else {
				started = true;
			}
			calculateLastMoveCost();
		}
	}

	/*
	 * Method that return if there are movements left.
	 * 
	 * @see daa.project.crvp.moves.Move#hasMoreNeighbors()
	 */
	@Override
	public boolean hasMoreNeighbors() {
		if (getSolution() == null) {
			throw new IllegalAccessError("trying to use move with no base solution set");
		}
		return canAdvance;
	}

	/**
	 * Try to move the To Position. First, it checks if the toRoutePosition is in
	 * the limit of it's current route. If it is the case, if there are more routes,
	 * simply advance the To Route. If not, advance the from Position.
	 * 
	 * If it's not in the limit, simply advance the to route position.
	 * 
	 * @return If it was possible to move To Position.
	 */
	private boolean advanceToPosition() {
		if ((getSolution().getNumberOfClientsInRoute(currentToRoute) == 0)
				|| (currentToRoutePosition == getSolution().getNumberOfClientsInRoute(currentToRoute) - 1)) {
			if (currentToRoute == getSolution().getNumberOfRoutes() - 1) {
				return advanceFromPosition();
			}
			else {
				return advanceToRoute();
			}
		}
		else {
			currentToRoutePosition++;
			return true;
		}
	}

	/**
	 * Try to advance the From Position. If it's in the last position of the route,
	 * it advance the from route. In other case it advance the current route
	 * position
	 * 
	 * @return If it was possible to move From Position.
	 */
	private boolean advanceFromPosition() {
		if ((getSolution().getNumberOfClientsInRoute(currentFromRoute) == 0)
				|| (currentFromRoutePosition == getSolution().getNumberOfClientsInRoute(currentFromRoute) - 1)) {
			return advanceFromRoute();
		}
		else {
			currentFromRoutePosition++;

			currentToRoutePosition = 0;
			int nextToRoute = getNextRouteOf(this.currentFromRoute);
			if (nextToRoute == DEFAULT_ROUTE_VALUE) {
				return false;
			}
			else {
				this.currentToRoute = nextToRoute;
				return true;
			}
		}
	}

	/**
	 * If there current route is not the one BEFORE the last one, move to the next
	 * one, reseting the current from route position. It jumps if the next route is
	 * empty.
	 * 
	 * @return If it was possible to move From Route.
	 */
	private boolean advanceFromRoute() {
		int nextFromRoute = getNextRouteOf(this.currentFromRoute);
		int nextToRoute = getNextRouteOf(nextFromRoute);
		if ((nextFromRoute != DEFAULT_ROUTE_VALUE) && (nextToRoute != DEFAULT_ROUTE_VALUE)) {
			currentFromRoutePosition = 0;
			currentFromRoute = nextFromRoute;
			currentToRoutePosition = 0;
			this.currentToRoute = nextToRoute;
			return true;

		}
		else {
			return false;
		}
	}

	/**
	 * Simply move the current to route, reseting the current to route position.
	 * 
	 * @return If it was possible to move To Route.
	 */
	private boolean advanceToRoute() {
		currentToRoutePosition = 0;

		int nextToRoute = getNextRouteOf(this.currentToRoute);
		if (nextToRoute == DEFAULT_ROUTE_VALUE) {
			return false;
		}
		else {
			this.currentToRoute = nextToRoute;
			return true;
		}
	}

	/**
	 * Method designed to get the absolute position of a client in the global routes
	 * array.
	 * 
	 * @param positionInRoute
	 *          Index of the position inside the route.
	 * @param route
	 *          Index of the route.
	 * @return Absolute position in the global routes array of the CVRP solution.
	 */
	private int getClientAbsolutePosition(int positionInRoute, int route) {
		if (getSolution() == null) {
			throw new IllegalAccessError("trying to use move with no base solution set");
		}
		return getSolution().getRouteStartingIndex(route) + positionInRoute;
	}

	/**
	 * Method that calculates the last movement distance difference. For doing so,
	 * It subtracts the the next and last distance to the old nodes and adds the new
	 * distance to reach the swapped nodes in each route.
	 */
	private void calculateLastMoveCost() {
		try {
			int realFromPosition = getClientAbsolutePosition(currentFromRoutePosition, currentFromRoute);
			int realToPosition = getClientAbsolutePosition(currentToRoutePosition, currentToRoute);

			CVRPClient depot = getSolution().getProblemInfo().getDepot();

			CVRPClient lastClientOfFromRoute = (currentFromRoutePosition == 0) ? depot
					: getSolution().getClient(realFromPosition - 1);
			CVRPClient clientOfFromRoute = getSolution().getClient(realFromPosition);
			CVRPClient nextClientOfFromRoute = (currentFromRoutePosition == (getSolution()
					.getNumberOfClientsInRoute(currentFromRoute) - 1)) ? depot : getSolution().getClient(realFromPosition + 1);

			CVRPClient lastClientOfToRoute = (currentToRoutePosition == 0) ? depot
					: getSolution().getClient(realToPosition - 1);
			CVRPClient clientOfToRoute = getSolution().getClient(realToPosition);
			CVRPClient nextClientOfToRoute = (currentToRoutePosition == (getSolution()
					.getNumberOfClientsInRoute(currentToRoute) - 1)) ? depot : getSolution().getClient(realToPosition + 1);

			this.lastMoveCost = -CVRPClient.euclideanDistance(lastClientOfFromRoute, clientOfFromRoute)
					- CVRPClient.euclideanDistance(clientOfFromRoute, nextClientOfFromRoute)
					- CVRPClient.euclideanDistance(lastClientOfToRoute, clientOfToRoute)
					- CVRPClient.euclideanDistance(clientOfToRoute, nextClientOfToRoute)
					+ CVRPClient.euclideanDistance(lastClientOfFromRoute, clientOfToRoute)
					+ CVRPClient.euclideanDistance(clientOfToRoute, nextClientOfFromRoute)
					+ CVRPClient.euclideanDistance(lastClientOfToRoute, clientOfFromRoute)
					+ CVRPClient.euclideanDistance(clientOfFromRoute, nextClientOfToRoute);
		}
		catch (Exception e) {
			System.err.println("Error in interroute swap calculateLastMoveCost()");
			System.err.println("currentFromRoutePosition: " + currentFromRoutePosition);
			System.err.println("currentFromRoute: " + currentFromRoute);
			System.err.println("currentToRoutePosition: " + currentToRoutePosition);
			System.err.println("currentToRoute: " + currentToRoute);
			for (int r = 0; r < getSolution().getNumberOfRoutes(); ++r) {
				System.out.print("Route " + r + ": ");
				for (int c = 0; c < getSolution().getNumberOfClientsInRoute(r); ++c) {
					System.out.print(getSolution().getClientId(r, c) + ", ");
				}
				System.out.println();
			}
			throw e;
		}
	}

	/*
	 * Method that returns if the current neighbor is feasible. For each route, we
	 * get the capacity of the route, subtract the old node and adds the new one.
	 * 
	 * @see daa.project.crvp.moves.Move#isCurrentNeighborFeasible()
	 */
	@Override
	public boolean isCurrentNeighborFeasible() {
		if (getSolution() == null) {
			throw new IllegalAccessError("trying to use move with no base solution set");
		}
		int realFromPosition = getClientAbsolutePosition(currentFromRoutePosition, currentFromRoute);
		int realToPosition = getClientAbsolutePosition(currentToRoutePosition, currentToRoute);

		CVRPClient clientOfFromRoute = getSolution().getClient(realFromPosition);
		CVRPClient clientOfToRoute = getSolution().getClient(realToPosition);

		int fromRouteCapacity = getSolution().getVehicleRemainingCapacity(currentFromRoute) + clientOfFromRoute.getDemand()
				- clientOfToRoute.getDemand();
		int toRouteCapacity = getSolution().getVehicleRemainingCapacity(currentToRoute) + clientOfToRoute.getDemand()
				- clientOfFromRoute.getDemand();

		// If they are in the limits, check the rest!
		if ((fromRouteCapacity >= 0) && (toRouteCapacity >= 0)) {
			for (int i = 0; i < getSolution().getNumberOfRoutes(); ++i) {
				if ((i != currentFromRoute) && (i != currentToRoute) && (getSolution().getVehicleRemainingCapacity(i) < 0)) {
					return false;
				}
			}
			return true;
		}
		else {
			return false;
		}

		// return (getSolution()(fromRouteCapacity >= 0) && (toRouteCapacity >= 0));
		// return getCurrentNeighbor().isFeasible();
	}

	/*
	 * Getter of last move cost.
	 * 
	 * @see daa.project.crvp.moves.Move#getLastMoveCost()
	 */
	@Override
	public double getLastMoveCost() {
		if (getSolution() == null) {
			throw new IllegalAccessError("trying to use move with no base solution set");
		}
		return this.lastMoveCost;
	}

	/*
	 * Getter of the current neighbor cost.
	 * 
	 * @see daa.project.crvp.moves.Move#getCost()
	 */
	@Override
	public double getCurrentNeighborCost() {
		if (getSolution() == null) {
			throw new IllegalAccessError("trying to use move with no base solution set");
		}
		return getSolution().getTotalDistance() + getLastMoveCost();
	}

	/*
	 * Method that generate a new solution of the swapped routes array.
	 * 
	 * @see daa.project.crvp.moves.Move#getCurrentNeighbor()
	 */
	@Override
	public CVRPSolution getCurrentNeighbor() {
		if (getSolution() == null) {
			throw new IllegalAccessError("trying to use move with no base solution set");
		}
		if (this.currentFromRoute == DEFAULT_ROUTE_VALUE) {
			throw new IllegalArgumentException("Calling getCurrent Neighbor without routes.");
		}
		else if (this.currentToRoute == DEFAULT_ROUTE_VALUE || !started) {
			return getSolution();
		}
		else {
			return new CVRPSolution(getSolution().getProblemInfo(), CVRPSolution.generateSwappedSolution(getSolution(),
					currentFromRoute, currentFromRoutePosition, currentToRoute, currentToRoutePosition));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daa.project.crvp.moves.Move#getState()
	 */
	@Override
	public MoveState getState() {
		int realFromPosition = getClientAbsolutePosition(currentFromRoutePosition, currentFromRoute);
		int realToPosition = getClientAbsolutePosition(currentToRoutePosition, currentToRoute);

		CVRPClient firstClient = getSolution().getClient(realFromPosition);
		CVRPClient secondClient = getSolution().getClient(realToPosition);

		return new MoveState(firstClient, secondClient);
	}
}
