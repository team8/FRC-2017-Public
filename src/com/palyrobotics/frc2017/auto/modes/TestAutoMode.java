package com.palyrobotics.frc2017.auto.modes;

import java.util.ArrayList;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.DriveTimeRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.EncoderTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.SafetyTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.AutocorrectPositioningSliderRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.CustomPositioningSliderRoutine;
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
		ArrayList<Routine> sequence = new ArrayList<Routine>();
		
		double driveForwardSetpoint = 5*Constants.kDriveTicksPerInch; //inches
		DriveSignal driveForward = DriveSignal.getNeutralSignal();
		
		driveForward.leftMotor.setMotionMagic(driveForwardSetpoint, Gains.steikShortDriveMotionMagicGains,
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		driveForward.rightMotor.setMotionMagic(driveForwardSetpoint, Gains.steikShortDriveMotionMagicGains,
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
//		
//		sequence.add(new CANTalonRoutine(driveForward, true));
////		return routine;
////		DriveSignal power = DriveSignal.getNeutralSignal();
////		power.leftMotor.setPercentVBus(0.3);
////		power.rightMotor.setPercentVBus(0.3);
////		return new DriveTimeRoutine(1, power);
//		
//		sequence.add(new CustomPositioningSliderRoutine(5));
//		return new SequentialRoutine(sequence);
		
		//sequence.add(new AutocorrectPositioningSliderRoutine(Slider.SliderTarget.CENTER));
		sequence.add(new VisionSliderRoutine());
		sequence.add(new CANTalonRoutine(driveForward, true));
		return new SequentialRoutine(sequence);
		
		//return new CustomPositioningSliderRoutine(-3);
		
		//EncoderTurnAngleRoutine routine = new EncoderTurnAngleRoutine(90);
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
