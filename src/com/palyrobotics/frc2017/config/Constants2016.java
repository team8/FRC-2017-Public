package com.palyrobotics.frc2017.config;

/**
 * Created by Nihar on 1/28/17.
 * Stores the constants used on Derica for future reference as needed
 */
public class Constants2016 {
	// CANTalon offboard constants for Derica
	public static double kDericaInchesToTicks = 1400 / (2 * 3.1415 * 3.5);
	public static double kDericaInchesToDegrees = 42 / 180.0;

	public static double kAcceptableDrivePositionError = 150;
	public static double kAcceptableDriveVelocityError = 40;

	public static double kAutoIntakeSpeed = 1.0;
	// !!! Electrical constants (do not change at runtime, lol)

	// Motors
	public static int kDericaLeftDriveMasterDeviceID = 3;
	public static int kDericaLeftDriveSlaveDeviceID  = 2;
	public static int kDericaLeftDriveFrontMotorPDP = 14;
	public static int kDericaLeftDriveBackMotorPDP = 3;
	public static int kDericaRightDriveMasterDeviceID = 1;
	public static int kDericaRightDriveSlaveDeviceID = 4;
	public static int kDericaRightDriveFrontMotorPDP = 15;
	public static int kDericaRightDriveBackMotorPDP = 2;
	// DIO Encoders
	public static int kDericaLeftDriveEncoderDIOA = 2;
	public static int kDericaLeftDriveEncoderDIOB = 3;
	public static int kDericaRightDriveEncoderDIOA = 0;
	public static int kDericaRightDriveEncoderDIOB = 1;
	public static double kDericaDegreeToDistance = 0.209;

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
	public static double kAcceptableDriveError = 200;
	//Encoder Turn in Place Controller gains
	public static double kEncoderTurnKp = 0.07;
	public static double kEncoderTurnKi = 0.01;
	public static double kEncoderTurnKd = 0.007;
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
	// Drive parameters
	public static double kDriveEncoderCountsPerRev = 250.0;
	public static double kDriveWheelSizeInches = 8; //pneumatic wheels
}
