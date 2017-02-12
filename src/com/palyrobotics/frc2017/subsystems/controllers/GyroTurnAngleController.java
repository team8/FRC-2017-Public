package com.palyrobotics.frc2017.subsystems.controllers;

import com.palyrobotics.frc2017.util.LegacyDrive;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.util.DriveSignal;
import com.palyrobotics.frc2017.robot.team254.lib.util.Pose;

/**
 * Turns drivetrain using the gyroscope
 * @author Nihar
 *
 */
public class GyroTurnAngleController implements LegacyDrive.DriveController {
	
	private double mMaxVel;
	
	private double mP;
	private double mI;
	private double mD;

	private double mPriorHeading;
	private Pose mSetpoint;
	private Pose mCachedPose;
	
	public GyroTurnAngleController(Pose priorSetpoint, double heading, double maxVel) {
		this.mMaxVel = maxVel;
		mPriorHeading = priorSetpoint.getHeading();
		mSetpoint = priorSetpoint.copy();
		mSetpoint.heading += heading;
		System.out.println("Target angle: " + (mSetpoint.getHeading() - mPriorHeading));
	}
	
	@Override
	public DriveSignal update(Pose pose) {
		mCachedPose = pose;
		mP = mSetpoint.getHeading() - pose.getHeading();
		mI = mI + mP * Constants.kControlLoopsDt;
		
		mD = -pose.getHeadingVelocity();
		
		double leftSpeed = Math.max(-mMaxVel, 
				Math.min(mMaxVel, Constants.kGyroTurnKp * mP + Constants.kGyroTurnKi * mI + Constants.kGyroTurnKd * mD));
		double rightSpeed = -leftSpeed;
//		System.out.println("PID calc: " + Constants.kGyroTurnKp*P + Constants.kGyroTurnKi*I + Constants.kGyroTurnKd*D);
//		System.out.println("Left speed "+leftSpeed);
		DriveSignal output = DriveSignal.getNeutralSignal();
		output.leftMotor.setPercentVBus(leftSpeed);
		output.rightMotor.setPercentVBus(rightSpeed);
		return output;
	}

	@Override
	public Pose getCurrentSetpoint() {
		return mSetpoint;
	}

	@Override
	public boolean onTarget() {
		System.out.println("Gyro Turn angle error: " + String.valueOf(Math.abs(mSetpoint.getHeading() - mCachedPose.getHeading())).substring(0, 4));
		if(Math.abs(mSetpoint.getHeading() - mCachedPose.getHeading()) < Constants.kAcceptableGyroTurnError &&
				Math.abs(mCachedPose.getHeadingVelocity()) < Constants.kAcceptableGyroTurnStopSpeed) {
			System.out.println("Gyro turn on target");
			return true;
		} else return false;
	}

}
