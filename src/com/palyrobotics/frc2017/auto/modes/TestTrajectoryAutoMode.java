package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.DriveSensorResetRoutine;
import com.palyrobotics.frc2017.config.Gains;
import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.Translation2d;
import com.team254.lib.trajectory.Path.Waypoint;
import com.team254.lib.trajectory.Path.Waypoint;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nihar on 4/5/17.
 */
public class TestTrajectoryAutoMode extends AutoModeBase {
	private Path mPath;
	private String mDesired;

	public TestTrajectoryAutoMode() {
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
		List<Waypoint> path = new ArrayList<>();
		path.add(new Waypoint(new Translation2d(0,0), 36.0));
		path.add(new Waypoint(new Translation2d(10,0), 36.0));
		path.add(new Waypoint(new Translation2d(10, 83.05), 36.0));
		return new DrivePathRoutine(new Path(path), false);
	}
}
