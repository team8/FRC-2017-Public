package com.palyrobotics.frc2017.subsystems.controllers;

import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.Robot;
import com.palyrobotics.frc2017.subsystems.Drive.DriveController;
import com.palyrobotics.frc2017.util.CANTalonOutput;
import com.palyrobotics.frc2017.util.Pose;
import com.palyrobotics.frc2017.util.archive.DriveSignal;

public class GyroMotionMagicTurnAngleController implements DriveController {
	private Pose mCachedPose;
	private final double mTargetHeading;	// Absolute setpoint in degrees
	private double mLeftTarget, mRightTarget;
	private CANTalonOutput mLeftOutput, mRightOutput;
	
	private final Gains mGains;
	private final double mCruiseVel, mMaxAccel;
	
	private final double kInchesPerDegree, kTicksPerInch;
	private final double kTolerance;
		
	/**
	 * 
	 * @param priorSetpoint
	 * @param angle Relative setpoint in degrees
	 */
	public GyroMotionMagicTurnAngleController(Pose priorSetpoint, double angle) {
		mCachedPose = priorSetpoint;
		mTargetHeading = priorSetpoint.heading + angle;
		
		if (Constants.kRobotName == Constants.RobotName.DERICA) {
			mGains = Gains.dericaPosition;
			mCruiseVel = Gains.kDericaTurnMotionMagicCruiseVelocity;
			mMaxAccel = Gains.kDericaTurnMotionMagicCruiseAccel;
			kInchesPerDegree = 1 / Constants2016.kDericaInchesToDegrees;
			kTicksPerInch = Constants2016.kDericaInchesToTicks;
			kTolerance = Constants2016.kAcceptableGyroTurnError;
		} else {
			mGains = Gains.steikTurnMotionMagicGains;
			mCruiseVel = Gains.kSteikTurnMotionMagicCruiseVelocity;
			mMaxAccel = Gains.kSteikTurnMotionMagicMaxAcceleration;
			kInchesPerDegree = Constants.kDriveInchesPerDegree;
			kTicksPerInch = Constants.kDriveTicksPerInch;
			kTolerance = Constants.kAcceptableTurnAngleError;
		}
		
		mLeftTarget = priorSetpoint.leftEnc + (angle * kInchesPerDegree * kTicksPerInch);
		System.out.println("Left target: " + mLeftTarget);
		mRightTarget = priorSetpoint.rightEnc - (angle * kInchesPerDegree * kTicksPerInch);
		System.out.println("Right target: " + mRightTarget);
		
		mLeftOutput = new CANTalonOutput();
		mLeftOutput.setMotionMagic(mLeftTarget, mGains, mCruiseVel, mMaxAccel);
		mRightOutput = new CANTalonOutput();
		mRightOutput.setMotionMagic(mRightTarget, mGains, mCruiseVel, mMaxAccel);
	}

	@Override
	public DriveSignal update(RobotState state) {
		mCachedPose = state.drivePose;
		double error = mTargetHeading - mCachedPose.heading;
		
		mLeftTarget = mCachedPose.leftEnc + (error * kInchesPerDegree * kTicksPerInch);
		mRightTarget = mCachedPose.rightEnc - (error * kInchesPerDegree * kTicksPerInch);
		mLeftOutput.setMotionMagic(mLeftTarget, mGains, mCruiseVel, mMaxAccel);
		mRightOutput.setMotionMagic(mRightTarget, mGains, mCruiseVel, mMaxAccel);

		return new DriveSignal(mLeftOutput, mRightOutput);
	}

	@Override
	public Pose getSetpoint() {
		return new Pose(0, 0, 0, 0, 0, 0, mTargetHeading, 0);
	}

	@Override
	public boolean onTarget() {
		if (Robot.getRobotState().leftSetpoint != mLeftOutput.getSetpoint() || Robot.getRobotState().rightSetpoint != mRightOutput.getSetpoint() ||
				Robot.getRobotState().leftControlMode != mLeftOutput.getControlMode() || Robot.getRobotState().rightControlMode != mRightOutput.getControlMode()) {
			System.out.println("Mismatched desired talon and actual talon states!");
			return false;
		}
		if (mCachedPose == null) {
			System.out.println("Cached pose is null");
			return false;
		}
		
		return Math.abs(Robot.getRobotState().drivePose.heading - mTargetHeading) < kTolerance;
	}

}
