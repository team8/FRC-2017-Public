package com.palyrobotics.frc2017.subsystems.controllers.team254;

import static com.palyrobotics.frc2017.robot.team254.lib.trajectory.TrajectoryFollower.TrajectorySetpoint;

import com.palyrobotics.frc2017.util.LegacyDrive;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.robot.team254.lib.trajectory.TrajectoryFollower;
import com.palyrobotics.frc2017.util.DriveSignal;
import com.palyrobotics.frc2017.robot.team254.lib.util.Pose;
import com.palyrobotics.frc2017.robot.team254.lib.util.SynchronousPID;

public class DriveStraightController implements LegacyDrive.DriveController {

    private TrajectoryFollowingPositionController mDistanceController;
    private SynchronousPID mTurnPid;
    private Pose mSetpointRelativePose;

    public DriveStraightController(Pose priorSetpoint, double goalSetpoint, double maxVelocity) {
        TrajectoryFollower.TrajectoryConfig config = new TrajectoryFollower.TrajectoryConfig();
        config.dt = Constants.kControlLoopsDt;
        config.max_acc = Constants.kDriveMaxAccelInchesPerSec2;
        config.max_vel = maxVelocity;

        mDistanceController = new TrajectoryFollowingPositionController(
                Constants.kDrivePositionKp,
                Constants.kDrivePositionKi,
                Constants.kDrivePositionKd,
                Constants.kDrivePositionKv,
                Constants.kDrivePositionKa,
                Constants.kDriveOnTargetError,
                config);

        TrajectorySetpoint initialSetpoint = new TrajectorySetpoint();
        initialSetpoint.pos = encoderDistance(priorSetpoint);
        initialSetpoint.vel = encoderVelocity(priorSetpoint);
        mDistanceController.setGoal(initialSetpoint, goalSetpoint);

        mTurnPid = new SynchronousPID();
        mTurnPid.setPID(
                Constants.kDriveStraightKp,
                Constants.kDriveStraightKi,
                Constants.kDriveStraightKd);
        mTurnPid.setSetpoint(priorSetpoint.getHeading());
        mSetpointRelativePose = new Pose(
                priorSetpoint.getLeftDistance(),
                priorSetpoint.getRightDistance(),
                0,
                0,
                priorSetpoint.getHeading(),
                priorSetpoint.getHeadingVelocity());
    }

    @Override
    public DriveSignal update(Pose currentPose) {
        mDistanceController.update(
                (currentPose.getLeftDistance() + currentPose.getRightDistance()) / 2.0,
                (currentPose.getLeftVelocity() + currentPose.getRightVelocity()) / 2.0);
        double throttle = mDistanceController.get();
        double turn = mTurnPid.calculate(currentPose.getHeading());
        DriveSignal output = DriveSignal.getNeutralSignal();
        output.leftMotor.setPercentVBus(throttle + turn);
        output.rightMotor.setPercentVBus(throttle - turn);
        return output;
    }

    @Override
    public Pose getCurrentSetpoint() {
        TrajectorySetpoint trajectorySetpoint = mDistanceController.getSetpoint();
        double dist = trajectorySetpoint.pos;
        double velocity = trajectorySetpoint.vel;
        return new Pose(
                mSetpointRelativePose.getLeftDistance() + dist,
                mSetpointRelativePose.getRightDistance() + dist,
                mSetpointRelativePose.getLeftVelocity() + velocity,
                mSetpointRelativePose.getRightVelocity() + velocity,
                mSetpointRelativePose.getHeading(),
                mSetpointRelativePose.getHeadingVelocity());
    }

    public static double encoderVelocity(Pose pose) {
        return (pose.getLeftVelocity() + pose.getRightVelocity()) / 2.0;
    }

    public static double encoderDistance(Pose pose) {
        return (pose.getLeftDistance() + pose.getRightDistance()) / 2.0;
    }

    @Override
    public boolean onTarget() {
        return mDistanceController.isOnTarget();
    }

}
