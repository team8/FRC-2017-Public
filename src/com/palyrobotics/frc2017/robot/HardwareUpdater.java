package com.palyrobotics.frc2017.robot;

import com.ctre.CANTalon;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.team254.lib.util.ADXRS453_Gyro;
import com.palyrobotics.frc2017.config.Constants.RobotName;
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
		if (HardwareAdapter.getInstance().getDrivetrain().gyro != null) {
			HardwareAdapter.getInstance().getDrivetrain().gyro.calibrate();
		}

		CANTalon leftMasterTalon = HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon;
		CANTalon leftSlave1Talon = HardwareAdapter.getInstance().getDrivetrain().leftSlave1Talon;
		CANTalon otherLeftSlaveTalon = HardwareAdapter.getInstance().getDrivetrain().leftSlave2Talon;
		CANTalon rightMasterTalon = HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon;
		CANTalon rightSlaveTalon = HardwareAdapter.getInstance().getDrivetrain().rightSlaveTalon;
		CANTalon otherRightSlaveTalon = HardwareAdapter.getInstance().getDrivetrain().secondRightSlaveTalon;
		
		// Enable all talons' brake mode and disables forward and reverse soft
		// limits
		leftMasterTalon.enableBrakeMode(true);
		leftSlave1Talon.enableBrakeMode(true);
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
		leftSlave1Talon.enable();
		otherLeftSlaveTalon.enable();
		rightMasterTalon.enable();
		rightSlaveTalon.enable();
		otherRightSlaveTalon.enable();
		
		leftMasterTalon.configPeakOutputVoltage(12, -12);
		rightMasterTalon.configPeakOutputVoltage(12, -12);
		leftSlave1Talon.configPeakOutputVoltage(12, -12);
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
		leftSlave1Talon.changeControlMode(CANTalon.TalonControlMode.Follower);
		leftSlave1Talon.set(leftMasterTalon.getDeviceID());
		otherLeftSlaveTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
		otherLeftSlaveTalon.set(leftMasterTalon.getDeviceID());
		rightSlaveTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
		rightSlaveTalon.set(rightMasterTalon.getDeviceID());
		otherRightSlaveTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
		otherRightSlaveTalon.set(rightMasterTalon.getDeviceID());
	
		if (Constants.kRobotName == RobotName.AEGIR || Constants.kRobotName == RobotName.STEIK) {
			//Climber setup
			CANTalon climber = HardwareAdapter.ClimberHardware.getInstance().climberTalon;
			climber.reset();
			climber.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Absolute);
			climber.setPosition(0);	
			climber.ConfigRevLimitSwitchNormallyOpen(false); // Prevent the motor from driving backwards
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

			// Set up the Talon to read from a relative CTRE mag encoder sensor
			slider.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Absolute);
			// Calibrate the encoder
			double current_pot_pos = HardwareAdapter.SliderHardware.getInstance().sliderPotentiometer.getValue();
			double distance_to_center = current_pot_pos - Constants.kPotentiometerCenterPos;
			double position_in_rev = (distance_to_center / 4096.0) * 10.0;
			slider.setPosition(-position_in_rev);
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
		} else {
			robotState.drivePose.heading = 0;
			robotState.drivePose.headingVelocity = 0;
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
		if (Constants.kRobotName == Constants.RobotName.AEGIR || Constants.kRobotName == Constants.RobotName.STEIK) {
			CANTalon sliderTalon = HardwareAdapter.SliderHardware.getInstance().sliderTalon;
			robotState.sliderEncoder = sliderTalon.getPosition();
			robotState.sliderPotentiometer = HardwareAdapter.SliderHardware.getInstance().sliderPotentiometer.getValue();
			robotState.sliderVelocity = sliderTalon.getSpeed();
			if (sliderTalon.getControlMode().isPID()) {
				robotState.sliderClosedLoopError = Optional.of(sliderTalon.getClosedLoopError());
			} else {
				robotState.sliderClosedLoopError = Optional.empty();
			}
		}
//		if (Constants.kRobotName == Constants.RobotName.STEIK) {
//			robotState.climberCurrentDraw = HardwareAdapter.getInstance().kPDP.getCurrent(Constants.kSteikClimberMotorPDP);
//		} else if (Constants.kRobotName == Constants.RobotName.AEGIR) {
//			robotState.climberCurrentDraw = HardwareAdapter.getInstance().kPDP.getCurrent(Constants.kAegirClimberMotorPDP);
//		}
		robotState.climberEncoder = HardwareAdapter.ClimberHardware.getInstance().climberTalon.getPosition();
	}

	/**
	 * Sets the output from all subsystems for the respective hardware
	 */
	void updateSubsystems() {
		// On Derica or Tyr only update the drivetrain
		if (Constants.kRobotName == Constants.RobotName.STEIK || Constants.kRobotName == Constants.RobotName.AEGIR) {
			updateSteikSubsystems();
		}
		updateDrivetrain();
	}

	private void updateSteikSubsystems() {
//		// FLIPPERS
//		HardwareAdapter.getInstance().getFlippers().leftSolenoid.set(mFlippers.getFlipperSignal().leftFlipper);
//		HardwareAdapter.getInstance().getFlippers().rightSolenoid.set(mFlippers.getFlipperSignal().rightFlipper);
//		// SLIDER
		updateCANTalonSRX(HardwareAdapter.getInstance().getSlider().sliderTalon, mSlider.getOutput(), 1);
		// SPATULA
		HardwareAdapter.getInstance().getSpatula().spatulaSolenoid.set(mSpatula.getOutput());
//		// INTAKE
//		HardwareAdapter.getInstance().getIntake().leftIntakeMotor.set(mIntake.getOutput());
//		HardwareAdapter.getInstance().getIntake().rightIntakeMotor.set(-mIntake.getOutput());
		// CLIMBER
		updateCANTalonSRX(HardwareAdapter.getInstance().getClimber().climberTalon, mClimber.getOutput(), 1);
		//System.out.println(mClimber.getOutput());
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