package com.palyrobotics.frc2016.config;

import com.palyrobotics.frc2016.subsystems.Drive.DriveGear;
import com.palyrobotics.frc2016.robot.team254.lib.util.Pose;

/**
 * Holds all hardware input, such as sensors. <br />
 * Can be simulated
 * @author Nihar
 *
 */
public class RobotState {
	public enum GamePeriod {
		AUTO, TELEOP, DISABLED
	}
	// Updated by autoInit, teleopInit, disabledInit
	public GamePeriod gamePeriod;

	// Tracks total voltage from PDP
	public double voltage;

	// No sensors on the robot currently
	public DriveGear gear;

	// Collects drivetrain sensor data into {@link Pose}
	public Pose getDrivePose() {
		return new Pose(0, 0, 0, 0, 0, 0);
	}

	// DIO Values
	public double left_encoder;
	public double right_encoder;
	public double shooter_potentiometer;
}
