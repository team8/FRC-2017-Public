package com.palyrobotics.frc2017.config.dashboard;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class DashboardManager {
	private boolean enableCANTable = true;

	private static DashboardManager instance = new DashboardManager();
	
	public static final String TABLE_NAME = "RobotTable";
	public static final String CAN_TABLE_NAME = "data_table";
	
	private NetworkTable robotTable;
	private NetworkTable canTable;
	
	public static DashboardManager getInstance() {
		return instance;
	}
	
	private DashboardManager() {}
	
	public void robotInit() {
		try {
			this.robotTable = NetworkTable.getTable(TABLE_NAME);
			if (enableCANTable) {
				this.canTable = NetworkTable.getTable(CAN_TABLE_NAME);
			}
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
	
	public void updateCANTable(String string) {
		if (canTable != null) {
			canTable.putString("status", string+"\n");
		}
	}
	
	public void enableCANTable(boolean enable) {
		if (enable) {
			if (canTable != null) {
				canTable.putString("start", "true");
				canTable.putString("end", "false");
			}
		} else {
			if (canTable != null) {
				canTable.putString("start", "false");
				canTable.putString("end", "true");
			}
		}
	}
}
