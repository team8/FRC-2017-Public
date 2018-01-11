package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.routines.drive.DrivePathRoutine;
import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.Path.Waypoint;
import com.team254.lib.trajectory.Translation2d;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nihar on 4/5/17.
 */
public class TestTrajectoryAutoMode extends AutoModeBase {
	private Path mPath;

	public TestTrajectoryAutoMode() {
	}
	@Override
	public String toString() {
		return "TestTrajectoryAutoMode";
	}

	@Override
	public void prestart() {
	}

	@Override
	public Routine getRoutine() {
		List<Waypoint> path = new ArrayList<>();
		
		// Path 1: Forward and left
		path.add(new Waypoint(new Translation2d(0,0), 6.0));
		path.add(new Waypoint(new Translation2d(40,0), 6.0));
		path.add(new Waypoint(new Translation2d(40, 83.05), 0.0));
		
		// Path 2: Lollipop
		/*path.add(new Waypoint(new Translation2d(0,0), 6.0));
		path.add(new Waypoint(new Translation2d(60,0), 6.0));
		path.add(new Waypoint(new Translation2d(120, 60), 6.0));
		path.add(new Waypoint(new Translation2d(180,0), 6.0));
		path.add(new Waypoint(new Translation2d(120,-60), 6.0));
		path.add(new Waypoint(new Translation2d(60, 0), 6.0));
		path.add(new Waypoint(new Translation2d(0, 0), 0.0));*/
		return new DrivePathRoutine(new Path(path), false);
	}
}
