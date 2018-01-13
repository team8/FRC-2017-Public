package com.palyrobotics.frc2018.auto;

import com.palyrobotics.frc2018.behavior.Routine;

public abstract class AutoModeBase {
	protected boolean active = false;

	public abstract String toString();

	// Will be run before the routine is taken
	public abstract void prestart();

	public abstract Routine getRoutine();

	public void stop() {
		active = false;
	}

	public boolean isActive() {
		return active;
	}
}