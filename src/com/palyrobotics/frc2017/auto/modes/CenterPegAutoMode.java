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
import com.palyrobotics.frc2017.util.CANTalonOutput;
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
	private SequentialRoutine mSequentialRoutine;

	private CANTalonOutput.CANTalonOutputFactory distanceProvider;
	private int kIzone;

	public CenterPegAutoMode(CenterAutoVariant direction) {
		mVariant = direction;
		switch (Constants.kRobotName) {
			case DERICA:
				distanceProvider = new CANTalonOutput.CANTalonOutputFactory(Constants2016.kDericaPositionkP, 
																			Constants2016.kDericaPositionkI, 
																			Constants2016.kDericaPositionkD,
																			Constants2016.kDericaPositionkF, 
																			Constants2016.kDericaPositionkIzone,
																			Constants2016.kDericaPositionRampRate);
			case AEGIR:
				distanceProvider = new CANTalonOutput.CANTalonOutputFactory(Constants.kAegirDriveDistancekP, 
																			Constants.kAegirDriveDistancekI, 
																			Constants.kAegirDriveDistancekD,
																			Constants.kAegirDriveDistancekF, 
																			Constants.kAegirDriveDistancekIzone,
																			Constants.kAegirDriveDistancekRampRate);
			case STEIK:
				distanceProvider = new CANTalonOutput.CANTalonOutputFactory(Constants.kSteikDriveDistancekP, 
																			Constants.kSteikDriveDistancekI, 
																			Constants.kSteikDriveDistancekD,
																			Constants.kSteikDriveDistancekF, 
																			Constants.kSteikDriveDistancekIzone,
																			Constants.kSteikDriveDistancekRampRate);
		}
	}

	@Override
	protected void execute() throws AutoModeEndedException {
		runRoutine(mSequentialRoutine);
	}

	@Override
	public void prestart() {
		String log = "Starting Center Peg Auto Mode";
		// Construct sequence of routines to run
		ArrayList<Routine> sequence = new ArrayList<Routine>();
		// Straight drive distance to the center peg
		DriveSignal driveForward = DriveSignal.getNeutralSignal();
		driveForward.leftMotor.setPosition(distanceProvider.withDistance(Constants.kCenterPegDistanceInches));
		driveForward.rightMotor.setPosition(distanceProvider.withDistance(Constants.kCenterPegDistanceInches));
		sequence.add(new CANTalonRoutine(driveForward));
		sequence.add(new TimeoutRoutine(2.5));

		// Back off from the peg after 2.5 seconds
		DriveSignal driveBack = DriveSignal.getNeutralSignal();
		driveBack.leftMotor.setPosition(distanceProvider.withDistance(-5));
		driveBack.rightMotor.setPosition(distanceProvider.withDistance(-5));

		// If variant includes a cross, drive past the airship after turn angle
		DriveSignal passAirship = DriveSignal.getNeutralSignal();
		passAirship.leftMotor.setPosition(distanceProvider.withDistance(7));
		passAirship.rightMotor.setPosition(distanceProvider.withDistance(7));

		DriveSignal crossOver = DriveSignal.getNeutralSignal();
		crossOver.leftMotor.setPosition(distanceProvider.withDistance(15));
		crossOver.rightMotor.setPosition(distanceProvider.withDistance(15));
		switch (mVariant) {
			case NOTHING:
				break;
			case CROSS_LEFT:
				sequence.add(new CANTalonRoutine(driveBack));
				sequence.add(new BBTurnAngleRoutine(-20));
				sequence.add(new CANTalonRoutine(passAirship));
				sequence.add(new BBTurnAngleRoutine(20));
				sequence.add(new CANTalonRoutine(crossOver));
				log += " and crossing left";
				break;
			case CROSS_RIGHT:
				sequence.add(new CANTalonRoutine(driveBack));
				sequence.add(new BBTurnAngleRoutine(20));
				sequence.add(new CANTalonRoutine(passAirship));
				sequence.add(new BBTurnAngleRoutine(-20));
				sequence.add(new CANTalonRoutine(crossOver));
				log += " and crossing right";
				break;
		}

		mSequentialRoutine = new SequentialRoutine(sequence);
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
