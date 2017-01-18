package com.palyrobotics.frc2016.subsystems;

import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.config.RobotState;
import com.palyrobotics.frc2016.robot.Robot;
import com.palyrobotics.frc2016.robot.team254.lib.util.DriveSignal;
import com.palyrobotics.frc2016.robot.team254.lib.util.Pose;
import com.palyrobotics.frc2016.subsystems.controllers.BangBangTurnAngleController;
import com.palyrobotics.frc2016.subsystems.controllers.EncoderTurnAngleController;
import com.palyrobotics.frc2016.subsystems.controllers.GyroTurnAngleController;
import com.palyrobotics.frc2016.subsystems.controllers.team254.DriveFinishLineController;
import com.palyrobotics.frc2016.subsystems.controllers.team254.DrivePathController;
import com.palyrobotics.frc2016.subsystems.controllers.team254.DriveStraightController;
import com.palyrobotics.frc2016.subsystems.controllers.team254.TimedOpenLoopController;
import com.palyrobotics.frc2016.subsystems.controllers.team254.TurnInPlaceController;
import com.palyrobotics.frc2016.util.CheesyDriveHelper;
import com.palyrobotics.frc2016.config.Constants;
import com.palyrobotics.frc2016.util.Subsystem;
import com.palyrobotics.frc2016.robot.team254.lib.trajectory.Path;

import com.palyrobotics.frc2016.util.SubsystemLoop;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

/**
 * Represents the drivetrain
 * Uses controllers or cheesydrivehelper/proportionaldrivehelper to calculate DriveSignal
 */
public class Drive extends Subsystem implements SubsystemLoop {
	private static Drive instance_ = new Drive();
	public static Drive getInstance() {
		return instance_;
	}
	// Helper classes to calculate teleop output
	private CheesyDriveHelper cdh = new CheesyDriveHelper();
//	private ProportionalDriveHelper pdh = new ProportionalDriveHelper();

	public interface DriveController {
		DriveSignal update(Pose pose);
		Pose getCurrentSetpoint();

		boolean onTarget();
	}
	private DriveController m_controller = null;

	// Derica is always considered high gear
	public enum DriveGear {HIGH, LOW}
	private DriveGear mGear;

	// Encoder DPP
	private final double m_inches_per_tick;
	private final double m_wheelbase_width; // Get from CAD
	private final double m_turn_slip_factor; // Measure empirically
	public final double INCHES_TO_TICKS;

	// Cache poses to not allocated at 200Hz
	private Pose m_cached_pose = new Pose(0, 0, 0, 0, 0, 0);
	// Cached robot state, updated by looper
	private RobotState m_cached_robot_state;
	// Stores output
	private DriveSignal mSignal = DriveSignal.NEUTRAL;

	private Drive() {
		super("Drive");
		if(Constants.kRobotName == Constants.RobotName.TYR) {
			m_wheelbase_width = 26.0;
			m_turn_slip_factor = 1.2;
			m_inches_per_tick = 0.184;
			INCHES_TO_TICKS = 1400 / (2 * 3.1415 * 3.5);
		}
		else {
			m_wheelbase_width = 22.0;
			m_turn_slip_factor = 1.2;
			m_inches_per_tick = 0.07033622;
			mGear = DriveGear.HIGH;
			INCHES_TO_TICKS = 1400 / (2 * 3.1415 * 3.5);
		}
	}

	/**
	 * @return DriveSignal
	 */
	public DriveSignal getDriveSignal() {
		return mSignal;
	}

	@Override
	public void start() {
	}

	/**
	 * Updates the drivetrain and its DriveSignal
	 * Pass in the newest RobotState
	 */
	@Override
	public void update(Commands commands, RobotState state) {
		m_cached_robot_state = state;
		Commands.Setpoints setpoints = commands.robotSetpoints;
		// Call methods associated with any setpoints that are present
		// Encoder drive distance routine
//		setpoints.encoder_drive_setpoint.ifPresent(this.setDistanceSetpoint(setpoints.encoder_drive_setpoint));

		if(m_controller==null && m_cached_robot_state.gamePeriod==RobotState.GamePeriod.TELEOP && commands.routine_request == Commands.Routines.NONE) {
			setDriveOutputs(cdh.cheesyDrive(commands, m_cached_robot_state));
		}
		else if (m_controller==null) {
			setDriveOutputs(DriveSignal.NEUTRAL);
		}
		else {
			setDriveOutputs(m_controller.update(getPhysicalPose()));
		}
	}

	@Override
	public void stop() {
	}

	private void setDriveOutputs(DriveSignal signal) {
		mSignal = signal;
	}

	/**
	 * Allows shifting of gear - Note that Derica cannot shift gears
	 *
	 * @param targetGear Desired gear to shift to
	 * @return What the shifterSolenoid should be set to
	 */
	public DoubleSolenoid.Value setGear(DriveGear targetGear) {
		if (Constants.kRobotName == Constants.RobotName.DERICA) {
			System.err.println("No gear shifting on Derica");
			return null;
		}
		switch (targetGear) {
			case HIGH:
				return Value.kForward;
			case LOW:
				return Value.kReverse;
		}
		return null;
	}

	public boolean isHighGear() {
		return mGear == DriveGear.HIGH;
	}

	public void setOpenLoop(DriveSignal signal) {
		m_controller = null;
		setDriveOutputs(signal);
	}

	public void setDistanceSetpoint(double distance) {
		setDistanceSetpoint(distance, Constants.kDriveMaxSpeedInchesPerSec);
	}
	public void setDistanceSetpoint(double distance, double velocity) {
		// 0 < vel < max_vel
		double vel_to_use = Math.min(Constants.kDriveMaxSpeedInchesPerSec, Math.max(velocity, 0));
		m_controller = new DriveStraightController(
				getPoseToContinueFrom(false),
				distance,
				vel_to_use);
	}

	public void setAutoAlignSetpoint(double heading) {
		// Check if already turning to that setpoint
		if(m_controller instanceof GyroTurnAngleController) {
//			if(m_controller.getCurrentSetpoint().getHeading()-getPhysicalPose().getHeading() != heading) {
//				// New auto align iteration
//				System.out.println("New auto align setpoint");
//				setGyroTurnAngleSetpoint(heading);
//			}
		} else {
			System.out.println("Started auto align controller");
			setGyroTurnAngleSetpoint(heading, 0.45);
		}
	}

	public void setTimerDriveSetpoint(double velocity, double time) {
		m_controller = (DriveController) new TimedOpenLoopController(velocity, time, 0, 1.5);
	}
	
	public void setTimerDriveSetpoint(double velocity, double time, double decel_time) {
		m_controller = (DriveController) new TimedOpenLoopController(velocity, time, 0, decel_time);
	}
	
	public void setTimerDriveSetpoint(double start_power, double time_full_on, double end_power, double time_to_decel) {
		m_controller = (DriveController) new TimedOpenLoopController(start_power, time_full_on, end_power, time_to_decel);
		
	}
	
	public void setTurnSetpoint(double heading) {
		setTurnSetpoint(heading, Constants.kTurnMaxSpeedRadsPerSec);
	}
	
	public void setTurnSetpoint(double heading, double velocity) {
		velocity = Math.min(Constants.kTurnMaxSpeedRadsPerSec, Math.max(velocity, 0));
		m_controller = new TurnInPlaceController(getPoseToContinueFrom(true), heading, velocity);
	}

	public void setEncoderTurnAngleSetpoint(double heading) {
		setEncoderTurnAngleSetpoint(heading, 1);
	}
	
	public void setEncoderTurnAngleSetpoint(double heading, double maxVel) {
		m_controller = new EncoderTurnAngleController(getPoseToContinueFrom(true), heading, maxVel);
	}

	public void setGyroTurnAngleSetpoint(double heading) {
		setGyroTurnAngleSetpoint(heading, 0.7);
	}
	
	public void setGyroTurnAngleSetpoint(double heading, double maxVel) {
		m_controller = new GyroTurnAngleController(getPoseToContinueFrom(true), heading, maxVel);
	}
	
	public void setBangBangTurnAngleSetpoint(double heading) {
		m_controller = new BangBangTurnAngleController(getPoseToContinueFrom(true), heading);
	}

	// Wipes current controller
	public void resetController() {
		m_controller = null;
	}

	public void setPathSetpoint(Path path) {
		resetController();
		m_controller = new DrivePathController(path);
	}

	public void setFinishLineSetpoint(double distance, double heading) {
		resetController();
		m_controller = new DriveFinishLineController(distance, heading, 1.0);
	}

	private Pose getPoseToContinueFrom(boolean for_turn_controller) {
		if (!for_turn_controller && m_controller instanceof TurnInPlaceController) {
			Pose pose_to_use = getPhysicalPose();
			pose_to_use.m_heading = ((TurnInPlaceController) m_controller).getHeadingGoal();
			pose_to_use.m_heading_velocity = 0;
			return pose_to_use;
		} else if (m_controller == null || (m_controller instanceof DriveStraightController && for_turn_controller)) {
			return getPhysicalPose();
		} else if (m_controller instanceof DriveFinishLineController) {
			return getPhysicalPose();
		} else if (m_controller.onTarget()) {
			return m_controller.getCurrentSetpoint();
		} else {
			return getPhysicalPose();
		}
	}

	/**
	 * @return The pose according to the current sensor state
	 */
	public Pose getPhysicalPose() {
		// If drivetrain has not had first update yet, return initial robot pose of 0,0,0,0,0,0
		if(m_cached_robot_state == null) {
			return new Pose(0,0,0,0,0,0);
		}
		m_cached_pose = m_cached_robot_state.getDrivePose();
		return m_cached_pose;
	}

	public Drive.DriveController getController() {
		return m_controller;
	}

	public boolean controllerOnTarget() {
		return m_controller != null && m_controller.onTarget();
	}

	public boolean hasController() {
		return m_controller != null;
	}
}