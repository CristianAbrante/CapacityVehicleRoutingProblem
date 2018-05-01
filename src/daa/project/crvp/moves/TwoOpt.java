/**
 * 
 */
package daa.project.crvp.moves;

import java.util.ArrayList;

import daa.project.crvp.problem.CVRPClient;
import daa.project.crvp.problem.CVRPSolution;

/**
 * <h2>TwoOpt</h2>
 * 
 * @author	Cristian Abrante Dorta
 * @company	University Of La Laguna
 * @date 		01/05/2018
 * @version 1.0.0
 */
public class TwoOpt extends Move {
  
  private final int NO_ROUTE_POSITION = -1;
  private final int DEFAULT_FIRST_POSITION = -1;
  
  private int firstClient = DEFAULT_FIRST_POSITION;
  private int secondClient = firstClient + 1;
  private int currentRoute = DEFAULT_FIRST_POSITION;
  
  private boolean hasMoreNeighbors = false;
  
  private double currentCost;
  
  
  @Override
  public CVRPSolution getSolution() {
    if (super.getSolution() != null) {
      return super.getSolution();      
    } else {
      throw new IllegalAccessError("trying to use move with no base solution set");
    }
  }
  
  @Override
  public void setSolution(CVRPSolution solution) {
    super.setSolution(solution);
    firstClient = DEFAULT_FIRST_POSITION;
    secondClient = firstClient + 1;
    currentRoute = DEFAULT_FIRST_POSITION;
    
    if (getSolution().getNumberOfRoutes() == 0) {
      throw new IllegalArgumentException("Cannot perform moves on solution with no routes");
    }
    updateHasMoreNeigbors();
    updateCurrentCost();
  }

  @Override
  public void nextNeighbor() {
    setNextClientsState();
    updateCurrentCost();
    //System.out.println(String.format("r: %d c1: %d c2: %d hmn: %b", currentRoute, firstClient, secondClient, hasMoreNeighbors));
  }
  
  @Override
  public boolean hasMoreNeighbors() {
    if (getSolution() != null) {
      return hasMoreNeighbors;      
    } else {
      throw new IllegalAccessError("trying to use move with no base solution set");
    }
  }
  
  @Override
  public double getLastMoveCost() {
    return this.currentCost - getSolution().getTotalDistance();
  }
  
  @Override
  public double getCurrentNeighborCost() {
    if (getSolution() != null) {
      return this.currentCost;
    } else {
      throw new IllegalAccessError("trying to use move with no base solution set");
    }
  }
  
  @Override
  public boolean isCurrentNeighborFeasible() {
    return getSolution().isFeasible();
  }
  
  @Override
  public CVRPSolution getCurrentNeighbor() {
    if (currentRoute == DEFAULT_FIRST_POSITION) {
      return getSolution();
    }
    ArrayList<Integer> swappedClientsSolution = CVRPSolution.generateSwappedSolution(getSolution(), 
                                                                                     currentRoute, 
                                                                                     firstClient, 
                                                                                     currentRoute, 
                                                                                     secondClient);
    // We swap all the intermediate nodes of the two indexes.
    int firstClientNext = firstClient + 1;
    int secondClientPrevious = secondClient - 1;
    while (secondClientPrevious > firstClientNext) {
      int firstSwapIndex = getSolution().getRouteStartingIndex(currentRoute) + firstClientNext;
      int secondSwapIndex = getSolution().getRouteStartingIndex(currentRoute) + secondClientPrevious;
      java.util.Collections.swap(swappedClientsSolution, firstSwapIndex, secondSwapIndex);
      firstClientNext++;
      secondClientPrevious--;
    }
    
    return new CVRPSolution(getSolution().getProblemInfo(), swappedClientsSolution);
  }
  
  private void updateHasMoreNeigbors() {
    if (currentRoute == DEFAULT_FIRST_POSITION) {
      hasMoreNeighbors = getNextRoute(currentRoute) != NO_ROUTE_POSITION;
    } else {
      int clientsInRoute = getSolution().getNumberOfClientsInRoute(currentRoute);
      if (secondClient == clientsInRoute -1
       && firstClient == clientsInRoute -2) {
        hasMoreNeighbors = getNextRoute(currentRoute) != NO_ROUTE_POSITION;
        return;
      }
      hasMoreNeighbors = true;
    }
  }
  
  private void setNextClientsState() {
    if (hasMoreNeighbors()) {
      if (firstClient == DEFAULT_FIRST_POSITION) {
        currentRoute = getNextRoute(currentRoute);
        firstClient = 0;
        secondClient = firstClient + 1;
      } else {
        int clientsInRoute = getSolution().getNumberOfClientsInRoute(currentRoute);
        if (secondClient == clientsInRoute -1) {
          if (firstClient == clientsInRoute - 2) {
            currentRoute = getNextRoute(currentRoute);
            firstClient = 0;
            secondClient = firstClient + 1;
          } else {
            firstClient += 1;
            secondClient = firstClient + 1;
          }
        } else {
          secondClient += 1;
        }
      }
      updateHasMoreNeigbors();
    }
  }
  
  private int getNextRoute(int currentRoute) {
    int nextRoute = currentRoute + 1;
    int numberOfRoutes = getSolution().getNumberOfRoutes();
    while (nextRoute < numberOfRoutes
        && getSolution().getNumberOfClientsInRoute(nextRoute) < 2) {
      nextRoute += 1;
    }
    return nextRoute < numberOfRoutes ? nextRoute : NO_ROUTE_POSITION;
  }
  
  private void updateCurrentCost() {
    if (currentRoute == DEFAULT_FIRST_POSITION) {
      this.currentCost = getSolution().getTotalDistance();
      return;
    }
    
    CVRPClient firstNode = getSolution().getClient(currentRoute, firstClient);
    CVRPClient secondNode = getSolution().getClient(currentRoute, secondClient);
    
    CVRPClient previousFirstNode;
    if (firstClient > 0) {
      previousFirstNode = getSolution().getClient(currentRoute, firstClient - 1);
    } else {
      previousFirstNode = getSolution().getProblemInfo().getDepot();
    }
    
    CVRPClient postSecondNode;
    int numberOfClientsInRoute = getSolution().getNumberOfClientsInRoute(currentRoute);
    if (secondClient < numberOfClientsInRoute - 1) {
      postSecondNode = getSolution().getClient(currentRoute, secondClient + 1);
    } else {
      postSecondNode = getSolution().getProblemInfo().getDepot();
    }
    
    this.currentCost = getSolution().getTotalDistance()                             // Previous cost
                     - CVRPClient.euclideanDistance(firstNode, previousFirstNode)   // - d(i, i -1)
                     - CVRPClient.euclideanDistance(secondNode, postSecondNode)     // - d(k, k + 1)
                     + CVRPClient.euclideanDistance(previousFirstNode, secondNode)  // + d(i - 1, k)
                     + CVRPClient.euclideanDistance(firstNode, postSecondNode);     // + d(i, k + 1)
    
  }
}
