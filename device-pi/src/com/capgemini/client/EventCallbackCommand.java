package com.capgemini.client;

import com.capgemini.device.Ambulance;
import com.ibm.iotf.client.device.Command;
import com.ibm.iotf.client.device.CommandCallback;
import org.json.JSONObject;

/**
 * 
 * @author cmammado
 *
 */
public class EventCallbackCommand implements CommandCallback {

	// Latitude of the emergency location
	private Double emergencyLatitude;
	// Longitude of the emergency location
	private Double emergencyLongitude;
	// Vehicle identification number (vin) of the closest ambulance to the
	// emergency location
	private String vin;

	/*
	 * This method is automatically called if a new command will be published.
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.iotf.client.device.CommandCallback#processCommand(com.ibm.iotf.
	 * client.device.Command)
	 */
	@Override
	public void processCommand(Command cmd) {

		// Get the payload of command
		JSONObject data = new JSONObject(cmd.getPayload()).getJSONObject("d");

		// Get the vehicle identification number of ambulance which must route
		// to the emergency location
		if (data.has("vin")) {
			vin = data.getString("vin");
		} else
			vin = null;

		// Get the latitude of the emergency location
		if (data.has("emergencyLatitude")) {
			emergencyLatitude = Double.parseDouble(data.getString("emergencyLatitude"));
		} else
			emergencyLatitude = null;

		// Get the longitude of the emergency location
		if (data.has("emergencyLongitude")) {
			emergencyLongitude = Double.parseDouble(data.getString("emergencyLongitude"));
		} else
			emergencyLongitude = null;

		int size = Ambulance.ambulances.size();

		for (int i = 0; i < size; i++) {
			if (vin != null) {
				if (Ambulance.ambulances.get(i).id.equals(vin)) {

					// Set the destination of the closest ambulance to the emergency position
					if (emergencyLatitude != null && emergencyLongitude != null)
						Ambulance.ambulances.get(i).setNewDestination(emergencyLatitude, emergencyLongitude);
				}
			}

		}

	}

}
