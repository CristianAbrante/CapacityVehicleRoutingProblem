/**
 * [DESCRIPTION]
 * 
 * @author Ã�ngel Igareta (angel@igareta.com)
 * @version 1.0
 * @since 20-04-2018
 */
package daa.project.crvp.moves;

import daa.project.crvp.problem.CVRPClient;
import daa.project.crvp.problem.CVRPSolution;

/**
 * Relocation move is an inter-route move that moves an element from a route to
 * another one.
 */
public class Relocation extends Move {

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
	/** Boolean that denotes if there are more movements. */
	private boolean canAdvance = true;

	/** If the algorithm has started */
	private boolean started = false;

	private double lastMoveCost;

	@Override
	public void setSolution(CVRPSolution solution) {
		super.setSolution(solution);

		this.started = false;
		this.canAdvance = true;

		this.currentFromRoutePosition = 0;
		this.currentFromRoute = getNextRouteOf(-1);

		if (this.currentFromRoute == 0) {
			this.currentToRoute = (getSolution().getNumberOfRoutes() > 1) ? 1 : DEFAULT_ROUTE_VALUE;
		}
		else {
			this.currentToRoute = 0;
		}
		this.currentToRoutePosition = 0;

		if ((this.currentFromRoute == DEFAULT_ROUTE_VALUE) || (this.currentToRoute == DEFAULT_ROUTE_VALUE)) {
			this.canAdvance = false;
		}
	}

	private int getNextRouteOf(int currentRoute) {
		for (int i = currentRoute + 1; i < getSolution().getNumberOfRoutes(); ++i) {
			if (getSolution().getNumberOfClientsInRoute(i) > 0) {
				return i;
			}
		}
		return DEFAULT_ROUTE_VALUE;
	}

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

	@Override
	public boolean hasMoreNeighbors() {
		if (getSolution() == null) {
			throw new IllegalAccessError("trying to use move with no base solution set");
		}
		return canAdvance;
	}

	private boolean advanceToPosition() {
		if (this.currentToRoutePosition == this.getSolution().getNumberOfClientsInRoute(currentToRoute)) {
			if (this.currentToRoute == this.getSolution().getNumberOfRoutes() - 1) {
				return advanceFromPosition();
			}
			else {
				return advanceToRoute();
			}
		}
		else {
			this.currentToRoutePosition++;
			return true;
		}
	}

	private boolean advanceFromPosition() {
		if ((getSolution().getNumberOfClientsInRoute(currentFromRoute) == 0)
				|| (currentFromRoutePosition == getSolution().getNumberOfClientsInRoute(currentFromRoute) - 1)) {
			return advanceFromRoute();
		}
		else {
			currentFromRoutePosition++;
			this.currentToRoutePosition = 0;

			this.currentToRoute = getNextToRoute();
			if (this.currentToRoute == DEFAULT_ROUTE_VALUE) {
				throw new IllegalArgumentException("This error can't be throwed.");
			}
			else {
				return true;
			}
		}
	}

	private boolean advanceFromRoute() {
		if ((currentFromRoute + 1) < getSolution().getNumberOfRoutes()) {
			int nextFromRoute = getNextRouteOf(currentFromRoute);
			if (nextFromRoute == DEFAULT_ROUTE_VALUE) {
				return false;
			}
			else {
				currentFromRoutePosition = 0;
				currentFromRoute = nextFromRoute;
				this.currentToRoutePosition = 0;

				this.currentToRoute = getNextToRoute();
				if (this.currentToRoute == DEFAULT_ROUTE_VALUE) {
					throw new IllegalArgumentException("This error can't be throwed.");
				}
				else {
					return true;
				}
			}
		}
		else {
			return false;
		}
	}

	private int getNextToRoute() {
		for (int i = 0; i < getSolution().getNumberOfRoutes(); ++i) {
			if (i != this.currentFromRoute) {
				return i;
			}
		}
		return DEFAULT_ROUTE_VALUE;
	}

	private boolean advanceToRoute() {
		this.currentToRoutePosition = 0;
		if (currentToRoute + 1 == currentFromRoute) {
			if (currentToRoute + 2 >= this.getSolution().getNumberOfRoutes()) {
				return advanceFromPosition();
			}
			else {
				this.currentToRoute += 2;
			}
		}
		else {
			if (currentToRoute + 1 >= this.getSolution().getNumberOfRoutes()) {
				return advanceFromPosition();
			}
			else {
				this.currentToRoute += 1;
			}
		}
		return true;
	}

	@Override
	public boolean isCurrentNeighborFeasible() {
		if (getSolution() == null) {
			throw new IllegalAccessError("trying to use move with no base solution set");
		}
		int realFromPosition = getClientAbsolutePosition(currentFromRoutePosition, currentFromRoute);
		CVRPClient clientOfFromRoute = getSolution().getClient(realFromPosition);

		int fromRouteDemand = getSolution().getVehicleRemainingCapacity(currentFromRoute) + clientOfFromRoute.getDemand();
		int toRouteDemand = getSolution().getVehicleRemainingCapacity(currentToRoute) - clientOfFromRoute.getDemand();

		// If the route is in the limits, check the rest!
		if ((fromRouteDemand >= 0) && (toRouteDemand >= 0)) {
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
		// return getCurrentNeighbor().isFeasible();
	}

	@Override
	public double getLastMoveCost() {
		if (getSolution() == null) {
			throw new IllegalAccessError("trying to use move with no base solution set");
		}
		return this.lastMoveCost;
		// return (getCurrentNeighborCost() - getSolution().getTotalDistance());
	}

	@Override
	public double getCurrentNeighborCost() {
		if (getSolution() == null) {
			throw new IllegalAccessError("trying to use move with no base solution set");
		}
        return getSolution().getTotalDistance() + getLastMoveCost();
        //        return getCurrentNeighbor().getTotalDistance();
	}

	/**
	 * Method that calculates the last movement distance difference. For doing so,
	 * It subtracts the the next and last distance to the old nodes and adds the new
	 * distance to reach the swapped nodes in each route.
	 */
	private void calculateLastMoveCost() {
		try {
			CVRPClient depot = getSolution().getProblemInfo().getDepot();

			// From route
			int realFromPosition = getClientAbsolutePosition(currentFromRoutePosition, currentFromRoute);
			CVRPClient lastClientOfFromRoute = (currentFromRoutePosition == 0) ? depot
					: getSolution().getClient(realFromPosition - 1);
			CVRPClient clientOfFromRoute = getSolution().getClient(realFromPosition);
			CVRPClient nextClientOfFromRoute = (currentFromRoutePosition == (getSolution()
					.getNumberOfClientsInRoute(currentFromRoute) - 1)) ? depot : getSolution().getClient(realFromPosition + 1);

			this.lastMoveCost = -CVRPClient.euclideanDistance(lastClientOfFromRoute, clientOfFromRoute)
					- CVRPClient.euclideanDistance(clientOfFromRoute, nextClientOfFromRoute)
					+ CVRPClient.euclideanDistance(lastClientOfFromRoute, nextClientOfFromRoute); // ¿Good?

			// To Route
			// If To Route Is Empty
			if (getSolution().getNumberOfClientsInRoute(currentToRoute) == 0) {
				this.lastMoveCost += (2.0 * CVRPClient.euclideanDistance(depot, clientOfFromRoute));
			}
			else { // If route has more than 1 element.
				int realToPosition = getClientAbsolutePosition(currentToRoutePosition, currentToRoute);
				CVRPClient lastClientOfToRoute = (currentToRoutePosition == 0) ? depot
						: getSolution().getClient(realToPosition - 1);
				CVRPClient clientOfToRoute = (currentToRoutePosition == (getSolution()
						.getNumberOfClientsInRoute(currentToRoute))) ? depot : getSolution().getClient(realToPosition);

				this.lastMoveCost = this.lastMoveCost + CVRPClient.euclideanDistance(lastClientOfToRoute, clientOfFromRoute)
						+ CVRPClient.euclideanDistance(clientOfFromRoute, clientOfToRoute)
						- CVRPClient.euclideanDistance(lastClientOfToRoute, clientOfToRoute);
			}
		}
		catch (Exception e) {
			System.err.println("Error in relocation movement calculateLastMoveCost()");
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
		//this.lastMoveCost = (getCurrentNeighbor().getTotalDistance() - getSolution().getTotalDistance());
	}

	private int getClientAbsolutePosition(int positionInRoute, int route) {
		if (getSolution() == null) {
			throw new IllegalAccessError("trying to use move with no base solution set");
		}
		return getSolution().getRouteStartingIndex(route) + positionInRoute;
	}

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
			return new CVRPSolution(getSolution().getProblemInfo(), CVRPSolution.generateMovedSolution(getSolution(),
					currentFromRoute, currentFromRoutePosition, currentToRoute, currentToRoutePosition));
		}
	}

	/*
	 * (non-Javadoc) We generate a fake state with the second client as null.
	 * 
	 * @see daa.project.crvp.moves.Move#getState()
	 */
	@Override
	public MoveState getState() {
		int realFromPosition = getSolution().getRouteStartingIndex(currentFromRoute) + currentFromRoutePosition;
		CVRPClient firstClient = getSolution().getClient(realFromPosition);
		return new MoveState(firstClient, null);
	}
}
