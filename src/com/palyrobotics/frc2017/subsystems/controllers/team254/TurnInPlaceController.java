package com.palyrobotics.frc2017.subsystems.controllers.team254;

import com.palyrobotics.frc2017.util.LegacyDrive;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.robot.team254.lib.trajectory.TrajectoryFollower;
import com.palyrobotics.frc2017.util.DriveSignal;
import com.palyrobotics.frc2017.robot.team254.lib.util.Pose;

/**
 * Controls the robot to turn in place
 */
public class TurnInPlaceController implements LegacyDrive.DriveController {
    private final TrajectoryFollowingPositionController mController;
    private final Pose mSetpointRelativePose;

    public TurnInPlaceController(Pose poseToContinueFrom, double destHeading, double velocity) {
        TrajectoryFollower.TrajectoryConfig config = new TrajectoryFollower.TrajectoryConfig();
        config.dt = Constants.kControlLoopsDt;
        config.max_acc = Constants.kTurnMaxAccelRadsPerSec2;
        config.max_vel = velocity;
        mController = new TrajectoryFollowingPositionController(
                Constants.kTurnKp,
                Constants.kTurnKi,
                Constants.kTurnKd,
                Constants.kTurnKv,
                Constants.kTurnKa,
                Constants.kTurnOnTargetError,
                config);
        TrajectoryFollower.TrajectorySetpoint initialSetpoint = new TrajectoryFollower.TrajectorySetpoint();
        initialSetpoint.pos = poseToContinueFrom.getHeading();
        initialSetpoint.vel = poseToContinueFrom.getHeadingVelocity();
        mController.setGoal(initialSetpoint, destHeading);

        mSetpointRelativePose = poseToContinueFrom;
    }

    @Override
    public DriveSignal update(Pose pose) {
    	System.out.println(pose.getHeading()+" "+mController.m_error+" "+pose.getHeadingVelocity()+" "+mController.get());
        mController.update(pose.getHeading(), pose.getHeadingVelocity());
        double turn = mController.get();
        DriveSignal output = DriveSignal.getNeutralSignal();
        output.leftMotor.setPercentVBus(turn);
        output.rightMotor.setPercentVBus(-turn);
        return output;
    }

    @Override
    public Pose getCurrentSetpoint() {
        TrajectoryFollower.TrajectorySetpoint setpoint = mController.getSetpoint();
        // TODO: these encoder values are wrong, but this isn't a controller I want to use anyways
        return new Pose(
                mSetpointRelativePose.getLeftDistance(),
                mSetpointRelativePose.getRightDistance(),
                mSetpointRelativePose.getLeftVelocity(),
                mSetpointRelativePose.getRightVelocity(),
                setpoint.pos,
                setpoint.vel);
    }

    @Override
    public boolean onTarget() {
        return mController.isOnTarget();
    }

    public double getHeadingGoal() {
        return mController.getGoal();
    }
}

