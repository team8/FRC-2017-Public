package com.palyrobotics.frc2017.util;

import edu.wpi.first.wpilibj.Timer;

/**
 * Double Click Timer
 * @author Ailyn Tong
 * Differentiates between a single and double click.
 * A tolerance window ensures a double click isn't registered immediately after a single click
 * due to fast update times.
 * A WaitTimer ensures a double click can be acted upon and not immediately overridden by a single click.
 * 
 * Everything is handled by twice() for ease of use
 */
public class DoubleClickTimer {
	//Default Values in milliseconds
	public static final double DEFAULT_MIN_TOLERANCE = 200;
	public static final double DEFAULT_MAX_TOLERANCE = 500;
	public static final double DEFAULT_WAIT_TIME = 500;
	
	//Milliseconds
	private double kMinTolerance, kMaxTolerance, kWaitTime;
	
	private double t_0, t_f;
	private boolean twice = false;
	
	private WaitTimer wait;
	
	/**
	 * Default Constructor
	 */
	public DoubleClickTimer() {
		new DoubleClickTimer(DEFAULT_MIN_TOLERANCE, DEFAULT_MAX_TOLERANCE, DEFAULT_WAIT_TIME);
	}
	
	/**
	 * Constructor
	 * @param minTolerance Lower limit for registering double click
	 * @param maxTolerance Upper limit for registering double click
	 */
	public DoubleClickTimer(double minTolerance, double maxTolerance) {
		new DoubleClickTimer(minTolerance, maxTolerance, DEFAULT_WAIT_TIME);
	}
	
	/**
	 * Constructor
	 * @param minTolerance Lower limit for registering double click
	 * @param maxTolerance Upper limit for registering double click
	 * @param waitTime Cooldown time before double click can be registered again
	 */
	public DoubleClickTimer(double minTolerance, double maxTolerance, double waitTime) {
		kMinTolerance = minTolerance;
		kMaxTolerance = maxTolerance;
		kWaitTime = waitTime;
		
		reset();
		
		wait = new WaitTimer(waitTime);
	}
	
	/**
	 * Resets time variables
	 */
	private void reset() {
		t_0 = -1;
		t_f = -1;
	}
	
	/**
	 * Starts the timer if not already started
	 * Otherwise record the current time
	 */
	private void register() {
		if (t_0 == -1) t_0 = Timer.getFPGATimestamp();
		else t_f = Timer.getFPGATimestamp();
	}
	
	/**
	 * Checks if double click has been registered
	 * Also handles reset and wait
	 * @return true if double click, false otherwise
	 */
	public boolean twice() {
		/* If a double click has already been registered,
		 * wait timer will override double click timer */
		if (twice) {
			if (wait.timeout()) {
				twice = false;
				return false;
			}
			else return true;
		}
		
		register();
					
		//Pass if nothing has been registered
		if (t_0 == -1 || t_f == -1) return false;
		
		//Reset timer if tolerance was passed
		if (t_f - t_0 > kMaxTolerance) {
			reset();
			twice = false;
		}
		//Register as double click if within tolerance
		else if (t_f - t_0 >= kMinTolerance && t_f - t_0 <= kMaxTolerance) {
			System.out.println("double click");
			reset();
			twice = true;
		}
		//Register as single click
		else twice = false;
		
		return twice;
	}
}
