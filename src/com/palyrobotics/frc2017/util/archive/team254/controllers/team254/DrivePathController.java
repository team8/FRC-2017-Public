package com.palyrobotics.frc2017.util.archive.team254.controllers.team254;

import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.util.archive.LegacyDrive;
import com.palyrobotics.frc2017.util.archive.team254.trajectory.LegacyTrajectoryFollower;
import com.palyrobotics.frc2017.util.archive.team254.trajectory.Path;
import com.palyrobotics.frc2017.util.archive.team254.trajectory.Trajectory;
import com.palyrobotics.frc2017.robot.team254.lib.util.ChezyMath;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.palyrobotics.frc2017.robot.team254.lib.util.LegacyPose;

/**
 * DrivePathController.java This controller drives the robot along a specified
 * trajectory.
 *
 * @author Tom Bottiglieri
 */
public class DrivePathController implements LegacyDrive.DriveController {

    public DrivePathController(Path path) {
        init();
        loadProfile(path.getLeftWheelTrajectory(), path.getRightWheelTrajectory(), 1.0, 0.0);
    }

    Trajectory trajectory;
    LegacyTrajectoryFollower followerLeft = new LegacyTrajectoryFollower("left");
    LegacyTrajectoryFollower followerRight = new LegacyTrajectoryFollower("right");
    double direction;
    double heading;
    double kTurn = -Constants2016.kDrivePathHeadingFollowKp;

    public boolean onTarget() {
        return followerLeft.isFinishedTrajectory();
    }

    private void init() {
        followerLeft.configure(Constants2016.kDrivePositionKp,
                Constants2016.kDrivePositionKi, Constants2016.kDrivePositionKd,
                Constants2016.kDrivePositionKv, Constants2016.kDrivePositionKa);
        followerRight.configure(Constants2016.kDrivePositionKp,
                Constants2016.kDrivePositionKi, Constants2016.kDrivePositionKd,
                Constants2016.kDrivePositionKv, Constants2016.kDrivePositionKa);
    }

    private void loadProfile(Trajectory leftProfile, Trajectory rightProfile,
                             double direction, double heading) {
        reset();
        followerLeft.setTrajectory(leftProfile);
        followerRight.setTrajectory(rightProfile);
        this.direction = direction;
        this.heading = heading;
    }

    public void loadProfileNoReset(Trajectory leftProfile,
                                   Trajectory rightProfile) {
        followerLeft.setTrajectory(leftProfile);
        followerRight.setTrajectory(rightProfile);
    }

    public void reset() {
        followerLeft.reset();
        followerRight.reset();
    }

    public int getFollowerCurrentSegmentNumber() {
        return followerLeft.getCurrentSegmentNumber();
    }

    public int getNumSegments() {
        return followerLeft.getNumSegments();
    }

    public void setTrajectory(Trajectory t) {
        this.trajectory = t;
    }

    public double getGoal() {
        return 0;
    }

    @Override
    public DriveSignal update(LegacyPose pose) {
        if (onTarget()) {
            return DriveSignal.getNeutralSignal();
        } else {
            double distanceL = direction * pose.getLeftDistance();
            double distanceR = direction * pose.getRightDistance();

            double speedLeft = direction * followerLeft.calculate(distanceL);
            double speedRight = direction * followerRight.calculate(distanceR);

            double goalHeading = followerLeft.getHeading();
            double observedHeading = -pose.getHeading();

            double angleDiffRads = ChezyMath.getDifferenceInAngleRadians(
                    observedHeading, goalHeading);
            double angleDiff = Math.toDegrees(angleDiffRads);

            double turn = kTurn * angleDiff;
            System.out.println("left: " + (speedLeft+turn));
            System.out.println("right: " + (speedRight-turn));
            DriveSignal output = DriveSignal.getNeutralSignal();
            output.leftMotor.setPercentVBus(speedLeft + turn);
            output.rightMotor.setPercentVBus(speedRight - turn);
            return output;
        }
    }

    @Override
    public LegacyPose getCurrentSetpoint() {
        return new LegacyPose(followerLeft.getCurrentSegment().pos, 0, 0, 0, -followerLeft.getHeading(), 0);
    }
}
