package com.palyrobotics.frc2016.behavior.routines.auto;

import com.palyrobotics.frc2016.behavior.Routine;
import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.util.Subsystem;

/**
 * Used for Tyr to ensure shooter is down
 * @author Nihar
 *
 */
public class TurnAngleAutoRoutine extends Routine {
	
	private double mHeading;
	
	public TurnAngleAutoRoutine(double heading) {
		this.mHeading = heading;
	}
	
	@Override
	public boolean finished() {
		return drive.controllerOnTarget();
	}

	@Override
	public Commands update(Commands commands) {
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		return commands;
	}

	@Override
	public void start() {
		drive.setTurnSetpoint(mHeading);
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{drive};
	}

	@Override
	public String getName() {
		return "TurnAngleAutoRoutine";
	}

}
