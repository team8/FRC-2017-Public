package com.palyrobotics.frc2016.auto;

import com.palyrobotics.frc2016.behavior.routines.auto.TimeoutRoutine;
import com.palyrobotics.frc2016.subsystems.*;

public abstract class AutoMode extends AutoModeBase {
	/**
	 * Keeps access to all subsystems to modify their output and read their status like
	 * {@link Drive#controllerOnTarget()}
	 */
	protected final Drive drive = Drive.getInstance();
	protected final Flippers flippers = Flippers.getInstance();
	protected final Slider slider = Slider.getInstance();
	protected final Spatula spatula = Spatula.getInstance();
	protected final Intake intake = Intake.getInstance();
	protected final Climber climber = Climber.getInstance();

	public void waitTime(double seconds) throws AutoModeEndedException {
		runRoutine(new TimeoutRoutine(seconds));
	}
}
