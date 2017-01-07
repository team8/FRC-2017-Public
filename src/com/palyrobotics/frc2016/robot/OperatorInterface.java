package com.palyrobotics.frc2016.robot;

import com.palyrobotics.frc2016.config.Constants;
import edu.wpi.first.wpilibj.Joystick;

import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.config.Commands.*;
import com.palyrobotics.frc2016.config.Commands.JoystickInput.XboxInput;
import com.palyrobotics.frc2016.subsystems.Drive.DriveGear;
import com.palyrobotics.frc2016.util.XboxController;
import com.palyrobotics.frc2016.robot.team254.lib.util.Latch;

/**
 * Used to produce Commands {@link Commands} from human input
 * Singleton class. Should only be used in robot package.
 * @author Nihar
 *
 */
public class OperatorInterface {
	private static OperatorInterface mInstance = new OperatorInterface();

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
        // xu: if !m_testing:
		if(Constants.kRobotName == Constants.RobotName.TYR) {
			updateTyrCommands();
			m_commands.operatorStickInput = new XboxInput(((XboxController) operatorStick).getLeftX(), ((XboxController) operatorStick).getLeftY(), ((XboxController) operatorStick).getRightX(), ((XboxController) operatorStick).getRightY());
		} else {
			updateDericaCommands();
			m_commands.operatorStickInput = new XboxInput(operatorStick.getX(), operatorStick.getY(), operatorStick.getX(), operatorStick.getY());
		}
		m_commands.leftStickInput = new JoystickInput(leftStick.getX(), leftStick.getY(), leftStick.getTrigger());
		m_commands.rightStickInput = new JoystickInput(rightStick.getX(), rightStick.getY(), rightStick.getTrigger());
		// xu: serialize it
		// cmd_str = m_commands.serialize();
		// m_file.append(cmd_str);
		// xu: else: (if m_testing) {
		//     cmd_str = m_file.readline();
		//     m_command.deserialize(cmd_str);
		//     }
	}
	
	public void updateDericaCommands() {		
		// Operator Stick - Derica Intake Control
		if (operatorStick.getRawButton(5)) {
			m_commands.intakeRequest = Commands.IntakeRequest.EXPEL;
			m_commands.low_request = Commands.LowGoalShooterRequest.SHOOT;
		} else if (operatorStick.getRawButton(3)) {
			m_commands.intakeRequest = Commands.IntakeRequest.INTAKE;
			m_commands.low_request = Commands.LowGoalShooterRequest.LOAD;
		} else if(operatorStick.getRawButton(4)) {
			m_commands.routine_request = Commands.Routines.ENCODER_DRIVE;
		}
		else {
			m_commands.intakeRequest = Commands.IntakeRequest.NONE;
			m_commands.low_request = Commands.LowGoalShooterRequest.NONE;
			m_commands.routine_request = Commands.Routines.NONE;
		}

		// Left Stick trigger cancels current routine
		m_commands.cancel_current_routine = leftStick.getTrigger(); // Cancels routine?
	}
	
	public void updateTyrCommands() {
		// Operator Stick - Intake Control
		if (((XboxController) operatorStick).getRightTriggerPressed()) {
			m_commands.intakeRequest = Commands.IntakeRequest.INTAKE;
		} else if (((XboxController) operatorStick).getLeftTriggerPressed()) {
			m_commands.intakeRequest = Commands.IntakeRequest.EXPEL;
		} else {
			m_commands.intakeRequest = Commands.IntakeRequest.NONE;
		}
		// Operator Stick - Shooter Control
		if (((XboxController) operatorStick).getButtonX()) {
			m_commands.shooter_request = Commands.ShooterRequest.EXTEND;
		} else if (((XboxController) operatorStick).getButtonB()) {
			m_commands.shooter_request = Commands.ShooterRequest.RETRACT;
		} else {
			m_commands.shooter_request = Commands.ShooterRequest.NONE;
		}
		// Operator Stick - Latch Control
		if (((XboxController) operatorStick).getButtonA()) {
			m_commands.latch_request = Commands.LatchRequest.LOCK;
		} else if (((XboxController) operatorStick).getButtonY()) {
			m_commands.latch_request = Commands.LatchRequest.UNLOCK;
		} else {
			m_commands.latch_request = Commands.LatchRequest.NONE;
		}
		// Operator Stick - Grabber Control
		if (((XboxController) operatorStick).getLeftBumper()) {
			m_commands.grabber_request = Commands.GrabberRequest.RELEASE;
		} else {
			m_commands.grabber_request = Commands.GrabberRequest.GRAB;
		}
		
		// Right Stick - Activate routine
		if(rightStick.getRawButton(2)) {
			m_commands.routine_request = Routines.AUTO_ALIGN;
		} else {
			m_commands.routine_request = Routines.NONE;
		}
		
		if(rightStick.getRawButton(4)) {
			m_commands.gear_request = DriveGear.LOW;
		} else if(rightStick.getRawButton(6)) {
			m_commands.gear_request = DriveGear.HIGH;
		}
		
		// Left Stick trigger cancels current routine
		m_commands.cancel_current_routine = leftStick.getTrigger(); // Cancels routine?
	}
}