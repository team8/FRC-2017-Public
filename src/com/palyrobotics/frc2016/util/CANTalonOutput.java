package com.palyrobotics.frc2016.util;

import com.ctre.CANTalon;

/**
 * Created by Nihar on 1/14/17.
 * Mocks the output of a CANTalon's configuration
 * Allows passthrough of -1 to 1 signal
 * Allows configuration for offboard SRX calculations
 */
public class CANTalonOutput {
	// PercentVBus, Speed,
	private CANTalon.TalonControlMode controlMode;
	// If set to Follower TalonControlMode
	private int masterDeviceID;
	private double setpoint;
	private float rampRate;
	private double P,I,D;

	/**
	 * Constructs the mock output with appropriate configuration
	 */
	public CANTalonOutput() {

	}

	private void setPID(double p, double i, double d, float rampRate) {
		this.P = p;
		this.I = i;
		this.D = d;
		this.rampRate = rampRate;
	}

	/**
	 * Sets Talon to TalonControlMode.Speed, velocity target control loop
	 * @param speed, target velocity (from -1023019 to 10230?)
	 * @param p,i,d,rampRate parameters for control loop
	 */
	public void setSpeed(double speed, double p, double i, double d, float rampRate) {
		controlMode = CANTalon.TalonControlMode.Speed;
		setpoint = speed;
		setPID(p, i, d, rampRate);
	}

	/**
	 * Sets Talon to standard -1 to 1 voltage control
	 */
	public void setPercentVBus(double power) {
		controlMode = CANTalon.TalonControlMode.PercentVbus;
		setpoint = power;
	}

	public void setVoltage(double voltage, double p, double i, double d, float rampRate) {
		controlMode = CANTalon.TalonControlMode.Voltage;
		setpoint = voltage;
		setPID(p, i, d, rampRate);
	}

	public void setCurrent(double current, double p, double i, double d, float rampRate) {
		controlMode = CANTalon.TalonControlMode.Current;
		setpoint = current;
		setPID(p, i, d, rampRate);
	}

	public void setSlave(int masterDeviceID) {
		controlMode = CANTalon.TalonControlMode.Follower;
		this.masterDeviceID = masterDeviceID;
	}
}
