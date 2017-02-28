package com.palyrobotics.frc2017.robot;

import com.palyrobotics.frc2017.auto.AutoMode;
import com.palyrobotics.frc2017.auto.AutoModeExecuter;
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
	private AutoModeExecuter mAutoModeExecuter = new AutoModeExecuter(mRoutineManager);
	private DashboardManager mDashboardManager = DashboardManager.getInstance();

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
		
		// Start control loops
		mSubsystemLooper.start();

		mDrive.resetController();
		
		AutoMode mode = AutoModeSelector.getInstance().getAutoMode();
		mAutoModeExecuter.setAutoMode(mode);
		// Prestart auto mode
		mode.prestart();
		mAutoModeExecuter.start();
		
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
		mRoutineManager.reset(commands);
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
		// Stop auto mode
		mAutoModeExecuter.stop();

		// Stop routine_request
		mRoutineManager.reset(commands);
		
		// Stop control loops
		mSubsystemLooper.stop();

		// Stop controllers
		mDrive.setNeutral();

		// Manually run garbage collector
		System.gc();

		System.out.println("End disabledInit()");
	}

	@Override
	public void disabledPeriodic() {
		mHardwareUpdater.updateSensors(robotState);
	}
}