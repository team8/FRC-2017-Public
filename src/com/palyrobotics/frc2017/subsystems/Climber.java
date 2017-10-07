package com.palyrobotics.frc2017.subsystems;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.config.dashboard.DashboardManager;
import com.palyrobotics.frc2017.config.dashboard.DashboardValue;
import com.palyrobotics.frc2017.util.CANTalonOutput;

/**
 * Subsystem that represents the climber
 * A single winch motor with an encoder
 * Uses current draw to detect when starting and stopping climb
 * @author Ailyn Tong, Robbie Selwyn
 */
public class Climber extends Subsystem{
	private static Climber instance = new Climber();
	public static Climber getInstance() {
		return instance;
	}

	private CANTalonOutput mOutput = new CANTalonOutput();

	// TODO Find constants

	private int mPrevEnc;
	private final Gains mGains = new Gains(0.1, 0, 0.01, 0, 0, 0);

	public static final int kMinimumDeltaEnc = 1;	// Minimum amount encoder should be shifting
	public static final int kEncoderTicksToTop = 100;
	public static final float kClimbingTriggerCurrent = 70;
	public static final float kStallingTriggerCurrent = 130;
	public static final double kRopeGrabSpeed = 0.5;	// Turn slowly while waiting to catch rope
	public static final double kClimbSpeed = 0.7;
	public static final double kClimbScaleFactor = 1;

	private double mTarget = -1; // Encoder endpoint
	
	private DashboardValue current;
	private DashboardValue state;
	private DashboardValue encoder;
	
	public enum ClimberState {
		IDLE,
		MANUAL,
		WAITING_FOR_ROPE,
		CLIMBING_ENCODER_DISTANCE,
		AUTOMATIC_CLIMBING
	}

	private ClimberState mState;

	private Climber() {
		super("Climber");
		
		current = new DashboardValue("climbercurrent");
		encoder = new DashboardValue("climberencoder");
		state = new DashboardValue("climberstate");
	}

	@Override
	public void start() {
		mState = ClimberState.IDLE;
		mOutput.setPercentVBus(0);
	}

	@Override
	public void stop() {
		mState = ClimberState.IDLE;
		mOutput.setPercentVBus(0);
	}

	@Override
	public void update(Commands commands, RobotState robotState) {
		boolean isNewState = !(mState == commands.wantedClimberState);
		// Sets mState
		switch (commands.wantedClimberState) {
		case IDLE:
			mState = commands.wantedClimberState;
			break;
		case MANUAL:
			mState = commands.wantedClimberState;
			break;
		case WAITING_FOR_ROPE:	// unused
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
				} else {
					mState = commands.wantedClimberState;
				}
			}
			break;
		case CLIMBING_ENCODER_DISTANCE:	//unused
			// Should never occur because never set by OI
			break;
		case AUTOMATIC_CLIMBING:
			mState = ClimberState.AUTOMATIC_CLIMBING;
		}
		// Calculates output
		switch (mState) {
		case IDLE:
			mOutput.setPercentVBus(0);
			break;
		case MANUAL:
			mOutput.setPercentVBus(-commands.climberStickInput.y * kClimbScaleFactor);
			break;
		case WAITING_FOR_ROPE:	// unused
			mOutput.setPercentVBus(kRopeGrabSpeed);
			break;
		case CLIMBING_ENCODER_DISTANCE:	// unused
			// PD loop
			if (isNewState) {
				mOutput.setPosition(kEncoderTicksToTop, mGains);
			}
			break;
		case AUTOMATIC_CLIMBING:
			mOutput.setVoltage(6);
			break;
		}
		
//		current.updateValue(mOutput);
		state.updateValue(this.mState.name());
//		encoder.updateValue(HardwareAdapter.getInstance());
		DashboardManager.getInstance().publishKVPair(state);
	}

	public CANTalonOutput getOutput() {
		return mOutput;
	}

	public ClimberState getState() {
		return mState;
	}

	@Override
	public String getStatus() {
		return "Climber State: " + mState + "\nOutput Control Mode: " + mOutput.getControlMode() + 
				"\nOutput: " + mOutput.getSetpoint()+"\n";
	}
}