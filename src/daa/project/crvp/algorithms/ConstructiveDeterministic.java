package daa.project.crvp.algorithms;

import java.util.ArrayList;

import daa.project.crvp.problem.CVRPClient;
import daa.project.crvp.problem.CVRPSolution;
import daa.project.crvp.problem.CVRPSpecification;

public class ConstructiveDeterministic {
	/** Problem specification. */
	private static CVRPSpecification	problemSpecification;
	/** Current client. */
	private static CVRPClient	currentClient;
	/** Remaining capacity of the current vehicle. */
	private static int	remainingVehicleCapacity;
	/** Remaining clients. */
	private static ArrayList<CVRPClient> remainingClients;
	
	public static CVRPSolution constructDeterministicSolution(CVRPSpecification problemSpecification) {
		ConstructiveDeterministic.problemSpecification = problemSpecification;
		// Solution codification.
		ArrayList<Integer> solution = new ArrayList<>();
		// Remaining clients to serve, remove the depot.
		remainingClients = new ArrayList<>(
				ConstructiveDeterministic.problemSpecification.getClients());
		remainingClients.remove(ConstructiveDeterministic.problemSpecification.getDepot());
		// Start from the depot.
		currentClient = ConstructiveDeterministic.problemSpecification.getDepot();
		// Establishes the remaining capacity of the current vehicle or route.
		remainingVehicleCapacity = ConstructiveDeterministic.problemSpecification.getCapacity();
		// Keep iterating until no clients left.
		while (!remainingClients.isEmpty()) {
			CVRPClient closestClient = null;
			Double minimumDistance = Double.MAX_VALUE;
			for (CVRPClient client : remainingClients) {
				if (client.getDemand() > remainingVehicleCapacity) {
					continue;
				}
				if (CVRPClient.euclideanDistance(currentClient, client) < minimumDistance) {
					closestClient = client;
					minimumDistance = CVRPClient.euclideanDistance(currentClient, client);
				}
			}
			if (closestClient != null) {
				solution.add(problemSpecification.getClients().indexOf(closestClient));
				remainingVehicleCapacity -= closestClient.getDemand();
				remainingClients.remove(closestClient);
			} else {
				solution.add(CVRPSolution.SEPARATOR);
				remainingVehicleCapacity = ConstructiveDeterministic.problemSpecification.getCapacity();
			}
			if (remainingClients.isEmpty()) {
				solution.add(CVRPSolution.SEPARATOR);
			}
		}
		
		// Return the generated solution.
		return new CVRPSolution(ConstructiveDeterministic.problemSpecification, solution);
	}
	
}
