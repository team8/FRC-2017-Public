package com.palyrobotics.frc2017.subsystems;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.robot.Robot;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for Spatula
 * @author Ailyn Tong
 */
public class SpatulaTest {
	
	@Test
	public void testOutput() {
		Commands commands = Robot.getCommands();
		Spatula spatula = Spatula.getInstance();
		
		commands.wantedSpatulaState = Spatula.SpatulaState.UP;
		spatula.update(commands, Robot.getRobotState());
		assertThat("Spatula not up", spatula.getOutput(), equalTo(Spatula.SpatulaState.UP));
		
		commands.wantedSpatulaState = Spatula.SpatulaState.DOWN;
		spatula.update(commands, Robot.getRobotState());
		assertThat("Spatula not down", spatula.getOutput(), equalTo(Spatula.SpatulaState.DOWN));
	}
}
