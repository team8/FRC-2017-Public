package com.palyrobotics.frc2017.subsystems;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.robot.Robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * Unit tests for Flippers
 * @author Ailyn Tong
 */
public class FlippersTest {
	
	@Test
	public void testOutput() {
		Commands commands = Robot.getCommands();
		Flippers flippers = Flippers.getInstance();
		
		Flippers.FlipperSignal desired = new Flippers.FlipperSignal(DoubleSolenoid.Value.kForward, DoubleSolenoid.Value.kForward);
		commands.wantedFlipperSignal = desired;
		flippers.update(commands, Robot.getRobotState());
		assertThat("Spatula not up", flippers.getFlipperSignal(), equalTo(desired));
		
		desired = new Flippers.FlipperSignal(DoubleSolenoid.Value.kForward, DoubleSolenoid.Value.kReverse);
		commands.wantedFlipperSignal = desired;
		flippers.update(commands, Robot.getRobotState());
		assertThat("Spatula not up", flippers.getFlipperSignal(), equalTo(desired));
		
		desired = new Flippers.FlipperSignal(DoubleSolenoid.Value.kReverse, DoubleSolenoid.Value.kForward);
		commands.wantedFlipperSignal = desired;
		flippers.update(commands, Robot.getRobotState());
		assertThat("Spatula not up", flippers.getFlipperSignal(), equalTo(desired));
		
		desired = new Flippers.FlipperSignal(DoubleSolenoid.Value.kReverse, DoubleSolenoid.Value.kReverse);
		commands.wantedFlipperSignal = desired;
		flippers.update(commands, Robot.getRobotState());
		assertThat("Spatula not up", flippers.getFlipperSignal(), equalTo(desired));
	}
}
