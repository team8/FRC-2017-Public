package com.palyrobotics.frc2017.auto.modes;

import java.util.ArrayList;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.auto.AutoPathLoader;
import com.palyrobotics.frc2017.auto.modes.SidePegAutoMode.SideAutoVariant;
import com.palyrobotics.frc2017.behavior.ParallelRoutine;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.DriveSensorResetRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.GyroMotionMagicTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.CustomPositioningSliderRoutine;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.team254.lib.trajectory.Path;

/**
 * Side peg autonomous using motion profiles
 * @author Ailyn Tong
 */
public class TrajectorySidePegAutoMode extends AutoModeBase {
	public enum TrajectorySidePostVariant {
		NONE,
		BACKUP,
		NEUTRAL_ZONE,
		BOTH
	}
	private final SideAutoVariant mVariant;
	private final TrajectorySidePostVariant mPostVariant;
	private Path mPath, mPath2, mPostPath;
	
	private final boolean mUseGyro = true;
	private boolean mPostInverted;
	
	private final Gains mTrajectoryGains, mShortGains;
	private final double backupDistance = 10;	// distance in inches
	private final double pilotWaitTime = 2;	// time in seconds
	
	private double backupPosition = 2;
	
	private SequentialRoutine mSequentialRoutine;
	
	public TrajectorySidePegAutoMode(SideAutoVariant direction, TrajectorySidePostVariant postScore) {
		AutoPathLoader.loadPaths();
		mVariant = direction;
		mPostVariant = postScore;
		mTrajectoryGains = Gains.steikTrajectory;
		mShortGains = Gains.steikShortDriveMotionMagicGains;
	}

	@Override
	public void prestart() {
		ArrayList<Routine> sequence = new ArrayList<>();
		
		sequence.add(new DriveSensorResetRoutine());
		switch (mVariant) {
		case BLUE_LEFT:
			mPath = AutoPathLoader.get("BlueBoiler");
			mPostInverted = true;
			break;
		case BLUE_RIGHT:
			mPath = AutoPathLoader.get("BlueLoading");
			mPostInverted = false;
			break;
		case RED_LEFT:
			mPath = AutoPathLoader.get("RedLoading");
			mPostInverted = true;
			break;
		case RED_RIGHT:
			mPath = AutoPathLoader.get("RedBoiler");
			mPath2 = AutoPathLoader.get("RedBoilerAirship");
			mPostInverted = false;
			break;
		}
		ArrayList<Routine> parallelSlider = new ArrayList<>();
		parallelSlider.add(new DrivePathRoutine(mPath, mTrajectoryGains, mUseGyro, false));
		parallelSlider.add(new CustomPositioningSliderRoutine(0));

		sequence.add(new ParallelRoutine(parallelSlider));
		sequence.add(new GyroMotionMagicTurnAngleRoutine(60));
		sequence.add(new DriveSensorResetRoutine());
		sequence.add(new DrivePathRoutine(mPath2, mTrajectoryGains, mUseGyro, false));
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
		case NEUTRAL_ZONE:
			mPostPath = AutoPathLoader.get("SideGoToNeutral");
			sequence.add(new DrivePathRoutine(mPostPath, mTrajectoryGains, mUseGyro, mPostInverted));
			break;
		case BOTH:
			mPostPath = AutoPathLoader.get("SideGoToNeutral");
			sequence.add(getBackup(backupPosition));
			sequence.add(new DrivePathRoutine(mPostPath, mTrajectoryGains, mUseGyro, mPostInverted));
			break;
		}
		
		mSequentialRoutine = new SequentialRoutine(sequence);
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
		driveReturn.leftMotor.setMotionMagic(-driveBackupSetpoint+3*Constants.kDriveTicksPerInch, mShortGains, 
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		driveReturn.rightMotor.setMotionMagic(-driveBackupSetpoint+3*Constants.kDriveTicksPerInch, mShortGains, 
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		
		// Create a routine that drives back, then moves the slider while moving back forward
		ArrayList<Routine> sequence = new ArrayList<>();
		ArrayList<Routine> parallelSliding = new ArrayList<>();
		parallelSliding.add(new CANTalonRoutine(driveBackup, true));
		ArrayList<Routine> slideSequence = new ArrayList<>();
		slideSequence.add(new TimeoutRoutine(0.5));
		slideSequence.add(new CustomPositioningSliderRoutine(sliderPosition));
		parallelSliding.add(new SequentialRoutine(slideSequence));
		sequence.add(new ParallelRoutine(parallelSliding));
		sequence.add(new CANTalonRoutine(driveReturn, true));
		sequence.add(new TimeoutRoutine(pilotWaitTime));
		
		return new SequentialRoutine(sequence);
	}

	@Override
	public Routine getRoutine() {
		return mSequentialRoutine;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "TrajectorySidePegAuto"+mVariant+mPostVariant;
	}
}
