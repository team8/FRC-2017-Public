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

	// Cheesy Drive tuning

	//Sensitivities for how fast turning is
	public static double kLowGearDriveSensitivity = .70;
	public static double kHighGearDriveSensitivity = 0.85;
	public static double kLowGearQuickTurnSensitivity = 0.8;
	public static double kHighGearQuickTurnSensitivity = 0.8;

	//The rate at which the QuickStopAccumulator will decrease
	public static double kQuickStopAccumulatorDecreaseRate = 0.8;

	//The value at which the QuickStopAccumulator will begin to decrease
	public static double kQuickStopAccumulatorDecreaseThreshold = 1.2;
	public static double kNegativeInertiaScalar = 5.0;
	
	//How much the QuickStopAccumulator is affected by the wheel
	//(1-alpha) is how much the QuickStopAccumulator is affected by the previous QuickStopAccumulator
	//Range: (0, 1)
	public static double kAlpha = 0.45;

	// Manual control speed tuning
	public static double kManualIntakeSpeed = 1.0;
	public static double kManualExhaustSpeed = -1.0;

	/*
	 * Control loop constants for both robots
	 */
	public static double kTurnInPlacePower = 0.2; // for bang bang
	public static double kDriveMaxClosedLoopOutput = 8.0;
	// Unit Conversions for CANTalons
	public static double kDriveTicksPerInch = 360 / (3.95 * Math.PI);
	public static double kDriveInchesPerDegree = 0.97*21.5/90;
	public static double kDriveSpeedUnitConversion = 360 / (3.95 * Math.PI * 10);
	
	public static double kSliderTicksPerInch = 900;
	public static double kSliderRevolutionsPerInch = 0.218099;

	// Aegir: 2172 RIGHT  |  3452 LEFT
	// Vali: 2036 RIGHT | 3314 LEFT
	public static final double kPotentiometerRightPos = 2172;
	public static final double kPotentiometerLeftPos = 3452;
	public static final double kPotentiometerCenterPos = (kPotentiometerRightPos + kPotentiometerLeftPos) / 2;
	
	// Tolerances
	public static double kAcceptableDrivePositionError = 5;
	public static double kAcceptableDriveVelocityError = 5;
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
	public static int kSteikLeftIntakeMotorDeviceID = 14;
	public static int kSteikLeftIntakeMotorPDP = 11;
	public static int kSteikRightIntakeMotorDeviceID = 12;
	public static int kSteikRightIntakeMotorPDP = 13;

	// CLIMBER
	public static int kSteikClimberMotorDeviceID = 9;
	public static int kSteikClimberMotorPDP = 14;

	// !!! Physical constants

	// Base line
	public static double k254BaseLineDistanceInches = 93.3;
	public static double kRedBaseLineDistanceInches = 93.3;
	public static double kBlueBaseLineDistanceInches = 93.3;

	// Center peg
	public static double k254CenterPegDistanceInches = 86;
	public static double kRedCenterPegDistanceInches = 113.75 - 31; // first number is to airship
	public static double kBlueCenterPegDistanceInches = 108.5 - 31; // first num is to airship

	// Side peg
	public static double k254LoadingStationForwardDistanceInches = 74;
	public static double k254LoadingStationAirshipDistanceInches = 84;//24.5688;
	public static double k254BoilerForwardDistanceInches = 60;
	public static double k254BoilerAirshipDistanceInches = 93;
	public static double kSidePegTurnAngleDegrees = 60;
	
	/*
	 * CAD measurements -
	 * loading airship distances look too short
	 * blue right/loading - 67.87, 69.44
	 * red left/loading - 73.85, 69.25
	 * 
	 * blue left/boiler - 65.92, 73.36
	 * red right/boiler - 71.79, 73.36  
	 */
	
	// Blue right loading station
	public static double kBlueLoadingStationForwardDistanceInches = 69; // 73, 69
	public static double kBlueLoadingStationAirshipDistanceInches = 84; // 105, too far, 84
	
	// Red left loading station
	public static double kRedLoadingStationForwardDistanceInches = 71; // 87.75, 75, 
	public static double kRedLoadingStationAirshipDistanceInches = 84; // 108, 95

	// Blue left boiler
	public static double kBlueBoilerForwardDistanceInches = 66; // 69, 67, 66
	public static double kBlueBoilerAirshipDistanceInches = 70.5; // 107, 87.5, 70.5

	// Red right boiler
	public static double kRedBoilerForwardDistanceInches = 65; // 75, 64, 60, 64
	public static double kRedBoilerAirshipDistanceInches = 70; // 106, 70

	// !!! Loop rate of normal Looper
	public static double kControlLoopsDt = 0.005;

	// !!! Loop rate of subsystem updates
	public static double kSubsystemLooperDt = 0.005;
	
	public static double kSubsystemPrintLooperDt = 1.0;

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