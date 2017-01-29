package com.palyrobotics.frc2016.config;

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

	// Tracks total voltage from kPDP
	public double voltage;

	// Pose stores encoder and gyro data for drivetrain
	public Pose drivePose = new Pose(0,0,0,0,0,0);

	public double climberCurrentDraw;
	public int climberEncoder;
}
