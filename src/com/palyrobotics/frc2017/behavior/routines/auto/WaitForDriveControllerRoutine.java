package com.palyrobotics.frc2017.behavior.routines.auto;

/**
 * Waits for drivetrain controller to reach it's target, or time out
 * @author Team 254
 *
 */
public class WaitForDriveControllerRoutine extends TimeoutRoutine {
    public WaitForDriveControllerRoutine(double timeout) {
        super(timeout);
    }

    @Override
    public boolean finished() {
        return drive.controllerOnTarget() || super.finished();
    }

}
