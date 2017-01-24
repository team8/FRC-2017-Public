package com.palyrobotics.frc2016.behavior;

import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.util.Subsystem;

import java.util.ArrayList;

/**
 * Created by Nihar on 12/27/16.
 */
public class ParallelRoutine extends Routine {
	ArrayList<Routine> mRoutines;

	/**
	 * Runs all routines at the same time
	 * Finishes when all routines finish
	 * @param routines
	 */
	public ParallelRoutine(ArrayList<Routine> routines) {
		this.mRoutines = routines;
	}

	@Override
	public void start() {
		for(Routine routine: mRoutines) {
			routine.start();
		}
	}

	@Override
	public Commands update(Commands commands) {
		for(Routine routine: mRoutines) {
			if(!routine.finished()) {
				routine.update(commands);
			}
		}
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		for(Routine routine : mRoutines) {
			routine.cancel(commands);
		}
		return commands;
	}

	@Override
	public boolean finished() {
		for(Routine routine : mRoutines) {
			if(!routine.finished()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return RoutineManager.sharedSubsystem(mRoutines);
	}

	@Override
	public String getName() {
		//TODO:
		return null;
	}
}
