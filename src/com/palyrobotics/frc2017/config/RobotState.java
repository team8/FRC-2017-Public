package com.palyrobotics.frc2017.config;

import com.ctre.CANTalon.TalonControlMode;
import com.palyrobotics.frc2017.robot.team254.lib.util.LegacyPose;
import com.palyrobotics.frc2017.util.Pose;

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

	public TalonControlMode leftControlMode;
	public TalonControlMode rightControlMode;
	
	public double leftSetpoint;
	public double rightStepoint;
	
	// Tracks total voltage from kPDP
	public double totalVoltageDraw;

	// Pose stores drivetrain sensor data
	public Pose drivePose = new Pose(0,0,0,0,0, 0, 0,0,0,0);

	public double sliderPotentiometer;
	public double sliderEncoder;
	public boolean sliderLeftHFX;
	public boolean sliderRightHFX;

	// Climber
	public double climberCurrentDraw;
	public double climberEncoder;
}
