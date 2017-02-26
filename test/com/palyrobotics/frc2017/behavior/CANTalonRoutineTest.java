package com.palyrobotics.frc2017.behavior;

import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.subsystems.Drive;
import com.palyrobotics.frc2017.subsystems.controllers.CANTalonDriveController;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

/**
 * Created by Nihar on 2/16/17.
 */
public class CANTalonRoutineTest {
	@Test
	public void basicTest() throws Exception {
		// Construct arbitrary offboard position loop drive signal
		DriveSignal mSignal = DriveSignal.getNeutralSignal();
		mSignal.leftMotor.setPosition(10, Gains.aegirPosition);
		mSignal.rightMotor.setPosition(10, Gains.aegirPosition);
		CANTalonRoutine mRoutine = new CANTalonRoutine(mSignal);

		RoutineManager routineManager = new RoutineManager();
		Commands commands = new Commands();

		routineManager.addNewRoutine(mRoutine);
		// Update to actually add and run routine
		commands = routineManager.update(commands);
		assertThat("Routine finished early!", mRoutine.finished(), equalTo(false));
		assertThat("Routine didn't request drive state to OFF_BOARD_CONTROLLER",
				commands.wantedDriveState, equalTo(Drive.DriveState.OFF_BOARD_CONTROLLER));

		ArrayList<Routine> expectedRoutineList = new ArrayList<>();
		expectedRoutineList.add(mRoutine);
		assertThat("CANTalonRoutine was not added", routineManager.getCurrentRoutines(), equalTo(expectedRoutineList));

		Drive drive = Drive.getInstance();
		assertNotNull("Drive controller shouldn't be null", drive.getController());
		drive.update(commands, new RobotState());
		assertNotNull("Drive controller shouldn't be null", drive.getController());
		assertThat("Drive controller not of expected type", drive.getController().getClass(),
				equalTo(CANTalonDriveController.class));
		assertThat("Drive controller output not as expected", drive.getController().update(new RobotState()), equalTo(mSignal));
	}
}
