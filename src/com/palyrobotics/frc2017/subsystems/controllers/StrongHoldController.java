package com.palyrobotics.frc2017.subsystems.controllers;

import com.palyrobotics.frc2017.robot.team254.lib.util.Controller;
import com.palyrobotics.frc2017.robot.team254.lib.util.SynchronousPID;

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
	double kDefaultMaxSpeed = 0.5;
	// Target angle
	double angle;
	// Tolerance for holding position
	double tolerance;
	
	// Whether controller is enabled or not
	private boolean mEnabled = false;
	
	// Controller for finding outputs
	private SynchronousPID mController;
	
	// Source of angle input
	AnalogPotentiometer inputSource;
	
	/**
	 * Constructs the PID controller for holding position at an angle
	 * @param kp P constant
	 * @param ki I constant
	 * @param kd D constant
	 * @param angle Target angle
	 */
	public StrongHoldController(double p, double i, double d, double tolerance, AnalogPotentiometer inputSource) {
		this.tolerance = tolerance;
		mController = new SynchronousPID(p, i, d);
		mController.setOutputRange(-kDefaultMaxSpeed, kDefaultMaxSpeed);
		this.inputSource = inputSource;
	}
	
	/**
	 * Manually set the PID constants
	 * @param p
	 * @param i
	 * @param d
	 */
	public void setPID(double p, double i, double d) {
		mController.setPID(p, i, d);
	}
	/**
	 * Set the tolerance (acceptable range of error)
	 */
	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}
	/**
	 * Set max speed, limiting maximum output
	 */
	public void setMaxSpeed(double speed) {
		if(speed < 0) {
			speed = -speed;
		}
		mController.setOutputRange(-speed, speed);
	}
	
	// Allow subsystem to enable and disable this controller to know when to ignore output
	public void enable() {
		mEnabled = true;
	}
	public void disable() {
		mEnabled = false;
	}
	public boolean isEnabled() {
		return mEnabled;
	}
	
	/**
	 * Sets a new setpoint for the controller
	 */
	public void setPositionSetpoint(double setpoint) {
		mController.setSetpoint(setpoint);
	}
	
	/**
	 * Should be called in a timed thread
	 * @return The output for the motor
	 */
	public double update() {
		return mController.calculate(inputSource.get());
	}
	
	@Override
	/**
	 * @return true when the controller is on target
	 */
	public boolean isOnTarget() {
		return mController.onTarget(tolerance);
	}

	@Override
	public void reset() {
		
	}
}
