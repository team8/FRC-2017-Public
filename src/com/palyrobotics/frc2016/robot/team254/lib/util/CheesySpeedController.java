package com.palyrobotics.frc2016.robot.team254.lib.util;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SpeedController;

public class CheesySpeedController implements SpeedController {
    protected SpeedController[] m_controllers;
    protected int[] m_pdp_slots;
    protected boolean m_invert = false;

    public CheesySpeedController(SpeedController controller, int pdp_slot) {
        m_controllers = new SpeedController[]{controller};
        m_pdp_slots = new int[]{pdp_slot};
    }

    public CheesySpeedController(SpeedController controller, int[] pdp_slots) {
        m_controllers = new SpeedController[]{controller};
        m_pdp_slots = pdp_slots;
    }

    public CheesySpeedController(SpeedController[] controllers, int[] pdp_slots) {
        assert (controllers.length == pdp_slots.length);
        m_controllers = controllers;
        m_pdp_slots = pdp_slots;
    }

    public void setInverted(boolean inverted) {
        m_invert = inverted;
    }

    public boolean getInverted() {
        return m_invert;
    }

    protected double sign() {
        return (m_invert ? -1.0 : 1.0);
    }

    /**
     * Takes the PDP as input and returns the current drawn by these slots
     * @param pdp Reference to the Power distribution panel
     * @return current of all slots or this pdp
     */
    public double getCurrent(PowerDistributionPanel pdp) {
        double current = 0.0;
        for (int slot : m_pdp_slots) {
            current += pdp.getCurrent(slot);
        }
        return current;
    }

    public double getSignedCurrent(PowerDistributionPanel pdp) {
        return getCurrent(pdp) * Math.signum(get()) * sign();
    }

    @Override
    public void pidWrite(double output) {
        for (SpeedController controller : m_controllers) {
            controller.pidWrite(sign() * output);
        }
    }

    @Override
    public double get() {
        return m_controllers[0].get() * sign();
    }

    @Override
    public void set(double speed) {
        for (SpeedController controller : m_controllers) {
            controller.set(speed * sign());
        }
    }

    @Override
    public void disable() {
        for (SpeedController controller : m_controllers) {
            controller.disable();
        }
    }

	public void stopMotor() {
		for (SpeedController controller : m_controllers) {
			controller.set(0.0);
		}
		
	}
}