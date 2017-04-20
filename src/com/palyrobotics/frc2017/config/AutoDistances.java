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
	public static double kBlueLoadingStationForwardDistanceInches = 79.5; // 79.5
	public static double kBlueLoadingStationAirshipDistanceInches = 73; // 66, 70, 73
	// Red left loading station
	public static double kRedLoadingStationForwardDistanceInches = 78; // 79
	public static double kRedLoadingStationAirshipDistanceInches = 71.5; // 73,
	// Blue left boiler
	public static double kBlueBoilerForwardDistanceInches = 82.5; // 82.5
	public static double kBlueBoilerAirshipDistanceInches = 73; // 73,
	// Red right boiler
	public static double kRedBoilerForwardDistanceInches = 84; // 84,
	public static double kRedBoilerAirshipDistanceInches = 73; // 73,

	/* TRAJECTORY TUNING */
	// robot distance appears to go 4 inches more, relative to the back of the robot

	// Distances in feet, angles in radians
	public static double kBackup = -0.8;
	public static double kRedCenter = 89.0/12;
	public static double kBlueCenter = 89.0/12;
	// Blue right loading station
	public static double kBlueLoadingPegX = 99.5+7;//115.0; // 79.5
	public static double kBlueLoadingPegY = 53.25+30;//70.0; // 66, 70, 73
	// Red left loading station
	public static double kRedLoadingPegX = 102+10;//115.0; // 79
	public static double kRedLoadingPegY = 53.5;//63.22; // 66, 70, 73
	// Blue left boiler
	public static double kBlueBoilerPegX = 100;//100; // 79.5, 82.5
	public static double kBlueBoilerPegY = 63;//82; // 73,
	// Red right boiler
	// should be positive
	public static double kRedBoilerPegX=101;//100;
	public static double kRedBoilerPegY=62;//82;
}
