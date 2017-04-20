package com.palyrobotics.frc2017.auto.modes;

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
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.palyrobotics.frc2017.util.logger.Logger;

import java.util.ArrayList;

/**
 * Created by Nihar on 2/11/17.
 * Goes for side peg autonomous
 * Configured for left vs right
 */
public class SidePegAutoMode extends AutoModeBase {
	// Represents the peg we are going for
	public enum SideAutoVariant {
		RED_BOILER, 	// Boiler
		BLUE_LOADING,	// Loading station
		RED_LOADING, 	// Loading station
		BLUE_BOILER    // Boiler
	}
	// Represents post score action
	public enum SideAutoPostVariant {
		NONE,
		BACKUP,
		NEUTRAL_ZONE
	}

	// Store configuration on construction
	private final SideAutoVariant mVariant;
	private final SideAutoPostVariant mPostVariant;
	
	private SequentialRoutine mSequentialRoutine;

	// Long distance vs short distance
	private Gains mLongGains, mShortGains;

	private final double pilotWaitTime = 1.5; // time in seconds
	private final double backupDistance = 12;	// distance in inches
	private final double neutralBackupDistance = 47;
	private final double neutralZoneDistance = 12 * 15;	// distance in inches

	private double[] sliderPositions;

	public SidePegAutoMode(SideAutoVariant direction, SideAutoPostVariant postScore) {
		mVariant = direction;
		mPostVariant = postScore;
		mLongGains = Gains.steikLongDriveMotionMagicGains;
		mShortGains = Gains.steikShortDriveMotionMagicGains;
		switch (mVariant) {
			case RED_LOADING:
				sliderPositions = new double[]{0, 3};
				break;
			case BLUE_BOILER:
				sliderPositions = new double[]{0, 1};
				break;
			case RED_BOILER:
				sliderPositions = new double[]{0, 2};
				break;
			case BLUE_LOADING:
				sliderPositions = new double[]{0, -4};
				break;
		}

	}

	@Override
	public Routine getRoutine() {
		return mSequentialRoutine;
	}

	@Override
	public void prestart() {
		System.out.println("Starting "+this.toString()+" Auto Mode");
		Logger.getInstance().logRobotThread("Starting "+this.toString()+" Auto Mode");

		ArrayList<Routine> sequence = new ArrayList<>();

		sequence.add(getDriveForward());

		switch (mVariant) {
		case RED_LOADING:
			sliderPositions = new double[]{0, 3};
			sequence.add(new EncoderTurnAngleRoutine(Constants.kSidePegTurnAngleDegrees));
			break;
		case BLUE_BOILER:
			sliderPositions = new double[]{0, 1};
			sequence.add(new EncoderTurnAngleRoutine(Constants.kSidePegTurnAngleDegrees));
			break;
		case RED_BOILER:

			sliderPositions = new double[]{0, 2};
			sequence.add(new EncoderTurnAngleRoutine(-Constants.kSidePegTurnAngleDegrees));
			break;
		case BLUE_LOADING:
			sliderPositions = new double[]{0, -4};
			sequence.add(new EncoderTurnAngleRoutine(-Constants.kSidePegTurnAngleDegrees));
			break;
		}
		
		sequence.add(getDriveToAirship()); // drive to airship
		sequence.add(new TimeoutRoutine(pilotWaitTime));	// Wait so pilot can pull gear out

		switch (mPostVariant) {
		case NONE:
			break;
		case BACKUP:
			sequence.add(getBackup(sliderPositions[1]));
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
		DriveSignal driveForward = DriveSignal.getNeutralSignal();
		// For Red Left = Blue Right, Red Right = Blue Left
		double driveForwardSetpoint;
		switch (mVariant) {
		// loading station side
		case RED_LOADING:
			driveForwardSetpoint = AutoDistances.kRedLoadingStationForwardDistanceInches * Constants.kDriveTicksPerInch;
			break;
		case BLUE_LOADING:
			driveForwardSetpoint = AutoDistances.kBlueLoadingStationForwardDistanceInches * Constants.kDriveTicksPerInch;
			break;
		// boiler side
		case RED_BOILER:
			driveForwardSetpoint = AutoDistances.kRedBoilerForwardDistanceInches * Constants.kDriveTicksPerInch;
			break;
		case BLUE_BOILER:
			driveForwardSetpoint = AutoDistances.kBlueBoilerForwardDistanceInches * Constants.kDriveTicksPerInch;
			break;
		default:
			System.err.println("What in tarnation no side peg distance");
			driveForwardSetpoint = 0;
			break;
		}
		driveForward.leftMotor.setMotionMagic(driveForwardSetpoint, mLongGains,
				Gains.kSteikLongDriveMotionMagicCruiseVelocity, Gains.kSteikLongDriveMotionMagicMaxAcceleration);
		driveForward.rightMotor.setMotionMagic(driveForwardSetpoint, mLongGains,
				Gains.kSteikLongDriveMotionMagicCruiseVelocity, Gains.kSteikLongDriveMotionMagicMaxAcceleration);
		
		Logger.getInstance().logRobotThread("Drive forward", driveForward);
		ArrayList<Routine> initialSlide = new ArrayList<>();
		initialSlide.add(new CANTalonRoutine(driveForward, true));
		initialSlide.add(new CustomPositioningSliderRoutine(sliderPositions[0]));
		return new ParallelRoutine(initialSlide);
	}
	/*
	 * GET AIRSHIP
	 */
	private CANTalonRoutine getDriveToAirship() {
		DriveSignal driveToAirship = DriveSignal.getNeutralSignal();
		double driveToAirshipSetpoint = 0;
		switch (mVariant) {
		// loading station side
		case RED_LOADING:
			driveToAirshipSetpoint = AutoDistances.kRedLoadingStationAirshipDistanceInches * Constants.kDriveTicksPerInch;
			break;
		case BLUE_LOADING:
			driveToAirshipSetpoint = AutoDistances.kBlueLoadingStationAirshipDistanceInches * Constants.kDriveTicksPerInch;
			break;
			// boiler side
		case RED_BOILER:
			driveToAirshipSetpoint = AutoDistances.kRedBoilerAirshipDistanceInches * Constants.kDriveTicksPerInch;
			break;
		case BLUE_BOILER:
			driveToAirshipSetpoint = AutoDistances.kBlueBoilerAirshipDistanceInches * Constants.kDriveTicksPerInch;
			break;
		default:
			System.err.println("What in tarnation no side peg airship distance");
			driveToAirshipSetpoint = 0;
			break;
		}
		driveToAirshipSetpoint += 2 * Constants.kDriveTicksPerInch;
		driveToAirship.leftMotor.setMotionMagic(driveToAirshipSetpoint, mLongGains,
				Gains.kSteikLongDriveMotionMagicCruiseVelocity, Gains.kSteikLongDriveMotionMagicMaxAcceleration);
		driveToAirship.rightMotor.setMotionMagic(driveToAirshipSetpoint, mLongGains,
				Gains.kSteikLongDriveMotionMagicCruiseVelocity, Gains.kSteikLongDriveMotionMagicMaxAcceleration);
		
		Logger.getInstance().logRobotThread("Drive to airship", driveToAirship);
		return new CANTalonRoutine(driveToAirship, true, 4);
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
	/*
	 * GET NEUTRAL ZONE
	 */
	private SequentialRoutine getDriveToNeutralZone() {
		double driveBackupSetpoint = -(neutralBackupDistance) * Constants.kDriveTicksPerInch;
		double driveToNeutralZoneSetpoint = neutralZoneDistance * Constants.kDriveTicksPerInch;
		
		DriveSignal backupSignal = DriveSignal.getNeutralSignal();
		backupSignal.leftMotor.setMotionMagic(driveBackupSetpoint, mShortGains, 
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		backupSignal.rightMotor.setMotionMagic(driveBackupSetpoint, mShortGains, 
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		
		DriveSignal neutralZoneSignal = DriveSignal.getNeutralSignal();
		neutralZoneSignal.leftMotor.setMotionMagic(driveToNeutralZoneSetpoint, mShortGains, 
				Gains.kSteikLongDriveMotionMagicCruiseVelocity, Gains.kSteikLongDriveMotionMagicMaxAcceleration);
		neutralZoneSignal.rightMotor.setMotionMagic(driveToNeutralZoneSetpoint, mShortGains, 
				Gains.kSteikLongDriveMotionMagicCruiseVelocity, Gains.kSteikLongDriveMotionMagicMaxAcceleration);
		
		ArrayList<Routine> sequence = new ArrayList<>();
		
		// Back up while lowering spatula
		ArrayList<Routine> parallel = new ArrayList<>();
		parallel.add(new CANTalonRoutine(backupSignal, true));
		ArrayList<Routine> spatulaSequence = new ArrayList<>();
//		spatulaSequence.add(new TimeoutRoutine(1));
		spatulaSequence.add(new SpatulaDownAutocorrectRoutine());
		parallel.add(new SequentialRoutine(spatulaSequence));
		sequence.add(new ParallelRoutine(parallel));

		// Turn towards neutral zone
		// Intentional switch-case falling
		switch (mVariant) {
		case RED_LOADING:
		case BLUE_BOILER:
			sequence.add(new EncoderTurnAngleRoutine(-(Constants.kSidePegTurnAngleDegrees - 15)));
			break;
		case RED_BOILER:
		case BLUE_LOADING:
			sequence.add(new EncoderTurnAngleRoutine(Constants.kSidePegTurnAngleDegrees - 15));
			break;
		}
		
		// Drive forward
		sequence.add(new CANTalonRoutine(neutralZoneSignal, true));
		
		return new SequentialRoutine(sequence);
	}

	@Override
	public String toString() {
		String name;
		switch (mVariant) {
		case RED_LOADING:
			name = "RedLeftSidePeg";
			break;
		case RED_BOILER:
			name = "RedRightSidePeg";
			break;
		case BLUE_BOILER:
			name = "BlueLeftSidePeg";
			break;
		case BLUE_LOADING:
			name = "BlueRightSidePeg";
			break;
		default:
			name = "SidePeg";
			break;
		}
		name += "SliderInitialMove"+sliderPositions[0];
		name += "EncoderTurn";
		switch (mPostVariant) {
		case NONE:
			break;
		case BACKUP:
			name += "Backup" + sliderPositions[1];
			break;
		case NEUTRAL_ZONE:
			name += "NeutralZone";
			break;
		}
		return name;
	}
}