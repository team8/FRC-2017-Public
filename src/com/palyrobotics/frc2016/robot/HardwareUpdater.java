package com.palyrobotics.frc2016.robot;

import com.palyrobotics.frc2016.config.RobotState;
import com.palyrobotics.frc2016.subsystems.Drive;

/**
 * Should only be used in robot package.
 */
class HardwareUpdater {
	private Drive mDrive;
	
	/**
	 * Hardware Updater for Derica
	 * Updates Drive, Catapult, Intake, LowGoalShooter
	 * @param mDrive
	 * @param mCatapult
	 * @param mIntake
	 * @param mLowGoalShooter
	 */
	HardwareUpdater(Drive mDrive) {
		this.mDrive = mDrive;
	}

	/**
	 * Updates all the sensor data taken from the hardware
	 */
	void updateSensors() {
		RobotState robotState = Robot.getRobotState();
		robotState.left_encoder = HardwareAdapter.DrivetrainHardware.getInstance().kLeftDriveEncoder.get();
		robotState.right_encoder = HardwareAdapter.DrivetrainHardware.getInstance().kRightDriveEncoder.get();
	}

	/**
	 * Sets the output from all subsystems for the respective hardware
	 */
	void updateSubsystems() {
		updateDrivetrain();
	}
	
	private void updateDrivetrain() {
		HardwareAdapter.getInstance().getDrivetrain().kLeftDriveMotor.set(mDrive.getDriveSignal().leftMotor);
		// Need to invert right side motor
		HardwareAdapter.getInstance().getDrivetrain().kRightDriveMotor.set(-mDrive.getDriveSignal().rightMotor);
	}
	

}
