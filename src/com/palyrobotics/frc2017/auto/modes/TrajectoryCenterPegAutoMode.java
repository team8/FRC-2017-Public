package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.auto.AutoPathLoader;
import com.palyrobotics.frc2017.auto.modes.archive.CenterPegAutoMode.Alliance;
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

import java.util.ArrayList;

/**
 * Created by Nihar on 4/13/17.
 */
public class TrajectoryCenterPegAutoMode extends AutoModeBase {
	private final Alliance mVariant;
	private boolean mBackup = true;
	private Path mPath;
	
	private final boolean mUseGyro = true;

	private final Gains mShortGains;
	private final Gains.TrajectoryGains mTrajectoryGains;
	private final double backupDistance = 15;	// distance in inches
	// Store the left/right slider positions
	private double[] sliderPositions;

	private double[] blueSliderPositions = new double[]{2.5, 0, 4.5};
	private double[] redSliderPositions = new double[]{0, 2.5, -3.5};

	private final double pilotWaitTime = 2.5;	// time in seconds before first backup

	private SequentialRoutine mSequentialRoutine;
	
	public TrajectoryCenterPegAutoMode(Alliance variant, boolean backup) {
		AutoPathLoader.loadPaths();
		mVariant = variant;
		mBackup = backup;
		mTrajectoryGains = Gains.kStraightTrajectoryGains;
		switch (mVariant) {
			case BLUE:
				sliderPositions = blueSliderPositions;
				break;
			case RED:
				sliderPositions = redSliderPositions;
				break;
		}
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

		// Drive forward while moving slider to initial position
		ArrayList<Routine> initialSlide = new ArrayList<>();
		initialSlide.add(new CustomPositioningSliderRoutine(sliderPositions[0]));
		initialSlide.add(new DrivePathRoutine(mPath, mTrajectoryGains, mUseGyro, false));
		sequence.add(new ParallelRoutine(initialSlide));

		sequence.add(new TimeoutRoutine(pilotWaitTime));
		sequence.add(new DriveSensorResetRoutine());
	
		if (mBackup) {
			sequence.add(getBackup(sliderPositions[1]));		// Move slider slightly to the left
			sequence.add(new TimeoutRoutine(1.5));
			sequence.add(getBackup(sliderPositions[2]));		// Move slider slightly to the left
			sequence.add(new TimeoutRoutine(pilotWaitTime));
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
		driveReturn.leftMotor.setMotionMagic(-driveBackupSetpoint+2*Constants.kDriveTicksPerInch, mShortGains,
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		driveReturn.rightMotor.setMotionMagic(-driveBackupSetpoint+2*Constants.kDriveTicksPerInch, mShortGains,
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
		sequence.add(new CANTalonRoutine(driveReturn, true, 1));
		sequence.add(new TimeoutRoutine(pilotWaitTime));
		
		return new SequentialRoutine(sequence);
	}

	@Override
	public String toString() {
		return "TrajectoryCenterPegAutoMode";
	}
}
