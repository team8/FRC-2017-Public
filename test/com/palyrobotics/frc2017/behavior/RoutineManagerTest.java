package com.palyrobotics.frc2017.behavior;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import com.palyrobotics.frc2016.behavior.*;
import com.palyrobotics.frc2017.config.Commands;
import org.junit.Test;

/**
 * Created by Nihar on 1/22/17.
 * Unit tests for {@link RoutineManager}
 * @author Nihar
 */
public class RoutineManagerTest {
	private RoutineManager mRoutineManager = new RoutineManager();

	/**
	 * Test that the reset method <br/>
	 * Resets running routines, cancels routines, updates commands
	 */
	@Test
	public void testResetMethod() {
		Commands commands = new Commands();
		mRoutineManager.reset(commands);
		SampleRoutine mRoutine = new SampleRoutine();
		DrivetrainRoutine mDriveRoutine = new DrivetrainRoutine();
		mRoutineManager.addNewRoutine(commands, mRoutine);
		mRoutineManager.addNewRoutine(commands, mDriveRoutine);
		// TODO: Test actual functionality of the reset method
		mRoutineManager.reset(commands);
		assertThat("Routines were not emptied", mRoutineManager.getCurrentRoutines().isEmpty(), equalTo(true));
		assertThat("First routine was not canceled", mRoutine.finished(), equalTo(true));
		assertThat("Second routine was not canceled", mDriveRoutine.finished(), equalTo(true));
	}
	
	@Test
	public void testConflictingRoutinesMethod() {
		ArrayList<Routine> correctConflicts = new ArrayList<Routine>();
		correctConflicts.add(new SampleRoutine());
		@SuppressWarnings("unchecked")
		ArrayList<Routine> routinesList = (ArrayList<Routine>) correctConflicts.clone();
		routinesList.add(new DrivetrainRoutine());
		ArrayList<Routine> conflicts = mRoutineManager.conflictingRoutines(routinesList, new SampleRoutine());
		assertThat("Not all conflicts were detected with just one conflict", conflicts, equalTo(correctConflicts));
		
		correctConflicts = new ArrayList<Routine>();
		correctConflicts.add(new DrivetrainRoutine());
		correctConflicts.add(new DrivetrainIntakeRoutine());
		@SuppressWarnings("unchecked")
		ArrayList<Routine> routinesList2 = (ArrayList<Routine>) correctConflicts.clone();
		routinesList2.add(new SampleRoutine());	
		conflicts = mRoutineManager.conflictingRoutines(routinesList2, new DrivetrainRoutine());
		assertThat("Not all conflicts were detected with multiple conflicts", conflicts, equalTo(correctConflicts));
		
		correctConflicts = new ArrayList<Routine>();
		correctConflicts.add(new SampleRoutine());	
		@SuppressWarnings("unchecked")
		ArrayList<Routine> routinesList3 = (ArrayList<Routine>) correctConflicts.clone();
		routinesList3.add(new DrivetrainRoutine());
		routinesList3.add(new DrivetrainIntakeRoutine());	
		conflicts = mRoutineManager.conflictingRoutines(routinesList3, new SampleRoutine());
		assertThat("Not all conflicts were detected with multiple non-conflicts", conflicts, equalTo(correctConflicts));
	}
}
