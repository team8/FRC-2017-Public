package com.palyrobotics.frc2017.robot;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.auto.AutoModeSelector;
import com.palyrobotics.frc2017.behavior.RoutineManager;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.config.dashboard.DashboardManager;
import com.palyrobotics.frc2017.subsystems.*;
import com.palyrobotics.frc2017.util.archive.SubsystemLooper;
import com.palyrobotics.frc2017.robot.team254.lib.util.RobotData;
import com.palyrobotics.frc2017.robot.team254.lib.util.SystemManager;

import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {
	// Instantiate singleton classes
	private static RobotState robotState = new RobotState();
	public static RobotState getRobotState() {
		return robotState;
	}

	// Single instance to be passed around
	private static Commands commands = new Commands();
	public static Commands getCommands() {return commands;}

	private OperatorInterface operatorInterface = OperatorInterface.getInstance();
	// Instantiate separate thread controls
	private SubsystemLooper mSubsystemLooper = new SubsystemLooper();
	private RoutineManager mRoutineManager = new RoutineManager();

	// Subsystem controllers
	private Drive mDrive = Drive.getInstance();
	private Flippers mFlippers = Flippers.getInstance();
	private Slider mSlider = Slider.getInstance();
	private Spatula mSpatula = Spatula.getInstance();
	private Intake mIntake = Intake.getInstance();
	private Climber mClimber = Climber.getInstance();

	// Hardware Updater
	private HardwareUpdater mHardwareUpdater;

	static {
		SystemManager.getInstance().add(new RobotData());
	}
	
	@Override
	public void robotInit() {
		System.out.println("Start robotInit() for "+Constants.kRobotName.toString());
		DashboardManager.robotInit();
		if (Constants.kRobotName == Constants.RobotName.STEIK || Constants.kRobotName == Constants.RobotName.AEGIR) {
			try {
				mHardwareUpdater = new HardwareUpdater(mDrive, mFlippers, mSlider, mSpatula, mIntake, mClimber);
			} catch (Exception e) {
				System.exit(1);
			}
			mSubsystemLooper.register(mDrive);
			mSubsystemLooper.register(mFlippers);
			mSubsystemLooper.register(mSlider);
			mSubsystemLooper.register(mSpatula);
			mSubsystemLooper.register(mIntake);
			mSubsystemLooper.register(mClimber);
		} else {
			try {
				mHardwareUpdater = new HardwareUpdater(mDrive);
			} catch (Exception e) {
				System.exit(1);
			}
			mSubsystemLooper.register(mDrive);
		}
		
		mHardwareUpdater.initHardware();
		System.out.println("End robotInit()");
	}

	@Override
	public void autonomousInit() {
		System.out.println("Start autonomousInit()");
		robotState.gamePeriod = RobotState.GamePeriod.AUTO;
		mHardwareUpdater.configureTalons(false);
		
		// Start control loops
		mSubsystemLooper.start();

		// Get the selected auto mode
		AutoModeBase mode = AutoModeSelector.getInstance().getAutoMode();
		// Prestart and run the auto mode
		mode.prestart();
		mRoutineManager.addNewRoutine(mode.getRoutine());
		System.out.println("End autonomousInit()");
	}

	@Override
	public void autonomousPeriodic() {
		commands = mRoutineManager.update(commands);
		mHardwareUpdater.updateSensors(robotState);
		mHardwareUpdater.updateSubsystems();
	}

	@Override
	public void teleopInit() {
		System.out.println("Start teleopInit()");
		robotState.gamePeriod = RobotState.GamePeriod.TELEOP;
		mHardwareUpdater.configureTalons(false);
		mRoutineManager.reset(commands);
		commands.wantedDriveState = Drive.DriveState.CHEZY;	//switch to chezy after auto ends
		commands = operatorInterface.updateCommands(commands);
		mSubsystemLooper.start();
		System.out.println("End teleopInit()");
	}

	@Override
	public void teleopPeriodic() {
		// Update RobotState
		mHardwareUpdater.updateSensors(robotState);
		// Gets joystick commands
		// Updates commands based on routines
		commands = mRoutineManager.update(operatorInterface.updateCommands(commands));
		//Update the hardware
		mHardwareUpdater.updateSubsystems();
	}

	@Override
	public void disabledInit() {
		System.out.println("Start disabledInit()");
		System.out.println("Current Auto Mode: " + AutoModeSelector.getInstance().getAutoMode().toString());
		robotState.gamePeriod = RobotState.GamePeriod.DISABLED;
		// Stops updating routines
		mRoutineManager.reset(commands);
		
		commands = new Commands();
		
		// Stop control loops
		mSubsystemLooper.stop();

		// Stop controllers
		mDrive.setNeutral();
		
		mHardwareUpdater.disableTalons();

		// Manually run garbage collector
		System.gc();
		
		System.out.println("Encoder: "+robotState.drivePose.leftEnc);
		System.out.println("End disabledInit()");
	}

	@Override
	public void disabledPeriodic() {
		mHardwareUpdater.updateSensors(robotState);
	}
}