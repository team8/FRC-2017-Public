package com.palyrobotics.frc2017.subsystems;

import java.util.HashMap;
import java.util.Optional;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.util.CANTalonOutput;
import com.palyrobotics.frc2017.util.Subsystem;
import com.palyrobotics.frc2017.util.archive.SubsystemLoop;

/**
 * Created by Nihar on 1/28/17.
 * @author Prashanti
 * Controls the slider subsystem,
 */
public class Slider extends Subsystem implements SubsystemLoop {
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
		VISION_POSITIONING,			// unused
	}
	
	public enum SliderTarget {
		NONE,
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
	private final HashMap<SliderTarget,Double> mEncoderTargetPositions = new HashMap<SliderTarget,Double>(); //TODO: find actual values
	private final HashMap<SliderTarget,Double> mPotentiometerTargetPositions = new HashMap<SliderTarget,Double>();
	
	//PID constants
	private static final Gains mEncoderGains = 
			(Constants.kRobotName == Constants.RobotName.STEIK) ? Gains.steikSliderEncoder : Gains.aegirSliderEncoder;
	private static final Gains mPotentiometerGains = 
			(Constants.kRobotName == Constants.RobotName.STEIK) ? Gains.steikSliderPotentiometer : Gains.aegirSliderPotentiometer;
	
	
	private CANTalonOutput mOutput = new CANTalonOutput();
	
	private Slider() {
		super("Slider");
				
		mEncoderTargetPositions.put(SliderTarget.LEFT, -1.0);
		mEncoderTargetPositions.put(SliderTarget.CENTER, 0.0);
		mEncoderTargetPositions.put(SliderTarget.RIGHT, 1.0);
		mPotentiometerTargetPositions.put(SliderTarget.LEFT, 0.0);
		mPotentiometerTargetPositions.put(SliderTarget.CENTER, 0.0);
		mPotentiometerTargetPositions.put(SliderTarget.RIGHT, 0.0);
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
		mState = commands.wantedSliderState;
	}
	
	/**
	 * Takes in new set of commands, must be called by a routine!
	 * @param commands the commands
	 * @param master the object calling the method
	 * @throws IllegalAccessException if master not a routine
	 */
	public void run(Commands commands,  Object master) throws IllegalAccessException {
		//Throws an exception if called by an object that isn't a routine
		if(!(master instanceof Routine)) {
			throw new IllegalAccessException();
		}
		
		
		mState = commands.wantedSliderState;
		
		switch(mState) {
			case IDLE:
				mTarget = SliderTarget.NONE;
				mOutput.setPercentVBus(0);
				break;
			case WAITING:
				mTarget = SliderTarget.NONE;
				mOutput.setPercentVBus(0);
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
			case VISION_POSITIONING:	// unused
				setSetpointsVision();
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
			return true;
		}
		return Math.abs(mRobotState.sliderPotentiometer - mPotentiometerTargetPositions.get(mTarget)) < kPotentiometerTolerance;
	}
	
	/**
	 * Checks if an encoder control loop is on target
	 * @return if the control loop is on target
	 */
	private boolean onTargetEncoderPositioning() {
		if (mTarget == SliderTarget.NONE) {
			return true;
		}
		if (!mRobotState.sliderClosedLoopError.isPresent()) {
			return false;
		}
		return Math.abs(mRobotState.sliderClosedLoopError.get()) < kEncoderTolerance && mRobotState.sliderVelocity == 0;
	}
	
	/**
	 * Updates encoder automatic positioning on the slider
	 */
	private void setSetpointsEncoder() {
		if (onTargetEncoderPositioning()) {
			mState = SliderState.IDLE;
			mTarget = SliderTarget.NONE;
		} else {
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
	
	/**
	 * Updates the control loop using vision targeting
	 */
	private void setSetpointsVision() {
		//TODO: actually write this
	}
	
	public boolean onTarget() {
		return onTargetEncoderPositioning() || onTargetPotentiometerPositioning();
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
	
	public void printStatus() {
		System.out.println("Slider Status:");
		System.out.println("State is " + mState.toString());
//		System.out.println(((this.onTarget()) ? "On target":"Not on target"));
//		System.out.println("Target is " + mTarget.toString());
//		System.out.println("Output is " + mOutput.getSetpoint() + " with CANTalon in " + mOutput.getControlMode());
//		System.out.println("Encoder value is " + mRobotState.sliderEncoder);
//		System.out.println("Potentiometer value is " + mRobotState.sliderPotentiometer);
		System.out.println();
	}
}
