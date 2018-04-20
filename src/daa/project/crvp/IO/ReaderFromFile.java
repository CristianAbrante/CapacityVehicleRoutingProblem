package daa.project.crvp.IO;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import daa.project.crvp.problem.CVRPClient;
import daa.project.crvp.problem.CVRPSpecification;

public class ReaderFromFile {
	private String[] PARAMS = {"NAME", "COMMENT", "TYPE", "DIMENSION", "EDGE_WEIGHT_TYPE", "CAPACITY"};
	private String[] INTERESTED_PARAMS = {"DIMENSION", "CAPACITY"};
	private String[] INTERESTED_NODE_INFORMATION = {"NODE_COORD_SECTION", "DEMAND_SECTION", "DEPOT_SECTION"};
	private ArrayList<Point> nodeCoords;
	private ArrayList<Integer> nodeDemands;
	private ArrayList<Integer> depots;
	private int numberOfNodes;
	private CVRPSpecification problemSpecification;
	
	public ReaderFromFile(String fileName) throws FileNotFoundException, IOException {
		problemSpecification = new CVRPSpecification();
		String line;
		int lineNumber = 0;
		int lastIntestedParams = 0;
		nodeCoords = new ArrayList<Point>();
		nodeDemands = new ArrayList<>();
		depots = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			while((line = br.readLine()) != null) {
				
				// Reading node params
				if(lineNumber < PARAMS.length) {
					Pattern pattern = Pattern.compile("(" + INTERESTED_PARAMS[lastIntestedParams] + ")\\s*:\\s*(\\d+)");
					Matcher matcher = pattern.matcher(line);

					if(matcher.find()) {
						switch (lastIntestedParams) {
							case 0 :
								this.numberOfNodes = Integer.valueOf(matcher.group(2));
								break;

							case 1:
								problemSpecification.setCapacity(Integer.valueOf(matcher.group(2)));

							default :
								break;
						}
						lastIntestedParams++;
					}
				} else {
					if(lineNumber <= numberOfNodes + PARAMS.length) {
						if(line.matches("[^A-Za-z]+")) {
							Pattern pattern = Pattern.compile("(\\d+)\\s*(\\d+)$");
							Matcher matcher = pattern.matcher(line);
							matcher.matches();
							
							if(matcher.find()) {
								nodeCoords.add(new Point(Integer.valueOf(matcher.group(1).trim()), Integer.valueOf(matcher.group(2).trim())));
							}
							
						}
					}
					
					if(lineNumber >= numberOfNodes + PARAMS.length + 1) {
						if(line.matches("[^A-Za-z]+")) {
							Pattern pattern = Pattern.compile("(\\d+)\\s*$");
							Matcher matcher = pattern.matcher(line);
							matcher.matches();
							if(matcher.find()) {
								nodeDemands.add(Integer.valueOf(matcher.group(1).trim()));
							}
						}
					}
					
					if(lineNumber > numberOfNodes * 2 + PARAMS.length + 1) {
						if(line.matches("[^A-Za-z]+")) {
							line = line.trim();
							if(Integer.valueOf(line) > 0) {
								depots.add(Integer.valueOf(line));
							}
								
						}
					}
				}
				
				lineNumber++;
			}
		}
		
		constructProblemSpecification();
	}
	 
	private void constructProblemSpecification() {
		for(int i = 0; i < nodeCoords.size(); i++) {
			new CVRPClient((int) nodeCoords.get(i).getX(), (int) nodeCoords.get(i).getY(), nodeDemands.get(i));
		}
	}
}

