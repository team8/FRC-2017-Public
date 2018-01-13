package com.palyrobotics.frc2017.util;

import com.palyrobotics.frc2017.robot.RobotTest;
import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.config.RobotState;
import com.palyrobotics.frc2018.util.archive.CheesyDriveHelper;
import com.palyrobotics.frc2018.util.archive.DriveSignal;

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

		// Robot state is not used by CDH
		RobotState testRobotState = new RobotState();
		Commands testCommands = RobotTest.getCommands();

		// Test that 0 input leads to 0 output (no negative inertia to start)
		testCommands.leftStickInput.y = 0;
		testCommands.rightStickInput.x = 0;
		mTestCDH.cheesyDrive(testCommands, testRobotState);
		DriveSignal output = mTestCDH.cheesyDrive(testCommands, testRobotState);
		DriveSignal zeroOutput = DriveSignal.getNeutralSignal();
		assertTrue("Zero input should have zero output", output.equals(zeroOutput));

		// Test turning
		testCommands.leftStickInput.y = 0.5;
		testCommands.rightStickInput.x = -0.5;
	}

	@Test
	public void testChezy() {
		System.out.println(mTestCDH.remapThrottle(0));
	}

	// Helper method to check if two numbers have the same sign
	private boolean matchSign(double n1, double n2) {
		return (n1 <= 0) == (n2 <= 0);
	}
}
