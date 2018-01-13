package com.palyrobotics.frc2018.config;

import com.palyrobotics.frc2018.robot.team254.lib.util.ConstantsBase;

public class Constants extends ConstantsBase {
	public enum RobotName {
		UNNAMED
	}

	// Initialization constants
	public static final RobotName kRobotName = RobotName.UNNAMED;
	
	// Android app information
	public static String kPackageName = "com.frc8.team8vision";
	public static String kActivityName = "MainActivity";
	public static String kVisionDataFileName = "data.json";
	public static String kVisionVideoFileName = "video.json";
	public static int kAndroidConnectionUpdateRate = 5;	// Update rate in milliseconds
	public static int kAndroidDataSocketUpdateRate = 100;
	public static int kAndroidVisionSocketUpdateRate = 10;
	public static int kMJPEGVisionSocketUpdateRate = 20;
	public static int kVisionDataPort = 8008;
	public static int kVideoPort = 8009;
	public static int kMJPEGServerSocketPort = 1180;
	
	//Threshold for quickturn sensitivity change
	public static double kQuickTurnSensitivityThreshold = 0.90;

	//Sensitivities for how fast non-quickturn turning is
	public static double kDriveSensitivity = .70;
	
	//Sensitivities for quickturn
	public static double kQuickTurnSensitivity = 0.8;
	public static double kPreciseQuickTurnSensitivity = 0.35;
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

	/*
	 * Control loop constants for both robots
	 */
	public static double kTurnInPlacePower = 0.17; // for bang bang
	public static double kDriveMaxClosedLoopOutput = 8.0;
	// Unit Conversions for CANTalons
	public static double kDriveTicksPerInch = 360 / (3.95 * Math.PI);
	public static double kDriveInchesPerDegree = 0.99*21.5/90;
	public static double kDriveSpeedUnitConversion = 360 / (3.95 * Math.PI * 10);


	public static final double kRobotWidthInches = 40.0;
	public static final double kRobotLengthInches = 31.0;

	// Tolerances
	public static double kAcceptableDrivePositionError = 15;
	public static double kAcceptableDriveVelocityError = 5;
	public static double kAcceptableShortDrivePositionError = 1;
	public static double kAcceptableShortDriveVelocityError = 3;
	public static double kAcceptableTurnAngleError = 1; // 0.5
	public static double kAcceptableGyroZeroError = 3;
	public static double kAcceptableEncoderZeroError = 10;


	/* !!! End of editable Constants! !!!
	 **********************************************************************************
	 */
	public static int kEndEditableArea = 0;

	/*
	 * ************************************
	 *  2018_Unnamed ELECTRONIC CONSTANTS
	 * ************************************
	 */
	// PDP
	public static int k2018_UnnamedPDPDeviceID = 11;
	
	// DRIVETRAIN
	// PDP slots for drivetrain 0, 1, 2, 3, 12, 13
	public static int k2018_UnnamedLeftDriveMasterDeviceID  = 1;
	public static int k2018_UnnamedLeftDriveSlaveDeviceID = 2;
	public static int k2018_UnnamedLeftDriveOtherSlaveDeviceID = 3;
	public static int k2018_UnnamedLeftDriveFrontMotorPDP = 0;
	public static int k2018_UnnamedLeftDriveBackMotorPDP = 0;
	public static int k2018_UnnamedLeftDriveThirdMotorPDP = 0;
	public static int k2018_UnnamedRightDriveMasterDeviceID = 6;
	public static int k2018_UnnamedRightDriveSlaveDeviceID = 5;
	public static int k2018_UnnamedRightDriveOtherSlaveDeviceID = 4;
	public static int k2018_UnnamedRightDriveFrontMotorPDP = 0;
	public static int k2018_UnnamedRightDriveBackMotorPDP = 0;
	public static int k2018_UnnamedRightDriveThirdMotorPDP = 0;


	// !!! Physical constants


	// !!! Loop rate of normal Looper
	public static double kNormalLoopsDt = 0.02;

	// Adaptive Pure Pursuit Controller

	public static double kDriveWheelDiameterInches = 7.3;
	public static double kTrackLengthInches = 8.265;
	public static double kTrackWidthInches = 23.8;
	public static double kTrackEffectiveDiameter = (kTrackWidthInches * kTrackWidthInches
			+ kTrackLengthInches * kTrackLengthInches) / kTrackWidthInches;
	public static double kTrackScrubFactor = 0.9;
	public static double kPathFollowingLookahead = 20.0;
	public static double kPathFollowingMaxAccel = 5.0 * kDriveTicksPerInch;
	public static double kPathFollowingMaxVel = 10.0 * kDriveTicksPerInch;
	public static double kPathFollowingTolerance = 0.20;

	@Override
	public String toString() {
	return "kQuickStopAccumulatorDecreaseRate "+kQuickStopAccumulatorDecreaseRate+
	"kQuickStopAccumulatorDecreaseThreshold "+kQuickStopAccumulatorDecreaseThreshold+
	"kNegativeInertiaScalar "+kNegativeInertiaScalar+
	"kAlpha "+ kAlpha +
	"kDriveTicksPerInch "+kDriveTicksPerInch +
	"kDriveInchesPerDegree" + kDriveInchesPerDegree +
	"kDriveSpeedUnitConversion "+kDriveSpeedUnitConversion
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