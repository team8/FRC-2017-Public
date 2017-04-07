package com.palyrobotics.frc2017.subsystems.controllers;

import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.Robot;
import com.palyrobotics.frc2017.robot.team254.lib.util.SynchronousPID;
import com.palyrobotics.frc2017.subsystems.Drive.DriveController;
import com.palyrobotics.frc2017.util.CANTalonOutput;
import com.palyrobotics.frc2017.util.Pose;
import com.palyrobotics.frc2017.util.archive.DriveSignal;

public class DriveStraightController implements DriveController {

	private Pose cachedPose;
	private double target;
	private Gains mGains;
	
	private SynchronousPID forwardPID;
	private SynchronousPID headingPID;
	
	private final double kTolerance = 1;
	
	public DriveStraightController(Pose priorSetpoint, double distance) {
		target = priorSetpoint.leftEnc + (distance * Constants.kDriveTicksPerInch);
		System.out.println("Target: "+target);
		cachedPose = priorSetpoint;
		
		if(Constants.kRobotName.equals(Constants.RobotName.STEIK)) {
			mGains = Gains.steikTurnMotionMagicGains;
		} else {
			mGains = Gains.dericaPosition;
		}
		
		forwardPID = new SynchronousPID(mGains.P, mGains.I, mGains.D);
		headingPID = new SynchronousPID(mGains.P, mGains.I, mGains.D);
		forwardPID.setOutputRange(-1, 1);
		headingPID.setOutputRange(-1, 1);
		forwardPID.setSetpoint(distance);
		forwardPID.setSetpoint(target);
		headingPID.setSetpoint(0);
		
	}

	@Override
	public boolean onTarget() {
		if (cachedPose == null) {
			System.out.println("Cached pose is null");
			return false;
		}
		
		return Math.abs(Robot.getRobotState().drivePose.heading) < kTolerance &&
				Math.abs((Robot.getRobotState().drivePose.leftEnc + Robot.getRobotState().drivePose.rightEnc)/2  - target) < kTolerance;
	}
	

	@Override
	public DriveSignal update(RobotState state) {
		CANTalonOutput leftOutput = new CANTalonOutput();
		CANTalonOutput rightOutput = new CANTalonOutput();
		cachedPose = state.drivePose;
		
		double forward = forwardPID.calculate(state.drivePose.leftEnc * Constants.kDriveTicksPerInch);
		double angle = headingPID.calculate(state.drivePose.heading);
		
		leftOutput.setPercentVBus(forward + angle);
		rightOutput.setPercentVBus(forward - angle);
			
		return new DriveSignal(leftOutput, rightOutput);
	}

	@Override
	public Pose getSetpoint() {
		return new Pose(target, 0, 0, target, 0, 0, 0, 0, 0, 0);
	}

}
