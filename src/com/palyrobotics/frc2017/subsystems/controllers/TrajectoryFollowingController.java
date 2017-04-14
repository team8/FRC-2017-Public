package com.palyrobotics.frc2017.subsystems.controllers;

import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.team254.lib.util.ChezyMath;
import com.palyrobotics.frc2017.subsystems.Drive;
import com.palyrobotics.frc2017.util.Pose;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.palyrobotics.frc2017.util.archive.team254.trajectory.LegacyTrajectoryFollower;
import com.team254.lib.trajectory.Path;

/**
 * Created by Nihar on 4/5/17.
 */
public class TrajectoryFollowingController implements Drive.DriveController {
	private LegacyTrajectoryFollower mLeftFollower = new LegacyTrajectoryFollower("left");
	private LegacyTrajectoryFollower mRightFollower = new LegacyTrajectoryFollower("right");

	private final Gains mGains;
	private boolean mGyroCorrection;
	private boolean mIllegalPath;

	public TrajectoryFollowingController(Path path, Gains gains, boolean correctUsingGyro, boolean inverted) {
		mGains = gains;
		
		// set trajectory gains
		mLeftFollower.configure(gains.P, gains.I, gains.D,
				Gains.kSteikTrajectorykV, Gains.kSteikTrajectorykA);
		mRightFollower.configure(gains.P, gains.I, gains.D,
				Gains.kSteikTrajectorykV, Gains.kSteikTrajectorykA);

		// set goals and paths
		if (path == null) {
			mIllegalPath = true;
			System.out.println("No path!");
			return;
		} else {
			mIllegalPath = false;
		}
		path.getRightWheelTrajectory().setInvertedY(inverted);
		path.getLeftWheelTrajectory().setInvertedY(inverted);
		mRightFollower.setTrajectory(path.getRightWheelTrajectory());
		mLeftFollower.setTrajectory(path.getLeftWheelTrajectory());
		
		mGyroCorrection = correctUsingGyro;
	}

	@Override
	public DriveSignal update(RobotState state) {
		if (mIllegalPath) {
			return DriveSignal.getNeutralSignal();
		}
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
		return !mIllegalPath | mLeftFollower.isFinishedTrajectory() && mRightFollower.isFinishedTrajectory();
	}
}
