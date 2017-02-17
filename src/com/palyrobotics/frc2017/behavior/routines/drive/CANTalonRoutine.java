package com.palyrobotics.frc2017.behavior.routines.drive;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
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
	private final DriveSignal mSignal;
	public CANTalonRoutine(DriveSignal controller) {
		this.mSignal = controller;
	}

	@Override
	public void start() {
		System.out.println("cantalon routine started");
		drive.setCANTalonController(mSignal);
	}

	@Override
	public Commands update(Commands commands) {
		System.out.println("msignal setpoint: " + mSignal.leftMotor.getSetpoint());
		Commands output = commands.copy();
		output.wantedDriveState = Drive.DriveState.OFF_BOARD_CONTROLLER;
		return output;
	}

	@Override
	public Commands cancel(Commands commands) {
		System.out.println("cantalon routine finished");
		Commands output = commands.copy();
		output.wantedDriveState = Drive.DriveState.NEUTRAL;
		return output;
	}

	@Override
	public boolean finished() {
		// Wait for controller to be added before finshing routine
		System.out.println("no controller: " + !drive.hasController());
		System.out.println("controller on target: " + drive.controllerOnTarget());
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
