package com.palyrobotics.frc2016.behavior.routines;

import com.palyrobotics.frc2016.behavior.Routine;
import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.robot.team254.lib.util.DriveSignal;

import com.palyrobotics.frc2016.util.Subsystem;

import java.util.Optional;

public class DriveTimeRoutine extends Routine {
	private long mStartTime;
	private long mEndTime;
	private DriveSignal mDrivePower;

	/**
	 * Constructs with a specified time setpoint and velocity
	 * @param time How long to drive (seconds)
	 * @param drivePower Drive signal to output (left/right speeds -1 to 1)
	 */
	public DriveTimeRoutine(double time, DriveSignal drivePower) {
		// Keeps the offset prepared, when routine starts, will add System.currentTime
		mEndTime = (long) (1000*time);
		this.mDrivePower = drivePower;
	}

	@Override
	public void start() {
		drive.resetController();
		mStartTime = System.currentTimeMillis();
		mEndTime += System.currentTimeMillis();
	}

	//Routines just change the states of the robotsetpoints, which the behavior manager then moves the physical subsystems based on.
	@Override
	public Commands update(Commands commands) {
		commands.wantedDriveState = Commands.WantedDriveState.ROUTINE;
		commands.robotSetpoints.drivePowerSetpoint = Optional.of(mDrivePower);
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		System.out.println("Cancelling");
		drive.resetController();
		drive.setOpenLoop(DriveSignal.NEUTRAL);
		return commands;
	}

	@Override
	public boolean finished() {
		// Finish after the time is up
		return (System.currentTimeMillis() >= mEndTime);
	}

	@Override
	public String getName() {
		return "DriveTimeRoutine";
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{drive};
	}
}