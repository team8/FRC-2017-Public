package com.palyrobotics.frc2017.robot;

import edu.wpi.first.wpilibj.Joystick;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.routines.EncoderDriveRoutine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Commands.*;
import com.palyrobotics.frc2017.config.Commands.JoystickInput.XboxInput;
import com.palyrobotics.frc2017.subsystems.SimpleClimber;
import com.palyrobotics.frc2017.subsystems.Intake;
import com.palyrobotics.frc2017.subsystems.SimpleSlider;
import com.palyrobotics.frc2017.subsystems.Spatula.SpatulaState;

/**
 * Used to produce Commands {@link Commands} from human input
 * Singleton class. Should only be used in robot package.
 * @author Nihar
 *
 */
public class OperatorInterface {
	private static OperatorInterface instance = new OperatorInterface();

	public static OperatorInterface getInstance() {
		return instance;
	}

	private OperatorInterface() {}

	private HardwareAdapter.Joysticks mJoysticks = HardwareAdapter.getInstance().getJoysticks();
	private Joystick mLeftStick = mJoysticks.leftStick;
	private Joystick mRightStick = mJoysticks.rightStick;
	private Joystick mOperatorStick = mJoysticks.operatorStick;

	/**
	 * Helper method to only add routines that aren't already in wantedRoutines
	 * @param commands Current set of commands being modified
	 * @param wantedRoutine Routine to add to the commands
	 */
	private void addWantedRoutine(Commands commands, Routine wantedRoutine) {
		for (Routine routine : commands.wantedRoutines) {
			if (routine.getClass().equals(wantedRoutine.getClass())) {
				return;
			}
		}
	}

	/**
	 * Modifies the commands that are passed in
	 * @param prevCommands
	 */
	public void updateCommands(Commands prevCommands) {
		// TODO: Change how routines are commanded
		if (mOperatorStick.getRawButton(4)) {
			prevCommands.addWantedRoutine(new EncoderDriveRoutine(500));
		}
		
		//TODO figure out Steik controls
		
		// Flippers
		//TODO wait a certain amount of time before being able to toggle again
		if (mOperatorStick.getRawButton(1)) {
			prevCommands.wantedFlipperSignal.toggleLeft();
		} else if (mOperatorStick.getRawButton(1)) {
			prevCommands.wantedFlipperSignal.toggleRight();
		}
		// Slider
		if (mOperatorStick.getRawButton(0)){
			prevCommands.wantedSimpleSliderState = SimpleSlider.SimpleSliderState.IDLE;
		} else if(mOperatorStick.getRawButton(0)){
			prevCommands.wantedSimpleSliderState = SimpleSlider.SimpleSliderState.MANUAL;
		}
		// Spatula
		if (mOperatorStick.getRawButton(1)) {
			prevCommands.wantedSpatulaState = 
					(prevCommands.wantedSpatulaState == SpatulaState.UP) ? SpatulaState.UP : SpatulaState.DOWN;
		}
		// Intake
		if (mOperatorStick.getRawButton(1)) {
			prevCommands.wantedIntakeState = Intake.IntakeState.INTAKE;
		} else if (mOperatorStick.getRawButton(1)) {
			prevCommands.wantedIntakeState = Intake.IntakeState.EXPEL;
		} else {
			prevCommands.wantedIntakeState = Intake.IntakeState.IDLE;
		}
		// Climber
		// Hold button 10 to climb, release to stop
		if(mOperatorStick.getRawButton(10)) {
			prevCommands.wantedSimpleClimberState = SimpleClimber.ClimberState.CLIMBING;
		} else {
			prevCommands.wantedSimpleClimberState = SimpleClimber.ClimberState.IDLE;
		}
		
		prevCommands.operatorStickInput = new XboxInput(mOperatorStick.getX(), mOperatorStick.getY(), mOperatorStick.getX(), mOperatorStick.getY());
		// Left Stick trigger cancels current routine
		prevCommands.cancelCurrentRoutines = mLeftStick.getTrigger();
		prevCommands.leftStickInput = new JoystickInput(mLeftStick.getX(), mLeftStick.getY(), mLeftStick.getTrigger());
		prevCommands.rightStickInput = new JoystickInput(mRightStick.getX(), mRightStick.getY(), mRightStick.getTrigger());
		
	}
}