package com.palyrobotics.frc2016.behavior.routines;

import com.palyrobotics.frc2016.behavior.Routine;
import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.robot.team254.lib.util.DriveSignal;
import com.palyrobotics.frc2016.subsystems.Drive;
import com.palyrobotics.frc2016.util.Subsystem;

public class BBTurnAngleRoutine extends Routine {

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{drive};
	}
	
	private double mAngle;
	
	private States mState = States.START;
	
	private enum States {
		START, TURNING, DONE
	}
	
	public BBTurnAngleRoutine(double angle) {
		this.mAngle = angle;
	}
	
	@Override
	public void start() {
		drive.setOpenLoop(DriveSignal.NEUTRAL);
		mState = States.START;
	}

	@Override
	public Commands update(Commands commands) {
		Commands.Setpoints setpoints = commands.robotSetpoints;
		
		switch(mState) {
		case START:
			System.out.println("Set setpoint: " + mAngle);
			drive.setBangBangTurnAngleSetpoint(mAngle);
			commands.wantedDriveState = Drive.DriveState.CONTROLLER;
			mState = States.TURNING;
			break;
			
		case TURNING:
			if(drive.controllerOnTarget()) {
				mState = States.DONE;
			}
			break;
			
		case DONE:
			drive.resetController();
			break;
		}
		
		return commands;
	}
	

	@Override
	public Commands cancel(Commands commands) {
		mState = States.DONE;
		drive.setOpenLoop(DriveSignal.NEUTRAL);
		return commands;
	}

	@Override
	public boolean finished() {
		return mState == States.DONE;
	}

	@Override
	public String getName() {
		return "BangBangGyroTurnAngleRoutine";
	}

}
