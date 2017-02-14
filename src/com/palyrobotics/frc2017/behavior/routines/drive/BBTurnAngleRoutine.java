package com.palyrobotics.frc2017.behavior.routines.drive;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.subsystems.Drive;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.palyrobotics.frc2017.util.Subsystem;

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
		drive.setNeutral();
		mState = States.START;
	}

	@Override
	public Commands update(Commands commands) {		
		switch(mState) {
		case START:
			System.out.println("Set setpoint: " + mAngle);
			drive.setTurnAngleSetpoint(mAngle);
			commands.wantedDriveState = Drive.DriveState.OFF_BOARD_CONTROLLER;
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
		drive.setNeutral();
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
