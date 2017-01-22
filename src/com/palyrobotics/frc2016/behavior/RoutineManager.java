package com.palyrobotics.frc2016.behavior;

import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.robot.team254.lib.util.StateHolder;
import com.palyrobotics.frc2016.robot.team254.lib.util.Tappable;
import com.palyrobotics.frc2016.util.Subsystem;

import java.util.*;

public class RoutineManager implements Tappable {
	// Routines that are being run
	private ArrayList<Routine> runningRoutines = new ArrayList<Routine>();
	private ArrayList<Routine> routinesToRemove = new ArrayList<Routine>();

	private Commands.Setpoints mSetpoints;

	public void addNewRoutine(Commands commands, Routine newRoutine) {
		// combine running routines w/ new routine to check for shared subsystems
		ArrayList<Routine> conflicts = conflictingRoutines(runningRoutines, newRoutine);
		for(Routine routine : conflicts) {
			routine.cancel(commands);
			System.out.println("Canceling routine " + routine.getName());
			runningRoutines.remove(routine);
		}
		newRoutine.start();
		runningRoutines.add(newRoutine);
	}

	public ArrayList<Routine> getCurrentRoutine() {
		return runningRoutines;
	}

	/** Wipes all current routines <br />
	 * Pass in the commands so that routines can clean up
	 * @param commands
	 */
	public void reset(Commands commands) {
		// Cancel all running routines
		if(runningRoutines.size() != 0) {
			for(Routine routine : runningRoutines) {
				routine.cancel(commands);
			}
		}
		// Empty the running routines
		runningRoutines.clear();
	}

	public RoutineManager() {
		mSetpoints = new Commands.Setpoints();
		mSetpoints.reset();
	}

	/**
	 * Updates the commands that are passed in based on the running and canceled routines
	 * @param commands Commands object to be modified
	 */
	public void update(Commands commands) {
		// If current routine exists and is finished, nullify it
//		if (m_cur_routine != null && m_cur_routine.isFinished()) {
//			System.out.println("Routine cancel called");
//			setNewRoutine(null);
//		}
		routinesToRemove = new ArrayList<Routine>();
		
		for(Routine routine : runningRoutines) {
			if(routine != null && routine.finished()) {
				System.out.println("Routine cancel called");
				commands = routine.cancel(commands);
				routinesToRemove.add(routine);
			} else {
				System.out.println("Updating routine: " + routine.getName());
				commands = routine.update(commands);
			}
		}
		
		for(Routine routine : routinesToRemove) {
			System.out.println("Removing routine: " + routine.getName());
			runningRoutines.remove(routine);
		}
		
		// Set TROUT routine_request
		if (commands.cancelCurrentRoutines) {
			System.out.println("Cancel routine button");
			reset(commands);
		} else if(!commands.wantedRoutines.isEmpty()) {
			for(Routine routine : commands.wantedRoutines) {
				addNewRoutine(commands, routine);
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
//		states.put("mode", mCurrentRoutine != null ? mCurrentRoutine.getName() : "---");
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