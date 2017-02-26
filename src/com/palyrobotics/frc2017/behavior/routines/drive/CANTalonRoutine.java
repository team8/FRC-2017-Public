package com.palyrobotics.frc2017.behavior.routines.drive;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.robot.Robot;
import com.palyrobotics.frc2017.subsystems.Drive;
import com.palyrobotics.frc2017.subsystems.controllers.CANTalonDriveController;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.palyrobotics.frc2017.util.Subsystem;

/**
 * Created by Nihar on 2/12/17.
 * @author Nihar
 * Should be used to set the drivetrain to an offboard closed loop cantalon
 */
public class CANTalonRoutine extends Routine {
	private Commands output;
	private final DriveSignal mSignal;
	
	public CANTalonRoutine(DriveSignal controller) {
		this.mSignal = controller;
	}

	@Override
	public void start() {
		drive.setCANTalonController(mSignal);
	}

	@Override
	public Commands update(Commands commands) {
		output = commands.copy();
		output.wantedDriveState = Drive.DriveState.OFF_BOARD_CONTROLLER;
		return output;
	}

	@Override
	public Commands cancel(Commands commands) {
		Commands output = commands.copy();
		output.wantedDriveState = Drive.DriveState.NEUTRAL;
		return output;
	}

	@Override
	public boolean finished() {
		double leftScalar = (Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks : Constants.kDriveInchesToTicks;
		double rightScalar = (Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kDericaInchesToTicks : Constants.kDriveInchesToTicks;
		if(mSignal.leftMotor.getSetpoint() * leftScalar != Robot.getRobotState().leftSetpoint || mSignal.rightMotor.getSetpoint() * rightScalar != Robot.getRobotState().rightSetpoint 
				|| mSignal.leftMotor.getControlMode() != Robot.getRobotState().leftControlMode || mSignal.rightMotor.getControlMode() != Robot.getRobotState().rightControlMode) {
			System.out.println("Mismatched desired talon and actual talon states!");
			return false;
		}
		// Wait for controller to be added before finshing routine
		return !drive.hasController() || drive.getController().getClass() == CANTalonDriveController.class && drive.controllerOnTarget();
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
