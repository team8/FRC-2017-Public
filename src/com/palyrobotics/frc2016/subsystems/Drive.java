package com.palyrobotics.frc2016.subsystems;

import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.config.RobotState;
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
	private static Drive instance = new Drive();
	public static Drive getInstance() {
		return instance;
	}
	// Helper classes to calculate teleop output
	private CheesyDriveHelper mCDH = new CheesyDriveHelper();
//	private ProportionalDriveHelper pdh = new ProportionalDriveHelper();

	public interface DriveController {
		DriveSignal update(Pose pose);
		Pose getCurrentSetpoint();

		boolean onTarget();
	}
	private DriveController mController = null;

	// Derica is always considered high gear
	public enum DriveGear {HIGH, LOW}
	private DriveGear mGear;

	// Encoder DPP
	private final double kInchesPerTick;
	private final double kWheelbaseWidth; // Get from CAD
	private final double kTurnSlipFactor; // Measure empirically

	// Cache poses to not allocated at 200Hz
	private Pose mCachedPose = new Pose(0, 0, 0, 0, 0, 0);
	// Cached robot state, updated by looper
	private RobotState mCachedRobotState;
	// Stores output
	private DriveSignal mSignal = DriveSignal.NEUTRAL;

	private Drive() {
		super("Drive");
		if(Constants.kRobotName == Constants.RobotName.TYR) {
			kWheelbaseWidth = 26.0;
			kTurnSlipFactor = 1.2;
			kInchesPerTick = 0.184;
		}
		else {
			kWheelbaseWidth = 22.0;
			kTurnSlipFactor = 1.2;
			kInchesPerTick = 0.07033622;
			mGear = DriveGear.HIGH;
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
		Commands.Setpoints setpoints = commands.robotSetpoints;
		// Call methods associated with any setpoints that are present
		// Encoder drive distance routine
//		setpoints.encoder_drive_setpoint.ifPresent(this.setDistanceSetpoint(setpoints.encoder_drive_setpoint));

		if(mController == null && mCachedRobotState.gamePeriod == RobotState.GamePeriod.TELEOP && commands.routineRequest == Commands.Routines.NONE) {
			setDriveOutputs(mCDH.cheesyDrive(commands, mCachedRobotState));
		}
		else if (mController == null) {
			setDriveOutputs(DriveSignal.NEUTRAL);
		}
		else {
			setDriveOutputs(mController.update(getPhysicalPose()));
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
		mController = null;
		setDriveOutputs(signal);
	}

	public void setDistanceSetpoint(double distance) {
		setDistanceSetpoint(distance, Constants.kDriveMaxSpeedInchesPerSec);
	}
	public void setDistanceSetpoint(double distance, double velocity) {
		// 0 < vel < max_vel
		double velToUse = Math.min(Constants.kDriveMaxSpeedInchesPerSec, Math.max(velocity, 0));
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
		setTurnSetpoint(heading, Constants.kTurnMaxSpeedRadsPerSec);
	}
	
	public void setTurnSetpoint(double heading, double velocity) {
		velocity = Math.min(Constants.kTurnMaxSpeedRadsPerSec, Math.max(velocity, 0));
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
	
	public void setBangBangTurnAngleSetpoint(double heading) {
		mController = new BangBangTurnAngleController(getPoseToContinueFrom(true), heading);
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

	private Pose getPoseToContinueFrom(boolean forTurnController) {
		if (!forTurnController && mController instanceof TurnInPlaceController) {
			Pose poseToUse = getPhysicalPose();
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
	public Pose getPhysicalPose() {
		// If drivetrain has not had first update yet, return initial robot pose of 0,0,0,0,0,0
		if(mCachedRobotState == null) {
			return new Pose(0,0,0,0,0,0);
		}
		mCachedPose = mCachedRobotState.drivePose;
		return mCachedPose;
	}

	public Drive.DriveController getController() {
		return mController;
	}

	public boolean controllerOnTarget() {
		return mController != null && mController.onTarget();
	}

	public boolean hasController() {
		return mController != null;
	}
}