package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoMode;
import com.palyrobotics.frc2017.auto.AutoModeEndedException;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
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
public class SidePegAutoMode extends AutoMode {
	// Might pivot differently when turning left vs right
	public final double kSidePegTurnAngleDegrees = 60;

	// Represents the peg we are going for
	public enum SideAutoVariant {
		LEFT, RIGHT
	}
	private final SideAutoVariant mVariant;
	private SequentialRoutine mSequentialRoutine;

	private CANTalonOutput.CANTalonOutputFactory distanceProvider;
	
	private DriveSignal driveForward = DriveSignal.getNeutralSignal();
	private DriveSignal driveToAirship = DriveSignal.getNeutralSignal();

	public SidePegAutoMode(SideAutoVariant direction) {
		mVariant = direction;
		distanceProvider = new CANTalonOutput.CANTalonOutputFactory();
	}

	@Override
	protected void execute() throws AutoModeEndedException {
		runRoutine(mSequentialRoutine);
	}

	@Override
	public void prestart() {
		System.out.println("Starting "+this.toString()+" Auto Mode");
		
		driveForward.leftMotor = new CANTalonOutput();
		driveForward.leftMotor.setPosition(distanceProvider.withDistance(Constants.kSidePegDistanceForwardInches));
		driveForward.rightMotor = new CANTalonOutput();
		driveForward.rightMotor.setPosition(distanceProvider.withDistance(Constants.kSidePegDistanceForwardInches));

		driveToAirship.leftMotor = new CANTalonOutput();
		driveToAirship.leftMotor.setPosition(distanceProvider.withDistance(Constants.kSidePegDistanceToAirshipInches));
		driveToAirship.rightMotor = new CANTalonOutput();
		driveToAirship.rightMotor.setPosition(distanceProvider.withDistance(Constants.kSidePegDistanceToAirshipInches));
		
		ArrayList<Routine> score = new ArrayList<Routine>();
		score.add(new CANTalonRoutine(driveToAirship));
//		score.add(slider score auto)

		ArrayList<Routine> sequence = new ArrayList<Routine>();
		sequence.add(new CANTalonRoutine(driveForward));
		if (mVariant == SideAutoVariant.LEFT) {
			sequence.add(new BBTurnAngleRoutine(-kSidePegTurnAngleDegrees));
		} else {
			sequence.add(new BBTurnAngleRoutine(kSidePegTurnAngleDegrees));
		}
		sequence.add(new CANTalonRoutine(driveToAirship));
		mSequentialRoutine = new SequentialRoutine(sequence);
	}
	
	@Override
	public String toString() {
		return (mVariant == SideAutoVariant.LEFT) ? "LeftPeg" : "RightPeg";
	}
}
