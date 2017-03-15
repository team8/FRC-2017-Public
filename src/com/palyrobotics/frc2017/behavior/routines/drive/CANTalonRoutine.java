package com.palyrobotics.frc2017.behavior.routines.drive;

import com.ctre.CANTalon;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.robot.Robot;
import com.palyrobotics.frc2017.subsystems.Drive;
import com.palyrobotics.frc2017.subsystems.controllers.CANTalonDriveController;
import com.palyrobotics.frc2017.util.CANTalonOutput;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.palyrobotics.frc2017.util.Subsystem;

/**
 * Created by Nihar on 2/12/17.
 * @author Nihar
 * Should be used to set the drivetrain to an offboard closed loop cantalon
 */
public class CANTalonRoutine extends Routine {
	private boolean relativeSetpoint = false;
	private final DriveSignal mSignal;
	
	public CANTalonRoutine(DriveSignal controller) {
		this.mSignal = controller;
	}

	/*
	  * Setpoint is relative when you want it to be updated on start
	  * For position and motion magic only
	  */
	public CANTalonRoutine(DriveSignal controller, boolean relativeSetpoint) {
		this.mSignal = controller;
		this.relativeSetpoint = relativeSetpoint;
	}

	@Override
	public void start() {
		if (relativeSetpoint) {
			System.out.println("CANTalon relative start point:" +Robot.getRobotState().drivePose.leftEnc);
			if (mSignal.leftMotor.getControlMode() == CANTalon.TalonControlMode.MotionMagic) {
				mSignal.leftMotor.setMotionMagic(mSignal.leftMotor.getSetpoint()+
								Robot.getRobotState().drivePose.leftEnc,
						mSignal.leftMotor.gains,
						mSignal.leftMotor.cruiseVel, mSignal.leftMotor.accel);
				mSignal.rightMotor.setMotionMagic(mSignal.rightMotor.getSetpoint()+
								Robot.getRobotState().drivePose.rightEnc,
						mSignal.rightMotor.gains,
						mSignal.rightMotor.cruiseVel, mSignal.rightMotor.accel);
			}
			else if (mSignal.leftMotor.getControlMode() == CANTalon.TalonControlMode.Position) {
				mSignal.leftMotor.setPosition(mSignal.leftMotor.getSetpoint()+
						Robot.getRobotState().drivePose.leftEnc, mSignal.leftMotor.gains);
				mSignal.rightMotor.setPosition(mSignal.rightMotor.getSetpoint()+
						Robot.getRobotState().drivePose.rightEnc, mSignal.rightMotor.gains);

			}
		}
		drive.setCANTalonController(mSignal);
		System.out.println("Sent drivetrain signal "+mSignal.toString());
	}

	@Override
	public Commands update(Commands commands) {
		Commands output = commands.copy();
		output.wantedDriveState = Drive.DriveState.OFF_BOARD_CONTROLLER;
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
			System.out.println("Mismatched desired talon and actual talon states! desired, actual");
			System.out.println(mSignal.leftMotor.getSetpoint()+", "+Robot.getRobotState().leftSetpoint);
			return false;
		}
		else if (mSignal.rightMotor.getSetpoint() != Robot.getRobotState().rightSetpoint) {
			System.out.println("Mismatched desired talon and actual talon states! desired, actual");
			System.out.println(mSignal.rightMotor.getSetpoint()+", "+Robot.getRobotState().rightSetpoint);
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
		if (!drive.hasController() || (drive.getController().getClass() == CANTalonDriveController.class && drive.controllerOnTarget())) {
			System.out.println("CANTalon on target and finished!");
		}
		return !drive.hasController() || (drive.getController().getClass() == CANTalonDriveController.class && drive.controllerOnTarget());
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
