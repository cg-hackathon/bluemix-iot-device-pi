package com.capgemini.client;

import java.io.File;
import java.util.Properties;
import com.capgemini.client.EventCallbackCommand;
import com.ibm.iotf.client.device.DeviceClient;

/**
 * 
 * @author cmammado
 *
 */
public class Client {

	private DeviceClient client;

	public Client() {

		//Provide the device specific data
		Properties options = DeviceClient.parsePropertiesFile(new File("device.prop"));
		try {
			client = new DeviceClient(options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Set command callback
		client.setCommandCallback(new EventCallbackCommand());
		// Connect to Internet of Things Foundation
		client.connect();

	}

	/**
	 * This method retrieves the device client
	 * 
	 * @return device client
	 */
	public DeviceClient getClient() {
		return client;
	}

}
