package com.palyrobotics.frc2017.config;

import java.util.Optional;

import com.ctre.CANTalon.TalonControlMode;
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

	// Drivetrain
	public TalonControlMode leftControlMode;
	public TalonControlMode rightControlMode;
	
	public double leftSetpoint;
	public double rightSetpoint;
	
	// Tracks total voltage from kPDP
	public double totalVoltageDraw;

	// Pose stores drivetrain sensor data
	public Pose drivePose = new Pose(0,0,0,0,0, 0, 0,0,0,0);

	// Slider
	public double sliderPotentiometer;
	public double sliderEncoder;
	public double sliderVelocity;
	public Optional<Integer> sliderClosedLoopError;
	public double sliderPosition;

	// Climber
	public double climberCurrentDraw;
	public double climberEncoder;
}
