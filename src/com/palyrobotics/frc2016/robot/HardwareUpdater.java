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
	 * @param drive
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
		// Only on Derica
		if(Constants.kRobotName == Constants.RobotName.DERICA) {
			robotState.drivePose.leftDistance = HardwareAdapter.DrivetrainHardware.getInstance().leftMasterTalon.getEncPosition();
			robotState.drivePose.leftVelocity = HardwareAdapter.DrivetrainHardware.getInstance().leftMasterTalon.getEncVelocity();
			robotState.drivePose.rightDistance = HardwareAdapter.DrivetrainHardware.getInstance().rightMasterTalon.getEncPosition();
			robotState.drivePose.rightVelocity = HardwareAdapter.DrivetrainHardware.getInstance().rightMasterTalon.getEncVelocity();
		}
	}

	/**
	 * Sets the output from all subsystems for the respective hardware
	 */
	void updateSubsystems() {
		updateDrivetrain();
	}

	private void updateDrivetrain() {
		CANTalon kLeftFront = HardwareAdapter.getInstance().getDrivetrain().leftSlaveTalon;
		CANTalon kLeftBack = HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon;

		HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon.set(mDrive.getDriveSignal().leftMotor);
		// Need to invert right side motors
		CANTalon kRightFront = HardwareAdapter.getInstance().getDrivetrain().rightSlaveTalon;
		CANTalon kRightBack = HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon;
		HardwareAdapter.getInstance().getDrivetrain().rightSlaveTalon.set(-mDrive.getDriveSignal().rightMotor);
		HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon.set(-mDrive.getDriveSignal().rightMotor);
	}
	

}
