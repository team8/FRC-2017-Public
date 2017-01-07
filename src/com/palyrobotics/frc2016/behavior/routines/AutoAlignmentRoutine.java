package com.palyrobotics.frc2016.behavior.routines;

import java.util.Optional;

import com.palyrobotics.frc2016.behavior.Routine;
import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.robot.team254.lib.util.DriveSignal;

import com.palyrobotics.frc2016.util.Subsystem;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class AutoAlignmentRoutine extends Routine {
	/* Start = Start of the routine
	 * Set_Angle = wait for vision, then set angle
	 * Aligning = waiting while robot turns
	 * Done = no goal spotted, or finished iterations
	 */
	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{drive};
	}

	private enum AutoAlignStates {
		START, SET_ANGLE, ALIGNING, DONE
	}

	public AutoAlignStates m_state = AutoAlignStates.START;
	private boolean m_is_new_state = true;
	private NetworkTable table = NetworkTable.getTable("visiondata");
	// Threshold angle for which we will turn
	private final double m_min_angle = 3;
	
	// Number of iterations for successive auto alignments
	private final int m_default_iterations = 2;
	private int m_iterations = m_default_iterations;

	// Timer used for waiting period for camera stabilization
	private Timer m_timer = new Timer();
	private final double m_wait_time = 1.5; 
	
	
	Commands.Setpoints setpoints;

	/**
	 * Changes number of successive alignments
	 */
	public void setIterations(int iterations) {
		this.m_iterations = iterations;
	}

	@Override
	public Commands update(Commands commands) {
		Commands.Setpoints setpoints = commands.robotSetpoints;
		AutoAlignStates new_state = m_state;
		switch(m_state) {
		case START:
			if(m_iterations > 0) {
				m_timer.reset();
				m_timer.start();
				drive.resetController();
				setpoints.auto_align_setpoint = Commands.Setpoints.m_nullopt;
				System.out.println("Started auto align " + m_state);
				new_state = AutoAlignStates.SET_ANGLE;
			} else {
				new_state = AutoAlignStates.DONE;
			}
			setpoints.currentRoutine = Commands.Routines.AUTO_ALIGN;
			break;
		case SET_ANGLE:
			// Wait for m_wait_time before reading vision data (latency)
			if(m_timer.get() < m_wait_time) {
				break;
			}
			// If angle turnpoint has been set, then set this routine to waiting for alignment
			if(setpoints.auto_align_setpoint.isPresent()) {
				System.out.println("Already set angle setpoint");
				new_state = AutoAlignStates.ALIGNING;
				break;
			}
			// Check for no goal, then already aligned, otherwise set setpoint
			double skewAngle = table.getNumber("skewangle", 10000)/2;
			if(skewAngle == 10000/2) {
				System.out.println(skewAngle);
				System.out.println("No goal detected");
				m_iterations = 0;
				new_state = AutoAlignStates.DONE;				
			}
			else if(Math.abs(skewAngle) <= m_min_angle) {
				System.out.println("Already aligned");
			} else {
//				skewAngle = (skewAngle >=0) ? skewAngle-2:skewAngle+2;
				setpoints.auto_align_setpoint = Optional.of(skewAngle);
				System.out.println("setpoint #"+m_iterations+": "+setpoints.auto_align_setpoint.get());				
			}
			break;
		case ALIGNING:
//			System.out.println("aligning, waiting on controller");
			// If finished turning, start next sequence or finish
			if(drive.controllerOnTarget()) {
				System.out.println("Drive controller reached target");
				m_iterations--;
				if(m_iterations > 0) {
					System.out.println("Starting new iteration");
					new_state = AutoAlignStates.START;
				} else {
					System.out.println("Finished auto aligning");
					new_state = AutoAlignStates.DONE;
				}
			}
			break;
		case DONE:
			drive.resetController();
			break;
		}
		m_is_new_state = false;
		if(m_state != new_state) {
			m_state = new_state;
			m_is_new_state = true;
		}
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		m_state = AutoAlignStates.DONE;
		drive.setOpenLoop(DriveSignal.NEUTRAL);
		drive.resetController();
		return commands;
	}

	@Override
	public void start() {
		m_timer.reset();
		m_timer.start();
		if(m_iterations < 1) {
			m_iterations = m_default_iterations;
		}
	}

	@Override
	public boolean isFinished() {
		return m_state == AutoAlignStates.DONE && m_iterations == 0;
	}

	@Override
	public String getName() {
		return "Auto Alignment Routine";
	}

}
