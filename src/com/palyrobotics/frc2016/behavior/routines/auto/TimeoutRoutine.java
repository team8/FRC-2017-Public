package com.palyrobotics.frc2016.behavior.routines.auto;

import com.palyrobotics.frc2016.behavior.Routine;
import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.util.Subsystem;

public class TimeoutRoutine extends Routine {
    double m_timeout;
    double m_time_start;

    public TimeoutRoutine(double timeout) {
        m_timeout = timeout;
    }

    @Override
    public boolean isFinished() {
        return System.currentTimeMillis() >= m_time_start + 1000*m_timeout;
    }

    @Override
    public Commands update(Commands commands) {
    	return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
    	return commands;
    }

    @Override
    public void start() {
        m_time_start = System.currentTimeMillis();
    }

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{};
	}

	@Override
	public String getName() {
		return "Timeout";
	}

}
