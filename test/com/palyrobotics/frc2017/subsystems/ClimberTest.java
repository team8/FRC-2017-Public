package com.palyrobotics.frc2017.subsystems;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.robot.Robot;

public class ClimberTest {
	
	@Test
	public void testOutput() {
		Commands commands = Robot.getCommands();
		Climber climber = Climber.getInstance();
		
		commands.wantedClimberState = Climber.ClimberState.IDLE;
		climber.update(commands, Robot.getRobotState());
		assertTrue("Climber is not idle", climber.getOutput() == (0));
		
		commands.wantedClimberState = Climber.ClimberState.CLIMBING;
		climber.update(commands, Robot.getRobotState());
		assertTrue("Climber isn't climbing at proper speed", climber.getOutput() == (climber.kClimberSpeed));
	}
}
