package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.auto.AutoPathLoader;
import com.palyrobotics.frc2017.auto.modes.SidePegAutoMode.SideAutoVariant;
import com.palyrobotics.frc2017.behavior.ParallelRoutine;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.DriveSensorResetRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.CustomPositioningSliderRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.VisionDriveForwardRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.VisionSliderRoutine;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.util.logger.Logger;
import com.palyrobotics.frc2017.vision.CommandExecutor;
import com.palyrobotics.frc2017.vision.VisionManager;
import com.team254.lib.trajectory.Path;

import java.util.ArrayList;

/**
 * Side peg autonomous using motion profiles
 * @author Eric Liu
 */
public class VisionSidePegAutoMode extends AutoModeBase {
	private final SideAutoVariant mVariant;
	private Path mPath;

	private final boolean mUseGyro = false;
	private boolean mAndroidConnected = true;

	private final Gains.TrajectoryGains mTrajectoryGains;

	private Routine mSequentialRoutine;

	public VisionSidePegAutoMode(SideAutoVariant direction) {
		AutoPathLoader.loadPaths();
		mVariant = direction;
		switch (mVariant) {
			case BLUE_BOILER:
				mPath = AutoPathLoader.get("BlueBoilerVision");
				mTrajectoryGains = Gains.kRightTurnTrajectoryGains;
				break;
			case BLUE_LOADING:
				mPath = AutoPathLoader.get("BlueLoadingVision");
				mTrajectoryGains = Gains.kLeftTurnTrajectoryGains;
				break;
			case RED_LOADING:
				mPath = AutoPathLoader.get("RedLoadingVision");
				mTrajectoryGains = Gains.kRightTurnTrajectoryGains;
				break;
			case RED_BOILER:
				mPath = AutoPathLoader.get("RedBoilerVision");
				mTrajectoryGains = Gains.kLeftTurnTrajectoryGains;
				break;
			default:
				mPath = null;
				mTrajectoryGains = null;
				System.err.println("In default case");
				break;
		}
	}

	@Override
	public void prestart() {
		if (!VisionManager.getInstance().isServerStarted() || !CommandExecutor.isNexusConnected()) {
			System.out.println("Vision server not started!");
			Logger.getInstance().logRobotThread("Vision server not detected, fallback to default side peg");

			mAndroidConnected = false;

			//Use non-vision paths if no android connection
			switch (mVariant) {
				case BLUE_BOILER:
					mPath = AutoPathLoader.get("BlueBoiler");
					break;
				case BLUE_LOADING:
					mPath = AutoPathLoader.get("BlueLoading");
					break;
				case RED_LOADING:
					mPath = AutoPathLoader.get("RedLoading");
					break;
				case RED_BOILER:
					mPath = AutoPathLoader.get("RedBoiler");
					break;
			}
		}

		ArrayList<Routine> sequence = new ArrayList<>();

		sequence.add(new DriveSensorResetRoutine());
		ArrayList<Routine> parallelSlider = new ArrayList<>();

		//Slider all the way to the left if android connected, centered if not.
		if(mAndroidConnected) {
			// move the slider all the way to the left
			parallelSlider.add(new CustomPositioningSliderRoutine(-7));
		} else {
			//move the slider to the center
			parallelSlider.add(new CustomPositioningSliderRoutine(0));
		}

		//The motion profile
		parallelSlider.add(new DrivePathRoutine(mPath, mTrajectoryGains, mUseGyro, false));

		//Add the combined motion profile and slider movement to the sequence
		sequence.add(new ParallelRoutine(parallelSlider));

		//If android connected, go for vision
		if(mAndroidConnected) {
			sequence.add(new TimeoutRoutine(1.5));
			sequence.add(getFirstAttempt());
		}

		sequence.add(new DriveSensorResetRoutine());

		mSequentialRoutine = new SequentialRoutine(sequence);
	}

	private Routine getFirstAttempt() {
		ArrayList<Routine> scoreSequence = new ArrayList<Routine>();

		scoreSequence.add(new VisionSliderRoutine());
		scoreSequence.add(new VisionDriveForwardRoutine(1.0));

		return new ParallelRoutine(scoreSequence);
	}

	@Override
	public Routine getRoutine() {
		return mSequentialRoutine;
	}

	@Override
	public String toString() {
		return "VisionTrajectorySidePegAuto " + mVariant;
	}
}