package com.palyrobotics.frc2017.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Optional;

public class PoseTest {
	
	@Test
	public void testEquals() {
		Pose pose = new Pose();
		Pose other = new Pose();
		assertThat("Poses are not equal", pose.equals(other), equalTo(true));
		
		pose.heading = 1;
		assertThat("Poses should not be equal", pose.equals(other), equalTo(false));
		
		pose = new Pose();	// reset
		pose.leftError = Optional.of(5.0);
		assertThat("Optionals should not be equal", pose.equals(other), equalTo(false));
	}
	
	// Dependent on equals()
	@Test
	public void testCopy() {
		Pose pose = new Pose();
		Pose copy = pose.copy();
		assertThat("Poses are not equal", pose.equals(copy), equalTo(true));
		
		pose.leftEnc = 1;
		assertThat("Poses should have different references", pose == copy, equalTo(false));
		
		copy = pose.copy();
		assertThat("Poses are not equal", pose.equals(copy), equalTo(true));
		
		pose.rightError = Optional.of(5.0);
		copy = pose.copy();
		assertThat("Poses are not equal", pose.equals(copy), equalTo(true));
	}
}
