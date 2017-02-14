package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoMode;
import com.palyrobotics.frc2017.auto.AutoModeEndedException;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.util.archive.DriveSignal;

public class BaseLineAutoMode extends AutoMode {
	private CANTalonRoutine mRoutine;
	
	private double kP, kI, kD, kF, kRampRate;
	private int kIzone;

	public BaseLineAutoMode() {
		switch (Constants.kRobotName) {
		case DERICA:
			kP = Constants2016.kDericaPositionkP;
			kI = Constants2016.kDericaPositionkI;
			kD = Constants2016.kDericaPositionkD;
			kF = Constants2016.kDericaPositionkF;
			kIzone = Constants2016.kDericaPositionkIzone;
			kRampRate = Constants2016.kDericaPositionRampRate;
		case AEGIR:
			kP = Constants.kAegirDriveDistancekP;
			kI = Constants.kAegirDriveDistancekI;
			kD = Constants.kAegirDriveDistancekD;
			kF = Constants.kAegirDriveDistancekF;
			kIzone = Constants.kAegirDriveDistancekIzone;
			kRampRate = Constants.kAegirDriveDistancekRampRate;
		case STEIK:
			kP = Constants.kSteikDriveDistancekP;
			kI = Constants.kSteikDriveDistancekI;
			kD = Constants.kSteikDriveDistancekD;
			kF = Constants.kSteikDriveDistancekF;
			kIzone = Constants.kSteikDriveDistancekIzone;
			kRampRate = Constants.kSteikDriveDistancekRampRate;
		}
	}

	@Override
	protected void execute() throws AutoModeEndedException {
		runRoutine(mRoutine);
	}

	@Override
	public void prestart() {
		System.out.println("Starting Base Line Auto Mode");
		
		DriveSignal driveForward = DriveSignal.getNeutralSignal();
		driveForward.leftMotor.setPosition(Constants.kBaseLineDistanceInches, kP, kI, kD, kF, kIzone, kRampRate);
		driveForward.rightMotor.setPosition(Constants.kBaseLineDistanceInches, kP, kI, kD, kF, kIzone, kRampRate);
		mRoutine = new CANTalonRoutine(driveForward);
	}

	@Override
	public String toString() {
		return "BaseLine";
	}
}
