package com.palyrobotics.frc2017.subsystems;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.util.Subsystem;
import com.palyrobotics.frc2017.util.SubsystemLoop;

/**
 * STEIK SIMPLE SLIDER:
<<<<<<< HEAD
 * Chain that slides the Spatula and ramp left and right.  Simple Slider utilizes a motor that is controlled by two buttons for left and right. 
=======
 * Effectively is a rotating chain that slides the Spatula and ramp left and right.  
 * The Simple Slider utilizes a motor that is controlled by two buttons for left and right. 
>>>>>>> origin/simple-slider-setup
 * The Slider should only move when the Spatula is up.
 * @author Sophia Vera 
 * @author Amelia Mao
 * 
 * 
 */
public class SimpleSlider  extends Subsystem implements SubsystemLoop  {
	//Creates Singleton class
	private static SimpleSlider instance = new SimpleSlider();
	public static SimpleSlider getInstance() {
		return instance;
	}
	
	public SimpleSlider() {
		super("Simple Slider");
	}
	
	private double mOutput;
	public static double kMotorScaleFactor = 0.5;
	
	public enum SimpleSliderState {
		IDLE, //Slider is not moving
		MANUAL
	}
	
	@Override
	public void start() {		
	
	}
	
	@Override
	public void stop() {
		
	}
	
	/**
	 * Updates the Slider's motor output
	 */
	@Override
	public void update(Commands commands, RobotState robotState) {
		switch (commands.wantedSimpleSliderState){
		case IDLE:
			mOutput = 0;
			break;
		case MANUAL:
			mOutput = commands.operatorStickInput.x * kMotorScaleFactor;
			break;
		}
	}
	
	public double getOutput(){
		return mOutput;
	}
}
