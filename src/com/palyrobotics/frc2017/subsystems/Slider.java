package com.palyrobotics.frc2017.subsystems;

import java.util.HashMap;
import java.util.Optional;

import com.ctre.CANTalon.TalonControlMode;
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

	
	//all code assumes that right is 0 and left and center are both positive on both pot and encoder
	
	public enum SliderState {
		IDLE,
		MANUAL,
		ENCODER_POSITIONING,
		POTENTIOMETER_POSITIONING,	// only used when encoder broken
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

	private double mEncoderOffset;
	
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
	
	//Miscellaneous constants
	private static final double kScalar = 0.5;
	private static final double kCalibratingVoltage = 0.2;
	private static final double kMaxVoltage = 0.5;
	private static final int kPotentiometerTolerance = 0;
	private static final int kEncoderTolerance = 0;
	private static final int kCalibrationSetpoint = 0;
	
	private CANTalonOutput mOutput = new CANTalonOutput();
	
	private Slider() {
		super("Slider");
		
		if (isEncoderFunctional) mState = SliderState.ENCODER_POSITIONING;
		else if (isPotentiometerFunctional) mState = SliderState.POTENTIOMETER_POSITIONING;
		else mState = SliderState.MANUAL;
		
		mEncoderTargetPositions.put(SliderTarget.LEFT, 0.0);
		mEncoderTargetPositions.put(SliderTarget.CENTER, 0.0);
		mEncoderTargetPositions.put(SliderTarget.RIGHT, 0.0);
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
			case MANUAL:
				System.out.println("Manual");
				mTarget = SliderTarget.NONE;
				mOutput.setPercentVBus(Math.max(-kMaxVoltage, Math.min(kMaxVoltage, commands.operatorStickInput.x * kScalar)));
				break;
			case ENCODER_POSITIONING:
				mTarget = commands.robotSetpoints.sliderSetpoint;
				setSetpointsEncoder();
				break;
			case POTENTIOMETER_POSITIONING:
				mTarget = commands.robotSetpoints.sliderSetpoint;
				setSetpointsPotentiometer();
				break;
			case VISION_POSITIONING:	// unused
				setSetpointsVision();
				break;
		}
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
		if(mTarget == SliderTarget.NONE) {
			return true;
		}
		return Math.abs(mRobotState.sliderEncoder - mEncoderTargetPositions.get(mTarget)) < kEncoderTolerance
					&& (!isPotentiometerFunctional || onTargetPotentiometerPositioning());
	}
	
	/**
	 * Updates encoder automatic positioning on the slider
	 */
	private void setSetpointsEncoder() {
		if (onTargetEncoderPositioning()) {
			mTarget = SliderTarget.NONE;
		} else {
			mOutput.setPosition(getRealEncoderPosition(mEncoderTargetPositions.get(mTarget)), mEncoderGains);
		}
	}
	
	private void setSetpointsPotentiometer() {
		if (onTargetPotentiometerPositioning()) {
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
	 * Takes an adjusted position (which is absolute) and uses the offset to get the actual encoder value
	 * @param adjustedPosition
	 * @return the actual position that corresponds to the adjusted position
	 */
	private double getRealEncoderPosition(double adjustedPosition) {
		return adjustedPosition + mEncoderOffset;
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
//		System.out.println("Output is " + mOutput.getSetpoint() + " with CANTalon in " + mOutput.getControlMode());
//		System.out.println("Encoder value is " + mRobotState.sliderEncoder);
//		System.out.println("Potentiometer value is " + mRobotState.sliderPotentiometer);
		System.out.println();
	}
}
