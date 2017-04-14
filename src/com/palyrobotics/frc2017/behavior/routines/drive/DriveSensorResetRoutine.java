package com.palyrobotics.frc2017.behavior.routines.drive;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.robot.HardwareAdapter;
import com.palyrobotics.frc2017.robot.team254.lib.util.ConstantsBase;
import com.palyrobotics.frc2017.util.Subsystem;

/**
 * Created by EricLiu on 4/13/17.
 */
public class DriveSensorResetRoutine extends Routine
{

    @Override
    public void start() {
        HardwareAdapter.getInstance().getDrivetrain().resetSensors();
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
        if(Math.abs(drive.getPose().leftEnc) <= Constants.kAcceptableEncoderZeroError && Math.abs(drive.getPose().rightEnc) <= Constants.kAcceptableEncoderZeroError
                && Math.abs(drive.getPose().heading) <= Constants.kAcceptableGyroZeroError) {
            return true;
        } else return false;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{drive};
    }

    @Override
    public String getName() {
        return "DriveSensorResetRoutine";
    }
}
