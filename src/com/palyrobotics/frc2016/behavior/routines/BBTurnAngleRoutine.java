package com.palyrobotics.frc2016.behavior.routines;

import com.palyrobotics.frc2016.behavior.Routine;
import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.robot.HardwareAdapter;
import com.palyrobotics.frc2016.robot.team254.lib.util.DriveSignal;
import com.palyrobotics.frc2016.util.Subsystem;

public class BBTurnAngleRoutine extends Routine {

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{drive};
	}
	
	private double angle;
	private double maxVel;
	
	private States m_state = States.START;
	
	private enum States {
		START, TURNING, DONE
	}
	
	public BBTurnAngleRoutine(double angle, double maxVel) {
		this.angle = angle;
		this.maxVel = maxVel;
	}
	
	@Override
	public void start() {
		drive.setOpenLoop(new DriveSignal(0,0));
		m_state = States.START;
	}

	@Override
	public Commands update(Commands commands) {
		Commands.Setpoints setpoints = commands.robotSetpoints;
		
		switch(m_state) {
		case START:
			System.out.println("Set setpoint: "+angle);
			drive.setBangBangTurnAngleSetpoint(angle, maxVel);
			
			setpoints.currentRoutine = Commands.Routines.TURN_ANGLE;
			m_state = States.TURNING;
			break;
			
		case TURNING:
			if(drive.controllerOnTarget()) {
				m_state = States.DONE;
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
		m_state = States.DONE;
		drive.setOpenLoop(DriveSignal.NEUTRAL);
		return commands;
	}

	@Override
	public boolean isFinished() {
		return m_state == States.DONE;
	}

	@Override
	public String getName() {
		return "BangBangGyroTurnAngleRoutine";
	}

}
