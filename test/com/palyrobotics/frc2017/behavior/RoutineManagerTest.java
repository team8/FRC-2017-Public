package com.palyrobotics.frc2017.behavior;

import com.palyrobotics.frc2017.config.Commands;
import org.junit.Test;

/**
 * Created by Nihar on 1/22/17.
 * Unit tests for {@link RoutineManager}
 * @author Nihar
 */
public class RoutineManagerTest {
	private RoutineManager mRoutineManager = new RoutineManager();

	/**
	 * Test that the reset method <br/>
	 * Resets running routines, cancels routines, updates commands
	 */
	@Test
	public void testResetMethod() {
		Commands commands = new Commands();
		mRoutineManager.reset(commands);

		mRoutineManager.addNewRoutine(commands, new SampleRoutine());
		// TODO: Test actual functionality of the reset method
		mRoutineManager.reset(commands);
	}
}
