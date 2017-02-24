package com.palyrobotics.frc2017.robot;

import com.palyrobotics.frc2017.subsystems.*;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.routines.drive.EncoderDriveRoutine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Commands.*;
import com.palyrobotics.frc2017.config.Commands.JoystickInput.XboxInput;

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
	 * @return whether or not wantedRoutine was successfully added
	 */
	private boolean addWantedRoutine(Commands commands, Routine wantedRoutine) {
		for (Routine routine : commands.wantedRoutines) {
			if (routine.getClass().equals(wantedRoutine.getClass())) {
				return false;
			}
		}
		commands.wantedRoutines.add(wantedRoutine);
		return true;
	}

	/**
	 * Returns modified commands
	 * @param prevCommands
	 */
	public Commands updateCommands(Commands prevCommands) {
		Commands newCommands = prevCommands.copy();
		if(prevCommands.wantedDriveState != Drive.DriveState.OFF_BOARD_CONTROLLER) {
			newCommands.wantedDriveState = Drive.DriveState.CHEZY;
		}

		// TODO: Change how routines are commanded
		if (mOperatorStick.getRawButton(4)) {
			newCommands.addWantedRoutine(new EncoderDriveRoutine(500));
		}

		// Flippers
		//TODO figure out flipper controls
		//TODO wait a certain amount of time before being able to toggle again
		// Left Flipper
		if (mOperatorStick.getRawButton(1)) {
			newCommands.wantedFlipperSignal.leftFlipper = DoubleSolenoid.Value.kForward;
		} else if (mOperatorStick.getRawButton(1)) {
			newCommands.wantedFlipperSignal.leftFlipper = DoubleSolenoid.Value.kReverse;
		}
		//Right Flipper
		if (mOperatorStick.getRawButton(1)) {
			newCommands.wantedFlipperSignal.rightFlipper = DoubleSolenoid.Value.kForward;
		} else if (mOperatorStick.getRawButton(1)) {
			newCommands.wantedFlipperSignal.rightFlipper = DoubleSolenoid.Value.kReverse;
		}

		// Slider
		if (mOperatorStick.getRawButton(1)){
			newCommands.wantedSimpleSliderState = SimpleSlider.SimpleSliderState.IDLE;
		} else if(mOperatorStick.getRawButton(1)){
			newCommands.wantedSimpleSliderState = SimpleSlider.SimpleSliderState.MANUAL;
		}

		// Spatula
		if (mOperatorStick.getRawButton(3)) {
			newCommands.wantedSpatulaState = Spatula.SpatulaState.UP;
		} else if (mOperatorStick.getRawButton(2)) {
			newCommands.wantedSpatulaState = Spatula.SpatulaState.DOWN;
		}

		// Intake
		if (mOperatorStick.getRawButton(1)) {
			newCommands.wantedIntakeState = Intake.IntakeState.INTAKE;
		} else if (mOperatorStick.getRawButton(11)) {
			newCommands.wantedIntakeState = Intake.IntakeState.EXPEL;
		} else {
			newCommands.wantedIntakeState = Intake.IntakeState.IDLE;
		}
		
		// Climber
//		if (mOperatorStick.getRawButton(7)) { // overall cancel button for automatic
//			newCommands.wantedClimberState = Climber.ClimberState.IDLE;
//		} else if (mOperatorStick.getRawButton(6)) {
//			newCommands.wantedClimberState = Climber.ClimberState.WAITING_FOR_ROPE;
//		} else if (mOperatorStick.getY() > 0) {
//			newCommands.wantedClimberState = Climber.ClimberState.MANUAL;
//		}
		if (mOperatorStick.getY() != 0) {
			newCommands.wantedClimberState = Climber.ClimberState.MANUAL;
		} else {
			newCommands.wantedClimberState = Climber.ClimberState.IDLE;
		}

		newCommands.operatorStickInput = new XboxInput(mOperatorStick.getX(), mOperatorStick.getY(), mOperatorStick.getX(), mOperatorStick.getY());
		// Left Stick trigger cancels current routine
		newCommands.cancelCurrentRoutines = mLeftStick.getTrigger();
		newCommands.leftStickInput = new JoystickInput(mLeftStick.getX(), mLeftStick.getY(), mLeftStick.getTrigger());
		newCommands.rightStickInput = new JoystickInput(mRightStick.getX(), mRightStick.getY(), mRightStick.getTrigger());

		return newCommands;
	}
}