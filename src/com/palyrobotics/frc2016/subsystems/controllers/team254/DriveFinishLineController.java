package com.palyrobotics.frc2016.subsystems.controllers.team254;

import com.palyrobotics.frc2016.subsystems.Drive;
import com.palyrobotics.frc2016.config.Constants;
import com.palyrobotics.frc2016.util.DriveSignal;
import com.palyrobotics.frc2016.robot.team254.lib.util.Pose;
import com.palyrobotics.frc2016.robot.team254.lib.util.SynchronousPID;

public class DriveFinishLineController implements Drive.DriveController {
	BangBangFinishLineController m_controller;

	Pose m_setpoint = new Pose(0, 0, 0, 0, 0, 0);
	double m_heading = 0;
	private SynchronousPID mTurnPid;
	public DriveSignal mSignal = DriveSignal.getNeutralSignal();

	public DriveFinishLineController(double distance, double heading, double tolerance) {
		m_controller = new BangBangFinishLineController(tolerance);
		m_controller.setGoal(distance);
		m_heading = heading;
		mTurnPid = new SynchronousPID();
		mTurnPid.setPID(
				Constants.kDriveStraightKp,
				Constants.kDriveStraightKi,
				Constants.kDriveStraightKd);
		mTurnPid.setSetpoint(heading);
	}

	@Override
	public DriveSignal update(Pose pose) {
		m_setpoint = pose;
		double position = (pose.getLeftDistance() + pose.getRightDistance()) / 2.0;
		double throttle = m_controller.update(position);
		double turn = mTurnPid.calculate(pose.getHeading());
		mSignal.leftMotor.setPercentVBus(throttle + turn);
		mSignal.rightMotor.setPercentVBus(throttle - turn);
		return mSignal;
	}

	@Override
	public Pose getCurrentSetpoint() {
		return m_setpoint;
	}

	@Override
	public boolean onTarget() {
		return m_controller.isOnTarget();
	}
}
