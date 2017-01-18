package com.palyrobotics.frc2016.robot;

import com.ctre.CANTalon;
import com.palyrobotics.frc2016.config.Constants;
import com.palyrobotics.frc2016.config.RobotState;
import com.palyrobotics.frc2016.subsystems.Drive;

/**
 * Should only be used in robot package.
 */
class HardwareUpdater {
	private Drive mDrive;
	
	/**
	 * Hardware Updater for Derica
	 * Updates Drive
	 * @param mDrive
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
		// Only on Derica
		if(Constants.kRobotName == Constants.RobotName.DERICA) {
			robotState.m_pose.m_left_distance = HardwareAdapter.DrivetrainHardware.getInstance().kDriveLeftMasterTalon.getEncPosition();
			robotState.m_pose.m_left_velocity = HardwareAdapter.DrivetrainHardware.getInstance().kDriveLeftMasterTalon.getEncVelocity();
			robotState.m_pose.m_right_distance = HardwareAdapter.DrivetrainHardware.getInstance().kDriveRightMasterTalon.getEncPosition();
			robotState.m_pose.m_right_velocity = HardwareAdapter.DrivetrainHardware.getInstance().kDriveRightMasterTalon.getEncVelocity();
		}
	}

	/**
	 * Sets the output from all subsystems for the respective hardware
	 */
	void updateSubsystems() {
		updateDrivetrain();
	}

	private void updateDrivetrain() {
		CANTalon kLeftFront = HardwareAdapter.getInstance().getDrivetrain().kDriveLeftSlaveTalon;
		CANTalon kLeftBack = HardwareAdapter.getInstance().getDrivetrain().kDriveLeftMasterTalon;

		HardwareAdapter.getInstance().getDrivetrain().kDriveLeftMasterTalon.set(mDrive.getDriveSignal().leftMotor);
		// Need to invert right side motors
		CANTalon kRightFront = HardwareAdapter.getInstance().getDrivetrain().kDriveRightSlaveTalon;
		CANTalon kRightBack = HardwareAdapter.getInstance().getDrivetrain().kDriveRightMasterTalon;
		HardwareAdapter.getInstance().getDrivetrain().kDriveRightSlaveTalon.set(-mDrive.getDriveSignal().rightMotor);
		HardwareAdapter.getInstance().getDrivetrain().kDriveRightMasterTalon.set(-mDrive.getDriveSignal().rightMotor);
	}
	

}
