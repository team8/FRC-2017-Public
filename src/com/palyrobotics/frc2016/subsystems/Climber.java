package com.palyrobotics.frc2016.subsystems;

import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.config.Constants;
import com.palyrobotics.frc2016.config.RobotState;
import com.palyrobotics.frc2016.robot.HardwareAdapter;
import com.palyrobotics.frc2016.util.Subsystem;
import com.palyrobotics.frc2016.util.SubsystemLoop;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

public class Climber extends Subsystem implements SubsystemLoop {

	private static Climber instance = new Climber();
	
	private float climbSpeed = 0;
	
	private final int ENCODER_TICKS_TO_TOP = 100;
	private int encoder_cutoff = -1; // ENCODER_TICKS_TO_TOP + starting encoder state
	
	private PDPCurrentObserver currentObserver = new PDPCurrentObserver(Constants.kSteikClimberMotorPDP);
	
	public static enum ClimberState {
		NOT_MOVING,
		CLIMBING_MANUAL,
		CLIMBING_ENCODER_WAITING_FOR_CURRENT_TRIGGER,
		CLIMBING_ENCODER_DISTANCE
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
		if (commands.wantedClimbState == ClimberState.CLIMBING_ENCODER_WAITING_FOR_CURRENT_TRIGGER && currentObserver.hasInitiallyTriggeredClimb()) {
			commands.wantedClimbState = ClimberState.CLIMBING_ENCODER_DISTANCE; // change the state
			encoder_cutoff = HardwareAdapter.getInstance().getClimber().positionEncoder.get() + this.ENCODER_TICKS_TO_TOP;
		}
		
		if (commands.wantedClimbState == ClimberState.CLIMBING_ENCODER_DISTANCE && (currentObserver.isStalling() || 
				HardwareAdapter.getInstance().getClimber().positionEncoder.get() > this.encoder_cutoff)) {
			commands.wantedClimbState = ClimberState.NOT_MOVING;
		}
		
		// TODO Auto-generated method stub
		switch (commands.wantedClimbState) {
			case NOT_MOVING: 
				this.climbSpeed = 0;
				break;
			case CLIMBING_MANUAL:
				this.climbSpeed = 1;
				break;
			case CLIMBING_ENCODER_WAITING_FOR_CURRENT_TRIGGER:
				this.climbSpeed = .5f; // go slowly at the beginning
				break;
			case CLIMBING_ENCODER_DISTANCE:
				this.climbSpeed = 1;
				break;
		}
	}
	
	public static class PDPCurrentObserver {

		private final float INITIAL_TRIGGER_CURRENT = 2;
		private final float STALLING_TRIGGER_CURRENT = 70;
		
		private int port;
		
		public PDPCurrentObserver(int portObserved) {
			this.port = portObserved;
		}
		
		public boolean hasInitiallyTriggeredClimb() {
			return new PowerDistributionPanel().getCurrent(port) > INITIAL_TRIGGER_CURRENT;
		}
		
		public boolean isStalling() {
			return new PowerDistributionPanel().getCurrent(port) > STALLING_TRIGGER_CURRENT;
		}
		
	}
	
}
