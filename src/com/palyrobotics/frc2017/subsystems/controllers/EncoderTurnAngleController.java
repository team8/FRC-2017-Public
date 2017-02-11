package com.palyrobotics.frc2017.subsystems.controllers;

import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.util.LegacyDrive;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.util.DriveSignal;
import com.palyrobotics.frc2017.robot.team254.lib.util.Pose;

public class EncoderTurnAngleController implements LegacyDrive.DriveController {
	private Pose mCachedPose;

	private double mMaxVel;
	private double mLeftTarget;
	private double mRightTarget;
	private double mLeftSpeed;
	private double mRightSpeed;
	
	private final double kDegreeToDistance;
	
	private double mLeftP;
	private double mLeftI;
	private double mLeftD;
	
	private double mRightP;
	private double mRightI;
	private double mRightD;
	
	public EncoderTurnAngleController(Pose priorSetpoint, double angle, double maxVel) {
		this.mMaxVel = maxVel;
		
		if(Constants.kRobotName == Constants.RobotName.DERICA) {
			kDegreeToDistance = Constants2016.kDericaDegreeToDistance;
		} else if(Constants.kRobotName == Constants.RobotName.TYR) {
			kDegreeToDistance = Constants2016.kTyrDegreeToDistance;
		} else {
			kDegreeToDistance = 1;
		}
		
		mLeftTarget = priorSetpoint.getLeftDistance() + angle * kDegreeToDistance;
		mRightTarget = priorSetpoint.getRightDistance() - angle * kDegreeToDistance;
	}
	
	@Override
	public DriveSignal update(Pose pose) {
		mLeftP = mLeftTarget - pose.getLeftDistance();
		mRightP = mRightTarget - pose.getRightDistance();
		
		mLeftI = mLeftI + mLeftP * Constants.kControlLoopsDt;
		mRightI = mRightI + mRightP * Constants.kControlLoopsDt;
		
		mLeftD = -pose.getLeftVelocity();
		mRightD = -pose.getRightVelocity();
		
		mLeftSpeed = Math.max(-mMaxVel, 
				Math.min(mMaxVel, Constants.kEncoderTurnKp * mLeftP + Constants.kEncoderTurnKi * mLeftI + Constants.kEncoderTurnKd * mLeftD));
		mRightSpeed = Math.max(-mMaxVel, 
				Math.min(mMaxVel, Constants.kEncoderTurnKp * mRightP + Constants.kEncoderTurnKi * mRightI + Constants.kEncoderTurnKd * mRightD));
		DriveSignal output = DriveSignal.getNeutralSignal();
		output.leftMotor.setPercentVBus(mLeftSpeed);
		output.rightMotor.setPercentVBus(mRightSpeed);
		return output;
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
				mLeftTarget,
				mRightTarget,
				mLeftSpeed,
				mRightSpeed,
				mCachedPose.getHeading(),
				mCachedPose.getHeadingVelocity());
	}

	@Override
	public boolean onTarget() {
		if(Math.abs(mLeftP / kDegreeToDistance) < Constants.kAcceptableEncoderTurnError && 
				Math.abs(mRightP / kDegreeToDistance) < Constants.kAcceptableEncoderTurnError && mLeftD == 0 && mRightD == 0) {
			return true;
		} else return false;
	}

}
