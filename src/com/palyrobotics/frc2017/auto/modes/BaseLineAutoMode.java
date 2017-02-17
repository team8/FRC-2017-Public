package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoMode;
import com.palyrobotics.frc2017.auto.AutoModeEndedException;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.util.CANTalonOutput;
import com.palyrobotics.frc2017.util.archive.DriveSignal;

public class BaseLineAutoMode extends AutoMode {
	private CANTalonRoutine mRoutine;
	
	private CANTalonOutput.CANTalonOutputFactory distanceProvider;

	public BaseLineAutoMode() {
		distanceProvider = new CANTalonOutput.CANTalonOutputFactory();
		distanceProvider.P = 0.1;
		distanceProvider.I = 0.00025;
		distanceProvider.D = 1;
		distanceProvider.F = 0;
		distanceProvider.izone = 750;
		distanceProvider.rampRate = 0;
		
	}

	@Override
	protected void execute() throws AutoModeEndedException {
		runRoutine(mRoutine);
	}

	@Override
	public void prestart() {
		System.out.println("Starting Base Line Auto Mode");
		
		DriveSignal driveForward = DriveSignal.getNeutralSignal();
		driveForward.leftMotor.setPosition(distanceProvider.withDistance(Constants.kBaseLineDistanceInches));
		driveForward.rightMotor.setPosition(distanceProvider.withDistance(Constants.kBaseLineDistanceInches));
		mRoutine = new CANTalonRoutine(driveForward);
	}

	@Override
	public String toString() {
		return "BaseLine";
	}
}
