package com.capgemini.device;

import com.capgemini.client.Client;
import com.google.gson.JsonObject;

/**
 * 
 * @author cmammado
 *
 */
public class Car implements Runnable {
	
	//How close the vehicles reach their destination
	public static double distlatLong = 0.00005;
	
	//The steps driving the vehicles per iteration
	public static double driveSteps = 0.000001;

	// ID of car/ambulance
	public String id;
	// Latitude of current location
	public double currentLatitude;
	// Longitude of current location
	public double currentLongitude;
	// Latitude of destination location
	private double destinationLatitude;
	// Longitude of destination location
	private double destinationLongitude;

	RouteCalculator routeCalculator;
	private Thread t;
	// Name of thread, each car and ambulance has its own thread
	private String threadname;
	private boolean isEmergency = false;
	// State of ambulance, free or busy
	private boolean isFree = true;

	// ID of emergency
	private String emergencyID;

	public static Client client = new Client();

	/**
	 * This method retrieves the current latitude of car/ambulance
	 * 
	 * @return current latitude of location
	 */
	public double getCurrentLatitude() {
		return currentLatitude;
	}

	/**
	 * This method sets the current latitude of car/ambulance
	 * 
	 * @param currentLatitude
	 *            current latitude to be set
	 */
	public void setCurrentLatitude(double currentLatitude) {
		this.currentLatitude = currentLatitude;
	}

	/**
	 * This method retrieves the current longitude of car/ambulance
	 * 
	 * @return current longitude of location
	 */
	public double getCurrentLongitude() {
		return currentLongitude;
	}

	/**
	 * This methods sets the current longitude of car/ambulance
	 * 
	 * @param currentLongitude
	 *            current longitude to be set
	 */
	public void setCurrentLongitude(double currentLongitude) {
		this.currentLongitude = currentLongitude;
	}

	/**
	 * This method retrieves the latitude of destination
	 * 
	 * @return latitude of destination
	 */
	public double getDestinationLatitude() {
		return destinationLatitude;
	}

	/**
	 * This method sets the latitude of destination
	 * 
	 * @param destinationLatitude
	 *            latitude to be set for destination
	 */
	public void setDestinationLatitude(double destinationLatitude) {
		this.destinationLatitude = destinationLatitude;
	}

	/**
	 * This method retrieves the longitude of destination
	 * 
	 * @return longitude of destination
	 */
	public double getDestinationLongitude() {
		return destinationLongitude;
	}

	/**
	 * This method sets the longitude of destination
	 * 
	 * @param destinationLongitude
	 *            longitude to be set for destination
	 */
	public void setDestinationLongitude(double destinationLongitude) {
		this.destinationLongitude = destinationLongitude;
	}

	/**
	 * Constructor for cars
	 * 
	 * @param currentLatitude
	 *            current latitude of position
	 * @param currentLongitude
	 *            current longitude of position
	 * @param deviceType
	 *            type of device (car or ambulance)
	 * @param threadname
	 *            name of folder of graphhopper files for car
	 */
	public Car(String id, double currentLatitude, double currentLongitude, String folder) {
		this.id = id;
		this.currentLatitude = currentLatitude;
		this.currentLongitude = currentLongitude;
		this.threadname = folder;
		routeCalculator = new RouteCalculator(threadname);
	}

	/**
	 * This method publishes the current location of car/ambulance to the IoT
	 * Foundation.
	 * 
	 * @param currentLatitude
	 *            latitude of current location
	 * @param currentLongitude
	 *            longitude of current location
	 */
	public void publishLocation(double currentLatitude, double currentLongitude) throws Exception {
		JsonObject event = new JsonObject();

		event.addProperty("vin", id);

		// For ambulances add a state of ambulance
		if (this.id.startsWith("ambulance"))
			event.addProperty("isFree", Boolean.toString(isFree));
		// Add ID of emergency
		if (this.emergencyID != null)
			event.addProperty("emergencyID", emergencyID);

		// Add latitude of current location
		event.addProperty("latitude", Double.toString(currentLatitude));
		// Add longitude of current location
		event.addProperty("longitude", Double.toString(currentLongitude));

		// Publish event to IoT
		client.getClient().publishEvent("location", event, 1);

	}

	/*
	 * public void setNewDestination() { this.destinationLatitude = 48.1233 +
	 * (Math.random() * 0.0096); this.destinationLongitude = 11.6519 +
	 * (Math.random() * 0.0306); }
	 */

	/**
	 * This method sets randomly a new destination for cars
	 */
	public void setNewDestination() {

		this.destinationLatitude = MapCoordinatePoint.pointDownRightLatitude + (Math.random() * MapCoordinatePoint.distanceToTop);
		this.destinationLongitude = MapCoordinatePoint.pointDownRightLongitude  + (Math.random() *- MapCoordinatePoint.distanceToLeft);

	}

	/**
	 * This method sets the destination of ambulance to the location of
	 * emergency
	 * 
	 * @param latitude
	 *            latitude of emergency
	 * @param longitude
	 *            longitude of emergency
	 */
	public void setNewDestination(Double latitude, Double longitude, String id) {
		if (!latitude.isNaN())
			this.destinationLatitude = latitude;
		if (!longitude.isNaN())
			this.destinationLongitude = longitude;
		if (!id.trim().isEmpty()) {
			this.emergencyID = id;
			this.isEmergency = true;
		}

	}

	/**
	 * This method starts each car/ambulance in its own thread
	 */
	public void route() {
		if (t == null) {
			t = new Thread(this, threadname);
			t.start();
		}
	}

	/**
	 * This method implements routing algorithm for cars/ambulances
	 */
	@Override
	public void run() {
		while (true) {
			if (!isEmergency) {
					this.setNewDestination();
			}

			else {
				isFree = false;
				isEmergency = false;
			}

			// Calculate the route between the current and destination location
			routeCalculator.calculateRoute(currentLatitude, currentLongitude, destinationLatitude,
					destinationLongitude);
			int i = 0;

			if (routeCalculator.getPointList() != null) {

				while (routeCalculator.getPointList().getSize() > i) {

					double nextPointLatitude = this.routeCalculator.getPointList().getLatitude(i);
					double nextpointLongitude = this.routeCalculator.getPointList().getLongitude(i);

					// Take the difference between current location and the
					// destination
					double distLat = currentLatitude - nextPointLatitude;
					double distLong = currentLongitude - nextpointLongitude;
					
					// While the difference is bigger than the threshold x
					while (Math.abs(distLat) > distlatLong || Math.abs(distLong) > distlatLong) {
						// check if we have to move in lat direction
						if (Math.abs(distLat) > distlatLong) {
							// go xxx steps in direction
							if (distLat < 0) {
								currentLatitude = currentLatitude + driveSteps;
							} else {
								currentLatitude = currentLatitude - driveSteps;
							}
						}
						// check if we have to move in long direction
						if (Math.abs(distLong) > distlatLong) {
							if (distLong < 0) {
								currentLongitude = currentLongitude + driveSteps;
							} else {
								currentLongitude = currentLongitude - driveSteps;
							}
						}

						try {
							this.publishLocation(currentLatitude, currentLongitude);
						} catch (Exception e) {
							e.printStackTrace();
						}
						distLat = currentLatitude - nextPointLatitude;
						distLong = currentLongitude - nextpointLongitude;
						if (isEmergency) {
							break;
						}
					}
					currentLatitude = nextPointLatitude;
					currentLongitude = nextpointLongitude;

					i = i + 1;

					if (isEmergency) {
						break;

					}
				}
			}

			isFree = true;

		}
	}

}
