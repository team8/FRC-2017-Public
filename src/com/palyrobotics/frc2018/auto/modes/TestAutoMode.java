package com.palyrobotics.frc2018.auto.modes;

import java.util.ArrayList;

import com.palyrobotics.frc2018.auto.AutoModeBase;
import com.palyrobotics.frc2018.behavior.ParallelRoutine;
import com.palyrobotics.frc2018.behavior.Routine;
import com.palyrobotics.frc2018.behavior.SequentialRoutine;
import com.palyrobotics.frc2018.behavior.routines.drive.EncoderTurnAngleRoutine;
import com.palyrobotics.frc2018.behavior.routines.drive.TalonSRXRoutine;
import com.palyrobotics.frc2018.behavior.routines.drive.TimedDriveRoutine;
import com.palyrobotics.frc2018.config.Constants;
import com.palyrobotics.frc2018.config.Gains;
import com.palyrobotics.frc2018.util.archive.DriveSignal;

/**
 * Created by Nihar on 1/11/17.
 * An AutoMode for running test autonomous
 */
public class TestAutoMode extends AutoModeBase {

	@Override
	public Routine getRoutine() {
//		ArrayList<Routine> parallel = new ArrayList<Routine>();
		ArrayList<Routine> sequence = new ArrayList<Routine>();

		sequence.add(new TimedDriveRoutine(6, 3.5));
		return new SequentialRoutine(sequence);
	}

	@Override
	public String toString() {
		return "Test";
	}

	@Override
	public void prestart() {
		System.out.println("Starting TestAutoMode");
	}

	private SequentialRoutine getDrop() {
		Gains mShortGains = Gains.unnamedShortDriveMotionMagicGains;

		DriveSignal driveBackup = DriveSignal.getNeutralSignal();
		double driveBackupSetpoint = -30 * Constants.kDriveTicksPerInch;
		driveBackup.leftMotor.setMotionMagic(driveBackupSetpoint, mShortGains,
				(int )Gains.k2018_UnnamedShortDriveMotionMagicCruiseVelocity, (int) Gains.k2018_UnnamedShortDriveMotionMagicMaxAcceleration);
		driveBackup.rightMotor.setMotionMagic(driveBackupSetpoint, mShortGains,
				(int) Gains.k2018_UnnamedShortDriveMotionMagicCruiseVelocity, (int) Gains.k2018_UnnamedShortDriveMotionMagicMaxAcceleration);

		ArrayList<Routine> sequence = new ArrayList<>();
		ArrayList<Routine> parallelDrop = new ArrayList<>();

		parallelDrop.add(new TalonSRXRoutine(driveBackup, true));
		sequence.add(new ParallelRoutine(parallelDrop));
		sequence.add(new EncoderTurnAngleRoutine(180));

		return new SequentialRoutine(sequence);
	}

}
