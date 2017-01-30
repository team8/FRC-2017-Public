package com.palyrobotics.frc2017.behavior;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.util.Subsystem;

import java.util.ArrayList;

/**
 * Created by Nihar on 12/27/16.
 */
public class SequentialRoutine extends Routine {
	private ArrayList<Routine> mRoutines;
	private int mRunningRoutineIndex = 0;
	private boolean mIsDone = false;
	private Subsystem[] mRequiredSubsystems;

	public SequentialRoutine(ArrayList<Routine> routines) {
		mRoutines = routines;
		mRequiredSubsystems = RoutineManager.sharedSubsystem(mRoutines);
	}

	@Override
	public void start() {
		mRoutines.get(mRunningRoutineIndex).start();
	}

	@Override
	public Commands update(Commands commands) {
		if(mIsDone) {
			return commands;
		}
		// Update the current routine
		mRoutines.get(mRunningRoutineIndex).update(commands);
		// Keep moving to next routine if the current routine is finished
		while(mRoutines.get(mRunningRoutineIndex).finished()) {
			mRoutines.get(mRunningRoutineIndex).cancel(commands);
			mRunningRoutineIndex++;
			// If final routine is finished, don't update anything
			if(mRunningRoutineIndex > mRoutines.size()-1) {
				mIsDone = true;
				break;
			}
			// Start the next routine
			mRoutines.get(mRunningRoutineIndex).start();
			// TODO: Update the new routine once or no?
		}
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		mRoutines.get(mRunningRoutineIndex).cancel(commands);
		return commands;
	}

	@Override
	public boolean finished() {
		return mIsDone;
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return mRequiredSubsystems;
	}

	@Override
	public String getName() {
		String name = "SequentialRoutine of";
		for(Routine routine : mRoutines) {
			name+= routine.getName();
		}
		return name;
	}
}
