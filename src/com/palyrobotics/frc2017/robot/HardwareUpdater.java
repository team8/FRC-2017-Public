package com.palyrobotics.frc2017.robot;

import com.ctre.CANTalon;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.subsystems.*;
import com.palyrobotics.frc2017.util.CANTalonOutput;

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
	 * Hardware Updater for Steik/Aegir
	 */
	HardwareUpdater(Drive drive, Flippers flippers, Slider slider, Spatula spatula, Intake intake, Climber climber)
			throws Exception {
		if (Constants.kRobotName != Constants.RobotName.AEGIR && Constants.kRobotName != Constants.RobotName.STEIK) {
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
		HardwareAdapter.getInstance().getDrivetrain().gyro.calibrate();
		CANTalon leftMasterTalon = HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon;
		CANTalon leftSlaveTalon = HardwareAdapter.getInstance().getDrivetrain().leftSlaveTalon;
		CANTalon otherLeftSlaveTalon = HardwareAdapter.getInstance().getDrivetrain().secondLeftSlaveTalon;
		CANTalon rightMasterTalon = HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon;
		CANTalon rightSlaveTalon = HardwareAdapter.getInstance().getDrivetrain().rightSlaveTalon;
		CANTalon otherRightSlaveTalon = HardwareAdapter.getInstance().getDrivetrain().secondRightSlaveTalon;
		
		// Enable all talons' brake mode and disables forward and reverse soft
		// limits
		leftMasterTalon.enableBrakeMode(true);
		leftSlaveTalon.enableBrakeMode(true);
		otherLeftSlaveTalon.enableBrakeMode(true);
		rightSlaveTalon.enableBrakeMode(true);
		rightMasterTalon.enableBrakeMode(true);
		otherRightSlaveTalon.enableBrakeMode(true);
		leftMasterTalon.enableForwardSoftLimit(false);
		leftMasterTalon.enableReverseSoftLimit(false);
		otherLeftSlaveTalon.enableForwardSoftLimit(false);
		rightMasterTalon.enableForwardSoftLimit(false);
		rightMasterTalon.enableReverseSoftLimit(false);
		otherRightSlaveTalon.enableForwardSoftLimit(false);
		
		// Enable all the talons
		leftMasterTalon.enable();
		leftSlaveTalon.enable();
		otherLeftSlaveTalon.enable();
		rightMasterTalon.enable();
		rightSlaveTalon.enable();
		otherRightSlaveTalon.enable();
		
		leftMasterTalon.configPeakOutputVoltage(12, -12);
		rightMasterTalon.configPeakOutputVoltage(12, -12);
		leftSlaveTalon.configPeakOutputVoltage(12, -12);
		rightSlaveTalon.configPeakOutputVoltage(12, -12);
		
		// Configure master talon feedback devices
		leftMasterTalon.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		rightMasterTalon.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);

		// Zero encoders
		leftMasterTalon.setEncPosition(0);
		rightMasterTalon.setEncPosition(0);

		// Reverse right side
		rightMasterTalon.reverseOutput(true);
		rightMasterTalon.setInverted(true);
		rightMasterTalon.reverseSensor(true);

		// Set slave talons to follower mode
		leftSlaveTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
		leftSlaveTalon.set(leftMasterTalon.getDeviceID());
		otherLeftSlaveTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
		otherLeftSlaveTalon.set(leftMasterTalon.getDeviceID());
		rightSlaveTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
		rightSlaveTalon.set(rightMasterTalon.getDeviceID());
		otherRightSlaveTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
		otherRightSlaveTalon.set(rightMasterTalon.getDeviceID());

		// Slider
		// Reset and turn on the Talon
		CANTalon sliderTalon = HardwareAdapter.SliderHardware.getInstance().sliderMotor;
		sliderTalon.reset();
		sliderTalon.clearStickyFaults();
		sliderTalon.enable();
		sliderTalon.enableControl();

		// Limit the Talon output
		sliderTalon.configMaxOutputVoltage(Constants.kSliderMaxVoltage);
		sliderTalon.configPeakOutputVoltage(Constants.kSliderMaxVoltage, -Constants.kSliderMaxVoltage);
		sliderTalon.setVoltageRampRate(Integer.MAX_VALUE);

		// Set up the Talon to read from a relative CTRE mag encoder sensor
		sliderTalon.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Absolute);
		sliderTalon.setPosition(0);
		sliderTalon.setEncPosition(0);
		sliderTalon.setPulseWidthPosition(0);

		// Climber setup
		CANTalon climber = HardwareAdapter.ClimberHardware.getInstance().climberMotor;
		climber.reset();
		climber.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Absolute);
		climber.setPosition(0);	
		climber.ConfigRevLimitSwitchNormallyOpen(false); // Prevent the motor from driving backwards
		climber.ConfigFwdLimitSwitchNormallyOpen(true);
		climber.enable();
	}

	/**
	 * Updates all the sensor data taken from the hardware
	 */
	void updateSensors(RobotState robotState) {
		robotState.leftControlMode = HardwareAdapter.DrivetrainHardware.getInstance().leftMasterTalon.getControlMode();
		robotState.rightControlMode = HardwareAdapter.DrivetrainHardware.getInstance().rightMasterTalon.getControlMode();
		robotState.leftSetpoint = HardwareAdapter.DrivetrainHardware.getInstance().leftMasterTalon.getSetpoint();
		robotState.rightStepoint = HardwareAdapter.DrivetrainHardware.getInstance().rightMasterTalon.getSetpoint();
		robotState.drivePose.heading = HardwareAdapter.DrivetrainHardware.getInstance().gyro.getAngle();
		robotState.drivePose.headingVelocity = HardwareAdapter.DrivetrainHardware.getInstance().gyro.getRate();
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
		robotState.drivePose.leftError = Optional.of(leftMasterTalon.getError());
		robotState.drivePose.rightError = Optional.of(rightMasterTalon.getError());
		if (Constants.kRobotName == Constants.RobotName.AEGIR || Constants.kRobotName == Constants.RobotName.STEIK) {
			robotState.sliderEncoder = HardwareAdapter.SliderHardware.getInstance().sliderMotor.getPosition();
			robotState.sliderPotentiometer = HardwareAdapter.SliderHardware.getInstance().sliderPotentiometer.get();
		}

		// Update kPDP current draw
//		if (Constants.kRobotName == Constants.RobotName.STEIK) {
//			robotState.climberCurrentDraw = HardwareAdapter.getInstance().kPDP.getCurrent(Constants.kSteikClimberMotorPDP);
//		} else if (Constants.kRobotName == Constants.RobotName.AEGIR) {
//			robotState.climberCurrentDraw = HardwareAdapter.getInstance().kPDP.getCurrent(Constants.kAegirClimberMotorPDP);
//		}

		robotState.climberEncoder = HardwareAdapter.ClimberHardware.getInstance().climberMotor.getPosition();
	}

	/**
	 * Sets the output from all subsystems for the respective hardware
	 */
	void updateSubsystems() {
		// On Derica or Tyr only update the drivetrain
		if (Constants.kRobotName == Constants.RobotName.STEIK || Constants.kRobotName == Constants.RobotName.AEGIR) {
			updateSteikSubsystems();
		}
		//updateDrivetrain();
	}

	private void updateSteikSubsystems() {
//		// FLIPPERS
//		HardwareAdapter.getInstance().getFlippers().leftSolenoid.set(mFlippers.getFlipperSignal().leftFlipper);
//		HardwareAdapter.getInstance().getFlippers().rightSolenoid.set(mFlippers.getFlipperSignal().rightFlipper);
//		// SLIDER
//		updateCANTalonSRX(HardwareAdapter.getInstance().getSimpleSlider().sliderMotor, mSlider.getOutput(), 1);
		// SPATULA
		HardwareAdapter.getInstance().getSpatula().spatulaSolenoid.set(mSpatula.getOutput());
//		// INTAKE
//		HardwareAdapter.getInstance().getIntake().leftIntakeMotor.set(mIntake.getOutput());
//		HardwareAdapter.getInstance().getIntake().rightIntakeMotor.set(-mIntake.getOutput());
		// CLIMBER
		updateCANTalonSRX(HardwareAdapter.getInstance().getClimber().climberMotor, mClimber.getOutput(), 1);
	}

	/**
	 * Updates the drivetrain on Derica, Steik, Aegir Uses CANTalonOutput and
	 * can run off-board control loops through SRX
	 */
	private void updateDrivetrain() {
		double leftScalar = 1;
		double rightScalar = 1;
		
		//NOTE: If these are changed, change corresponding scalar in CANTalonRoutine. Otherwise it will not stop.
		if (mDrive.getDriveSignal().leftMotor.getControlMode() == CANTalon.TalonControlMode.Position) {
			leftScalar = (Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
					: Constants.kDriveInchesToTicks;
		}
		if (mDrive.getDriveSignal().rightMotor.getControlMode() == CANTalon.TalonControlMode.Position) {
			rightScalar = (Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks
					: Constants.kDriveInchesToTicks;
		}
		
		System.out.println("left mode: " + HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon.getControlMode());
		System.out.println("left error: "+ HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon.getError());
		
		System.out.println("right mode: " + HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon.getControlMode());
		System.out.println("right error: " + HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon.getError());
		
		updateCANTalonSRX(HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon, mDrive.getDriveSignal().leftMotor, leftScalar);
		updateCANTalonSRX(HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon, mDrive.getDriveSignal().rightMotor, rightScalar);

	}

	/**
	 * Helper method for processing a CANTalonOutput for an SRX
	 * 
	 * @param scalar
	 *            For converting to native units if needed
	 */
	private void updateCANTalonSRX(CANTalon talon, CANTalonOutput output, double scalar) {
		if (talon.getControlMode() != output.getControlMode()) {
			talon.changeControlMode(output.getControlMode());
			if(output.getControlMode().isPID()) {
				talon.setPID(output.gains.P, output.gains.I, output.gains.D, output.gains.F, output.gains.izone, output.gains.rampRate, output.profile);
			}
			if (output.getControlMode() == CANTalon.TalonControlMode.MotionMagic) {
				talon.setMotionMagicAcceleration(output.accel);
				talon.setMotionMagicCruiseVelocity(output.cruiseVel);
			}
		}
		talon.set(output.getSetpoint() * scalar);
	}
}