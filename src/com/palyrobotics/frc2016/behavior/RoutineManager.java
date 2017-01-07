package com.palyrobotics.frc2016.behavior;

import com.palyrobotics.frc2016.behavior.routines.*;
import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.robot.team254.lib.util.StateHolder;
import com.palyrobotics.frc2016.robot.team254.lib.util.Tappable;
import com.palyrobotics.frc2016.util.Subsystem;

import java.util.*;

public class RoutineManager implements Tappable {
	// Routines that are being run
	ArrayList<Routine> mRunningRoutines = new ArrayList<Routine>();

	private Routine m_cur_routine = null;
	private Commands.Setpoints m_setpoints;
	//    private ManualRoutine m_manual_routine = new ManualRoutine();

	public void addNewRoutine(Routine newRoutine) {
		// combine running routines w/ new routine to check for shared subsystems
		ArrayList<Routine> conflicts = conflictingRoutines(mRunningRoutines, newRoutine);
		for(Routine routine : conflicts) {
			routine.cancel(Commands.getInstance());
			System.out.println("Canceling routine "+routine.getName());
			mRunningRoutines.remove(routine);
		}
		newRoutine.start();
		mRunningRoutines.add(newRoutine);
	}

	private void setNewRoutine(Routine new_routine) {
		// Cancel if new routine diff from current routine
		boolean needs_cancel = (new_routine != m_cur_routine) && (m_cur_routine != null);
		boolean needs_start = (new_routine != m_cur_routine) && (new_routine != null);
		// Cancel old routine
		if (needs_cancel) {
			m_cur_routine.cancel(Commands.getInstance());
			// Reset all setpoints
			m_setpoints.reset();
		}
		// Start next routine
		if (needs_start) {
			new_routine.start();
		}
		System.out.println("Ending " + m_cur_routine.getName() + " Starting " + new_routine.getName());
		m_cur_routine = new_routine;
	}

	public Routine getCurrentRoutine() {
		return m_cur_routine;
	}

	// Wipes all current routines
	public void reset() {
		// Cancel all running routines
		if(mRunningRoutines.size() != 0) {
			for(Routine routine : mRunningRoutines) {
				routine.cancel(Commands.getInstance());
			}
		}
		// Empty the running routines
		mRunningRoutines = new ArrayList<Routine>();
	}

	public RoutineManager() {
		m_setpoints = new Commands.Setpoints();
		m_setpoints.reset();
	}

	public void update() {
		Commands commands = Commands.getInstance();
		// If current routine exists and is finished, nullify it
//		if (m_cur_routine != null && m_cur_routine.isFinished()) {
//			System.out.println("Routine cancel called");
//			setNewRoutine(null);
//		}
		
		for(Routine routine : mRunningRoutines) {
			if(routine != null && routine.isFinished()) {
				System.out.println("Routine cancel called");
				commands = routine.cancel(commands);
			} else {
				commands = routine.update(commands);
			}
		}

		// Set TROUT routine_request
		if (commands.cancel_current_routine) {
			System.out.println("Cancel routine button");
			addNewRoutine(null);
		} else if (commands.routine_request == Commands.Routines.ENCODER_DRIVE && !(m_cur_routine instanceof EncoderDriveRoutine)) {
			addNewRoutine(new EncoderDriveRoutine(500));
		} else if (commands.routine_request == Commands.Routines.TIMER_DRIVE && !(m_cur_routine instanceof DriveTimeRoutine)) {
			System.out.println("Setting routine");
			addNewRoutine(new DriveTimeRoutine(3, 0.5));
		} else if (commands.routine_request == Commands.Routines.AUTO_ALIGN && !(m_cur_routine instanceof AutoAlignmentRoutine)) {
//			System.out.println("Auto align activated");
			addNewRoutine(new AutoAlignmentRoutine());
		} else if (commands.routine_request == Commands.Routines.TURN_ANGLE && !(m_cur_routine instanceof TurnAngleRoutine)) {
			System.out.println("Turn angle activated");
			addNewRoutine(new TurnAngleRoutine(45, 0.3));
		}

		//changes the setpoints according to the current routine update
//		if (m_cur_routine != null) {
//			m_setpoints = m_cur_routine.update(commands);
//		}

		// Get manual m_setpoints
		//        m_setpoints = m_manual_routine.update(commands, m_setpoints);
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
		states.put("mode", m_cur_routine != null ? m_cur_routine.getName() : "---");
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