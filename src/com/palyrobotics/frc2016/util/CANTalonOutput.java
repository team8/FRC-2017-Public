package com.palyrobotics.frc2016.util;

import com.ctre.CANTalon;

/**
 * Created by Nihar on 1/14/17.
 * Mocks the output of a CANTalon's configuration
 * Allows passthrough of -1 to 1 mSignal
 * Allows configuration for offboard SRX calculations
 */
public class CANTalonOutput {
	// PercentVBus, Speed, Current, Voltage, not Follower, MotionProfile, MotionMagic
	private CANTalon.TalonControlMode controlMode;
	private double setpoint;
	public double P,I,D, f, rampRate;
	public int izone;
	public int profile = 0;

	public CANTalon.TalonControlMode getControlMode() {
		return controlMode;
	}

	public double getSetpoint() {
		return setpoint;
	}

	private void setPID(double p, double i, double d, double f, int izone, double rampRate) {
		this.P = p;
		this.I = i;
		this.D = d;
		this.f = f;
		this.izone = izone;
		this.rampRate = rampRate;
	}

	/**
	 * Sets Talon to TalonControlMode.Speed, velocity target control loop
	 * @param speed, target velocity (from -1023019 to 10230?)
	 * @param p,i,d, f, izone, rampRate parameters for control loop
	 */
	public void setSpeed(double speed, double p, double i, double d, double f, int izone, double rampRate) {
		controlMode = CANTalon.TalonControlMode.Speed;
		setpoint = speed;
		setPID(p, i, d, f, izone, rampRate);
	}

	/**
	 * Sets Talon to TalonControlMode.Position
	 */
	public void setPosition(double setpoint, double p, double i, double d, double f, int izone, double rampRate) {
		controlMode = CANTalon.TalonControlMode.Position;
		this.setpoint = setpoint;
		setPID(p, i, d, f, izone, rampRate);
	}
	/**
	 * Sets Talon to standard -1 to 1 voltage control
	 */
	public void setPercentVBus(double power) {
		controlMode = CANTalon.TalonControlMode.PercentVbus;
		setpoint = power;
	}

	public void setVoltage(double voltage, double p, double i, double d, double f, int izone, double rampRate) {
		controlMode = CANTalon.TalonControlMode.Voltage;
		setpoint = voltage;
		setPID(p, i, d, f, izone, rampRate);
	}

	public void setCurrent(double current, double p, double i, double d, double f, int izone, double rampRate) {
		controlMode = CANTalon.TalonControlMode.Current;
		setpoint = current;
		setPID(p, i, d, f, izone, rampRate);
	}

	/* Should not be used as talon's should be set to slave mode when initialized
	public void setSlave(int masterDeviceID) {
		controlMode = CANTalon.TalonControlMode.Follower;
		this.masterDeviceID = masterDeviceID;
	} */
}