package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoMode;
import com.palyrobotics.frc2017.auto.AutoModeEndedException;

/**
 * Created by Nihar on 1/11/17.
 * An AutoMode for running test autonomous
 */
public class TestAutoMode extends AutoMode {
	// Current configured to test gyro turn angle

	@Override
	protected void execute() throws AutoModeEndedException {
		drive.setTurnAngleSetpoint(90);
//		drive.setDistanceSetpoint(1);
	}

	@Override
	public String toString() {
		return "Test";
	}

	@Override
	public void prestart() {
		System.out.println("Starting TestAutoMode");
	}
}
