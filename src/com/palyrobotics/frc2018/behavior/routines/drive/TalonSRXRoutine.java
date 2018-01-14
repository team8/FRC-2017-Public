package com.palyrobotics.frc2018.behavior.routines.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.palyrobotics.frc2018.behavior.Routine;
import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.config.RobotState;
import com.palyrobotics.frc2018.robot.Robot;
import com.palyrobotics.frc2018.subsystems.Drive;
import com.palyrobotics.frc2018.subsystems.Subsystem;
import com.palyrobotics.frc2018.subsystems.controllers.TalonSRXDriveController;
import com.palyrobotics.frc2018.util.archive.DriveSignal;

/**
 * Created by Nihar on 2/12/17.
 * @author Nihar
 * Should be used to set the drivetrain to an offboard closed loop cantalon
 */
public class TalonSRXRoutine extends Routine {
	private boolean relativeSetpoint = false;
	private final DriveSignal mSignal;
	
	private double timeout;
	private double startTime;
	private static RobotState robotState;
	
	public TalonSRXRoutine(DriveSignal controller, boolean relativeSetpoint) {
		this.mSignal = controller;
		this.timeout = 1 << 30;
		this.relativeSetpoint = relativeSetpoint;
		this.robotState = Robot.getRobotState();
	}

	/*
	  * Setpoint is relative when you want it to be updated on start
	  * For position and motion magic only
	  * 
	  * Timeout is in seconds
	  */
	public TalonSRXRoutine(DriveSignal controller, boolean relativeSetpoint, double timeout) {
		this.mSignal = controller;
		this.relativeSetpoint = relativeSetpoint;
		this.timeout = timeout * 1000;
		this.robotState = Robot.getRobotState();
	}

	@Override
	public void start() {
		
		startTime = System.currentTimeMillis();
		
		if (relativeSetpoint) {
			if (mSignal.leftMotor.getControlMode().equals(ControlMode.MotionMagic)) {


				System.out.println(mSignal.leftMotor.getSetpoint());
				System.out.println(robotState.drivePose.leftEnc);
				System.out.println(mSignal.leftMotor.gains);
				System.out.println(mSignal.leftMotor.cruiseVel);
				System.out.println(mSignal.leftMotor.accel);
				mSignal.leftMotor.setMotionMagic(mSignal.leftMotor.getSetpoint()+
								robotState.drivePose.leftEnc,
						mSignal.leftMotor.gains,
						mSignal.leftMotor.cruiseVel, mSignal.leftMotor.accel);
				mSignal.rightMotor.setMotionMagic(mSignal.rightMotor.getSetpoint()+
								robotState.drivePose.rightEnc,
						mSignal.rightMotor.gains,
						mSignal.rightMotor.cruiseVel, mSignal.rightMotor.accel);
			}
			else if (mSignal.leftMotor.getControlMode().equals(ControlMode.Position)) {
				mSignal.leftMotor.setPosition(mSignal.leftMotor.getSetpoint()+
						robotState.drivePose.leftEnc, mSignal.leftMotor.gains);
				mSignal.rightMotor.setPosition(mSignal.rightMotor.getSetpoint()+
						robotState.drivePose.rightEnc, mSignal.rightMotor.gains);
				

			}
		}
		drive.setTalonSRXController(mSignal);
		System.out.println("Sent drivetrain signal "+mSignal.toString());
	}

	@Override
	public Commands update(Commands commands) {
		Commands output = commands.copy();
		output.wantedDriveState = Drive.DriveState.OFF_BOARD_CONTROLLER;
//		DashboardManager.getInstance().updateCANTable(((TalonSRXDriveController)drive.getController()).getCanTableString());
		return output;
	}



	@Override
	public Commands cancel(Commands commands) {
		drive.setNeutral();
		commands.wantedDriveState = Drive.DriveState.NEUTRAL;
		return commands;
	}

	@Override
	public boolean finished() {
		// Wait for controller to be added before finishing routine
		if (mSignal.leftMotor.getSetpoint() != Robot.getRobotState().leftSetpoint) {
			System.out.println("Mismatched desired talon and actual talon setpoints! desired, actual");
			System.out.println("Left: " + mSignal.leftMotor.getSetpoint()+", "+Robot.getRobotState().leftSetpoint);
			return false;
		}
		else if (mSignal.rightMotor.getSetpoint() != Robot.getRobotState().rightSetpoint) {
			System.out.println("Mismatched desired talon and actual talon setpoints! desired, actual");
			System.out.println("Right: " + mSignal.rightMotor.getSetpoint()+", "+Robot.getRobotState().rightSetpoint);
			return false;
		}
		else if (mSignal.leftMotor.getControlMode() != Robot.getRobotState().leftControlMode) {
			System.out.println("Mismatched desired talon and actual talon states!");
			System.out.println(mSignal.leftMotor.getControlMode() + ", "+Robot.getRobotState().leftControlMode);
			return false;
		}
		else if (mSignal.rightMotor.getControlMode() != Robot.getRobotState().rightControlMode) {
			System.out.println("Mismatched desired talon and actual talon states!");
			System.out.println(mSignal.rightMotor.getControlMode()+","+Robot.getRobotState().rightControlMode);
			return false;
		}
		if (!drive.hasController() || (drive.getController().getClass() == TalonSRXDriveController.class && drive.controllerOnTarget())) {
		}
		return !drive.hasController() || System.currentTimeMillis() > this.timeout+startTime || (drive.getController().getClass() == TalonSRXDriveController.class && drive.controllerOnTarget());
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{Drive.getInstance()};
	}

	@Override
	public String getName() {
		return "DriveCANTalonRoutine";
	}
}
