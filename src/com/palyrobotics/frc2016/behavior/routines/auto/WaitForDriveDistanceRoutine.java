package com.palyrobotics.frc2016.behavior.routines.auto;

import com.palyrobotics.frc2016.robot.team254.lib.util.Pose;

/**
 * Waits for drivetrain to pass a specific distance
 * @author Team 254
 *
 */
public class WaitForDriveDistanceRoutine extends TimeoutRoutine {
    public double distance;
    public boolean positive;

    public WaitForDriveDistanceRoutine(double distance, boolean positive, double timeout) {
        super(timeout);
        this.distance = distance;
        this.positive = positive;

    }

    @Override
    public boolean finished() {
        Pose pose = drive.getPhysicalPose();
        double avg = (pose.getLeftDistance() + pose.getRightDistance()) / 2.0;
        return (positive ? avg >= distance : avg <= distance) || super.finished();
    }

}
