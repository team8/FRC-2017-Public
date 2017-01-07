package com.palyrobotics.frc2016.auto;

import com.palyrobotics.frc2016.behavior.routines.auto.TimeoutRoutine;
import com.palyrobotics.frc2016.subsystems.*;

public abstract class AutoMode extends AutoModeBase {
	/**
	 * Keeps access to all subsystems to modify their output and read their status like
	 * {@link Drive#controllerOnTarget()} {@link Drive#setGear(Drive.DriveGear)}
	 */
	protected final Drive drive = Drive.getInstance();

	public void waitTime(double seconds) throws AutoModeEndedException {
		runRoutine(new TimeoutRoutine(seconds));
	}
}
