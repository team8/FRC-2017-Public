package com.palyrobotics.frc2016.behavior.routines.auto;

import com.palyrobotics.frc2016.behavior.Routine;
import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.robot.team254.lib.util.DriveSignal;
import com.palyrobotics.frc2016.subsystems.Drive;
import com.palyrobotics.frc2016.util.Subsystem;

public class DriveDistanceRoutine extends Routine {

	private double distance;
	private double maxVel;
	private double startPoint;
	
	public DriveDistanceRoutine(double distance) {
		this.distance = distance;
		this.maxVel = 1;
	}
	
	public DriveDistanceRoutine(double distance, double maxVel) {
		this.distance = distance;
		this.maxVel = maxVel;
	}
	
	@Override
	public void start() {
		System.out.println("Starting EncoderDriveAction");
		drive.resetController();
		startPoint = drive.getPhysicalPose().getRightDistance();
		System.out.println(startPoint);
		//setDistanceSetpoint is relative
		drive.setDistanceSetpoint(distance, maxVel);
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
		drive.setOpenLoop(DriveSignal.NEUTRAL);
		return commands;
	}

	@Override
	public boolean isFinished() {
		return drive.controllerOnTarget();
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{drive};
	}

	@Override
	public String getName() {
		return "DriveDistance";
	}
}