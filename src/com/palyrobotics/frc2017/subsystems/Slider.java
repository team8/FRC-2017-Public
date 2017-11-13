package com.palyrobotics.frc2017.subsystems;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.config.dashboard.DashboardManager;
import com.palyrobotics.frc2017.config.dashboard.DashboardValue;
import com.palyrobotics.frc2017.util.CANTalonOutput;

import java.util.HashMap;
import java.util.Optional;

/**
 * Created by Nihar on 1/28/17.
 * @author Prashanti
 * Controls the slider subsystem,
 */
public class Slider extends Subsystem{
	private static Slider instance = new Slider();
	public static Slider getInstance() {
		return instance;
	}

	//Miscellaneous constants
	private static final int kPotentiometerTolerance = 0;
	private static final int kEncoderTolerance = 40;
	
	//all code assumes that right is 0 and left and center are both positive on both pot and encoder
	
	public enum SliderState {
		IDLE,
		MANUAL,
		WAITING,
		AUTOMATIC_POSITIONING,
		CUSTOM_POSITIONING
	}
	
	public enum SliderTarget {
		NONE,
		CUSTOM,
		DONE,
		LEFT,
		CENTER,
		RIGHT
	}
	
	private SliderState mState;
	private SliderTarget mTarget;
	
	private RobotState mRobotState;
	
	//Potentiometer PID
	private Optional<Double> previousPotentiometer = Optional.empty();
	private Optional<Double> integralPotentiometer = Optional.empty();
	
	//Sensor functionality fields
	private boolean isEncoderFunctional = true;
	private boolean isPotentiometerFunctional = false;
	
	//Positioning constants
	private final HashMap<SliderTarget,Double> mEncoderTargetPositions = new HashMap<SliderTarget,Double>();
	private final HashMap<SliderTarget,Double> mPotentiometerTargetPositions = new HashMap<SliderTarget,Double>();
	
	//PID constants
	private static final Gains mEncoderGains = Gains.steikSliderEncoder;
	private static final Gains mPotentiometerGains = Gains.steikSliderPotentiometer;
	
	
	private CANTalonOutput mOutput = new CANTalonOutput();
	
	private DashboardValue sliderPotentiometer;
	private DashboardValue sliderDist;
	
	private Slider() {
		super("Slider");
				
		mEncoderTargetPositions.put(SliderTarget.LEFT, -1.0);
		mEncoderTargetPositions.put(SliderTarget.CENTER, 0.0);
		mEncoderTargetPositions.put(SliderTarget.RIGHT, 1.0);
		mPotentiometerTargetPositions.put(SliderTarget.LEFT, 0.0);
		mPotentiometerTargetPositions.put(SliderTarget.CENTER, 0.0);
		mPotentiometerTargetPositions.put(SliderTarget.RIGHT, 0.0);
		
		sliderPotentiometer = new DashboardValue("slider-pot");
		sliderDist = new DashboardValue("sliderDistance");
	}
	
	
	@Override
	public void start() {
		mState = SliderState.IDLE;
		mTarget = SliderTarget.NONE;
		mOutput.setPercentVBus(0);
	}
	
	@Override
	public void stop() {
		mState = SliderState.IDLE;
		mTarget = SliderTarget.NONE;
		mOutput.setPercentVBus(0);
	}
	
	/**
	 * Updates the slider with the newest robot state, does not set any states or change the output
	 * @param commands the commands
	 * @param robotState robot sensor data
	 */
	@Override
	public void update(Commands commands, RobotState robotState) {
		mRobotState = robotState;
		// Updating the output based on slider target will cause desync and exceptions
		sliderPotentiometer.updateValue(robotState.sliderPotentiometer);
		sliderDist.updateValue(robotState.sliderPosition);
		DashboardManager.getInstance().publishKVPair(sliderPotentiometer);
		DashboardManager.getInstance().publishKVPair(sliderDist);
		
		mState = commands.wantedSliderState;
		switch(mState) {

			case IDLE:
				mTarget = SliderTarget.NONE;
				mOutput.setPercentVBus(0);
				break;
			case WAITING:
				mTarget = commands.robotSetpoints.sliderSetpoint;
				mOutput.setPercentVBus(0);
				break;
			case MANUAL:
				mTarget = SliderTarget.NONE;
				setManualOutput(commands);
				break;
			case AUTOMATIC_POSITIONING:
				mTarget = commands.robotSetpoints.sliderSetpoint;
				if (isEncoderFunctional) {
					setSetpointsEncoder();
				} else if (isPotentiometerFunctional) {
					setSetpointsPotentiometer();
				} else {
					System.err.println("Attempting automatic positioning without sensors!");
				}
				break;
			case CUSTOM_POSITIONING:
				if(!isEncoderFunctional) {
					System.err.println("No custom positioning with potentiometer");
					break;
				}
				if (!commands.robotSetpoints.sliderCustomSetpoint.isPresent()) {
					System.err.println("No setpoint");
					break;
				} else {
					mTarget = SliderTarget.CUSTOM;
				}
				mTarget = SliderTarget.CUSTOM;
				//problem  below
				mOutput.setPosition(commands.robotSetpoints.sliderCustomSetpoint.get(), mEncoderGains);
				break;
		}		
	}
	
	
	/**
	 * Encapsulate to use in both run and update methods
	 */
	private void setManualOutput(Commands commands) {
		mOutput.setVoltage(commands.sliderStickInput.x*Constants.kSliderMaxVoltage);
	}
	
	/**
	 * Checks if a potentiometer control loop is on target
	 * @return if the control loop is on target
	 */
	private boolean onTargetPotentiometerPositioning() {
		if(mTarget == SliderTarget.NONE) {
			return false;
		}
		boolean output = true;
		try {
			output = Math.abs(mRobotState.sliderPotentiometer - mPotentiometerTargetPositions.get(mTarget)) < kPotentiometerTolerance;
		} catch (NullPointerException e) {

		}
		return output;
	}
	
	/**
	 * Checks if an encoder control loop is on target
	 * @return if the control loop is on target
	 */
	private boolean onTargetEncoderPositioning() {
		if (mState == SliderState.IDLE) {
			return false;
		}
		if (mTarget == SliderTarget.DONE) {
			return true;
		}
		if(mRobotState.sliderClosedLoopError == null) {
			return false;
		}
		if (mRobotState.sliderClosedLoopError.isPresent()) {
			return Math.abs(mRobotState.sliderClosedLoopError.get()) < kEncoderTolerance && mRobotState.sliderVelocity == 0;
		} else {
			return false;
		}
	}
	
	/**
	 * Updates encoder automatic positioning on the slider
	 */
	private void setSetpointsEncoder() {
		if (onTargetEncoderPositioning()) {
			mState = SliderState.IDLE;
			mTarget = SliderTarget.DONE;
		} else if (mTarget == SliderTarget.NONE) {
			return;
		}
		else {
			System.out.println("automatic setpoint"+mEncoderTargetPositions.get(mTarget));
			mOutput.setPosition(mEncoderTargetPositions.get(mTarget), mEncoderGains);
		}
	}
	
	private void setSetpointsPotentiometer() {
		if (onTargetPotentiometerPositioning()) {
			mState = SliderState.IDLE;
			mTarget = SliderTarget.NONE;
			previousPotentiometer = Optional.empty();
			integralPotentiometer = Optional.empty();
		} else {
			updatePotentiometerAutomaticPositioning();
		}
	}
	
	/**
	 * Updates the control loop for positioning using the potentiometer
	 * @return whether the control loop is on target
	 */
	private void updatePotentiometerAutomaticPositioning() {
		double potentiometerValue = mRobotState.sliderPotentiometer;
		if(previousPotentiometer.isPresent() && integralPotentiometer.isPresent()) {
			mOutput.setPercentVBus(Math.max(-1, Math.min(1, 
					mPotentiometerGains.P * (mPotentiometerTargetPositions.get(mTarget) - potentiometerValue) +
					mPotentiometerGains.I * integralPotentiometer.get() +
					mPotentiometerGains.D * (previousPotentiometer.get() - potentiometerValue))));
			integralPotentiometer = Optional.of((integralPotentiometer.get() + mPotentiometerTargetPositions.get(mTarget) - potentiometerValue));
			previousPotentiometer = Optional.of(potentiometerValue);
		}
		else {
			mOutput.setPercentVBus(Math.max(-1, Math.min(1, 
					mPotentiometerGains.P * (mPotentiometerTargetPositions.get(mTarget) - potentiometerValue))));
			integralPotentiometer = Optional.of(mPotentiometerTargetPositions.get(mTarget) - potentiometerValue);
			previousPotentiometer = Optional.of(potentiometerValue);
		}
	}
	
	public boolean onTarget() {
		return (onTargetEncoderPositioning());
	}
	
	/**
	 * @return the current slider state
	 */
	public SliderState getSliderState() {
		return mState;
	}
	
	/**
	 * Get the output for the slider motor
	 * @return the output
	 */
	public CANTalonOutput getOutput() {
		return mOutput;
	}
	
	public String getStatus() {
		return "Slider State: " + mState + "\n" + ((this.onTarget()) ? "On target":"Not on target") +
				"\nTarget: " + mTarget + "\nOutput: " + mOutput.getSetpoint() + 
				" with CANTalon in " + mOutput.getControlMode() + "\nEncoder value is " + mRobotState.sliderEncoder
				+ "\nPotentiometer value is " + mRobotState.sliderPotentiometer + "\n";
	}
}
