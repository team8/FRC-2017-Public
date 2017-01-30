package com.palyrobotics.frc2017.behavior.routines.auto;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.robot.team254.lib.trajectory.Path;
import com.palyrobotics.frc2017.util.DriveSignal;
import com.palyrobotics.frc2017.util.Subsystem;

public class DrivePathRoutine extends Routine {

	private Path mPath;
	
	public DrivePathRoutine(Path path) {
		this.mPath = path;
	}
	
	@Override
	public boolean finished() {
		return drive.controllerOnTarget();
	}

	@Override
	public Commands update(Commands commands) {
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		System.out.println("DrivePathAction Done");
		drive.setOpenLoop(DriveSignal.getNeutralSignal());
		return commands;
	}

	@Override
	public void start() {
		System.out.println("DrivePathAction Started");
		drive.setPathSetpoint(mPath);
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{drive};
	}

	@Override
	public String getName() {
		return "DrivePathRoutine";
	}

}
