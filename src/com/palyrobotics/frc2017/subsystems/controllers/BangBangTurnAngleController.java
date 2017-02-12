package com.palyrobotics.frc2017.subsystems.controllers;

import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.util.LegacyDrive;
import com.palyrobotics.frc2017.util.DriveSignal;
import com.palyrobotics.frc2017.robot.team254.lib.util.Pose;

/**
 * Turns drivetrain using the gyroscope
 * @author Robbie
 *
 */
public class BangBangTurnAngleController implements LegacyDrive.DriveController {
	
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
			return DriveSignal.getNeutralSignal();
		}
		mCurrentPose = pose;
		System.out.println("Current Pose: " + mCurrentPose.heading);
		DriveSignal output = DriveSignal.getNeutralSignal();
		if (pose.heading < mHeading) {
			output.leftMotor.setPercentVBus(this.mMaxVel);
			output.rightMotor.setPercentVBus(-(this.mMaxVel+.05));
		} else {
			output.leftMotor.setPercentVBus(-(this.mMaxVel + .05));
			output.rightMotor.setPercentVBus(this.mMaxVel);
		}
		return output;
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
