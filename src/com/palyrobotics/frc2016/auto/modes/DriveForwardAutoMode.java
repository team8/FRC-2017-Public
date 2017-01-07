package com.palyrobotics.frc2016.auto.modes;

import com.palyrobotics.frc2016.auto.AutoMode;
import com.palyrobotics.frc2016.auto.AutoModeEndedException;
import com.palyrobotics.frc2016.behavior.routines.auto.DriveDistanceRoutine;

public class DriveForwardAutoMode extends AutoMode {
	protected void routine() throws AutoModeEndedException {
		waitTime(3);
		runRoutine(new DriveDistanceRoutine(100));
	}

	public void prestart() {
		System.out.println("Starting DriveForwardAutoMode");
	}
	
	@Override
	public String toString() {
		return "DriveForward";
	}
}
