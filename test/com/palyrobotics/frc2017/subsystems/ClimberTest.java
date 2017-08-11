package com.palyrobotics.frc2017.subsystems;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.Robot;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Tests for Climber
 * @author Ailyn Tong
 */
public class ClimberTest {
	Climber climber = Climber.getInstance();
	RobotState robotState = Robot.getRobotState();
	Commands commands = Robot.getCommands();
	
	@Test
	public void testOutput() {
		// Test idle output
		commands.wantedClimberState = Climber.ClimberState.IDLE;
		climber.update(commands, robotState);
		assertThat("Climber not idle", climber.getState(), equalTo(Climber.ClimberState.IDLE));
		assertThat("Climber should not move", climber.getOutput(), equalTo(0));
		
		// Test manual output
		commands.wantedClimberState = Climber.ClimberState.MANUAL;
		climber.update(commands, robotState);
		assertThat("Climber not in manual mode", climber.getState(), equalTo(Climber.ClimberState.MANUAL));
		assertThat("Climber not moving at constant speed", climber.getOutput(), equalTo(Climber.kClimbSpeed));
		
		commands.wantedClimberState = Climber.ClimberState.WAITING_FOR_ROPE;
		climber.update(commands, robotState);
		assertThat("Climber not waiting for rope", climber.getState(), equalTo(Climber.ClimberState.WAITING_FOR_ROPE));
		assertThat("Climber not moving slowly", climber.getOutput(), equalTo(Climber.kRopeGrabSpeed));
		
		// Current draw spikes when rope starts to hold robot's weight
		robotState.climberEncoder = 0;	// for next test
		robotState.climberCurrentDraw = Climber.kClimbingTriggerCurrent + 10;
		climber.update(commands, robotState);
		assertThat("Climber not in encoder climb mode", climber.getState(), equalTo(Climber.ClimberState.MANUAL));
		
		// Current draw decreases after initial spike
		robotState.climberEncoder = Climber.kMinimumDeltaEnc + 1;
		robotState.climberCurrentDraw = 20;
		climber.update(commands, robotState);
		assertThat("Climber should still be in encoder climb mode", climber.getState(), equalTo(Climber.ClimberState.CLIMBING_ENCODER_DISTANCE));
		
		// Climber stuck - if the encoder value doesn't change enough between iterations, cancel climb
		climber.update(commands, robotState);
		assertThat("Climber should idle because stuck", climber.getState(), equalTo(Climber.ClimberState.IDLE));
		
		// Trigger climb again for next test
		robotState.climberCurrentDraw = Climber.kClimbingTriggerCurrent + 10;
		climber.update(commands, robotState);
		// Stalling
		robotState.climberCurrentDraw = Climber.kStallingTriggerCurrent + 10;
		climber.update(commands, robotState);
		assertThat("Climber should idle because stuck", climber.getState(), equalTo(Climber.ClimberState.IDLE));
		
		// Trigger climb again for next test
		robotState.climberCurrentDraw = Climber.kClimbingTriggerCurrent + 10;
		climber.update(commands, robotState);
		// Reached end
		robotState.climberEncoder = Climber.kEncoderTicksToTop + 1;
		climber.update(commands, robotState);
		assertThat("Climber should idle because finished", climber.getState(), equalTo(Climber.ClimberState.IDLE));
	}
}
