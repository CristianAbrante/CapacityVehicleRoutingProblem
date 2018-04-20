/** 
 * File containing the Node class definition. 
 */
package daa.project.crvp.problem;

import java.awt.Point;

/**
 * Class which represents a node of Capacity Routing Vehicle Problem.
 * 
 * @author Daute Rodríguez Rodríguez (alu0100973914@ull.edu.es)
 * @version 1.0
 * @since 16 abr. 2018
 */
public class CVRPClient extends Point {
	/** Demand of the node. */
	private int demand;

	/**
	 * Default constructor.
	 * 
	 * @param xCoordinate
	 *            X axis coordinate of the node.
	 * @param yCoordinate
	 *            Y axis coordinate of the node.
	 * @param demand
	 *            Demand of the node.
	 */
	public CVRPClient(int xCoordinate, int yCoordinate, int demand) {
		super(xCoordinate, yCoordinate);
		this.setDemand(demand);
	}

	/**
	 * Getter method for the demand attribute.
	 * 
	 * @return demand of the node.
	 */
	public int getDemand() {
		return demand;
	}

	/**
	 * Setter method for the demand attribute.
	 * 
	 * @param demand
	 *            of the node.
	 */
	public void setDemand(int demand) {
		this.demand = demand;
	}

	/**
	 * Calculates the euclidean distance between two given nodes.
	 * 
	 * @param firstNode
	 *            First node.
	 * @param secondNode
	 *            Second node.
	 * @return Distance between nodes.
	 */
	public static int euclideanDistance(CVRPClient firstNode, CVRPClient secondNode) {
		return (int) Point.distance(firstNode.getX(), firstNode.getY(), secondNode.getX(), secondNode.getY());
	}
}
