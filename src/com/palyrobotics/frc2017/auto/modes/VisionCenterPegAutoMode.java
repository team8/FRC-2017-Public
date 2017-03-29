package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.behavior.ParallelRoutine;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.CustomPositioningSliderRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.VisionSliderRoutine;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.palyrobotics.frc2017.util.logger.Logger;
import com.palyrobotics.frc2017.vision.AndroidConnectionHelper;

import java.util.ArrayList;

/**
 * Created by Nihar on 2/11/17.
 * BBTurnAngle might be replaced with EncoderTurnAngle if no gyro
 */
public class VisionCenterPegAutoMode extends AutoModeBase {
	private final CenterPegAutoMode.Alliance mAlliance;
	private Routine mSequentialRoutine;
	private boolean mBackup = true;
	
	private Gains mShortGains, mLongGains;
	private double initialSliderPosition;	// distance from center in inches
	private final double backupDistance = 10;	// distance in inches
	private final double pilotWaitTime = 3;	// time in seconds
	private double bonusDistance = 14;
	private boolean isRightTarget;

	public VisionCenterPegAutoMode(CenterPegAutoMode.Alliance alliance, boolean isRightTarget, boolean backup) {
		mAlliance = alliance;
		this.isRightTarget = isRightTarget;
		initialSliderPosition = (isRightTarget) ? -7 : 7;
		mShortGains = Gains.steikShortDriveMotionMagicGains;
		mLongGains = Gains.steikLongDriveMotionMagicGains;

		mBackup = backup;
	}

	@Override
	public Routine getRoutine() {
		return mSequentialRoutine;
	}

	@Override
	public void prestart() {
		String log = "Starting Vision Center Peg Auto Mode";
		Logger.getInstance().logRobotThread("Starting Vision Center Peg Auto Mode");
		if (!AndroidConnectionHelper.getInstance().isServerStarted()  || !AndroidConnectionHelper.getInstance().isNexusConnected()) {
			System.out.println("Failed to find vision server, revert auto");
			Logger.getInstance().logRobotThread("Failed to find vision server, revert");
			CenterPegAutoMode fallback = new CenterPegAutoMode(mAlliance, true);
			fallback.prestart();
			mSequentialRoutine = fallback.getRoutine();
			return;
		}
		// Construct sequence of routines to run
		ArrayList<Routine> sequence = new ArrayList<>();
		// Straight drive distance to the center peg
		DriveSignal driveForward = DriveSignal.getNeutralSignal();
		double driveForwardSetpoint =
				((mAlliance == CenterPegAutoMode.Alliance.BLUE) ? Constants.k254CenterPegDistanceInches : Constants.k254CenterPegDistanceInches)
						* Constants.kDriveTicksPerInch;
		driveForwardSetpoint -= bonusDistance * Constants.kDriveTicksPerInch;
		// Aegir: right +30
		// Vali: left +100
		driveForward.leftMotor.setMotionMagic(driveForwardSetpoint, mLongGains,
			Gains.kSteikLongDriveMotionMagicCruiseVelocity, Gains.kSteikLongDriveMotionMagicMaxAcceleration);
		driveForward.rightMotor.setMotionMagic(driveForwardSetpoint+30, mLongGains,
				Gains.kSteikLongDriveMotionMagicCruiseVelocity, Gains.kSteikLongDriveMotionMagicMaxAcceleration);
		
		DriveSignal driveBonus = DriveSignal.getNeutralSignal();
		driveBonus.leftMotor.setMotionMagic(bonusDistance, mShortGains, Gains.kSteikShortDriveMotionMagicCruiseVelocity,
				Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		driveBonus.rightMotor.setMotionMagic(bonusDistance, mShortGains, Gains.kSteikShortDriveMotionMagicCruiseVelocity,
				Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		
		// Drive forward while moving slider to initial position
		ArrayList<Routine> initialSlide = new ArrayList<>();
		initialSlide.add(new CANTalonRoutine(driveForward, true));
		initialSlide.add(new CustomPositioningSliderRoutine(initialSliderPosition));
		sequence.add(new ParallelRoutine(initialSlide));
		sequence.add(new VisionSliderRoutine());
		sequence.add(new CANTalonRoutine(driveBonus, true));
		sequence.add(new TimeoutRoutine(pilotWaitTime));
		
		if (mBackup) {
			sequence.add(getBackup(-2));		// Move slider slightly to the right
			sequence.add(new TimeoutRoutine(pilotWaitTime));
		}

		mSequentialRoutine = new SequentialRoutine(sequence);
		System.out.println(log);
	}
	@Override
	public String toString() {
		String name = (mAlliance == CenterPegAutoMode.Alliance.BLUE) ? "BlueCenterPeg" : "RedCenterPeg";
		return "Vision"+name;
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
		slideSequence.add(new TimeoutRoutine(0.5));
		slideSequence.add(new CustomPositioningSliderRoutine(sliderPosition));
		parallelSliding.add(new SequentialRoutine(slideSequence));
		sequence.add(new ParallelRoutine(parallelSliding));
		sequence.add(new CANTalonRoutine(driveReturn, true));
		sequence.add(new TimeoutRoutine(pilotWaitTime));
		
		return new SequentialRoutine(sequence);
	}
}