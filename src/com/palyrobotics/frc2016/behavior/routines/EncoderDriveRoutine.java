package com.palyrobotics.frc2016.behavior.routines;

import java.util.Optional;

import com.palyrobotics.frc2016.behavior.Routine;
import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.robot.team254.lib.util.DriveSignal;

import com.palyrobotics.frc2016.util.Subsystem;
import edu.wpi.first.wpilibj.Timer;

/**
 * Drives forward a specified distance
 * Uses right encoder to determine if distance is reached
 * Times out after specified seconds, default m_default_timeout
 * @author Nihar
 */
public class EncoderDriveRoutine extends Routine {
	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{drive};
	}
	/*
	 * START = Set new drive setpoint
	 * DRIVING = Waiting to reach drive setpoint
	 * DONE = reached target or not operating
	 */
	private enum EncoderDriveRoutineStates {
		START, DRIVING, DONE
	}

	EncoderDriveRoutineStates m_state = EncoderDriveRoutineStates.START;
	private double m_distance;
	private double m_velocity_setpoint;
	private final double m_default_velocity_setpoint = 0.5;
	
	// Timeout after x seconds
	private double m_timeout;
	private final double m_default_timeout = 5;
	Timer m_timer = new Timer();

	private boolean m_is_new_state = true;

	/**
	 * Constructs with target distance
	 * Uses default timeout and default velocity setpoint
	 * @param distance Target distance to travel
	 */
	public EncoderDriveRoutine(double distance) {
		this.m_distance = distance;
		this.m_timeout = m_default_timeout;
		setVelocity(m_default_velocity_setpoint);
	}
	
	/**
	 * Constructs with specified timeout
	 * @param distance Target distance to travel
	 * @param timeout Time (seconds) before timeout
	 */
	public EncoderDriveRoutine(double distance, int timeout) {
		this.m_distance = distance;
		this.m_timeout = timeout;
		setVelocity(m_default_velocity_setpoint);
	}
	
	/**
	 * 
	 * @param distance Target distance to travel
	 * @param timeout Time (seconds) before timeout
	 * @param velocity Target velocity
	 */
	public EncoderDriveRoutine(double distance, double timeout, double velocity) {
		this.m_distance = distance;
		this.m_timeout = timeout;
		setVelocity(velocity);
	}
	
	/**
	 * Sets the velocity setpoint
	 * @param velocity target velocity to drive at (0 to 1)
	 * @return true if valid setspeed
	 */
	public boolean setVelocity(double velocity) {
		if(velocity > 0) {
			this.m_velocity_setpoint = velocity;
			return true;
		}
		return false;
	}

	//Routines just change the states of the robotsetpoints, which the behavior manager then moves the physical subsystems based on.
	@Override
	public Commands update(Commands commands) {
		EncoderDriveRoutineStates new_state = m_state;
		Commands.Setpoints setpoints = commands.robotSetpoints;
		switch (m_state) {
		case START:
			m_timer.reset();
			m_timer.start();
			// Only set the setpoint the first time the state is START 
			if(m_is_new_state) {
				setpoints.encoder_drive_setpoint = Optional.of(m_distance);
				setpoints.drive_velocity_setpoint = Optional.of(m_velocity_setpoint);
			}

			setpoints.currentRoutine = Commands.Routines.ENCODER_DRIVE;
			new_state = EncoderDriveRoutineStates.DRIVING;
			drive.setDistanceSetpoint(setpoints.encoder_drive_setpoint.get());
			break;
		case DRIVING:
			setpoints.encoder_drive_setpoint = Optional.of(m_distance);
			setpoints.drive_velocity_setpoint = Optional.of(m_velocity_setpoint);
			if(drive.getPhysicalPose().getRightDistance() > m_distance) {
				new_state = EncoderDriveRoutineStates.DONE;
			}
			if(m_timer.get() > m_timeout) {
				new_state = EncoderDriveRoutineStates.DONE;
			}
			break;
		case DONE:
			drive.resetController();
			break;
		}
		
		m_is_new_state = false;
		if(new_state != m_state) {
			m_state = new_state;
			m_timer.reset();
			m_is_new_state = true;
		}
		
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		m_state = EncoderDriveRoutineStates.DONE;
		m_timer.stop();
		m_timer.reset();
		drive.setOpenLoop(DriveSignal.NEUTRAL);
		drive.resetController();
		return commands;
	}

	@Override
	public void start() {
		drive.resetController();
		m_timer.reset();
	}

	@Override
	public boolean isFinished() {
		return m_state == EncoderDriveRoutineStates.DONE;
	}

	@Override
	public String getName() {
		return "Encoder Drive Forward";
	}

}
