package com.palyrobotics.frc2017.subsystems;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.util.CANTalonOutput;
import com.palyrobotics.frc2017.util.Subsystem;
import com.palyrobotics.frc2017.util.SubsystemLoop;

/**
 * STEIK SIMPLE SLIDER:
 * Chain that slides the Spatula and ramp left and right.  Simple Slider utilizes a motor and is controlled by the Slider class. 
 * The Slider should only move when the Spatula is up.
 * @author Sophia Vera 
 * @author Amelia Mao
 */
public class SimpleSlider  extends Subsystem implements SubsystemLoop {
	//Creates Singleton class
	private static SimpleSlider instance = new SimpleSlider();
	public static SimpleSlider getInstance() {
		return instance;
	}
	
	public SimpleSlider() {
		super("Simple Slider");
	}
	
	private CANTalonOutput mOutput;
	public static double kMotorScaleFactor = 0.5;
	
	public enum SimpleSliderState {
		IDLE, //Slider is not moving
		MANUAL
	}

	/**
	 * Updates the Slider's motor output
	 */
	@Override
	public void update(Commands commands, RobotState robotState) {
		switch (commands.wantedSimpleSliderState){
		case IDLE:
			mOutput.setPercentVBus(0);
			break;
		case MANUAL:
			mOutput.setPercentVBus(commands.operatorStickInput.x * kMotorScaleFactor);
			break;
		}
	}
	
	public CANTalonOutput getOutput(){
		return mOutput;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
}
