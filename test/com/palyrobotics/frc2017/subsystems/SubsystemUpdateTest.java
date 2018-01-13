package com.palyrobotics.frc2017.subsystems;

import com.palyrobotics.frc2017.robot.RobotTest;
import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.config.RobotState;
import com.palyrobotics.frc2018.subsystems.Drive;

import org.junit.Test;

/**
 * Tests instantion of all subsystems.
 *
 * @author Robbie Selwyn
 *
 */
public class SubsystemUpdateTest {
	
	@Test
	public void test() {
		Commands c = RobotTest.getCommands();
		RobotState r = new RobotState();
		
		Drive.getInstance().update(c, r);
	}
	
}
