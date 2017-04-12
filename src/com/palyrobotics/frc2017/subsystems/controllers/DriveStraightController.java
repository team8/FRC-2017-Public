package com.palyrobotics.frc2017.subsystems.controllers;

import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.config.Constants.RobotName;
import com.palyrobotics.frc2017.config.Constants2016;
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
	
	private final double kTolerance;
	
	public DriveStraightController(Pose priorSetpoint, double distance) {
		target = (priorSetpoint.leftEnc + priorSetpoint.rightEnc)/2 + (distance * Constants.kDriveTicksPerInch);
		System.out.println("Target: "+target);
		cachedPose = priorSetpoint;
		
		mGains = new Gains(.00035, 0.001, 0.0015, 0, 0, 0);
		kTolerance = (Constants.kRobotName == RobotName.DERICA) ? Constants2016.kAcceptableDriveError : Constants.kAcceptableDrivePositionError;
		forwardPID = new SynchronousPID(mGains.P, mGains.I, mGains.D);
		headingPID = new SynchronousPID(Gains.kSteikTrajectoryTurnkP, 0, 0.005);
		forwardPID.setOutputRange(-1, 1);
		headingPID.setOutputRange(-0.2, 0.2);
		forwardPID.setSetpoint(target);
		headingPID.setSetpoint(priorSetpoint.heading);
		
	}

	@Override
	public boolean onTarget() {
		if (cachedPose == null) {
			System.out.println("Cached pose is null");
			return false;
		}
		
		return Math.abs(Robot.getRobotState().drivePose.heading) < kTolerance &&
				Math.abs((Robot.getRobotState().drivePose.leftEnc + Robot.getRobotState().drivePose.rightEnc)/2  - target) < kTolerance
				&& Math.abs(Robot.getRobotState().drivePose.leftSpeed)<0.01
				&& Math.abs(Robot.getRobotState().drivePose.rightSpeed)<0.01;
	}
	

	@Override
	public DriveSignal update(RobotState state) {
		CANTalonOutput leftOutput = new CANTalonOutput();
		CANTalonOutput rightOutput = new CANTalonOutput();
		cachedPose = state.drivePose;
		double distanceSoFar = state.drivePose.leftEnc+state.drivePose.rightEnc;
		distanceSoFar /= 2;
		double throttle = forwardPID.calculate(distanceSoFar);
		double turn = headingPID.calculate(state.drivePose.heading) * Constants.kDriveInchesPerDegree;
		
		leftOutput.setPercentVBus(throttle + turn);
		rightOutput.setPercentVBus(throttle - turn);
		
		System.out.println("Left enc: " + state.drivePose.leftEnc + " Right enc: " + state.drivePose.rightEnc);
//		System.out.println("Left: " + leftOutput.getSetpoint() + "Right: " + rightOutput.getSetpoint());
			
		return new DriveSignal(leftOutput, rightOutput);
	}

	@Override
	public Pose getSetpoint() {
		return new Pose(target, 0, 0, target, 0, 0, 0, 0, 0, 0);
	}

}
