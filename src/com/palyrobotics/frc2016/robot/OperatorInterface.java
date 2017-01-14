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
	private static final OperatorInterface mInstance = new OperatorInterface();

	public static OperatorInterface getInstance() {
		return mInstance;
	}

	private OperatorInterface(/* xu: string file_path, boolean read_from_file, boolean write_to_file */) {
		// xu: m_testing = testing;
		// if (m_testing) {
		//    m_file = open(file, read);
		// } else {
		//    m_file = open(file, write);
	    // }
	}
	
	private Commands m_commands = Commands.getInstance();

	private HardwareAdapter.Joysticks joysticks = HardwareAdapter.getInstance().getJoysticks();
	private Joystick leftStick = joysticks.kLeftStick;
	private Joystick rightStick = joysticks.kRightStick;
	private Joystick operatorStick = joysticks.kOperatorStick;

	private Latch driveForwardLatch = new Latch();

//	public void reset() {
//		m_commands = new Commands();
//	}
	
	public void updateCommands() {
		if(operatorStick.getRawButton(4)) {
			m_commands.routine_request = Commands.Routines.ENCODER_DRIVE;
		}
		else {
			m_commands.routine_request = Commands.Routines.NONE;
		}
		m_commands.operatorStickInput = new XboxInput(operatorStick.getX(), operatorStick.getY(), operatorStick.getX(), operatorStick.getY());
		// Left Stick trigger cancels current routine
		m_commands.cancel_current_routine = leftStick.getTrigger();
		m_commands.leftStickInput = new JoystickInput(leftStick.getX(), leftStick.getY(), leftStick.getTrigger());
		m_commands.rightStickInput = new JoystickInput(rightStick.getX(), rightStick.getY(), rightStick.getTrigger());
	}
}