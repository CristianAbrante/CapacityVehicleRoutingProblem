/**
 * [DESCRIPTION]
 * 
 * @author Ángel Igareta (angel@igareta.com)
 * @version 1.0
 * @since 20-04-2018
 */
package daa.project.crvp.moves;

import daa.project.crvp.problem.CVRPSolution;

/**
 * [DESCRIPTION]
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
	/** Cost of the last movement. */
	private double lastMoveCost = 0.0;

	@Override
	public void setSolution(CVRPSolution solution) {
		super.setSolution(solution);

		this.canAdvance = true;
		this.lastMoveCost = 0.0;

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
			canAdvance = advanceToPosition();
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
		return this.getCurrentNeighbor().isFeasible();
	}

	@Override
	public double getLastMoveCost() {
		if (getSolution() == null) {
			throw new IllegalAccessError("trying to use move with no base solution set");
		}
		return (this.getCurrentNeighbor().getTotalDistance() - this.getSolution().getTotalDistance());
	}

	@Override
	public double getCurrentNeighborCost() {
		if (getSolution() == null) {
			throw new IllegalAccessError("trying to use move with no base solution set");
		}
		return this.getCurrentNeighbor().getTotalDistance();
	}

	@Override
	public CVRPSolution getCurrentNeighbor() {
		if (getSolution() == null) {
			throw new IllegalAccessError("trying to use move with no base solution set");
		}
		if (this.currentFromRoute == DEFAULT_ROUTE_VALUE) {
			throw new IllegalArgumentException("Calling getCurrent Neighbor without routes.");
		}
		else if (this.currentToRoute == DEFAULT_ROUTE_VALUE) {
			return getSolution();
		}
		else {
			return new CVRPSolution(getSolution().getProblemInfo(), CVRPSolution.generateMovedSolution(getSolution(),
					currentFromRoute, currentFromRoutePosition, currentToRoute, currentToRoutePosition));
		}
	}
}
