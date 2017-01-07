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

	private Timer timer = new Timer();
	
	private double runTime;
	private double leftSpeed = 0.5;
	private double rightSpeed = 0.5;
	
	/**
	 * Drives forward at (0.5, 0.5) for a specified number of seconds.
	 * 
	 * @param runTime how long this action runs
	 */
	public DriveTimeRoutine(double runTime) {
		this.runTime = runTime;
	}
	
	/**
	 * Drives forward at (leftSpeed, rightSpeed) for a specified number of seconds.
	 * 
	 * @param runTime how long this action runs
	 * @param leftSpeed the speed of the left motor
	 * @param rightSpeed the speed of the right motor
	 */
	public DriveTimeRoutine(double runTime, double leftSpeed, double rightSpeed) {
		this.runTime = runTime;
		this.leftSpeed = leftSpeed;
		this.rightSpeed = rightSpeed;
	}
	
	@Override
	public boolean isFinished() {
		if(timer.get() < runTime) {
			return false;
		}
		else return true;
	}

	@Override
	public Commands update(Commands commands) {
		System.out.println("Drive Time:" + timer.get());
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
		timer.reset();
		timer.start();
		drive.setOpenLoop(new DriveSignal(leftSpeed, rightSpeed*.95));
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{drive};
	}

	@Override
	public String getName() {
		return "DriveTime";
	}
}