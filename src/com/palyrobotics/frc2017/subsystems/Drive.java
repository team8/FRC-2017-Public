package com.palyrobotics.frc2017.subsystems;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.config.dashboard.DashboardManager;
import com.palyrobotics.frc2017.config.dashboard.DashboardValue;
import com.palyrobotics.frc2017.subsystems.controllers.CANTalonDriveController;
import com.palyrobotics.frc2017.subsystems.controllers.EncoderTurnAngleController;
import com.palyrobotics.frc2017.util.*;
import com.palyrobotics.frc2017.subsystems.controllers.BangBangTurnAngleController;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.util.archive.CheesyDriveHelper;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.palyrobotics.frc2017.util.archive.SubsystemLoop;

/**
 * Represents the drivetrain
 * Uses controllers or cheesydrivehelper/proportionaldrivehelper to calculate DriveSignal
 * @author Nihar
 */
public class Drive extends Subsystem implements SubsystemLoop {
	private static Drive instance = new Drive();
	public static Drive getInstance() {
		return instance;
	}

	/* Various control states for the drivetrain
	 * Chezy - chezy drive with joystick values, offboard - can talon offboard loop
	 * on board - control loop calculated in code, open loop - use drive outputs passed in through commands
	 * neutral - do nothing
	 */
	public enum DriveState {CHEZY, OFF_BOARD_CONTROLLER, ON_BOARD_CONTROLLER, OPEN_LOOP, NEUTRAL}
	private DriveState mState = DriveState.NEUTRAL;

	// Helper class to calculate teleop output
	private CheesyDriveHelper mCDH = new CheesyDriveHelper();

	private Drive.DriveController mController = null;
	// Used for off board controllers to be called only once
	private boolean newController = false;

	// Encoder DPP
	private final double kInchesPerTick;
	private final double kWheelbaseWidth; // Get from CAD
	private final double kTurnSlipFactor; // Measure empirically
	public final double kInchesToTicks;

	// Cache poses to not be allocating at 200Hz
	private Pose mCachedPose = new Pose(0, 0, 0, 0, 0, 0, 0, 0);
	// Cached robot state, updated by looper
	private RobotState mCachedRobotState;
	// Stores output
	private DriveSignal mSignal = DriveSignal.getNeutralSignal();

	private DashboardValue motors;
	
	private DashboardValue leftEncoder;
	private DashboardValue rightEncoder;
	
	private Drive() {
		super("Drive");
		if (Constants.kRobotName == Constants.RobotName.DERICA) {
			kWheelbaseWidth = 22.0;
			kTurnSlipFactor = 1.2;
			kInchesPerTick = 0.07033622;
			kInchesToTicks = 1400 / (2 * 3.1415 * 3.5);
		} else if (Constants.kRobotName == Constants.RobotName.STEIK) {
			kWheelbaseWidth = 0;
			kTurnSlipFactor = 0;
			kInchesPerTick = 1/Constants.kDriveTicksPerInch;
			kInchesToTicks = Constants.kDriveTicksPerInch;
		} else {
			kWheelbaseWidth = 0;
			kTurnSlipFactor = 0;
			kInchesPerTick = 1/Constants.kDriveTicksPerInch;
			kInchesToTicks = Constants.kDriveTicksPerInch;
		}
		
		motors = new DashboardValue("driveSpeedUpdate");
		
		leftEncoder = new DashboardValue("leftdriveencoder");
		rightEncoder = new DashboardValue("rightdriveencoder");
	}

	/**
	 * @return DriveSignal
	 */
	public DriveSignal getDriveSignal() {
		return mSignal;
	}

	@Override
	public void start() {
		setNeutral();
	}

	/**
	 * Updates the drivetrain and its DriveSignal
	 * Pass in the newest RobotState
	 */
	@Override
	public void update(Commands commands, RobotState state) {
		mCachedRobotState = state;
		mCachedPose = state.drivePose.copy();
		boolean mIsNewState = !(mState == commands.wantedDriveState);
		mState = commands.wantedDriveState;
		
		switch(mState) {
			case CHEZY:
				setDriveOutputs(mCDH.cheesyDrive(commands, mCachedRobotState));
				break;
			case OFF_BOARD_CONTROLLER:
				if (mController == null) {
					setDriveOutputs(DriveSignal.getNeutralSignal());
					System.err.println("No offboard controller to use!");
					break;
				}
				setDriveOutputs(mController.update(mCachedRobotState));
				break;
			case ON_BOARD_CONTROLLER:
				if (mController == null) {
					System.err.println("No onboard controller to use!");
					commands.wantedDriveState = DriveState.NEUTRAL;
				} else {
					setDriveOutputs(mController.update(mCachedRobotState));
				}
				break;
			case OPEN_LOOP:
				if (commands.robotSetpoints.drivePowerSetpoint.isPresent()) {
					setDriveOutputs(commands.robotSetpoints.drivePowerSetpoint.get());
				}
				break;
			case NEUTRAL:
				if(!newController && mIsNewState) {
					resetController();
				}
				setDriveOutputs(DriveSignal.getNeutralSignal());
				
				if(mCachedRobotState.gamePeriod.equals(RobotState.GamePeriod.TELEOP)) {
					if(mIsNewState) {
						resetController();
					}
					commands.wantedDriveState = DriveState.CHEZY;
				}
				break;
		}
		
		mIsNewState = false;
		mState = commands.wantedDriveState;
		
		leftEncoder.updateValue(state.drivePose.leftEnc);
		rightEncoder.updateValue(state.drivePose.rightEnc);
		
		DashboardManager.getInstance().publishKVPair(leftEncoder);
		DashboardManager.getInstance().publishKVPair(rightEncoder);

		motors.updateValue(state.drivePose.leftSpeed + ", " + state.drivePose.rightSpeed);
		DashboardManager.getInstance().publishKVPair(motors);
	}

	@Override
	public void stop() {
	}

	private void setDriveOutputs(DriveSignal signal) {
		mSignal = signal;
	}

	/**
	 * Used when external reset of drivetrain is desired
	 */
	public void setNeutral() {
		mController = null;
		mState = DriveState.NEUTRAL;
		setDriveOutputs(DriveSignal.getNeutralSignal());
	}

	public void setCANTalonController(DriveSignal signal) {
		mController = new CANTalonDriveController(signal);
		newController = true;
	}

	public void setTurnAngleSetpoint(double heading) {
		mController = new BangBangTurnAngleController(mCachedPose, heading);
		newController = true;
	}
	
	public void setTurnAngleEncoderSetpoint(double angle) {
		System.out.println("Encoder angle "+angle);
		mController = new EncoderTurnAngleController(mCachedPose, angle);
		newController = true;
	}

	// Wipes current controller
	public void resetController() {
		mController = null;
	}

	/**
	 * @return The pose according to the current sensor state
	 */
	public Pose getPose() {
		// If drivetrain has not had first update yet, return initial robot pose of 0,0,0,0,0,0
		if(mCachedRobotState == null) {
			return new Pose(0,0,0,0,0,0,0,0);
		}
		return mCachedPose;
	}

	public Drive.DriveController getController() {
		return mController;
	}

	public boolean controllerOnTarget() {
		return (mController==null || mController.onTarget());
	}

	public boolean hasController() {
		return mController != null;
	}

	public interface DriveController {
		DriveSignal update(RobotState state);

		Pose getSetpoint();

		boolean onTarget();
	}

	@Override
	public String printStatus() {
		return "Drive State: " + mState + "\nOutput Control Mode: " + mSignal.leftMotor.getControlMode() +
				"\nLeft Setpoint: " + mSignal.leftMotor.getSetpoint() + "\nRight Setpoint: " + mSignal.rightMotor.getSetpoint() +
				"\nLeft Enc: "+mCachedPose.leftEnc + "\nRight Enc: "+mCachedPose.rightEnc+
				"\nGyro: "+mCachedPose.heading+"\n";
	}
}