package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.EncoderTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.SafetyTurnAngleRoutine;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.util.archive.DriveSignal;

import java.util.ArrayList;

/**
 * Created by Nihar on 2/11/17.
 * BBTurnAngle might be replaced with EncoderTurnAngle if no gyro
 */
public class SidePegAutoMode extends AutoModeBase {
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

	private Gains mGains;
	
	private DriveSignal driveForward = DriveSignal.getNeutralSignal();
	private DriveSignal driveToAirship = DriveSignal.getNeutralSignal();
	
	private DriveSignal backUp = DriveSignal.getNeutralSignal();
	private DriveSignal driveToPerpendicular = DriveSignal.getNeutralSignal();
	private DriveSignal backUpIntoHopper = DriveSignal.getNeutralSignal();
	
	private DriveSignal driveTowardsNeutralZone = DriveSignal.getNeutralSignal();
	
	public SidePegAutoMode(SideAutoVariant direction, PostSideAutoVariant postDrive) {
		mVariant = direction;
		mPost = postDrive;
		if(Constants.kRobotName == Constants.RobotName.DERICA) {
			mGains = Gains.dericaPosition;
		} else {
			mGains = (Constants.kRobotName == Constants.RobotName.STEIK) ? Gains.steikPosition : Gains.aegirDriveMotionMagicGains;
		}
	}

	@Override
	public Routine getRoutine() {
		return mSequentialRoutine;
	}

	@Override
	public void prestart() {
		System.out.println("Starting "+this.toString()+" Auto Mode");
		
		double driveForwardSetpoint = Constants.kSidePegDistanceForwardInches * 
				((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
						: Constants.kDriveTicksPerInch);
		driveForward.leftMotor.setMotionMagic(driveForwardSetpoint, mGains,
			Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
		driveForward.rightMotor.setMotionMagic(driveForwardSetpoint, mGains,
				Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);

		double driveToAirshipSetpoint = Constants.kSidePegDistanceToAirshipInches * 
				((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
						: Constants.kDriveTicksPerInch);
		driveToAirship.leftMotor.setMotionMagic(driveToAirshipSetpoint, mGains,
			Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
		driveToAirship.rightMotor.setMotionMagic(driveToAirshipSetpoint, mGains,
				Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
		
		ArrayList<Routine> score = new ArrayList<>();
//		score.add(new CANTalonRoutine(driveToAirship));
//		score.add(slider score auto)

		ArrayList<Routine> sequence = new ArrayList<>();
		sequence.add(new CANTalonRoutine(driveForward, true));
		if (mVariant == SideAutoVariant.LEFT) {
			sequence.add(new EncoderTurnAngleRoutine(kSidePegTurnAngleDegrees));
		} else {
			sequence.add(new EncoderTurnAngleRoutine(-kSidePegTurnAngleDegrees));
		}
		sequence.add(new CANTalonRoutine(driveToAirship, true));
		sequence.add(new TimeoutRoutine(2.5));	// Wait 2.5s so pilot can pull gear out
		
//		//TODO: adjust distances and add relative setpoints
//		// Add the variants
//		backUp.leftMotor.setPosition(-3, mGains);
//		backUp.rightMotor.setPosition(-3, mGains);
//		
//		// distance to the line that is perpendicular to and intersects the hopper
//		driveToPerpendicular.leftMotor.setPosition(4, mGains);
//		driveToPerpendicular.rightMotor.setPosition(4, mGains);
//		
//		backUpIntoHopper.leftMotor.setPosition(-8, mGains);
//		backUpIntoHopper.rightMotor.setPosition(-8, mGains);
//		
//		driveTowardsNeutralZone.leftMotor.setPosition(15, mGains);
//		driveTowardsNeutralZone.rightMotor.setPosition(15, mGains);
//		
//		/**
//		 * VARIANTS WORK AS FOLLOWS:
//		 * 
//		 * NONE:
//		 * - Do nothing
//		 * 
//		 * HIT_CLOSE_HOPPER:
//		 * - Drive to side peg like normal
//		 * - Back up, rotate, drive to the perpendicular to the hopper, turn 90 degrees, back up into the hopper
//		 * 
//		 * MOVE_TO_LOADING_STATION:
//		 * - Drive to side peg like normal
//		 * - Back up, rotate, drive up a little bit.  Rotate again towards the loading station (only if on the left side), drive 
//		 */
//		switch (this.mPost) {
//			case NONE:
//				break;
//			case HIT_CLOSE_HOPPER:
//				sequence.add(new CANTalonRoutine(backUp));
//				if (mVariant == SideAutoVariant.LEFT) {
//					sequence.add(new EncoderTurnAngleRoutine(kSidePegTurnAngleDegrees));
//				} else {
//					sequence.add(new EncoderTurnAngleRoutine(-kSidePegTurnAngleDegrees));
//				}
//				sequence.add(new CANTalonRoutine(driveToPerpendicular));
//				if (mVariant == SideAutoVariant.LEFT) {
//					sequence.add(new EncoderTurnAngleRoutine(90));
//				} else {
//					sequence.add(new EncoderTurnAngleRoutine(-90));
//				}
//				sequence.add(new CANTalonRoutine(backUpIntoHopper));
//				break;
//				
//			case MOVE_TO_LOADING_STATION:
//				sequence.add(new CANTalonRoutine(backUp));
//				if (mVariant == SideAutoVariant.LEFT) {
//					sequence.add(new EncoderTurnAngleRoutine(kSidePegTurnAngleDegrees));
//				} else {
//					sequence.add(new EncoderTurnAngleRoutine(-kSidePegTurnAngleDegrees));
//				}
//				sequence.add(new CANTalonRoutine(driveToPerpendicular));
//				if (mVariant == SideAutoVariant.LEFT) sequence.add(new EncoderTurnAngleRoutine(25));
//				sequence.add(new CANTalonRoutine(driveTowardsNeutralZone));
//				break;				
//		}
		
		mSequentialRoutine = new SequentialRoutine(sequence);
	}
	
	@Override
	public String toString() {
		return (mVariant == SideAutoVariant.LEFT) ? "LeftPeg" : "RightPeg";
	}
}
