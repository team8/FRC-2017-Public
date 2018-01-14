package com.palyrobotics.frc2018.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.palyrobotics.frc2018.config.Constants;
import com.palyrobotics.frc2018.config.RobotState;
import com.palyrobotics.frc2018.subsystems.Drive;
import com.palyrobotics.frc2018.util.TalonSRXOutput;
import com.palyrobotics.frc2018.util.logger.Logger;

import java.util.Optional;
import java.util.logging.Level;

/**
 * Should only be used in robot package.
 */
class HardwareUpdater {

	// Subsystem references

	private Drive mDrive;

	/**
	 * Hardware Updater for 2018_Unnamed
	 */
	HardwareUpdater(Drive drive) throws Exception {

		this.mDrive = drive;
	}

	/**
	 * Initialize all hardware
	 */
	void initHardware() {
		Logger.getInstance().logRobotThread(Level.INFO,"Init hardware");
		configureTalons(true);
		PigeonIMU gyro = HardwareAdapter.DrivetrainHardware.getInstance().gyro;
		gyro.setYaw(0, 0);
		gyro.setFusedHeading(0, 0);
	}

	void disableTalons() {
		Logger.getInstance().logRobotThread(Level.INFO,"Disabling talons");
		HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon.set(ControlMode.Disabled, 0);
		HardwareAdapter.getInstance().getDrivetrain().leftSlave1Talon.set(ControlMode.Disabled, 0);
		HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon.set(ControlMode.Disabled, 0);
		HardwareAdapter.getInstance().getDrivetrain().rightSlave1Talon.set(ControlMode.Disabled, 0);
		HardwareAdapter.getInstance().getDrivetrain().leftSlave2Talon.set(ControlMode.Disabled, 0);
		HardwareAdapter.getInstance().getDrivetrain().rightSlave2Talon.set(ControlMode.Disabled, 0);
	}

	void configureTalons(boolean calibrateSliderEncoder) {
		configureDriveTalons();
	}

	void configureDriveTalons() {
		WPI_TalonSRX leftMasterTalon = HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon;
		WPI_TalonSRX leftSlave1Talon = HardwareAdapter.getInstance().getDrivetrain().leftSlave1Talon;
		WPI_TalonSRX leftSlave2Talon = HardwareAdapter.getInstance().getDrivetrain().leftSlave2Talon;
		WPI_TalonSRX rightMasterTalon = HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon;
		WPI_TalonSRX rightSlave1Talon = HardwareAdapter.getInstance().getDrivetrain().rightSlave1Talon;
		WPI_TalonSRX rightSlave2Talon = HardwareAdapter.getInstance().getDrivetrain().rightSlave2Talon;
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

		WPI_TalonSRX leftMasterTalon = HardwareAdapter.DrivetrainHardware.getInstance().leftMasterTalon;
		WPI_TalonSRX rightMasterTalon = HardwareAdapter.DrivetrainHardware.getInstance().rightMasterTalon;

		robotState.leftControlMode = leftMasterTalon.getControlMode();
		robotState.rightControlMode = rightMasterTalon.getControlMode();

		//TODO: sketchy, not exactly setpoint for all modes, do we even need this?
		robotState.leftSetpoint = leftMasterTalon.getMotorOutputPercent();
		robotState.rightSetpoint = rightMasterTalon.getMotorOutputPercent();

		PigeonIMU gyro = HardwareAdapter.DrivetrainHardware.getInstance().gyro;
		if (gyro != null) {
			robotState.drivePose.heading = gyro.getFusedHeading();
			robotState.drivePose.headingVelocity = (robotState.drivePose.heading - robotState.drivePose.lastHeading)/Constants.kNormalLoopsDt;
			robotState.drivePose.lastHeading = gyro.getFusedHeading();
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

	}

	/**
	 * Updates the hardware to run with output values of subsystems
	 */
	void updateHardware() {
		update2018_UnnamedSubsystems();
		updateDrivetrain();
	}

	private void update2018_UnnamedSubsystems() {
	}

	/**
	 * Updates the drivetrain
	 * Uses TalonSRXOutput and can run off-board control loops through SRX
	 */
	private void updateDrivetrain() {
		updateTalonSRX(HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon, mDrive.getDriveSignal().leftMotor);
		updateTalonSRX(HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon, mDrive.getDriveSignal().rightMotor);
	}

	/**
	 * Helper method for processing a TalonSRXOutput for an SRX
	 */
	private void updateTalonSRX(WPI_TalonSRX talon, TalonSRXOutput output) {
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