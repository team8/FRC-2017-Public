package com.palyrobotics.frc2016.robot;

import edu.wpi.first.wpilibj.Joystick;

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
	private static final OperatorInterface instance = new OperatorInterface();

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
	
	public void updateCommands() {
		if(mOperatorStick.getRawButton(4)) {
			mCommands.routineRequest = Commands.Routines.ENCODER_DRIVE;
		}
		else {
			mCommands.routineRequest = Commands.Routines.NONE;
		}
		mCommands.operatorStickInput = new XboxInput(mOperatorStick.getX(), mOperatorStick.getY(), mOperatorStick.getX(), mOperatorStick.getY());
		// Left Stick trigger cancels current routine
		mCommands.cancelCurrentRoutine = mLeftStick.getTrigger();
		mCommands.leftStickInput = new JoystickInput(mLeftStick.getX(), mLeftStick.getY(), mLeftStick.getTrigger());
		mCommands.rightStickInput = new JoystickInput(mRightStick.getX(), mRightStick.getY(), mRightStick.getTrigger());
	}
}