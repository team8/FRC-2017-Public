package com.palyrobotics.frc2016.config;

import com.palyrobotics.frc2016.robot.team254.lib.util.ConstantsBase;

public class Constants extends ConstantsBase {
	public enum RobotName {
		STEIK, AEGIR, TYR, DERICA
	}
	public static final RobotName kRobotName = RobotName.STEIK;

	// Chezy Drive tuning
	public static double kDriveSensitivity = .85;
	public static double kNegativeInertiaScalar = 5.0;

	// Manual control speed tuning
	public static double kManualIntakeSpeed = 1.0;
	public static double kManualExhaustSpeed = -1.0;

	// DriveStraightController gains
	public static double kDriveMaxSpeedInchesPerSec = 80.0;
	public static double kDriveMaxAccelInchesPerSec2 = 10.0;
	public static double kDrivePositionKp = 0.7;
	public static double kDrivePositionKi = 0;
	public static double kDrivePositionKd = .07;
	public static double kDrivePositionKv = 0.008;
	public static double kDrivePositionKa = 0.0017;
	// PID Tuning for turning to straighten
	public static double kDriveStraightKp = 00;
	public static double kDriveStraightKi = 0;
	public static double kDriveStraightKd = 0.04;
	public static double kDriveOnTargetError = 1.5;
	public static double kDrivePathHeadingFollowKp = 0.01;

	//Encoder Turn in Place Controller gains
	public static double kEncoderTurnKp = 0.07;
	public static double kEncoderTurnKi = 0.01;
	public static double kEncoderTurnKd = 0.007;
	public static double kTyrDegreeToDistance = 0;
	public static double kAcceptableEncoderTurnError = 2;
	
	// Gyro Turn in Place controller gains
	public static double kGyroTurnKp = 0.195E-1;
	public static double kGyroTurnKi = 0.04;
	public static double kGyroTurnKd = 0.014E-1;
	public static double kAcceptableGyroTurnError = 2;
	public static double kAcceptableGyroTurnStopSpeed = 1.2;
	public static double kTurnAngleSpeed = .35;

	
	// TurnInPlaceController gains
	public static double kTurnMaxSpeedRadsPerSec = 4.5;
	public static double kTurnMaxAccelRadsPerSec2 = 4.5;
	public static double kTurnKp = 3.0;
	public static double kTurnKi = 0.18;
	public static double kTurnKd = 0.23;
	public static double kTurnKv = 0.085;
	public static double kTurnKa = 0.075;
	public static double kTurnOnTargetError = 0.1;

	// !!! End of editable Constants! !!!
	public static int kEndEditableArea = 0;

	// CANTalon Tuning
	public static float kPeakVoltage = 8.0f;

	// Compressor Ports DON'T WORK
	public static int kCompressorRelayPort = 0;

	/*
	 * STEIK ELECTRONIC CONSTANTS
	 */
	// DRIVETRAIN
	public static int kSteikLeftDriveMasterDeviceID  = 0;
	public static int kSteikLeftDriveSlaveDeviceID = 0;
	public static int kSteikLeftDriveFrontMotorPDP = 0;
	public static int kSteikLeftDriveBackMotorPDP = 0;
	public static int kSteikRightDriveMasterDeviceID = 0;
	public static int kSteikRightDriveSlaveDeviceID = 0;
	public static int kSteikRightDriveFrontMotorPDP = 0;
	public static int kSteikRightDriveBackMotorPDP = 0;

	//FLIPPERS
	public static int kSteikLeftFlipperPortExtend = 0;
	public static int kSteikLeftFlipperPortRetract = 0;
	public static int kSteikRightFlipperPortExtend = 0;
	public static int kSteikRightFlipperPortRetract = 0;

	// SLIDER
	public static int kSteikSliderMotorDeviceID = 0;
	public static int kSteikSliderMotorPDP = 0;
	public static int kSteikSliderLeftHallEffect = 0;
	public static int kSteikSliderMiddleHallEffect = 0;
	public static int kSteikSliderRightHallEffect = 0;

	//SPATULA
	public static int kSteikSpatulaPortExtend = 0;
	public static int kSteikSpatulaPortRetract = 0;
	
	//INTAKE
	public static int kSteikLeftIntakeMotorDeviceID = 0;
	public static int kSteikLeftIntakeMotorPDP = 0;
	public static int kSteikRightIntakeMotorDeviceID = 0;
	public static int kSteikRightIntakeMotorPDP = 0;

	// CLIMBER CONSTANTS
	public static int kSteikClimberMotorDeviceID = 2;
	public static int kSteikClimberMotorPDP = 3;

	public static int kSteikClimberEncoderDIOA = 4;
	public static int kSteikClimberEncoderDIOB = 5;

	/*
	 * AEGIR ELECTRONIC CONSTANTS
	 */
	public static int kAegirLeftDriveMasterDeviceID  = 0;
	public static int kAegirLeftDriveSlaveDeviceID = 0;
	public static int kAegirLeftDriveFrontMotorPDP = 0;
	public static int kAegirLeftDriveBackMotorPDP = 0;
	public static int kAegirRightDriveMasterDeviceID = 0;
	public static int kAegirRightDriveSlaveDeviceID = 0;
	public static int kAegirRightDriveFrontMotorPDP = 0;
	public static int kAegirRightDriveBackMotorPDP = 0;

	// FLIPPER
	public static int kAegirLeftFlipperPortExtend = 0;
	public static int kAegirLeftFlipperPortRetract = 0;
	public static int kAegirRightFlipperPortExtend = 0;
	public static int kAegirRightFlipperPortRetract = 0;

	// SLIDER
	public static int kAegirSliderMotorDeviceID = 0;
	public static int kAegirSliderMotorPDP = 0;
	public static int kAegirSliderLeftHallEffect = 0;
	public static int kAegirSliderMiddleHallEffect = 0;
	public static int kAegirSliderRightHallEffect = 0;

	//SPATULA
	public static int kAegirSpatulaPortExtend = 0;
	public static int kAegirSpatulaPortRetract = 0;

	//INTAKE
	public static int kAegirLeftIntakeMotorDeviceID = 0;
	public static int kAegirLeftIntakeMotorPDP = 0;
	public static int kAegirRightIntakeMotorDeviceID = 0;
	public static int kAegirRightIntakeMotorPDP = 0;

	// CLIMBER
	public static int kAegirClimberMotorDeviceID = 2;
	public static int kAegirClimberMotorPDP = 3;
	public static int kAegirClimberEncoderDIOA = 4;
	public static int kAegirClimberEncoderDIOB = 5;

	// !!! Physical constants

	// !!! Loop rate of normal Looper
	public static double kControlLoopsDt = 0.005;

	// !!! Loop rate of subsystem updates
	public static double kSubsystemLooperDt = 0.005;

	// Drive parameters
	public static double kDriveEncoderCountsPerRev = 250.0;
	public static double kDriveWheelSizeInches = 8; //pneumatic wheels


	@Override
	public String getFileLocation() {
		return "~/constants.txt";
	}

	static {
		new Constants().loadFromFile();
	}
}
