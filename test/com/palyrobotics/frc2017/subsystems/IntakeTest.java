package com.palyrobotics.frc2017.subsystems;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.robot.Robot;

/**
 * Unit tests for Intake
 * @author Ailyn Tong
 */
public class IntakeTest {

	@Test
	public void testOutput() {
		Commands commands = Robot.getCommands();
		Intake intake = Intake.getInstance();
		
		commands.wantedIntakeState = Intake.IntakeState.IDLE;
		intake.update(commands, Robot.getRobotState());
		assertThat("Intake should be idle", intake.getOutput(), equalTo(0));
		
		commands.wantedIntakeState = Intake.IntakeState.INTAKE;
		intake.update(commands, Robot.getRobotState());
		assertThat("Intake should be intaking", intake.getOutput(), equalTo(1));
		
		commands.wantedIntakeState = Intake.IntakeState.EXPEL;
		intake.update(commands, Robot.getRobotState());
		assertThat("Intake should be expelling", intake.getOutput(), equalTo(-1));
	}
}
