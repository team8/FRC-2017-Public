package com.palyrobotics.frc2017.subsystems;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.Robot;
import com.palyrobotics.frc2017.util.archive.DriveSignal;

/**
 * Created by Nihar on 1/22/17.
 * Tests {@link LegacyDrive}
 */
public class DriveTest {
	@Test
	public void testOffboard() {
		Commands commands = new Commands();
		RobotState state = Robot.getRobotState();
		Drive drive = Drive.getInstance();
		drive.resetController();
		drive.update(commands, state);
		commands.wantedDriveState = Drive.DriveState.OFF_BOARD_CONTROLLER;
		drive.update(commands, state);	// should print error message that controller is missing

		DriveSignal signal = DriveSignal.getNeutralSignal();
		signal.leftMotor.setPercentVBus(0.5);
		signal.rightMotor.setPercentVBus(0.5);
		drive.setCANTalonController(signal);
		drive.update(commands, state);
		assertThat("not updating correctly", drive.getDriveSignal(), equalTo(signal));
		signal.leftMotor.setPercentVBus(1);
		drive.update(commands, state);
		assertFalse("Signal was updated through external reference!", drive.getDriveSignal()==signal);

		// Test that pass by reference is ok
		DriveSignal newSignal = DriveSignal.getNeutralSignal();
		newSignal.leftMotor.setPercentVBus(1);
		newSignal.rightMotor.setPercentVBus(1);
		drive.setCANTalonController(newSignal);
		drive.update(commands, state);
		assertThat("not updating correctly", drive.getDriveSignal(), equalTo(newSignal));
	}

	@Test
	public void testNeutral() throws Exception {
		Drive drive = Drive.getInstance();
		drive.setNeutral();
		assertThat("Drive output not neutral!", drive.getDriveSignal(), equalTo(DriveSignal.getNeutralSignal()));

		// TODO: Undo neutral and try again
	}
}
