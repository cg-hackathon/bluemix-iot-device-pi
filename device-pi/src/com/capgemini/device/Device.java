package com.capgemini.device;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.capgemini.device.Ambulance;
import com.capgemini.device.Car;
import com.capgemini.device.DisplayTableForRaspberryPi;

/**
 * 
 * @author cmammado
 *
 */
public class Device {

	public static void main(String[] args) throws Exception {
		InputStream input = null;
		int numCars = 0;
		int numAmbulances = 0;
		boolean printTable = false;
		
		try {

			input = new FileInputStream("app.conf");
			Properties prop = new Properties();
			prop.load(input);

			// Get the number of cars from app.conf
			numCars = new Integer(prop.getProperty("Cars"));
			// Get the number of ambulances from app.conf
			numAmbulances = new Integer(prop.getProperty("Ambulances"));
			// Get the value of parameter to show tables or not
			printTable = new Boolean(prop.getProperty("Print-Table"));

			// Create graphhopper files for each car and ambulance

			for (int i = 1; i < numCars + 1; i++) {
				File srcDir = new File("graphhopper");
				File destDir = new File("threads/car" + i);

				try {
					FileUtils.copyDirectory(srcDir, destDir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			for (int i = 1; i < numAmbulances + 1; i++) {
				File srcDir = new File("graphhopper");
				File destDir = new File("threads/ambulance" + i);

				try {
					FileUtils.copyDirectory(srcDir, destDir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println("Could not find properties file. " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Could not read properties file. " + e.getMessage());
		} finally {
			if (input != null)
				input.close();
		}

		// The list of all cars
		ArrayList<Car> c = new ArrayList<Car>();
		for (int i = 0; i < numCars; i++) {
			// Create a new car with random current location
			// Car car = new Car("car" + (i + 1), 48.1233 + (Math.random() *
			// 0.0096), 11.6519 + (Math.random() * 0.0306),
			// "threads/car" + (i + 1));
			
			Car car = new Car("car" + (i + 1), 	MapCoordinatePoint.pointDownRightLatitude + (Math.random() * MapCoordinatePoint.distanceToTop),
					MapCoordinatePoint.pointDownRightLongitude  + (Math.random() *- MapCoordinatePoint.distanceToLeft), "threads/car" + (i + 1));
			// Publish the current location of car
			car.publishLocation(car.currentLatitude, car.currentLongitude);
			// Route car to random destination
			car.route();
			// Add car to the list of cars
			c.add(car);
		}

		// The list of all ambulances
		ArrayList<Ambulance> a = new ArrayList<Ambulance>();
		for (int i = 0; i < numAmbulances; i++) {
			// Create a new ambulance with random current location
			Ambulance ambulance = new Ambulance("ambulance" + (i + 1), 	MapCoordinatePoint.pointDownRightLatitude + (Math.random() * MapCoordinatePoint.distanceToTop),
					MapCoordinatePoint.pointDownRightLongitude  + (Math.random() *- MapCoordinatePoint.distanceToLeft), "threads/ambulance" + (i + 1));
			// Publish the current location of ambulance
			ambulance.publishLocation(ambulance.currentLatitude, ambulance.currentLongitude);
			// Route ambulance to random destination
			ambulance.route();
			// Add ambulance to the list of ambulances
			a.add(ambulance);
		}

		// Print current and destination positions of cars and ambulances in a
		// table
		if (printTable) {
			Car[] cars = c.toArray(new Car[c.size()]);
			Ambulance[] ambulances = a.toArray(new Ambulance[a.size()]);
			// print all cars and ambulances on the RaspberryPi display
			DisplayTableForRaspberryPi display = new DisplayTableForRaspberryPi(cars, ambulances, "displayThread");
			display.printDisplayTableForRaspberryPi();
		}
		
		
	}
}
