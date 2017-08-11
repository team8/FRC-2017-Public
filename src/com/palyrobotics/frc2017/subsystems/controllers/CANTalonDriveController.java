package com.palyrobotics.frc2017.subsystems.controllers;

import com.ctre.CANTalon;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.Robot;
import com.palyrobotics.frc2017.subsystems.Drive;
import com.palyrobotics.frc2017.util.CANTalonOutput;
import com.palyrobotics.frc2017.util.Pose;
import com.palyrobotics.frc2017.util.archive.DriveSignal;

/**
 * Created by Nihar on 2/12/17.
 * @author Nihar
 * Controller used for running an offboard can talon srx control loop
 */
public class CANTalonDriveController implements Drive.DriveController {
	private final DriveSignal mSignal;

	private RobotState mCachedState = null;
	
    private String canTableString;

	/**
	 * Constructs a drive controller to store a signal <br />
	 * @param signal
	 */
	public CANTalonDriveController(DriveSignal signal) {
		// Use copy constructors and prevent the signal passed in from being modified externally
		this.mSignal = new DriveSignal(new CANTalonOutput(signal.leftMotor), new CANTalonOutput(signal.rightMotor));
	}

	@Override
	public DriveSignal update(RobotState state) {
		mCachedState = state;
		
		Pose drivePose = Robot.getRobotState().drivePose;
		
		setCanTableString(new double[] {
				(drivePose.leftMotionMagicPos.isPresent()) ? drivePose.leftMotionMagicPos.get() : 0,
				(drivePose.leftMotionMagicVel.isPresent()) ? drivePose.leftMotionMagicVel.get() : 0,
				drivePose.leftEnc,
				drivePose.leftSpeed,
				mSignal.leftMotor.getSetpoint() - drivePose.leftEnc,
				(drivePose.rightMotionMagicPos.isPresent()) ? drivePose.rightMotionMagicPos.get() : 0,
				(drivePose.rightMotionMagicVel.isPresent()) ? drivePose.rightMotionMagicVel.get() : 0,
				drivePose.rightEnc,
				drivePose.rightSpeed,
				mSignal.rightMotor.getSetpoint() - drivePose.rightEnc,
		});

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
		if (mCachedState == null) {
			return false;
		}
		double positionTolerance = (Constants.kRobotName == Constants.RobotName.DERICA) ?
				Constants2016.kAcceptableDrivePositionError : (mSignal.leftMotor.gains.equals(Gains.steikShortDriveMotionMagicGains)) ?
				Constants.kAcceptableShortDrivePositionError : Constants.kAcceptableDrivePositionError;
		double velocityTolerance = (Constants.kRobotName == Constants.RobotName.DERICA) ?
				Constants2016.kAcceptableDriveVelocityError : (mSignal.leftMotor.gains.equals(Gains.steikShortDriveMotionMagicGains)) ?
				Constants.kAcceptableShortDriveVelocityError : Constants.kAcceptableDriveVelocityError;

		// Motion magic is not PID so ignore whether talon closed loop error is around
		if (mSignal.leftMotor.getControlMode() == CANTalon.TalonControlMode.MotionMagic) {
			return (Math.abs(mCachedState.drivePose.leftEnc - mSignal.leftMotor.getSetpoint()) < positionTolerance) &&
					(Math.abs(mCachedState.drivePose.leftSpeed) < velocityTolerance) &&
					(Math.abs(mCachedState.drivePose.rightEnc - mSignal.rightMotor.getSetpoint()) < positionTolerance) &&
					(Math.abs(mCachedState.drivePose.rightSpeed) < velocityTolerance);
		}
		if (!mCachedState.drivePose.leftError.isPresent() || !mCachedState.drivePose.rightError.isPresent()) {
//			System.err.println("Talon closed loop error not found!");
			return false;
		}
		return (Math.abs(mCachedState.drivePose.leftError.get()) < positionTolerance) &&
				(Math.abs(mCachedState.drivePose.rightError.get()) < positionTolerance && 
				Math.abs(mCachedState.drivePose.leftSpeed) < velocityTolerance &&
				Math.abs(mCachedState.drivePose.rightSpeed) < velocityTolerance);
	}
	
	private void setCanTableString(double[] a) {
		canTableString = "";
		for(int i = 0; i < a.length-1; i++) {
			canTableString = canTableString + Double.toString(a[i]) + ", ";
		}
		canTableString = canTableString + Double.toString(a[a.length-1]);
	}

	public String getCanTableString() {
		return this.canTableString;
	}
}