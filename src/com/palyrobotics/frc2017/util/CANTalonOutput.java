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

	// Used for motion magic
	public double accel;
	public double cruiseVel;

	/**
	 * Default constructor
	 */
	public CANTalonOutput() {
		controlMode = CANTalon.TalonControlMode.Disabled;
		setpoint = 0;
		profile = 0;
		setPID(0,0,0,0,0,0);
		
		accel = 0;
		cruiseVel = 0;
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
		
		this.accel = talon.accel;
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
	 * @param setpoint for drivesignal, use inches, which get converted in HardwareUpdater, otherwise native units
	 * @param p,i,d, f, izone, rampRate parameters for control loop
	 */
	public void setPosition(double setpoint, double p, double i, double d, double f, int izone, double rampRate) {
		controlMode = CANTalon.TalonControlMode.Position;
		setPID(p, i, d, f, izone, rampRate);
		this.setpoint = setpoint;
	}

	public void setPosition(CANTalonOutputFactory canTalon) {
		controlMode = CANTalon.TalonControlMode.Position;
		setPID(canTalon.P, canTalon.I, canTalon.D, canTalon.F, canTalon.izone, canTalon.rampRate);
		this.setpoint = canTalon.distance;
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
	 * Uses the CANTalon 1D motion profile generator
	 * @param setpoint target position in native units
	 * @param accel max acceleration and deceleration
	 * @param cruiseVelocity cruise velocity to max out at
	 */
	public void setMotionMagic(double setpoint, double accel, double cruiseVelocity, double p, double i, double d, double f, int izone, double rampRate) {
		controlMode = CANTalon.TalonControlMode.MotionMagic;
		this.setpoint = setpoint;
		this.accel = accel;
		this.cruiseVel = cruiseVelocity;
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

	/**
	 * Class used to get a set point with all the CANTalon constants.  This is better
	 * than calling the setDistance(...) everytime because the constants only need to be added once.
	 *
	 * @author Robbie Selwyn
	 *
	 */
	public static class CANTalonOutputFactory {

		public double P,I,D, F, rampRate;
		public int izone;
		public int profile;

		public double distance;

		public CANTalonOutputFactory(double p, double i, double d, double f, int izone, double rampRate) {
			this.P = p;
			this.I = i;
			this.D = d;
			this.F = f;
			this.izone = izone;
			this.rampRate = rampRate;
			this.profile = 0;
			this.distance = 0;
		}

		public CANTalonOutputFactory() {
			switch (Constants.kRobotName) {
			case DERICA:
				new CANTalonOutput.CANTalonOutputFactory(Constants2016.kDericaPositionkP, 
						Constants2016.kDericaPositionkI, 
						Constants2016.kDericaPositionkD,
						Constants2016.kDericaPositionkF, 
						Constants2016.kDericaPositionkIzone,
						Constants2016.kDericaPositionRampRate);
			case AEGIR:
				new CANTalonOutput.CANTalonOutputFactory(Constants.kAegirDriveDistancekP, 
						Constants.kAegirDriveDistancekI, 
						Constants.kAegirDriveDistancekD,
						Constants.kAegirDriveDistancekF, 
						Constants.kAegirDriveDistancekIzone,
						Constants.kAegirDriveDistancekRampRate);
			case STEIK:
				new CANTalonOutput.CANTalonOutputFactory(Constants.kSteikDriveDistancekP, 
						Constants.kSteikDriveDistancekI, 
						Constants.kSteikDriveDistancekD,
						Constants.kSteikDriveDistancekF, 
						Constants.kSteikDriveDistancekIzone,
						Constants.kSteikDriveDistancekRampRate);
			}
		}

		public CANTalonOutputFactory withDistance(double distance) {
			this.distance = distance;
			return this;
		}

	}
}