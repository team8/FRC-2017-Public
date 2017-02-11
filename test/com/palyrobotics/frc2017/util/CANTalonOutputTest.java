package com.palyrobotics.frc2017.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Nihar on 2/3/17.
 */
public class CANTalonOutputTest {
	/**
	 * Test equals method works
	 * @throws Exception
	 */
	@Test
	public void equals() throws Exception {
		CANTalonOutput one = new CANTalonOutput();
		CANTalonOutput two = new CANTalonOutput();
		one.setPercentVBus(0);
		two.setPercentVBus(0);
		assertTrue("Both vbus not equal", one.equals(two));
		two.setPercentVBus(1);
		assertFalse("Vbus shouldn't be considered equal", one.equals(two));

		one = DriveSignal.getNeutralSignal().leftMotor;
		two = DriveSignal.getNeutralSignal().leftMotor;
		assertFalse("Neutral signals should have different references", one==two);
		assertTrue("Neutral signals should be equivalent", one.equals(two));
	}

	/**
	 * Test the copy method
	 */
	@Test
	public void testCopy() throws Exception {
		CANTalonOutput one = new CANTalonOutput();
		CANTalonOutput two;
		one.setPercentVBus(0);
		two = new CANTalonOutput(one);
		assertTrue("Copied wasn't equal", one.equals(two));
		one.setPercentVBus(0.5);
		two = new CANTalonOutput(one);
		assertTrue("Copied wasn't equal", one.equals(two));
		one.setDisabled();
		two = new CANTalonOutput(one);
		assertTrue("Copied wasn't equal", one.equals(two));
		one.setPosition(0, 0, 0, 0, 0, 0, 0);;
		two = new CANTalonOutput(one);
		assertTrue("Copied wasn't equal", one.equals(two));
	}
}