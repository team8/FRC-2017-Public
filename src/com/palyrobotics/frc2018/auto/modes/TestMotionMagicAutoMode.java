package com.palyrobotics.frc2018.auto.modes;

import com.palyrobotics.frc2018.config.Gains;

import java.util.ArrayList;

import com.palyrobotics.frc2018.auto.AutoModeBase;
import com.palyrobotics.frc2018.behavior.Routine;
import com.palyrobotics.frc2018.behavior.SequentialRoutine;
import com.palyrobotics.frc2018.behavior.routines.drive.TalonSRXRoutine;
import com.palyrobotics.frc2018.config.Constants;
import com.palyrobotics.frc2018.util.archive.DriveSignal;

public class TestMotionMagicAutoMode extends AutoModeBase {

	// Drive Distance PID control loop
	public static final double kSteikDriveStraightTurnkP = -0.06;
	public static final double kSteikDriveDistancekP = 0.5;
	public static final double kSteikDriveDistancekI = 0.0025;
	public static final double kSteikDriveDistancekD = 12.0;
	public static final int kSteikDriveDistancekIzone = 125;
	public static final double kSteikDriveDistancekRampRate = 0.0;
	public static final Gains steikDriveDistance = new Gains(kSteikDriveDistancekP, kSteikDriveDistancekI, kSteikDriveDistancekD,
			0, kSteikDriveDistancekIzone, kSteikDriveDistancekRampRate);

	// Drive Velocity offboard control loop
	public static final double kSteikDriveVelocitykP = 6.0;
	public static final double kSteikDriveVelocitykI = 0.002;
	public static final double kSteikDriveVelocitykD = 85;
	public static final double kSteikDriveVelocitykF = 2.624;
	public static final int kSteikDriveVelocitykIzone = 800;
	public static final double kSteikDriveVelocitykRampRate = 0.0;
	public static final Gains steikVelocity = new Gains(kSteikDriveVelocitykP, kSteikDriveVelocitykI, kSteikDriveVelocitykD,
			kSteikDriveVelocitykF, kSteikDriveVelocitykIzone, kSteikDriveVelocitykRampRate);

	// Drive Motion Magic offboard control loop
	// Short distance max speed 45 in/s Max accel 95 in/s^2
	public static final double kSteikShortDriveMotionMagicCruiseVelocity = 60 * Constants.kDriveSpeedUnitConversion;
	public static final double kSteikShortDriveMotionMagicMaxAcceleration = 100 * Constants.kDriveSpeedUnitConversion;
	public static final double kSteikShortDriveMotionMagickP = 2.40;
	public static final double kSteikShortDriveMotionMagickI = 0.00040;
	public static final double kSteikShortDriveMotionMagickD = 275;
	public static final double kSteikShortDriveMotionMagickF = 2.075;
	public static final int kSteikShortDriveMotionMagickIzone = 150;
	public static final double kSteikShortDriveMotionMagickRampRate = 0.0;
	public static final Gains steikShortDriveMotionMagicGains = new Gains(kSteikShortDriveMotionMagickP, kSteikShortDriveMotionMagickI, kSteikShortDriveMotionMagickD,
			kSteikShortDriveMotionMagickF, kSteikShortDriveMotionMagickIzone, kSteikShortDriveMotionMagickRampRate);
	
	// Long distance more aggressive, 180 in/s, 120 in/s^2 accel
	public static final double kSteikLongDriveMotionMagicCruiseVelocity = 180 * Constants.kDriveSpeedUnitConversion;
	public static final double kSteikLongDriveMotionMagicMaxAcceleration = 120 * Constants.kDriveSpeedUnitConversion;
	public static final double kSteikLongDriveMotionMagickP = 3;
	public static final double kSteikLongDriveMotionMagickI = 0.005;
	public static final double kSteikLongDriveMotionMagickD = 200;
	public static final double kSteikLongDriveMotionMagickF = 2.0;
	public static final int kSteikLongDriveMotionMagickIzone = 50;
	public static final double kSteikLongDriveMotionMagickRampRate = 0.0;
	public static final Gains steikLongDriveMotionMagicGains = new Gains(kSteikLongDriveMotionMagickP, kSteikLongDriveMotionMagickI, kSteikLongDriveMotionMagickD,
			kSteikLongDriveMotionMagickF, kSteikLongDriveMotionMagickIzone, kSteikLongDriveMotionMagickRampRate);

	private SequentialRoutine mSequentialRoutine;

	
	public TestMotionMagicAutoMode() {
		
	}
	
	private TalonSRXRoutine getDrive() {

		Gains mLongGains = steikLongDriveMotionMagicGains;
		Gains mShortGains = steikShortDriveMotionMagicGains;
		DriveSignal driveToAirship = DriveSignal.getNeutralSignal();
		double driveToAirshipSetpoint = 48; // inches
		driveToAirshipSetpoint += 2;
		driveToAirshipSetpoint *= Constants.kDriveTicksPerInch;
		driveToAirship.leftMotor.setMotionMagic(driveToAirshipSetpoint, mLongGains,
				(int) kSteikLongDriveMotionMagicCruiseVelocity, (int) kSteikLongDriveMotionMagicMaxAcceleration);
		driveToAirship.rightMotor.setMotionMagic(driveToAirshipSetpoint, mLongGains,
				(int) kSteikLongDriveMotionMagicCruiseVelocity, (int) kSteikLongDriveMotionMagicMaxAcceleration);
		
//		Logger.getInstance().logRobotThread("Drive to airship", driveToAirship);
		return new TalonSRXRoutine(driveToAirship, true, 2);
	}
	
	private void updateTables() {
	
	}
	
	@Override
	public Routine getRoutine() {
		return mSequentialRoutine;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "yikes";
	}

	@Override
	public void prestart() {
		// TODO Auto-generated method stub
		ArrayList<Routine> sequence = new ArrayList<>();
		sequence.add(getDrive());
		mSequentialRoutine = new SequentialRoutine(sequence);
	}
}
