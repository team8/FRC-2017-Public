package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.behavior.ParallelRoutine;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SequentialRoutine;
import com.palyrobotics.frc2017.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.BBTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.EncoderTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.drive.SafetyTurnAngleRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.SliderDistancePositioningAutocorrectRoutine;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.subsystems.Slider.SliderTarget;
import com.palyrobotics.frc2017.util.archive.DriveSignal;

import java.util.ArrayList;

import javax.sound.midi.SysexMessage;

/**
 * Created by Nihar on 2/11/17.
 * Goes for side peg autonomous
 * Configured for left vs right
 * Can use a gyro bang bang or an encoder turn angle loop
 */
public class SidePegAutoMode extends AutoModeBase {
	// Represents the peg we are going for
	public enum SideAutoVariant {
		RED_RIGHT, BLUE_RIGHT,
		RED_LEFT, BLUE_LEFT
	}
	
	public enum PostSideAutoVariant {
		HIT_CLOSE_HOPPER,
		MOVE_TO_LOADING_STATION,
		NONE
	}
	
	private final SideAutoVariant mVariant;
	private final PostSideAutoVariant mPost;
	private final boolean mShouldCenterSlider;
	private final boolean mShouldUseGyro;
	private SequentialRoutine mSequentialRoutine;

	private Gains mGains;
	
	private DriveSignal driveForward = DriveSignal.getNeutralSignal();
	private DriveSignal driveToAirship = DriveSignal.getNeutralSignal();
	
	private DriveSignal backUp = DriveSignal.getNeutralSignal();
	private DriveSignal driveToPerpendicular = DriveSignal.getNeutralSignal();
	private DriveSignal backUpIntoHopper = DriveSignal.getNeutralSignal();
	
	private DriveSignal driveTowardsNeutralZone = DriveSignal.getNeutralSignal();
	
	public SidePegAutoMode(SideAutoVariant direction, boolean shouldCenterSlider,
						   boolean shouldUseGyro,
						   PostSideAutoVariant postDrive) {
		mVariant = direction;
		mPost = postDrive;
		mShouldCenterSlider = shouldCenterSlider;
		mShouldUseGyro = shouldUseGyro;
		if(Constants.kRobotName == Constants.RobotName.DERICA) {
			mGains = Gains.dericaPosition;
		} else {
			mGains = (Constants.kRobotName == Constants.RobotName.STEIK) ? Gains.steikPosition : Gains.aegirDriveMotionMagicGains;
		}
	}

	@Override
	public Routine getRoutine() {
		if (mShouldCenterSlider) {
			ArrayList<Routine> parallel = new ArrayList<>();
			parallel.add(new SliderDistancePositioningAutocorrectRoutine(SliderTarget.CENTER));
			parallel.add(mSequentialRoutine);
			return new ParallelRoutine(parallel);
		}
		return mSequentialRoutine;
	}

	@Override
	public void prestart() {
		System.out.println("Starting "+this.toString()+" Auto Mode");
		double driveToAirshipSetpoint = 0;
		if(mVariant == SidePegAutoMode.SideAutoVariant.RED_RIGHT || mVariant == SidePegAutoMode.SideAutoVariant.BLUE_LEFT) {
			driveToAirshipSetpoint = Constants.kSidePegDistanceToAirshipBoilerInches * 
					((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
							: Constants.kDriveTicksPerInch);
		} else if(mVariant == SidePegAutoMode.SideAutoVariant.RED_LEFT || mVariant == SidePegAutoMode.SideAutoVariant.BLUE_RIGHT) {
			driveToAirshipSetpoint = Constants.kSidePegDistanceToAirshipLoadingStationInches * 
					((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
							: Constants.kDriveTicksPerInch);
		} else {
			System.out.println("No Side Auto Variant");
		}
		driveToAirship.leftMotor.setMotionMagic(driveToAirshipSetpoint, mGains,
			Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
		driveToAirship.rightMotor.setMotionMagic(driveToAirshipSetpoint, mGains,
				Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);

		ArrayList<Routine> sequence = new ArrayList<>();
		
		// NOTE: Intentional switch case falling
		// For Red Left = Blue Right, Red Right = Blue Left
		double driveForwardSetpoint;
		switch (mVariant) {
			// loading station side
			case RED_LEFT:
				driveForwardSetpoint = Constants.kSidePegDistanceLoadingStationInches * 
				((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
						: Constants.kDriveTicksPerInch);
				break;
			case BLUE_RIGHT:
				driveForwardSetpoint = Constants.kSidePegDistanceLoadingStationInches * 
					((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
							: Constants.kDriveTicksPerInch);
				break;
			// boiler side
			case RED_RIGHT:
				driveForwardSetpoint = Constants.kSidePegDistanceBoilerInches * 
				((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
						: Constants.kDriveTicksPerInch);
				break;
			case BLUE_LEFT:
				driveForwardSetpoint = Constants.kSidePegDistanceBoilerInches * 
					((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
							: Constants.kDriveTicksPerInch);
				break;
			default:
				System.err.println("What in tarnation no side peg distance");
				driveForwardSetpoint = 0;
				break;
		}
		driveForward.leftMotor.setMotionMagic(driveForwardSetpoint, mGains,
				Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
		driveForward.rightMotor.setMotionMagic(driveForwardSetpoint, mGains,
				Gains.kAegirDriveMotionMagicCruiseVelocity, Gains.kAegirDriveMotionMagicMaxAcceleration);
		sequence.add(new CANTalonRoutine(driveForward, true));
		// Added drive dist sequence
		
		// NOTE: switch case falling, split by lefts vs rights
		switch (mVariant) {
			case RED_LEFT:
				if (mShouldUseGyro) {
					sequence.add(new SafetyTurnAngleRoutine(Constants.kSidePegTurnAngleDegrees));
				} else {
					sequence.add(new EncoderTurnAngleRoutine(Constants.kSidePegTurnAngleDegrees));
				}
				break;
			case BLUE_LEFT:
				if (mShouldUseGyro) {
					sequence.add(new SafetyTurnAngleRoutine(Constants.kSidePegTurnAngleDegrees));
				} else {
					sequence.add(new EncoderTurnAngleRoutine(Constants.kSidePegTurnAngleDegrees));
				}
				break;
			case RED_RIGHT:
				if (mShouldUseGyro) {
					sequence.add(new SafetyTurnAngleRoutine(-Constants.kSidePegTurnAngleDegrees));
				} else {
					sequence.add(new EncoderTurnAngleRoutine(-Constants.kSidePegTurnAngleDegrees));
				}
				break;
			case BLUE_RIGHT:
				if (mShouldUseGyro) {
					sequence.add(new SafetyTurnAngleRoutine(-Constants.kSidePegTurnAngleDegrees));
				} else {
					sequence.add(new EncoderTurnAngleRoutine(-Constants.kSidePegTurnAngleDegrees));
				}
				break;
		}
		sequence.add(new CANTalonRoutine(driveToAirship, true));
		sequence.add(new TimeoutRoutine(2.5));	// Wait 2.5s so pilot can pull gear out
		
		mSequentialRoutine = new SequentialRoutine(sequence);
	}
	
	@Override
	public String toString() {
		String name;
		switch (mVariant) {
			case RED_LEFT:
				name = "RedLeftSidePeg";
				break;
			case RED_RIGHT:
				name = "RedRightSidePeg";
				break;
			case BLUE_LEFT:
				name = "BlueLeftSidePeg";
				break;
			case BLUE_RIGHT:
				name = "BlueRightSidePeg";
				break;
			default:
				name = "SidePeg";
				break;
		}
		if (mShouldCenterSlider) {
			name += "SliderCenter";
		} else {
			name += "NotSliderCenter";
		}
		if (mShouldUseGyro) {
			name += "Gyro";
		} else {
			name += "EncoderTurn";
		}
		return name;
	}
}



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