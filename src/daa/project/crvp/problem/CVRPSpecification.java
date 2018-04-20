/** 
 * File containing the ProblemSpecification class definition. 
 */
package daa.project.crvp.problem;

import java.util.ArrayList;

/**
 * Class which represents an instance of Capacity Routing Vehicle Problem.
 * 
 * @author Daute Rodríguez Rodríguez (alu0100973914@ull.edu.es)
 * @version 1.0
 * @since 16 abr. 2018
 */
public class CVRPSpecification {

	/** Clients. */
	private ArrayList<CVRPClient> clients;
	/** Depot identifier. */
	private int depotID;
	/** Maximum capacity of a vehicle. */
	private int capacity;
	/** Minimum vehicle number. */
	private int minimumVehicles;
	
	/**
     * Create an object holding the given information for a Capacitated Vehicle Routing Problem
     *  
     * @param clients Clients.
     * @param depotID Depot identifier.
     * @param capacity Maximum capacity of a vehicle.
     */
	public CVRPSpecification(ArrayList<CVRPClient> clients, int depotID, int capacity, int minimumVehicles) {
		this.setCapacity(capacity);
		this.setDepotID(depotID);
		this.setClients(clients);
		this.setMinimunVehicles(minimumVehicles);
	}
	
    /**
     * Create an empty data structure to hold information for a Capacitated Vehicle Routing Problem
     */
	public CVRPSpecification() {
        this(new ArrayList<>(), -1, -1, -1);
	}
    
	/**
	 * Getter method for the clients attribute.
	 * @return Clients of the problem.
	 */
	public ArrayList<CVRPClient> getClients() {
		return clients;
	}
	
    /**
     * Returns the client information for the specified client via its ID
     * 
     * @param clientId Client ID
     * @return Client information
     */
    public CVRPClient getClient(int clientId) {
        if (clientId < 0 || clientId >= getClients().size()) {
            throw new IllegalArgumentException("Invalid client ID \"" + clientId
                    + "\" Expected client ID to be 0 <= clientId < " + getClients().size());
        }
        return getClients().get(clientId);
    }
    
    /**
     * Returns the depot information
     * 
     * @return the depot information
     */
    public CVRPClient getDepot() {
        return getClients().get(getDepotID());
    }
	
	/**
	 * Setter method for the clients attribute.
	 * @param Clients of the problem.
	 */
	public void setClients(ArrayList<CVRPClient> clients) {
		this.clients = clients;
	}
	
    /**
     * @return The minimum number of vehicles that have to be used to solve this problem
     */
	public int getMinimunVehicles() {
		return this.minimumVehicles;
	}
	
	/**
	 * Setter method for the minimum vehicles attribute.
	 * @param Min vehicle for the problem.
	 */
	public void setMinimunVehicles(int minimumVehicles) {
		this.minimumVehicles = minimumVehicles;
	}
	
	/**
	 * Method for add a new client at clients.
	 * @param Client to be added.
	 */
	public void addClient(CVRPClient clients) {
		getClients().add(clients);
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
