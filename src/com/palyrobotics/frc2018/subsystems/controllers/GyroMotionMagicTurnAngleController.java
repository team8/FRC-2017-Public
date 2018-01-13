package com.palyrobotics.frc2018.subsystems.controllers;

import com.palyrobotics.frc2018.config.Constants;
import com.palyrobotics.frc2018.config.Gains;
import com.palyrobotics.frc2018.config.RobotState;
import com.palyrobotics.frc2018.robot.Robot;
import com.palyrobotics.frc2018.subsystems.Drive.DriveController;
import com.palyrobotics.frc2018.util.Pose;
import com.palyrobotics.frc2018.util.TalonSRXOutput;
import com.palyrobotics.frc2018.util.archive.DriveSignal;

public class GyroMotionMagicTurnAngleController implements DriveController {
	private Pose mCachedPose;
	private final double mTargetHeading;	// Absolute setpoint in degrees
	private double mLeftTarget, mRightTarget;
	private TalonSRXOutput mLeftOutput, mRightOutput;
	
	private final Gains mGains;
	private final int mCruiseVel, mMaxAccel;
	
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
		
		mGains = Gains.unnamedTurnMotionMagicGains;
		mCruiseVel = (int) Gains.k2018_UnnamedTurnMotionMagicCruiseVelocity;
		mMaxAccel = (int) Gains.k2018_UnnamedTurnMotionMagicMaxAcceleration;
		kInchesPerDegree = Constants.kDriveInchesPerDegree;
		kTicksPerInch = Constants.kDriveTicksPerInch;
		kTolerance = Constants.kAcceptableTurnAngleError;
		
		System.out.println("Current heading: " + mCachedPose.heading);
		System.out.println("Target heading: " + mTargetHeading);
		mLeftTarget = priorSetpoint.leftEnc - (angle * kInchesPerDegree * kTicksPerInch);
//		System.out.println("Left target: " + mLeftTarget);
		mRightTarget = priorSetpoint.rightEnc + (angle * kInchesPerDegree * kTicksPerInch);
//		System.out.println("Right target: " + mRightTarget);
		
		mLeftOutput = new TalonSRXOutput();
		mLeftOutput.setMotionMagic(mLeftTarget, mGains, mCruiseVel, mMaxAccel);
		mRightOutput = new TalonSRXOutput();
		mRightOutput.setMotionMagic(mRightTarget, mGains, mCruiseVel, mMaxAccel);
	}

	@Override
	public DriveSignal update(RobotState state) {
		mCachedPose = state.drivePose;
		double error = mTargetHeading - mCachedPose.heading;
//		System.out.println(mCachedPose.headingVelocity);
		// Compensate for current motion
//		error -= mCachedPose.headingVelocity*Constants.kSubsystemLooperDt;
		mLeftTarget = mCachedPose.leftEnc - (error * kInchesPerDegree * kTicksPerInch);
		mRightTarget = mCachedPose.rightEnc + (error * kInchesPerDegree * kTicksPerInch);
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
		// Wait for controller to be added before finishing routine
//		if (mLeftOutput.getSetpoint() != Robot.getRobotState().leftSetpoint) {
//			System.out.println("Mismatched desired talon and actual talon setpoints! desired, actual");
//			System.out.println("Left: " + mLeftOutput.getSetpoint()+", "+Robot.getRobotState().leftSetpoint);
//			return false;
//		}
//		else if (mRightOutput.getSetpoint() != Robot.getRobotState().rightSetpoint) {
//			System.out.println("Mismatched desired talon and actual talon setpoints! desired, actual");
//			System.out.println("Right: " + mRightOutput.getSetpoint()+", "+Robot.getRobotState().rightSetpoint);
//			return false;
//		}
//		else if (mLeftOutput.getControlMode() != Robot.getRobotState().leftControlMode) {
//			System.out.println("Mismatched desired talon and actual talon states!");
//			System.out.println(mLeftOutput.getControlMode() + ", "+Robot.getRobotState().leftControlMode);
//			return false;
//		}
//		else if (mRightOutput.getControlMode() != Robot.getRobotState().rightControlMode) {
//			System.out.println("Mismatched desired talon and actual talon states!");
//			System.out.println(mRightOutput.getControlMode()+","+Robot.getRobotState().rightControlMode);
//			return false;
//		}
		if (mCachedPose == null) {
			System.out.println("Cached pose is null");
			return false;
		}
		System.out.println("On target "+(Math.abs(Robot.getRobotState().drivePose.heading - mTargetHeading) < 3.4));
		System.out.println(Robot.getRobotState().drivePose.heading);
		return Math.abs(Robot.getRobotState().drivePose.heading - mTargetHeading) < kTolerance
				&& Math.abs(Robot.getRobotState().drivePose.headingVelocity)<0.05;
	}

}
