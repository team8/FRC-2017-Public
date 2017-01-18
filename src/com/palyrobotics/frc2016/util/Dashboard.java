package com.palyrobotics.frc2016.util;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 * Updates the general non-subsystem information for the dashboard
 * @author Nihar
 * Based on work by Robbie and Bert
 */
public class Dashboard {
	private static Dashboard dashboard = new Dashboard();
	
	/**
	 * Get instance of the driver station
	 * @return the dashboard
	 */
	public static Dashboard getInstance() {
		return dashboard;
	}
	
	private DriverStation mDriverStation = null;
	private boolean mCheckAlliance = true;
	protected NetworkTable dashboardTable = null;
		
	/**
	 * Default Tyr states
	 * Make sure to call in robot init to instantiate network table and driver station
	 */
	public void init() {
		dashboardTable = NetworkTable.getTable("RobotTable");
		this.mDriverStation = DriverStation.getInstance();
		
		dashboardTable.putNumber("match-time", 90);
		dashboardTable.putString("game-period", "DISABLED");
		dashboardTable.putBoolean("brownout-status", false);
		dashboardTable.putNumber("battery", 12.0);
		dashboardTable.putString("alliance", "invalid");
		dashboardTable.putString("accumulatorstate", "Disabled");
		dashboardTable.putString("drivetrainstate", "Disabled");
		dashboardTable.putString("shooterstate", "Disabled");
		dashboardTable.putString("breacherstate", "Disabled");
		dashboardTable.putString("grabberstate", "Disabled");
	}
	
	/**
	 * Update periodically
	 */
	public void update() {
		// Update phase/period of the game
		if (mDriverStation.isAutonomous()) {
			dashboardTable.putString("game-period", "Autonomous");
		} else if (mDriverStation.isDisabled()) {
			dashboardTable.putString("game-period", "Disabled");
		} else if (mDriverStation.isOperatorControl()) {
			dashboardTable.putString("game-period", "Teleop");
		} else if (mDriverStation.isTest()) {
			dashboardTable.putString("game-period", "Test");
		} else {
			dashboardTable.putString("game-period", "Unidentified");
		}
		// Update alliance
		if(mCheckAlliance) {
			switch (mDriverStation.getAlliance()) {
			case Blue:
				dashboardTable.putString("alliance", "blue");
				mCheckAlliance = false;
				break;
			case Red:
				dashboardTable.putString("alliance", "red");
				mCheckAlliance = false;
				break;
			case Invalid:
				dashboardTable.putString("alliance", "invalid");
			}
		}
		// Update information from the driver station
		dashboardTable.putNumber("match-time", mDriverStation.getMatchTime());
		dashboardTable.putBoolean("brownout-status", mDriverStation.isBrownedOut());
		dashboardTable.putNumber("battery", mDriverStation.getBatteryVoltage());

	}
	
	/**
	 * Returns the dashboard selected autopath
	 * @return -1 if nothing, otherwise a string name for the auto path
	 */
	public String getSelectedAutoMode() {
		return dashboardTable.getString("autopath", "-1");
	}
	
	public NetworkTable getTable() {
		return dashboardTable;
	}
}
