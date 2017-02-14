package com.palyrobotics.frc2017.util.archive.team254.controllers.team254;

import static com.palyrobotics.frc2017.util.archive.team254.trajectory.TrajectoryFollower.TrajectorySetpoint;

import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.util.archive.LegacyDrive;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.util.archive.team254.trajectory.TrajectoryFollower;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.palyrobotics.frc2017.robot.team254.lib.util.LegacyPose;
import com.palyrobotics.frc2017.robot.team254.lib.util.SynchronousPID;

public class DriveStraightController implements LegacyDrive.DriveController {

    private TrajectoryFollowingPositionController mDistanceController;
    private SynchronousPID mTurnPid;
    private LegacyPose mSetpointRelativePose;

    public DriveStraightController(LegacyPose priorSetpoint, double goalSetpoint, double maxVelocity) {
        TrajectoryFollower.TrajectoryConfig config = new TrajectoryFollower.TrajectoryConfig();
        config.dt = Constants.kControlLoopsDt;
        config.max_acc = Constants2016.kDriveMaxAccelInchesPerSec2;
        config.max_vel = maxVelocity;

        mDistanceController = new TrajectoryFollowingPositionController(
                Constants2016.kDrivePositionKp,
                Constants2016.kDrivePositionKi,
                Constants2016.kDrivePositionKd,
                Constants2016.kDrivePositionKv,
                Constants2016.kDrivePositionKa,
                Constants2016.kDriveOnTargetError,
                config);

        TrajectorySetpoint initialSetpoint = new TrajectorySetpoint();
        initialSetpoint.pos = encoderDistance(priorSetpoint);
        initialSetpoint.vel = encoderVelocity(priorSetpoint);
        mDistanceController.setGoal(initialSetpoint, goalSetpoint);

        mTurnPid = new SynchronousPID();
        mTurnPid.setPID(
                Constants2016.kDriveStraightKp,
                Constants2016.kDriveStraightKi,
                Constants2016.kDriveStraightKd);
        mTurnPid.setSetpoint(priorSetpoint.getHeading());
        mSetpointRelativePose = new LegacyPose(
                priorSetpoint.getLeftDistance(),
                priorSetpoint.getRightDistance(),
                0,
                0,
                priorSetpoint.getHeading(),
                priorSetpoint.getHeadingVelocity());
    }

    @Override
    public DriveSignal update(LegacyPose currentPose) {
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
    public LegacyPose getCurrentSetpoint() {
        TrajectorySetpoint trajectorySetpoint = mDistanceController.getSetpoint();
        double dist = trajectorySetpoint.pos;
        double velocity = trajectorySetpoint.vel;
        return new LegacyPose(
                mSetpointRelativePose.getLeftDistance() + dist,
                mSetpointRelativePose.getRightDistance() + dist,
                mSetpointRelativePose.getLeftVelocity() + velocity,
                mSetpointRelativePose.getRightVelocity() + velocity,
                mSetpointRelativePose.getHeading(),
                mSetpointRelativePose.getHeadingVelocity());
    }

    public static double encoderVelocity(LegacyPose pose) {
        return (pose.getLeftVelocity() + pose.getRightVelocity()) / 2.0;
    }

    public static double encoderDistance(LegacyPose pose) {
        return (pose.getLeftDistance() + pose.getRightDistance()) / 2.0;
    }

    @Override
    public boolean onTarget() {
        return mDistanceController.isOnTarget();
    }

}
