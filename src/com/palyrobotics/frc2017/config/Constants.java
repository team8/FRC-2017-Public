package com.palyrobotics.frc2017.config;

import com.palyrobotics.frc2017.robot.team254.lib.util.ConstantsBase;

public class Constants extends ConstantsBase {
	public enum RobotName {
		STEIK, AEGIR, DERICA
	}
	public static final RobotName kRobotName = RobotName.AEGIR;

	// Cheesy Drive tuning
	public static double kDriveSensitivity = .85;
	public static double kNegativeInertiaScalar = 5.0;

	// Manual control speed tuning
	public static double kManualIntakeSpeed = 1.0;
	public static double kManualExhaustSpeed = -1.0;

	/*
	 * Control loop constants for both robots
	 */
	public static double kTurnInPlaceSpeed = 0.5;
	public static double kTurnEncoderMaxAccel = 10;
	public static double kTurnEncoderMaxVel = 10;
	// Unit Conversions for CANTalons
	// TODO: Calculate the unit conversions for native units and inches, rpm, etc as needed
	/*
	 * Measured 100 in. = 2910 ticks
	 */
	public static double kDriveInchesToTicks = 29.1;
	// TODO: tune degrees to ticks
	public static double kDriveDegreesToTicks = 1000;
	
	public static final double kPotentiometerRightPos = 2172;
	public static final double kPotentiometerLeftPos = 3452;
	public static final double kPotentiometerCenterPos = (kPotentiometerRightPos + kPotentiometerLeftPos) / 2;
	
	// Tolerances
	public static double kAcceptableDrivePositionError = 100;
	public static double kAcceptableDriveVelocityError = 40;
	public static double kAcceptableTurnAngleError = 0.5;
	public static double kAcceptableSliderPositionError;

	public static double kSliderMaxVoltage = 6.0; // open loop limit
	public static double kSliderPeakOutputVoltage = 4.0; // closed loop limit
	public static double kClimberMaxVoltage = 12.0;

	/* !!! End of editable Constants! !!!
	 **********************************************************************************
	 */
	public static int kEndEditableArea = 0;



	/*
	 * ************************************
	 *  STEIK ELECTRONIC CONSTANTS
	 * ************************************
	 */
	// PDP
	public static int kSteikPDPDeviceID = 11;
	
	// DRIVETRAIN
	public static int kSteikLeftDriveMasterDeviceID  = 0;
	public static int kSteikLeftDriveSlaveDeviceID = 0;
	public static int kSteikLeftDriveOtherSlaveDeviceID = 0;
	public static int kSteikLeftDriveFrontMotorPDP = 0;
	public static int kSteikLeftDriveBackMotorPDP = 0;
	public static int kSteikLeftDriveThirdMotorPDP = 0;
	public static int kSteikRightDriveMasterDeviceID = 0;
	public static int kSteikRightDriveSlaveDeviceID = 0;
	public static int kSteikRightDriveOtherSlaveDeviceID = 0;
	public static int kSteikRightDriveFrontMotorPDP = 0;
	public static int kSteikRightDriveBackMotorPDP = 0;
	public static int kSteikRightDriveThirdMotorPDP = 0;

	// FLIPPERS
	public static int kSteikLeftFlipperPortExtend = 0;
	public static int kSteikLeftFlipperPortRetract = 0;
	public static int kSteikRightFlipperPortExtend = 0;
	public static int kSteikRightFlipperPortRetract = 0;

	// SLIDER
	public static int kSteikSliderMotorDeviceID = 0;
	public static int kSteikSliderMotorSpeed = 0;
	public static int kSteikSliderPotentiometer = 0;
	public static int kSteikSliderPotentiometerFullRange = 0;
	public static int kSteikSliderPotentiometerOffset;

	// SPATULA
	public static int kSteikSpatulaPortExtend = 1;
	public static int kSteikSpatulaPortRetract = 0;

	// INTAKE
	public static int kSteikLeftIntakeMotorDeviceID = 0;
	public static int kSteikLeftIntakeMotorPDP = 0;
	public static int kSteikRightIntakeMotorDeviceID = 0;
	public static int kSteikRightIntakeMotorPDP = 0;

	// CLIMBER
	public static int kSteikClimberMotorDeviceID = 9;
	public static int kSteikClimberMotorPDP = 14;

	/*
	 * ************************************
	 *  AEGIR ELECTRONIC CONSTANTS
	 * ************************************
	 */
	// PDP
	public static int kAegirPDPDeviceID = 11;
	
	// DRIVETRAIN
	public static int kAegirLeftDriveMasterDeviceID  = 1;
	public static int kAegirLeftDriveSlaveDeviceID = 2;
	public static int kAegirLeftDriveOtherSlaveDeviceID = 3;
	public static int kAegirLeftDriveFrontMotorPDP = 0;
	public static int kAegirLeftDriveBackMotorPDP = 0;
	public static int kAegirLeftDriveThirdMotorPDP = 0;
	public static int kAegirRightDriveMasterDeviceID = 6;
	public static int kAegirRightDriveSlaveDeviceID = 5;
	public static int kAegirRightDriveOtherSlaveDeviceID = 4;
	public static int kAegirRightDriveFrontMotorPDP = 0;
	public static int kAegirRightDriveBackMotorPDP = 0;
	public static int kAegirRightDriveThirdMotorPDP = 0;
	
	// FLIPPERS
	public static int kAegirLeftFlipperPortExtend = 0;
	public static int kAegirLeftFlipperPortRetract = 1;
	public static int kAegirRightFlipperPortExtend = 2;
	public static int kAegirRightFlipperPortRetract = 3;

	// SLIDER
	public static int kAegirSliderMotorDeviceID = 10;
	public static int kAegirSliderMotorSpeed = 0;
	public static int kAegirSliderPotentiometerPort = 3;
	public static int kAegirSliderPotentiometerFullRange = 0;
	public static int kAegirSliderPotentiometerOffset;

	// SPATULA
	public static int kAegirSpatulaPortExtend = 1;
	public static int kAegirSpatulaPortRetract = 0;

	// INTAKE
	public static int kAegirLeftIntakeMotorDeviceID = 14;
	public static int kAegirLeftIntakeMotorPDP = 11;
	public static int kAegirRightIntakeMotorDeviceID = 12;
	public static int kAegirRightIntakeMotorPDP = 13;

	// CLIMBER
	public static int kAegirClimberMotorDeviceID = 9;
	public static int kAegirClimberMotorPDP = 14;

	// !!! Physical constants
	public static double kBaseLineDistanceInches = 93.3;
	public static double kCenterPegDistanceInches = 110;
	// For side peg auto, robot starts 4 gear widths (44") from center of alliance wall
	public static double kSidePegDistanceForwardInches = 122.5688;
	public static double kSidePegDistanceToAirshipInches = 17.5688;

	// !!! Loop rate of normal Looper
	public static double kControlLoopsDt = 0.005;

	// !!! Loop rate of subsystem updates
	public static double kSubsystemLooperDt = 0.005;
	
	public static double kSubsystemPrintLooperDt = 1.0;


	@Override
	public String getFileLocation() {
		return "~/constants.txt";
	}

	static {
		new Constants().loadFromFile();
	}
}
