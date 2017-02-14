package com.palyrobotics.frc2017.util.archive;

import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.util.archive.LegacyDrive;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.palyrobotics.frc2017.robot.team254.lib.util.LegacyPose;

public class EncoderTurnAngleController implements LegacyDrive.DriveController {
	private LegacyPose mCachedPose;

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
	
	public EncoderTurnAngleController(LegacyPose priorSetpoint, double angle, double maxVel) {
		this.mMaxVel = maxVel;
		
		if(Constants.kRobotName == Constants.RobotName.DERICA) {
			kDegreeToDistance = Constants2016.kDericaDegreeToDistance;
		} else {
			kDegreeToDistance = Constants.kDegreeToDistance;
		}
		
		mLeftTarget = priorSetpoint.getLeftDistance() + angle * kDegreeToDistance;
		mRightTarget = priorSetpoint.getRightDistance() - angle * kDegreeToDistance;
	}
	
	@Override
	public DriveSignal update(LegacyPose pose) {
		mLeftP = mLeftTarget - pose.getLeftDistance();
		mRightP = mRightTarget - pose.getRightDistance();
		
		mLeftI = mLeftI + mLeftP * Constants.kControlLoopsDt;
		mRightI = mRightI + mRightP * Constants.kControlLoopsDt;
		
		mLeftD = -pose.getLeftVelocity();
		mRightD = -pose.getRightVelocity();
		
		mLeftSpeed = Math.max(-mMaxVel, 
				Math.min(mMaxVel, Constants2016.kEncoderTurnKp * mLeftP + Constants2016.kEncoderTurnKi * mLeftI + Constants2016.kEncoderTurnKd * mLeftD));
		mRightSpeed = Math.max(-mMaxVel, 
				Math.min(mMaxVel, Constants2016.kEncoderTurnKp * mRightP + Constants2016.kEncoderTurnKi * mRightI + Constants2016.kEncoderTurnKd * mRightD));
		DriveSignal output = DriveSignal.getNeutralSignal();
		output.leftMotor.setPercentVBus(mLeftSpeed);
		output.rightMotor.setPercentVBus(mRightSpeed);
		return output;
	}

	@Override
	public LegacyPose getCurrentSetpoint() {
		return new LegacyPose(
				mLeftTarget,
				mRightTarget,
				mLeftSpeed,
				mRightSpeed,
				mCachedPose.getHeading(),
				mCachedPose.getHeadingVelocity());
	}

	@Override
	public boolean onTarget() {
		if(Math.abs(mLeftP / kDegreeToDistance) < Constants2016.kAcceptableEncoderTurnError &&
				Math.abs(mRightP / kDegreeToDistance) < Constants2016.kAcceptableEncoderTurnError && mLeftD == 0 && mRightD == 0) {
			return true;
		} else return false;
	}

}
