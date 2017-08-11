package com.palyrobotics.frc2017.auto.modes.archive;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.behavior.ParallelRoutine;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.SpatulaDownAutocorrectRoutine;
import com.palyrobotics.frc2017.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.EncoderTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.CustomPositioningSliderRoutine;
import com.palyrobotics.frc2017.config.AutoDistances;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.palyrobotics.frc2017.util.logger.Logger;

import java.util.ArrayList;

import static com.palyrobotics.frc2017.auto.modes.SidePegAutoMode.SideAutoVariant;

public class SidePegAutoToCenterAutoMode extends AutoModeBase {
	// Store configuration on construction
	private final SideAutoVariant mVariant;
	private final boolean mBackup;
	
	private SequentialRoutine mSequentialRoutine;

	// Long distance vs short distance
	private Gains mLongGains, mShortGains;

	private final double pilotWaitTime = 1.5; // time in seconds
	private final double backupDistance = 10;	// distance in inches
	private final double driveToCenter = 12 * 20;
	

	double initialSliderPosition = 0;
	double backupPosition = 0;

	public SidePegAutoToCenterAutoMode(SideAutoVariant direction, boolean backup) {
		mVariant = direction;
		mBackup = backup;
		mLongGains = Gains.steikLongDriveMotionMagicGains;
		mShortGains = Gains.steikShortDriveMotionMagicGains;
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

		// NOTE: switch case falling, split by lefts vs rights
		switch (mVariant) {
		case RED_LOADING:
			backupPosition = 3;
			sequence.add(new EncoderTurnAngleRoutine(Constants.kSidePegTurnAngleDegrees));
			break;
		case BLUE_BOILER:
			backupPosition = 1; //-3
			sequence.add(new EncoderTurnAngleRoutine(Constants.kSidePegTurnAngleDegrees));
			break;
		case RED_BOILER:
			backupPosition = 2;
			sequence.add(new EncoderTurnAngleRoutine(-Constants.kSidePegTurnAngleDegrees));
			break;
		case BLUE_LOADING:
			backupPosition = -4;
			sequence.add(new EncoderTurnAngleRoutine(-Constants.kSidePegTurnAngleDegrees));
			break;
		}
		
		sequence.add(getDriveToAirship());
		sequence.add(new TimeoutRoutine(pilotWaitTime));	// Wait 2.5s so pilot can pull gear out
		
		ArrayList<Routine> parallel = new ArrayList<Routine>();
		// In parallel, back up and drop the spatula
		parallel.add(getBackup(backupPosition));
		parallel.add(new SpatulaDownAutocorrectRoutine());
		
		sequence.add(new ParallelRoutine(parallel));
		sequence.add(getStraightenOutAndDriveToCenter());

		mSequentialRoutine = new SequentialRoutine(sequence);
	}
	
	private Routine getStraightenOutAndDriveToCenter() {
		ArrayList<Routine> sequence = new ArrayList<>();

		switch (mVariant) {
		case RED_LOADING:
			sequence.add(new EncoderTurnAngleRoutine(-Constants.kSidePegTurnAngleDegrees));
			break;
		case BLUE_BOILER:
			sequence.add(new EncoderTurnAngleRoutine(-Constants.kSidePegTurnAngleDegrees));
			break;
		case RED_BOILER:
			sequence.add(new EncoderTurnAngleRoutine(Constants.kSidePegTurnAngleDegrees));
			break;
		case BLUE_LOADING:
			sequence.add(new EncoderTurnAngleRoutine(Constants.kSidePegTurnAngleDegrees));
			break;
		}
		
		DriveSignal driveToCenter = DriveSignal.getNeutralSignal();

		// Back up an additional 12 inches to allow the gear to fully exit
		double driveToCenterSetPoint = this.driveToCenter * Constants.kDriveTicksPerInch;
		driveToCenter.leftMotor.setMotionMagic(driveToCenterSetPoint, mShortGains, 
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		driveToCenter.rightMotor.setMotionMagic(driveToCenterSetPoint, mShortGains, 
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		
		sequence.add(new CANTalonRoutine(driveToCenter, true));
		
		return new SequentialRoutine(sequence);
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
			initialSliderPosition = 0;
			driveForwardSetpoint = AutoDistances.kRedLoadingStationForwardDistanceInches * Constants.kDriveTicksPerInch;
			break;
		case BLUE_LOADING:
			initialSliderPosition = 0;
			driveForwardSetpoint = AutoDistances.kBlueLoadingStationForwardDistanceInches * Constants.kDriveTicksPerInch;
			break;
		// boiler side
		case RED_BOILER:
			initialSliderPosition = 0;
			driveForwardSetpoint = AutoDistances.kRedBoilerForwardDistanceInches * Constants.kDriveTicksPerInch;
			break;
		case BLUE_BOILER:
			initialSliderPosition = 0;
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
		initialSlide.add(new CustomPositioningSliderRoutine(initialSliderPosition));
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
		return new CANTalonRoutine(driveToAirship, true);
	}
	/*
	 * GET BACKUP
	 */
	private SequentialRoutine getBackup(double sliderPosition) {
		DriveSignal driveBackup = DriveSignal.getNeutralSignal();
		DriveSignal driveReturn = DriveSignal.getNeutralSignal();

		// Back up an additional 12 inches to allow the gear to fully exit
		double driveBackupSetpoint = -(backupDistance + 12) * Constants.kDriveTicksPerInch;
		driveBackup.leftMotor.setMotionMagic(driveBackupSetpoint, mShortGains, 
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		driveBackup.rightMotor.setMotionMagic(driveBackupSetpoint, mShortGains, 
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);

		// Create a routine that drives back, then moves the slider while moving back forward
		ArrayList<Routine> sequence = new ArrayList<>();

		sequence.add(new CANTalonRoutine(driveBackup, true));
		
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
		name += "SliderInitialMove"+initialSliderPosition;
		name += "EncoderTurn";
		if (mBackup) {
			name += "Backup"+backupPosition;
		} else {
			name += "NotBackup";
		}
		return name;
	}
}
