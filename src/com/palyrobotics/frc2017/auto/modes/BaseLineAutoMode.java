package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoMode;
import com.palyrobotics.frc2017.auto.AutoModeEndedException;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.util.archive.DriveSignal;

public class BaseLineAutoMode extends AutoMode {
	private CANTalonRoutine mRoutine;
	
	private Gains mGains;

	public BaseLineAutoMode() {
		if(Constants.kRobotName == Constants.RobotName.DERICA) {
			mGains = Gains.dericaPosition;
		} else {
			mGains = (Constants.kRobotName == Constants.RobotName.STEIK) ? Gains.steikPosition : Gains.aegirPosition;
		}
	}

	@Override
	protected void execute() throws AutoModeEndedException {
		runRoutine(mRoutine);
	}

	@Override
	public void prestart() {
		System.out.println("Starting Base Line Auto Mode");
		// Drive straight until baseline
		DriveSignal driveForward = DriveSignal.getNeutralSignal();
		driveForward.leftMotor.setPosition(Constants.kBaseLineDistanceInches, mGains);
		driveForward.rightMotor.setPosition(Constants.kBaseLineDistanceInches, mGains);
		mRoutine = new CANTalonRoutine(driveForward);
	}

	@Override
	public String toString() {
		return "BaseLine";
	}
}
