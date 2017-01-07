package com.palyrobotics.frc2016.util;

import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.config.RobotState;

public abstract class Subsystem implements SubsystemLoop {
	protected Dashboard mDashboard = Dashboard.getInstance();
	private String name;

	public Subsystem(String name) {
		this.name = name;
//		SystemManager.getInstance().add(this);
	}
	// Updates the subsystem with current commands and state
	public abstract void update(Commands commands, RobotState robotState);

	public String getName() {
		return name;
	}

//	public abstract void reloadConstants();
}
