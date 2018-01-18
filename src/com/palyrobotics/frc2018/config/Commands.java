package com.palyrobotics.frc2018.config;

import com.palyrobotics.frc2018.behavior.Routine;
import com.palyrobotics.frc2018.subsystems.Drive;
import com.palyrobotics.frc2018.util.archive.DriveSignal;
import com.palyrobotics.frc2018.util.logger.Logger;

import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Commands represent the desired setpoints and subsystem states for the robot. <br />
 * Store Requests (enum) for each subsystem and setpoints {@link Setpoints} <br />
 * Directly stores real or mock Joystick input in {@link JoystickInput} <br />
 * Variables are public and have default values to prevent NullPointerExceptions
 * @author Nihar
 *
 */
public class Commands {

	private static Commands instance = new Commands();
	public static Commands getInstance() {
		return instance;
	}

	private Commands() {}

	public ArrayList<Routine> wantedRoutines = new ArrayList<Routine>();

	// Store WantedStates for each subsystem state machine
	public Drive.DriveState wantedDriveState = Drive.DriveState.NEUTRAL;

	public void addWantedRoutine(Routine wantedRoutine) {
		for(Routine routine : wantedRoutines) {
			if(routine.getClass().equals(wantedRoutine.getClass())) {
				Logger.getInstance().logRobotThread(Level.WARNING, "tried to add duplicate routine", routine.getName());
				return;
			}
		}
		wantedRoutines.add(wantedRoutine);
	}

	public static void reset() {
		instance = new Commands();
	}

	/**
	 * Stores numeric setpoints
	 * @author Nihar
	 */
	public static class Setpoints {
		public static final Optional<Double> NULLOPT = Optional.empty();
		
		public Optional<DriveSignal> drivePowerSetpoint = Optional.empty();

		/**
		 * Resets all the setpoints
		 */
		public void reset() {
			drivePowerSetpoint = Optional.empty();
		}
	}
	// All robot setpoints
	public Setpoints robotSetpoints = new Setpoints();
	
	/**
	 * Class to store Joystick input
	 * @author Nihar
	 */
	public static class JoystickInput {

		public class XboxInput extends JoystickInput {
			public double leftX, leftY, rightX, rightY;
			public XboxInput(double leftX, double leftY, double rightX, double rightY) {
				super(leftX, leftY, false);
				this.leftX = leftX;
				this.leftY = leftY;
				this.rightX = rightX;
				this.rightY = rightY;
			}
		}
		public double x,y;
		public boolean triggerPressed;
		public JoystickInput(double x, double y, boolean triggerPressed) {
			this.x = x; this.y = y; this.triggerPressed = triggerPressed;
		}
		@Override
		public String toString() {
			return "Joystick X: "+this.x+" Y: "+ this.y;
		}
	}
	// Stores Joystick values
	public JoystickInput leftStickInput = new JoystickInput(0,0, false);
	public JoystickInput rightStickInput = new JoystickInput(0,0, false);
	// Allows you to cancel all running routines
	public boolean cancelCurrentRoutines = false;

	/**
	 * @return a copy of these commands
	 */
	public Commands copy() {
		Commands copy = new Commands();
		copy.wantedDriveState = this.wantedDriveState;

		copy.cancelCurrentRoutines = this.cancelCurrentRoutines;
		copy.leftStickInput = this.leftStickInput;
		copy.rightStickInput = this.rightStickInput;

		for (Routine r : this.wantedRoutines) {
			copy.wantedRoutines.add(r);
		}
		
		// Copy robot setpoints
		copy.robotSetpoints = new Setpoints();
		// Copy optionals that are present
		robotSetpoints.drivePowerSetpoint.ifPresent((DriveSignal signal) -> copy.robotSetpoints.drivePowerSetpoint = Optional.of(signal));
		return copy;
	}

	@Override
	public String toString() {
		String log = "";
		log+="Left Driver Stick: "+leftStickInput;
		log+="Right Driver Stick: "+rightStickInput+"\n";
		String wantedRoutineName = "";
		for(Routine r : this.wantedRoutines) {
			wantedRoutineName += r.getName() + " ";
		}
		log+="Wanted Routines: "+wantedRoutineName+"\n";

		return log;
	}
}