package com.capgemini.device;

import dnl.utils.text.table.TextTable;
/**
 * 
 * @author belmahjo
 *
 */
public class DisplayTableForRaspberryPi implements Runnable {
	// Header of table with device id, current longitude, current latitude,
	// destination longitude and destination latitude
	private String[] tabelheader = { "Dev", "cLong", "cLat", "dLong", "dLat" };
	private int numberBeforeCommaPlusOne = 3;
	private int numberAfterComma = 5;
	private TextTable table;
	private Thread t;
	private String threadname;
	private Car[] carArray;
	private Ambulance[] ambulanceArray;

	public DisplayTableForRaspberryPi(Car[] carArray, Ambulance[] ambulanceArray, String threadname) {
		this.carArray = carArray;
		this.ambulanceArray = ambulanceArray;
		this.threadname = threadname;
	}

	public Object[][] createTableData(Car[] cars, Ambulance[] ambulances, String[] tableheader) {

		Object[][] tableData = new Object[cars.length + ambulances.length][tableheader.length];

		for (int i = 0; i < cars.length; i++) {
			tableData[i][0] = cars[i].id;
			tableData[i][1] = Double.toString(cars[i].getCurrentLongitude()).substring(0,
					numberBeforeCommaPlusOne + numberAfterComma);
			tableData[i][2] = Double.toString(cars[i].getCurrentLatitude()).substring(0,
					numberBeforeCommaPlusOne + numberAfterComma);
			tableData[i][3] = Double.toString(cars[i].getDestinationLongitude()).substring(0,
					numberBeforeCommaPlusOne + numberAfterComma);
			tableData[i][4] = Double.toString(cars[i].getDestinationLatitude()).substring(0,
					numberBeforeCommaPlusOne + numberAfterComma);

		}

		for (int i = 0; i < ambulances.length; i++) {
			tableData[cars.length + i][0] = "amb" + ambulances[i].id.substring(9);
			tableData[cars.length + i][1] = Double.toString(ambulances[i].getCurrentLongitude()).substring(0,
					numberBeforeCommaPlusOne + numberAfterComma);
			tableData[cars.length + i][2] = Double.toString(ambulances[i].getCurrentLatitude()).substring(0,
					numberBeforeCommaPlusOne + numberAfterComma);
			tableData[cars.length + i][3] = Double.toString(ambulances[i].getDestinationLongitude()).substring(0,
					numberBeforeCommaPlusOne + numberAfterComma);
			tableData[cars.length + i][4] = Double.toString(ambulances[i].getDestinationLatitude()).substring(0,
					numberBeforeCommaPlusOne + numberAfterComma);

		}

		return tableData;
	}

	public void printDisplayTableForRaspberryPi() {
		if (t == null) {
			t = new Thread(this, threadname);
			t.start();
		}

	}

	@Override
	public void run() {
		while (true) {
			this.table = new TextTable(tabelheader, createTableData(this.carArray, this.ambulanceArray, tabelheader));
			System.out.println("----------------------------------------------------------");
			table.printTable();
			try {
				Thread.sleep(500); // 1000 milliseconds = second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			System.out.flush();
		}
	}

}
