package com.palyrobotics.frc2016.behavior;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.util.Subsystem;
import com.palyrobotics.frc2017.subsystems.Drive;

/**
 * Created by Nihar on 1/22/17.
 * Used for testing {@link RoutineManager} in {@link RoutineManagerTest}
 */
public class DrivetrainRoutine extends Routine {
	
	private boolean isFinished;
	
	@Override
	public void start() {
		isFinished = false;
	}

	@Override
	public Commands update(Commands commands) {
		return null;
	}

	@Override
	public Commands cancel(Commands commands) {
		isFinished = true;
		return null;
	}

	@Override
	public boolean finished() {
		return isFinished;
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		Subsystem[] required = {Drive.getInstance()};
		return required;
	}

	@Override
	public String getName() {
		return null;
	}
}