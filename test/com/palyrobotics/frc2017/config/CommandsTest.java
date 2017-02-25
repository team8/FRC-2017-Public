package com.palyrobotics.frc2017.config;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.SampleRoutine;
import com.palyrobotics.frc2017.subsystems.Drive;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by Nihar on 1/22/17.
 * Tests the {@link Commands}
 * @author Nihar
 */
public class CommandsTest {
	private Commands mCommands = new Commands();

	/**
	 * Tests for null pointer exceptions when initially setting values in Commands
	 */
	@Test
	public void testNullPointers() {
		// Check for variable construction in Commands if a line throws an Exception
		mCommands.wantedDriveState.toString();
		mCommands.wantedFlipperSignal.toString();
		mCommands.wantedIntakeState.toString();
		mCommands.wantedSpatulaState.toString();
		mCommands.robotSetpoints.toString();
		mCommands.leftStickInput.y = 0;
		mCommands.rightStickInput.y = 0;
		mCommands.sliderStickInput.x = 0;
		System.out.println("No null pointer exceptions!");
	}

	/**
	 * Test that the copy method works
	 */
	@Test
	public void testCopyMethod() {
		mCommands = new Commands();
		Commands copy = mCommands.copy();

		// Test the
		mCommands.wantedDriveState = Drive.DriveState.NEUTRAL;
		copy.wantedDriveState = Drive.DriveState.CHEZY;
		assertThat("Copy modified original drivestate", mCommands.wantedDriveState, equalTo(Drive.DriveState.NEUTRAL));

		mCommands.robotSetpoints.drivePowerSetpoint = Optional.of(DriveSignal.getNeutralSignal());
		copy.robotSetpoints.drivePowerSetpoint = null;

		assertThat("Copy modified original setpoints",
				mCommands.robotSetpoints.drivePowerSetpoint.get(), equalTo(DriveSignal.getNeutralSignal()));
		
		System.out.println(copy.cancelCurrentRoutines);
		System.out.println(copy.wantedRoutines);
	}
}
