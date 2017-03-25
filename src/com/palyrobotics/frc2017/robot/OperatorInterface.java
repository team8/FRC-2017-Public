package com.palyrobotics.frc2017.robot;

import com.palyrobotics.frc2017.behavior.routines.scoring.ManualControlSliderRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.AutocorrectPositioningSliderRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.PositioningSliderRoutine;
import com.palyrobotics.frc2017.subsystems.*;
import com.palyrobotics.frc2017.util.DoubleClickTimer;
import com.palyrobotics.frc2017.util.logger.Logger;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Commands.*;

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
	private Joystick mDriveStick = mJoysticks.driveStick;
	private Joystick mTurnStick = mJoysticks.turnStick;
	private Joystick mSliderStick = mJoysticks.sliderStick;
	private Joystick mClimberStick = mJoysticks.climberStick;
	
	// Adjust parameters as needed, default for now
	private DoubleClickTimer sliderLeft = new DoubleClickTimer();
	private DoubleClickTimer sliderRight = new DoubleClickTimer();

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
		if(prevCommands.wantedDriveState != Drive.DriveState.OFF_BOARD_CONTROLLER
				&& prevCommands.wantedDriveState != Drive.DriveState.ON_BOARD_CONTROLLER) {
			newCommands.wantedDriveState = Drive.DriveState.CHEZY;
		}
		newCommands.leftStickInput = new JoystickInput(mDriveStick.getX(), mDriveStick.getY(), mDriveStick.getTrigger());
		newCommands.rightStickInput = new JoystickInput(mTurnStick.getX(), mTurnStick.getY(), mTurnStick.getTrigger());
		newCommands.sliderStickInput = new JoystickInput(mSliderStick.getX(), mSliderStick.getY(), mSliderStick.getTrigger());
		newCommands.climberStickInput = new JoystickInput(mClimberStick.getX(), mClimberStick.getY(), mClimberStick.getTrigger());

		// Flippers
		//TODO figure out flipper controls
		// Left Flipper
		if (mSliderStick.getRawButton(1)) {
			newCommands.wantedFlipperSignal.leftFlipper = DoubleSolenoid.Value.kForward;
		} else if (mSliderStick.getRawButton(1)) {
			newCommands.wantedFlipperSignal.leftFlipper = DoubleSolenoid.Value.kReverse;
		}
		//Right Flipper
		if (mSliderStick.getRawButton(1)) {
			newCommands.wantedFlipperSignal.rightFlipper = DoubleSolenoid.Value.kForward;
		} else if (mSliderStick.getRawButton(1)) {
			newCommands.wantedFlipperSignal.rightFlipper = DoubleSolenoid.Value.kReverse;
		}

		// Slider
		if (mSliderStick.getRawButton(2)) {	// opposite of preferred thumb position
			newCommands.robotSetpoints.sliderSetpoint = Slider.SliderTarget.NONE;
			newCommands.addWantedRoutine(new ManualControlSliderRoutine());
		} else if (mSliderStick.getRawButton(3)) {	// preferred thumb position
			newCommands.robotSetpoints.sliderSetpoint = Slider.SliderTarget.CENTER;
			newCommands.addWantedRoutine(new PositioningSliderRoutine());
		} else if (mSliderStick.getRawButton(4)) {
			newCommands.robotSetpoints.sliderSetpoint = Slider.SliderTarget.LEFT;
			if (sliderLeft.twice()) {
				newCommands.addWantedRoutine(new PositioningSliderRoutine());
			} else {
				newCommands.addWantedRoutine(new AutocorrectPositioningSliderRoutine(Slider.SliderTarget.LEFT));
			}
		} else if (mSliderStick.getRawButton(5)) {
			newCommands.robotSetpoints.sliderSetpoint = Slider.SliderTarget.RIGHT;
			if (sliderRight.twice()) {
				newCommands.addWantedRoutine(new PositioningSliderRoutine());
			} else {
				newCommands.addWantedRoutine(new AutocorrectPositioningSliderRoutine(Slider.SliderTarget.RIGHT));
			}
		} else if (Slider.getInstance().getSliderState() == Slider.SliderState.IDLE) {
			newCommands.addWantedRoutine(new ManualControlSliderRoutine());
		}

		// Spatula
		if (mSliderStick.getRawButton(8)) {
			newCommands.wantedSpatulaState = Spatula.SpatulaState.DOWN;
		} else if (mSliderStick.getRawButton(9)) {
			newCommands.wantedSpatulaState = Spatula.SpatulaState.UP;
		}

		// Intake
		if (mSliderStick.getRawButton(1)) {
			newCommands.wantedIntakeState = Intake.IntakeState.INTAKE;
		} else if (mSliderStick.getRawButton(11)) {
			newCommands.wantedIntakeState = Intake.IntakeState.EXPEL;
		} else {
			newCommands.wantedIntakeState = Intake.IntakeState.IDLE;
		}

		// Climber buttons overrides joystick
		if (mSliderStick.getRawButton(10)) {
			newCommands.climberStickInput.y = -0.15;
		} else if (mSliderStick.getRawButton(11)) {
			newCommands.climberStickInput.y = -0.8;
		} else if (mDriveStick.getRawButton(7)) {
			newCommands.climberStickInput.y = -0.15;
		} else if (mDriveStick.getRawButton(10)) {
			newCommands.climberStickInput.y = -0.8;
		}
		// Climber joystick may be set in a virtual sense
		if (newCommands.climberStickInput.y <= 0.02) {
			newCommands.wantedClimberState = Climber.ClimberState.MANUAL;
		} else {
			newCommands.wantedClimberState = Climber.ClimberState.IDLE;
		}
		// Left Stick trigger cancels current routine
		newCommands.cancelCurrentRoutines = mDriveStick.getTrigger();

		Logger.getInstance().logRobotThread("Drive stick", newCommands.leftStickInput);
		Logger.getInstance().logRobotThread("Turn stick", newCommands.rightStickInput);
		Logger.getInstance().logRobotThread("Slider stick", newCommands.sliderStickInput);
		Logger.getInstance().logRobotThread("Climber stick", newCommands.climberStickInput);

		return newCommands;
	}
}