package com.palyrobotics.frc2017.util.archive.team254.controllers.team254;

import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.util.archive.LegacyDrive;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.util.archive.team254.trajectory.TrajectoryFollower;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.palyrobotics.frc2017.robot.team254.lib.util.LegacyPose;

/**
 * Controls the robot to turn in place
 */
public class TurnInPlaceController implements LegacyDrive.DriveController {
    private final TrajectoryFollowingPositionController mController;
    private final LegacyPose mSetpointRelativePose;

    public TurnInPlaceController(LegacyPose poseToContinueFrom, double destHeading, double velocity) {
        TrajectoryFollower.TrajectoryConfig config = new TrajectoryFollower.TrajectoryConfig();
        config.dt = Constants.kControlLoopsDt;
        config.max_acc = Constants2016.kTurnMaxAccelRadsPerSec2;
        config.max_vel = velocity;
        mController = new TrajectoryFollowingPositionController(
                Constants2016.kTurnKp,
                Constants2016.kTurnKi,
                Constants2016.kTurnKd,
                Constants2016.kTurnKv,
                Constants2016.kTurnKa,
                Constants2016.kTurnOnTargetError,
                config);
        TrajectoryFollower.TrajectorySetpoint initialSetpoint = new TrajectoryFollower.TrajectorySetpoint();
        initialSetpoint.pos = poseToContinueFrom.getHeading();
        initialSetpoint.vel = poseToContinueFrom.getHeadingVelocity();
        mController.setGoal(initialSetpoint, destHeading);

        mSetpointRelativePose = poseToContinueFrom;
    }

    @Override
    public DriveSignal update(LegacyPose pose) {
    	System.out.println(pose.getHeading()+" "+mController.m_error+" "+pose.getHeadingVelocity()+" "+mController.get());
        mController.update(pose.getHeading(), pose.getHeadingVelocity());
        double turn = mController.get();
        DriveSignal output = DriveSignal.getNeutralSignal();
        output.leftMotor.setPercentVBus(turn);
        output.rightMotor.setPercentVBus(-turn);
        return output;
    }

    @Override
    public LegacyPose getCurrentSetpoint() {
        TrajectoryFollower.TrajectorySetpoint setpoint = mController.getSetpoint();
        // TODO: these encoder values are wrong, but this isn't a controller I want to use anyways
        return new LegacyPose(
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

