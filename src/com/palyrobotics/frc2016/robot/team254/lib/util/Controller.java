package com.palyrobotics.frc2016.robot.team254.lib.util;

public abstract class Controller {
    protected boolean m_enabled = false;

    public abstract void reset();
    public abstract boolean isOnTarget();
}
