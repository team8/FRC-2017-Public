package com.palyrobotics.frc2017.behavior;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.util.Subsystem;
import com.palyrobotics.frc2017.subsystems.Drive;
import com.palyrobotics.frc2017.subsystems.Intake;


/**
 * Created by Nihar on 1/22/17.
 * Used for testing {@link RoutineManager} in {@link com.palyrobotics.frc2017.behavior.RoutineManagerTest}
 */
public class DrivetrainIntakeRoutine extends Routine {
	
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
		Subsystem[] required = {Drive.getInstance(), Intake.getInstance()};
		return required;
	}

	@Override
	public String getName() {
		return null;
	}
}