package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.auto.AutoPathLoader;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.DriveSensorResetRoutine;
import com.palyrobotics.frc2017.config.Gains;
import com.team254.lib.trajectory.Path;

import java.util.ArrayList;

/**
 * Created by Nihar on 4/5/17.
 */
public class TestTrajectoryAutoMode extends AutoModeBase {
	private Path mPath;
	private String mDesired;

	public TestTrajectoryAutoMode() {
		mDesired = "RightSideDriveToNeutral";
		AutoPathLoader.loadPaths();
		mPath = AutoPathLoader.get(mDesired);
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
		ArrayList<Routine> sequence = new ArrayList<>();
		sequence.add(new DriveSensorResetRoutine());
		sequence.add(new DrivePathRoutine(mPath, Gains.steikTrajectory, false, false));
		sequence.add(new DriveSensorResetRoutine());
//		sequence.add(new DrivePathRoutine(AutoPathLoader.get("GoToNeutral"), Gains.steikTrajectory, true, false));
		return new SequentialRoutine(sequence);
	}
}
