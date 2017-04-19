package com.palyrobotics.frc2017.config;

import com.palyrobotics.frc2017.robot.team254.lib.util.ConstantsBase;

public class Constants extends ConstantsBase {
	public enum RobotName {
		STEIK, DERICA
	}

	// Initialization constants
	public static final RobotName kRobotName = RobotName.STEIK;
	public static final boolean kCalibrateSliderWithPotentiometer = true;
	
	// Android app information
	public static String kPackageName = "com.frc8.team8vision";
	public static String kActivityName = "MainActivity";
	public static int kAndroidConnectionUpdateRate = 5;	// Update rate in milliseconds
	public static int kAndroidDataSocketUpdateRate = 100;
	public static int kAndroidVisionSocketUpdateRate = 10;
	public static int kMJPEGVisionSocketUpdateRate = 10;
	public static int kAndroidDataSocketPort = 8008;
	public static int kAndroidVisionSocketPort = 8009;
	public static int kMJPEGServerSocketPort = 1180;

	// Cheesy Drive

	//Sensitivities for how fast non-quickturn turning is
	public static double kLowGearDriveSensitivity = .70;
	public static double kHighGearDriveSensitivity = 0.85;
	
	//Sensitivities for quickturn
	public static double kLowGearQuickTurnSensitivity = 0.8;
	public static double kLowGearPreciseQuickTurnSensitivity = 0.35;
	public static double kHighGearQuickTurnSensitivity = 0.8;
	public static double kHighGearPreciseQuickTurnSensitivity = 0.35;
	
	//Threshold for quickturn sensitivity change
	public static double kQuickTurnSensitivityThreshold = 0.90;

	//The rate at which the QuickStopAccumulator will decrease
	public static double kQuickStopAccumulatorDecreaseRate = 0.8;

	//The value at which the QuickStopAccumulator will begin to decrease
	public static double kQuickStopAccumulatorDecreaseThreshold = 1.2;
	public static double kNegativeInertiaScalar = 5.0;
	
	//How much the QuickStopAccumulator is affected by the wheel
	//(1-alpha) is how much the QuickStopAccumulator is affected by the previous QuickStopAccumulator
	//Range: (0, 1)
	public static double kAlpha = 0.45;

	public static double kCyclesUntilStop = 50;

	// Manual control speed tuning
	public static double kManualIntakeSpeed = -1.0;
	public static double kManualExhaustSpeed = 1.0;

	/*
	 * Control loop constants for both robots
	 */
	public static double kTurnInPlacePower = 0.17; // for bang bang
	public static double kDriveMaxClosedLoopOutput = 8.0;
	// Unit Conversions for CANTalons
	public static double kDriveTicksPerInch = 360 / (3.95 * Math.PI);
	public static double kDriveInchesPerDegree = 0.99*21.5/90;
	public static double kDriveSpeedUnitConversion = 360 / (3.95 * Math.PI * 10);
	
	public static double kSliderTicksPerInch = 900;
	public static double kSliderRevolutionsPerInch = 0.218099;

	// Aegir: 2199 RIGHT  |  3499 LEFT
	// Vali: 2032 RIGHT | 3310 LEFT
	// new belly pan shifted vali
	public static final double kPotentiometerRightPos = 2032;
	public static final double kPotentiometerLeftPos = 3310;
	public static final double kPotentiometerCenterPos = (kPotentiometerRightPos + kPotentiometerLeftPos) / 2;
	
	// Tolerances
	public static double kAcceptableDrivePositionError = 15;
	public static double kAcceptableDriveVelocityError = 5;
	public static double kAcceptableTurnAngleError = 1; // 0.5
	public static double kAcceptableSliderPositionError;
	public static double kAcceptableGyroZeroError = 3;
	public static double kAcceptableEncoderZeroError = 10;

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
	// PDP slots for drivetrain 0, 1, 2, 3, 12, 13
	public static int kSteikLeftDriveMasterDeviceID  = 1;
	public static int kSteikLeftDriveSlaveDeviceID = 2;
	public static int kSteikLeftDriveOtherSlaveDeviceID = 3;
	public static int kSteikLeftDriveFrontMotorPDP = 0;
	public static int kSteikLeftDriveBackMotorPDP = 0;
	public static int kSteikLeftDriveThirdMotorPDP = 0;
	public static int kSteikRightDriveMasterDeviceID = 6;
	public static int kSteikRightDriveSlaveDeviceID = 5;
	public static int kSteikRightDriveOtherSlaveDeviceID = 4;
	public static int kSteikRightDriveFrontMotorPDP = 0;
	public static int kSteikRightDriveBackMotorPDP = 0;
	public static int kSteikRightDriveThirdMotorPDP = 0;
	
	// FLIPPERS
	public static int kSteikLeftFlipperPortExtend = 0;
	public static int kSteikLeftFlipperPortRetract = 1;
	public static int kSteikRightFlipperPortExtend = 2;
	public static int kSteikRightFlipperPortRetract = 3;

	// SLIDER
	public static int kSteikSliderMotorDeviceID = 10;
	public static int kSteikSliderMotorSpeed = 0;
	public static int kSteikSliderPotentiometerPort = 3;
	public static int kSteikSliderPotentiometerFullRange = 0;
	public static int kSteikSliderPotentiometerOffset;

	// SPATULA
	public static int kSteikSpatulaPortExtend = 1;
	public static int kSteikSpatulaPortRetract = 0;

	// INTAKE
	public static int kSteikIntakeMotorDeviceID = 0;
	public static int kSteikIntakeMotorPDP = 8;

	// CLIMBER
	public static int kSteikClimberMotorDeviceID = 9;
	public static int kSteikClimberMotorPDP = 14;

	// !!! Physical constants

	public static double kSidePegTurnAngleDegrees = 60;

	// !!! Loop rate of normal Looper
	public static double kNormalLoopsDt = 0.01;

	// !!! Loop rate of subsystem updates
	public static double kSubsystemLooperDt = 0.01;
	
	public static double kSubsystemPrintLooperDt = 0.01;

	@Override
	public String toString() {
	return "kLowGearDriveSensitivity "+kLowGearDriveSensitivity+
	"kHighGearDriveSensitivity "+kHighGearDriveSensitivity+
	"kLowGearQuickTurnSensitivity "+kLowGearQuickTurnSensitivity+
	"kHighGearQuickTurnSensitivity "+kHighGearQuickTurnSensitivity+
	"kQuickStopAccumulatorDecreaseRate "+kQuickStopAccumulatorDecreaseRate+
	"kQuickStopAccumulatorDecreaseThreshold "+kQuickStopAccumulatorDecreaseThreshold+
	"kNegativeInertiaScalar "+kNegativeInertiaScalar+
	"kAlpha "+ kAlpha +
	"kDriveTicksPerInch "+kDriveTicksPerInch +
	"kDriveInchesPerDegree" + kDriveInchesPerDegree +
	"kDriveSpeedUnitConversion "+kDriveSpeedUnitConversion +
	"kPotentiometerRightPos "+kPotentiometerRightPos +
	"kPotentiometerLeftPos "+kPotentiometerLeftPos
	;
	}
	
	@Override
	public String getFileLocation() {
		return "~/constants.txt";
	}

	static {
		new Constants().loadFromFile();
	}
}