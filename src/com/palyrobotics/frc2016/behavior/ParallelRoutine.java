package com.palyrobotics.frc2016.behavior;

import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.util.Subsystem;

import java.util.ArrayList;

/**
 * Created by Nihar on 12/27/16.
 */
public class ParallelRoutine extends Routine {
	ArrayList<Routine> parallelRoutines;

	/**
	 * Runs all routines at the same time
	 * Finishes when all routines finish
	 * @param parallelRoutines
	 */
	public ParallelRoutine(ArrayList<Routine> parallelRoutines) {
		this.parallelRoutines = parallelRoutines;
	}

	@Override
	public void start() {
		for(Routine routine: parallelRoutines) {
			routine.start();
		}
	}

	@Override
	public Commands update(Commands commands) {
		for(Routine routine: parallelRoutines) {
			if(!routine.isFinished()) {
				routine.update(commands);
			}
		}
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		for(Routine routine : parallelRoutines) {
			routine.cancel(commands);
		}
		return commands;
	}

	@Override
	public boolean isFinished() {
		for(Routine routine : parallelRoutines) {
			if(!routine.isFinished()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		//TODO:
		return new Subsystem[0];
	}

	@Override
	public String getName() {
		//TODO:
		return null;
	}
}
