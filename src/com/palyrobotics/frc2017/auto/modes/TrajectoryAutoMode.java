package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoMode;
import com.palyrobotics.frc2017.auto.AutoModeEndedException;
import com.palyrobotics.frc2017.behavior.routines.auto.DrivePathRoutine;
import com.palyrobotics.frc2017.robot.team254.lib.trajectory.Path;
import com.palyrobotics.frc2017.robot.team254.lib.trajectory.Trajectory;
import com.palyrobotics.frc2017.robot.team254.lib.trajectory.Trajectory.Segment;

public class TrajectoryAutoMode extends AutoMode {

	@Override
	protected void routine() throws AutoModeEndedException {
		Segment[] segments = new Segment[50];
		
		for(int i = 0; i < 50; i++) {
			segments[i] = new Segment(2 * i, 0.5, 0.1, 0.1, 0, 10, 50, 50);
		}
		
		Trajectory left = new Trajectory(segments);
		Trajectory right = new Trajectory(segments);
		Trajectory.Pair pair = new Trajectory.Pair(left, right);
		Path path = new Path("low bar", pair);
		runRoutine(new DrivePathRoutine(path));
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void prestart() {
		// TODO Auto-generated method stub
		System.out.println("Starting TrajectoryAutoMode");
	}

}
