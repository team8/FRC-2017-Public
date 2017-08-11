package com.palyrobotics.frc2017.behavior.routines;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.robot.HardwareAdapter;
import com.palyrobotics.frc2017.subsystems.Subsystem;

/**
 * Created by EricLiu on 4/17/17.
 */
public class SliderSensorResetRoutine extends Routine {
    @Override
    public void start() {
        HardwareAdapter.getInstance().getSlider().resetEncoder();
    }

    @Override
    public Commands update(Commands commands) {
        Commands output = commands.copy();
        return output;
    }

    @Override
    public Commands cancel(Commands commands) {
        Commands output = commands.copy();
        return output;
    }

    @Override
    public boolean finished() {
        return true;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{slider};
    }

    @Override
    public String getName() {
        return "SliderSensorResetRoutine";
    }
}
