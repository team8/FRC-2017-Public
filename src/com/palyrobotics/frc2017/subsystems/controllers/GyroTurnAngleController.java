package com.palyrobotics.frc2017.subsystems.controllers;

import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.util.archive.LegacyDrive;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.palyrobotics.frc2017.robot.team254.lib.util.LegacyPose;

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
	private LegacyPose mSetpoint;
	private LegacyPose mCachedPose;
	
	public GyroTurnAngleController(LegacyPose priorSetpoint, double heading, double maxVel) {
		this.mMaxVel = maxVel;
		mPriorHeading = priorSetpoint.getHeading();
		mSetpoint = priorSetpoint.copy();
		mSetpoint.heading += heading;
		System.out.println("Target angle: " + (mSetpoint.getHeading() - mPriorHeading));
	}
	
	@Override
	public DriveSignal update(LegacyPose pose) {
		mCachedPose = pose;
		mP = mSetpoint.getHeading() - pose.getHeading();
		mI = mI + mP * Constants.kControlLoopsDt;
		
		mD = -pose.getHeadingVelocity();
		
		double leftSpeed = Math.max(-mMaxVel, 
				Math.min(mMaxVel, Constants2016.kGyroTurnKp * mP + Constants2016.kGyroTurnKi * mI + Constants2016.kGyroTurnKd * mD));
		double rightSpeed = -leftSpeed;
//		System.out.println("PID calc: " + Constants.kGyroTurnKp*P + Constants.kGyroTurnKi*I + Constants.kGyroTurnKd*D);
//		System.out.println("Left speed "+leftSpeed);
		DriveSignal output = DriveSignal.getNeutralSignal();
		output.leftMotor.setPercentVBus(leftSpeed);
		output.rightMotor.setPercentVBus(rightSpeed);
		return output;
	}

	@Override
	public LegacyPose getCurrentSetpoint() {
		return mSetpoint;
	}

	@Override
	public boolean onTarget() {
		System.out.println("Gyro Turn angle error: " + String.valueOf(Math.abs(mSetpoint.getHeading() - mCachedPose.getHeading())).substring(0, 4));
		if(Math.abs(mSetpoint.getHeading() - mCachedPose.getHeading()) < Constants2016.kAcceptableGyroTurnError &&
				Math.abs(mCachedPose.getHeadingVelocity()) < Constants2016.kAcceptableGyroTurnStopSpeed) {
			System.out.println("Gyro turn on target");
			return true;
		} else return false;
	}

}
