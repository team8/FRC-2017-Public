package com.palyrobotics.frc2017.auto;

import com.palyrobotics.frc2017.auto.modes.*;
import com.palyrobotics.frc2017.auto.modes.DriveStraightCenterPegAutoMode.CenterAutoPostVariant;
import com.palyrobotics.frc2017.auto.modes.archive.*;
import com.palyrobotics.frc2017.auto.modes.archive.CenterPegAutoMode.Alliance;
import com.palyrobotics.frc2017.auto.modes.SidePegAutoMode.SideAutoPostVariant;
import com.palyrobotics.frc2017.auto.modes.SidePegAutoMode.SideAutoVariant;
import com.palyrobotics.frc2017.auto.modes.TrajectoryCenterPegAutoMode.TrajectoryCenterPostVariant;
import com.palyrobotics.frc2017.auto.modes.TrajectorySidePegAutoMode.TrajectorySidePostVariant;

import org.json.simple.JSONArray;

import java.util.ArrayList;

/**
 * @author Nihar, based off Team 254 2015
 */
public class AutoModeSelector {
	private static AutoModeSelector instance = null;
	private ArrayList<AutoModeBase> mAutoModes = new ArrayList<>();
	private enum AutoIndices {
		DO_NOTHING(0), BASELINE(1),
		CENTER_PEG(2), SIDE_PEG(3), 
		VISION_CENTER_PEG(4), VISION_SIDE_PEG(5), 
		DRIVE_STRAIGHT_BASELINE(6), DRIVE_STRAIGHT_CENTER_PEG(7), DRIVE_STRAIGHT_SIDE_PEG(8),
		TRAJECTORY_CENTER(9), TRAJECTORY_SIDE(10),
		MO_PRO_SIDE(11),
		TEST(12), TEST_TRAJECTORY(13);
		private final int id;
		AutoIndices(int id) {this.id = id;}
		public int get() {return id;}
	}
	
	/**
	 * comment for which auto mode the selectedIndex refers to
	 */
	int selectedIndex = AutoIndices.TEST_TRAJECTORY.get();

	public static AutoModeSelector getInstance() {
		if (instance == null) {
			instance = new AutoModeSelector();
		}
		return instance;
	}

	/**
	 * Add an AutoMode to list to choose from
	 * @param auto AutoMode to add
	 */
	public void registerAutonomous(AutoModeBase auto) {
		mAutoModes.add(auto);
	}

	private AutoModeSelector() {
  /*0*/ registerAutonomous(new DoNothingAutoMode());
		
  /*1*/ registerAutonomous(new BaseLineAutoMode(Alliance.RED));

  /*2*/ registerAutonomous(new CenterPegAutoMode(Alliance.BLUE, // Alliance Color
		  										 true)); // Backup boolean
		// red left/ blue right = loading station, red right/blue left = boiler
  /*3*/	registerAutonomous(new SidePegAutoMode(SideAutoVariant.RED_LEFT, // Alliance color and side
											   SideAutoPostVariant.BACKUP)); // Should backup?
  /*4*/ registerAutonomous(new VisionCenterPegAutoMode(Alliance.BLUE, false, true)); // alliance, seeking right vision target?, backup?
  /*5*/ registerAutonomous(new VisionSidePegAutoMode(SideAutoVariant.BLUE_LEFT, // Field position
		  										false, true)); // seeking right vision target?, backup?
  /*6*/ registerAutonomous(new DriveStraightBaseLineAutoMode(Alliance.RED));
  /*7*/ registerAutonomous(new DriveStraightCenterPegAutoMode(Alliance.BLUE, CenterAutoPostVariant.BACKUP));
  /*8*/ registerAutonomous(new DriveStraightSidePegAutoMode(SideAutoVariant.RED_LEFT, SideAutoPostVariant.BACKUP));
		
  /*9*/ registerAutonomous(new TrajectoryCenterPegAutoMode(Alliance.BLUE, TrajectoryCenterPostVariant.NONE));
  /*10*/registerAutonomous(new TrajectorySidePegAutoMode(SideAutoVariant.BLUE_LEFT, TrajectorySidePostVariant.NONE));
  /*11*/registerAutonomous(new MotionProfileSidePegAutoMode(SideAutoVariant.BLUE_LEFT,
				false)); // use gyro or not
  
  /*12*/registerAutonomous(new TestAutoMode());
  /*13*/registerAutonomous(new TestTrajectoryAutoMode("RedBoiler"));
	}

	/**
	 * Get the currently selected AutoMode
	 * @return AutoMode currently selected
	 */
	public AutoModeBase getAutoMode() {
		return mAutoModes.get(selectedIndex);
	}

	/**
	 * Get the AutoMode at specified index
	 * @param index index of desired AutoMode
	 * @return AutoMode at specified index
	 */
	public AutoModeBase getAutoMode(int index) {
		// Assumes future selections will be the same auto mode
		selectedIndex = index;
		return mAutoModes.get(index);
	}

	/**
	 * Gets the names of all registered AutoModes
	 * @return ArrayList of AutoModes string name
	 * @see AutoModeBase#toString()
	 */
	public ArrayList<String> getAutoModeList() {
		ArrayList<String> list = new ArrayList<String>();
		for (AutoModeBase autoMode : mAutoModes) {
			list.add(autoMode.toString());
		}
		return list;
	}

	public JSONArray getAutoModeJSONList() {
		JSONArray list = new JSONArray();
		list.addAll(getAutoModeList());
		return list;
	}

	/**
	 * Attempt to set
	 * @return false if unable to find appropriate AutoMode
	 * @see AutoModeBase#toString()
	 */
	public boolean setAutoModeByName(String name) {
		int numOccurrences = 0;
		int index = -1;
		for(int i = 0; i<mAutoModes.size(); i++) {
			if(mAutoModes.get(i).toString() == name) {
				numOccurrences++;
				index = i;
			}
		}
		if(numOccurrences == 1) {
			setAutoModeByIndex(index);
			return true;
		} else if(numOccurrences == 0) {
			System.out.println("Couldn't find AutoMode " + name);
		} else {
			System.out.println("Found multiple AutoModes " + name);
		}
		System.err.println("Didn't select AutoMode");
		return false;
	}

	/**
	 * Called during disabled in order to access dashbord and set auto mode
	 * @return false if unable to set automode
	 */
	public boolean setFromDashboard(String selection) {
		if(!setAutoModeByName(selection)) {
			System.err.println("Did not find requested auto mode");
			return false;
		}
		return true;
	}

	private void setAutoModeByIndex(int which) {
		if (which < 0 || which >= mAutoModes.size()) {
			which = 0;
		}
		selectedIndex = which;
		System.out.println("Selected AutoMode " + mAutoModes.get(selectedIndex).toString());
	}

}
