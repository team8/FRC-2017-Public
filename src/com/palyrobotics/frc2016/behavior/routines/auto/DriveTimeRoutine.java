package com.palyrobotics.frc2016.behavior.routines.auto;

import com.palyrobotics.frc2016.behavior.Routine;
import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.robot.team254.lib.util.DriveSignal;
import com.palyrobotics.frc2016.util.Subsystem;

import edu.wpi.first.wpilibj.Timer;

/**
 * Drives for a specific period of time
 * @author Nihar, Eric
 *
 */
public class DriveTimeRoutine extends Routine {

	private Timer mTimer = new Timer();
	
	private double mRunTime;
	private double mSpeed = 0.5;
	
	/**
	 * Drives forward at (0.5, 0.5) for a specified number of seconds.
	 * 
	 * @param runTime how long this action runs
	 */
	public DriveTimeRoutine(double runTime) {
		this.mRunTime = runTime;
	}
	
	/**
	 * Drives forward at (leftSpeed, rightSpeed) for a specified number of seconds.
	 * 
	 * @param runTime how long this action runs
	 * @param leftSpeed the speed of the left motor
	 * @param rightSpeed the speed of the right motor
	 */
	public DriveTimeRoutine(double runTime, double speed) {
		this.mRunTime = runTime;
		this.mSpeed = speed;
	}
	
	@Override
	public boolean finished() {
		if(mTimer.get() < mRunTime && !drive.controllerOnTarget()) {
			return false;
		}
		else return true;
	}

	@Override
	public Commands update(Commands commands) {
		System.out.println("Drive Time:" + mTimer.get());
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		System.out.println("TimerDriveForwardAction done");
		drive.setOpenLoop(DriveSignal.NEUTRAL);
		return commands;
	}

	@Override
	public void start() {
		System.out.println("Starting TimerDriveForwardAction");
		mTimer.reset();
		mTimer.start();
		drive.setTimerDriveSetpoint(mSpeed, mRunTime);
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{drive};
	}

	@Override
	public String getName() {
		return "DriveTimeRoutine";
	}
}