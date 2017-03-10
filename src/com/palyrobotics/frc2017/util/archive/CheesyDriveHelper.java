package com.palyrobotics.frc2017.util.archive;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.team254.lib.util.Util;

/**
 * CheesyDriveHelper implements the calculations used in CheesyDrive for teleop control.
 * Returns a DriveSignal for the motor output
 */
public class CheesyDriveHelper {
	private double mOldWheel, mQuickStopAccumulator;
	private final double kWheelStickDeadband = 0.02;
	private final double kThrottleStickDeadband = 0.02;

	public DriveSignal cheesyDrive(Commands commands, RobotState robotState) {
		double throttle = -commands.leftStickInput.y;
		double wheel = commands.rightStickInput.x;
		boolean isQuickTurn = commands.rightStickInput.triggerPressed;
		boolean isHighGear = false;

		double wheelNonLinearity;

		wheel = handleDeadband(wheel, kWheelStickDeadband);
		throttle = handleDeadband(throttle, kThrottleStickDeadband);
		System.out.println("forward power: " + throttle);

		double negInertia = wheel - mOldWheel;
		mOldWheel = wheel;

		if (isHighGear) {
			wheelNonLinearity = 0.6;
			// Apply a sin function that's scaled to make it feel better.
			wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel)
					/ Math.sin(Math.PI / 2.0 * wheelNonLinearity);
			wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel)
					/ Math.sin(Math.PI / 2.0 * wheelNonLinearity);
		} else {
			wheelNonLinearity = 0.5;
			// Apply a sin function that's scaled to make it feel better.
			wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel)
					/ Math.sin(Math.PI / 2.0 * wheelNonLinearity);
			wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel)
					/ Math.sin(Math.PI / 2.0 * wheelNonLinearity);
			wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel)
					/ Math.sin(Math.PI / 2.0 * wheelNonLinearity);
		}

		double leftPwm, rightPwm, overPower;
		double sensitivity;

		double angularPower;
		double linearPower;

		// Negative inertia!
		double negInertiaAccumulator = 0.0;
		double negInertiaScalar;
		if (isHighGear) {
			negInertiaScalar = 4.0;
			sensitivity = Constants.kHighGearDriveSensitivity;
		} else {
			if (wheel * negInertia > 0) {
				negInertiaScalar = 2.5;
			} else {
				if (Math.abs(wheel) > 0.65) {
					negInertiaScalar = 5.0;
				} else {
					negInertiaScalar = 3.0;
				}
			}
			sensitivity = Constants.kLowGearDriveSensitivity;
		}
		double negInertiaPower = negInertia * negInertiaScalar;
		negInertiaAccumulator += negInertiaPower;

		wheel = wheel + negInertiaAccumulator;
		if (negInertiaAccumulator > 1) {
			negInertiaAccumulator -= 1;
		} else if (negInertiaAccumulator < -1) {
			negInertiaAccumulator += 1;
		} else {
			negInertiaAccumulator = 0;
		}
		linearPower = throttle;

		// Quickturn!
		if (isQuickTurn) {
			// Can be tuned
			double alpha = Constants.kAlpha;
			mQuickStopAccumulator = (1 - alpha) * mQuickStopAccumulator
					+ alpha * Util.limit(wheel, 1.0) * 5;
			
			overPower = 1.0;
			
			//Different sensitivity on quick turning
			if (isHighGear) {
				sensitivity = Constants.kHighGearQuickTurnSensitivity;
			} else {
				sensitivity = Constants.kLowGearQuickTurnSensitivity;
			}

			angularPower = wheel * sensitivity;
		} else {
			overPower = 0.0;
			angularPower = Math.abs(throttle) * wheel * sensitivity
					- mQuickStopAccumulator;
			if (mQuickStopAccumulator > Constants.kQuickStopAccumulatorDecreaseThreshold) {
				mQuickStopAccumulator -= Constants.kQuickStopAccumulatorDecreaseRate;
			} else if (mQuickStopAccumulator < -Constants.kQuickStopAccumulatorDecreaseThreshold) {
				mQuickStopAccumulator += Constants.kQuickStopAccumulatorDecreaseRate;
			} else {
				mQuickStopAccumulator = 0.0;
			}
		}

		rightPwm = leftPwm = linearPower;
		
		leftPwm += angularPower;
		rightPwm -= angularPower;

		if (leftPwm > 1.0) {
			rightPwm -= overPower * (leftPwm - 1.0);
			leftPwm = 1.0;
		} else if (rightPwm > 1.0) {
			leftPwm -= overPower * (rightPwm - 1.0);
			rightPwm = 1.0;
		} else if (leftPwm < -1.0) {
			rightPwm += overPower * (-1.0 - leftPwm);
			leftPwm = -1.0;
		} else if (rightPwm < -1.0) {
			leftPwm += overPower * (-1.0 - rightPwm);
			rightPwm = -1.0;
		}
		DriveSignal mSignal = DriveSignal.getNeutralSignal();
		mSignal.leftMotor.setPercentVBus(leftPwm);
		mSignal.rightMotor.setPercentVBus(rightPwm);
		return mSignal;
	}

	/**
	 * Neutralizes a value within a deadband
	 * @param val Value to control deadband
	 * @param deadband Value of deadband
	 * @return 0 if within deadband, otherwise value
	 */
	private double handleDeadband(double val, double deadband) {
		return (Math.abs(val) > Math.abs(deadband)) ? val : 0.0;
	}
}