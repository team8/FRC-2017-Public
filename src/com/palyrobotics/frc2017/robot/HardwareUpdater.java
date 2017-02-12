package com.palyrobotics.frc2017.robot;

import com.ctre.CANTalon;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.subsystems.*;
import com.palyrobotics.frc2017.util.CANTalonOutput;
import com.palyrobotics.frc2017.util.LegacyDrive;

/**
 * Should only be used in robot package.
 */
class HardwareUpdater {
	// Subsystem references
	private LegacyDrive mLegacyDrive;
	private Drive mDrive;
	private Flippers mFlippers;
	private Slider mSlider;
	private Spatula mSpatula;
	private Intake mIntake;
	private Climber mClimber;

	/**
	 * Hardware Updater for Steik/Aegir
	 */
	HardwareUpdater(Drive drive, Flippers flippers, Slider slider, Spatula spatula, Intake intake, Climber climber) throws Exception {
		if(Constants.kRobotName != Constants.RobotName.AEGIR && Constants.kRobotName != Constants.RobotName.STEIK) {
			System.out.println("Incompatible robot name and hardware!");
			throw new Exception();
		}
		this.mDrive = drive;
		this.mFlippers = flippers;
		this.mSlider = slider;
		this.mSpatula = spatula;
		this.mIntake = intake;
		this.mClimber = climber;
	}

	/**
	 * Hardware updater for Derica
	 * @throws Exception 
	 */
	HardwareUpdater(Drive drive) throws Exception {
		if(Constants.kRobotName != Constants.RobotName.DERICA) {
			System.out.println("Incompatible robot name and hardware!");
			throw new Exception();
		}
		this.mDrive = drive;
	}

	/**
	 * Hardware Updater for Tyr
	 * Updates only the drivetrain
	 */
	HardwareUpdater(LegacyDrive drive) throws Exception {
		if(Constants.kRobotName != Constants.RobotName.TYR) {
			System.out.println("Incompatible robot name and hardware!");
			throw new Exception();
		}

		this.mLegacyDrive = drive;
	}

	/**
	 * Initialize all hardware
	 */
	void initHardware() {
		// TODO: Add the gyro calibration
//		HardwareAdapter.getInstance().getDrivetrain().gyro.calibrate();
		System.out.println("Gyro calibrated");
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

			// Enable all the talons
			leftMasterTalon.enable();
			leftSlaveTalon.enable();
			rightMasterTalon.enable();
			rightSlaveTalon.enable();
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
	void updateSensors(RobotState robotState) {
		robotState.drivePose.heading = HardwareAdapter.DrivetrainHardware.getInstance().gyro.getAngle();
		robotState.drivePose.headingVelocity = HardwareAdapter.DrivetrainHardware.getInstance().gyro.getRate();
		// Non-Tyr robots use talons
		if(Constants.kRobotName != Constants.RobotName.TYR) {
			CANTalon leftMasterTalon = HardwareAdapter.DrivetrainHardware.getInstance().leftMasterTalon;
			CANTalon rightMasterTalon = HardwareAdapter.DrivetrainHardware.getInstance().rightMasterTalon;
			robotState.drivePose.leftDistance = leftMasterTalon.getEncPosition();
			robotState.drivePose.leftVelocity = leftMasterTalon.getEncVelocity();
			robotState.drivePose.rightDistance = rightMasterTalon.getEncPosition();
			robotState.drivePose.rightVelocity = rightMasterTalon.getEncVelocity();

			robotState.leftClosedLoopError = leftMasterTalon.getClosedLoopError();
			robotState.rightClosedLoopError = rightMasterTalon.getClosedLoopError();
		}
		if(Constants.kRobotName == Constants.RobotName.AEGIR || Constants.kRobotName == Constants.RobotName.STEIK) {
			robotState.sliderEncoder = HardwareAdapter.SliderHardware.getInstance().sliderMotor.getPosition();
//			robotState.sliderEncoder = HardwareAdapter.SliderHardware.getInstance().sliderEncoder.getDistance();
			robotState.sliderPotentiometer = HardwareAdapter.SliderHardware.getInstance().sliderPotentiometer.get();
			robotState.sliderRightHFX = HardwareAdapter.SliderHardware.getInstance().sliderRightHFX.get();
			robotState.sliderLeftHFX = HardwareAdapter.SliderHardware.getInstance().sliderLeftHFX.get();
		}

		// Update kPDP current draw
		if (Constants.kRobotName == Constants.RobotName.STEIK) {
			robotState.climberCurrentDraw = HardwareAdapter.getInstance().kPDP.getCurrent(Constants.kSteikClimberMotorPDP);
		} else if (Constants.kRobotName == Constants.RobotName.AEGIR) {
			robotState.climberCurrentDraw = HardwareAdapter.getInstance().kPDP.getCurrent(Constants.kAegirClimberMotorPDP);
		}

		if (HardwareAdapter.getInstance().getClimber().climberEncoder != null) {
			robotState.climberEncoder = HardwareAdapter.getInstance().getClimber().climberEncoder.get();
		}
	}

	/**
	 * Sets the output from all subsystems for the respective hardware
	 */
	void updateSubsystems() {
		// On Derica or Tyr only update the drivetrain
		if (Constants.kRobotName == Constants.RobotName.STEIK || Constants.kRobotName == Constants.RobotName.AEGIR) {
			updateSteikSubsystems();
		}
		if (Constants.kRobotName == Constants.RobotName.TYR) {
			updateTyrDrivetrain();
		} else {
			updateDrivetrain();
		}
	}

	private void updateSteikSubsystems() {
		// FLIPPERS
		HardwareAdapter.getInstance().getFlippers().leftSolenoid.set(mFlippers.getFlipperSignal().leftFlipper);
		HardwareAdapter.getInstance().getFlippers().rightSolenoid.set(mFlippers.getFlipperSignal().rightFlipper);
		// SLIDER
		updateCANTalonSRX(HardwareAdapter.getInstance().getSimpleSlider().sliderMotor, mSlider.getOutput());
		// SPATULA
		HardwareAdapter.getInstance().getSpatula().spatulaSolenoid.set(mSpatula.getOutput());
		// INTAKE
		HardwareAdapter.getInstance().getIntake().leftIntakeMotor.set(mIntake.getOutput());
		HardwareAdapter.getInstance().getIntake().rightIntakeMotor.set(-mIntake.getOutput());
		// CLIMBER
		HardwareAdapter.getInstance().getClimber().climberMotor.set(mClimber.getOutput());
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
				talon.setPID(output.P, output.I, output.D, output.F, output.izone, output.rampRate, output.profile);
			}
		}
		// Don't resend setpoint if that is the currently running loop
		if(talon.getSetpoint() != output.getSetpoint()) {
			talon.setSetpoint(output.getSetpoint());
		}
	}

	/**
	 * Updates the drivetrain on Tyr
	 * Can only use direct PWM signals
	 * Automatically inverts the right side motor output
	 */
	private void updateTyrDrivetrain() {
		CANTalon kLeftFront = HardwareAdapter.getInstance().getDrivetrain().leftSlaveTalon;
		CANTalon kLeftBack = HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon;
		kLeftFront.set(mLegacyDrive.getDriveSignal().leftMotor.getSetpoint());
		kLeftBack.set(mLegacyDrive.getDriveSignal().leftMotor.getSetpoint());

		// Need to invert right side motors
		CANTalon kRightFront = HardwareAdapter.getInstance().getDrivetrain().rightSlaveTalon;
		CANTalon kRightBack = HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon;
		kRightFront.set(-mLegacyDrive.getDriveSignal().rightMotor.getSetpoint());
		kRightBack.set(-mLegacyDrive.getDriveSignal().rightMotor.getSetpoint());
	}
}