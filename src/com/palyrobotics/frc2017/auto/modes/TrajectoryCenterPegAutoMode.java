package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.auto.AutoPathLoader;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.routines.drive.DrivePathRoutine;
import com.team254.lib.trajectory.Path;

import static com.palyrobotics.frc2017.auto.modes.archive.CenterPegAutoMode.Alliance;

/**
 * Created by Nihar on 4/13/17.
 */
public class TrajectoryCenterPegAutoMode extends AutoModeBase {
	private final Alliance mVariant;
	private Path mPath;



	@Override
	public void prestart() {
	}

	@Override
	public Routine getRoutine() {
		return new DrivePathRoutine(mPath, true);
	}

	public TrajectoryCenterPegAutoMode(Alliance variant) {
		AutoPathLoader.loadPaths();
		this.mVariant = variant;
		switch (mVariant) {
			case BLUE:
				mPath = AutoPathLoader.get("BlueCenter");
				break;
			case RED:
				mPath = AutoPathLoader.get("RedCenter");
				break;
		}

	}

	@Override
	public String toString() {
		return "TestTrajectoryAutoMode";
	}
}
