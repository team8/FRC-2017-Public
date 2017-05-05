package com.palyrobotics.frc2017.config;

/**
 * Created by Nihar on 4/13/17.
 */
public class AutoDistances {
	/* PLEB AUTO */

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
	// robot distance appears to go 4 inches more, relative to the back of the robot

	// Distances in feet, angles in radians

	// Calibration data
	// For x = 128, y = 126
	// setpoint x = 103, setpoint y = 64.5
	public static double kBackup = -0.8;
	public static double kRedCenter = 103.0/12;
	public static double kBlueCenter = 103.0/12;
	// Blue right loading station
	public static double kBlueLoadingPegX = 104.5;// 104.5
	public static double kBlueLoadingPegY = 68.5;// 67.5
	// Red left loading station
	public static double kRedLoadingPegX = 104;// 104
	public static double kRedLoadingPegY = 67.5;// 66.5
	// Blue left boiler
	public static double kBlueBoilerPegX = 105; // 105
	public static double kBlueBoilerPegY = 67.5; // 65.5
	// Red right boiler
	// should be positive
	public static double kRedBoilerPegX = 104;// 104
	public static double kRedBoilerPegY = 64.5; // 63.5
}
