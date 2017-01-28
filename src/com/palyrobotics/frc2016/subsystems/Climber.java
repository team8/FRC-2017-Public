package com.palyrobotics.frc2016.subsystems;

import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.config.RobotState;
import com.palyrobotics.frc2016.util.Subsystem;
import com.palyrobotics.frc2016.util.SubsystemLoop;

public class Climber extends Subsystem implements SubsystemLoop {

	private static Climber instance = new Climber();
	
	private float climbSpeed = 0;
	
	public static enum ClimberState {
		NOT_MOVING,
		CLIMBING
	}
	
	public static Climber getInstance() {
		return instance;
	}
	
	private Climber() {
		super("Climber");
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	public float getClimbState() {
		return this.climbSpeed;
	}
	
	@Override
	public void update(Commands commands, RobotState robotState) {
		// TODO Auto-generated method stub
		switch (commands.wantedClimbState) {
			case NOT_MOVING: 
				this.climbSpeed = 0;
				break;
			case CLIMBING:
				this.climbSpeed = 1;
				break;
		}
	}
	
	
}
