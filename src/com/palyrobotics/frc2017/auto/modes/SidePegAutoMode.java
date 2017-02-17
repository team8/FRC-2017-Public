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
	
	public enum PostSideAutoVariant {
		HIT_CLOSE_HOPPER,
		MOVE_TO_LOADING_STATION,
		NONE
	}
	
	private final SideAutoVariant mVariant;
	private final PostSideAutoVariant mPost;
	private SequentialRoutine mSequentialRoutine;

	private CANTalonOutput.CANTalonOutputFactory distanceProvider;
	
	private DriveSignal driveForward = DriveSignal.getNeutralSignal();
	private DriveSignal driveToAirship = DriveSignal.getNeutralSignal();
	
	private DriveSignal backUp = DriveSignal.getNeutralSignal();
	private DriveSignal driveToPerpendicular = DriveSignal.getNeutralSignal();
	private DriveSignal backUpIntoHopper = DriveSignal.getNeutralSignal();
	
	private DriveSignal driveTowardsNeutralZone = DriveSignal.getNeutralSignal();
	
	public SidePegAutoMode(SideAutoVariant direction, PostSideAutoVariant postDrive) {
		mVariant = direction;
		mPost = postDrive;
		distanceProvider = new CANTalonOutput.CANTalonOutputFactory();
		distanceProvider.P = 0.15;
		distanceProvider.I = 0.0002;
		distanceProvider.D = 1;
		distanceProvider.F = 0;
		distanceProvider.izone = 750;
		distanceProvider.rampRate = 0;
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
		
		// Add the variants
		backUp.leftMotor = new CANTalonOutput();
		backUp.leftMotor.setPosition(distanceProvider.withDistance(-3));
		backUp.rightMotor = new CANTalonOutput();
		backUp.rightMotor.setPosition(distanceProvider.withDistance(-3));
		
		// distance to the line that is perpendicular to and intersects the hopper
		driveToPerpendicular.leftMotor = new CANTalonOutput();
		driveToPerpendicular.leftMotor.setPosition(distanceProvider.withDistance(4));
		driveToPerpendicular.rightMotor = new CANTalonOutput();
		driveToPerpendicular.rightMotor.setPosition(distanceProvider.withDistance(4));
		
		backUpIntoHopper.leftMotor = new CANTalonOutput();
		backUpIntoHopper.leftMotor.setPosition(distanceProvider.withDistance(-8));
		backUpIntoHopper.rightMotor = new CANTalonOutput();
		backUpIntoHopper.rightMotor.setPosition(distanceProvider.withDistance(-8));
		
		driveTowardsNeutralZone.leftMotor = new CANTalonOutput();
		driveTowardsNeutralZone.leftMotor.setPosition(distanceProvider.withDistance(15));
		driveTowardsNeutralZone.rightMotor = new CANTalonOutput();
		driveTowardsNeutralZone.rightMotor.setPosition(distanceProvider.withDistance(15));
		
		/**
		 * VARIANTS WORK AS FOLLOWS:
		 * 
		 * NONE:
		 * - Do nothing
		 * 
		 * HIT_CLOSE_HOPPER:
		 * - Drive to side peg like normal
		 * - Back up, rotate, drive to the perpendicular to the hopper, turn 90 degrees, back up into the hopper
		 * 
		 * MOVE_TO_LOADING_STATION:
		 * - Drive to side peg like normal
		 * - Back up, rotate, drive up a little bit.  Rotate again towards the loading station (only if on the left side), drive 
		 */
		switch (this.mPost) {
			case NONE:
				break;
			case HIT_CLOSE_HOPPER:
				sequence.add(new CANTalonRoutine(backUp));
				if (mVariant == SideAutoVariant.LEFT) {
					sequence.add(new BBTurnAngleRoutine(kSidePegTurnAngleDegrees));
				} else {
					sequence.add(new BBTurnAngleRoutine(-kSidePegTurnAngleDegrees));
				}
				sequence.add(new CANTalonRoutine(driveToPerpendicular));
				if (mVariant == SideAutoVariant.LEFT) {
					sequence.add(new BBTurnAngleRoutine(90));
				} else {
					sequence.add(new BBTurnAngleRoutine(-90));
				}
				sequence.add(new CANTalonRoutine(backUpIntoHopper));
				break;
				
			case MOVE_TO_LOADING_STATION:
				sequence.add(new CANTalonRoutine(backUp));
				if (mVariant == SideAutoVariant.LEFT) {
					sequence.add(new BBTurnAngleRoutine(kSidePegTurnAngleDegrees));
				} else {
					sequence.add(new BBTurnAngleRoutine(-kSidePegTurnAngleDegrees));
				}
				sequence.add(new CANTalonRoutine(driveToPerpendicular));
				if (mVariant == SideAutoVariant.LEFT) sequence.add(new BBTurnAngleRoutine(25));
				sequence.add(new CANTalonRoutine(driveTowardsNeutralZone));
				break;				
		}
		
		mSequentialRoutine = new SequentialRoutine(sequence);
	}
	
	@Override
	public String toString() {
		return (mVariant == SideAutoVariant.LEFT) ? "LeftPeg" : "RightPeg";
	}
}
