package com.palyrobotics.frc2016.behavior.routines;

import com.palyrobotics.frc2016.behavior.Routine;
import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.robot.team254.lib.util.DriveSignal;

import com.palyrobotics.frc2016.util.Subsystem;
import edu.wpi.first.wpilibj.Timer;

public class DriveTimeRoutine extends Routine {
	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{drive};
	}

	private enum DriveTimeRoutineStates {
		START, DRIVING, DONE
	}
	DriveTimeRoutineStates state = DriveTimeRoutineStates.START;
	Timer timer = new Timer();
	// Default values for time and velocity setpoints
	private double mTimeSetpoint;
	private double mVelocitySetpoint;
	
	private boolean mIsNewState = true;
	/**
	 * Constructs with a specified time setpoint and velocity
	 * @param time How long to drive (seconds)
	 * @param velocity What velocity to drive at (0 to 1)
	 */
	public DriveTimeRoutine(double time, double velocity) {
		setTimeSetpoint(time);
		setVelocity(velocity);
	}
	
	/**
	 * Sets the time setpoint that will be used
	 * @param time how long to drive forward in seconds
	 */
	public void setTimeSetpoint(double time) {
		this.mTimeSetpoint = time;
	}
	
	/**
	 * Sets the velocity setpoint
	 * @param velocity target velocity to drive at (0 to 1)
	 * @return true if valid setspeed
	 */
	public boolean setVelocity(double velocity) {
		if(velocity > 0) {
			this.mVelocitySetpoint = velocity;
			return true;
		}
		return false;
	}
	//Routines just change the states of the robotsetpoints, which the behavior manager then moves the physical subsystems based on.
	@Override
	public Commands update(Commands commands) {
		DriveTimeRoutineStates newState = state;
		Commands.Setpoints setpoints = commands.robotSetpoints;
		switch (state) {
		case START:
			// Only set the setpoint the first time the state is START
			if(mIsNewState) {
				timer.reset();
				timer.start();
				drive.setTimerDriveSetpoint(mVelocitySetpoint, mTimeSetpoint);
			}

			setpoints.currentRoutine = Commands.Routines.TIMER_DRIVE;
			newState = DriveTimeRoutineStates.DRIVING;
			break;
		case DRIVING:
			if(drive.controllerOnTarget()) {
				newState = DriveTimeRoutineStates.DONE;
			}
			break;
		case DONE:
			drive.resetController();
			System.out.println("DONE called");
			setpoints.currentRoutine = Commands.Routines.NONE;
			break;
		}

		mIsNewState = false;
		if(newState != state) {
			state = newState;
			mIsNewState = true;
		}
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		state = DriveTimeRoutineStates.DONE;
		System.out.println("Cancelling");
		timer.stop();
		timer.reset();
		drive.resetController();
		drive.setOpenLoop(DriveSignal.NEUTRAL);
		return commands;
	}
	
	@Override
	public void start() {
		drive.resetController();
		timer.reset();
		state = DriveTimeRoutineStates.START;
	}
	
	@Override
	public boolean finished() {
		// allow
		return state == DriveTimeRoutineStates.DONE && mIsNewState==false;
	}

	@Override
	public String getName() {
		return "DriveTimeRoutine";
	}

}
