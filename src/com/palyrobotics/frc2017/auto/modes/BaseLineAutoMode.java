package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoMode;
import com.palyrobotics.frc2017.auto.AutoModeEndedException;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.robot.Robot;
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
		// Drive straight until baseline
		DriveSignal driveForward = DriveSignal.getNeutralSignal();
		double setpoint = Constants.kBaseLineDistanceInches * 
				((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
						: Constants.kDriveInchesToTicks );
		driveForward.leftMotor.setMotionMagic(setpoint+Robot.getRobotState().drivePose.leftEnc, mGains, 
			Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
		driveForward.rightMotor.setMotionMagic(setpoint+Robot.getRobotState().drivePose.rightEnc, mGains, 
				Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
//		driveForward.leftMotor.setPosition(setpoint, mGains);
//		driveForward.rightMotor.setPosition(setpoint, mGains);
		mRoutine = new CANTalonRoutine(driveForward);
		runRoutine(mRoutine);
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
