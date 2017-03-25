package com.palyrobotics.frc2017.auto.modes;

import java.util.ArrayList;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.EncoderTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.SafetyTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.CustomPositioningSliderRoutine;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.util.archive.DriveSignal;

/**
 * Created by Nihar on 1/11/17.
 * An AutoMode for running test autonomous
 */
public class TestAutoMode extends AutoModeBase {
	// Currently configured to test encoder turn angle

	@Override
	public Routine getRoutine() {
//		double driveForwardSetpoint = 75*Constants.kDriveTicksPerInch; //inches
//		DriveSignal driveForward = DriveSignal.getNeutralSignal();
//		
//		driveForward.leftMotor.setMotionMagic(driveForwardSetpoint, Gains.steikShortDriveMotionMagicGains,
//				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
//		driveForward.rightMotor.setMotionMagic(driveForwardSetpoint, Gains.steikShortDriveMotionMagicGains,
//				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
//		
//		CANTalonRoutine routine = new CANTalonRoutine(driveForward, true);
//		return routine;
		return new CustomPositioningSliderRoutine(5 * Constants.kSliderRevolutionsPerInch);
//		EncoderTurnAngleRoutine routine = new EncoderTurnAngleRoutine(90);
//		EncoderTurnAngleRoutine routine2 = new EncoderTurnAngleRoutine(60);
//		ArrayList<Routine> sequence = new ArrayList<Routine>();
//		sequence.add(routine);
//		sequence.add(new TimeoutRoutine(2));
//		sequence.add(routine2);
//		sequence.add(new TimeoutRoutine(2));
//		sequence.add(new EncoderTurnAngleRoutine(-60));
//		return new SequentialRoutine(sequence);
	}

	@Override
	public String toString() {
		return "Test";
	}

	@Override
	public void prestart() {
		System.out.println("Starting TestAutoMode");
	}
}
