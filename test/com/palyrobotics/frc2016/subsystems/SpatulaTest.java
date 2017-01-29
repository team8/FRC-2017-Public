package com.palyrobotics.frc2016.subsystems;

import static org.junit.Assert.*;

import org.junit.Test;

import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.robot.Robot;

/**
 * Spatula Tests
 * @author Ailyn Tong
 */
public class SpatulaTest {
	
	@Test
	public void testOutput() {
		Commands commands = Robot.getCommands();
		Spatula spatula = Spatula.getInstance();
		
		commands.wantedSpatulaState = Spatula.SpatulaState.UP;
		spatula.update(commands, Robot.getRobotState());
		assertTrue("Spatula not up", spatula.getOutput().equals(Spatula.SpatulaState.UP));
		
		commands.wantedSpatulaState = Spatula.SpatulaState.DOWN;
		spatula.update(commands, Robot.getRobotState());
		assertTrue("Spatula isn't down", spatula.getOutput().equals(Spatula.SpatulaState.DOWN));
	}
}
