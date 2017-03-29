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
		ArrayList<Routine> sequence = new ArrayList<Routine>();
		
<<<<<<< HEAD
		sequence.add(new CustomPositioningSliderRoutine(-7));
//		sequence.add(new EncoderTurnAngleRoutine(60));
		sequence.add(new VisionSliderRoutine());
=======
		double driveForwardSetpoint = 5*Constants.kDriveTicksPerInch; //inches
		DriveSignal driveForward = DriveSignal.getNeutralSignal();
		
		driveForward.leftMotor.setMotionMagic(driveForwardSetpoint, Gains.steikShortDriveMotionMagicGains,
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		driveForward.rightMotor.setMotionMagic(driveForwardSetpoint, Gains.steikShortDriveMotionMagicGains,
				Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
		
//		sequence.add(new CANTalonRoutine(driveForward, true));
////		return routine;
////		DriveSignal power = DriveSignal.getNeutralSignal();
////		power.leftMotor.setPercentVBus(0.3);
////		power.rightMotor.setPercentVBus(0.3);
////		return new DriveTimeRoutine(1, power);
//		
//		sequence.add(new CustomPositioningSliderRoutine(5));
		
//		sequence.add(new AutocorrectPositioningSliderRoutine(Slider.SliderTarget.CENTER));
//		sequence.add(new VisionSliderRoutine());
//		sequence.add(new CANTalonRoutine(driveForward, true));
		sequence.add(new SafetyTurnAngleRoutine(90));
>>>>>>> Tried gyro turn angle, update vision code
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
}
