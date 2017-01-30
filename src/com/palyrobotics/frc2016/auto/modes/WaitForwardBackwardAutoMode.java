package com.palyrobotics.frc2016.auto.modes;

import com.palyrobotics.frc2016.auto.AutoMode;
import com.palyrobotics.frc2016.auto.AutoModeEndedException;
import com.palyrobotics.frc2016.behavior.routines.DriveTimeRoutine;
import com.palyrobotics.frc2016.behavior.routines.auto.DriveDistanceRoutine;
import com.palyrobotics.frc2016.util.DriveSignal;

public class WaitForwardBackwardAutoMode extends AutoMode{
	
	private double mWaitTime;
	private double mDriveTime;
	private double mBackDistance;
	
	/**
	 * 
	 * @param waitTime time to wait before starting
	 * @param driveTime time to drive forwards
	 * @param backDistance distance to back up
	 */
	public WaitForwardBackwardAutoMode(double waitTime, double driveTime, double backDistance) {
		this.mWaitTime = waitTime;
		this.mDriveTime = driveTime;
		this.mBackDistance = backDistance;
	}
	
	protected void routine() throws AutoModeEndedException {
		waitTime(mWaitTime);
		// TODO: No drive power set
		runRoutine(new DriveTimeRoutine(mDriveTime, DriveSignal.getNeutralSignal()));
		waitTime(1);
		runRoutine(new DriveDistanceRoutine(mBackDistance));
	}

	public void prestart() {
		System.out.println("Starting WaitForwardBackwardAutoMode");
	}
	
	@Override
	public String toString() {
		return "WaitForwardBackward";
	}
}