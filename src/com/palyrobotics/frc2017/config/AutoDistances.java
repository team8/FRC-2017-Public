package com.palyrobotics.frc2017.config;

/**
 * Created by Nihar on 4/13/17.
 */
public class AutoDistances {
	/* PLEB AUTO */

	public static double kVisionDistanceInches = 30;

	// Base line
	public static double k254BaseLineDistanceInches = 93.3;
	public static double kRedBaseLineDistanceInches = 110;
	public static double kBlueBaseLineDistanceInches = 110;
	// Center peg
	public static double k254CenterPegDistanceInches = 80;
	public static double kRedCenterPegDistanceInches = 82;
	public static double kBlueCenterPegDistanceInches = 82;
	// Side peg
	public static double k254LoadingStationForwardDistanceInches = 80; // 84, 80
	public static double k254LoadingStationAirshipDistanceInches = 66; // 66
	public static double k254BoilerForwardDistanceInches = 80; // 80
	public static double k254BoilerAirshipDistanceInches = 73; // 81.5, 73
	// Blue right loading station
	public static double kBlueLoadingStationForwardDistanceInches = 75; // 80, 75
	public static double kBlueLoadingStationAirshipDistanceInches = 84; // 66, 70, 73
	// Red left loading station
	public static double kRedLoadingStationForwardDistanceInches = 75; // 80, 75
	public static double kRedLoadingStationAirshipDistanceInches = 84; // 71.5, 73
	// Blue left boiler
	public static double kBlueBoilerForwardDistanceInches = 75; // 80, 75
	public static double kBlueBoilerAirshipDistanceInches = 78; // 73, 72, 78
	// Red right boiler
	public static double kRedBoilerForwardDistanceInches = 75; // 80, 75
	public static double kRedBoilerAirshipDistanceInches = 78; // 73, 72

	/* TRAJECTORY TUNING */

	// Distances in inches, angles in radians

	// Calibration data
	public static double kBackup = -0.8;
	public static double kRedCenterPeg = 111.0;
	public static double kRedCenterPegOffsetX = 3.5;
	public static double kRedCenterPegOffsetY = 4.0;

	public static double kBlueCenterPeg = 111.0;
	public static double kBlueCenterPegOffsetX = 3.5;
	public static double kBlueCenterPegOffsetY = 3.0;

	// Blue right loading station
	public static double kBlueLoadingPegX = 130.0;
	public static double kBlueLoadingPegY = 90.5;
	//Positive increases the magnitude of each dimension
	public static double kBlueLoadingPegOffsetX = (-2.0-0.75);
	public static double kBlueLoadingPegOffsetY = (11.0+0.75);

	// Red left loading station
	public static double kRedLoadingPegX = 132.0;
	public static double kRedLoadingPegY = 92.0;
	//Positive increases the magnitude of each dimension
	public static double kRedLoadingPegOffsetX = -1.0;
	public static double kRedLoadingPegOffsetY = 6.5;

	// Blue left boiler
	public static double kBlueBoilerPegX = (125.5);
	public static double kBlueBoilerPegY = (94.0);
	//Positive increases the magnitude of each dimension
	public static double kBlueBoilerPegOffsetX = -1.67;
	public static double kBlueBoilerPegOffsetY = 6.75;

	// Red right boiler
	// should be positive
	public static double kRedBoilerPegX = 127.0;
	public static double kRedBoilerPegY = 91.0;
	//Positive increases the magnitude of each dimension
	public static double kRedBoilerPegOffsetX = -2.0;
	public static double kRedBoilerPegOffsetY = 11.0;
}
