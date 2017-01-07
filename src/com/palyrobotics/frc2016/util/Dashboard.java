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
	private boolean checkAlliance = true;
	protected NetworkTable mDashboardTable = null;
		
	/**
	 * Default Tyr states
	 * Make sure to call in robot init to instantiate network table and driver station
	 */
	public void init() {
		mDashboardTable = NetworkTable.getTable("RobotTable");
		this.mDriverStation = DriverStation.getInstance();
		
		mDashboardTable.putNumber("match-time", 90);
		mDashboardTable.putString("game-period", "DISABLED");
		mDashboardTable.putBoolean("brownout-status", false);
		mDashboardTable.putNumber("battery", 12.0);
		mDashboardTable.putString("alliance", "invalid");
		mDashboardTable.putString("accumulatorstate", "Disabled");
		mDashboardTable.putString("drivetrainstate", "Disabled");
		mDashboardTable.putString("shooterstate", "Disabled");
		mDashboardTable.putString("breacherstate", "Disabled");
		mDashboardTable.putString("grabberstate", "Disabled");
	}
	
	/**
	 * Update periodically
	 */
	public void update() {
		// Update phase/period of the game
		if (mDriverStation.isAutonomous()) {
			mDashboardTable.putString("game-period", "Autonomous");
		} else if (mDriverStation.isDisabled()) {
			mDashboardTable.putString("game-period", "Disabled");
		} else if (mDriverStation.isOperatorControl()) {
			mDashboardTable.putString("game-period", "Teleop");
		} else if (mDriverStation.isTest()) {
			mDashboardTable.putString("game-period", "Test");
		} else {
			mDashboardTable.putString("game-period", "Unidentified");
		}
		// Update alliance
		if(checkAlliance) {
			switch (mDriverStation.getAlliance()) {
			case Blue:
				mDashboardTable.putString("alliance", "blue");
				checkAlliance = false;
				break;
			case Red:
				mDashboardTable.putString("alliance", "red");
				checkAlliance = false;
				break;
			case Invalid:
				mDashboardTable.putString("alliance", "invalid");
			}
		}
		// Update information from the driver station
		mDashboardTable.putNumber("match-time", mDriverStation.getMatchTime());
		mDashboardTable.putBoolean("brownout-status", mDriverStation.isBrownedOut());
		mDashboardTable.putNumber("battery", mDriverStation.getBatteryVoltage());

	}
	
	/**
	 * Returns the dashboard selected autopath
	 * @return -1 if nothing, otherwise a string name for the auto path
	 */
	public String getSelectedAutoMode() {
		return mDashboardTable.getString("autopath", "-1");
	}
	
	public NetworkTable getTable() {
		return mDashboardTable;
	}
}
