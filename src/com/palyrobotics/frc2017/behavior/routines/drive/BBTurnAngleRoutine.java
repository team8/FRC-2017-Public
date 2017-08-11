package com.palyrobotics.frc2017.behavior.routines.drive;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.subsystems.Drive;
import com.palyrobotics.frc2017.subsystems.Subsystem;

public class BBTurnAngleRoutine extends Routine {

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{drive};
	}
	
	private double mAngle;
	
	private GyroBBState mState = GyroBBState.START;
	private double startTime;
	private enum GyroBBState {
		START, TURNING, TIMED_OUT, DONE
	}
	
	public BBTurnAngleRoutine(double angle) {
		this.mAngle = angle;
	}
	
	@Override
	public void start() {
		System.out.println("start bb turn angle");
		drive.setNeutral();
		mState = GyroBBState.START;
		startTime = System.currentTimeMillis();
	}

	@Override
	public Commands update(Commands commands) {	
		if (mState != GyroBBState.TIMED_OUT && (System.currentTimeMillis() - startTime > 5000)) {
			System.err.println("Timed out!");
			mState = GyroBBState.TIMED_OUT;
		}
		switch(mState) {
		case START:
			System.out.println("Set setpoint: " + mAngle);
			drive.setTurnAngleSetpoint(mAngle);
			commands.wantedDriveState = Drive.DriveState.ON_BOARD_CONTROLLER;
			mState = GyroBBState.TURNING;
			break;
		case TURNING:
			if(drive.controllerOnTarget()) {
				mState = GyroBBState.DONE;
			}
			break;
		case TIMED_OUT:
			drive.setNeutral();
			break;
		case DONE:
			drive.resetController();
			break;
		}
		
		return commands;
	}
	

	@Override
	public Commands cancel(Commands commands) {
		mState = GyroBBState.DONE;
		commands.wantedDriveState = Drive.DriveState.NEUTRAL;
		drive.setNeutral();
		return commands;
	}

	@Override
	public boolean finished() {
		return mState == GyroBBState.DONE;
	}

	@Override
	public String getName() {
		return "BangBangGyroTurnAngleRoutine";
	}

}
