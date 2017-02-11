package com.palyrobotics.frc2017.subsystems;

import com.mindsensors.CANSD540;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.config.dashboard.DashboardManager;
import com.palyrobotics.frc2017.config.dashboard.DashboardValue;
import com.palyrobotics.frc2017.util.Subsystem;
import com.palyrobotics.frc2017.util.SubsystemLoop;

import edu.wpi.first.wpilibj.DoubleSolenoid;
/**
 * Subsystem that represents the climber
 * A single winch motor with an encoder
 * Uses current draw to detect when starting and stopping climb
 * @author Ailyn Tong, Robbie Selwyn
 */
public class Climber extends Subsystem implements SubsystemLoop {
	private static Climber instance = new Climber();
	public static Climber getInstance() {
		return instance;
	}

	private double mOutput = 0;

	// TODO Find constants

	// Store for PD loop
	private int mPrevEnc;
	private static final double kP = 0.1;
	private static final double kD = 0.01;

	public static final int kMinimumDeltaEnc = 1;	// Minimum amount encoder should be shifting
	public static final int kEncoderTicksToTop = 100;
	public static final float kClimbingTriggerCurrent = 2;
	public static final float kStallingTriggerCurrent = 70;
	public static final double kRopeGrabSpeed = 0.5;	// Turn slowly while waiting to catch rope
	public static final double kClimbSpeed = 1;

	private int mTarget = -1; // Encoder endpoint
	private DashboardValue mDv;

	public enum ClimberState {
		IDLE,
		MANUAL,
		WAITING_FOR_ROPE,
		CLIMBING_ENCODER_DISTANCE
	}

	private ClimberState mState;

	private Climber() {
		super("Climber");
		
		mDv = new DashboardValue("climber");
	}

	@Override
	public void start() {
		mState = ClimberState.IDLE;
	}

	@Override
	public void stop() {
		mState = ClimberState.IDLE;
	}

	@Override
	public void update(Commands commands, RobotState robotState) {
		// Sets mState
		switch (commands.wantedClimberState) {
		case IDLE:
			mState = commands.wantedClimberState;
			break;
		case MANUAL:
			mState = commands.wantedClimberState;
			break;
		case WAITING_FOR_ROPE:
			// Climber is climbing
			if (mState == Climber.ClimberState.CLIMBING_ENCODER_DISTANCE) {
				// Too much current draw (stalling)
				if (robotState.climberCurrentDraw > kStallingTriggerCurrent) {
					System.out.println("Climber stalling, switching to idle");
					mState = ClimberState.IDLE;
				} 
				// Encoder not shifting enough
				else if (robotState.climberEncoder - mPrevEnc < kMinimumDeltaEnc) {
					System.out.println("Climber stuck, switching to idle");
					mState = ClimberState.IDLE;
				}
				// Reached end
				else if (robotState.climberEncoder > mTarget) {
					System.out.println("Climb complete, switching to idle");
					mState = ClimberState.IDLE;
				}
				else {
					mState = Climber.ClimberState.CLIMBING_ENCODER_DISTANCE;
				}
			} 
			// Climber is waiting
			else {
				// Detect rope catch using current draw
				if (robotState.climberCurrentDraw > kClimbingTriggerCurrent) {
					System.out.println("Rope has been caught, swithing to encoder climb");
					mState = ClimberState.CLIMBING_ENCODER_DISTANCE;
					mTarget = robotState.climberEncoder + kEncoderTicksToTop;	// Calculate endpoint
					mPrevEnc = robotState.climberEncoder;	// Initialize for climb
				} else {
					mState = commands.wantedClimberState;
				}
			}
			break;
		case CLIMBING_ENCODER_DISTANCE:
			// Should never occur because never set by OI
			break;
		}
		// Calculates output
		switch (mState) {
		case IDLE:
			mOutput = 0;
			break;
		case MANUAL:
			mOutput = kClimbSpeed;
			break;
		case WAITING_FOR_ROPE:
			mOutput = kRopeGrabSpeed;
			break;
		case CLIMBING_ENCODER_DISTANCE:
			// PD loop
			mOutput = kP * (mTarget - robotState.climberEncoder) 
			+ kD * (mPrevEnc - robotState.climberEncoder);
			mPrevEnc = robotState.climberEncoder;
			break;
		}
		
		if (mOutput == 0) {
			mDv.updateValue("NOT MOVING");
		}
		else {
			mDv.updateValue("MOVING");
		}
		
		DashboardManager.getInstance().publishKVPair(mDv);
	}

	public double getOutput() {
		System.out.println(mOutput);
		return mOutput;
	}

	public ClimberState getState() {
		return mState;
	}
}