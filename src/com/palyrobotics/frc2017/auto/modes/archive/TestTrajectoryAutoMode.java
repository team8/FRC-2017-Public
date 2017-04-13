package com.palyrobotics.frc2017.auto.modes.archive;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.auto.AutoPathLoader;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.routines.drive.DrivePathRoutine;
import com.team254.lib.trajectory.Path;

/**
 * Created by Nihar on 4/5/17.
 */
public class TestTrajectoryAutoMode extends AutoModeBase {
	private Path mPath;
	private String mDesired;

	public TestTrajectoryAutoMode(String pathName) {
		AutoPathLoader.loadPaths();
		mDesired = pathName;
		mPath = AutoPathLoader.get(pathName);
	}
	@Override
	public String toString() {
		return "TestTrajectoryAutoMode"+mDesired;
	}

	@Override
	public void prestart() {
	}

	@Override
	public Routine getRoutine() {
		return new DrivePathRoutine(mPath, true);
	}
}
