package com.palyrobotics.frc2016.behavior;

import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.util.Subsystem;

/**
 * Created by Nihar on 1/22/17.
 * Used for testing {@link RoutineManager} in {@link RoutineManagerTest}
 */
public class SampleRoutine extends Routine {
	@Override
	public void start() {

	}

	@Override
	public Commands update(Commands commands) {
		return null;
	}

	@Override
	public Commands cancel(Commands commands) {
		return null;
	}

	@Override
	public boolean finished() {
		return false;
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[0];
	}

	@Override
	public String getName() {
		return null;
	}
}
