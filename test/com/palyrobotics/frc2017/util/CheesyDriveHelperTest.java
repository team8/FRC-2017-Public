package com.palyrobotics.frc2017.util;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.RobotState;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests CheesyDriveHelper
 * Created by Nihar on 12/23/16.
 * @author Nihar
 */
public class CheesyDriveHelperTest {
	// Instance to test with
	private CheesyDriveHelper mTestCDH = new CheesyDriveHelper();

	/**
	 * Tests for positive/negative/zero inputs matching sign of outputs
	 */
	@Test
	public void testSign() {
		// Test matchSign helper method
		assertTrue("Match Sign broken", matchSign(1,2));
		assertFalse("Match Sign broken", matchSign(1,-2));
		assertTrue("Match Sign broken", matchSign(-1,-2));
		assertTrue("Match Sign broken", matchSign(0,0));
		assertFalse("Match Sign broken", matchSign(1,0));
		// Robot state only used by CDH to check for high gear vs low gear
		RobotState testRobotState = new RobotState();
		Commands testCommands = new Commands();

		// Test that 0 input leads to 0 output (no negative inertia to start)
		testCommands.leftStickInput.y = 0;
		testCommands.rightStickInput.x = 0;
		mTestCDH.cheesyDrive(testCommands, testRobotState);
		DriveSignal output = mTestCDH.cheesyDrive(testCommands, testRobotState);
		boolean zeroOutput = (output.leftMotor.getSetpoint() == 0) && (output.rightMotor.getSetpoint() == 0);
		assertTrue("Zero input should have zero output", zeroOutput);

		// Test turning
		testCommands.leftStickInput.y = 0.5;
		testCommands.rightStickInput.x = -0.5;
	}

	// Helper method to check if two numbers have the same sign
	private boolean matchSign(double n1, double n2) {
		return (n1 <= 0) == (n2 <= 0);
	}
}
