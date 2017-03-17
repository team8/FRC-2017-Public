package com.palyrobotics.frc2017.config;

import com.palyrobotics.frc2017.robot.team254.lib.util.ConstantsBase;

public class Constants extends ConstantsBase {
	public enum RobotName {
		STEIK, AEGIR, DERICA
	}

	// Initialization constants
	public static final RobotName kRobotName = RobotName.AEGIR;
	public static final boolean kCalibrateSliderWithPotentiometer = true;

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
	public static double kDriveTicksPerInch = 360 / (3.95 * Math.PI); // = 29.0105;
	public static double kDriveInchesPerDegree = 21.5/90; // = 0.2388;
	public static double kDriveSpeedUnitConversion = 360 / (3.95 * Math.PI * 10);


	// Aegir right 2172; left 3452;
	public static final double kPotentiometerRightPos = 2036;
	public static final double kPotentiometerLeftPos = 3314;
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
	// PDP slots for drivetrain 0, 1, 2, 3, 12, 13
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

	// Base line
	public static double k254BaseLineDistanceInches = 93.3;
	public static double kRedBaseLineDistanceInches = 93.3;
	public static double kBlueBaseLineDistanceInches = 93.3;

	// Center peg
	public static double k254CenterPegDistanceInches = 86;
	public static double kRedCenterPegDistanceInches = 113.75 - 31; // first number is to airship
	public static double kBlueCenterPegDistanceInches = 108.5 - 31; // first num is to airship

	// Side peg
	public static double k254SidePegDistanceLoadingStationInches = 74;
	public static double k254SidePegDistanceToAirshipLoadingStationInches = 84;//24.5688;
	public static double k254SidePegDistanceBoilerInches = 60;
	public static double k254SidePegDistanceToAirshipBoilerInches = 93;
	public static double kSidePegTurnAngleDegrees = 60;

	// Blue right loading station
	public static double kBlueLoadingStationForwardDistanceInches = 71; // original 73
	public static double kBlueLoadingStationAirshipDistanceInches = 105; // original
	
	// Red left loading station
	public static double kRedLoadingStationForwardDistanceInches = 87.75; // original 87.75
	public static double kRedLoadingStationAirshipDistanceInches = 108; // original 108

	// Blue left boiler
	public static double kBlueBoilerForwardDistanceInches = 62.75; // original
	public static double kBlueBoilerAirshipDistanceInches = 66; // original 

	// Red right boiler
	public static double kRedBoilerForwardDistanceInches = 72; // original measurement 75
	public static double kRedBoilerAirshipDistanceInches = 106; // original measurement 106


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
