package com.palyrobotics.frc2016.config;

import java.util.Optional;

import com.palyrobotics.frc2016.config.Commands.JoystickInput.XboxInput;
import com.palyrobotics.frc2016.subsystems.Drive;

/**
 * Commands represent the desired setpoints and subsystem states for the robot. <br />
 * Store Requests (enum) for each subsystem and setpoints {@link Setpoints} <br />
 * Directly stores real or mock Joystick input in {@link JoystickInput}
 * @author Nihar
 *
 */
public class Commands {
	static Commands commands = new Commands();
	
	public static synchronized Commands getInstance() {
		return commands;
	}
	
	/**
	 * Stores all the subsystem setpoints, including what the currently running Routine is
	 * @author Nihar
	 */
	public static class Setpoints {
		public static final Optional<Double> m_nullopt = Optional.empty();
		// Desired setpoints
		public Optional<Double> auto_align_setpoint = m_nullopt;
		public Optional<Double> encoder_drive_setpoint = m_nullopt;
		public Optional<Double> timer_drive_time_setpoint = m_nullopt;
		public Optional<Double> drive_velocity_setpoint = m_nullopt;
		
		public Routines currentRoutine;
		/**
		 * Resets all the setpoints
		 */
		public void reset() {
			auto_align_setpoint = m_nullopt;
			encoder_drive_setpoint = m_nullopt;
			timer_drive_time_setpoint = m_nullopt;
			drive_velocity_setpoint = m_nullopt;
		}
	}
	// All robot setpoints
	public Setpoints robotSetpoints = new Setpoints();
	
	/**
	 * Class to store Joystick input
	 * Should eventually be extended to contain all buttons
	 * @author Nihar
	 */
	public static class JoystickInput {
		public static class XboxInput extends JoystickInput {
			public double leftX, leftY, rightX, rightY;
			public XboxInput(double leftX, double leftY, double rightX, double rightY) {
				super(leftX, leftY, false);
			}
		}
		public double x,y;
		public boolean triggerPressed;
		public JoystickInput(double x, double y, boolean triggerPressed) {
			this.x = x; this.y = y; this.triggerPressed = triggerPressed;
		}
		public JoystickInput(double x, double y) {
			this.x = x; this.y = y;
		}
	}
	// Stores Joystick values
	public JoystickInput leftStickInput;
	public JoystickInput rightStickInput;
	public XboxInput operatorStickInput;
	// Routine Request
	public static enum Routines {
		TIMER_DRIVE, ENCODER_DRIVE, TURN_ANGLE, AUTO_ALIGN, NONE
	}
	// Routine requests
	public Routines routine_request = Routines.NONE;
	
	// Subsystem requests
	public IntakeRequest intakeRequest;
	public GrabberRequest grabber_request;
	public LatchRequest latch_request;
	public Drive.DriveGear gear_request;
	public ShooterRequest shooter_request;
	public WinchRequest winch_request;
	public PinRequest pin_request;
	public LowGoalShooterRequest low_request;
	
	// Subsystem Requests
	public enum IntakeRequest {
		NONE, INTAKE, EXPEL, STOP
	}
	public enum GrabberRequest {
		GRAB, RELEASE
	}
	public enum ShooterRequest {
		NONE, EXTEND, RETRACT
	}
	public enum LowGoalShooterRequest {
		NONE, SHOOT, LOAD
	}
	public enum LatchRequest {
		NONE, LOCK, UNLOCK
	}
	public enum WinchRequest {
		NONE, WIND, UNWIND
	}
	public enum PinRequest {
		NONE, LOCK, UNLOCK
	}
	// Allows you to cancel routine
	public boolean cancel_current_routine = false;
}