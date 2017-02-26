package com.palyrobotics.frc2017.auto;

import com.palyrobotics.frc2017.auto.modes.BaseLineAutoMode;
import com.palyrobotics.frc2017.auto.modes.CenterPegAutoMode;
import com.palyrobotics.frc2017.auto.modes.SidePegAutoMode;
import org.json.simple.JSONArray;

import com.palyrobotics.frc2017.auto.modes.DoNothingAutoMode;
import com.palyrobotics.frc2017.auto.modes.TestAutoMode;

import java.util.ArrayList;

/**
 * @author Nihar, based off Team 254 2015
 */
public class AutoModeSelector {
	private static AutoModeSelector instance = null;
	private ArrayList<AutoMode> mAutoModes = new ArrayList<AutoMode>();
	/**
	 * comment for which auto mode the selectedIndex refers to
	 */
	int selectedIndex = 2;	// BaseLineAutoMode
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
	public void registerAutonomous(AutoMode auto) {
		mAutoModes.add(auto);
	}

	private AutoModeSelector() {
		registerAutonomous(new TestAutoMode());

		registerAutonomous(new DoNothingAutoMode());
		
		registerAutonomous(new BaseLineAutoMode());

		registerAutonomous(new CenterPegAutoMode(CenterPegAutoMode.CenterAutoVariant.NOTHING));
		registerAutonomous(new CenterPegAutoMode(CenterPegAutoMode.CenterAutoVariant.CROSS_LEFT));
		registerAutonomous(new CenterPegAutoMode(CenterPegAutoMode.CenterAutoVariant.CROSS_RIGHT));

		registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.LEFT, SidePegAutoMode.PostSideAutoVariant.NONE));
		registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.RIGHT, SidePegAutoMode.PostSideAutoVariant.NONE));
		registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.LEFT, SidePegAutoMode.PostSideAutoVariant.HIT_CLOSE_HOPPER));
		registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.RIGHT, SidePegAutoMode.PostSideAutoVariant.HIT_CLOSE_HOPPER));
		registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.LEFT, SidePegAutoMode.PostSideAutoVariant.MOVE_TO_LOADING_STATION));
		registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.RIGHT, SidePegAutoMode.PostSideAutoVariant.MOVE_TO_LOADING_STATION));
	}

	/**
	 * Get the currently selected AutoMode
	 * @return AutoMode currently selected
	 */
	public AutoMode getAutoMode() {
		return mAutoModes.get(selectedIndex);
	}

	/**
	 * Get the AutoMode at specified index
	 * @param index index of desired AutoMode
	 * @return AutoMode at specified index
	 */
	public AutoMode getAutoMode(int index) {
		// Assumes future selections will be the same auto mode
		selectedIndex = index;
		return mAutoModes.get(index);
	}

	/**
	 * Gets the names of all registered AutoModes
	 * @return ArrayList of AutoModes string name
	 * @see AutoMode#toString()
	 */
	public ArrayList<String> getAutoModeList() {
		ArrayList<String> list = new ArrayList<String>();
		for (AutoMode autoMode : mAutoModes) {
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
	 * @see AutoMode#toString()
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
