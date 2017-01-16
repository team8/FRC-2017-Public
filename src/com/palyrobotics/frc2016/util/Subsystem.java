package com.palyrobotics.frc2016.util;

import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.config.RobotState;

public abstract class Subsystem implements SubsystemLoop {
	protected Dashboard dashboard = Dashboard.getInstance();
	private String mName;

	public Subsystem(String name) {
		this.mName = name;
//		SystemManager.getInstance().add(this);
	}
	// Updates the subsystem with current commands and state
	public abstract void update(Commands commands, RobotState robotState);

	public String getName() {
		return mName;
	}

//	public abstract void reloadConstants();
}
