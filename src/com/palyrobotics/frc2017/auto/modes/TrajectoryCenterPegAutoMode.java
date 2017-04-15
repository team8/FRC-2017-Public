package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.auto.AutoPathLoader;
import com.palyrobotics.frc2017.behavior.ParallelRoutine;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.DriveSensorResetRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.CustomPositioningSliderRoutine;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.team254.lib.trajectory.Path;

import static com.palyrobotics.frc2017.auto.modes.archive.CenterPegAutoMode.Alliance;

import java.util.ArrayList;

/**
 * Created by Nihar on 4/13/17.
 */
public class TrajectoryCenterPegAutoMode extends AutoModeBase {
	public enum TrajectoryCenterPostVariant {
		NONE,
		BACKUP,
		NEUTRAL_ZONE_LEFT,
		NEUTRAL_ZONE_RIGHT,
		BOTH_LEFT,
		BOTH_RIGHT
	}
	private final Alliance mVariant;
	private final TrajectoryCenterPostVariant mPostVariant;
	private Path mPath, mPostPath;
	
	private final boolean mUseGyro = true;
	private boolean mPostInverted;
	
	private final Gains mTrajectoryGains, mShortGains;
	private final double backupDistance = 10;	// distance in inches
	private final double pilotWaitTime = 2;	// time in seconds
	
	private double backupPosition = -3;
	
	private SequentialRoutine mSequentialRoutine;
	
	public TrajectoryCenterPegAutoMode(Alliance variant, TrajectoryCenterPostVariant postScore) {
		AutoPathLoader.loadPaths();
		mVariant = variant;
		mPostVariant = postScore;
		mTrajectoryGains = new Gains(Gains.kSteikTrajectoryStraightkP, Gains.kSteikTrajectorykI,
				Gains.kSteikTrajectoryStraightkD, 0, 0 ,0);
		mShortGains = Gains.steikShortDriveMotionMagicGains;
	}

	@Override
	public void prestart() {
		ArrayList<Routine> sequence = new ArrayList<>();
		
		sequence.add(new DriveSensorResetRoutine());
		switch (mVariant) {
		case BLUE:
			mPath = AutoPathLoader.get("BlueCenter");
			break;
		case RED:
			mPath = AutoPathLoader.get("RedCenter");
			break;
		}

		double initialSliderPosition = (mVariant == Alliance.BLUE) ? 0 : 0;
		// Drive forward while moving slider to initial position
		ArrayList<Routine> initialSlide = new ArrayList<>();
		initialSlide.add(new CustomPositioningSliderRoutine(initialSliderPosition));
		initialSlide.add(new DrivePathRoutine(mPath, mTrajectoryGains, mUseGyro, false));
		sequence.add(new ParallelRoutine(initialSlide));

		sequence.add(new TimeoutRoutine(pilotWaitTime));
		sequence.add(new DriveSensorResetRoutine());
		switch (mPostVariant) {
		case NONE:
			mPostPath = null;
			break;
		case BACKUP:
			mPostPath = null;
			sequence.add(getBackup(backupPosition));
			break;
		case NEUTRAL_ZONE_LEFT:
			mPostPath = AutoPathLoader.get("CenterGoToNeutral");
			mPostInverted = true;
			break;
		case NEUTRAL_ZONE_RIGHT:
			mPostPath = AutoPathLoader.get("CenterGoToNeutral");
			mPostInverted = false;
			break;
		case BOTH_LEFT:
			mPostPath = AutoPathLoader.get("CenterGoToNeutral");
			mPostInverted = true;
			sequence.add(getBackup(backupPosition));
			break;
		case BOTH_RIGHT:
			mPostPath = AutoPathLoader.get("CenterGoToNeutral");
			mPostInverted = false;
			sequence.add(getBackup(backupPosition));
			break;
		}
		if (mPostPath != null) {
			sequence.add(new DrivePathRoutine(mPostPath, mTrajectoryGains, mUseGyro, mPostInverted));
		}
		
		mSequentialRoutine = new SequentialRoutine(sequence);
	}

	@Override
	public Routine getRoutine() {
		return mSequentialRoutine;
	}
	/*
	 * GET BACKUP
	 */
	private SequentialRoutine getBackup(double sliderPosition) {
		DriveSignal driveBackup = DriveSignal.getNeutralSignal();
		DriveSignal driveReturn = DriveSignal.getNeutralSignal();

		double driveBackupSetpoint = -backupDistance * Constants.kDriveTicksPerInch;
		driveBackup.leftMotor.setMotionMagic(driveBackupSetpoint, mShortGains, 
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		driveBackup.rightMotor.setMotionMagic(driveBackupSetpoint, mShortGains, 
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);

		// drive forward same distance as backup
		driveReturn.leftMotor.setMotionMagic(-driveBackupSetpoint, mShortGains,
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		driveReturn.rightMotor.setMotionMagic(-driveBackupSetpoint, mShortGains,
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		
		// Create a routine that drives back, then moves the slider while moving back forward
		ArrayList<Routine> sequence = new ArrayList<>();
		ArrayList<Routine> parallelSliding = new ArrayList<>();
		parallelSliding.add(new CANTalonRoutine(driveBackup, true));
		ArrayList<Routine> slideSequence = new ArrayList<>();
		slideSequence.add(new TimeoutRoutine(0.3));
		slideSequence.add(new CustomPositioningSliderRoutine(sliderPosition));
		parallelSliding.add(new SequentialRoutine(slideSequence));
		sequence.add(new ParallelRoutine(parallelSliding));
		sequence.add(new CANTalonRoutine(driveReturn, true));
		sequence.add(new TimeoutRoutine(pilotWaitTime));
		
		return new SequentialRoutine(sequence);
	}

	@Override
	public String toString() {
		return "TrajectoryCenterPegAutoMode";
	}
}
