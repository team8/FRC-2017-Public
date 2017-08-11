package com.palyrobotics.frc2017.util.archive;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Joystick;

/**
 * Do not run, is only reference code for usage of offboard CANTalon processing
 */
public class CANTalonTester {
	public static final int DERICA_LEFT_A = 3;
	public static final int DERICA_LEFT_B = 2;
	public static final int DERICA_RIGHT_A = 1;
	public static final int DERICA_RIGHT_B = 4;
	public static final int DRIVE_STICK = 0;
	public static final int TURN_STICK = 1;
	//public static final double DISTANCE_PER_REV = 20.32;
	public static final double INCHES_TO_TICKS = 1400 / (2 * 3.1415 * 3.5);
	public static final double INCHES_TO_DEGREES = 42 / 180.0;
	
	boolean verbose = false;
	
	public double PEAK_VOLTAGE = 8.0f;
	
	
	private enum State {
		FORWARD_DRIVE,
		HUMAN_DRIVE,
		TURN_ANGLE,
		VELOCITY_TARGET,
		STOP
	}
	
	State state;
	Joystick drive_stick;
	Joystick turn_stick;
	CANTalon left_master;
	CANTalon left_slave;
	CANTalon right_master;
	CANTalon right_slave;
	
	public CANTalonTester() {
		drive_stick = new Joystick(DRIVE_STICK);
		turn_stick = new Joystick(TURN_STICK);
		left_master = new CANTalon(DERICA_LEFT_A);
		left_slave = new CANTalon(DERICA_LEFT_B);
		right_master = new CANTalon(DERICA_RIGHT_A);
		right_slave = new CANTalon(DERICA_RIGHT_B);
	}
	
	public void init() {
		System.out.println("CANTalonTester Init");
		//Sets the slave controllers to follow the masters
		left_slave.changeControlMode(CANTalon.TalonControlMode.Follower);
		left_slave.set(left_master.getDeviceID());
		right_slave.changeControlMode(CANTalon.TalonControlMode.Follower);
		right_slave.set(right_master.getDeviceID());

		
		//Sets the masters to use the encoders that are directly plugged into them
		left_master.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		right_master.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		left_master.reverseSensor(true);
		right_master.reverseOutput(true);
		
		
		//Zeroes encoders
		left_master.setEncPosition(0);
		right_master.setEncPosition(0);
		
		left_master.configPeakOutputVoltage(PEAK_VOLTAGE, -PEAK_VOLTAGE);
		right_master.configPeakOutputVoltage(PEAK_VOLTAGE, -PEAK_VOLTAGE);
		
		
		state = State.VELOCITY_TARGET;
		
		switch(state) {
		case HUMAN_DRIVE:
			left_master.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
			right_master.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
//			left_master.setVoltageRampRate(12.3);
//			right_master.setVoltageRampRate(12.3);
			
			left_master.enableForwardSoftLimit(false);
			left_master.enableReverseSoftLimit(false);
			right_master.enableForwardSoftLimit(false);
			right_master.enableReverseSoftLimit(false);

//			left_master.setForwardSoftLimit(15 * kDriveInchesToTicks);
//			left_master.setReverseSoftLimit(-15 * kDriveInchesToTicks);
//			right_master.setForwardSoftLimit(15 * kDriveInchesToTicks);
//			right_master.setReverseSoftLimit(-15 * kDriveInchesToTicks);
//			left_master.enableForwardSoftLimit(true);
//			left_master.enableReverseSoftLimit(true);
//			right_master.enableReverseSoftLimit(true);
//			right_master.enableForwardSoftLimit(true);
			
			break;
		case VELOCITY_TARGET:
			right_master.setPID(3.0, 0, 50.0, 2.122, 0, 0, 0);
			left_master.setPID(3.0, 0, 50.0, 2.122, 0, 0, 0);
			left_master.changeControlMode(CANTalon.TalonControlMode.Speed);
			right_master.changeControlMode(CANTalon.TalonControlMode.Speed);
			
			right_master.setSetpoint(12 / 10.0f * INCHES_TO_TICKS);
			left_master.setSetpoint(12 / 10.0f * INCHES_TO_TICKS);
			break;
		case FORWARD_DRIVE:
			right_master.setPID(0.4, 0, 4, 0, 0, 0, 0);
			left_master.setPID(0.4, 0, 4, 0, 0, 0, 0);
			left_master.changeControlMode(CANTalon.TalonControlMode.Position);
			right_master.changeControlMode(CANTalon.TalonControlMode.Position);
			right_master.setSetpoint(-72 * INCHES_TO_TICKS);
			left_master.setSetpoint(-72 * INCHES_TO_TICKS);
			break;
		case TURN_ANGLE:
			right_master.setPID(1.6, 0, 0, 0, 0, 0, 0);
			left_master.setPID(1.6, 0, 0, 0, 0, 0, 0);
			left_master.changeControlMode(CANTalon.TalonControlMode.Position);
			right_master.changeControlMode(CANTalon.TalonControlMode.Position);
			right_master.setSetpoint(40 * INCHES_TO_TICKS);
			left_master.setSetpoint(-40 * INCHES_TO_TICKS);
			break;
		default:
			System.out.println("No open-loop command");
			break;
		}
	}
	
	public void update() {
		System.out.println("CANTalonTester Update");
		//System.out.println(state);
		//System.out.println("Left inches: "+ left_master.getPosition() / kDriveInchesToTicks);
		//System.out.println("Right inches: "+ right_master.getPosition() / kDriveInchesToTicks);
		System.out.println("Target: " + 5 * INCHES_TO_TICKS);
		System.out.println("Left speed: " + left_master.getSpeed());
		System.out.println("Right speed: " + right_master.getSpeed());
		System.out.println("Left error: " + left_master.getClosedLoopError());
		System.out.println("Right error: " + right_master.getClosedLoopError());
		//System.out.println("Left outputVoltageDrop: " + left_master.getOutputVoltage());
		//System.out.println("Right outputVoltageDrop: " + right_master.getOutputVoltage());

		//System.out.println("Left percentVBus: " + left_master.getOutputVoltage() / left_master.getBusVoltage());
		//System.out.println("Right percentVBus: " + right_master.getOutputVoltage() / right_master.getBusVoltage());	
		
		if(verbose) {
			System.out.println("Left currentAmps: " + left_master.getOutputCurrent());
			System.out.println("Left outputVoltageDrop: " + left_master.getOutputVoltage());
			System.out.println("Left busVoltageDrop: " + left_master.getBusVoltage());
			System.out.println("Left outputPercent: " + left_master.getOutputVoltage() / left_master.getBusVoltage());
		}
		
		switch(state) {
		case HUMAN_DRIVE:
			double forward = drive_stick.getY() * -1;
			double turn = turn_stick.getX();
			
			double left = forward + turn;
			double right = forward - turn;
			
			right *= -1;
			
			System.out.println("Left: " + left);
			System.out.println("Right: " + right);
			
			left_master.set(left);
			right_master.set(right);
			break;
		case STOP:
			left_master.set(0);
			right_master.set(0);
		default:
			System.out.println("No State");
		}
	}
}
