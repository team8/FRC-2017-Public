package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.robot.Robot;
import com.palyrobotics.frc2017.util.archive.DriveSignal;

public class BaseLineAutoMode extends AutoModeBase {
	private CANTalonRoutine mRoutine;
	
	private Gains mGains;

	public BaseLineAutoMode() {
		if(Constants.kRobotName == Constants.RobotName.DERICA) {
			mGains = Gains.dericaPosition;
		} else {
			mGains = (Constants.kRobotName == Constants.RobotName.STEIK) ? Gains.steikPosition : Gains.aegirDriveMotionMagicGains;
		}
	}

	@Override
	public Routine getRoutine() {
		// Drive straight until baseline
		DriveSignal driveForward = DriveSignal.getNeutralSignal();
		double setpoint = Constants.kBaseLineDistanceInches * 
				((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
						: Constants.kDriveTicksPerInch);
		driveForward.leftMotor.setMotionMagic(setpoint, mGains,
			Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
		driveForward.rightMotor.setMotionMagic(setpoint, mGains,
				Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
		mRoutine = new CANTalonRoutine(driveForward, true);
		return mRoutine;
	}

	@Override
	public void prestart() {
		System.out.println("Starting Base Line Auto Mode");
	}

	@Override
	public String toString() {
		return "BaseLine";
	}
}
