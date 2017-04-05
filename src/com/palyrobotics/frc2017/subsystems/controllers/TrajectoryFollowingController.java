package com.palyrobotics.frc2017.subsystems.controllers;

import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.team254.lib.util.ChezyMath;
import com.palyrobotics.frc2017.subsystems.Drive;
import com.palyrobotics.frc2017.util.Pose;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.Trajectory;
import com.team254.lib.trajectory.TrajectoryFollower;

/**
 * Created by Nihar on 4/5/17.
 */
public class TrajectoryFollowingController implements Drive.DriveController {
	private Trajectory mTrajectory;
	private TrajectoryFollower mLeftFollower = new TrajectoryFollower();
	private TrajectoryFollower mRightFollower = new TrajectoryFollower();

	private boolean mGyroCorrection;

	public TrajectoryFollowingController(Path path, boolean correctUsingGyro) {
		mLeftFollower.setTrajectory(path.getLeftWheelTrajectory());
		mLeftFollower.configure(Gains.kSteikTrajectorykP, Gains.kSteikTrajectorykI, Gains.kSteikTrajectorykD,
				Gains.kSteikTrajectorykV, Gains.kSteikTrajectorykA);
		mRightFollower.setTrajectory(path.getRightWheelTrajectory());
		mRightFollower.configure(Gains.kSteikTrajectorykP, Gains.kSteikTrajectorykI, Gains.kSteikTrajectorykD,
				Gains.kSteikTrajectorykV, Gains.kSteikTrajectorykA);
		this.mGyroCorrection = correctUsingGyro;
	}

	@Override
	public DriveSignal update(RobotState state) {
		if (onTarget()) {
			return DriveSignal.getNeutralSignal();
		}
		DriveSignal driveSignal = DriveSignal.getNeutralSignal();

		double leftPower = mLeftFollower.calculate(state.drivePose.leftEnc);
		double rightPower = mRightFollower.calculate(state.drivePose.rightEnc);

		if (!mGyroCorrection) {
			driveSignal.leftMotor.setPercentVBus(leftPower);
			driveSignal.rightMotor.setPercentVBus(rightPower);
		} else {
			double gyroError = ChezyMath.getDifferenceInAngleRadians(Math.toRadians(state.drivePose.heading), mLeftFollower.getHeading());
			gyroError = Math.toDegrees(gyroError);
			driveSignal.leftMotor.setPercentVBus(leftPower+Gains.kSteikTrajectoryTurnkP*gyroError);
			driveSignal.rightMotor.setPercentVBus(rightPower-Gains.kSteikTrajectoryTurnkP*gyroError);
		}
		return driveSignal;
	}

	@Override
	public Pose getSetpoint() {
		// TODO: what to return?
		return null;
	}

	@Override
	public boolean onTarget() {
		return mLeftFollower.isFinishedTrajectory() && mRightFollower.isFinishedTrajectory();
	}
}
