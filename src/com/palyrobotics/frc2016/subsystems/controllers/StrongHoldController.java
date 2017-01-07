package com.palyrobotics.frc2016.subsystems.controllers;

import com.palyrobotics.frc2016.robot.team254.lib.util.Controller;
import com.palyrobotics.frc2016.robot.team254.lib.util.SynchronousPID;

import edu.wpi.first.wpilibj.AnalogPotentiometer;

/**
 * A controller to use PID to hold an arm at a target angle
 * Can be enabled/disabled so the subsystem knows to ignore output
 * Starts disabled by default
 * After initial instantiation with P,I,D,tolerance and target angle
 * All those constants can be changed later
 * Call update() to getDriveSignal the new output
 * Can setMaxSpeed() to limit output range
 * 
 * @author Nihar
 *
 */
public class StrongHoldController extends Controller {
	// Tuning constants
	double m_default_max_speed = 0.5;
	// Target angle
	double m_angle;
	// Tolerance for holding position
	double m_tolerance;
	
	// Whether controller is enabled or not
	private boolean m_enabled = false;
	
	// Controller for finding outputs
	private SynchronousPID m_controller;
	
	// Source of angle input
	AnalogPotentiometer m_input_source;
	
	/**
	 * Constructs the PID controller for holding position at an angle
	 * @param kp P constant
	 * @param ki I constant
	 * @param kd D constant
	 * @param angle Target angle
	 */
	public StrongHoldController(double p, double i, double d, double tolerance, AnalogPotentiometer inputSource) {
		m_tolerance = tolerance;
		m_controller = new SynchronousPID(p, i, d);
		m_controller.setOutputRange(-m_default_max_speed, m_default_max_speed);
		m_input_source = inputSource;
	}
	
	/**
	 * Manually set the PID constants
	 * @param p
	 * @param i
	 * @param d
	 */
	public void setPID(double p, double i, double d) {
		m_controller.setPID(p, i, d);
	}
	/**
	 * Set the tolerance (acceptable range of error)
	 */
	public void setTolerance(double tolerance) {
		m_tolerance = tolerance;
	}
	/**
	 * Set max speed, limiting maximum output
	 */
	public void setMaxSpeed(double speed) {
		if(speed < 0) {
			speed = -speed;
		}
		m_controller.setOutputRange(-speed, speed);
	}
	
	// Allow subsystem to enable and disable this controller to know when to ignore output
	public void enable() {
		m_enabled = true;
	}
	public void disable() {
		m_enabled = false;
	}
	public boolean isEnabled() {
		return m_enabled;
	}
	
	/**
	 * Sets a new setpoint for the controller
	 */
	public void setPositionSetpoint(double setpoint) {
		m_controller.setSetpoint(setpoint);
	}
	
	/**
	 * Should be called in a timed thread
	 * @return The output for the motor
	 */
	public double update() {
		return m_controller.calculate(m_input_source.get());
	}
	
	@Override
	/**
	 * @return true when the controller is on target
	 */
	public boolean isOnTarget() {
		return m_controller.onTarget(m_tolerance);
	}

	@Override
	public void reset() {
		
	}
}
