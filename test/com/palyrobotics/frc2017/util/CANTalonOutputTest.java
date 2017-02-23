package com.palyrobotics.frc2017.util;

import com.ctre.CANTalon;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Created by Nihar on 2/3/17.
 */
public class CANTalonOutputTest {
	/**
	 * Test that CANTalon outputs are being configured correctly
	 * TODO: Not all accounted for
	 */
	@Test
	public void testConfigurations() throws Exception {
		CANTalonOutput testOutput = new CANTalonOutput();
		testOutput.gains = Gains.dericaPosition;
		// Test vbus configuration
		testOutput.setPercentVBus(0.5);
		assertThat("Percent vbus setpoint incorrect", testOutput.getSetpoint(), equalTo(0.5));
		assertThat("Percent vbus control mode incorrect", testOutput.getControlMode(), equalTo(CANTalon.TalonControlMode.PercentVbus));
		testOutput = new CANTalonOutput();

		// Test position configuration w/o factory used
		testOutput.setPosition(10, Gains.dericaPosition);
		assertThat("Position setpoint incorrect", testOutput.getSetpoint(), equalTo(10.0));
		assertThat("Position control mode incorrect", testOutput.getControlMode(), equalTo(CANTalon.TalonControlMode.Position));
		// TODO: test the PIDF izone ramprate
		testOutput = new CANTalonOutput();
		// Test position configuration w/o factory used
	}

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
		one.setPosition(0, Gains.dericaPosition);
		two = new CANTalonOutput(one);
		assertTrue("Copied wasn't equal", one.equals(two));
	}
}