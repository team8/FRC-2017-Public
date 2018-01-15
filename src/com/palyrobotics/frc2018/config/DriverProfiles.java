package com.palyrobotics.frc2018.config;

public class DriverProfiles {
	/**
	 * Class for configuring the control constants for the robot
	 * Has one static method which configures the constants based off the driver
	 * @author Justin
	 */
	public static void configConstants() {
		switch(Constants.kDriverName) {
		case ERIC:
			Constants.kDriveSensitivity = .70;
			
			Constants.kQuickTurnSensitivity = 0.8;
			Constants.kPreciseQuickTurnSensitivity = 0.35;
			
			Constants.kQuickTurnSensitivityThreshold = 0.90;

			Constants.kQuickStopAccumulatorDecreaseRate = 0.8;

			Constants.kQuickStopAccumulatorDecreaseThreshold = 1.2;
			Constants.kNegativeInertiaScalar = 5.0;
			
			Constants.kAlpha = 0.45;

			Constants.kCyclesUntilStop = 50;

			break;
		}
	}
}