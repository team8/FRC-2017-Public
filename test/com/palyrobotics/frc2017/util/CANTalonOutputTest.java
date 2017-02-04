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

}