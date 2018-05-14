/**
 * QuickHullPanel.java
 *
 * @author Ángel Igareta (angel@igareta.com)
 * @version 1.0
 * @since 21-04-2018
 */
package daa.project.cvrp.graphic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import daa.project.cvrp.IO.ReaderFromFile;
import daa.project.cvrp.problem.CVRPClient;
import daa.project.cvrp.problem.CVRPSolution;

/**
 * Class designed to represent graphically a CVRP Solution. It open a new window
 * where the clients are represented as dots and the routes as lines of
 * different color.
 */
public class CVRPGraphic extends JPanel {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** COLORS */
	/** WINDOW_COLOR */
	private static final Color WINDOW_COLOR = new Color(234, 234, 202);
	/** RGB_MAX */
	private static final int RGB_MAX = 170;
	/** POINT_COLOR */
	private final Color POINT_COLOR = Color.BLUE;
	/** POINT_COLOR */
	private final Color DEPOT_COLOR = Color.RED;

	/** Frame that contains the CVRPPanel */
	private JFrame cvrpWindow;
	/** Array that contains the routes of the CVRP solution. */
	ArrayList<ArrayList<CVRPClient>> routesArray;
	/** Array that contains the routes of the CVRP solution. */
	ArrayList<ArrayList<String>> idArray;

	/** SCREEN_MARGIN represent the distance between the screen and the window. */
	private final int SCREEN_MARGIN = 150;
	/** SCREEN_WIDTH represents the width of the JFrame. */
	private final int SCREEN_WIDTH;
	/** SCREEN_HEIGHT represents the height of the JFrame. */
	private final int SCREEN_HEIGHT;
	/** Window title. */
	private final String WINDOW_TITLE = "CVRP Representation";
	/** Integer necessary to scale the points depending on it's maximum size. */
	private int pointScale;
	/** Diameter of the point. */
	private final int POINT_DIAMETER = 10;
	/** Radius of the point. */
	private final int POINT_RADIUS = POINT_DIAMETER / 2;
	/** Radius of the DEPOT. */
	private final int DEPOT_DIAMETER = 20;

	/**
	 * Method that initialize the Graphic CVRP. For setting a solution use
	 * setSolution method.
	 */
	public CVRPGraphic() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int minimumSize = (int) ((screenSize.getWidth() < screenSize.getHeight()) ? screenSize.getWidth()
				: screenSize.getHeight()) - SCREEN_MARGIN;
		this.SCREEN_WIDTH = minimumSize;
		this.SCREEN_HEIGHT = minimumSize;

		this.cvrpWindow = new JFrame();
		this.cvrpWindow.add(this);
		this.initializeWindow();
	}

	/**
	 * Method to initialize the window.
	 */
	private void initializeWindow() {
		this.cvrpWindow.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		this.cvrpWindow.setTitle(WINDOW_TITLE);
		this.cvrpWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.cvrpWindow.setBackground(WINDOW_COLOR);
		this.cvrpWindow.setLocationRelativeTo(null);
		this.cvrpWindow.setResizable(false);
	}

	/*
	 * (non-Javadoc) Method that paints the client and routes of different color.
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		for (int i = 0; i < this.routesArray.size(); ++i) {
			Random rand = new Random();
			final Color routeColor = new Color(rand.nextInt(RGB_MAX), rand.nextInt(RGB_MAX), rand.nextInt(RGB_MAX));

			g.setColor(POINT_COLOR);
			for (int j = 1; j < this.routesArray.get(i).size(); j++) {
				int xPos1 = this.routesArray.get(i).get(j - 1).getxCoordinate() * pointScale;
				int yPos1 = this.routesArray.get(i).get(j - 1).getyCoordinate() * pointScale;
				int xPos2 = this.routesArray.get(i).get(j).getxCoordinate() * pointScale;
				int yPos2 = this.routesArray.get(i).get(j).getyCoordinate() * pointScale;

				g.setColor(POINT_COLOR);
				g.fillOval(xPos1, yPos1, POINT_DIAMETER, POINT_DIAMETER);
				g.fillOval(xPos2, yPos2, POINT_DIAMETER, POINT_DIAMETER);

				g.setColor(DEPOT_COLOR);
				if ((j - 1) == 0) {
					g.fillOval(xPos1 - (DEPOT_DIAMETER / 2), yPos1 - (DEPOT_DIAMETER / 2), DEPOT_DIAMETER, DEPOT_DIAMETER);
				}

				// IDS
				g.setColor(Color.BLACK);
				g.drawString(String.valueOf(this.idArray.get(i).get(j - 1)), xPos1 - 10, yPos1 - 10);
				g.drawString(String.valueOf(this.idArray.get(i).get(j)), xPos2 - 10, yPos2 - 10);

				g.setColor(routeColor);
				g.drawLine(xPos1 + POINT_RADIUS, yPos1 + POINT_RADIUS, xPos2 + POINT_RADIUS, yPos2 + POINT_RADIUS);

				g.setColor(Color.BLACK);
				Point middlePoint = getMiddlePoint(new Point(xPos1, yPos1), new Point(xPos2, yPos2));
				double distance = CVRPClient.euclideanDistance(this.routesArray.get(i).get(j),
						this.routesArray.get(i).get(j - 1));
				g.drawString(String.format("%.3f", distance), (int) middlePoint.getX() + 10, (int) middlePoint.getY() + 10);
			}
		}
	}

	/**
	 * Method that returns the middlePoint of two points passed by argument.
	 * 
	 * @param leftPoint
	 *          First point.
	 * @param rightPoint
	 *          Second point.
	 * @return Middle Point
	 */
	public Point getMiddlePoint(Point leftPoint, Point rightPoint) {
		double minorX = Math.min(leftPoint.getX(), rightPoint.getX());
		double minorY = Math.min(leftPoint.getY(), rightPoint.getY());

		int midXPos = (int) (Math.abs(leftPoint.getX() - rightPoint.getX()) / 2 + minorX);
		int midYPos = (int) (Math.abs(leftPoint.getY() - rightPoint.getY()) / 2 + minorY);

		return new Point(midXPos, midYPos);
	}

	/**
	 * Method that reset the current solution and assign a new one, saving it's
	 * routes and clients.
	 * 
	 * @param solution
	 */
	public void setSolution(CVRPSolution solution) {
		this.routesArray = new ArrayList<ArrayList<CVRPClient>>();
		this.idArray = new ArrayList<ArrayList<String>>();

		int maximumPosition = 100;

		for (int i = 0; i < solution.getNumberOfRoutes(); ++i) {
			if (solution.getNumberOfClientsInRoute(i) == 0) {
				continue;
			}

			ArrayList<CVRPClient> route = new ArrayList<CVRPClient>();
			ArrayList<String> idSubArray = new ArrayList<String>();

			route.add(solution.getProblemInfo().getDepot());
			idSubArray.add(String.valueOf(solution.getProblemInfo().getDepotID()));

			for (int j = 0; j < solution.getNumberOfClientsInRoute(i); ++j) {
				if (maximumPosition < solution.getClient(i, j).getxCoordinate()) {
					maximumPosition = solution.getClient(i, j).getxCoordinate();
				}
				if (maximumPosition < solution.getClient(i, j).getyCoordinate()) {
					maximumPosition = solution.getClient(i, j).getyCoordinate();
				}

				route.add(solution.getClient(i, j));
				idSubArray.add(String.valueOf(solution.getClientId(i, j)));
			}

			route.add(solution.getProblemInfo().getDepot());
			idSubArray.add(String.valueOf(solution.getProblemInfo().getDepotID()));

			this.routesArray.add(route);
			this.idArray.add(idSubArray);
		}

		this.pointScale = SCREEN_WIDTH / maximumPosition;
		this.cvrpWindow.repaint();
	}

	/**
	 * Method that shows the solution, setting to visible the frame.
	 */
	public void showSolution() {
		this.cvrpWindow.setVisible(true);
		this.cvrpWindow.repaint();
	}

	/**
	 * 
	 */
	public void dispose() {
		this.cvrpWindow.dispose();
	}
}
