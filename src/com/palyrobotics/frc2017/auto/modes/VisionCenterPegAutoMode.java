package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.auto.AutoPathLoader;
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
 * Center peg autonomous using motion profiles
 * @author Eric Liu
 */
public class VisionCenterPegAutoMode extends AutoModeBase {
	
	public enum CenterPegAutoVariant {
		BLUE,
		RED
	}

	private final CenterPegAutoVariant mVariant;
	private Path mPath;
	
	private final boolean mUseGyro = false;
	private boolean mAndroidConnected = true;

	private final Gains.TrajectoryGains mTrajectoryGains;

	private Routine mSequentialRoutine;
	
	public VisionCenterPegAutoMode(CenterPegAutoVariant direction) {
		AutoPathLoader.loadPaths();
		mVariant = direction;
		switch (mVariant) {
			case BLUE:
				mPath = AutoPathLoader.get("BlueCenterVision");
			break;
			case RED:
				mPath = AutoPathLoader.get("RedCenterVision");
			break;
		}
		mTrajectoryGains = Gains.kStraightTrajectoryGains;
	}

	@Override
	public void prestart() {
		
		// Make sure vision is going
		if(VisionManager.getInstance().isServerStarted()){
			System.out.println("Found vision server.");
		}
		
		System.out.println("Starting " + this.toString() + " Auto Mode");
		Logger.getInstance().logRobotThread("Starting " + this.toString() + " Auto Mode");

		if (!VisionManager.getInstance().isServerStarted() || !CommandExecutor.isNexusConnected()) {
			System.out.println("Vision server not started!");
			Logger.getInstance().logRobotThread("Vision server not detected, fallback to default center peg");

			mAndroidConnected = false;

			switch (mVariant) {
				case BLUE:
					mPath = AutoPathLoader.get("BlueCenter");
					break;
				case RED:
					mPath = AutoPathLoader.get("RedCenter");
					break;
			}
		}
		
		ArrayList<Routine> sequence = new ArrayList<>();
		
		sequence.add(new DriveSensorResetRoutine());
		ArrayList<Routine> parallelSlider = new ArrayList<>();

		if(mAndroidConnected) {
			//move the slider all the way to the left
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

		mSequentialRoutine = new SequentialRoutine(sequence);
	}
	
	private Routine getFirstAttempt() {
		ArrayList<Routine> scoreSequence = new ArrayList<Routine>();

		scoreSequence.add(new VisionSliderRoutine());
		scoreSequence.add(new VisionDriveForwardRoutine(1));

		return new ParallelRoutine(scoreSequence);
	}
	
	@Override
	public Routine getRoutine() {
		return mSequentialRoutine;
	}

	@Override
	public String toString() {
		return "VisionTrajectoryCenterPegAuto " + mVariant;
	}
}