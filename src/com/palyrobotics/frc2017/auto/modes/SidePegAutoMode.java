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
 * Created by Nihar on 2/11/17.
 * Goes for side peg autonomous
 * Configured for left vs right
 */
public class SidePegAutoMode extends AutoModeBase {
	// Represents the peg we are going for
	
	Routine mRoutine;
	
	public SidePegAutoMode() {	}

	@Override
	public Routine getRoutine() {
		List<Waypoint> path = new ArrayList<>();

		// Description of this auto mode path.
		path.add(new Waypoint(new Translation2d(0,0), 6.0));
		path.add(new Waypoint(new Translation2d(40, 0), 6.0));
		path.add(new Waypoint(new Translation2d(40 + Math.sqrt(3)*30, 30), 0));
		return new DrivePathRoutine(new Path(path), false);
	}

	@Override
	public void prestart() {
	}
	
	@Override
	public String toString() {
		return "SideAutoMode";
	}
}