package com.palyrobotics.frc2016.behavior;

import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.util.Subsystem;

/**
 * Created by Nihar on 12/27/16.
 * TODO: run a lambda or run routine?
 */
public class TimedRoutine extends Routine {
	/**
	 *
	 * @param time Time in seconds before routine automatically finishes
	 */
	public TimedRoutine(double time, Routine... routines) {

	}

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
