package com.palyrobotics.frc2017.util.archive;
import com.palyrobotics.frc2017.config.Commands;

public class ProportionalDriveHelper {
	private DriveSignal mSignal = DriveSignal.getNeutralSignal();

	public DriveSignal pDrive(Commands commands) {
		double throttle = -commands.leftStickInput.y;
		double wheel = commands.rightStickInput.x;

		double rightPwm = throttle - wheel;
		double leftPwm = throttle + wheel;

		mSignal.leftMotor.setPercentVBus(leftPwm);
		mSignal.rightMotor.setPercentVBus(rightPwm);
		return mSignal;
	}

}