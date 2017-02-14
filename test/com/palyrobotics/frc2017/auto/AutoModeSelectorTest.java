package com.palyrobotics.frc2017.auto;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import com.palyrobotics.frc2017.auto.modes.*;

/**
 * Created by Nihar on 1/22/17.
 * Tests {@link AutoModeSelector}
 */
public class AutoModeSelectorTest {
	/* TODO: possible tests
	 * Test that you can get an auto mode
	 * Test that the setAutoMode by name works
	 * Test that registerAutonomous works
	 * Test that you can get the list of auto modes
	 */
	
	//TODO add new automodes and test those
		
	@Test
	public void testGetAutoMode() throws IndexOutOfBoundsException {
		AutoModeSelector auto = new AutoModeSelector();

		// Using automodes registered in constructor
		assertThat("Incorrect auto mode retrieved", auto.getAutoMode().getClass(), equalTo(new TestAutoMode().getClass()));
		
		// Check index out of bounds
	}
	
	@Test
	public void testGetAutoModeList() {
		AutoModeSelector auto = new AutoModeSelector();
		
		ArrayList<String> correct = new ArrayList<String>();
		correct.add("DoNothing");
		correct.add("Test");
		ArrayList<String> test = auto.getAutoModeList();
		
		assertThat("Not all auto modes were retrieved", test.size(), equalTo(correct.size()));
		assertThat("Auto modes are incorrect", test, equalTo(correct));
	}

	// TODO: BROKEN WITH NEW AUTO MODES
	@Test
	public void testSetAutoModeByName() {
		AutoModeSelector auto = new AutoModeSelector();
		// 0 DoNothing
		// 1 Test
		auto.registerAutonomous(new DoNothingAutoMode());	// 2

		assertThat("Did not catch duplicates", auto.setAutoModeByName("DoNothing"), equalTo(false));
		assertThat("Found auto mode when none exists", auto.setAutoModeByName("WaitForwardBackward"), equalTo(false));
		assertThat("Auto mode has been registered", auto.setAutoModeByName("Trajectory"), equalTo(true));
	}
	
	@Test
	public void testRegisterAutonomous() {
		AutoModeSelector auto = new AutoModeSelector();

		ArrayList<String> correct = new ArrayList<String>();
		correct.add("DoNothing");
		correct.add("Test");
		correct.add("DoNothing");
		auto.registerAutonomous(new DoNothingAutoMode());
		assertThat("AutoModeSelected was constructed incorrectly", auto.getAutoModeList(), equalTo(correct));
	}
}
