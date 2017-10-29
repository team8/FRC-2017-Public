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
	//original offsets
	public static double kBackup = -0.8;
	public static double kRedCenterPeg = 111.0;
	public static double kRedCenterPegOffsetX = 1.5;//3.5;
	public static double kRedCenterPegOffsetY = -0.25;//4.0;
	public static double kRedCenterPegOffsetVisionX = 0.73;
	public static double kRedCenterPegOffsetVisionY = 3.635;

	//original offsets
	public static double kBlueCenterPeg = 110.25;
	public static double kBlueCenterPegOffsetX = 1.5;
	public static double kBlueCenterPegOffsetY = -0.25;
	public static double kBlueCenterPegOffsetVisionX = 0.73;
	public static double kBlueCenterPegOffsetVisionY = 3.635;

	// Blue right loading station
	//original offsets
	public static double kBlueLoadingPegX = 129.0;
	public static double kBlueLoadingPegY = 90.5;
	//Positive increases the magnitude of each dimension
	public static double kBlueLoadingPegOffsetX = -8.0+7.0/2.0;//(-2.0-0.75);
	public static double kBlueLoadingPegOffsetY = 14.5-7.0/2.0*Math.sqrt(3);//(11.0+0.75);

	// Red left loading station
	public static double kRedLoadingPegX = 131.0;
	public static double kRedLoadingPegY = 89.5;
	//Positive increases the magnitude of each dimension
	public static double kRedLoadingPegOffsetX = 0.0-4.25*Math.sqrt(3)+Math.sqrt(3)/2;//-1.0;
	public static double kRedLoadingPegOffsetY = 0.5+4.25+Math.sqrt(3);//6.5;

	// Blue left boiler
	//Original offsets
	public static double kBlueBoilerPegX = 129.0;
	public static double kBlueBoilerPegY = 92.5;
	//Positive increases the magnitude of each dimension
	public static double kBlueBoilerPegOffsetX = 0.0-4.25*Math.sqrt(3)+Math.sqrt(3)/2;//-1.67;
	public static double kBlueBoilerPegOffsetY = 0.5+4.25+Math.sqrt(3);//6.75;

	// Red right boiler
	// should be positive
	public static double kRedBoilerPegX = 127.0;
	public static double kRedBoilerPegY = 91.0;
	//Positive increases the magnitude of each dimension
	public static double kRedBoilerPegOffsetX = -8.0+7.0/2.0;//-2.0;
	public static double kRedBoilerPegOffsetY = 14.5-7.0/2.0*Math.sqrt(3);//11.0;
}
