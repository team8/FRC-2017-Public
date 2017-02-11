package com.palyrobotics.frc2017.subsystems;

import org.junit.Test;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.RobotState;

/**
 * Tests instantion of all subsystems.
 *
 * @author Robbie Selwyn
 *
 */
public class SubsystemUpdateTest {
	
	@Test
	public void test() {
		Commands c = new Commands();
		RobotState r = new RobotState();
		
		Drive.getInstance().update(c, r);
		Climber.getInstance().update(c, r);
		Flippers.getInstance().update(c, r);
		Spatula.getInstance().update(c, r);
		Slider.getInstance().update(c, r);
		Spatula.getInstance().update(c, r);
	}
	
}
