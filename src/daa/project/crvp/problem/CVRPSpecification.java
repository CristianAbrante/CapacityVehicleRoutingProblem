/** 
 * File containing the ProblemSpecification class definition. 
 */
package daa.project.crvp.problem;

/**
 * Class which represents an instance of Capacity Routing Vehicle Problem.
 * 
 * @author Daute Rodríguez Rodríguez (alu0100973914@ull.edu.es)
 * @version 1.0
 * @since 16 abr. 2018
 */
public class CVRPSpecification {

	/** Clients. */
	private CVRPClient[] clients;
	/** Depot identifier. */
	private int depotID;
	/** Maximum capacity of a vehicle. */
	private int capacity;
	
	/**
	 * Default constructor.
	 * @param clients Clients.
	 * @param depotID Depot identifier.
	 * @param capacity Maximum capacity of a vehicle.
	 */
	public CVRPSpecification(CVRPClient[] clients, int depotID, int capacity) {
		this.setCapacity(capacity);
		this.setDepotID(depotID);
		this.setClients(clients);
	}
	
	/**
	 * Getter method for the clients attribute.
	 * @return Clients of the problem.
	 */
	public CVRPClient[] getClients() {
		return clients;
	}
	
    /**
     * Returns the client information for the specified client via its ID
     * 
     * @param clientId Client ID
     * @return Client information
     */
    public CVRPClient getClient(int clientId) {
        if (clientId < 0 || clientId >= getClients().length) {
            throw new IllegalArgumentException("Invalid client ID \"" + clientId
                    + "\" Expected client ID to be 0 <= clientId < " + getClients().length);
        }
        return getClients()[clientId];
    }
	
	/**
	 * Setter method for the clients attribute.
	 * @param Clients of the problem.
	 */
	public void setClients(CVRPClient[] clients) {
		this.clients = clients;
	}
	
	/**
	 * Getter method for the depotID attribute.
	 * @return Identifier of the depot.
	 */
	public int getDepotID() {
		return depotID;
	}
	
	/**
	 * Setter method for the depotID attribute.
	 * @param Identifier of the depot.
	 */
	public void setDepotID(int depotID) {
		this.depotID = depotID;
	}
	
	/**
	 * Getter method for the capacity attribute.
	 * @return Capacity of each vehicle.
	 */
	public int getCapacity() {
		return capacity;
	}
	
	/**
	 * Setter method for the capacity attribute.
	 * @param Capacity of each vehicle.
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
}
