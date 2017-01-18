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
	 * @param drive
	 * @param mCatapult
	 * @param mIntake
	 * @param mLowGoalShooter
	 */
	HardwareUpdater(Drive drive) {
		this.mDrive = drive;
	}

	/**
	 * Updates all the sensor data taken from the hardware
	 */
	void updateSensors() {
		System.out.println("Gyro "+HardwareAdapter.DrivetrainHardware.getInstance().gyro.getAngle());
		RobotState robotState = Robot.getRobotState();
		robotState.drivePose.heading = HardwareAdapter.DrivetrainHardware.getInstance().gyro.getAngle();
		robotState.drivePose.headingVelocity = HardwareAdapter.DrivetrainHardware.getInstance().gyro.getRate();
		robotState.drivePose.leftDistance = HardwareAdapter.DrivetrainHardware.getInstance().leftDriveEncoder.get();
		robotState.drivePose.leftVelocity = HardwareAdapter.DrivetrainHardware.getInstance().leftDriveEncoder.getRate();
		robotState.drivePose.rightDistance = HardwareAdapter.DrivetrainHardware.getInstance().rightDriveEncoder.get();
		robotState.drivePose.rightVelocity = HardwareAdapter.DrivetrainHardware.getInstance().rightDriveEncoder.getRate();
	}

	/**
	 * Sets the output from all subsystems for the respective hardware
	 */
	void updateSubsystems() {
		updateDrivetrain();
	}
	
	private void updateDrivetrain() {
		HardwareAdapter.getInstance().getDrivetrain().leftDriveMotor.set(mDrive.getDriveSignal().leftMotor);
		// Need to invert right side motor
		HardwareAdapter.getInstance().getDrivetrain().rightDriveMotor.set(-mDrive.getDriveSignal().rightMotor);
	}
	

}
