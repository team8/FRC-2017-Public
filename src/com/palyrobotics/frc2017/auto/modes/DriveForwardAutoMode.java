package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoMode;
import com.palyrobotics.frc2017.auto.AutoModeEndedException;
import com.palyrobotics.frc2017.behavior.routines.auto.DriveDistanceRoutine;

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
