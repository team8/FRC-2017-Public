package com.palyrobotics.frc2017.auto.modes;

import java.util.ArrayList;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.behavior.ParallelRoutine;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.SliderSensorResetRoutine;
import com.palyrobotics.frc2017.behavior.routines.SpatulaDownAutocorrectRoutine;
import com.palyrobotics.frc2017.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.DriveStraightRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.DriveTimeRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.EncoderTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.GyroMotionMagicTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.SafetyTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.AutocorrectPositioningSliderRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.CustomPositioningSliderRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.MultiSampleVisionSliderRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.VisionSliderRoutine;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.subsystems.Slider;
import com.palyrobotics.frc2017.util.archive.DriveSignal;

/**
 * Created by Nihar on 1/11/17.
 * An AutoMode for running test autonomous
 */
public class TestAutoMode extends AutoModeBase {
	// Currently configured to test encoder turn angle

	@Override
	public Routine getRoutine() {
//		ArrayList<Routine> parallel = new ArrayList<Routine>();
//		
		ArrayList<Routine> sequence = new ArrayList<Routine>();

//		sequence.add(new ParallelRoutine(parallel));
//		
//		sequence.add(new GyroMotionMagicTurnAngleRoutine(90));
//		sequence.add(new TimeoutRoutine(1));
//		sequence.add(new GyroMotionMagicTurnAngleRoutine(90));
//		sequence.add(new TimeoutRoutine(1));
//		sequence.add(new GyroMotionMagicTurnAngleRoutine(90));
//		sequence.add(new TimeoutRoutine(1));
//		sequence.add(new GyroMotionMagicTurnAngleRoutine(90));
//		sequence.add(new TimeoutRoutine(1));
//		sequence.add(new DriveStraightRoutine(75));
		//return getDrop();
		sequence.add(new SliderSensorResetRoutine());
		sequence.add(new CustomPositioningSliderRoutine(0));
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
		Gains 	mShortGains = Gains.steikShortDriveMotionMagicGains;


		DriveSignal driveBackup = DriveSignal.getNeutralSignal();
		double driveBackupSetpoint = -30 * Constants.kDriveTicksPerInch;
		driveBackup.leftMotor.setMotionMagic(driveBackupSetpoint, mShortGains,
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		driveBackup.rightMotor.setMotionMagic(driveBackupSetpoint, mShortGains,
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);

		ArrayList<Routine> sequence = new ArrayList<>();
		ArrayList<Routine> parallelDrop = new ArrayList<>();

		parallelDrop.add(new CANTalonRoutine(driveBackup, true));
		parallelDrop.add(new SpatulaDownAutocorrectRoutine());
		sequence.add(new ParallelRoutine(parallelDrop));
		sequence.add(new EncoderTurnAngleRoutine(180));

		return new SequentialRoutine(sequence);
	}

}
