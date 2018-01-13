package com.palyrobotics.frc2017.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants.RobotName;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.subsystems.*;
import com.palyrobotics.frc2017.util.TalonSRXOutput;
import com.palyrobotics.frc2017.util.logger.Logger;

import java.util.Optional;
import java.util.logging.Level;

/**
 * Should only be used in robot package.
 */
class HardwareUpdater {

	// Subsystem references
	
	private Drive mDrive;
	private Slider mSlider;
	private Spatula mSpatula;
	private Intake mIntake;
	private Climber mClimber;

	/**
	 * Hardware Updater for Steik
	 */
	HardwareUpdater(Drive drive, Slider slider, Spatula spatula, Intake intake, Climber climber)
			throws Exception {
		if (Constants.kRobotName != Constants.RobotName.STEIK) {
			System.out.println("Incompatible robot name and hardware!");
			throw new Exception();
		}
		this.mDrive = drive;
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
		AHRS gyro = HardwareAdapter.getInstance().getDrivetrain().gyro;
		if (gyro != null) {
			int i = 0;
			while (!gyro.isConnected()) {
				i++;
				if (i > 1000) {
					System.out.println("waited for gyro to connect, didn't find");
					break;
				}
			}
		}
		gyro.zeroYaw();
	}
	
	void disableTalons() {
		Logger.getInstance().logRobotThread(Level.INFO,"Disabling talons");
		HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon.set(ControlMode.Disabled, 0);
		HardwareAdapter.getInstance().getDrivetrain().leftSlave1Talon.set(ControlMode.Disabled, 0);
		HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon.set(ControlMode.Disabled, 0);
		HardwareAdapter.getInstance().getDrivetrain().rightSlave1Talon.set(ControlMode.Disabled, 0);
		if(Constants.kRobotName == RobotName.STEIK) {
			HardwareAdapter.getInstance().getDrivetrain().leftSlave2Talon.set(ControlMode.Disabled, 0);
			HardwareAdapter.getInstance().getDrivetrain().rightSlave2Talon.set(ControlMode.Disabled, 0);
			HardwareAdapter.getInstance().getClimber().climberTalon.set(ControlMode.Disabled, 0);
			HardwareAdapter.getInstance().getSlider().sliderTalon.set(ControlMode.Disabled, 0);
		}
	}
	
	void configureTalons(boolean calibrateSliderEncoder) {
		configureDriveTalons();
		if (Constants.kRobotName == RobotName.STEIK) {
			//Climber setup
			TalonSRX climber = HardwareAdapter.ClimberHardware.getInstance().climberTalon;
			climber.enableVoltageCompensation(true);
			climber.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
			climber.setSensorPhase(false);
			climber.setSelectedSensorPosition(0, 0, 0);
			climber.configPeakOutputForward(Constants.kClimberMaxVoltage, 0);
			climber.configPeakOutputReverse(-Constants.kClimberMaxVoltage, 0);
			climber.configForwardLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.NormallyOpen, 0);
			climber.configReverseLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.NormallyClosed, 0);

			TalonSRX slider = HardwareAdapter.SliderHardware.getInstance().sliderTalon;
			slider.enableVoltageCompensation(true);
			// Reset and turn on the Talon 
			slider.clearStickyFaults(0);
			slider.setStatusFramePeriod(0, 5, 0);
			slider.configPeakOutputForward(Constants.kSliderPeakOutputPower, 0);
			slider.configPeakOutputReverse(-Constants.kSliderPeakOutputPower, 0);
			if (calibrateSliderEncoder) {
				// Set up the Talon to read from a relative CTRE mag encoder sensor
				slider.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
				slider.setSensorPhase(false);
				// Calibrate the encoder
				double current_pot_pos = HardwareAdapter.SliderHardware.getInstance().sliderPotentiometer.getValue();
				double distance_to_center = current_pot_pos - Constants.kPotentiometerCenterPos;
				int position_in_rev = (int) ((distance_to_center / 4096.0) * 10.0);
				if (Constants.kCalibrateSliderWithPotentiometer) {
					slider.setSelectedSensorPosition(-position_in_rev, 0, 0);
				} else {
					slider.setSelectedSensorPosition(0, 0, 0);
				}
			}
		}
	}
	
	void configureDriveTalons() {
		TalonSRX leftMasterTalon = HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon;
		TalonSRX leftSlave1Talon = HardwareAdapter.getInstance().getDrivetrain().leftSlave1Talon;
		TalonSRX leftSlave2Talon = HardwareAdapter.getInstance().getDrivetrain().leftSlave2Talon;
		TalonSRX rightMasterTalon = HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon;
		TalonSRX rightSlave1Talon = HardwareAdapter.getInstance().getDrivetrain().rightSlave1Talon;
		TalonSRX rightSlave2Talon = HardwareAdapter.getInstance().getDrivetrain().rightSlave2Talon;
		// Enable all talons' brake mode and disables forward and reverse soft
		// limits

		leftMasterTalon.setNeutralMode(NeutralMode.Brake);
		leftSlave1Talon.setNeutralMode(NeutralMode.Brake);
		if (leftSlave2Talon != null) leftSlave2Talon.setNeutralMode(NeutralMode.Brake);
		rightMasterTalon.setNeutralMode(NeutralMode.Brake);
		rightSlave1Talon.setNeutralMode(NeutralMode.Brake);
		if (rightSlave2Talon != null) rightSlave2Talon.setNeutralMode(NeutralMode.Brake);

		leftMasterTalon.enableVoltageCompensation(true);
		leftSlave1Talon.enableVoltageCompensation(true);
		leftSlave2Talon.enableVoltageCompensation(true);

		rightMasterTalon.enableVoltageCompensation(true);
		rightSlave1Talon.enableVoltageCompensation(true);
		rightSlave2Talon.enableVoltageCompensation(true);

		leftMasterTalon.configForwardSoftLimitEnable(false, 0);
		leftMasterTalon.configReverseSoftLimitEnable(false, 0);
		leftSlave1Talon.configForwardSoftLimitEnable(false, 0);
		leftSlave1Talon.configReverseSoftLimitEnable(false, 0);

		if (rightSlave2Talon != null) {rightSlave2Talon.configForwardSoftLimitEnable(false, 0); rightSlave2Talon.configReverseSoftLimitEnable(false, 0);}

		rightMasterTalon.configForwardSoftLimitEnable(false, 0);
		rightMasterTalon.configReverseSoftLimitEnable(false, 0);
		rightSlave1Talon.configForwardSoftLimitEnable(false, 0);
		rightSlave1Talon.configReverseSoftLimitEnable(false, 0);

		if (rightSlave2Talon != null) {rightSlave2Talon.configForwardSoftLimitEnable(false, 0); rightSlave2Talon.configReverseSoftLimitEnable(false, 0);}
		
		// Allow max voltage for closed loop control
		leftMasterTalon.configPeakOutputForward(Constants.kDriveMaxClosedLoopOutput, 0);
		leftMasterTalon.configPeakOutputReverse(-Constants.kDriveMaxClosedLoopOutput, 0);
		leftSlave1Talon.configPeakOutputForward(Constants.kDriveMaxClosedLoopOutput, 0);
		leftSlave1Talon.configPeakOutputReverse(-Constants.kDriveMaxClosedLoopOutput, 0);

		if (leftSlave2Talon != null) {
			leftSlave2Talon.configPeakOutputForward(Constants.kDriveMaxClosedLoopOutput, 0);
			leftSlave2Talon.configPeakOutputReverse(-Constants.kDriveMaxClosedLoopOutput, 0);
		}
		
		rightMasterTalon.configPeakOutputForward(Constants.kDriveMaxClosedLoopOutput, 0);
		rightMasterTalon.configPeakOutputReverse(-Constants.kDriveMaxClosedLoopOutput, 0);
		rightSlave1Talon.configPeakOutputForward(Constants.kDriveMaxClosedLoopOutput, 0);
		rightSlave1Talon.configPeakOutputReverse(-Constants.kDriveMaxClosedLoopOutput, 0);

		if (rightSlave2Talon != null) {
			rightSlave2Talon.configPeakOutputForward(Constants.kDriveMaxClosedLoopOutput, 0);
			rightSlave2Talon.configPeakOutputReverse(-Constants.kDriveMaxClosedLoopOutput, 0);
		}
		
		// Configure master talon feedback devices
		leftMasterTalon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		rightMasterTalon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);

		leftMasterTalon.setSensorPhase(true);
		//TODO:is true or not?
		rightMasterTalon.setSensorPhase(true);

		leftMasterTalon.setStatusFramePeriod(0, 5, 0);
		rightMasterTalon.setStatusFramePeriod(0, 5, 0);

		leftMasterTalon.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_50Ms, 0);
		rightMasterTalon.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_50Ms, 0);

		leftMasterTalon.configVelocityMeasurementWindow(16, 0);
		rightMasterTalon.configVelocityMeasurementWindow(16, 0);

		// Zero encoders
		leftMasterTalon.setSelectedSensorPosition(0, 0, 0);
		rightMasterTalon.setSelectedSensorPosition(0, 0, 0);

		// Reverse right side
		rightMasterTalon.setInverted(true);
		rightSlave1Talon.setInverted(true);
		rightSlave2Talon.setInverted(true);

		// Set slave talons to follower mode
		leftSlave1Talon.set(ControlMode.Follower, leftMasterTalon.getDeviceID());
		if (leftSlave2Talon != null) {
			leftSlave2Talon.set(ControlMode.Follower, leftMasterTalon.getDeviceID());
		}
		rightSlave1Talon.set(ControlMode.Follower, rightMasterTalon.getDeviceID());
		if (rightSlave2Talon != null) {
			rightSlave2Talon.set(ControlMode.Follower, rightMasterTalon.getDeviceID());
		}
	}

	/**
	 * Updates all the sensor data taken from the hardware
	 */
	void updateSensors(RobotState robotState) {

		TalonSRX leftMasterTalon = HardwareAdapter.DrivetrainHardware.getInstance().leftMasterTalon;
		TalonSRX rightMasterTalon = HardwareAdapter.DrivetrainHardware.getInstance().rightMasterTalon;

		robotState.leftControlMode = leftMasterTalon.getControlMode();
		robotState.rightControlMode = rightMasterTalon.getControlMode();

		//TODO: sketchy, not exactly setpoint for all modes, do we even need this?
		robotState.leftSetpoint = leftMasterTalon.getMotorOutputPercent();
		robotState.rightSetpoint = rightMasterTalon.getMotorOutputPercent();

		AHRS gyro = HardwareAdapter.DrivetrainHardware.getInstance().gyro;
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


		//TODO: FIGURE OUT ENC VEL VS SPD
		robotState.drivePose.leftEnc = leftMasterTalon.getSelectedSensorPosition(0);
		robotState.drivePose.leftEncVelocity = leftMasterTalon.getSelectedSensorVelocity(0);
		robotState.drivePose.leftSpeed = leftMasterTalon.getSelectedSensorVelocity(0);
		robotState.drivePose.rightEnc = rightMasterTalon.getSelectedSensorPosition(0);
		robotState.drivePose.rightEncVelocity = rightMasterTalon.getSelectedSensorVelocity(0);
		robotState.drivePose.rightSpeed = rightMasterTalon.getSelectedSensorVelocity(0);
		
		if (leftMasterTalon.getControlMode().equals(ControlMode.MotionMagic)) {
			robotState.drivePose.leftMotionMagicPos = Optional.of(leftMasterTalon.getActiveTrajectoryPosition());
			robotState.drivePose.leftMotionMagicVel = Optional.of(leftMasterTalon.getActiveTrajectoryVelocity());
		}
		else {
			robotState.drivePose.leftMotionMagicPos = Optional.empty();
			robotState.drivePose.leftMotionMagicVel = Optional.empty();
		}
		
		if (rightMasterTalon.getControlMode().equals(ControlMode.MotionMagic)) {
			robotState.drivePose.rightMotionMagicPos = Optional.of(rightMasterTalon.getActiveTrajectoryPosition());
			robotState.drivePose.rightMotionMagicVel = Optional.of(rightMasterTalon.getActiveTrajectoryVelocity());
		}
		else {
			robotState.drivePose.rightMotionMagicPos = Optional.empty();
			robotState.drivePose.rightMotionMagicVel = Optional.empty();
		}
		robotState.drivePose.leftError = Optional.of(leftMasterTalon.getClosedLoopError(0));
        robotState.drivePose.rightError = Optional.of(rightMasterTalon.getClosedLoopError(0));

		if (Constants.kRobotName == Constants.RobotName.STEIK) {
			TalonSRX sliderTalon = HardwareAdapter.SliderHardware.getInstance().sliderTalon;
			robotState.sliderEncoder = sliderTalon.getSelectedSensorPosition(0);
			robotState.sliderPotentiometer = HardwareAdapter.SliderHardware.getInstance().sliderPotentiometer.getValue();
			robotState.sliderVelocity = sliderTalon.getSelectedSensorVelocity(0);
			robotState.sliderClosedLoopError = Optional.of(sliderTalon.getClosedLoopError(0));
		}
		if (HardwareAdapter.getInstance().getClimber().climberTalon != null) {
			robotState.climberEncoder = HardwareAdapter.ClimberHardware.getInstance().climberTalon.getSelectedSensorPosition(0);
		}
		if (HardwareAdapter.getInstance().getSlider().sliderTalon != null) {
			robotState.sliderPosition = HardwareAdapter.SliderHardware.getInstance().sliderTalon.getSelectedSensorPosition(0);
		}
	}

	/**
	 * Updates the hardware to run with output values of subsystems
	 */
	void updateHardware() {
		// On Derica only update the drivetrain
		if (Constants.kRobotName == Constants.RobotName.STEIK) {
			updateSteikSubsystems();
		}
		updateDrivetrain();
	}

	private void updateSteikSubsystems() {
		updateTalonSRX(HardwareAdapter.getInstance().getSlider().sliderTalon, mSlider.getOutput());
		// SPATULA
		HardwareAdapter.getInstance().getSpatula().spatulaSolenoid.set(mSpatula.getOutput());
		// INTAKE
		HardwareAdapter.getInstance().getIntake().intakeMotor.set(mIntake.getOutput());
		// CLIMBER
		updateTalonSRX(HardwareAdapter.getInstance().getClimber().climberTalon, mClimber.getOutput());
	}

	/**
	 * Updates the drivetrain on Derica, Steik 
	 * Uses TalonSRXOutput and can run off-board control loops through SRX
	 */
	private void updateDrivetrain() {
		updateTalonSRX(HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon, mDrive.getDriveSignal().leftMotor);
		updateTalonSRX(HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon, mDrive.getDriveSignal().rightMotor);
	}

	/**
	 * Helper method for processing a TalonSRXOutput for an SRX
	 */
	private void updateTalonSRX(TalonSRX talon, TalonSRXOutput output) {
		if(output.getControlMode().equals(ControlMode.Position) || output.getControlMode().equals(ControlMode.Velocity) || output.getControlMode().equals(ControlMode.MotionMagic)) {
			talon.config_kP(output.profile, output.gains.P, 0);
			talon.config_kI(output.profile, output.gains.I, 0);
			talon.config_kD(output.profile, output.gains.D, 0);
			talon.config_kF(output.profile, output.gains.F, 0);
			talon.config_IntegralZone(output.profile, output.gains.izone, 0);
			talon.configClosedloopRamp(output.gains.rampRate, 0);
		}
		if (output.getControlMode().equals(ControlMode.MotionMagic)) {
		    talon.configMotionAcceleration(output.accel, 0);
		    talon.configMotionCruiseVelocity(output.cruiseVel, 0);
		}
		if (output.getControlMode().equals(ControlMode.Velocity)) {
		    talon.configAllowableClosedloopError(output.profile, 0, 0);
		}
		talon.set(output.getControlMode(), output.getSetpoint());
	}
}