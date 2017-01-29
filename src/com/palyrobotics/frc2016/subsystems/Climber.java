package com.palyrobotics.frc2016.subsystems;

import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.config.RobotState;
import com.palyrobotics.frc2016.util.Subsystem;
import com.palyrobotics.frc2016.util.SubsystemLoop;

/**
 * Subsystem that represents the climber
 * A single winch motor with an encoder
 * Uses current draw to detect when starting and stopping climb
 */
public class Climber extends Subsystem implements SubsystemLoop {
	private static Climber instance = new Climber();
	public static Climber getInstance() {
		return instance;
	}

	private double climbSpeed = 0;
	// Store for PD loop
	private int prevEnc;
	private final double kP = 0.1;
	private final double kD = 0.01;

	private final int kEncoderTicksToTop = 100;
	private final float kClimbingTriggerCurrent = 2;
	private final float kStallingTriggerCurrent = 70;
	private final double kRopeGrabSpeed = 0.5;


	private int encoder_cutoff = -1; // kEncoderTicksToTop + starting encoder state


	public enum ClimberState {
		IDLE,
		CLIMBING_MANUAL,
		WAITING_FOR_ROPE,
		CLIMBING_ENCODER_DISTANCE
	}

	private ClimberState mState;


	private Climber() {
		super("Climber");
	}

	@Override
	public void start() {
		mState = ClimberState.IDLE;
	}

	@Override
	public void stop() {
		mState = ClimberState.IDLE;
	}

	public double getClimberOutput() {
		return this.climbSpeed;
	}
	
	@Override
	public void update(Commands commands, RobotState robotState) {
		// Check if climb has been initiated, manually overriden, or canceled
		switch (commands.wantedClimbState) {
			case IDLE:
				this.mState = ClimberState.IDLE;
				break;
			case CLIMBING_MANUAL:
				mState = ClimberState.CLIMBING_MANUAL;
				break;
			case WAITING_FOR_ROPE:
				this.climbSpeed = .5; // go slowly at the beginning
				break;
			case CLIMBING_ENCODER_DISTANCE:
				this.climbSpeed = 1;
				break;
		}

		// Switch states based on encoder or current draw thresholds
		switch (mState) {
			case WAITING_FOR_ROPE:
				// Once current draw is high enough
				if (robotState.climberCurrentDraw > kClimbingTriggerCurrent) {
					mState = ClimberState.CLIMBING_ENCODER_DISTANCE;
					encoder_cutoff = robotState.climberEncoder + this.kEncoderTicksToTop;
				}
				break;
			case CLIMBING_ENCODER_DISTANCE:
				if (robotState.climberCurrentDraw > kStallingTriggerCurrent) {
					mState = ClimberState.IDLE;
				}
				if (robotState.climberEncoder > encoder_cutoff) {
					mState = ClimberState.IDLE;
				}
				break;
		}

		// Determine output
		switch (mState) {
			case IDLE:
				climbSpeed = 0;
				break;
			case CLIMBING_MANUAL:
				climbSpeed = 0.5;
				break;
			case WAITING_FOR_ROPE:
				climbSpeed = kRopeGrabSpeed;
			case CLIMBING_ENCODER_DISTANCE:
				climbSpeed = kP*(encoder_cutoff-robotState.climberEncoder) + kD*(prevEnc - robotState.climberEncoder);
				break;
		}
	}
}
