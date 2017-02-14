package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoMode;
import com.palyrobotics.frc2017.auto.AutoModeEndedException;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.BBTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.util.CANTalonOutput;
import com.palyrobotics.frc2017.util.archive.DriveSignal;

import java.util.ArrayList;

/**
 * Created by Nihar on 2/11/17.
 */
public class SidePegAutoMode extends AutoMode {
	public final double kSidePegDriveDistance = 10;
	// Might pivot differently when turning left vs right
	public final double kSidePegLeftTurnAngleDegrees = -60;
	public final double getkSidePegRightTurnAngleDegrees = 60;

	// Represents the peg we are going for
	public enum SideAutoVariant {
		LEFT, RIGHT
	}
	private final SideAutoVariant mVariant;

	private DriveSignal driveForward = DriveSignal.getNeutralSignal();
	private DriveSignal driveToAirship = DriveSignal.getNeutralSignal();

	public SidePegAutoMode(SideAutoVariant direction) {
		mVariant = direction;
		driveForward.leftMotor = new CANTalonOutput();
		driveForward.leftMotor.setPosition(0, 0, 0, 0, 0, 0, 0);
		driveForward.rightMotor = new CANTalonOutput();
		driveForward.rightMotor.setPosition(0, 0, 0, 0, 0, 0, 0);

		driveToAirship.leftMotor = new CANTalonOutput();
		driveToAirship.leftMotor.setPosition(0, 0, 0, 0, 0, 0, 0);
		driveToAirship.rightMotor = new CANTalonOutput();
		driveToAirship.rightMotor.setPosition(0, 0, 0, 0, 0, 0, 0);
	}

	@Override
	protected void execute() throws AutoModeEndedException {
		ArrayList<Routine> score = new ArrayList<Routine>();
		score.add(new CANTalonRoutine(driveToAirship));
//		score.add(slider score auto)

		ArrayList<Routine> sequence = new ArrayList<Routine>();
		sequence.add(new CANTalonRoutine(driveToAirship));
		if (mVariant == SideAutoVariant.LEFT) {
			sequence.add(new BBTurnAngleRoutine(60));
		} else {
			sequence.add(new BBTurnAngleRoutine(-60));
		}
		SequentialRoutine sequentialRoutine = new SequentialRoutine(sequence);
		runRoutine(sequentialRoutine);
	}

	@Override
	public String toString() {
		return (mVariant == SideAutoVariant.LEFT) ? "LeftPeg" : "RightPeg";
	}

	@Override
	public void prestart() {
		System.out.println("Starting "+this.toString()+" auto mode");
	}
}
