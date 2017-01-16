package com.palyrobotics.frc2016.subsystems.controllers;

import com.palyrobotics.frc2016.config.Constants;
import com.palyrobotics.frc2016.subsystems.Drive;
import com.palyrobotics.frc2016.robot.team254.lib.util.DriveSignal;
import com.palyrobotics.frc2016.robot.team254.lib.util.Pose;

/**
 * Turns drivetrain using the gyroscope
 * @author Robbie
 *
 */
public class BangBangTurnAngleController implements Drive.DriveController {
	
	private double mMaxVel;
	private double mHeading;
	private Pose mCurrentPose;
	
	public BangBangTurnAngleController(Pose currentPose, double heading) {
		this.mMaxVel = Constants.kTurnAngleSpeed;
		this.mCurrentPose = currentPose;
		this.mHeading = this.mCurrentPose.heading + heading;
		System.out.println("Starting Heading" + this.mCurrentPose.heading);
	}
	
	@Override
	public DriveSignal update(Pose pose) {
		if (this.onTarget()) {
			return new DriveSignal(0,0);
		}
		mCurrentPose = pose;
		System.out.println("Current Pose: " + mCurrentPose.heading);
		if (pose.heading < mHeading) {
			return new DriveSignal(this.mMaxVel, -(this.mMaxVel+.05));
		}
		return new DriveSignal(-(this.mMaxVel + .05), this.mMaxVel);
	}

	@Override
	public Pose getCurrentSetpoint() {
		mCurrentPose.heading = mHeading;
		return mCurrentPose;
	}

	@Override
	public boolean onTarget() {
		return Math.abs(mCurrentPose.heading - mHeading) < Constants.kAcceptableGyroTurnError;
	}

}
