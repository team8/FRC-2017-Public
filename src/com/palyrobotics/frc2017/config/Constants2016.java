package com.palyrobotics.frc2017.config;

/**
 * Created by Nihar on 1/28/17.
 * Stores the constants used on Tyr and Derica for future reference as needed
 */
public class Constants2016 {
	public static double kManualLowGoalShootSpeed = 1.0;
	public static double kManualLowGoalLoadSpeed = 1.0;
	// Autonomous intake speed
	public static double kAutoIntakeSpeed = 1.0;
	// !!! Electrical constants (do not change at runtime, lol)
	/*
	 * TYR
	 */
	// DRIVETRAIN
	// Motors
	public static int kTyrLeftDriveFrontMotorDeviceID  = 1;
	public static int kTyrLeftDriveBackMotorDeviceID = 3;
	public static int kTyrLeftDriveFrontMotorPDP = 12; // Found using tyr code
	public static int kTyrLeftDriveBackMotorPDP = 13;
	public static int kTyrRightDriveFrontMotorDeviceID = 2;
	public static int kTyrRightDriveBackMotorDeviceID = 4;
	public static int kTyrRightDriveFrontMotorPDP = 14; // Found using tyr code
	public static int kTyrRightDriveBackMotorPDP = 15;
	// Gear switching solenoid
	public static int kTyrDriveSolenoidExtend = 0;
	public static int kTyrDriveSolenoidRetract = 7;
	// DIO Encoders
	public static int kTyrLeftDriveEncoderDIOA = 1;
	public static int kTyrLeftDriveEncoderDIOB = 0;
	public static int kTyrRightDriveEncoderDIOA = 2;
	public static int kTyrRightDriveEncoderDIOB = 3;
	// INTAKE
	public static int kTyrLeftIntakeMotorDeviceID = 1;
	public static int kTyrLeftIntakeMotorPDP = 7;
	public static int kTyrRightIntakeMotorDeviceID = 0;
	public static int kTyrRightIntakeMotorPDP = 8;
	// SHOOTER
	public static int kTyrShooterMotorDeviceID = 8;
	public static int kTyrShooterMotorPDP = 3;
	// TODO: Shooter Potentiometer Port
	public static int kTyrShooterPotentiometerPort = -1000000;
	//BREACHER
	public static int kBreacherMotorDeviceID = 5;
	public static int kBreacherMotorPDP = 10;
	// Solenoids (shooter, latch, grabber)
	public static int kShooterSolenoidPortExtend = 5;
	public static int kShooterSolenoidPortRetract = 2;
	public static int kLatchSolenoidPortExtend = 1;
	public static int kLatchSolenoidPortRetract = 6;
	public static int kGrabberSolenoidPortExtend = 4;
	public static int kGrabberSolenoidPortRetract = 3;
	// Motors
	public static int kDericaLeftDriveMasterDeviceID = 1;
	public static int kDericaLeftDriveSlaveDeviceID  = 4;
	public static int kDericaLeftDriveFrontMotorPDP = 14;
	public static int kDericaLeftDriveBackMotorPDP = 3;
	public static int kDericaRightDriveMasterDeviceID = 3;
	public static int kDericaRightDriveSlaveDeviceID = 2;
	public static int kDericaRightDriveFrontMotorPDP = 15;
	public static int kDericaRightDriveBackMotorPDP = 2;
	// DIO Encoders
	public static int kDericaLeftDriveEncoderDIOA = 2;
	public static int kDericaLeftDriveEncoderDIOB = 3;
	public static int kDericaRightDriveEncoderDIOA = 0;
	public static int kDericaRightDriveEncoderDIOB = 1;
	// INTAKE
	public static int kDericaIntakeMotorPWM = 7;
	public static int kDericaIntakeMotorPDP = 1;
	public static int kDericaArmIntakeMotorPWM = 8;
	public static int kDericaArmIntakeMotorPDP = 0;
	public static int kDericaLowGoalShooterPWM = 0;
	public static int kDericaLowGoalShooterPDP = 3;
	public static double kDericaDegreeToDistance = 0.209;
}
