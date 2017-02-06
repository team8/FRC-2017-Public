package com.palyrobotics.frc2017.robot;

import com.palyrobotics.frc2017.subsystems.Drive;
import com.palyrobotics.frc2017.util.CANTalonOutput;
import com.palyrobotics.frc2017.util.DriveSignal;
import com.palyrobotics.frc2017.util.LegacyDrive;
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
		if(prevCommands.wantedDriveState != Drive.DriveState.CONTROLLER) {
			newCommands.wantedDriveState = Drive.DriveState.CHEZY;
		}
		
		//TODO: REMOVE ALL THIS STUFF and the related imports
		newCommands.leftDriveOutput = new CANTalonOutput();
		if(mLeftStick.getRawButton(6)) {
			newCommands.leftDriveOutput.setPercentVBus(0.5);
		}
		else if(mLeftStick.getRawButton(7)) {
			newCommands.leftDriveOutput.setCurrent(5, 1, 0, 0.1, 0, 0, 0);;
		}
		else if(mLeftStick.getRawButton(10)) {
			newCommands.leftDriveOutput.setPosition(72 * 1400 / (2 * 3.1415 * 3.5), 0.4, 0, 4, 0, 0, 0);
		}
		else if(mLeftStick.getRawButton(11)) {
			newCommands.leftDriveOutput.setSpeed(12 / 10.0f * 1400 / (2 * 3.1415 * 3.5), 3, 0, 50, 2.122, 0, 0);
		}
		else {
			newCommands.leftDriveOutput = DriveSignal.getNeutralSignal().leftMotor;
		}
//		System.out.println(prevCommands.leftDriveOutput.toString());
		
		// TODO: Change how routines are commanded
		if (mOperatorStick.getRawButton(4)) {
			newCommands.addWantedRoutine(new EncoderDriveRoutine(500));
		}

		//TODO figure out Steik controls

		// Flippers
		//TODO wait a certain amount of time before being able to toggle again
		if (mOperatorStick.getRawButton(1)) {
			newCommands.wantedFlipperSignal.toggleLeft();
		} else if (mOperatorStick.getRawButton(1)) {
			newCommands.wantedFlipperSignal.toggleRight();
		}
		// Slider
		if (mOperatorStick.getRawButton(0)){
			prevCommands.wantedSimpleSliderState = SimpleSlider.SimpleSliderState.IDLE;
		} else if(mOperatorStick.getRawButton(0)){
			prevCommands.wantedSimpleSliderState = SimpleSlider.SimpleSliderState.MANUAL;
		}
		// Spatula
		if (mOperatorStick.getRawButton(1)) {
			newCommands.wantedSpatulaState = 
					(newCommands.wantedSpatulaState == SpatulaState.UP) ? SpatulaState.UP : SpatulaState.DOWN;
		}
		// Intake
		if (mOperatorStick.getRawButton(1)) {
			newCommands.wantedIntakeState = Intake.IntakeState.INTAKE;
		} else if (mOperatorStick.getRawButton(1)) {
			newCommands.wantedIntakeState = Intake.IntakeState.EXPEL;
		} else {
			newCommands.wantedIntakeState = Intake.IntakeState.IDLE;
		}
		// Climber
		// Hold button 10 to climb, release to stop
		if(mOperatorStick.getRawButton(10)) {
			prevCommands.wantedSimpleClimberState = SimpleClimber.ClimberState.CLIMBING;
		} else {
			prevCommands.wantedSimpleClimberState = SimpleClimber.ClimberState.IDLE;
		}
		
//		newCommands.operatorStickInput = new XboxInput(mOperatorStick.getX(), mOperatorStick.getY(), mOperatorStick.getX(), mOperatorStick.getY());
		// Left Stick trigger cancels current routine
		newCommands.cancelCurrentRoutines = mLeftStick.getTrigger();
		newCommands.leftStickInput = new JoystickInput(mLeftStick.getX(), mLeftStick.getY(), mLeftStick.getTrigger());
		newCommands.rightStickInput = new JoystickInput(mRightStick.getX(), mRightStick.getY(), mRightStick.getTrigger());
	
		// TODO REINSERT
		// overall cancel button
//		if (mOperatorStick.getRawButton(5)) {
//			newCommands.wantedClimbState = ClimberState.IDLE;
//		}
//		if (mOperatorStick.getRawButton(4)) {
//			newCommands.wantedClimbState = ClimberState.CLIMBING_MANUAL;
//		}
//		if (mOperatorStick.getRawButton(6)) {
//			newCommands.wantedClimbState = ClimberState.WAITING_FOR_ROPE;
//		}
		return newCommands;
	}
}