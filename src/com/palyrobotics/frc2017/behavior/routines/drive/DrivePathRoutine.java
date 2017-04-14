package com.palyrobotics.frc2017.behavior.routines.drive;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.subsystems.Drive;
import com.palyrobotics.frc2017.util.Subsystem;
import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.Trajectory;

/**
 * Created by Nihar on 4/5/17.
 */
public class DrivePathRoutine extends Routine {
	private Path mPath;
	private boolean mUseGyro;
	/**
	 *
	 * @param path Path to follow
	 * @param useGyro Whether to correct variations using gyro
	 */
	public DrivePathRoutine(Path path, boolean useGyro) {
		this.mPath = path;
		this.mUseGyro = useGyro;
	}

	@Override
	public void start() {
		drive.setTrajectoryController(mPath, mUseGyro);
	}

	@Override
	public Commands update(Commands commands) {
		commands.wantedDriveState = Drive.DriveState.ON_BOARD_CONTROLLER;
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		drive.setNeutral();
		commands.wantedDriveState = Drive.DriveState.NEUTRAL;
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
		return "DrivePathRoutine"+((mUseGyro)?"gyro":"noGyro");
	}
}
