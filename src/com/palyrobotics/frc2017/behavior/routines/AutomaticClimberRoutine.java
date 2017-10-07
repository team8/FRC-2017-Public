package com.palyrobotics.frc2017.behavior.routines;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.subsystems.Climber;
import com.palyrobotics.frc2017.subsystems.Subsystem;

public class AutomaticClimberRoutine extends Routine {

	private double mStartTime = 0;
	
	private boolean stopRoutine = false;
	@Override
	public void start() {
		mStartTime = System.currentTimeMillis();
	}

	@Override
	public Commands update(Commands commands) {
		if(commands.wantedClimberState != Climber.ClimberState.AUTOMATIC_CLIMBING && commands.wantedClimberState != Climber.ClimberState.IDLE) {
			stopRoutine = true;
		} else if(commands.wantedClimberState == Climber.ClimberState.IDLE) {
			commands.wantedClimberState = Climber.ClimberState.AUTOMATIC_CLIMBING;
		}
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		commands.wantedClimberState = Climber.ClimberState.IDLE;
		return commands;
	}

	@Override
	public boolean finished() {
		return ((System.currentTimeMillis() - mStartTime) > 30000 || stopRoutine);
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{Climber.getInstance()};
	}

	@Override
	public String getName() {
		return "AutomaticClimberRoutine";
	}

}
