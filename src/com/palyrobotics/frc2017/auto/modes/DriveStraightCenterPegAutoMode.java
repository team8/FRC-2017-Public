package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.auto.modes.archive.CenterPegAutoMode;
import com.palyrobotics.frc2017.behavior.ParallelRoutine;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.SpatulaDownAutocorrectRoutine;
import com.palyrobotics.frc2017.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.DriveStraightRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.GyroMotionMagicTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.CustomPositioningSliderRoutine;
import com.palyrobotics.frc2017.config.AutoDistances;
import com.palyrobotics.frc2017.util.logger.Logger;

import java.util.ArrayList;

/**
 * Center peg autonomous using drive straight and gyro turn angle
 * Includes backup or drive to neutral zone
 * @author Ailyn Tong
 */
public class DriveStraightCenterPegAutoMode extends AutoModeBase {
	// Represents post score action
	public enum CenterAutoPostVariant {
		NONE,
		BACKUP,
		NEUTRAL_ZONE_LEFT,
		NEUTRAL_ZONE_RIGHT
	}
	
	// Store configuration on construction
	private final CenterPegAutoMode.Alliance mAlliance;
	private final CenterAutoPostVariant mVariant;
	
	private SequentialRoutine mSequentialRoutine;
	
	private double initialSliderPosition;	// distance from center in inches
	private final double backupDistance = 10;	// distance in inches
	private final double pilotWaitTime = 2;	// time in seconds
	
	// Drop and drive distances
	private final double clearAirshipDistance = 5 * 14;	// distance in inches
	private final double neutralZoneDistance = 12 * 14;	// distance in inches

	public DriveStraightCenterPegAutoMode(CenterPegAutoMode.Alliance alliance, CenterAutoPostVariant variant) {
		mAlliance = alliance;
		mVariant = variant;
	}

	@Override
	public Routine getRoutine() {
		return mSequentialRoutine;
	}

	@Override
	public void prestart() {
		System.out.println("Starting " + this.toString() + " Auto Mode");
		Logger.getInstance().logRobotThread("Starting " + this.toString() + " Auto Mode");
		// Construct sequence of routines to run
		ArrayList<Routine> sequence = new ArrayList<>();
		// Straight drive distance to the center peg
		double driveForwardSetpoint =
				((mAlliance == CenterPegAutoMode.Alliance.BLUE) ? AutoDistances.kBlueCenterPegDistanceInches : AutoDistances.kRedCenterPegDistanceInches);
		
		initialSliderPosition = (mAlliance == CenterPegAutoMode.Alliance.BLUE) ? -2.5 : 0;
		
		// Drive forward while moving slider to initial position
		ArrayList<Routine> initialSlide = new ArrayList<>();
		initialSlide.add(new DriveStraightRoutine(driveForwardSetpoint));
		initialSlide.add(new CustomPositioningSliderRoutine(initialSliderPosition));
		sequence.add(new ParallelRoutine(initialSlide));
		sequence.add(new TimeoutRoutine(pilotWaitTime));
		
		switch (mVariant) {
		case NONE:
			break;
		case BACKUP:
			double backup = (mAlliance == CenterPegAutoMode.Alliance.BLUE) ? 0 : 5;
			sequence.add(getBackup(backup));
			sequence.add(new TimeoutRoutine(pilotWaitTime));
			break;
		case NEUTRAL_ZONE_LEFT:
			sequence.add(getDriveToNeutralZone(-90));
			break;
		case NEUTRAL_ZONE_RIGHT:
			sequence.add(getDriveToNeutralZone(90));
			break;
		}

		for (Routine r : sequence) {
			System.out.println(r.getName());
		}

		mSequentialRoutine = new SequentialRoutine(sequence);
	}
	
	@Override
	public String toString() {
		String name = (mAlliance == CenterPegAutoMode.Alliance.BLUE) ? "BlueDriveStraightCenterPeg" : "RedDriveStraightCenterPeg";
		switch (mVariant) {
		case NONE:
			break;
		case BACKUP:
			name += "Backup";
			break;
		case NEUTRAL_ZONE_LEFT:
			name += "NeutralZoneLeft";
			break;
		case NEUTRAL_ZONE_RIGHT:
			name += "NeutralZoneRight";
		}
		return name;
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
	private SequentialRoutine getDriveToNeutralZone(double angle) {
		double driveBackupSetpoint = -(backupDistance + 12);
		double driveClearAirshipSetpoint = clearAirshipDistance;
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
		// Turn parallel to airship
		sequence.add(new GyroMotionMagicTurnAngleRoutine(angle));
		// Drive clear of airship
		sequence.add(new DriveStraightRoutine(driveClearAirshipSetpoint));
		// Turn to face neutral zone
		sequence.add(new GyroMotionMagicTurnAngleRoutine(-angle));
		// Drive forward
		sequence.add(new DriveStraightRoutine(driveToNeutralZoneSetpoint));
		
		return new SequentialRoutine(sequence);
	}
}