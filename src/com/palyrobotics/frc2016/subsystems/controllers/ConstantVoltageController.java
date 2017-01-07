package com.palyrobotics.frc2016.subsystems.controllers;

import com.palyrobotics.frc2016.robot.team254.lib.util.Controller;

public class ConstantVoltageController extends Controller {
	private double m_voltage;
	/**
	 * Set the voltage for this controller to constantly return
	 * @param voltage Voltage to return, between -1 and 1
	 */
	public ConstantVoltageController(double voltage) {
		m_voltage = Math.max(-1, Math.min(1, voltage));
	}
	
	/**
	 * Returns the constant voltage output
	 * @return the voltage output, always constant
	 */
	public double get() {
		return m_voltage;
	}
	
	@Override
	public boolean isOnTarget() {
		return false;
	}

	@Override
	public void reset() {
		
	}
}
