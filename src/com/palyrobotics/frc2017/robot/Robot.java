package com.palyrobotics.frc2017.robot;

import com.palyrobotics.frc2017.auto.AutoMode;
import com.palyrobotics.frc2017.auto.AutoModeExecuter;
import com.palyrobotics.frc2017.auto.AutoModeSelector;
import com.palyrobotics.frc2017.behavior.RoutineManager;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.subsystems.*;
import com.palyrobotics.frc2017.util.Dashboard;
import com.palyrobotics.frc2017.util.SubsystemLooper;
import com.palyrobotics.frc2017.util.DriveSignal;
import com.palyrobotics.frc2017.robot.team254.lib.util.RobotData;
import com.palyrobotics.frc2017.robot.team254.lib.util.SystemManager;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Robot extends IterativeRobot {
	// Instantiate singleton classes
	private static RobotState robotState = new RobotState();
	public static RobotState getRobotState() {
		return robotState;
	}

	// Single instance to be passed around
	private static Commands commands = new Commands();
	public static Commands getCommands() {return commands;}

	private static OperatorInterface operatorInterface = OperatorInterface.getInstance();
	// Instantiate separate thread controls
	private SubsystemLooper mSubsystemLooper = new SubsystemLooper();
	private RoutineManager mRoutineManager = new RoutineManager();
	private AutoModeExecuter mAutoModeExecuter = new AutoModeExecuter(commands, mRoutineManager);

	// Subsystem controllers
	private Drive mDrive = Drive.getInstance();
	private Flippers mFlippers = Flippers.getInstance();
	private Slider mSlider = Slider.getInstance();
	private Spatula mSpatula = Spatula.getInstance();
	private Intake mIntake = Intake.getInstance();
	private Climber mClimber = Climber.getInstance();

	// Hardware Updater
	private HardwareUpdater mHardwareUpdater;

	private Dashboard mDashboard = Dashboard.getInstance();
	NetworkTable sensorTable;

	static {
		SystemManager.getInstance().add(new RobotData());
	}

	@Override
	public void robotInit() {
		System.out.println("Start robotInit()");
		mSubsystemLooper.register(mDrive);

		if (Constants.kRobotName == Constants.RobotName.STEIK || Constants.kRobotName == Constants.RobotName.AEGIR) {
			mHardwareUpdater = new HardwareUpdater(mDrive, mFlippers, mSlider, mSpatula, mIntake, mClimber);
			mSubsystemLooper.register(mFlippers);
			mSubsystemLooper.register(mSlider);
			mSubsystemLooper.register(mSpatula);
			mSubsystemLooper.register(mIntake);
			mSubsystemLooper.register(mClimber);
		} else {
			mHardwareUpdater = new HardwareUpdater(mDrive);
		}
		
		//        SystemManager.getInstance().add(routineManager);
		sensorTable = NetworkTable.getTable("Sensor");
		mDashboard.init();
		System.out.println("End robotInit()");
	}

	@Override
	public void autonomousInit() {
		System.out.println("Start autonomousInit()");
		robotState.gamePeriod = RobotState.GamePeriod.AUTO;

		mDrive.resetController();
		
		AutoMode mode = AutoModeSelector.getInstance().getAutoMode();
		mAutoModeExecuter.setAutoMode(mode);
		// Prestart auto mode
		mode.prestart();
		mAutoModeExecuter.start();
		// Start control loops
		mSubsystemLooper.start();
		System.out.println("End autonomousInit()");
	}

	@Override
	public void autonomousPeriodic() {
		mRoutineManager.update(commands);
		mDashboard.update();
		mHardwareUpdater.updateSensors(robotState);
		mHardwareUpdater.updateSubsystems();
	}

	@Override
	public void teleopInit() {
		System.out.println("Start teleopInit()");
		robotState.gamePeriod = RobotState.GamePeriod.TELEOP;
		mRoutineManager.reset(commands);
		operatorInterface.updateCommands(commands);
		mSubsystemLooper.start();
		System.out.println("End teleopInit()");
	}

	@Override
	public void teleopPeriodic() {
		// Update RobotState
		mHardwareUpdater.updateSensors(robotState);
		// Gets joystick commands
		operatorInterface.updateCommands(commands);
		
		// Updates commands based on routines
		mRoutineManager.update(commands);

		//Update the hardware
		mHardwareUpdater.updateSubsystems();
		
		// Update sensorTable with encoder distances
		sensorTable.putString("left", String.valueOf(robotState.drivePose.getLeftDistance()));
		sensorTable.putString("right", String.valueOf(robotState.drivePose.getRightDistance()));
		mDashboard.update();
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
		mDrive.setOpenLoop(DriveSignal.getNeutralSignal());

		// Manually run garbage collector
		System.gc();

		System.out.println("End disabledInit()");
	}

	@Override
	public void disabledPeriodic() {
		if(Dashboard.getInstance().getSelectedAutoMode() != "-1") {
			AutoModeSelector.getInstance().setFromDashboard();
		}
	}
}