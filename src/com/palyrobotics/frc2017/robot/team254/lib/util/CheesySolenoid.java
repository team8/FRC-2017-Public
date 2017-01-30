package com.palyrobotics.frc2017.robot.team254.lib.util;

import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Solenoid;

public class CheesySolenoid extends Solenoid {
    private boolean m_on = false;
    private boolean m_was_disabled = true;

    public CheesySolenoid(int channel) {
        super((channel > 7 ? 1 : 0), (channel > 7 ? channel - 8 : channel));
    }

    @Override
    public void set(boolean on) {
        boolean is_disabled = RobotState.isDisabled();
        if ((!is_disabled && m_was_disabled) || on != m_on) {
            super.set(on);
        }
        m_on = on;
        m_was_disabled = is_disabled;
    }

    @Override
    public boolean get() {
        return m_on;
    }
}
