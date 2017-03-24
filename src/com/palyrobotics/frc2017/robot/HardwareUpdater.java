package com.palyrobotics.frc2017.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.team254.lib.util.ADXRS453_Gyro;
import com.palyrobotics.frc2017.config.Constants.RobotName;
import com.palyrobotics.frc2017.config.dashboard.DashboardManager;
import com.palyrobotics.frc2017.subsystems.*;
import com.palyrobotics.frc2017.util.CANTalonOutput;
import com.palyrobotics.frc2017.util.logger.Logger;

import java.util.Optional;

/**
 * Should only be used in robot package.
 */
class HardwareUpdater {
	// Subsystem references
	private Drive mDrive;
	private Flippers mFlippers;
	private Slider mSlider;
	private Spatula mSpatula;
	private Intake mIntake;
	private Climber mClimber;

	/**
	 * Hardware Updater for Steik
	 */
	HardwareUpdater(Drive drive, Flippers flippers, Slider slider, Spatula spatula, Intake intake, Climber climber)
			throws Exception {
		if (Constants.kRobotName != Constants.RobotName.STEIK) {
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
	 * 
	 * @throws Exception
	 */
	HardwareUpdater(Drive drive) throws Exception {
		if (Constants.kRobotName != Constants.RobotName.DERICA) {
			System.out.println("Incompatible robot name and hardware!");
			throw new Exception();
		}
		this.mDrive = drive;
	}

	
	/**
	 * Initialize all hardware
	 */
	void initHardware() {
		Logger.getInstance().logRobotThread("Init hardware");
		configureTalons(true);
	}
	
	void disableTalons() {
		Logger.getInstance().logRobotThread("Disabling talons");
		HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon.disable();
		HardwareAdapter.getInstance().getDrivetrain().leftSlave1Talon.disable();
		HardwareAdapter.getInstance().getDrivetrain().leftSlave2Talon.disable();
		HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon.disable();
		HardwareAdapter.getInstance().getDrivetrain().rightSlave1Talon.disable();
		HardwareAdapter.getInstance().getDrivetrain().rightSlave2Talon.disable();
		if(Constants.kRobotName == RobotName.STEIK) {
			HardwareAdapter.getInstance().getClimber().climberTalon.disable();
			HardwareAdapter.getInstance().getSlider().sliderTalon.disable();
		}
	}
	
	void configureTalons(boolean calibrateSliderEncoder) {
		configureDriveTalons();
		if (Constants.kRobotName == RobotName.STEIK) {
			//Climber setup
			CANTalon climber = HardwareAdapter.ClimberHardware.getInstance().climberTalon;
			climber.reset();
			climber.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
			climber.setPosition(0);	
			climber.configMaxOutputVoltage(Constants.kClimberMaxVoltage);
			climber.configPeakOutputVoltage(Constants.kClimberMaxVoltage, 0); // Should never be used
			climber.ConfigRevLimitSwitchNormallyOpen(false); // Prevent the motor from spinning backwards
			climber.ConfigFwdLimitSwitchNormallyOpen(true);
			climber.enable();
			
			CANTalon slider = HardwareAdapter.SliderHardware.getInstance().sliderTalon;
			// Reset and turn on the Talon 
			slider.reset();
			slider.clearStickyFaults();
			slider.enable();
			slider.enableControl();
			slider.configMaxOutputVoltage(Constants.kSliderMaxVoltage);
			slider.configPeakOutputVoltage(Constants.kSliderPeakOutputVoltage, -Constants.kSliderPeakOutputVoltage);
			if (calibrateSliderEncoder) {
				// Set up the Talon to read from a relative CTRE mag encoder sensor
				slider.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
				// Calibrate the encoder
				double current_pot_pos = HardwareAdapter.SliderHardware.getInstance().sliderPotentiometer.getValue();
				double distance_to_center = current_pot_pos - Constants.kPotentiometerCenterPos;
				double position_in_rev = (distance_to_center / 4096.0) * 10.0;
				if (Constants.kCalibrateSliderWithPotentiometer) {
					slider.setPosition(-position_in_rev);
				} else {
					slider.setPosition(0);
				}
			}
		}
	}
	
	void configureDriveTalons() {
		CANTalon leftMasterTalon = HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon;
		CANTalon leftSlave1Talon = HardwareAdapter.getInstance().getDrivetrain().leftSlave1Talon;
		CANTalon leftSlave2Talon = HardwareAdapter.getInstance().getDrivetrain().leftSlave2Talon;
		CANTalon rightMasterTalon = HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon;
		CANTalon rightSlave1Talon = HardwareAdapter.getInstance().getDrivetrain().rightSlave1Talon;
		CANTalon rightSlave2Talon = HardwareAdapter.getInstance().getDrivetrain().rightSlave2Talon;
		
		// Enable all talons' brake mode and disables forward and reverse soft
		// limits
		leftMasterTalon.enableBrakeMode(true);
		leftSlave1Talon.enableBrakeMode(true);
		if (leftSlave2Talon != null) rightSlave2Talon.enableBrakeMode(true);
		rightMasterTalon.enableBrakeMode(true);
		rightSlave1Talon.enableBrakeMode(true);
		if (rightSlave2Talon != null) leftSlave2Talon.enableBrakeMode(true);

		leftMasterTalon.enableForwardSoftLimit(false);
		leftMasterTalon.enableReverseSoftLimit(false);
		leftSlave1Talon.enableForwardSoftLimit(false);
		leftSlave1Talon.enableReverseSoftLimit(false);
		if (leftSlave2Talon != null) {leftSlave2Talon.enableForwardSoftLimit(false); leftSlave2Talon.enableReverseSoftLimit(false);}
		rightMasterTalon.enableForwardSoftLimit(false);
		rightMasterTalon.enableReverseSoftLimit(false);
		rightSlave1Talon.enableForwardSoftLimit(false);
		rightSlave1Talon.enableReverseSoftLimit(false);
		if (rightSlave2Talon != null) {rightSlave2Talon.enableForwardSoftLimit(false); rightSlave2Talon.enableReverseSoftLimit(false);}
		
		// Enable all the talons
		leftMasterTalon.enable();
		leftSlave1Talon.enable();
		if (leftSlave2Talon != null) leftSlave2Talon.enable();
		rightMasterTalon.enable();
		rightSlave1Talon.enable();
		if (rightSlave2Talon != null) rightSlave2Talon.enable();
		
		// Allow max voltage for closed loop control
		leftMasterTalon.configPeakOutputVoltage(Constants.kDriveMaxClosedLoopOutput, -Constants.kDriveMaxClosedLoopOutput);
		leftSlave1Talon.configPeakOutputVoltage(Constants.kDriveMaxClosedLoopOutput, -Constants.kDriveMaxClosedLoopOutput);
		if (leftSlave2Talon != null) leftSlave2Talon.configPeakOutputVoltage(Constants.kDriveMaxClosedLoopOutput, -Constants.kDriveMaxClosedLoopOutput);
		rightMasterTalon.configPeakOutputVoltage(Constants.kDriveMaxClosedLoopOutput, -Constants.kDriveMaxClosedLoopOutput);
		rightSlave1Talon.configPeakOutputVoltage(Constants.kDriveMaxClosedLoopOutput, -Constants.kDriveMaxClosedLoopOutput);
		if (rightSlave2Talon != null) rightSlave2Talon.configPeakOutputVoltage(Constants.kDriveMaxClosedLoopOutput, -Constants.kDriveMaxClosedLoopOutput);
		
		// Allow max voltage for open loop control
		leftMasterTalon.configMaxOutputVoltage(13);
		leftSlave1Talon.configMaxOutputVoltage(13);
		if (leftSlave2Talon != null) leftSlave2Talon.configMaxOutputVoltage(13);
		rightMasterTalon.configMaxOutputVoltage(13);
		rightSlave1Talon.configMaxOutputVoltage(13);
		if (rightSlave2Talon != null) rightSlave2Talon.configMaxOutputVoltage(13);
		
		// Configure master talon feedback devices
		leftMasterTalon.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		rightMasterTalon.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);

		// Zero encoders
		leftMasterTalon.setEncPosition(0);
		rightMasterTalon.setEncPosition(0);
		leftMasterTalon.setPosition(0);
		rightMasterTalon.setPosition(0);

		// Reverse right side
		rightMasterTalon.reverseOutput(true);
		rightMasterTalon.setInverted(true);
		rightMasterTalon.reverseSensor(true);

		// Set slave talons to follower mode
		leftSlave1Talon.changeControlMode(CANTalon.TalonControlMode.Follower);
		leftSlave1Talon.set(leftMasterTalon.getDeviceID());
		if (leftSlave2Talon != null) {
			leftSlave2Talon.changeControlMode(CANTalon.TalonControlMode.Follower);
			leftSlave2Talon.set(leftMasterTalon.getDeviceID());
		}
		rightSlave1Talon.changeControlMode(CANTalon.TalonControlMode.Follower);
		rightSlave1Talon.set(rightMasterTalon.getDeviceID());
		if (rightSlave2Talon != null) {
			rightSlave2Talon.changeControlMode(CANTalon.TalonControlMode.Follower);
			rightSlave2Talon.set(rightMasterTalon.getDeviceID());
		}
	}

	/**
	 * Updates all the sensor data taken from the hardware
	 */
	void updateSensors(RobotState robotState) {
		robotState.leftControlMode = HardwareAdapter.DrivetrainHardware.getInstance().leftMasterTalon.getControlMode();
		robotState.rightControlMode = HardwareAdapter.DrivetrainHardware.getInstance().rightMasterTalon.getControlMode();
		robotState.leftSetpoint = HardwareAdapter.DrivetrainHardware.getInstance().leftMasterTalon.getSetpoint();
		robotState.rightSetpoint = HardwareAdapter.DrivetrainHardware.getInstance().rightMasterTalon.getSetpoint();
		ADXRS453_Gyro gyro = HardwareAdapter.DrivetrainHardware.getInstance().gyro;
		if (gyro != null) {
			robotState.drivePose.heading = HardwareAdapter.DrivetrainHardware.getInstance().gyro.getAngle();
			robotState.drivePose.headingVelocity = HardwareAdapter.DrivetrainHardware.getInstance().gyro.getRate();
			// Invert Steik gyros
			if (Constants.kRobotName != RobotName.DERICA) {
				robotState.drivePose.heading*=-1;
				robotState.drivePose.headingVelocity*=-1;
			}
		} else {
			robotState.drivePose.heading = -0;
			robotState.drivePose.headingVelocity = -0;
		}
		CANTalon leftMasterTalon = HardwareAdapter.DrivetrainHardware.getInstance().leftMasterTalon;
		CANTalon rightMasterTalon = HardwareAdapter.DrivetrainHardware.getInstance().rightMasterTalon;
		robotState.drivePose.leftEnc = leftMasterTalon.getPosition();
		robotState.drivePose.leftEncVelocity = leftMasterTalon.getEncVelocity();
		robotState.drivePose.leftSpeed = leftMasterTalon.getSpeed();
		// rightEnc is not getEncPosition() because that returns the absolute
		// position, not the inverted one, which we want.
		robotState.drivePose.rightEnc = rightMasterTalon.getPosition();
		robotState.drivePose.rightEncVelocity = rightMasterTalon.getEncVelocity();
		robotState.drivePose.rightSpeed = rightMasterTalon.getSpeed();
		if (leftMasterTalon.getControlMode().isPID()) {
			robotState.drivePose.leftError = Optional.of(leftMasterTalon.getError());
		} else {
			robotState.drivePose.leftError = Optional.empty();
		}
		if (rightMasterTalon.getControlMode().isPID()) {
			robotState.drivePose.rightError = Optional.of(rightMasterTalon.getError());
		} else {
			robotState.drivePose.rightError = Optional.empty();
		}
		if (Constants.kRobotName == Constants.RobotName.STEIK) {
			CANTalon sliderTalon = HardwareAdapter.SliderHardware.getInstance().sliderTalon;
			robotState.sliderEncoder = sliderTalon.getPosition();
			DashboardManager.getInstance().updateCANTable(HardwareAdapter.DrivetrainHardware.getInstance().leftMasterTalon.getOutputVoltage() + "," + HardwareAdapter.DrivetrainHardware.getInstance().rightMasterTalon.getOutputVoltage() + "," + HardwareAdapter.DrivetrainHardware.getInstance().leftMasterTalon.getPosition() + "," + HardwareAdapter.DrivetrainHardware.getInstance().rightMasterTalon.getPosition() + "," +  HardwareAdapter.DrivetrainHardware.getInstance().leftMasterTalon.getClosedLoopError() + "," + HardwareAdapter.DrivetrainHardware.getInstance().rightMasterTalon.getClosedLoopError());
			robotState.sliderPotentiometer = HardwareAdapter.SliderHardware.getInstance().sliderPotentiometer.getValue();
			robotState.sliderVelocity = sliderTalon.getSpeed();
			if (sliderTalon.getControlMode().isPID()) {
				robotState.sliderClosedLoopError = Optional.of(sliderTalon.getClosedLoopError());
			} else {
				robotState.sliderClosedLoopError = Optional.empty();
			}
		}
		if (HardwareAdapter.getInstance().getClimber().climberTalon != null) {
			robotState.climberEncoder = HardwareAdapter.ClimberHardware.getInstance().climberTalon.getPosition();
		}
		
		robotState.sliderPosition = HardwareAdapter.SliderHardware.getInstance().sliderTalon.getEncPosition();
	}

	/**
	 * Sets the output from all subsystems for the respective hardware
	 */
	void updateSubsystems() {
		// On Derica only update the drivetrain
		if (Constants.kRobotName == Constants.RobotName.STEIK) {
			updateSteikSubsystems();
		}
		updateDrivetrain();
	}

	private void updateSteikSubsystems() {
//		// FLIPPERS
//		HardwareAdapter.getInstance().getFlippers().leftSolenoid.set(mFlippers.getFlipperSignal().leftFlipper);
//		HardwareAdapter.getInstance().getFlippers().rightSolenoid.set(mFlippers.getFlipperSignal().rightFlipper);
//		// SLIDER
		updateCANTalonSRX(HardwareAdapter.getInstance().getSlider().sliderTalon, mSlider.getOutput());
		// SPATULA
		HardwareAdapter.getInstance().getSpatula().spatulaSolenoid.set(mSpatula.getOutput());
//		// INTAKE
		HardwareAdapter.getInstance().getIntake().leftIntakeMotor.set(mIntake.getOutput());
		HardwareAdapter.getInstance().getIntake().rightIntakeMotor.set(-mIntake.getOutput());
		// CLIMBER
		updateCANTalonSRX(HardwareAdapter.getInstance().getClimber().climberTalon, mClimber.getOutput());
	}

	/**
	 * Updates the drivetrain on Derica, Steik 
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
			talon.changeControlMode(output.getControlMode());
			if(output.getControlMode().isPID() || output.getControlMode() == TalonControlMode.MotionMagic) {
				talon.setPID(output.gains.P, output.gains.I, output.gains.D, output.gains.F, output.gains.izone, output.gains.rampRate, output.profile);
			}
			if (output.getControlMode() == CANTalon.TalonControlMode.MotionMagic) {
				talon.setMotionMagicAcceleration(output.accel);
				talon.setMotionMagicCruiseVelocity(output.cruiseVel);
			}
		talon.set(output.getSetpoint());
	}
}