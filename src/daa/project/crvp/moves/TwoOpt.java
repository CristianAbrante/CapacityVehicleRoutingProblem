/**
 * 
 */
package daa.project.crvp.moves;

import java.util.ArrayList;

import daa.project.crvp.problem.CVRPClient;
import daa.project.crvp.problem.CVRPSolution;

/**
 * <h2>TwoOpt</h2>
 * This class implements the 2-opt local search.
 * <p>
 * 2-opt search try to recombine two of the vertex
 * in a single route. The main idea is to
 * recombine the route in a way that it don't
 * cross itself.
 * 
 * @author	Cristian Abrante Dorta
 * @company	University Of La Laguna
 * @date 		21/04/2018
 * @version 1.0.0
 */
public class TwoOpt extends Move {
  
  /**
   * The position in the route where the search is
   * going to start from.
   */
  private final int STARTING_ROUTE_POSITION = 0;
  
  /**
   * Flag used to compute the first solution of all.
   */
  private boolean firstSolutionEvaluated = false;
  
  /**
   * Route that we are examining.
   */
  private int currentRoute = STARTING_ROUTE_POSITION;
  
  /**
   * The first node of the current route that we are examining.
   */
  private int currentFirstNode = STARTING_ROUTE_POSITION;
  
  /**
   * The second node of the current route that we are examining.
   */
  private int currentSecondNode = currentFirstNode + 1;
  
  /**
   * The current cost of the movement.
   */
  private double currentCost;
  
  /**
   * Flag that says if solution has more neighbours.
   */
  private boolean hasMoreNeighbors = true;
  
  /**
   * Constructor of TwoOpt
   */
  public TwoOpt() {
    super();
  }
  
  /**
   * Constructor of TwoOpt
   * 
   * @param solution that we are going to use for the movement.
   */
  public TwoOpt(CVRPSolution solution) {
    super();
    super.setSolution(solution);
  }
  
   /**
   * @see daa.project.crvp.moves.Move#setSolution(daa.project.crvp.problem.CVRPSolution)
   */
  @Override
  public void setSolution(CVRPSolution solution) {
    super.setSolution(solution);
    if (getSolution().getNumberOfRoutes() == 0) {
      throw new IllegalArgumentException("solution must have more than zero routes.");
    }
    initializeIndexes();
    firstSolutionEvaluated = false;
    updateCurrentCost();
  }
  
  /**
   * @see daa.project.crvp.moves.Move#nextNeighbor()
   */
  @Override
  public void nextNeighbor() {
    if (getSolution() == null) {
      throw new IllegalAccessError("initial solution is null");
    }
    if (hasMoreNeighbors()) {
      if (firstSolutionEvaluated) {
        updateNextPosition();
      } else {        
        firstSolutionEvaluated = true;
        evaluateIfHasMoreNeighbors();
      }
      updateCurrentCost();
    }
  }

  /**
   * @see daa.project.crvp.moves.Move#getLastMoveCost()
   */
  @Override
  public double getLastMoveCost() {
    return getCurrentNeighborCost() - getSolution().getTotalDistance();
  }

  /**
   * @see daa.project.crvp.moves.Move#isCurrentNeighborFeasible()
   */
  @Override
  public boolean isCurrentNeighborFeasible() {
    if (getSolution() != null) {
      return getSolution().isFeasible();      
    } else {
      throw new IllegalAccessError("trying to use move with no base solution set");
    }
  }

  /**
   * @see daa.project.crvp.moves.Move#getCurrentNeighbor()
   */
  @Override
  public CVRPSolution getCurrentNeighbor() {
    if (getSolution() == null) {
      throw new IllegalAccessError("trying to use move with no base solution set");
    }
    if (!firstSolutionEvaluated) {
      return getSolution();
    }
    ArrayList<Integer> swappedIndexSolution = CVRPSolution.generateSwappedSolution(getSolution(), currentRoute, currentFirstNode, currentRoute, currentSecondNode);
    // We swap all the intermediate nodes of the two indexes.
    int firstNodeIndex = currentFirstNode + 1;
    int secondNodeIndex = currentFirstNode - 1;
    while (secondNodeIndex > firstNodeIndex) {
      int firstSwapIndex = getSolution().getRouteStartingIndex(currentRoute) + firstNodeIndex;
      int secondSwapIndex = getSolution().getRouteStartingIndex(currentRoute) + secondNodeIndex;
      java.util.Collections.swap(swappedIndexSolution, firstSwapIndex, secondSwapIndex);
      firstNodeIndex++;
      secondNodeIndex--;
    }
    
    return new CVRPSolution(getSolution().getProblemInfo(), swappedIndexSolution);
  }
  
  /**
   * @see daa.project.crvp.moves.Move#hasMoreNeighbors()
   */
  @Override
  public boolean hasMoreNeighbors() {
    if (getSolution() != null) {
      return hasMoreNeighbors;      
    } else {
      throw new IllegalAccessError("trying to use move with no base solution set");
    }
  }
  
  /**
   * @see daa.project.crvp.moves.Move#getCurrentNeighborCost()
   */
  @Override
  public double getCurrentNeighborCost() {
    if (getSolution() != null) {
      return currentCost;      
    } else {
      throw new IllegalAccessError("trying to use move with no base solution set");
    }
  }
  
  private void initializeIndexes() {
    currentRoute = 0;
    while (currentRoute < getSolution().getNumberOfRoutes()
        && getSolution().getNumberOfClientsInRoute(currentRoute) < 2) {
      currentRoute++;
    }
    if (currentRoute < getSolution().getNumberOfRoutes()) {
      currentFirstNode = 0;
      currentSecondNode = 1;
      // TODO: Has more neighbours
      hasMoreNeighbors = true;
    } else {
      hasMoreNeighbors = false;
    }
  }
  
  private void evaluateIfHasMoreNeighbors() {
    int clientsInCurrentRoute = getSolution().getNumberOfClientsInRoute(currentRoute);
    if (currentSecondNode == clientsInCurrentRoute - 1) {
      if (currentFirstNode == clientsInCurrentRoute - 2) {
        if (currentRoute < getSolution().getNumberOfRoutes() - 1) {
          int nextRoute = currentRoute + 1;
          while (nextRoute < getSolution().getNumberOfRoutes()
              && getSolution().getNumberOfClientsInRoute(nextRoute) < 2) {
            nextRoute += 1;
          }
          if (nextRoute < getSolution().getNumberOfRoutes()) {
            hasMoreNeighbors = true;
          } else {
            hasMoreNeighbors = false;
          }
        } else {
          hasMoreNeighbors = false;
        }        
      } else {
        hasMoreNeighbors = true;
      }
    } else {
      hasMoreNeighbors = true;
    }
  }
  
  public void updateNextPosition() {
    if (hasMoreNeighbors()) {
      int clientsInCurrentRoute = getSolution().getNumberOfClientsInRoute(currentRoute);
      if (currentSecondNode == clientsInCurrentRoute - 1) {
        if (currentFirstNode == clientsInCurrentRoute - 2) {
          do {
            currentRoute++;            
          } while (getSolution().getNumberOfClientsInRoute(currentRoute) < 2);
          currentFirstNode = 0;
          currentSecondNode = 1;
        } else {
          currentFirstNode++;
          currentSecondNode = currentFirstNode + 1;
        }
      } else {
        currentSecondNode++;
      }
      // TODO: Evaluate more neighbors
      evaluateIfHasMoreNeighbors();
    }
  }
  
  /**
   * Method used to update the cost of the solution.
   */
  private void updateCurrentCost() {
    if (!firstSolutionEvaluated) {
      currentCost = getSolution().getTotalDistance();
      return;
    }
    // From the original cost, we remove the distances to the evaluated nodes.
    double costRemovingCurrentVertex = getSolution().getTotalDistance() 
                                       - getDistance(currentRoute, currentFirstNode - 1, currentFirstNode)
                                       - getDistance(currentRoute, currentSecondNode, currentSecondNode + 1);
    // if the first evaluated node is the first node we have
    // to update the distance to the depot
    if (currentFirstNode == 0) {
      costRemovingCurrentVertex -= getDepotDistance(currentRoute, 0);
      costRemovingCurrentVertex += getDepotDistance(currentRoute, currentSecondNode);
    }
    
    // If the second evaluated node is the last node, 
    // we update the last distance to the depot.
    if (currentSecondNode == getSolution().getNumberOfClientsInRoute(currentRoute) - 1) {
      costRemovingCurrentVertex -= getDepotDistance(currentRoute, currentSecondNode);
      costRemovingCurrentVertex += getDepotDistance(currentRoute, currentFirstNode);
    }
    
    // Finally the current cost is updated with the new crossed distances.
    currentCost = costRemovingCurrentVertex 
                  + getDistance(currentRoute, currentFirstNode - 1, currentSecondNode)
                  + getDistance(currentRoute, currentFirstNode, currentSecondNode + 1);
    
  }
  
  /**
   * Method used to return the distance between two nodes of a
   * route.
   * 
   * @param route the route where the nodes are
   * @param firstPosition the position of the first node in the route
   * @param secondPosition the position of the second node in the route
   * @return distance between the nodes and 0.0 if position is not valid
   */
  private double getDistance(int route, int firstPosition, int secondPosition) {
    try {
      CVRPClient firstNode =  getSolution().getClient(route, firstPosition);
      CVRPClient secondNode = getSolution().getClient(route, secondPosition);
      return CVRPClient.euclideanDistance(firstNode, secondNode);
    }
    catch(IndexOutOfBoundsException e) {
      return 0.0;
    }
  }
  
  /**
   * Method used to return the distance from the depot to the
   * node of the position.
   * 
   * @param route where the node is located
   * @param position of the node inside the route
   * @return distance between the node and the depot and 0.0 is solution 
   * is not valid.
   */
  private double getDepotDistance(int route, int position) {
    try {
      CVRPClient depot = getSolution().getProblemInfo().getDepot();
      CVRPClient client = getSolution().getClient(route, position);
      return CVRPClient.euclideanDistance(depot, client);
    }
    catch(IndexOutOfBoundsException e) {
      return 0.0;
    }
  }
}
