package com.palyrobotics.frc2017.auto;

import com.palyrobotics.frc2017.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2017.subsystems.*;
import com.palyrobotics.frc2017.util.archive.LegacyDrive;

public abstract class AutoMode extends AutoModeBase {
	/**
	 * Keeps access to all subsystems to modify their output and read their status like
	 * {@link LegacyDrive#controllerOnTarget()}
	 */
	protected final Drive drive = Drive.getInstance();
	protected final Slider slider = Slider.getInstance();

	public void waitTime(double seconds) throws AutoModeEndedException {
		runRoutine(new TimeoutRoutine(seconds));
	}
}