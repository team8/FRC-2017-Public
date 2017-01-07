package com.palyrobotics.frc2016.subsystems.controllers;

import com.palyrobotics.frc2016.config.RobotState;
import com.palyrobotics.frc2016.robot.Robot;
import com.palyrobotics.frc2016.subsystems.Drive;
import com.palyrobotics.frc2016.config.Constants;
import com.palyrobotics.frc2016.robot.team254.lib.util.DriveSignal;
import com.palyrobotics.frc2016.robot.team254.lib.util.Pose;

public class EncoderTurnAngleController implements Drive.DriveController {
	private Pose mCachedPose;

	private double maxVel;
	private double leftTarget;
	private double rightTarget;
	private double leftSpeed;
	private double rightSpeed;
	private double kDegreeToDistance;
	
	private double leftP;
	private double leftI;
	private double leftD;
	
	private double rightP;
	private double rightI;
	private double rightD;
	
	public EncoderTurnAngleController(Pose priorSetpoint, double angle, double maxVel) {
		this.maxVel = maxVel;
		
		if(Constants.kRobotName == Constants.RobotName.DERICA) {
			kDegreeToDistance = Constants.kDericaDegreeToDistance;
		} else if(Constants.kRobotName == Constants.RobotName.TYR) {
			kDegreeToDistance = Constants.kTyrDegreeToDistance;
		}
		
		leftTarget = priorSetpoint.getLeftDistance() + angle * kDegreeToDistance;
		rightTarget = priorSetpoint.getRightDistance() - angle * kDegreeToDistance;
	}
	
	@Override
	public DriveSignal update(Pose pose) {
		leftP = leftTarget - pose.getLeftDistance();
		rightP = rightTarget - pose.getRightDistance();
		
		leftI = leftI + leftP * Constants.kControlLoopsDt;
		rightI = rightI + rightP * Constants.kControlLoopsDt;
		
		leftD = -pose.getLeftVelocity();
		rightD = -pose.getRightVelocity();
		
		leftSpeed = Math.max(-maxVel, 
				Math.min(maxVel, Constants.kEncoderTurnKp*leftP + Constants.kEncoderTurnKi*leftI + Constants.kEncoderTurnKd*leftD));
		rightSpeed = Math.max(-maxVel, 
				Math.min(maxVel, Constants.kEncoderTurnKp*rightP + Constants.kEncoderTurnKi*rightI + Constants.kEncoderTurnKd*rightD));
		
		return new DriveSignal(leftSpeed, rightSpeed);
	}

	@Override
	public Pose getCurrentSetpoint() {
//		return new Pose(
//				HardwareAdaptor.kDrive.m_left_encoder.getDistance(),//leftTarget,
//				HardwareAdaptor.kDrive.m_right_encoder.getDistance(),//rightTarget,
//				HardwareAdaptor.kDrive.m_left_encoder.getRate(),//leftSpeed,
//				HardwareAdaptor.kDrive.m_right_encoder.getRate(),//rightSpeed,
//				Math.toRadians(HardwareAdaptor.kDrive.m_gyro.getAngle()),
//				HardwareAdaptor.kDrive.m_gyro.getRate());
		return new Pose(
				leftTarget,
				rightTarget,
				leftSpeed,
				rightSpeed,
				mCachedPose.getHeading(),
				mCachedPose.getHeadingVelocity());
	}

	@Override
	public boolean onTarget() {
		if(Math.abs(leftP/kDegreeToDistance) < Constants.kAcceptableEncoderTurnError && 
				Math.abs(rightP/kDegreeToDistance) < Constants.kAcceptableEncoderTurnError && leftD == 0 && rightD == 0) {
			return true;
		} else return false;
	}

}
