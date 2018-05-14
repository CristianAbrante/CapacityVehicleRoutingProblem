package daa.project.cvrp.IO;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import daa.project.cvrp.problem.CVRPClient;
import daa.project.cvrp.problem.CVRPSpecification;


public class ReaderFromFile {
	/** All possible problem params */
	private final String[] PARAMS = {
			"NAME", "COMMENT", "TYPE", "DIMENSION", "EDGE_WEIGHT_TYPE",
	"CAPACITY"};
	/** Problem params we're interested on */
	private final String[] INTERESTED_PARAMS = {"Min no of trucks", "DIMENSION", "CAPACITY"};
	/** Node information params that we're interested */
	private final String[] INTERESTED_NODE_INFORMATION =
		{"NODE_COORD_SECTION", "DEMAND_SECTION", "DEPOT_SECTION"};

	/** Represents the differents client coords */
	private ArrayList<Point> clientCoords;
	/** Represents the differents client demands */
	private ArrayList<Integer> clientDemands;
	/** Represents the differents depots */
	private ArrayList<Integer> depots;
	/** Represents the clients in our problem */
	private int numberOfClients;
	/** Represents the problem */
	private CVRPSpecification problemSpecification;

	/** 
	 * Constructs a ReaderFile
	 * @param fileName File to be readed
	 * @throws FileNotFoundException Exception that will be thrown if file doesn't exist
	 * @throws IOException Exception that will be thrown if input operation fails
	 */
	public ReaderFromFile(String fileName)
			throws FileNotFoundException, IOException {
		this.problemSpecification = new CVRPSpecification();
		this.clientCoords = new ArrayList<Point>();
		this.clientDemands = new ArrayList<>();
		this.depots = new ArrayList<>();
		String line;
		int lineNumber = 0;
		int lastIntestedParams = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			while ((line = br.readLine()) != null) {
				if(lineNumber == 1) {
					Pattern pattern = Pattern.compile(
							"(Optimal value|Best value)\\s*:\\s*(\\d+)"
							);
					Matcher matcher = pattern.matcher(line);
					matcher.find();
					problemSpecification.setOptimalValue(Integer.valueOf(matcher.group(2)));
				}
				/** Reading problem specification params */
				if (lineNumber < PARAMS.length) {
					/** Finds the params we're interested on */
					Pattern pattern = Pattern.compile(
							"(" + INTERESTED_PARAMS[lastIntestedParams]
									+ ")\\s*:\\s*(\\d+)"
							);
					Matcher matcher = pattern.matcher(line);
					/** If param is an interested param */
					if (matcher.find()) {
						switch (lastIntestedParams) {
							case 0 :
								/** Takes the minimun vehicle number */
								if(Integer.valueOf(matcher.group(2)) <= 0) {
									throw new IllegalArgumentException("Invalid minimum vehicles number " + Integer.valueOf(matcher.group(2))
											+ ". Expected minimum vehicles number to be more than 1");
								}else {
									problemSpecification.setMinimunVehicles(Integer.valueOf(matcher.group(2)));
								}
								break;
							case 1 :
								/** Takes the number after ':' and sets the numberOfClients */
								this.numberOfClients = Integer.valueOf(matcher.group(2));
								break;
							case 2 :
								/** Takes the number after ':' and sets the vehicle capacity */
								problemSpecification
								.setCapacity(Integer.valueOf(matcher.group(2)));
							default :
								break;
						}
						lastIntestedParams++;
					}
				} else {
					/** If the readed line is a client information */
					if (lineNumber <= numberOfClients + PARAMS.length) {
						/** If its not the header of client coords */
						if ((lineNumber == PARAMS.length && line.trim().matches(INTERESTED_NODE_INFORMATION[0])) || lineNumber > PARAMS.length) {
							/** Take the two last numbers of the clients coords section */
							Pattern pattern = Pattern.compile("(\\d+)\\s*(\\d+)$");
							Matcher matcher = pattern.matcher(line);
							matcher.matches();

							if (matcher.find()) {
								/** Adds the client coords */
								clientCoords.add(
										new Point(Integer.valueOf(matcher.group(1).trim()), Integer.valueOf(matcher.group(2).trim()))
										);
							}
						}else {
							throw new IllegalArgumentException("There are more problem params that the setted on the program (" + 
									PARAMS.length + ")");
						}
					}
					/** If the readed line is demand coords */
					if (lineNumber >= numberOfClients + PARAMS.length + 1) {
						/** If its not the header of demand coords */
						if ((lineNumber == numberOfClients + PARAMS.length + 1
						      && line.trim().matches(INTERESTED_NODE_INFORMATION[1]))
						      || lineNumber > numberOfClients + PARAMS.length + 1) {
							/** Takes the last number that corresponds to client capacity */
							Pattern pattern = Pattern.compile("(\\d+)\\s*$");
							Matcher matcher = pattern.matcher(line);
							matcher.matches();
							/** Add the client capacity information */
							if (matcher.find()) {
								if(Integer.valueOf(matcher.group(1).trim()) < problemSpecification.getCapacity()) {
									clientDemands
									.add(Integer.valueOf(matcher.group(1).trim()));
								}else {
									throw new IllegalArgumentException("Invalid capacity for client " + (getClientDemands().size() + 1)
											+ ". Expected client capacity to be less than " + problemSpecification.getCapacity());
								}
							}
						}else{
							throw new IllegalArgumentException("There are more clients that the setted on the program (" + 
									getNumberOfClients() + ")");
						}
					}
					/** If we're on depot information section */
					if (lineNumber > numberOfClients * 2 + PARAMS.length + 1) {
						if (line.matches("[^A-Za-z]+")) {
							line = line.trim();
							/** Takes the depots bigger than 0, because -1 is when the depots list ends */
							if (Integer.valueOf(line) > 0) {
                                depots.add(Integer.valueOf(line) - 1);
							}

						}
					}
				}
				lineNumber++;
			}
		}
		setProblemSpecification();
	}

	/**
	 * Sets the problem specification, adding the clients and demands information
	 * and depot
	 */
	private void setProblemSpecification() {
		for (int i = 0; i < clientCoords.size(); i++) {
			problemSpecification.addClient(
					new CVRPClient((int) clientCoords.get(i).getX(), (int) clientCoords.get(i).getY(), clientDemands.get(i))
					);
		}
		problemSpecification.setDepotID(depots.get(0));
	}

	/**
	 * Gets the client coords
	 * 
	 * @return The clients array coords
	 */
	public ArrayList<Point> getClientCoords() {

		return clientCoords;
	}

	/**
	 * Get the client demands
	 * 
	 * @return Client demands
	 */
	public ArrayList<Integer> getClientDemands() {

		return clientDemands;
	}

	/**
	 * Get possible depots
	 * 
	 * @return Possible depots
	 */
	public ArrayList<Integer> getDepots() {

		return depots;
	}

	/**
	 * Get the number of clients
	 * 
	 * @return Number of clients
	 */
	public int getNumberOfClients() {

		return numberOfClients;
	}

	/**
	 * Get the problem specification
	 * 
	 * @return Problem
	 */
	public CVRPSpecification getProblemSpecification() {

		return problemSpecification;
	}
}
