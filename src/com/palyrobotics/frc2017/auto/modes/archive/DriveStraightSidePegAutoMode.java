package com.palyrobotics.frc2017.auto.modes.archive;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.behavior.ParallelRoutine;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.SpatulaDownAutocorrectRoutine;
import com.palyrobotics.frc2017.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.*;
import com.palyrobotics.frc2017.behavior.routines.scoring.CustomPositioningSliderRoutine;
import com.palyrobotics.frc2017.config.AutoDistances;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.util.logger.Logger;

import java.util.ArrayList;

/**
 * Side peg autonomous using drive straight and gyro turn angle
 * Includes backup or drive to neutral zone
 * @author Ailyn Tong
 */
public class DriveStraightSidePegAutoMode extends AutoModeBase {
	// Store configuration on construction
	private final SidePegAutoMode.SideAutoVariant mVariant;
	private final SidePegAutoMode.SideAutoPostVariant mPostVariant;
	
	private SequentialRoutine mSequentialRoutine;

	private final double pilotWaitTime = 2.5; // time in seconds
	private final double backupDistance = 10;	// distance in inches
	private final double neutralZoneDistance = 12 * 14;	// distance in inches

	private double initialSliderPosition = 0;	// slider position in inches
	private double backupPosition = 0;	// slider position in inches

	public DriveStraightSidePegAutoMode(SidePegAutoMode.SideAutoVariant direction, SidePegAutoMode.SideAutoPostVariant postScore) {
		mVariant = direction;
		mPostVariant = postScore;
	}

	@Override
	public Routine getRoutine() {
		return mSequentialRoutine;
	}

	@Override
	public void prestart() {
		System.out.println("Starting " + this.toString() + " Auto Mode");
		Logger.getInstance().logRobotThread("Starting " + this.toString() + " Auto Mode");

		ArrayList<Routine> sequence = new ArrayList<>();

		sequence.add(getDriveForward());

		// NOTE: switch case falling, split by lefts vs rights
		switch (mVariant) {
		case RED_LEFT:
			backupPosition = 3;
			sequence.add(new GyroMotionMagicTurnAngleRoutine(Constants.kSidePegTurnAngleDegrees));
			break;
		case BLUE_LEFT:
			backupPosition = 1; //-3
			sequence.add(new GyroMotionMagicTurnAngleRoutine(Constants.kSidePegTurnAngleDegrees));
			break;
		case RED_RIGHT:
			backupPosition = 2;
			sequence.add(new GyroMotionMagicTurnAngleRoutine(-Constants.kSidePegTurnAngleDegrees));
			break;
		case BLUE_RIGHT:
			backupPosition = -4;
			sequence.add(new GyroMotionMagicTurnAngleRoutine(-Constants.kSidePegTurnAngleDegrees));
			break;
		}
		
		sequence.add(getDriveToAirship());
		sequence.add(new TimeoutRoutine(pilotWaitTime));	// Wait 2.5s so pilot can pull gear out

		switch (mPostVariant) {
		case NONE:
			break;
		case BACKUP:
			sequence.add(getBackup(backupPosition));
			break;
		case NEUTRAL_ZONE:
			sequence.add(getDriveToNeutralZone());
			break;
		}

		mSequentialRoutine = new SequentialRoutine(sequence);
	}
	/*
	 * DRIVE FORWARD
	 */
	private Routine getDriveForward() {
		// For Red Left = Blue Right, Red Right = Blue Left
		double driveForwardSetpoint;
		switch (mVariant) {
		// loading station side
		case RED_LEFT:
			initialSliderPosition = 0;
			driveForwardSetpoint = AutoDistances.kRedLoadingStationForwardDistanceInches;
			break;
		case BLUE_RIGHT:
			initialSliderPosition = -1.5;
			driveForwardSetpoint = AutoDistances.kBlueLoadingStationForwardDistanceInches;
			break;
		// boiler side
		case RED_RIGHT:
			initialSliderPosition = 0;
			driveForwardSetpoint = AutoDistances.kRedBoilerForwardDistanceInches;
			break;
		case BLUE_LEFT:
			initialSliderPosition = 2.5;
			driveForwardSetpoint = AutoDistances.kBlueBoilerForwardDistanceInches;
			break;
		default:
			System.err.println("What in tarnation no side peg distance");
			driveForwardSetpoint = 0;
			break;
		}
		
		Logger.getInstance().logRobotThread("Drive forward", driveForwardSetpoint);
		ArrayList<Routine> initialSlide = new ArrayList<>();
		initialSlide.add(new DriveStraightRoutine(driveForwardSetpoint));
		initialSlide.add(new CustomPositioningSliderRoutine(initialSliderPosition));
		return new ParallelRoutine(initialSlide);
	}
	/*
	 * GET AIRSHIP
	 */
	private DriveStraightRoutine getDriveToAirship() {
		double driveToAirshipSetpoint = 0;
		switch (mVariant) {
		// loading station side
		case RED_LEFT:
			driveToAirshipSetpoint = AutoDistances.kRedLoadingStationAirshipDistanceInches;
			break;
		case BLUE_RIGHT:
			driveToAirshipSetpoint = AutoDistances.kBlueLoadingStationAirshipDistanceInches;
			break;
		// boiler side
		case RED_RIGHT:
			driveToAirshipSetpoint = AutoDistances.kRedBoilerAirshipDistanceInches;
			break;
		case BLUE_LEFT:
			driveToAirshipSetpoint = AutoDistances.kBlueBoilerAirshipDistanceInches;
			break;
		default:
			System.err.println("What in tarnation no side peg airship distance");
			driveToAirshipSetpoint = 0;
			break;
		}
		driveToAirshipSetpoint += 2 * Constants.kDriveTicksPerInch;
		
		Logger.getInstance().logRobotThread("Drive to airship", driveToAirshipSetpoint);
		return new DriveStraightRoutine(driveToAirshipSetpoint);
	}
	/*
	 * GET BACKUP
	 */
	private SequentialRoutine getBackup(double sliderPosition) {
		double driveBackupSetpoint = -backupDistance;
		
		// Create a routine that drives back, then moves the slider while moving back forward
		ArrayList<Routine> sequence = new ArrayList<>();
		ArrayList<Routine> parallelSliding = new ArrayList<>();
		parallelSliding.add(new DriveStraightRoutine(driveBackupSetpoint));
		ArrayList<Routine> slideSequence = new ArrayList<>();
		slideSequence.add(new TimeoutRoutine(0.5));
		slideSequence.add(new CustomPositioningSliderRoutine(sliderPosition));
		parallelSliding.add(new SequentialRoutine(slideSequence));
		sequence.add(new ParallelRoutine(parallelSliding));
		sequence.add(new DriveStraightRoutine(-driveBackupSetpoint + 3));
		sequence.add(new TimeoutRoutine(pilotWaitTime));
		
		return new SequentialRoutine(sequence);
	}
	/*
	 * GET NEUTRAL ZONE
	 */
	private SequentialRoutine getDriveToNeutralZone() {
		double driveBackupSetpoint = -(backupDistance + 12);
		double driveToNeutralZoneSetpoint = neutralZoneDistance;
		
		ArrayList<Routine> sequence = new ArrayList<>();
		
		// Back up while lowering spatula
		ArrayList<Routine> parallel = new ArrayList<>();
		parallel.add(new DriveStraightRoutine(driveBackupSetpoint));
		ArrayList<Routine> spatulaSequence = new ArrayList<>();
		spatulaSequence.add(new TimeoutRoutine(1));
		spatulaSequence.add(new SpatulaDownAutocorrectRoutine());
		parallel.add(new SequentialRoutine(spatulaSequence));
		sequence.add(new ParallelRoutine(parallel));

		// Turn towards neutral zone
		// Intentional switch-case falling
		switch (mVariant) {
		case RED_LEFT:
		case BLUE_LEFT:
			sequence.add(new GyroMotionMagicTurnAngleRoutine(-Constants.kSidePegTurnAngleDegrees));
			break;
		case RED_RIGHT:
		case BLUE_RIGHT:
			sequence.add(new GyroMotionMagicTurnAngleRoutine(Constants.kSidePegTurnAngleDegrees));
			break;
		}
		
		// Drive forward
		sequence.add(new DriveStraightRoutine(driveToNeutralZoneSetpoint));
		
		return new SequentialRoutine(sequence);
	}

	@Override
	public String toString() {
		String name;
		switch (mVariant) {
		case RED_LEFT:
			name = "RedLeftDriveStraingtSidePeg";
			break;
		case RED_RIGHT:
			name = "RedRightDriveStraightSidePeg";
			break;
		case BLUE_LEFT:
			name = "BlueLeftDriveStraightSidePeg";
			break;
		case BLUE_RIGHT:
			name = "BlueRightDriveStraightSidePeg";
			break;
		default:
			name = "DriveStraightSidePeg";
			break;
		}
		name += "SliderInitialMove" + initialSliderPosition;
		name += "GyroTurn";
		switch (mPostVariant) {
		case NONE:
			break;
		case BACKUP:
			name += "Backup" + backupPosition;
			break;
		case NEUTRAL_ZONE:
			name += "NeutralZone";
			break;
		}
		return name;
	}
}