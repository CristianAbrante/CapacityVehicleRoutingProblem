/** 
 * File containing the Node class definition. 
 */
package daa.project.crvp.problem;

/**
 * Class which represents a node of Capacity Routing Vehicle Problem.
 * 
 * @author Daute Rodríguez Rodríguez (alu0100973914@ull.edu.es)
 * @version 1.0
 * @since 16 abr. 2018
 */
public class CVRPClient {
	/** X axis coordinate of the node. */
	private int xCoordinate;
	/** Y axis coordinate of the node. */
	private int yCoordinate;
	/** Demand of the node. */
	private int demand;
	
	/**
	 * Default constructor.
	 * @param xCoordinate X axis coordinate of the node.
	 * @param yCoordinate Y axis coordinate of the node.
	 * @param demand Demand of the node.
	 */
	public CVRPClient(int xCoordinate, int yCoordinate, int demand) {
		this.setxCoordinate(xCoordinate);
		this.setyCoordinate(yCoordinate);
		this.setDemand(demand);
	}
	
	/**
	 * Getter method for the xCoordinate attribute.
	 * @return xCoordinate of the node.
	 */
	public int getxCoordinate() {
		return this.xCoordinate;
	}
	
	/**
	 * Setter method for the xCoordinate attribute.
	 * @param xCoordinate of the node.
	 */
	public void setxCoordinate(int xCoordinate) {
		this.xCoordinate = xCoordinate;
	}
	
	/**
	 * Getter method for the yCoordinate attribute.
	 * @return yCoordinate of the node.
	 */
	public int getyCoordinate() {
		return this.yCoordinate;
	}
	
	/**
	 * Setter method for the yCoordinate attribute.
	 * @param yCoordinate of the node.
	 */
	public void setyCoordinate(int yCoordinate) {
		this.yCoordinate = yCoordinate;
	}
	
	/**
	 * Getter method for the demand attribute.
	 * @return demand of the node.
	 */
	public int getDemand() {
		return this.demand;
	}
	
	/**
	 * Setter method for the demand attribute.
	 * @param demand of the node.
	 */
	public void setDemand(int demand) {
		this.demand = demand;
	}
	
	/**
	 * Calculates the euclidean distance between two given nodes.
	 * @param firstNode First node.
	 * @param secondNode Second node.
	 * @return Distance between nodes.
	 */
  public static double euclideanDistance(CVRPClient firstNode, CVRPClient secondNode) {
		int firstNodeXCoord = firstNode.getxCoordinate();
		int secondNodeXCoord = secondNode.getxCoordinate();
		int firstNodeYCoord = firstNode.getyCoordinate();
		int secondNodeYCoord = secondNode.getyCoordinate();
		
        return Math.hypot(secondNodeXCoord - firstNodeXCoord, secondNodeYCoord - firstNodeYCoord);
	}
	
	/* (non-Javadoc)
	 * Overloaded method to prettify the client output, showing it's coordinates and demand.
	 * @see java.lang.Object#toString()
	 */
	@Override
    public String toString() {
		return "C: [" + this.getxCoordinate() + ", " + this.getyCoordinate() + "] D:" + this.getDemand();
	}
}