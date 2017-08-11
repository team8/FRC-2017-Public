package com.palyrobotics.frc2017.config.dashboard;

import com.palyrobotics.frc2017.config.Gains;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class DashboardManager {

	// Usage of cantable or not
	private boolean enableCANTable = true;

	// Allow motion profile gains to be modified over NT
	public final boolean pidTuning = false;

	private static DashboardManager instance = new DashboardManager();
	
	public static final String TABLE_NAME = "RobotTable";
	public static final String CAN_TABLE_NAME = "data_table";
	
	public NetworkTable robotTable;
	private NetworkTable canTable;
	
	public static DashboardManager getInstance() {
		return instance;
	}
	
	private DashboardManager() {}
	
	public void robotInit() {
		try {
			this.robotTable = NetworkTable.getTable(TABLE_NAME);
			Gains.initNetworkTableGains();
			if (enableCANTable) {
				this.canTable = NetworkTable.getTable(CAN_TABLE_NAME);
				NetworkTable.setUpdateRate(.015);
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
		if (!enableCANTable) {
			return;
		}
		if (canTable != null) {
			canTable.putString("status", string+"\n");
		} else {
			// try to reach it again
			try {
				this.canTable = NetworkTable.getTable(CAN_TABLE_NAME);
			}
			catch (UnsatisfiedLinkError e) {
			}
			catch (NoClassDefFoundError e) {}
		}
	}

	/**
	 * Start or stop sending cantable data
	 * @param start true if you want to start sending data
	 */
	public void toggleCANTable(boolean start) {
		if (start) {
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
