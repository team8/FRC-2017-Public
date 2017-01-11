package com.palyrobotics.frc2016.robot;

import com.palyrobotics.frc2016.auto.AutoMode;
import com.palyrobotics.frc2016.auto.AutoModeExecuter;
import com.palyrobotics.frc2016.auto.AutoModeSelector;
import com.palyrobotics.frc2016.behavior.RoutineManager;
import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.config.Constants;
import com.palyrobotics.frc2016.config.RobotState;
import com.palyrobotics.frc2016.subsystems.*;
import com.palyrobotics.frc2016.util.Dashboard;
import com.palyrobotics.frc2016.util.SubsystemLooper;
import com.palyrobotics.frc2016.robot.team254.lib.util.DriveSignal;
import com.palyrobotics.frc2016.robot.team254.lib.util.RobotData;
import com.palyrobotics.frc2016.robot.team254.lib.util.SystemManager;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Robot extends IterativeRobot {
	// Instantiate singleton classes
	private static RobotState mRobotState = new RobotState();

	public static RobotState getRobotState() {
		return mRobotState;
	}

	private static HardwareAdapter mHardwareAdapter = HardwareAdapter.getInstance();
	private static OperatorInterface mOperatorInterface = OperatorInterface.getInstance();
	// Instantiate separate thread controls
	private SubsystemLooper subsystem_looper = new SubsystemLooper();
	private RoutineManager routineManager = new RoutineManager();
	private AutoModeExecuter autoModeRunner = new AutoModeExecuter(routineManager);

	// Subsystem controllers
	private Drive mDrive = Drive.getInstance();
	
	//hardware updater
	private HardwareUpdater mHardwareUpdater;

	private Dashboard mDashboard = Dashboard.getInstance();
	NetworkTable sensorTable;

	static {
		SystemManager.getInstance().add(new RobotData());
	}

	@Override
	public void robotInit() {
		System.out.println("Start robotInit()");
		subsystem_looper.register(mDrive);
		mHardwareUpdater = new HardwareUpdater(mDrive);
		//        SystemManager.getInstance().add(routineManager);
		sensorTable = NetworkTable.getTable("Sensor");
		mDashboard.init();
		System.out.println("End robotInit()");
	}

	@Override
	public void autonomousInit() {
		System.out.println("Start autonomousInit()");
		mRobotState.gamePeriod = RobotState.GamePeriod.AUTO;

		mDrive.resetController();
		
		AutoMode mode = AutoModeSelector.getInstance().getAutoMode();
		autoModeRunner.setAutoMode(mode);
		// Prestart auto mode
		mode.prestart();
		autoModeRunner.start();
		// Start control loops
		subsystem_looper.start();
		System.out.println("End autonomousInit()");
	}

	@Override
	public void autonomousPeriodic() {
		routineManager.update();
		mDashboard.update();
		mHardwareUpdater.updateSubsystems();
	}

	@Override
	public void teleopInit() {
		System.out.println("Start teleopInit()");
		mRobotState.gamePeriod = RobotState.GamePeriod.TELEOP;
		mOperatorInterface.updateCommands();
		subsystem_looper.start();
		System.out.println("End teleopInit()");
	}

	@Override
	public void teleopPeriodic() {
		// Update RobotState
		mHardwareUpdater.updateSensors();
		// Gets joystick commands
		mOperatorInterface.updateCommands();
		
		// Updates commands based on routines
		routineManager.update();

		//Update the hardware
		mHardwareUpdater.updateSubsystems();
		
		// Update sensorTable with encoder distances
		sensorTable.putString("left", String.valueOf(mRobotState.left_encoder));
		sensorTable.putString("right", String.valueOf(mRobotState.right_encoder));
		mDashboard.update();
	}

	@Override
	public void disabledInit() {
		System.out.println("Start disabledInit()");
		System.out.println("Current Auto Mode: "+AutoModeSelector.getInstance().getAutoMode().toString());
		mRobotState.gamePeriod = RobotState.GamePeriod.DISABLED;
		// Stop auto mode
		autoModeRunner.stop();

		// Stop routine_request
		routineManager.reset();

		// Stop control loops
		subsystem_looper.stop();

		// Stop controllers
		mDrive.setOpenLoop(DriveSignal.NEUTRAL);

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