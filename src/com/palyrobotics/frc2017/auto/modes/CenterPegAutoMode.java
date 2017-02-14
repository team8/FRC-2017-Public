package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoMode;
import com.palyrobotics.frc2017.auto.AutoModeEndedException;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.BBTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.util.archive.DriveSignal;

import java.util.ArrayList;

/**
 * Created by Nihar on 2/11/17.
 */
public class CenterPegAutoMode extends AutoMode {
	// Represents the variation of center peg auto based on what to do after scoring
	public enum CenterAutoVariant {
		NOTHING, CROSS_LEFT, CROSS_RIGHT
	}
	private final CenterAutoVariant mVariant;
	private SequentialRoutine sequentialRoutine;

	private double kp, ki, kd, kf, kRampRate;
	private int kizone;

	public CenterPegAutoMode(CenterAutoVariant direction) {
		mVariant = direction;
		switch (Constants.kRobotName) {
			case DERICA:
				kp = Constants2016.kDericaPositionkP;
				ki = Constants2016.kDericaPositionkI;
				kd = Constants2016.kDericaPositionkD;
				kf = Constants2016.kDericaPositionkF;
				kizone = Constants2016.kDericaPositionkIzone;
				kRampRate = Constants2016.kDericaPositionRampRate;
			case AEGIR:
				kp = Constants.kAegirDriveDistancekP;
				ki = Constants.kAegirDriveDistancekI;
				kd = Constants.kAegirDriveDistancekD;
				kf = Constants.kAegirDriveDistancekF;
				kizone = Constants.kAegirDriveDistancekIzone;
				kRampRate = Constants.kAegirDriveDistancekRampRate;
			case STEIK:
				kp = Constants.kSteikDriveDistancekP;
				ki = Constants.kSteikDriveDistancekI;
				kd = Constants.kSteikDriveDistancekD;
				kf = Constants.kSteikDriveDistancekF;
				kizone = Constants.kSteikDriveDistancekIzone;
				kRampRate = Constants.kSteikDriveDistancekRampRate;
		}
	}

	@Override
	protected void execute() throws AutoModeEndedException {
		runRoutine(sequentialRoutine);
	}

	@Override
	public void prestart() {
		String log = "Starting Center Peg Auto Mode";
		// Construct sequence of routines to run
		ArrayList<Routine> sequence = new ArrayList<Routine>();
		// Straight drive distance to the center peg
		DriveSignal driveForward = DriveSignal.getNeutralSignal();
		driveForward.leftMotor.setPosition(10, kp, ki, kd, kf, kizone, kRampRate);
		driveForward.rightMotor.setPosition(10, kp, ki, kd, kf, kizone, kRampRate);
		sequence.add(new CANTalonRoutine(driveForward));
		sequence.add(new TimeoutRoutine(2.5));

		// Back off from the peg after 2.5 seconds
		DriveSignal driveBack = DriveSignal.getNeutralSignal();
		driveBack.leftMotor.setPosition(-5, kp, ki, kd, kf, kizone, kRampRate);
		driveBack.rightMotor.setPosition(-5, kp, ki, kd, kf, kizone, kRampRate);

		// If variant includes a cross, drive past the airship after turn angle
		DriveSignal passAirship = DriveSignal.getNeutralSignal();
		passAirship.leftMotor.setPosition(7, kp, ki, kd, kf, kizone, kRampRate);
		passAirship.rightMotor.setPosition(7, kp, ki, kd, kf, kizone, kRampRate);

		DriveSignal crossOver = DriveSignal.getNeutralSignal();
		crossOver.leftMotor.setPosition(15, kp, ki, kd, kf, kizone, kRampRate);
		crossOver.rightMotor.setPosition(15, kp, ki, kd, kf, kizone, kRampRate);
		switch (mVariant) {
			case NOTHING:
				break;
			case CROSS_LEFT:
				sequence.add(new CANTalonRoutine(driveBack));
				sequence.add(new BBTurnAngleRoutine(-20));
				sequence.add(new CANTalonRoutine(passAirship));
				sequence.add(new CANTalonRoutine(crossOver));
				log += " and crossing left";
				break;
			case CROSS_RIGHT:
				sequence.add(new CANTalonRoutine(driveBack));
				sequence.add(new BBTurnAngleRoutine(20));
				sequence.add(new CANTalonRoutine(passAirship));
				sequence.add(new CANTalonRoutine(crossOver));
				log += " and crossing right";
				break;
		}

		sequentialRoutine = new SequentialRoutine(sequence);
		System.out.println(log);
	}
	@Override
	public String toString() {
		String name = "CenterPeg";
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

}
