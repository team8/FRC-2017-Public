package com.palyrobotics.frc2017.util.archive.team254.controllers.team254;

import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.util.archive.LegacyDrive;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.palyrobotics.frc2017.robot.team254.lib.util.LegacyPose;
import com.palyrobotics.frc2017.robot.team254.lib.util.SynchronousPID;

public class DriveFinishLineController implements LegacyDrive.DriveController {
	BangBangFinishLineController m_controller;

	LegacyPose m_setpoint = new LegacyPose(0, 0, 0, 0, 0, 0);
	double m_heading = 0;
	private SynchronousPID mTurnPid;
	public DriveSignal mSignal = DriveSignal.getNeutralSignal();

	public DriveFinishLineController(double distance, double heading, double tolerance) {
		m_controller = new BangBangFinishLineController(tolerance);
		m_controller.setGoal(distance);
		m_heading = heading;
		mTurnPid = new SynchronousPID();
		mTurnPid.setPID(
				Constants2016.kDriveStraightKp,
				Constants2016.kDriveStraightKi,
				Constants2016.kDriveStraightKd);
		mTurnPid.setSetpoint(heading);
	}

	@Override
	public DriveSignal update(LegacyPose pose) {
		m_setpoint = pose;
		double position = (pose.getLeftDistance() + pose.getRightDistance()) / 2.0;
		double throttle = m_controller.update(position);
		double turn = mTurnPid.calculate(pose.getHeading());
		mSignal.leftMotor.setPercentVBus(throttle + turn);
		mSignal.rightMotor.setPercentVBus(throttle - turn);
		return mSignal;
	}

	@Override
	public LegacyPose getCurrentSetpoint() {
		return m_setpoint;
	}

	@Override
	public boolean onTarget() {
		return m_controller.isOnTarget();
	}
}
