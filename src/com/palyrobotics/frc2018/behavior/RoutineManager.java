package com.palyrobotics.frc2018.behavior;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.robot.team254.lib.util.StateHolder;
import com.palyrobotics.frc2018.robot.team254.lib.util.Tappable;
import com.palyrobotics.frc2018.subsystems.*;
import com.palyrobotics.frc2018.util.logger.Logger;

/**
 * Handles the updating of commands by passing them to each running routine. <br />
 * @author Nihar, Ailyn
 */
public class RoutineManager implements Tappable {
	// Routines that are being run
	private ArrayList<Routine> runningRoutines = new ArrayList<>();
	private ArrayList<Routine> routinesToRemove = new ArrayList<>();
	private ArrayList<Routine> routinesToAdd = new ArrayList<>();

	/**
	 * Stores the new routine to be added in next update cycle <br />
	 * Will automatically cancel any existing routines with the same subsystems
	 * @param newRoutine
	 */
	public void addNewRoutine(Routine newRoutine) {
		if(newRoutine == null) {
			System.err.println("Tried to add null routine to routine manager!");
			throw new NullPointerException();
		}
		routinesToAdd.add(newRoutine);
	}

	public ArrayList<Routine> getCurrentRoutines() {
		return runningRoutines;
	}

	/** Wipes all current routines <br />
	 * Pass in the commands so that routines can clean up
	 * @param commands
	 * @return modified commands if needed
	 */
	public Commands reset(Commands commands) {
		Logger.getInstance().logRobotThread("Routine manager reset");
		Commands output = commands.copy();
		// Cancel all running routines
		if(runningRoutines.size() != 0) {
			for(Routine routine : runningRoutines) {
				System.out.println("Canceling "+routine.getName());
				output = routine.cancel(output);
			}
		}
		// Empty the routine buffers
		runningRoutines.clear();
		routinesToAdd.clear();
		routinesToRemove.clear();
		return output;
	}

	/**
	 * Updates the commands that are passed in based on the running and canceled routines
	 * @param commands Current commands
	 * @return Modified commands
	 */
	public Commands update(Commands commands) {
		routinesToRemove = new ArrayList<>();
		Commands output = commands.copy();
		// Update all running routines
		for(Routine routine : runningRoutines) {
			if(routine.finished()) {
				Logger.getInstance().logRobotThread("Routine "+routine.getName()+" finished, canceled");
				output = routine.cancel(output);
				routinesToRemove.add(routine);
			} else {
				output = routine.update(output);
			}
		}
		
		// Remove routines that finished
		for(Routine routine : routinesToRemove) {
			runningRoutines.remove(routine);
		}

		// Add newest routines after current routines may have finished, start them, and update them
		for (Routine newRoutine : routinesToAdd) {
			// combine running routines w/ new routine to check for shared subsystems
			ArrayList<Routine> conflicts = conflictingRoutines(runningRoutines, newRoutine);
			for(Routine routine : conflicts) {
				Logger.getInstance().logRobotThread("Canceling routine "+routine.getName() +
						" that conflicts with "+newRoutine.getName());
				output = routine.cancel(output);
				runningRoutines.remove(routine);
			}
			newRoutine.start();
			output = newRoutine.update(output);
			runningRoutines.add(newRoutine);
		}
		
		routinesToAdd.clear();

		if (output.cancelCurrentRoutines) {
			System.out.println("Cancel routine button");
			Logger.getInstance().logRobotThread("Cancel routine button");
			output = this.reset(output);
		} else if(!output.wantedRoutines.isEmpty()) {
			// Routines requested by newly added routines
			for(Routine routine : output.wantedRoutines) {
				addNewRoutine(routine);
			}
		}
		//clears the wanted routines every update cycle
		output.wantedRoutines = new ArrayList<Routine>();
		return output;
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
		for (int j = 0; j < routinesList.size(); j++) {
			// Find intersection
			routineSubsystemSets.get(j).retainAll(subsystemsRequired);
			if(routineSubsystemSets.get(j).size()!=0) {
				conflicts.add(routinesList.get(j));
				// Move to next routine in the list
				continue;
			}
		}
		return conflicts;
	}

	@Override
	public void getState(StateHolder states) {
	}

	@Override
	public String getName() {
		return "RoutineManager";
	}
	
	public static Subsystem[] subsystemSuperset(ArrayList<Routine> routines) {
		HashSet<Subsystem> superset = new HashSet<Subsystem>();
		for (Routine routine : routines) {
			superset.addAll(Arrays.asList(routine.getRequiredSubsystems()));
		}
		return superset.toArray(new Subsystem[superset.size()]);
	}

	/**
	 * Finds overlapping subsystems
	 * Not optimized
	 */
	public static Subsystem[] sharedSubsystems(ArrayList<Routine> routines) {
		HashMap<Subsystem, Integer> counter = new HashMap<Subsystem, Integer>();
		counter.put(null, 0);	// for SampleRoutine
		counter.put(Drive.getInstance(), 0);
		// Count the number of times each subsystem appears
		for (Routine routine : routines) {
			for (Subsystem subsystem : routine.getRequiredSubsystems()) {
				counter.put(subsystem, counter.get(subsystem) + 1);
			}
		}
		// Add all subsystems that appear multiple times to return list
		HashSet<Subsystem> conflicts = new HashSet<Subsystem>();
		for (Subsystem subsystem : counter.keySet()) {
			if (counter.get(subsystem) > 1 && subsystem != null) {
				conflicts.add(subsystem);
			}
		}
		return conflicts.toArray(new Subsystem[conflicts.size()]);
	}
}