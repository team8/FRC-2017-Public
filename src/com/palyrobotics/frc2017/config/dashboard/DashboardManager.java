package com.palyrobotics.frc2017.config.dashboard;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class DashboardManager {

	private static DashboardManager instance = new DashboardManager();
	
	private final String TABLE_NAME = "DataTable";
	
	private NetworkTable robotTable;
	
	public static DashboardManager getInstance() {
		return instance;
	}
	
	private DashboardManager() {
		try{
			this.robotTable = NetworkTable.getTable(TABLE_NAME);
		}
		catch (UnsatisfiedLinkError e) {
			// Catch the error that occurs during unit tests.
		}
		catch (NoClassDefFoundError e) {
			
		}
	}
	
	/**
	 * Publishes a KV pair to the Network Table.
	 * @param d	The dashboard value.
	 */
	public void publishKVPair(DashboardValue d) {
		if (robotTable == null) {
			try {
				this.robotTable = NetworkTable.getTable(TABLE_NAME);
			}
			catch (UnsatisfiedLinkError e) {
				// Block the error in a unit test and don't publish the value.
			}
			catch (NoClassDefFoundError e) {}
		}
		
		// If we are now connected
		if (robotTable != null) {
			robotTable.putString(d.getKey(), d.getValue());
		}
	}
}
