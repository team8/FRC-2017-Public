package com.palyrobotics.frc2016.behavior.routines.auto;

import com.palyrobotics.frc2016.robot.team254.lib.util.Pose;

/**
 * Waits for drivetrain to pass a specific distance
 * @author Team 254
 *
 */
public class WaitForDriveDistanceRoutine extends TimeoutRoutine {
    public double m_distance;
    public boolean m_positive;

    public WaitForDriveDistanceRoutine(double distance, boolean positive, double timeout) {
        super(timeout);
        m_distance = distance;
        m_positive = positive;

    }

    @Override
    public boolean isFinished() {
        Pose pose = drive.getPhysicalPose();
        double avg = (pose.getLeftDistance() + pose.getRightDistance()) / 2.0;
        return (m_positive ? avg >= m_distance : avg <= m_distance) || super.isFinished();
    }

}
