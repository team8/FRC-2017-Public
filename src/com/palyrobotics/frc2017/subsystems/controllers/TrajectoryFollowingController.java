package com.palyrobotics.frc2017.subsystems.controllers;

import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.team254.lib.util.ChezyMath;
import com.palyrobotics.frc2017.subsystems.Drive;
import com.palyrobotics.frc2017.util.Pose;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.palyrobotics.frc2017.util.archive.team254.trajectory.LegacyTrajectoryFollower;
import com.palyrobotics.frc2017.util.archive.team254.trajectory.TrajectoryFollower;
import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.Trajectory;

/**
 * Created by Nihar on 4/5/17.
 */
public class TrajectoryFollowingController implements Drive.DriveController {
	private LegacyTrajectoryFollower mLeftFollower = new LegacyTrajectoryFollower("left");
	private LegacyTrajectoryFollower mRightFollower = new LegacyTrajectoryFollower("right");

	private boolean mGyroCorrection;

	public TrajectoryFollowingController(Path path, boolean correctUsingGyro) {
		// set trajectory gains
		mLeftFollower.configure(Gains.kSteikTrajectorykP, Gains.kSteikTrajectorykI, Gains.kSteikTrajectorykD,
				Gains.kSteikTrajectorykV, Gains.kSteikTrajectorykA);
		mRightFollower.configure(Gains.kSteikTrajectorykP, Gains.kSteikTrajectorykI, Gains.kSteikTrajectorykD,
				Gains.kSteikTrajectorykV, Gains.kSteikTrajectorykA);

		// set goals and paths
		mRightFollower.setTrajectory(path.getRightWheelTrajectory());
		mLeftFollower.setTrajectory(path.getLeftWheelTrajectory());
		this.mGyroCorrection = correctUsingGyro;
	}

	@Override
	public DriveSignal update(RobotState state) {
		if (onTarget()) {
			return DriveSignal.getNeutralSignal();
		}
		DriveSignal driveSignal = DriveSignal.getNeutralSignal();

		double leftPower = mLeftFollower.calculate(state.drivePose.leftEnc/Constants.kDriveTicksPerInch/12);
		double rightPower = mRightFollower.calculate(state.drivePose.rightEnc/Constants.kDriveTicksPerInch/12);

		if (!mGyroCorrection) {
			driveSignal.leftMotor.setPercentVBus(leftPower);
			driveSignal.rightMotor.setPercentVBus(rightPower);
		} else {
			double gyroError = ChezyMath.getDifferenceInAngleRadians(Math.toRadians(state.drivePose.heading), mLeftFollower.getHeading());
			gyroError = Math.toDegrees(gyroError);
			System.err.println("Using gyro!");
			driveSignal.leftMotor.setPercentVBus(leftPower+Gains.kSteikTrajectoryTurnkP*gyroError);
			driveSignal.rightMotor.setPercentVBus(rightPower-Gains.kSteikTrajectoryTurnkP*gyroError);
		}
		System.out.println(driveSignal.toString());
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
