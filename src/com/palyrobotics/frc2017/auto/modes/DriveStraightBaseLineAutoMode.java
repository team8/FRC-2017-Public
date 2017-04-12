package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.routines.drive.DriveStraightRoutine;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.util.logger.Logger;

/**
 * Base line autonomous using drive straight
 * @author Ailyn Tong
 */
public class DriveStraightBaseLineAutoMode extends AutoModeBase {
	private DriveStraightRoutine mRoutine;
	private final CenterPegAutoMode.Alliance mAlliance;
	
	public DriveStraightBaseLineAutoMode(CenterPegAutoMode.Alliance alliance) {
		mAlliance = alliance;
	}

	@Override
	public Routine getRoutine() {
		// Drive straight until baseline
		double setpoint = ((mAlliance == CenterPegAutoMode.Alliance.BLUE) ? Constants.kBlueBaseLineDistanceInches : Constants.kRedBaseLineDistanceInches)
						* Constants.kDriveTicksPerInch;
		mRoutine = new DriveStraightRoutine(setpoint);
		return mRoutine;
	}
	
	@Override
	public void prestart() {
		System.out.println("Starting " + this.toString() + " Auto Mode");
		Logger.getInstance().logRobotThread("Starting " + this.toString() + " Auto Mode");
	}

	@Override
	public String toString() {
		return (mAlliance == CenterPegAutoMode.Alliance.BLUE) ? "BlueDriveStraightBaseLine" : "RedDriveStraightBaseLine";
	}
}
