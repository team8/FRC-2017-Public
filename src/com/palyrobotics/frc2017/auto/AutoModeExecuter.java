package com.palyrobotics.frc2017.auto;

import com.palyrobotics.frc2017.behavior.RoutineManager;

/**
 * Runs an auto mode on its own thread, needs a routine manager
 */
public class AutoModeExecuter {
	private AutoModeBase mAutoMode;
	private RoutineManager mRoutineManager;
	private boolean mRunning = false;

	public AutoModeExecuter(RoutineManager routineManager) {
		this.mRoutineManager = routineManager;
	}

	public void setAutoMode(AutoModeBase newAutoMode) {
		mAutoMode = newAutoMode;
	}

	public void start() {
		mAutoMode.prestart();
	}
	
	public void run() {
		if (!mRunning) {
			try {
				mAutoMode.execute();
			} catch (AutoModeEndedException e) {
				this.stop();
			}
		} else {
			
		}
	}
	
	public void stop() {
		if (mAutoMode != null) {
			mAutoMode.stop();
		}
	}
}