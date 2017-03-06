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
		DriveSignal driveToAirship = DriveSignal.getNeutralSignal();
		double driveToAirshipSetpoint = Constants.kSidePegDistanceToAirshipInches * 
				((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
						: Constants.kDriveTicksPerInch);
		driveToAirship.leftMotor.setMotionMagic(driveToAirshipSetpoint, Gains.aegirDriveMotionMagicGains,
			Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
		driveToAirship.rightMotor.setMotionMagic(driveToAirshipSetpoint, Gains.aegirDriveMotionMagicGains,
				Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
		ArrayList<Routine> sequence = new ArrayList<>();
		sequence.add(new CANTalonRoutine(driveToAirship, true));
		DriveSignal driveBack = DriveSignal.getNeutralSignal();
		driveBack.leftMotor.setMotionMagic(0, Gains.aegirDriveMotionMagicGains, Gains.kAegirDriveMotionMagicCruiseVelocity,
				Gains.kAegirDriveMotionMagicMaxAcceleration);
		driveBack.rightMotor.setMotionMagic(0, Gains.aegirDriveMotionMagicGains, Gains.kAegirDriveMotionMagicCruiseVelocity,
				Gains.kAegirDriveMotionMagicMaxAcceleration);
		sequence.add(new CANTalonRoutine(driveToAirship));
		sequence.add(new CANTalonRoutine(driveBack));
		return new SequentialRoutine(sequence);
//		return new SafetyTurnAngleRoutine(20);
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
