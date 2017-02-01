package com.palyrobotics.frc2017.auto;

import com.palyrobotics.frc2017.behavior.routines.auto.TimeoutRoutine;
import com.palyrobotics.frc2017.subsystems.*;

public abstract class AutoMode extends AutoModeBase {
	/**
	 * Keeps access to all subsystems to modify their output and read their status like
	 * {@link Drive#controllerOnTarget()}
	 */
	protected final Drive drive = Drive.getInstance();
	protected final Flippers flippers = Flippers.getInstance();
	protected final Spatula spatula = Spatula.getInstance();
	protected final Intake intake = Intake.getInstance();

	public void waitTime(double seconds) throws AutoModeEndedException {
		runRoutine(new TimeoutRoutine(seconds));
	}
}
