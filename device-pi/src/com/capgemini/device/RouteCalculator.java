package com.capgemini.device;

import java.util.Locale;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.PointList;

/**
 * 
 * @author belmahjo
 *
 */
public class RouteCalculator {

	private PointList pointList;
	private String folder;
	private Double distance;

	/**
	 * This method retrieves the points between 2 locations
	 * 
	 * @return the list of all points between 2 locations
	 */
	public PointList getPointList() {
		return pointList;
	}

	/**
	 * This method retrieves the points between 2 locations
	 * 
	 * @param pointList
	 *            list of points between 2 locations
	 */
	public void setPointList(PointList pointList) {
		this.pointList = pointList;
	}

	/**
	 * Constructor
	 * 
	 * @param folder
	 *            the name of folder of graphhopper files
	 */
	public RouteCalculator(String folder) {
		this.folder = folder;

	}

	/**
	 * This method sets the distance between 2 locations
	 * 
	 * @param distance
	 *            distance to be set
	 */
	public void setDistance(Double distance) {
		this.distance = distance;
	}

	/**
	 * This method retrieves the distance between 2 locations
	 * 
	 * @return distance between start and end position
	 */
	public Double getDistance() {
		return distance;
	}

	/**
	 * This method calculates the route between start and end position
	 * 
	 * @param latFrom
	 *            latitude of starting position
	 * @param lonFrom
	 *            longitude of starting position
	 * @param latTo
	 *            latitude of destination
	 * @param lonTo
	 *            longitude of destination
	 */
	public void calculateRoute(double latFrom, double lonFrom, double latTo, double lonTo) {
		// create singleton
		GraphHopper hopper = new GraphHopper(); //forServer();
		
		
		// Set the location of graphhopper files
		hopper.setGraphHopperLocation(folder);
		
		hopper.setEncodingManager(new EncodingManager("car"));

		// now this can take minutes if it imports or a few seconds for loading
		// of course this is dependent on the area you import
		hopper.importOrLoad();

		// create a request object
		GHRequest req = new GHRequest(latFrom, lonFrom, latTo, lonTo).setWeighting("fastest").setVehicle("car")
				.setLocale(Locale.UK);
		GHResponse rsp = hopper.route(req);

		
		// check for errors
		if (rsp.hasErrors()) {
			System.out.println(this.folder.substring(8) + ": Response has errors. " + rsp.toString());
		
		} else {
			this.setPointList(rsp.getPoints());
			this.setDistance(rsp.getDistance());
		}

	}

}
