package com.palyrobotics.frc2016.robot;

import com.ctre.CANTalon;
import com.palyrobotics.frc2016.config.Constants;
import com.palyrobotics.frc2016.config.RobotState;
import com.palyrobotics.frc2016.subsystems.Drive;
import com.palyrobotics.frc2016.util.CANTalonOutput;

/**
 * Should only be used in robot package.
 */
class HardwareUpdater {
	private Drive mDrive;
	
	/**
	 * Hardware Updater for Derica and Tyr
	 * Updates only the drivetrain
	 */
	HardwareUpdater(Drive drive) {
		this.mDrive = drive;
	}

	/**
	 * Hardware Updater for Steik and Aegir
	 */
	HardwareUpdater(Drive drive, int temp) {
		this.mDrive = drive;
	}

	/**
	 * Initialize all hardware
	 */
	void initHardware() {
		HardwareAdapter.getInstance().getDrivetrain().gyro.calibrate();
		if(Constants.kRobotName != Constants.RobotName.TYR) {
			CANTalon leftMasterTalon = HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon;
			CANTalon leftSlaveTalon = HardwareAdapter.getInstance().getDrivetrain().leftSlaveTalon;
			CANTalon rightMasterTalon = HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon;
			CANTalon rightSlaveTalon = HardwareAdapter.getInstance().getDrivetrain().rightSlaveTalon;

			// Enable all talons' brake mode and disables forward and reverse soft limits
			leftMasterTalon.enableBrakeMode(true);
			leftSlaveTalon.enableBrakeMode(true);
			rightSlaveTalon.enableBrakeMode(true);
			rightMasterTalon.enableBrakeMode(true);
			leftMasterTalon.enableForwardSoftLimit(false);
			leftMasterTalon.enableReverseSoftLimit(false);
			rightMasterTalon.enableForwardSoftLimit(false);
			rightMasterTalon.enableReverseSoftLimit(false);

			// Configure master talon feedback devises
			leftMasterTalon.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
			rightMasterTalon.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
			leftMasterTalon.reverseSensor(true);
			rightMasterTalon.reverseOutput(true);

			// Zero encoders
			leftMasterTalon.setEncPosition(0);
			rightMasterTalon.setEncPosition(0);

			// Set slave talons to follower mode
			leftSlaveTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
			leftSlaveTalon.set(leftMasterTalon.getDeviceID());
			rightSlaveTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
			rightSlaveTalon.set(rightMasterTalon.getDeviceID());
			// Limit max output voltage
			leftMasterTalon.configPeakOutputVoltage(Constants.kPeakVoltage, -Constants.kPeakVoltage);
			rightMasterTalon.configPeakOutputVoltage(Constants.kPeakVoltage, -Constants.kPeakVoltage);

		}
	}

	/**
	 * Updates all the sensor data taken from the hardware
	 */
	void updateSensors() {
		System.out.println("Gyro "+HardwareAdapter.DrivetrainHardware.getInstance().gyro.getAngle());
		RobotState robotState = Robot.getRobotState();
		robotState.drivePose.heading = HardwareAdapter.DrivetrainHardware.getInstance().gyro.getAngle();
		robotState.drivePose.headingVelocity = HardwareAdapter.DrivetrainHardware.getInstance().gyro.getRate();
		// Non-Tyr robots use talons
		if(Constants.kRobotName != Constants.RobotName.TYR) {
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
		// On Derica or Tyr only update the drivetrain
		if(Constants.kRobotName == Constants.RobotName.TYR) {
			updateTyrDrivetrain();
		} else if (Constants.kRobotName == Constants.RobotName.DERICA) {
			updateDrivetrain();
		} else {
			updateDrivetrain();
			updateSteikSubsystems();
		}
	}

	private void updateSteikSubsystems() {

	}

	/**
	 * Updates the drivetrain on Derica, Steik, Aegir
	 * Uses CANTalonOutput and can run off-board control loops through SRX
	 */
	private void updateDrivetrain() {
		updateCANTalonSRX(HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon, mDrive.getDriveSignal().leftMotor);
		updateCANTalonSRX(HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon, mDrive.getDriveSignal().rightMotor);
	}

	/**
	 * Helper method for processing a CANTalonOutput for an SRX
	 */
	private void updateCANTalonSRX(CANTalon talon, CANTalonOutput output) {
		if(talon.getControlMode() != output.getControlMode()) {
			talon.changeControlMode(output.getControlMode());
			if(output.getControlMode().isPID()) {
				talon.setPID(output.P, output.I, output.D, output.f, output.izone, output.rampRate, output.profile);
			}
		}
		talon.setSetpoint(output.getSetpoint());
	}

	/**
	 * Updates the drivetrain on Tyr
	 * Can only use direct PWM signals
	 * Automatically inverts the right side motor output
	 */
	private void updateTyrDrivetrain() {
		CANTalon kLeftFront = HardwareAdapter.getInstance().getDrivetrain().leftSlaveTalon;
		CANTalon kLeftBack = HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon;
		kLeftFront.set(mDrive.getDriveSignal().leftMotor.getSetpoint());
		kLeftBack.set(mDrive.getDriveSignal().leftMotor.getSetpoint());

		// Need to invert right side motors
		CANTalon kRightFront = HardwareAdapter.getInstance().getDrivetrain().rightSlaveTalon;
		CANTalon kRightBack = HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon;
		kRightFront.set(-mDrive.getDriveSignal().rightMotor.getSetpoint());
		kRightBack.set(-mDrive.getDriveSignal().rightMotor.getSetpoint());
	}

}