package com.palyrobotics.frc2016.util;
import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.robot.team254.lib.util.DriveSignal;

public class ProportionalDriveHelper {
	private DriveSignal mSignal = DriveSignal.NEUTRAL;

	public DriveSignal pDrive(Commands commands) {
		double throttle = -commands.leftStickInput.y;
		double wheel = commands.rightStickInput.x;

		double rightPwm = throttle - wheel;
		double leftPwm = throttle + wheel;

		mSignal.leftMotor = leftPwm;
		mSignal.rightMotor = rightPwm;
		return new DriveSignal(leftPwm, rightPwm);
	}

}