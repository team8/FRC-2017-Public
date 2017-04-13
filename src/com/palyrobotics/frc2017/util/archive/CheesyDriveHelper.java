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
	private boolean mInitialBrake;
	private double mOldThrottle, mBrakeRate;
	private final double kWheelStickDeadband = 0.02;
	private final double kThrottleStickDeadband = 0.02;

	public DriveSignal cheesyDrive(Commands commands, RobotState robotState) {
		double throttle = -commands.leftStickInput.y;
		double wheel = commands.rightStickInput.x;

		//Quickturn if right trigger is pressed
		boolean isQuickTurn = commands.rightStickInput.triggerPressed;

		//Braking if left trigger is pressed
		boolean isBraking = commands.leftStickInput.triggerPressed;

		//Always on low gear
		boolean isHighGear = false;

		double wheelNonLinearity;

		wheel = handleDeadband(wheel, kWheelStickDeadband);
		throttle = handleDeadband(throttle, kThrottleStickDeadband);

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
		double linearPower = remapThrottle(throttle);;

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
		
		//neginertia is difference in wheel
		double negInertiaPower = negInertia * negInertiaScalar;
		negInertiaAccumulator += negInertiaPower;
		
		//possible source of occasional overturn
		wheel = wheel + negInertiaAccumulator;
		
		//limit between [-1, 1]
		if (negInertiaAccumulator > 1) {
			negInertiaAccumulator -= 1;
		} else if (negInertiaAccumulator < -1) {
			negInertiaAccumulator += 1;
		} else {
			negInertiaAccumulator = 0;
		}

		//Handle braking
		if(isBraking) {
			//Set up braking rates for linear deceleration in a set amount of time
			if(mInitialBrake) {
				mInitialBrake = false;

				//Old throttle initially set to throttle
				mOldThrottle = linearPower;

				//Braking rate set
				mBrakeRate = mOldThrottle/Constants.kCyclesUntilStop;
			}

			//If braking is not complete, decrease by the brake rate
			if(Math.abs(mOldThrottle) >= Math.abs(mBrakeRate)) {
				//reduce throttle
				mOldThrottle -= mBrakeRate;
				linearPower = mOldThrottle;
			} else {
				linearPower = 0;
			}
		} else {
			mInitialBrake = true;
		}

		// Quickturn!
		if (isQuickTurn) {
			// Can be tuned
			double alpha = Constants.kAlpha;
			mQuickStopAccumulator = (1 - alpha) * mQuickStopAccumulator
					+ alpha * Util.limit(wheel, 1.0) * 5;
			
			overPower = 1.0;
			
			//Different sensitivity on quick turning
			if (isHighGear) {
				if(Math.abs(commands.rightStickInput.x) < Constants.kQuickTurnSensitivityThreshold) {
					sensitivity = Constants.kHighGearPreciseQuickTurnSensitivity;
				} else {
					sensitivity = Constants.kHighGearQuickTurnSensitivity;
				}
			} else {
				if(Math.abs(commands.rightStickInput.x) < Constants.kQuickTurnSensitivityThreshold) {
					sensitivity = Constants.kLowGearPreciseQuickTurnSensitivity;
				} else {
					sensitivity = Constants.kLowGearQuickTurnSensitivity;
				}
			}

			angularPower = wheel * sensitivity;
		} else {
			overPower = 0.0;

			//Sets turn amount
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

	/**
	 * Throttle tuning functions
	 */
	public double remapThrottle(double initialThrottle) {
		double x = Math.abs(initialThrottle);
		double eric = 0.7*x + 1/(1/(0.15)+Math.pow(Math.E,-64*(x-0.35)))
		+1/(1/(0.15)+Math.pow(Math.E,-64*(x-0.75)));
		double nihar;
		if(x < 0.3) {
			nihar = x*x;
		} else if(x < 0.75) {
			nihar = 2*0.3*x;
		} else {
			nihar = (1-0.45)/(1-0.75)*(x-0.75) + 0.45; 
		}
//		return Math.signum(initialThrottle)*nihar;
//		return Math.signum(initialThrottle)*(Math.pow(x, 2));
//		return Math.signum(initialThrottle)*eric;
		return Math.signum(initialThrottle)*x;
	}
}