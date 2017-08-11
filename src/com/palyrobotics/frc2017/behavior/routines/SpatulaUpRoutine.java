package com.palyrobotics.frc2017.behavior.routines;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.subsystems.Spatula;
import com.palyrobotics.frc2017.subsystems.Subsystem;

public class SpatulaUpRoutine extends Routine {
	private double mStartTime = 0;
	private boolean mUpdated = false;

	@Override
	public void start() {
		mStartTime = System.currentTimeMillis();
	}

	@Override
	public Commands update(Commands commands) {
		mUpdated = true;
		commands.wantedSpatulaState = Spatula.SpatulaState.UP;
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		return commands;
	}

	@Override
	public boolean finished() {
		// TODO Auto-generated method stub
		return mUpdated && (System.currentTimeMillis() - mStartTime) > 2000;
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{Spatula.getInstance()};
	}

	@Override
	public String getName() {
		return "SpatulaUpRoutine";
	}

}
