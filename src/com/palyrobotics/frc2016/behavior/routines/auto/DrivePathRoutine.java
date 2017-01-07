package com.palyrobotics.frc2016.behavior.routines.auto;

import com.palyrobotics.frc2016.behavior.Routine;
import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.robot.team254.lib.trajectory.Path;
import com.palyrobotics.frc2016.robot.team254.lib.util.DriveSignal;
import com.palyrobotics.frc2016.util.Subsystem;

public class DrivePathRoutine extends Routine {

	private Path path;
	
	public DrivePathRoutine(Path path) {
		this.path = path;
	}
	
	@Override
	public boolean isFinished() {
		return drive.controllerOnTarget();
	}

	@Override
	public Commands update(Commands commands) {
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		System.out.println("DrivePathAction Done");
		drive.setOpenLoop(DriveSignal.NEUTRAL);
		return commands;
	}

	@Override
	public void start() {
		System.out.println("DrivePathAction Started");
		drive.setPathSetpoint(path);
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{drive};
	}

	@Override
	public String getName() {
		return "DrivePath";
	}

}
