package com.palyrobotics.frc2017.auto.modes;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.util.archive.DriveSignal;

public class BaseLineAutoMode extends AutoModeBase {
	private CANTalonRoutine mRoutine;
	private final CenterPegAutoMode.Alliance mAlliance;
	private Gains mGains;

	public BaseLineAutoMode(CenterPegAutoMode.Alliance alliance) {
		mAlliance = alliance;
		if(Constants.kRobotName == Constants.RobotName.DERICA) {
			mGains = Gains.dericaPosition;
		} else {
			mGains = Gains.steikLongDriveMotionMagicGains;
		}
	}

	@Override
	public Routine getRoutine() {
		// Drive straight until baseline
		DriveSignal driveForward = DriveSignal.getNeutralSignal();
		double setpoint =
				((mAlliance == CenterPegAutoMode.Alliance.BLUE) ? Constants.kBlueBaseLineDistanceInches : Constants.kRedBaseLineDistanceInches)
						*
				((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
						: Constants.kDriveTicksPerInch);
		driveForward.leftMotor.setMotionMagic(setpoint, mGains,
			Gains.kSteikLongDriveMotionMagicCruiseVelocity, Gains.kSteikLongDriveMotionMagicMaxAcceleration);
		driveForward.rightMotor.setMotionMagic(setpoint, mGains,
				Gains.kSteikLongDriveMotionMagicCruiseVelocity, Gains.kSteikLongDriveMotionMagicMaxAcceleration);
		mRoutine = new CANTalonRoutine(driveForward, true);
		return mRoutine;
	}

	@Override
	public void prestart() {
		System.out.println("Starting Base Line Auto Mode");
	}

	@Override
	public String toString() {
		return (mAlliance == CenterPegAutoMode.Alliance.BLUE) ? "BlueBaseLine" : "RedBaseLine";
	}
}
