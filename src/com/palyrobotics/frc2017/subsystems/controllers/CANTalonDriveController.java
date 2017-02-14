package com.palyrobotics.frc2017.subsystems.controllers;

import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.team254.lib.util.LegacyPose;
import com.palyrobotics.frc2017.subsystems.Drive;
import com.palyrobotics.frc2017.util.Pose;
import com.palyrobotics.frc2017.util.archive.DriveSignal;

/**
 * Created by Nihar on 2/12/17.
 * @author Nihar
 * Controller used for running an offboard can talon srx control loop
 */
public class CANTalonDriveController implements Drive.DriveController {
	private final DriveSignal mSignal;

	private RobotState mCachedState;
	public CANTalonDriveController(DriveSignal signal) {
		this.mSignal = signal;
	}

	@Override
	public DriveSignal update(RobotState state) {
		mCachedState = state;
		return this.mSignal;
	}

	@Override
	public Pose getSetpoint() {
		Pose output = mCachedState.drivePose.copy();
		switch (mSignal.leftMotor.getControlMode()) {
			case MotionMagic:
				output.leftEnc = mSignal.leftMotor.getSetpoint();
				output.leftEncVelocity = 0;
				output.leftSpeed = 0;
			case Position:
				output.leftEnc = mSignal.leftMotor.getSetpoint();
				output.leftEncVelocity = 0;
				output.leftSpeed = 0;
			case Speed:
				output.leftSpeed = mSignal.leftMotor.getSetpoint();
		}
		switch (mSignal.rightMotor.getControlMode()) {
			case MotionMagic:
				output.rightEnc = mSignal.rightMotor.getSetpoint();
				output.rightEncVelocity = 0;
				output.rightSpeed = 0;
			case Position:
				output.rightEnc = mSignal.rightMotor.getSetpoint();
				output.rightEncVelocity = 0;
				output.rightSpeed = 0;
			case Speed:
				output.rightSpeed = mSignal.rightMotor.getSetpoint();
		}
		return output;
	}

	@Override
	public boolean onTarget() {
		double tolerance = (Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kAcceptableDrivePositionError : Constants.kAcceptableDrivePositionError;
		if (!mCachedState.drivePose.leftError.isPresent() || !mCachedState.drivePose.rightError.isPresent()) {
			System.err.println("Talon closed loop error not found!");
			return false;
		}
		return (mCachedState.drivePose.leftError.get() < tolerance) && (mCachedState.drivePose.rightError.get() < tolerance);
	}
}
