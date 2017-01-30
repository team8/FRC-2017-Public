package com.palyrobotics.frc2017.behavior.routines.auto;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.util.DriveSignal;
import com.palyrobotics.frc2017.util.Subsystem;

public class DriveDistanceRoutine extends Routine {

	private double mDistance;
	private double mMaxVel;
	private double mStartPoint;
	
	public DriveDistanceRoutine(double distance) {
		this.mDistance = distance;
		this.mMaxVel = 1;
	}
	
	public DriveDistanceRoutine(double distance, double maxVel) {
		this.mDistance = distance;
		this.mMaxVel = maxVel;
	}
	
	@Override
	public void start() {
		System.out.println("Starting EncoderDriveAction");
		drive.resetController();
		mStartPoint = drive.getPhysicalPose().getRightDistance();
		System.out.println(mStartPoint);
		//setDistanceSetpoint is relative
		drive.setDistanceSetpoint(mDistance, mMaxVel);
	}

	@Override
	public Commands update(Commands commands) {
//		System.out.println("left encoder: " + drive.getPhysicalPose().getRightDistance());
//		System.out.println("right encoder: " + drive.getPhysicalPose().getLeftDistance());
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		System.out.println("EncoderDriveAction done");
		drive.setOpenLoop(DriveSignal.getNeutralSignal());
		return commands;
	}

	@Override
	public boolean finished() {
		return drive.controllerOnTarget();
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{drive};
	}

	@Override
	public String getName() {
		return "DriveDistanceRoutine";
	}
}