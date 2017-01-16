package com.palyrobotics.frc2016.auto.modes;

import com.palyrobotics.frc2016.auto.AutoMode;
import com.palyrobotics.frc2016.auto.AutoModeEndedException;

public class DoNothingAutoMode extends AutoMode {
	@Override
	protected void routine() throws AutoModeEndedException {

	}

	@Override
	public void prestart() {
		System.out.println("Starting DoNothingAutoMode");
	}

	@Override
	public String toString() {
		return "DoNothing";
	}
}
