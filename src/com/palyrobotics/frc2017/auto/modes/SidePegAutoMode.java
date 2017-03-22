package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.behavior.ParallelRoutine;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.BBTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.EncoderTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.SafetyTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.SliderDistancePositioningAutocorrectRoutine;
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
 * Can use a gyro bang bang or an encoder turn angle loop
 */
public class SidePegAutoMode extends AutoModeBase {
	// Represents the peg we are going for
	public enum SideAutoVariant {
		RED_RIGHT, BLUE_RIGHT,
		RED_LEFT, BLUE_LEFT
	}
	
	private final SideAutoVariant mVariant;
	private final boolean mShouldCenterSlider;
	private final boolean mShouldUseGyro;
	private final boolean mBackup;
	private SequentialRoutine mSequentialRoutine;

	private Gains mGains;
	
	private DriveSignal driveForward = DriveSignal.getNeutralSignal();
	private DriveSignal driveToAirship = DriveSignal.getNeutralSignal();
	private DriveSignal driveBackup = DriveSignal.getNeutralSignal();
	private DriveSignal driveReturn = DriveSignal.getNeutralSignal();
	
	public SidePegAutoMode(SideAutoVariant direction, boolean shouldCenterSlider,
						   boolean shouldUseGyro, boolean backup) {
		mVariant = direction;
		mShouldCenterSlider = shouldCenterSlider;
		mShouldUseGyro = shouldUseGyro;
		mBackup = backup;

		if(Constants.kRobotName == Constants.RobotName.DERICA) {
			mGains = Gains.dericaPosition;
		} else {
			mGains = (Constants.kRobotName == Constants.RobotName.STEIK) ? Gains.steikPosition : Gains.aegirDriveMotionMagicGains;
		}
	}

	@Override
	public Routine getRoutine() {
		if (mShouldCenterSlider) {
			ArrayList<Routine> parallel = new ArrayList<>();
			parallel.add(new SliderDistancePositioningAutocorrectRoutine(SliderTarget.CENTER));
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
		
		// NOTE: Intentional switch case falling
		// For Red Left = Blue Right, Red Right = Blue Left
		double driveForwardSetpoint;
		switch (mVariant) {
			// loading station side
			case RED_LEFT:
				driveForwardSetpoint = Constants.kRedLoadingStationForwardDistanceInches *
				((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
						: Constants.kDriveTicksPerInch);
				break;
			case BLUE_RIGHT:
				driveForwardSetpoint = Constants.kBlueLoadingStationForwardDistanceInches *
					((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
							: Constants.kDriveTicksPerInch);
				break;
			// boiler side
			case RED_RIGHT:
				driveForwardSetpoint = Constants.kRedBoilerForwardDistanceInches *
				((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
						: Constants.kDriveTicksPerInch);
				break;
			case BLUE_LEFT:
				driveForwardSetpoint = Constants.kBlueBoilerForwardDistanceInches *
					((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
							: Constants.kDriveTicksPerInch);
				break;
			default:
				System.err.println("What in tarnation no side peg distance");
				driveForwardSetpoint = 0;
				break;
		}
		driveForward.leftMotor.setMotionMagic(driveForwardSetpoint, mGains,
				Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
		driveForward.rightMotor.setMotionMagic(driveForwardSetpoint, mGains,
				Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
		sequence.add(new CANTalonRoutine(driveForward, true));
		// Added drive dist sequence
		
		// NOTE: switch case falling, split by lefts vs rights
		switch (mVariant) {
			case RED_LEFT:
			case BLUE_LEFT:
				if (mShouldUseGyro) {
					sequence.add(new SafetyTurnAngleRoutine(Constants.kSidePegTurnAngleDegrees));
				} else {
					sequence.add(new EncoderTurnAngleRoutine(Constants.kSidePegTurnAngleDegrees));
				}
				break;
			case RED_RIGHT:
			case BLUE_RIGHT:
				if (mShouldUseGyro) {
					sequence.add(new SafetyTurnAngleRoutine(-Constants.kSidePegTurnAngleDegrees));
				} else {
					sequence.add(new EncoderTurnAngleRoutine(-Constants.kSidePegTurnAngleDegrees));
				}
				break;
		}

		double driveToAirshipSetpoint = 0;
		switch (mVariant) {
			// loading station side
			case RED_LEFT:
				driveToAirshipSetpoint = Constants.kRedLoadingStationAirshipDistanceInches *
						((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
								: Constants.kDriveTicksPerInch);
				break;
			case BLUE_RIGHT:
				driveToAirshipSetpoint = Constants.kBlueLoadingStationAirshipDistanceInches *
						((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
								: Constants.kDriveTicksPerInch);
				break;
			// boiler side
			case RED_RIGHT:
				driveToAirshipSetpoint = Constants.kRedBoilerAirshipDistanceInches *
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
		driveToAirship.leftMotor.setMotionMagic(driveToAirshipSetpoint, mGains,
				Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
		driveToAirship.rightMotor.setMotionMagic(driveToAirshipSetpoint, mGains,
				Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
		sequence.add(new CANTalonRoutine(driveToAirship, true));
		sequence.add(new TimeoutRoutine(2.5));	// Wait 2.5s so pilot can pull gear out
		
		if (mBackup) {
			double driveBackupSetpoint = -24 * 
						((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
								: Constants.kDriveTicksPerInch);
			driveBackup.leftMotor.setMotionMagic(driveBackupSetpoint, mGains, 
					Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
			driveBackup.rightMotor.setMotionMagic(driveBackupSetpoint, mGains, 
					Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
			
			// drive forward same distance as backup
			driveReturn.leftMotor.setMotionMagic(-driveBackupSetpoint, mGains, 
					Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
			driveReturn.rightMotor.setMotionMagic(-driveBackupSetpoint, mGains, 
					Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
			
			sequence.add(new CANTalonRoutine(driveBackup, true));
			//TODO: move slider one way
			sequence.add(new CANTalonRoutine(driveReturn, true));
			sequence.add(new TimeoutRoutine(2.5));	// Wait 2.5s so pilot can pull gear out
			sequence.add(new CANTalonRoutine(driveBackup, true));
			//TODO: move slider other way
			sequence.add(new CANTalonRoutine(driveReturn, true));
			sequence.add(new TimeoutRoutine(2.5));	// Wait 2.5s so pilot can pull gear out
		}

		Logger.getInstance().logRobotThread("Drive forward", driveForward);
		Logger.getInstance().logRobotThread("Drive to airship", driveToAirship);
		mSequentialRoutine = new SequentialRoutine(sequence);
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
		if (mShouldCenterSlider) {
			name += "SliderCenter";
		} else {
			name += "NotSliderCenter";
		}
		if (mShouldUseGyro) {
			name += "Gyro";
		} else {
			name += "EncoderTurn";
		}
		if (mBackup) {
			name += "Backup";
		} else {
			name += "NotBackup";
		}
		return name;
	}
}