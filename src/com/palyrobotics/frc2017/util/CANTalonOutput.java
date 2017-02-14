package com.palyrobotics.frc2017.util;

import com.ctre.CANTalon;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;

/**
 * Created by Nihar on 1/14/17.
 * Mocks the output of a CANTalon's configuration
 * Allows passthrough of -1 to 1 mSignal
 * Allows configuration for offboard SRX calculations
 */
public class CANTalonOutput {
	
	/**
	 * Prevent null pointer exceptions
	 */
	private CANTalon.TalonControlMode controlMode;
	// PercentVBus, Speed, Current, Voltage, not Follower, MotionProfile, MotionMagic
	private double setpoint;	// Encoder ticks
	public double P,I,D, F, rampRate;
	public int izone;
	public int profile;
	
	/**
	 * Default constructor
	 */
	public CANTalonOutput() {
		controlMode = CANTalon.TalonControlMode.Disabled;
		setpoint = 0;
		profile = 0;
	}
	
	/**
	 * Copy constructor
	 * @param talon output to copy
	 */
	public CANTalonOutput(CANTalonOutput talon) {
		this.controlMode = talon.getControlMode();
		this.setpoint = talon.getSetpoint();
		this.P = talon.P;
		this.I = talon.I;
		this.D = talon.D;
		this.F = talon.F;
		this.izone = talon.izone;
		this.rampRate = talon.rampRate;
		this.profile = talon.profile;
	}
	
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
		this.F = f;
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
	 * @param setpoint in inches
	 * @param p,i,d, f, izone, rampRate parameters for control loop
	 */
	public void setPosition(double setpoint, double p, double i, double d, double f, int izone, double rampRate) {
		controlMode = CANTalon.TalonControlMode.Position;
		// Convert to encoder ticks
		this.setpoint = (Constants.kRobotName == Constants.RobotName.DERICA) ? 
				setpoint * Constants2016.kDericaInchesToTicks : Constants.kInchesToTicks;
		setPID(p, i, d, f, izone, rampRate);
	}
	/**
	 * Sets Talon to standard -1 to 1 voltage control
	 * @param power
	 */
	public void setPercentVBus(double power) {
		controlMode = CANTalon.TalonControlMode.PercentVbus;
		setpoint = power;
	}

	/**
	 * Sets Talon to TalonControlMode.Voltage
	 * @param voltage in volts
	 * @param p,i,d, f, izone, rampRate parameters for control loop
	 */
	public void setVoltage(double voltage, double p, double i, double d, double f, int izone, double rampRate) {
		controlMode = CANTalon.TalonControlMode.Voltage;
		setpoint = voltage;
		setPID(p, i, d, f, izone, rampRate);
	}

	/**
	 * Sets Talon to TalonControlMode.Current
	 * @param current in amps
	 * @param p,i,d, f, izone, rampRate parameters for control loop
	 */
	public void setCurrent(double current, double p, double i, double d, double f, int izone, double rampRate) {
		controlMode = CANTalon.TalonControlMode.Current;
		setpoint = current;
		setPID(p, i, d, f, izone, rampRate);
	}
	
	/**
	 * Sets Talon to TalonControlMode.Disabled
	 */
	public void setDisabled() {
		this.controlMode = CANTalon.TalonControlMode.Disabled;
	}
	
	public String toString() {
		String name = "";
		if (controlMode == null) {
			name += "null";
		} else {
			name += controlMode.toString();
		}
		name+= " "+getSetpoint();
		return name;
	}

	/**
	 * Used for unit tests to compare drive signal values
	 */
	@Override
	public boolean equals(Object other) {
		return ((CANTalonOutput) other).getSetpoint() == this.getSetpoint() && 
				((CANTalonOutput) other).controlMode == this.controlMode &&
				((CANTalonOutput) other).P == this.P && 
				((CANTalonOutput) other).I == this.I && 
				((CANTalonOutput) other).D == this.D && 
				((CANTalonOutput) other).F == this.F && 
				((CANTalonOutput) other).izone == this.izone && 
				((CANTalonOutput) other).rampRate == this.rampRate;
	}

	/* Should not be used as talon's should be set to slave mode when initialized
	public void setSlave(int masterDeviceID) {
		controlMode = CANTalon.TalonControlMode.Follower;
		this.masterDeviceID = masterDeviceID;
	} */
}