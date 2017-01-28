package com.palyrobotics.frc2016.subsystems;

import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.config.RobotState;
import com.palyrobotics.frc2016.util.Subsystem;
import com.palyrobotics.frc2016.util.SubsystemLoop;

public class Climber extends Subsystem implements SubsystemLoop {

	private static Climber instance = new Climber();
	
	private float climbState = 0;
	
	public static enum ClimberState {
		NOT_MOVING,
		CLIMBING,
		BACKWARDS
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
		return this.climbState;
	}
	
	@Override
	public void update(Commands commands, RobotState robotState) {
		// TODO Auto-generated method stub
		switch (commands.wantedClimbState) {
			case NOT_MOVING: 
				this.climbState = 0;
				break;
			case CLIMBING:
				this.climbState = 1;
				break;
			case BACKWARDS:
				this.climbState = -1;
				break;
		}
	}
	
	
}
