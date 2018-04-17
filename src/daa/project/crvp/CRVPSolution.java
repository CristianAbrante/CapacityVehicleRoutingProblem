/**
 * 
 */
package daa.project.crvp;

import java.util.ArrayList;

/**
 * @author alu0100967111
 *
 */
public class CRVPSolution {
	
	/**
	 * 
	 */
	private static final int SEPARATOR = -1;
	
	/**
	 * 
	 */
	private int associatedTargetFunctionValue;

	/**
	 * 
	 */
	private int numberOfRoutes;
	
	/**
	 * 
	 */
	private int[] vehicleRemainingCapacitiesArray;
	
	/**
	 * 
	 */
	private ArrayList<Integer> vehicleRouteArray;
	
	/**
	 * 
	 */
	public CRVPSolution() {
		
	}

	/**
	 * @return the associatedTargetFunctionValue
	 */
	public int getAssociatedTargetFunctionValue() {
		return associatedTargetFunctionValue;
	}

	/**
	 * @return the numberOfRoutes
	 */
	public int getNumberOfRoutes() {
		return numberOfRoutes;
	}

	/**
	 * @return the vehicleRemainingCapacitiesArray
	 */
	public int[] getVehicleRemainingCapacitiesArray() {
		return vehicleRemainingCapacitiesArray;
	}
	
	/**
	 * @return the vehicleRouteArray
	 */
	public ArrayList<Integer> getVehicleRouteArray() {
		return vehicleRouteArray;
	}
	
	/**
	 * @param vehicleRouteArray the vehicleRouteArray to set
	 */
	public void setVehicleRouteArray(ArrayList<Integer> vehicleRouteArray) {
		this.vehicleRouteArray = vehicleRouteArray;
	}
	
}
