package com.palyrobotics.frc2017.auto.modes;

import java.util.ArrayList;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.EncoderTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.SafetyTurnAngleRoutine;
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
		return new EncoderTurnAngleRoutine(90);
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
