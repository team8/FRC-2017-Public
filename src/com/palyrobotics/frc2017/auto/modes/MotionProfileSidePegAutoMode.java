package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.auto.AutoPathLoader;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2017.config.Gains;
import com.team254.lib.trajectory.Path;

import static com.palyrobotics.frc2017.auto.modes.SidePegAutoMode.SideAutoVariant;


/**
 * Created by Nihar on 4/13/17.
 */
public class MotionProfileSidePegAutoMode extends AutoModeBase {
	private final SideAutoVariant mVariant;
	private Path mPath;
	private boolean useGyro;


	@Override
	public void prestart() {
	}

	@Override
	public Routine getRoutine() {
		return new DrivePathRoutine(mPath, Gains.steikTrajectory, true, false);
	}

	/**
	 * Construct motion profile side peg auto mode
	 * @param variant Which variant of side peg
	 * @param useGyro Whether to use gyro to correct heading during the path
	 */
	public MotionProfileSidePegAutoMode(SideAutoVariant variant, boolean useGyro) {
		AutoPathLoader.loadPaths();
		this.mVariant = variant;
		switch (mVariant) {
			case RED_LEFT:
				mPath = AutoPathLoader.get("RedLoadingStation");
				break;
			case BLUE_LEFT:
				mPath = AutoPathLoader.get("BlueBoiler");
				break;
			case RED_RIGHT:
				mPath = AutoPathLoader.get("RedBoiler");
				break;
			case BLUE_RIGHT:
				mPath = AutoPathLoader.get("BlueLoadingStation");
				break;
		}
		this.useGyro = useGyro;
	}

	@Override
	public String toString() {
		return "TestTrajectoryAutoMode";
	}
}
