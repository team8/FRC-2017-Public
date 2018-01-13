package com.palyrobotics.frc2017.config;

import org.junit.Test;

import com.palyrobotics.frc2018.robot.Robot;

import static org.junit.Assert.assertTrue;

/**
 * Created by Nihar on 3/14/17.
 */
public class RobotStateTest {
	@Test
	public void simpleTest() {
		Robot.getRobotState().drivePose.heading = -100;
		assertTrue("Heading", Robot.getRobotState().drivePose.heading==-100);
	}
}
