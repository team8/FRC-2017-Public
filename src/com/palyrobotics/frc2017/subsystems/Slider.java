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
		AUTOMATIC_POSITIONING,
		VISION_POSITIONING,
		CALIBRATING
	}
	
	public enum SliderTarget {
		NONE,
		LEFT,
		CENTER,
		RIGHT
	}
	
	private SimpleSlider mSimpleSlider = SimpleSlider.getInstance();
	private RobotState mRobotState;
	private boolean isCalibrated = false;

	private SliderState mState;
	private SliderTarget mTarget;

	private double mEncoderOffset;
	private final double potentiometerHFXValue[] = {0.0, 0.0};
	
	private final int right = 0;
	private final int left = 1;

	
	//Sensor functionality fields
	private boolean isEncoderFunctional;
	private boolean[] isHFXFunctional = {true, true};
	private boolean isPotentiometerFunctional;

	private CANTalonOutput mOutput = new CANTalonOutput();

	private final static double mCalibratingVoltage = 0.2;
	
	//Positioning constants
	private final HashMap<SliderTarget,Double> mEncoderTargetPositions = new HashMap<SliderTarget,Double>(); //TODO: find actual values
	private final HashMap<SliderTarget,Double> mPotentiometerTargetPositions = new HashMap<SliderTarget,Double>();
	
	//PID constants
	private static final Gains mEncoderGains = 
			(Constants.kRobotName == Constants.RobotName.STEIK) ? Gains.steikSliderEncoder : Gains.aegirSliderEncoder;
	private static final Gains mPotentiometerGains = 
			(Constants.kRobotName == Constants.RobotName.STEIK) ? Gains.steikSliderPotentiometer : Gains.aegirSliderPotentiometer;
	private final static int potentiometer = 0;
	private final static int encoder = 1;
	private final static double[] tolerance = {0, 0};
	
	private Optional<Double> previousPotentiometer = Optional.empty();
	private Optional<Double> integralPotentiometerPositioning = Optional.empty();
	
	
	private final static double maxVoltage = 0.5;
	
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
		mSimpleSlider.update(commands, robotState);
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
		//Switches to the wanted state unless it is calibrating and the wanted state is not manuel or idle
		if(mState != SliderState.CALIBRATING || commands.wantedSliderState == SliderState.IDLE || commands.wantedSliderState == SliderState.MANUAL) {
			mState = commands.wantedSliderState;
		}
		switch(mState) {
			case AUTOMATIC_POSITIONING:
				//Automatic calibrating
				if(!isCalibrated) {
					mState = SliderState.CALIBRATING;
					break;
				}
				mTarget = commands.robotSetpoints.sliderSetpoint;
				setSetpointsDistance();
				break;
			case IDLE:
				mTarget = SliderTarget.NONE;
				mOutput.setPercentVBus(0);
				break;
			case MANUAL:
				System.out.println("Manual");
				mTarget = SliderTarget.NONE;
				mOutput = mSimpleSlider.getOutput();
				break;
			case CALIBRATING:
				if(isCalibrated) {
					break;
				}
				if(mRobotState.sliderRightHFX || (!isHFXFunctional[right] && isPotentiometerFunctional && 
							mRobotState.sliderPotentiometer < potentiometerHFXValue[right])) {
					mEncoderOffset = mRobotState.sliderEncoder;
					mState = SliderState.IDLE;
					isCalibrated = true;
					break;
				}
				else {
					mOutput.setPercentVBus(mCalibratingVoltage);
					break;
				}
			case VISION_POSITIONING:
				setSetpointsVision();
				break;
			default:
				break;
		}
	}

	private Slider() {
		super("Slider");
		mState = SliderState.IDLE;
		mEncoderTargetPositions.put(SliderTarget.LEFT, 0.0);
		mEncoderTargetPositions.put(SliderTarget.CENTER, 0.0);
		mEncoderTargetPositions.put(SliderTarget.RIGHT, 0.0);
		mPotentiometerTargetPositions.put(SliderTarget.LEFT, 0.0);
		mPotentiometerTargetPositions.put(SliderTarget.CENTER, 0.0);
		mPotentiometerTargetPositions.put(SliderTarget.RIGHT, 0.0);
	}
	/**
	 * Get the output for the slider motor
	 * @return the output
	 */
	public CANTalonOutput getOutput() {
		//limits the voltage now cause I'm too lazy to do it when it's set
		if(mOutput.getControlMode() == TalonControlMode.PercentVbus) {
			mOutput.setPercentVBus(Math.max(-maxVoltage, Math.min(maxVoltage, mOutput.getSetpoint())));
		}
		return mOutput;
	}
	
	/**
	 * Takes an adjusted position (which is absolute) and uses the offset to get the actual encoder value
	 * @param adjustedPosition
	 * @return the actual position that corresponds to the adjusted position
	 */
	private double getRealEncoderPosition(double adjustedPosition) {
		if(!isCalibrated) {
			throw new UnsupportedOperationException();
		}
		return adjustedPosition + mEncoderOffset;
	}
	
	/**
	 * Updates the control loop for positioning using the potentiometer
	 * @return whether the control loop is on target
	 */
	private boolean updatePotentiometerAutomaticPositioning() {
		double potentiometerValue = mRobotState.sliderPotentiometer;
		if(previousPotentiometer.isPresent() && integralPotentiometerPositioning.isPresent()) {
			mOutput.setPercentVBus(Math.max(-1, Math.min(1, 
					mPotentiometerGains.P*(mPotentiometerTargetPositions.get(mTarget) - potentiometerValue) +
					mPotentiometerGains.I*integralPotentiometerPositioning.get() +
					mPotentiometerGains.D*(previousPotentiometer.get()-potentiometerValue))));
			integralPotentiometerPositioning= Optional.of((integralPotentiometerPositioning.get() + mPotentiometerTargetPositions.get(mTarget) - potentiometerValue));
			previousPotentiometer = Optional.of(potentiometerValue);
		}
		else {
			mOutput.setPercentVBus(Math.max(-1, Math.min(1, 
					mPotentiometerGains.P*(mPotentiometerTargetPositions.get(mTarget) - potentiometerValue))));
			integralPotentiometerPositioning= Optional.of(mPotentiometerTargetPositions.get(mTarget) - potentiometerValue);
			previousPotentiometer = Optional.of(potentiometerValue);
		}
		return onTargetPotentiometerPositioning();
	}
	
	/**
	 * Checks if a potentiometer control loop is on target
	 * @return if the control loop is on target
	 */
	private boolean onTargetPotentiometerPositioning() {
		if(mTarget == SliderTarget.NONE) {
			return true;
		}
		return Math.abs(mRobotState.sliderPotentiometer - mPotentiometerTargetPositions.get(mTarget)) < tolerance[potentiometer];
	}
	
	/**
	 * Checks if an encoder control loop is on target
	 * @return if the control loop is on target
	 */
	private boolean onTargetEncoderPositioning() {
		if(mTarget == SliderTarget.NONE) {
			return true;
		}
		return Math.abs(mRobotState.sliderEncoder - mEncoderTargetPositions.get(mTarget)) < tolerance[encoder]
					&& (!isPotentiometerFunctional || 
							Math.abs(mRobotState.sliderPotentiometer - mPotentiometerTargetPositions.get(mTarget)) < tolerance[potentiometer]);
	}
	
	/**
	 * Getter for the slider state
	 * @return the current slider state
	 */
	public SliderState getSliderState() {
		return mState;
	}
	
	/**
	 * Updates the control loops for automatic positioning on the slider for both encoder and potentiometer loops
	 * @return whether the control loop is on target
	 */
	private void setSetpointsDistance() {
		if(!isEncoderFunctional && isPotentiometerFunctional) {
			if(updatePotentiometerAutomaticPositioning()) {
				mState = SliderState.IDLE;
				mTarget = SliderTarget.NONE;
				previousPotentiometer = Optional.empty();
				integralPotentiometerPositioning = Optional.empty();
			}
		}
		else if (isEncoderFunctional){
			mOutput.setPosition(getRealEncoderPosition(mEncoderTargetPositions.get(mTarget)), mEncoderGains);
			if(onTargetEncoderPositioning()) {
				mState = SliderState.IDLE;
				mTarget = SliderTarget.NONE;
			}
		}
		else {
			System.out.println("Calling an automatic positioning state with no distance sensors");
		}
	}
	
	/**
	 * Updates the control loop using vision targeting
	 */
	private void setSetpointsVision() {
		//TODO: actually write this
	}
	
	public void printStatus() {
		System.out.println("Slider Status:");
		System.out.println("State is " + mState.toString());
//		System.out.println("Output is " + mOutput.getSetpoint() + " with CANTalon in " + mOutput.getControlMode());
//		System.out.println("Encoder value is " + mRobotState.sliderEncoder);
//		System.out.println("Potentiometer value is " + mRobotState.sliderPotentiometer);
//		System.out.println("Left HFX value is " + mRobotState.sliderLeftHFX);
//		System.out.println("Right HFX value is " + mRobotState.sliderRightHFX);
		System.out.println();
	}
}
