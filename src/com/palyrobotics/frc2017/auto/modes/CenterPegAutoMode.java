package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.behavior.ParallelRoutine;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.DriveTimeRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.SafetyTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.CustomPositioningSliderRoutine;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.robot.Robot;
import com.palyrobotics.frc2017.util.archive.DriveSignal;

import java.util.ArrayList;

/**
 * Created by Nihar on 2/11/17.
 * BBTurnAngle might be replaced with EncoderTurnAngle if no gyro
 */
public class CenterPegAutoMode extends AutoModeBase {
	// Represents the variation of center peg auto based on what to do after scoring
	public enum PostCenterAutoVariant {
		NOTHING, CROSS_LEFT, CROSS_RIGHT
	}
	public enum Alliance {
		BLUE, RED
	}
	private final PostCenterAutoVariant mVariant;
	private final Alliance mAlliance;
	private SequentialRoutine mSequentialRoutine;
	private boolean mBackup = true;
	
	private Gains mShortGains;
	private final double backupTime = 0.5;
	private final double backupDistance = 14;
	private final double pilotWaitTime = 2.5;

	public CenterPegAutoMode(Alliance alliance, PostCenterAutoVariant direction) {
		mVariant = direction;
		mAlliance = alliance;
		if(Constants.kRobotName == Constants.RobotName.DERICA) {
			mShortGains = Gains.dericaPosition;
		} else {
			mShortGains = Gains.steikShortDriveMotionMagicGains;
		}
	}

	@Override
	public Routine getRoutine() {
		return mSequentialRoutine;
	}

	@Override
	public void prestart() {
		String log = "Starting Center Peg Auto Mode";
		// Construct sequence of routines to run
		ArrayList<Routine> sequence = new ArrayList<>();
		// Straight drive distance to the center peg
		DriveSignal driveForward = DriveSignal.getNeutralSignal();
		double driveForwardSetpoint =
				((mAlliance == Alliance.BLUE) ? Constants.k254CenterPegDistanceInches : Constants.k254CenterPegDistanceInches)
						*
				((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
						: Constants.kDriveTicksPerInch);
		// Aegir: right +30
		// Vali: left +100
		driveForward.leftMotor.setMotionMagic(driveForwardSetpoint, mShortGains,
			Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		driveForward.rightMotor.setMotionMagic(driveForwardSetpoint+30, mShortGains,
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		
		sequence.add(new CANTalonRoutine(driveForward, true));
		sequence.add(new TimeoutRoutine(pilotWaitTime));
		
		if (mBackup) {
			sequence.add(getTimedBackup(1.5));
			sequence.add(new TimeoutRoutine(pilotWaitTime));
			sequence.add(getTimedBackup(-1.5));
		}

		mSequentialRoutine = new SequentialRoutine(sequence);
		System.out.println(log);
	}
	@Override
	public String toString() {
		String name = (mAlliance == Alliance.BLUE) ? "BlueCenterPeg" : "RedCenterPeg";
		switch (mVariant) {
			case NOTHING:
				break;
			case CROSS_LEFT:
				name += "_CrossLeft";
				break;
			case CROSS_RIGHT:
				name += "_CrossRight";
				break;
		}
		return name;
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
		driveBack.leftMotor.setPercentVBus(-0.3);
		driveBack.rightMotor.setPercentVBus(-0.3);
		driveReturn.leftMotor.setPercentVBus(0.3);
		driveReturn.rightMotor.setPercentVBus(0.3);

		ArrayList<Routine> sequence = new ArrayList<>();
		ArrayList<Routine> parallelSliding = new ArrayList<>();
		parallelSliding.add(new DriveTimeRoutine(backupTime, driveBack));
		parallelSliding.add(new CustomPositioningSliderRoutine(sliderPosition));
		sequence.add(new ParallelRoutine(parallelSliding));
		sequence.add(new DriveTimeRoutine(backupTime, driveReturn));

		return new SequentialRoutine(sequence);
	}
}