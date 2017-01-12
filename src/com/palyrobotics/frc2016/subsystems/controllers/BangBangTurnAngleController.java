package com.palyrobotics.frc2016.subsystems.controllers;

import com.palyrobotics.frc2016.config.Constants;
import com.palyrobotics.frc2016.subsystems.Drive;
import com.palyrobotics.frc2016.robot.team254.lib.util.DriveSignal;
import com.palyrobotics.frc2016.robot.team254.lib.util.Pose;

/**
 * Turns drivetrain using the gyroscope
 * @author Nihar
 *
 */
public class BangBangTurnAngleController implements Drive.DriveController {
	
	private double maxVel;
	private double heading;
	private Pose currentPose;
	
	public BangBangTurnAngleController(Pose currentPose, double heading, double maxVel) {
		this.maxVel = maxVel;
		this.currentPose = currentPose;
		this.heading = this.currentPose.m_heading + heading;
		System.out.println("Starting Heading" + this.currentPose.m_heading);
	}
	
	@Override
	public DriveSignal update(Pose pose) {
		if (this.onTarget()) {
			return new DriveSignal(0,0);
		}
		currentPose = pose;
		System.out.println("Current Pose: " + currentPose.m_heading);
		if (pose.m_heading < heading) {
			return new DriveSignal(this.maxVel, -this.maxVel);
		}
		return new DriveSignal(-this.maxVel, this.maxVel);
	}

	@Override
	public Pose getCurrentSetpoint() {
		currentPose.m_heading = heading;
		return currentPose;
	}

	@Override
	public boolean onTarget() {
		return Math.abs(currentPose.m_heading-heading) < Constants.kAcceptableGyroTurnError;
	}

}
