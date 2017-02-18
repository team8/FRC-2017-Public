package com.palyrobotics.frc2017.behavior;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.robot.team254.lib.util.StateHolder;
import com.palyrobotics.frc2017.robot.team254.lib.util.Tappable;
import com.palyrobotics.frc2017.subsystems.Climber;
import com.palyrobotics.frc2017.subsystems.Drive;
import com.palyrobotics.frc2017.subsystems.Flippers;
import com.palyrobotics.frc2017.subsystems.Intake;
import com.palyrobotics.frc2017.subsystems.Slider;
import com.palyrobotics.frc2017.subsystems.Spatula;
import com.palyrobotics.frc2017.util.Subsystem;

import java.util.*;

public class RoutineManager implements Tappable {
	// Routines that are being run
	private ArrayList<Routine> runningRoutines = new ArrayList<Routine>();
	private ArrayList<Routine> routinesToRemove = new ArrayList<Routine>();

	public void addNewRoutine(Commands commands, Routine newRoutine) {
		if(newRoutine == null) {
			System.err.println("Tried to add null routine to routine manager!");
			throw new NullPointerException();
		}
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

	public ArrayList<Routine> getCurrentRoutines() {
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

	/**
	 * Updates the commands that are passed in based on the running and canceled routines
	 * @param commands Current commands
	 * @return Modified commands
	 */
	public Commands update(Commands commands) {
		// If current routine exists and is finished, nullify it
		//		if (m_cur_routine != null && m_cur_routine.isFinished()) {
		//			System.out.println("Routine cancel called");
		//			setNewRoutine(null);
		//		}
		routinesToRemove = new ArrayList<Routine>();
		Commands output = commands.copy();
		for(Routine routine : runningRoutines) {
			if(routine.finished()) {
				System.out.println("Routine cancel called");
				output = routine.cancel(output);
				routinesToRemove.add(routine);
			} else {
				output = routine.update(output);
			}
		}

		for(Routine routine : routinesToRemove) {
			System.out.println("Removing routine: " + routine.getName());
			runningRoutines.remove(routine);
		}

		// Set TROUT routine_request
		if (output.cancelCurrentRoutines) {
			reset(output);
		} else if(!output.wantedRoutines.isEmpty()) {
			for(Routine routine : output.wantedRoutines) {
				System.out.println("Adding routine: " +routine.getName());
				addNewRoutine(output, routine);
			}
		}
		//clears the wanted routines every update cycle
		output.wantedRoutines.clear();
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
		//		states.put("mode", mCurrentRoutine != null ? mCurrentRoutine.getName() : "---");
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

	@Deprecated
	/**
	 * Finds overlapping subsystems
	 * Not optimized
	 */
	public static Subsystem[] sharedSubsystems(ArrayList<Routine> routines) {
		HashMap<Subsystem, Integer> counter = new HashMap<Subsystem, Integer>();
		counter.put(null, 0);	// for SampleRoutine
		counter.put(Drive.getInstance(), 0);
		counter.put(Flippers.getInstance(), 0);
		counter.put(Slider.getInstance(), 0);
		counter.put(Spatula.getInstance(), 0);
		counter.put(Intake.getInstance(), 0);
		counter.put(Climber.getInstance(), 0);
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