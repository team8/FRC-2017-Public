package com.palyrobotics.frc2016.robot;

import edu.wpi.first.wpilibj.Joystick;

import com.palyrobotics.frc2016.behavior.Routine;
import com.palyrobotics.frc2016.behavior.routines.EncoderDriveRoutine;
import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.config.Commands.*;
import com.palyrobotics.frc2016.config.Commands.JoystickInput.XboxInput;
import com.palyrobotics.frc2016.robot.team254.lib.util.Latch;

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

	private OperatorInterface(/* xu: string file_path, boolean read_from_file, boolean write_to_file */) {
		// xu: m_testing = testing;
		// if (m_testing) {
		//    m_file = open(file, read);
		// } else {
		//    m_file = open(file, write);
	    // }
	}
	
	private Commands mCommands = Commands.getInstance();

	private HardwareAdapter.Joysticks mJoysticks = HardwareAdapter.getInstance().getJoysticks();
	private Joystick mLeftStick = mJoysticks.leftStick;
	private Joystick mRightStick = mJoysticks.rightStick;
	private Joystick mOperatorStick = mJoysticks.operatorStick;

	private Latch mDriveForwardLatch = new Latch();

//	public void reset() {
//		m_commands = new Commands();
//	}
	
	public void addWantedRoutine(Routine wantedRoutine) {
		for(Routine routine : mCommands.wantedRoutines) {
			if(routine.getClass().equals(wantedRoutine.getClass())) {
				return;
			}
		}
	}
	
	public void updateCommands() {
		// TODO: Change how routines are commanded
		if(mOperatorStick.getRawButton(4)) {
			mCommands.addWantedRoutine(new EncoderDriveRoutine(500));
		}
		mCommands.operatorStickInput = new XboxInput(mOperatorStick.getX(), mOperatorStick.getY(), mOperatorStick.getX(), mOperatorStick.getY());
		// Left Stick trigger cancels current routine
		mCommands.cancelCurrentRoutines = mLeftStick.getTrigger();
		mCommands.leftStickInput = new JoystickInput(mLeftStick.getX(), mLeftStick.getY(), mLeftStick.getTrigger());
		mCommands.rightStickInput = new JoystickInput(mRightStick.getX(), mRightStick.getY(), mRightStick.getTrigger());
	}
}