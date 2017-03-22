package com.palyrobotics.frc2017.auto;

import com.palyrobotics.frc2017.auto.modes.BaseLineAutoMode;
import com.palyrobotics.frc2017.auto.modes.CenterPegAutoMode;
import com.palyrobotics.frc2017.auto.modes.SidePegAutoMode;
import com.palyrobotics.frc2017.auto.modes.TestAutoMode;

import org.json.simple.JSONArray;

import com.palyrobotics.frc2017.auto.modes.DoNothingAutoMode;

import java.util.ArrayList;

/**
 * @author Nihar, based off Team 254 2015
 */
public class AutoModeSelector {
	private static AutoModeSelector instance = null;
	private ArrayList<AutoModeBase> mAutoModes = new ArrayList<>();
	/**
	 * comment for which auto mode the selectedIndex refers to
	 */
	int selectedIndex = 4;
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
//		registerAutonomous(new TestAutoMode());

  /*0*/ registerAutonomous(new DoNothingAutoMode());
		
  /*1*/ registerAutonomous(new BaseLineAutoMode(CenterPegAutoMode.Alliance.BLUE));
  /*2*/ registerAutonomous(new BaseLineAutoMode(CenterPegAutoMode.Alliance.RED));

  /*3*/ registerAutonomous(new CenterPegAutoMode(CenterPegAutoMode.Alliance.BLUE, CenterPegAutoMode.PostCenterAutoVariant.NOTHING));
  /*4*/ registerAutonomous(new CenterPegAutoMode(CenterPegAutoMode.Alliance.RED, CenterPegAutoMode.PostCenterAutoVariant.NOTHING));

		// side peg parameters - variant, slider, gyro, backup
  		// blue right/red left is loading station
  
		// ENCODER TURN VARIANTS, check slider centering
  /*5*/ registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.BLUE_RIGHT,
			false, false, false));
  /*6*/ registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.BLUE_RIGHT,
			true, false, false));
  /*7*/ registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.BLUE_LEFT,
			false, false, false));
  /*8*/ registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.BLUE_LEFT,
			true, false, false));
  
  /*9*/ registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.RED_RIGHT,
			false, false, false));
 /*10*/ registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.RED_RIGHT,
			true, false, false));
 /*11*/ registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.RED_LEFT,
			false, false, false));
 /*12*/ registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.RED_LEFT,
			true, false, false));

 		//ENCODER TURN BACKUP VARIANTS, check slider centering
 /*13*/ registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.BLUE_RIGHT,
			false, false, true));
 /*14*/ registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.BLUE_RIGHT,
			true, false, true));
 /*15*/ registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.BLUE_LEFT,
			false, false, true));
 /*16*/ registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.BLUE_LEFT,
			true, false, true));

 /*17*/ registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.RED_RIGHT,
			false, false, true));
 /*18*/ registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.RED_RIGHT,
			true, false, true));
 /*19*/ registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.RED_LEFT,
			false, false, true));
 /*20*/ registerAutonomous(new SidePegAutoMode(SidePegAutoMode.SideAutoVariant.RED_LEFT,
			true, false, true));
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
