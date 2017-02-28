package com.palyrobotics.frc2017.robot;

import com.palyrobotics.frc2017.behavior.routines.scoring.ManualSliderControlRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.SliderDistancePositioningAutocorrectRoutine;
import com.palyrobotics.frc2017.behavior.routines.scoring.SliderDistancePositioningRoutine;
import com.palyrobotics.frc2017.subsystems.*;
import com.palyrobotics.frc2017.util.DoubleClickTimer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Commands.*;
import com.palyrobotics.frc2017.subsystems.Intake;
import com.palyrobotics.frc2017.subsystems.Spatula;

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
		if(prevCommands.wantedDriveState != Drive.DriveState.OFF_BOARD_CONTROLLER) {
			newCommands.wantedDriveState = Drive.DriveState.CHEZY;
		}

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
			newCommands.addWantedRoutine(new ManualSliderControlRoutine());
		} else if (mSliderStick.getRawButton(3)) {	// preferred thumb position
			newCommands.robotSetpoints.sliderSetpoint = Slider.SliderTarget.CENTER;
			newCommands.addWantedRoutine(new SliderDistancePositioningRoutine());
		} else if (mSliderStick.getRawButton(4)) {
			newCommands.robotSetpoints.sliderSetpoint = Slider.SliderTarget.LEFT;
			if (sliderLeft.twice()) {
				newCommands.addWantedRoutine(new SliderDistancePositioningRoutine());
			} else {
				newCommands.addWantedRoutine(new SliderDistancePositioningAutocorrectRoutine(Slider.SliderTarget.LEFT));
			}
		} else if (mSliderStick.getRawButton(5)) {
			newCommands.robotSetpoints.sliderSetpoint = Slider.SliderTarget.RIGHT;
			if (sliderRight.twice()) {
				newCommands.addWantedRoutine(new SliderDistancePositioningRoutine());
			} else {
				newCommands.addWantedRoutine(new SliderDistancePositioningAutocorrectRoutine(Slider.SliderTarget.RIGHT));
			}
		} else if (Slider.getInstance().getSliderState() == Slider.SliderState.IDLE) {
			newCommands.addWantedRoutine(new ManualSliderControlRoutine());
		}
		
		// Spatula
		if (mSliderStick.getRawButton(8)) {
			newCommands.wantedSpatulaState = Spatula.SpatulaState.UP;
		} else if (mSliderStick.getRawButton(9)) {
			newCommands.wantedSpatulaState = Spatula.SpatulaState.DOWN;
		}

		// Intake
		if (mSliderStick.getRawButton(1)) {
			newCommands.wantedIntakeState = Intake.IntakeState.INTAKE;
		} else if (mSliderStick.getRawButton(11)) {
			newCommands.wantedIntakeState = Intake.IntakeState.EXPEL;
		} else {
			newCommands.wantedIntakeState = Intake.IntakeState.IDLE;
		}
		
		// Climber
		if (mClimberStick.getY() != 0) {
			newCommands.wantedClimberState = Climber.ClimberState.MANUAL;
		} else {
			newCommands.wantedClimberState = Climber.ClimberState.IDLE;
		}
		// Left Stick trigger cancels current routine
		newCommands.cancelCurrentRoutines = mDriveStick.getTrigger();

		newCommands.leftStickInput = new JoystickInput(mDriveStick.getX(), mDriveStick.getY(), mDriveStick.getTrigger());
		newCommands.rightStickInput = new JoystickInput(mTurnStick.getX(), mTurnStick.getY(), mTurnStick.getTrigger());
		newCommands.sliderStickInput = new JoystickInput(mSliderStick.getX(), mSliderStick.getY(), mSliderStick.getTrigger());
		newCommands.climberStickInput = new JoystickInput(mClimberStick.getX(), mClimberStick.getY(), mClimberStick.getTrigger());

		return newCommands;
	}
}