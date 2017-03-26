package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.behavior.ParallelRoutine;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.*;
import com.palyrobotics.frc2017.behavior.routines.scoring.CustomPositioningSliderRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.AutocorrectPositioningSliderRoutine;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.subsystems.Slider.SliderTarget;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.palyrobotics.frc2017.util.logger.Logger;

import java.util.ArrayList;

import javax.sound.midi.SysexMessage;

/**
 * Created by Nihar on 2/11/17.
 * Goes for side peg autonomous
 * Configured for left vs right
 */
public class SidePegAutoMode extends AutoModeBase {
	// Represents the peg we are going for
	public enum SideAutoVariant {
		RED_RIGHT, BLUE_RIGHT,
		RED_LEFT, BLUE_LEFT
	}

	// Store configuration on construction
	private final SideAutoVariant mVariant;
	private final boolean mShouldMoveSlider;
	private final boolean mBackup;
	
	private SequentialRoutine mSequentialRoutine;

	// Long distance vs short distance
	private Gains mLongGains, mShortGains;

	private final double pilotWaitTime = 2.5; // time in seconds
	private final double backupDistance = 15;
	private final double backupTime = 0.4; // time to drive back for, then forwards for

	public SidePegAutoMode(SideAutoVariant direction, boolean shouldMoveSlider, boolean backup) {
		mVariant = direction;
		mShouldMoveSlider = shouldMoveSlider;
		mBackup = backup;

		if(Constants.kRobotName == Constants.RobotName.DERICA) {
			mLongGains = Gains.dericaPosition;
			mShortGains = Gains.dericaPosition;
		} else {
			mLongGains = Gains.steikLongDriveMotionMagicGains;
			mShortGains = Gains.steikShortDriveMotionMagicGains;
		}
	}

	@Override
	public Routine getRoutine() {
		if (mShouldMoveSlider) {
			ArrayList<Routine> parallel = new ArrayList<>();
			parallel.add(new AutocorrectPositioningSliderRoutine(SliderTarget.CENTER));
			parallel.add(mSequentialRoutine);
			return new ParallelRoutine(parallel);
		}
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
		case RED_LEFT:
		case BLUE_LEFT:
			sequence.add(new EncoderTurnAngleRoutine(Constants.kSidePegTurnAngleDegrees));
			break;
		case RED_RIGHT:
		case BLUE_RIGHT:
			sequence.add(new EncoderTurnAngleRoutine(-Constants.kSidePegTurnAngleDegrees));
			break;
		}
		
		sequence.add(getDriveToAirship());
		sequence.add(new TimeoutRoutine(pilotWaitTime));	// Wait 2.5s so pilot can pull gear out

		if (mBackup) {
			sequence.add(getBackup(1.5));
			sequence.add(getBackup(-1.5));
		}

		mSequentialRoutine = new SequentialRoutine(sequence);
	}

	private CANTalonRoutine getDriveForward() {
		DriveSignal driveForward = DriveSignal.getNeutralSignal();
		// For Red Left = Blue Right, Red Right = Blue Left
		double driveForwardSetpoint;
		switch (mVariant) {
		// loading station side
		case RED_LEFT:
			driveForwardSetpoint = Constants.k254LoadingStationForwardDistanceInches *
			((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
					: Constants.kDriveTicksPerInch);
			break;
		case BLUE_RIGHT:
			driveForwardSetpoint = Constants.k254LoadingStationForwardDistanceInches *
			((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
					: Constants.kDriveTicksPerInch);
			break;
			// boiler side
		case RED_RIGHT:
			driveForwardSetpoint = Constants.k254BoilerForwardDistanceInches *
			((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
					: Constants.kDriveTicksPerInch);
			break;
		case BLUE_LEFT:
			driveForwardSetpoint = Constants.k254BoilerForwardDistanceInches *
			((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
					: Constants.kDriveTicksPerInch);
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
		return new CANTalonRoutine(driveForward, true);
	}
	
	private CANTalonRoutine getDriveToAirship() {
		DriveSignal driveToAirship = DriveSignal.getNeutralSignal();
		double driveToAirshipSetpoint = 0;
		switch (mVariant) {
		// loading station side
		case RED_LEFT:
			driveToAirshipSetpoint = Constants.k254LoadingStationAirshipDistanceInches *
			((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
					: Constants.kDriveTicksPerInch);
			break;
		case BLUE_RIGHT:
			driveToAirshipSetpoint = Constants.k254LoadingStationAirshipDistanceInches *
			((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
					: Constants.kDriveTicksPerInch);
			break;
			// boiler side
		case RED_RIGHT:
			driveToAirshipSetpoint = Constants.k254BoilerAirshipDistanceInches *
			((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
					: Constants.kDriveTicksPerInch);
			break;
		case BLUE_LEFT:
			driveToAirshipSetpoint = Constants.kBlueBoilerAirshipDistanceInches *
			((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
					: Constants.kDriveTicksPerInch);
			break;
		default:
			System.err.println("What in tarnation no side peg airship distance");
			driveToAirshipSetpoint = 0;
			break;
		}
		driveToAirship.leftMotor.setMotionMagic(driveToAirshipSetpoint, mLongGains,
				Gains.kSteikLongDriveMotionMagicCruiseVelocity, Gains.kSteikLongDriveMotionMagicMaxAcceleration);
		driveToAirship.rightMotor.setMotionMagic(driveToAirshipSetpoint, mLongGains,
				Gains.kSteikLongDriveMotionMagicCruiseVelocity, Gains.kSteikLongDriveMotionMagicMaxAcceleration);
		
		Logger.getInstance().logRobotThread("Drive to airship", driveToAirship);
		return new CANTalonRoutine(driveToAirship, true);
	}
	
	private SequentialRoutine getBackup(double sliderPosition) {
		DriveSignal driveBackup = DriveSignal.getNeutralSignal();
		DriveSignal driveReturn = DriveSignal.getNeutralSignal();

		double driveBackupSetpoint = -backupDistance *
				((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
						: Constants.kDriveTicksPerInch);
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
		parallelSliding.add(new CustomPositioningSliderRoutine(sliderPosition));
//		sequence.add(new CANTalonRoutine(driveBackup, true));
//		sequence.add(new CustomPositioningSliderRoutine(sliderPosition));
		sequence.add(new ParallelRoutine(parallelSliding));
		sequence.add(new CANTalonRoutine(driveReturn, true));
		sequence.add(new TimeoutRoutine(pilotWaitTime));
		
		return new SequentialRoutine(sequence);
	}

	private SequentialRoutine getTimedBackup(double sliderPosition) {
		DriveSignal driveBack = DriveSignal.getNeutralSignal();
		DriveSignal driveReturn = DriveSignal.getNeutralSignal();
		driveBack.leftMotor.setPercentVBus(0.25);
		driveBack.rightMotor.setPercentVBus(-0.3);
		driveReturn.leftMotor.setPercentVBus(-0.3);
		driveReturn.rightMotor.setPercentVBus(0.3);

		ArrayList<Routine> sequence = new ArrayList<>();
		ArrayList<Routine> parallelSliding = new ArrayList<>();
		parallelSliding.add(new DriveTimeRoutine(backupTime, driveBack));
		parallelSliding.add(new CustomPositioningSliderRoutine(sliderPosition));
		sequence.add(new ParallelRoutine(parallelSliding));
		sequence.add(new DriveTimeRoutine(backupTime, driveReturn));


		return new SequentialRoutine(sequence);
	}

	@Override
	public String toString() {
		String name;
		switch (mVariant) {
		case RED_LEFT:
			name = "RedLeftSidePeg";
			break;
		case RED_RIGHT:
			name = "RedRightSidePeg";
			break;
		case BLUE_LEFT:
			name = "BlueLeftSidePeg";
			break;
		case BLUE_RIGHT:
			name = "BlueRightSidePeg";
			break;
		default:
			name = "SidePeg";
			break;
		}
		if (mShouldMoveSlider) {
			name += "SliderCenter";
		} else {
			name += "NotSliderCenter";
		}
		name += "EncoderTurn";
		if (mBackup) {
			name += "Backup";
		} else {
			name += "NotBackup";
		}
		return name;
	}
}