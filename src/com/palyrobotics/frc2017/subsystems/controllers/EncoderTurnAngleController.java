package com.palyrobotics.frc2017.subsystems.controllers;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.Robot;
import com.palyrobotics.frc2017.subsystems.Drive.DriveController;
import com.palyrobotics.frc2017.util.CANTalonOutput;
import com.palyrobotics.frc2017.util.Pose;
import com.palyrobotics.frc2017.util.archive.DriveSignal;

public class EncoderTurnAngleController implements DriveController {

	private Pose cachedPose;
	private double leftTarget;
	private double rightTarget;
	private double maxAccel;
	private double maxVel;
	private Gains mGains;
	private CANTalonOutput leftOutput;
	private CANTalonOutput rightOutput;
	
	public EncoderTurnAngleController(Pose priorSetpoint, double angle) {
		leftTarget = priorSetpoint.leftEnc + (angle * Constants.kDriveInchesPerDegree * Constants.kDriveTicksPerInch);
		System.out.println("Left target: "+leftTarget);
		rightTarget = priorSetpoint.rightEnc - (angle * Constants.kDriveInchesPerDegree * Constants.kDriveTicksPerInch);
		System.out.println("Right target: "+rightTarget);
		cachedPose = priorSetpoint;
		this.maxAccel = (Constants.kRobotName == Constants.RobotName.DERICA) ? Gains.kDericaTurnMotionMagicCruiseVelocity : Gains.kSteikTurnMotionMagicCruiseVelocity;
		this.maxVel = (Constants.kRobotName == Constants.RobotName.DERICA) ?  Gains.kDericaTurnMotionMagicCruiseAccel : Gains.kSteikTurnMotionMagicMaxAcceleration;

		if(Constants.kRobotName.equals(Constants.RobotName.STEIK)) {
			mGains = Gains.steikTurnMotionMagicGains;
		} else {
			mGains = Gains.dericaPosition;
		}

		leftOutput = new CANTalonOutput();
		leftOutput.setMotionMagic(leftTarget, mGains, maxVel, maxAccel);
		rightOutput = new CANTalonOutput();
		rightOutput.setMotionMagic(rightTarget, mGains, maxVel, maxAccel);
	}

	@Override
	public boolean onTarget() {
		if(Robot.getRobotState().leftSetpoint != leftOutput.getSetpoint() || Robot.getRobotState().rightSetpoint != rightOutput.getSetpoint() ||
				Robot.getRobotState().leftControlMode != leftOutput.getControlMode() || Robot.getRobotState().rightControlMode != rightOutput.getControlMode()) {
			System.out.println("Mismatched desired talon and actual talon states!");
			return false;
		}

		double positionTolerance = ((Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kAcceptableDrivePositionError : Constants.kAcceptableTurnAngleError) * 
				Constants.kDriveInchesPerDegree * Constants.kDriveTicksPerInch;
		double velocityTolerance = (Constants.kRobotName == Constants.RobotName.DERICA) ? Constants2016.kAcceptableDriveVelocityError : Constants.kAcceptableDriveVelocityError;

		if(cachedPose == null) {
			System.out.println("Cached pose is null");
			return false;
		}
//		System.out.println("Left: " + Math.abs(leftTarget - cachedPose.leftEnc) + 
//				"Right: " + Math.abs(rightTarget - cachedPose.rightEnc));
		if(Math.abs(cachedPose.leftSpeed) < velocityTolerance && Math.abs(cachedPose.rightSpeed) < velocityTolerance &&
				Math.abs(leftTarget - cachedPose.leftEnc) < positionTolerance && Math.abs(rightTarget - cachedPose.rightEnc) < positionTolerance) {
			return true;
		}
		else return false;
	}

	@Override
	public DriveSignal update(RobotState state) {
		cachedPose = state.drivePose;
		return new DriveSignal(leftOutput, rightOutput);
		
	}

	@Override
	public Pose getSetpoint() {
		return new Pose(leftTarget, 0, 0, rightTarget, 0, 0, 0, 0, 0, 0);
	}

}
