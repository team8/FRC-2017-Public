package com.palyrobotics.frc2017.util.archive;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.team254.lib.util.LegacyPose;
import com.palyrobotics.frc2017.util.archive.team254.controllers.team254.DriveFinishLineController;
import com.palyrobotics.frc2017.util.archive.team254.controllers.team254.DrivePathController;
import com.palyrobotics.frc2017.util.archive.team254.controllers.team254.DriveStraightController;
import com.palyrobotics.frc2017.util.archive.team254.controllers.team254.TimedOpenLoopController;
import com.palyrobotics.frc2017.util.archive.team254.controllers.team254.TurnInPlaceController;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.util.archive.team254.trajectory.Path;
import com.palyrobotics.frc2017.util.Subsystem;

/**
 * Represents the drivetrain
 * Uses controllers or cheesydrivehelper/proportionaldrivehelper to calculate DriveSignal
 */
public class LegacyDrive extends Subsystem implements SubsystemLoop {
	private static LegacyDrive instance = new LegacyDrive();
	public static LegacyDrive getInstance() {
		return instance;
	}

	public enum LegacyDriveState {CHEZY, CONTROLLER, OPEN_LOOP, NEUTRAL}
	private LegacyDriveState mState = LegacyDriveState.NEUTRAL;

	// Helper classes to calculate teleop output
	private CheesyDriveHelper mCDH = new CheesyDriveHelper();
//	private ProportionalDriveHelper mPDH = new ProportionalDriveHelper();

	public interface DriveController {
		DriveSignal update(LegacyPose pose);
		LegacyPose getCurrentSetpoint();

		boolean onTarget();
	}
	private DriveController mController = null;

	// Encoder DPP
	private final double kInchesPerTick;
	private final double kWheelbaseWidth; // Get from CAD
	private final double kTurnSlipFactor; // Measure empirically
	public final double kInchesToTicks;

	// Cache poses to not allocated at 200Hz
	private LegacyPose mCachedPose = new LegacyPose(0, 0, 0, 0, 0, 0);
	// Cached robot state, updated by looper
	private RobotState mCachedRobotState;
	// Stores output
	private DriveSignal mSignal = DriveSignal.getNeutralSignal();

	private LegacyDrive() {
		super("LegacyDrive");
		if (Constants.kRobotName == Constants.RobotName.DERICA) {
			kWheelbaseWidth = 22.0;
			kTurnSlipFactor = 1.2;
			kInchesPerTick = 0.07033622;
			kInchesToTicks = 1400 / (2 * 3.1415 * 3.5);
		} else if (Constants.kRobotName == Constants.RobotName.AEGIR) {
			kWheelbaseWidth = 0;
			kTurnSlipFactor = 0;
			kInchesPerTick = 0.0;
			kInchesToTicks = 0;
		} else if (Constants.kRobotName == Constants.RobotName.STEIK) {
			kWheelbaseWidth = 0;
			kTurnSlipFactor = 0;
			kInchesPerTick = 0.0;
			kInchesToTicks = 0;
		} else {
			// Old Tyr constants
			kWheelbaseWidth = 26.0;
			kTurnSlipFactor = 1.2;
			kInchesPerTick = 0.184;
			kInchesToTicks = 0;
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
		mCachedRobotState = state;
//		mState = commands.wantedDriveState;
		Commands.Setpoints setpoints = commands.robotSetpoints;
		// Call methods associated with any setpoints that are present
		// Encoder drive distance routine
//		setpoints.encoder_drive_setpoint.ifPresent(this.setDistanceSetpoint(setpoints.encoder_drive_setpoint));

		switch(mState) {
			case CHEZY:
				setDriveOutputs(mCDH.cheesyDrive(commands, mCachedRobotState));
				break;
			case CONTROLLER:
				setDriveOutputs(mController.update(getPhysicalPose()));
				break;
			case OPEN_LOOP:
				setDriveOutputs(commands.robotSetpoints.drivePowerSetpoint.get());
			case NEUTRAL:
				setDriveOutputs(DriveSignal.getNeutralSignal());
				break;
		}
	}

	@Override
	public void stop() {
	}

	private void setDriveOutputs(DriveSignal signal) {
		mSignal = signal;
	}

	public void setOpenLoop(DriveSignal signal) {
		mController = null;
		setDriveOutputs(signal);
	}

	public void setDistanceSetpoint(double distance) {
		setDistanceSetpoint(distance, Constants2016.kDriveMaxSpeedInchesPerSec);
	}
	public void setDistanceSetpoint(double distance, double velocity) {
		// 0 < vel < max_vel
		double velToUse = Math.min(Constants2016.kDriveMaxSpeedInchesPerSec, Math.max(velocity, 0));
		mController = new DriveStraightController(
				getPoseToContinueFrom(false),
				distance,
				velToUse);
	}

	public void setAutoAlignSetpoint(double heading) {
		// Check if already turning to that setpoint
		if(mController instanceof GyroTurnAngleController) {
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
		mController = (DriveController) new TimedOpenLoopController(velocity, time, 0, 1.5);
	}
	
	public void setTimerDriveSetpoint(double velocity, double time, double decelTime) {
		mController = (DriveController) new TimedOpenLoopController(velocity, time, 0, decelTime);
	}
	
	public void setTimerDriveSetpoint(double startPower, double timeFullOn, double endPower, double timeToDecel) {
		mController = (DriveController) new TimedOpenLoopController(startPower, timeFullOn, endPower, timeToDecel);
		
	}
	
	public void setTurnSetpoint(double heading) {
		setTurnSetpoint(heading, Constants2016.kTurnMaxSpeedRadsPerSec);
	}
	
	public void setTurnSetpoint(double heading, double velocity) {
		velocity = Math.min(Constants2016.kTurnMaxSpeedRadsPerSec, Math.max(velocity, 0));
		mController = new TurnInPlaceController(getPoseToContinueFrom(true), heading, velocity);
	}

	public void setEncoderTurnAngleSetpoint(double heading) {
		setEncoderTurnAngleSetpoint(heading, 1);
	}
	
	public void setEncoderTurnAngleSetpoint(double heading, double maxVel) {
		mController = new EncoderTurnAngleController(getPoseToContinueFrom(true), heading, maxVel);
	}

	public void setGyroTurnAngleSetpoint(double heading) {
		setGyroTurnAngleSetpoint(heading, 0.7);
	}
	
	public void setGyroTurnAngleSetpoint(double heading, double maxVel) {
		mController = new GyroTurnAngleController(getPoseToContinueFrom(true), heading, maxVel);
	}

	// Wipes current controller
	public void resetController() {
		mController = null;
	}

	public void setPathSetpoint(Path path) {
		resetController();
		mController = new DrivePathController(path);
	}

	public void setFinishLineSetpoint(double distance, double heading) {
		resetController();
		mController = new DriveFinishLineController(distance, heading, 1.0);
	}

	private LegacyPose getPoseToContinueFrom(boolean forTurnController) {
		if (!forTurnController && mController instanceof TurnInPlaceController) {
			LegacyPose poseToUse = getPhysicalPose();
			poseToUse.heading = ((TurnInPlaceController) mController).getHeadingGoal();
			poseToUse.headingVelocity = 0;
			return poseToUse;
		} else if (mController == null || (mController instanceof DriveStraightController && forTurnController)) {
			return getPhysicalPose();
		} else if (mController instanceof DriveFinishLineController) {
			return getPhysicalPose();
		} else if (mController.onTarget()) {
			return mController.getCurrentSetpoint();
		} else {
			return getPhysicalPose();
		}
	}

	/**
	 * @return The pose according to the current sensor state
	 */
	public LegacyPose getPhysicalPose() {
		// If drivetrain has not had first update yet, return initial robot pose of 0,0,0,0,0,0
		if(mCachedRobotState == null) {
			return new LegacyPose(0,0,0,0,0,0);
		}
		return mCachedPose;
	}

	public LegacyDrive.DriveController getController() {
		return mController;
	}

	public boolean controllerOnTarget() {
		return mController != null && mController.onTarget();
	}

	public boolean hasController() {
		return mController != null;
	}
}