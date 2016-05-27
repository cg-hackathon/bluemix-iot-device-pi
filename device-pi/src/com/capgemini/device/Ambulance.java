package com.capgemini.device;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author cmammado
 *
 */
public class Ambulance extends Car {

	// List of all ambulances
	public static List<Ambulance> ambulances = new ArrayList<Ambulance>();

	/**
	 * Constructor for ambulances
	 * 
	 * @param currentLatitude
	 *            current latitude of ambulance
	 * @param currentLongitude
	 *            current longitude of ambulance
	 * @param deviceType
	 *            type of device (car or ambulance)
	 * @param folder
	 *            name of folder of graphhopper files for ambulance
	 */
	public Ambulance(String id, double currentLatitude, double currentLongitude, String folder) {
		super(id, currentLatitude, currentLongitude, folder);
		ambulances.add(this);

	}

}
