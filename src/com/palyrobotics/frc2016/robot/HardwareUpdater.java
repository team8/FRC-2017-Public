package com.palyrobotics.frc2016.robot;

import com.ctre.CANTalon;
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
		System.out.println("Gyro "+HardwareAdapter.DrivetrainHardware.getInstance().kGyro.getAngle());
		RobotState robotState = Robot.getRobotState();
		robotState.m_pose.m_heading = HardwareAdapter.DrivetrainHardware.getInstance().kGyro.getAngle();
		robotState.m_pose.m_heading_velocity = HardwareAdapter.DrivetrainHardware.getInstance().kGyro.getRate();
		robotState.m_pose.m_left_distance = HardwareAdapter.DrivetrainHardware.getInstance().kLeftDriveEncoder.get();
		robotState.m_pose.m_left_velocity = HardwareAdapter.DrivetrainHardware.getInstance().kLeftDriveEncoder.getRate();
		robotState.m_pose.m_right_distance = HardwareAdapter.DrivetrainHardware.getInstance().kRightDriveEncoder.get();
		robotState.m_pose.m_right_velocity = HardwareAdapter.DrivetrainHardware.getInstance().kRightDriveEncoder.getRate();
	}

	/**
	 * Sets the output from all subsystems for the respective hardware
	 */
	void updateSubsystems() {
		updateDrivetrain();
	}

	private void updateDrivetrain() {
		HardwareAdapter.getInstance().getDrivetrain().kLeftFrontDriveMotor.set(mDrive.getDriveSignal().leftMotor);
		HardwareAdapter.getInstance().getDrivetrain().kLeftBackDriveMotor.set(mDrive.getDriveSignal().leftMotor);
		// Need to invert right side motors
		HardwareAdapter.getInstance().getDrivetrain().kRightFrontDriveMotor.set(-mDrive.getDriveSignal().rightMotor);
		HardwareAdapter.getInstance().getDrivetrain().kRightBackDriveMotor.set(-mDrive.getDriveSignal().rightMotor);
	}
	

}
