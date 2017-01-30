package com.palyrobotics.frc2017.config;

import org.junit.Test;

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
		mCommands.operatorStickInput.x = 0;
		System.out.println("No null pointer exceptions!");
	}
}
