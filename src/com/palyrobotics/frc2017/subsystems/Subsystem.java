package com.palyrobotics.frc2017.subsystems;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.RobotState;

public abstract class Subsystem {
	private String mName;

	public Subsystem(String name) {
		this.mName = name;
	}
	// Updates the subsystem with current commands and state
	public abstract void update(Commands commands, RobotState robotState);

	public String getName() {
		return mName;
	}

	@Override
	public String toString() {
		return mName;
	}
//	public abstract void reloadConstants();
	public void start() {
		// TODO Auto-generated method stub
		
	}
	public void stop() {
		// TODO Auto-generated method stub
		
	}
	public String getStatus() {
		// TODO Auto-generated method stub
		return null;
	}
}
