package com.palyrobotics.frc2016.behavior;

import com.palyrobotics.frc2016.behavior.routines.*;
import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.robot.team254.lib.util.StateHolder;
import com.palyrobotics.frc2016.robot.team254.lib.util.Tappable;
import com.palyrobotics.frc2016.util.Subsystem;

import java.util.*;

public class RoutineManager implements Tappable {
	// Routines that are being run
	ArrayList<Routine> runningRoutines = new ArrayList<Routine>();
	ArrayList<Routine> removalRoutines = new ArrayList<Routine>();

	private Routine mCurrentRoutine = null;
	private Commands.Setpoints mSetpoints;
	//    private ManualRoutine m_manual_routine = new ManualRoutine();

	public void addNewRoutine(Routine newRoutine) {
		// combine running routines w/ new routine to check for shared subsystems
		ArrayList<Routine> conflicts = conflictingRoutines(runningRoutines, newRoutine);
		for(Routine routine : conflicts) {
			routine.cancel(Commands.getInstance());
			System.out.println("Canceling routine " + routine.getName());
			runningRoutines.remove(routine);
		}
		newRoutine.start();
		runningRoutines.add(newRoutine);
	}

	private void setNewRoutine(Routine newRoutine) {
		// Cancel if new routine diff from current routine
		boolean needs_cancel = (newRoutine != mCurrentRoutine) && (mCurrentRoutine != null);
		boolean needs_start = (newRoutine != mCurrentRoutine) && (newRoutine != null);
		// Cancel old routine
		if (needs_cancel) {
			mCurrentRoutine.cancel(Commands.getInstance());
			// Reset all setpoints
			mSetpoints.reset();
		}
		// Start next routine
		if (needs_start) {
			newRoutine.start();
		}
		System.out.println("Ending " + mCurrentRoutine.getName() + " Starting " + newRoutine.getName());
		mCurrentRoutine = newRoutine;
	}

	public Routine getCurrentRoutine() {
		return mCurrentRoutine;
	}

	// Wipes all current routines
	public void reset() {
		// Cancel all running routines
		if(runningRoutines.size() != 0) {
			for(Routine routine : runningRoutines) {
				routine.cancel(Commands.getInstance());
			}
		}
		// Empty the running routines
		runningRoutines = new ArrayList<Routine>();
	}

	public RoutineManager() {
		mSetpoints = new Commands.Setpoints();
		mSetpoints.reset();
	}

	public void update() {
		Commands commands = Commands.getInstance();
		// If current routine exists and is finished, nullify it
//		if (m_cur_routine != null && m_cur_routine.isFinished()) {
//			System.out.println("Routine cancel called");
//			setNewRoutine(null);
//		}
		removalRoutines = new ArrayList<Routine>();
		
		for(Routine routine : runningRoutines) {
			if(routine != null && routine.finished()) {
				System.out.println("Routine cancel called");
				commands = routine.cancel(commands);
				removalRoutines.add(routine);
			} else {
				System.out.println("Updating routine: " + routine.getName());
				commands = routine.update(commands);
			}
		}
		
		for(Routine routine : removalRoutines) {
			System.out.println("Removing routine: " + routine.getName());
			runningRoutines.remove(routine);
		}
		
		// Set TROUT routine_request
		if (commands.cancelCurrentRoutines) {
			System.out.println("Cancel routine button");
			addNewRoutine(null);
		} else if(!commands.wantedRoutines.isEmpty()) {
			for(Routine routine : commands.wantedRoutines) {
				addNewRoutine(routine);
			}
		}
		
		//clears the wanted routines every update cycle
		commands.wantedRoutines.clear();
	}

	/**
	 * Finds all conflicting routines required by all of the routines
	 *
	 * @param routinesList   Existing routines
	 * @param newRoutine The new routine
	 * @return Array of routines that require subsystems the newRoutine needs
	 */
	public ArrayList<Routine> conflictingRoutines(ArrayList<Routine> routinesList, Routine newRoutine) {
		// Get hash sets of required subsystems for existing routines
		ArrayList<HashSet<Subsystem>> routineSubsystemSets = new ArrayList<HashSet<Subsystem>>();
		HashSet<Subsystem> subsystemsRequired = new HashSet(Arrays.asList(newRoutine.getRequiredSubsystems()));
		for (int i = 0; i < routinesList.size(); i++) {
			routineSubsystemSets.add(new HashSet<Subsystem>(Arrays.asList(routinesList.get(i).getRequiredSubsystems())));
		}
		ArrayList<Routine> conflicts = new ArrayList<Routine>();
		// Any existing routines that require the same subsystem are added to routine
		breakpoint:
		for (int j = 0; j < routinesList.size(); j++) {
			routineSubsystemSets.get(j).retainAll(subsystemsRequired);
			if(routineSubsystemSets.get(j).size()!=0) {
				conflicts.add(routinesList.get(j));
				// Move to next routine in the list
				break breakpoint;
			}
		}
		return conflicts;
	}

	@Override
	public void getState(StateHolder states) {
		states.put("mode", mCurrentRoutine != null ? mCurrentRoutine.getName() : "---");
	}

	@Override
	public String getName() {
		return "RoutineManager";
	}

	@Deprecated
	/**
	 * Old helper method to find the overlapping subsystems
	 * is very efficient
	 */
	private Subsystem[] sharedSubsystem(Routine[] routines) {
		// Hash set is efficient for lookup and comparison to check w/ contents of others
		HashSet<Subsystem> initialSet = new HashSet<Subsystem>(Arrays.asList(routines[0].getRequiredSubsystems()));
		// Instantiate all other subsystems
		HashSet<Subsystem>[] routineSubsystemSets = new HashSet[routines.length];
		for (int i = 1; i < routines.length; i++) {
			routineSubsystemSets[i] = new HashSet<Subsystem>(Arrays.asList(routines[i].getRequiredSubsystems()));
		}
		for (int j = 1; j < routines.length; j++) {
			initialSet.retainAll(routineSubsystemSets[j]);
		}
		return (Subsystem[]) initialSet.toArray();
	}
}