package com.palyrobotics.frc2017.auto.modes;

import java.util.ArrayList;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.auto.AutoPathLoader;
import com.palyrobotics.frc2017.auto.modes.SidePegAutoMode.SideAutoVariant;
import com.palyrobotics.frc2017.behavior.ParallelRoutine;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.SpatulaDownAutocorrectRoutine;
import com.palyrobotics.frc2017.behavior.routines.SpatulaUpRoutine;
import com.palyrobotics.frc2017.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.*;
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
	private Path mPath, mPostPath;
	
	private final boolean mUseGyro = false;
	private boolean mPostInverted;
	
	private final Gains mShortGains;
	private final Gains.TrajectoryGains mTrajectoryGains;
	private final double backupDistance = 10;	// distance in inches
	private final double pilotWaitTime = 2;	// time in seconds

	private double[] sliderPositions;

	private SequentialRoutine mSequentialRoutine;
	
	public TrajectorySidePegAutoMode(SideAutoVariant direction, TrajectorySidePostVariant postScore) {
		AutoPathLoader.loadPaths();
		mVariant = direction;
		switch (mVariant) {
			case BLUE_BOILER:
				mPath = AutoPathLoader.get("BlueBoiler");
				sliderPositions = new double[]{0, 2, -1};
				mPostInverted = true;
				break;
			case BLUE_LOADING:
				mPath = AutoPathLoader.get("BlueLoading");
				sliderPositions = new double[]{0, 2, -1};
				mPostInverted = false;
				break;
			case RED_LOADING:
				mPath = AutoPathLoader.get("RedLoading");
				sliderPositions = new double[]{0, 2, -1};
				mPostInverted = true;
				break;
			case RED_BOILER:
				mPath = AutoPathLoader.get("RedBoiler");
				sliderPositions = new double[]{0, 2, -1};
				mPostInverted = false;
				break;
		}
		mPostVariant = postScore;
		mTrajectoryGains = Gains.kTrajectoryGains;
		mShortGains = Gains.steikShortDriveMotionMagicGains;
	}

	@Override
	public void prestart() {
		ArrayList<Routine> sequence = new ArrayList<>();
		
		sequence.add(new DriveSensorResetRoutine());
		ArrayList<Routine> parallelSlider = new ArrayList<>();
		parallelSlider.add(new CustomPositioningSliderRoutine(sliderPositions[0]));
		parallelSlider.add(new DrivePathRoutine(mPath, mTrajectoryGains, mUseGyro, false));

		sequence.add(new ParallelRoutine(parallelSlider));
		sequence.add(new DriveSensorResetRoutine());
		sequence.add(new TimeoutRoutine(pilotWaitTime));
		sequence.add(new DriveSensorResetRoutine());
		switch (mPostVariant) {
		case NONE:
			mPostPath = null;
			break;
		case BACKUP:
			mPostPath = null;
			sequence.add(getBackup(sliderPositions[1]));
			break;
		case NEUTRAL_ZONE:
			sequence.add(getDrop());
			mPostPath = AutoPathLoader.get("RightSideDriveToNeutral");
			sequence.add(new DrivePathRoutine(mPostPath, mTrajectoryGains, mUseGyro, mPostInverted));
			break;
		case BOTH:
			mPostPath = AutoPathLoader.get("RightSideDriveToNeutral");
			sequence.add(getBackup(sliderPositions[1]));
			sequence.add(getDrop());
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

	private SequentialRoutine getDrop() {
		DriveSignal driveBackup = DriveSignal.getNeutralSignal();
		double driveBackupSetpoint = -30 * Constants.kDriveTicksPerInch;
		driveBackup.leftMotor.setMotionMagic(driveBackupSetpoint, mShortGains,
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		driveBackup.rightMotor.setMotionMagic(driveBackupSetpoint, mShortGains,
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);

		ArrayList<Routine> sequence = new ArrayList<>();
		ArrayList<Routine> parallelDrop = new ArrayList<>();
		ArrayList<Routine> spatulaSequence = new ArrayList<>();

		parallelDrop.add(new CANTalonRoutine(driveBackup, true));
		spatulaSequence.add(new TimeoutRoutine(1));
		spatulaSequence.add(new SpatulaDownAutocorrectRoutine());
		parallelDrop.add(new SequentialRoutine(spatulaSequence));
		sequence.add(new ParallelRoutine(parallelDrop));
		sequence.add(new EncoderTurnAngleRoutine(180));
		sequence.add(new SpatulaUpRoutine());

		return new SequentialRoutine(sequence);
	}

	@Override
	public Routine getRoutine() {
		return mSequentialRoutine;
	}

	@Override
	public String toString() {
		return "TrajectorySidePegAuto"+mVariant+mPostVariant;
	}
}
