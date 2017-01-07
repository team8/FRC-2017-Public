package com.palyrobotics.frc2016.auto.modes;

import com.palyrobotics.frc2016.auto.AutoMode;
import com.palyrobotics.frc2016.auto.AutoModeEndedException;
import com.palyrobotics.frc2016.behavior.routines.auto.DriveDistanceRoutine;
import com.palyrobotics.frc2016.behavior.routines.auto.DriveTimeRoutine;

public class WaitForwardBackwardAutoMode extends AutoMode{
	
	private double waitTime;
	private double driveTime;
	private double backDistance;
	
	/**
	 * 
	 * @param waitTime time to wait before starting
	 * @param driveTime time to drive forwards
	 * @param backDistance distance to back up
	 */
	public WaitForwardBackwardAutoMode(double waitTime, double driveTime, double backDistance) {
		this.waitTime = waitTime;
		this.driveTime = driveTime;
		this.backDistance = backDistance;
	}
	
	protected void routine() throws AutoModeEndedException {
		waitTime(waitTime);
		runRoutine(new DriveTimeRoutine(driveTime));
		waitTime(1);
		runRoutine(new DriveDistanceRoutine(backDistance));
	}

	public void prestart() {
		System.out.println("Starting WaitForwardBackwardAutoMode");
	}
	
	@Override
	public String toString() {
		return "WaitForwardBackward";
	}
}