package com.palyrobotics.frc2017.behavior.routines.drive;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.util.Subsystem;

/**
 * Created by EricLiu on 4/13/17.
 */
public class DriveSensorResetRoutine extends Routine
{

    @Override
    public void start() {
        
    }

    @Override
    public Commands update(Commands commands) {
        return null;
    }

    @Override
    public Commands cancel(Commands commands) {
        return null;
    }

    @Override
    public boolean finished() {
        return false;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[0];
    }

    @Override
    public String getName() {
        return null;
    }
}
