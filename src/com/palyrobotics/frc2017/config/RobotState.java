package com.palyrobotics.frc2017.config;

import com.ctre.CANTalon.TalonControlMode;
import com.palyrobotics.frc2017.util.Pose;

import java.util.Optional;

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
	public GamePeriod gamePeriod = GamePeriod.DISABLED;

	// Drivetrain
	public TalonControlMode leftControlMode = TalonControlMode.Disabled;
	public TalonControlMode rightControlMode = TalonControlMode.Disabled;
	
	public double leftSetpoint = 0;
	public double rightSetpoint = 0;
	
	// Tracks total voltage from kPDP
	public double totalVoltageDraw = 0;

	// Pose stores drivetrain sensor data
	public Pose drivePose = new Pose(0,0,0,0,0, 0, 0,0,0,0);

	// Slider
	public double sliderPotentiometer = 0;
	public double sliderEncoder = 0;
	public double sliderVelocity = 0;
	public Optional<Integer> sliderClosedLoopError = Optional.empty();
	public double sliderPosition = 0;

	// Climber
	public double climberCurrentDraw = 0;
	public double climberEncoder = 0;
}
