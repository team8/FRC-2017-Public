package com.palyrobotics.frc2017.behavior.routines.auto;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.util.Subsystem;

public class TimeoutRoutine extends Routine {
    double timeout;
    double timeStart;

    public TimeoutRoutine(double timeout) {
        this.timeout = timeout;
    }

    @Override
    public boolean finished() {
        return System.currentTimeMillis() >= timeStart + 1000 * timeout;
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
        timeStart = System.currentTimeMillis();
    }

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{};
	}

	@Override
	public String getName() {
		return "TimeoutRoutine";
	}

}
