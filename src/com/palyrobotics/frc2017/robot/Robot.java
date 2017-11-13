package com.palyrobotics.frc2017.robot;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.auto.AutoModeSelector;
import com.palyrobotics.frc2017.behavior.RoutineManager;
import com.palyrobotics.frc2017.behavior.routines.AutomaticClimberRoutine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.config.dashboard.DashboardManager;
import com.palyrobotics.frc2017.config.dashboard.DashboardValue;
import com.palyrobotics.frc2017.robot.team254.lib.util.Loop;
import com.palyrobotics.frc2017.robot.team254.lib.util.Looper;
import com.palyrobotics.frc2017.subsystems.*;
import com.palyrobotics.frc2017.util.logger.Logger;
import com.palyrobotics.frc2017.vision.VisionManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {
	// Instantiate singleton classes
	private static RobotState robotState = new RobotState();
	public static RobotState getRobotState() {
		return robotState;
	}

	// Single instance to be passed around
	private static Commands commands = Commands.getInstance();
	public static Commands getCommands() {return commands;}
	

	private OperatorInterface operatorInterface = OperatorInterface.getInstance();
	private RoutineManager mRoutineManager = new RoutineManager();

	// Subsystem controllers
	private Drive mDrive = Drive.getInstance();
	private Slider mSlider = Slider.getInstance();
	private Spatula mSpatula = Spatula.getInstance();
	private Intake mIntake = Intake.getInstance();
	private Climber mClimber = Climber.getInstance();

	// Hardware Updater
	private HardwareUpdater mHardwareUpdater;

	private Looper mSubsystemLooper = new Looper();
	
	private double mStartTime;
	private boolean startedClimberRoutine = false;

	@Override
	public void robotInit() {
		Logger.getInstance().logRobotThread("Start robotInit() for "+Constants.kRobotName.toString());
		DashboardManager.getInstance().robotInit();
		VisionManager.getInstance().start(Constants.kAndroidConnectionUpdateRate, false);
		Logger.getInstance().logRobotThread("Finished starting");
		Logger.getInstance().setFileName("Offseason");
		Logger.getInstance().start();
		Logger.getInstance().logRobotThread("robotInit() start");
		Logger.getInstance().logRobotThread("Robot name: "+Constants.kRobotName);
		Logger.getInstance().logRobotThread("Alliance: " + DriverStation.getInstance().getAlliance());
		Logger.getInstance().logRobotThread("FMS connected: "+DriverStation.getInstance().isFMSAttached());
		Logger.getInstance().logRobotThread("Alliance station: "+DriverStation.getInstance().getLocation());
		try {
			DriverStation.reportWarning("Auto is "+AutoModeSelector.getInstance().getAutoMode().toString(), false);
			Logger.getInstance().logRobotThread((VisionManager.getInstance().isServerStarted()) ?
					"Nexus streaming": "Nexus not streaming");
			Logger.getInstance().logRobotThread("Auto", AutoModeSelector.getInstance().getAutoMode().toString());
			DashboardManager.getInstance().publishKVPair(new DashboardValue("automodestring", AutoModeSelector.getInstance().getAutoMode().toString()));
		} catch (NullPointerException e) {
			Logger.getInstance().logRobotThread("Auto: "+e.getMessage());
		}
		if (Constants.kRobotName == Constants.RobotName.STEIK) {
			try {
				mHardwareUpdater = new HardwareUpdater(this, mDrive, mSlider, mSpatula, mIntake, mClimber);
			} catch (Exception e) {
				System.exit(1);
			}

		} else {
			try {
				mHardwareUpdater = new HardwareUpdater(mDrive);
			} catch (Exception e) {
				System.exit(1);
			}
		}

		mHardwareUpdater.initHardware();

		mSubsystemLooper.register(new Loop() {
			@Override
			public void onStart() {

			}

			@Override
			public void update() {
				commands = mRoutineManager.update(commands);
				mHardwareUpdater.updateSensors(robotState);
				updateSubsystems();
				mHardwareUpdater.updateHardware();
			}

			@Override
			public void onStop() {

			}
		});
		System.out.println("Auto: "+AutoModeSelector.getInstance().getAutoMode().toString());
		System.out.println("End robotInit()");
		Logger.getInstance().logRobotThread("End robotInit()");
	}

	@Override
	public void autonomousInit() {
		System.out.println("Start autonomousInit()");
		Logger.getInstance().start();
		Logger.getInstance().logRobotThread("Start autonomousInit()");
		DashboardManager.getInstance().toggleCANTable(true);
		robotState.gamePeriod = RobotState.GamePeriod.AUTO;
		mHardwareUpdater.configureTalons(false);

		// Wait for talons to update
		try {
			System.out.println("Sleeping thread for 200 ms");
			Thread.sleep(200);
		} catch (InterruptedException e) {

		}

		mHardwareUpdater.updateSensors(robotState);
		mRoutineManager.reset(commands);

		startSubsystems();

		// Get the selected auto mode
		AutoModeBase mode = AutoModeSelector.getInstance().getAutoMode();

		// Prestart and run the auto mode
		mode.prestart();
		mRoutineManager.addNewRoutine(mode.getRoutine());
		Logger.getInstance().logRobotThread("Auto mode", mode.toString());
		Logger.getInstance().logRobotThread("Auto routine", mode.getRoutine().toString());
		System.out.println("End autonomousInit()");
		Logger.getInstance().logRobotThread("End autonomousInit()");
		mSubsystemLooper.start();
	}

	@Override
	public void autonomousPeriodic() {
	}

	@Override
	public void teleopInit() {
		System.out.println("Start teleopInit()");
		Logger.getInstance().start();
		Logger.getInstance().logRobotThread("Start teleopInit()");
		robotState.gamePeriod = RobotState.GamePeriod.TELEOP;
		mHardwareUpdater.configureTalons(false);
		mHardwareUpdater.updateSensors(robotState);
		mHardwareUpdater.updateHardware();
		mRoutineManager.reset(commands);
		mStartTime = System.currentTimeMillis();
		DashboardManager.getInstance().toggleCANTable(true);
		commands.wantedDriveState = Drive.DriveState.CHEZY;	//switch to chezy after auto ends
		commands = operatorInterface.updateCommands(commands);
		startSubsystems();
		Logger.getInstance().logRobotThread("End teleopInit()");
		System.out.println("End teleopInit()");
	}

	@Override
	public void teleopPeriodic() {
		// Update RobotState
		// Gets joystick commands
		// Updates commands based on routines
		Logger.getInstance().logRobotThread("Teleop Commands: ", commands);
		logPeriodic();

		//Auto climber
		if(System.currentTimeMillis() - mStartTime >= 105000 && !startedClimberRoutine) {
			mRoutineManager.addNewRoutine(new AutomaticClimberRoutine());
			startedClimberRoutine = true;
		}
		commands = mRoutineManager.update(operatorInterface.updateCommands(commands));
		mHardwareUpdater.updateSensors(robotState);
		updateSubsystems();

		//Update the hardware
		mHardwareUpdater.updateHardware();
	}

	@Override
	public void disabledInit() {
		System.out.println("Start disabledInit()");
		Logger.getInstance().logRobotThread("Start disabledInit()");
		System.out.println("Current Auto Mode: " + AutoModeSelector.getInstance().getAutoMode().toString());
		robotState.gamePeriod = RobotState.GamePeriod.DISABLED;
		mSubsystemLooper.stop();

		// Stops updating routines
		mRoutineManager.reset(commands);

		//Creates a new Commands instance in place of the old one
		Commands.reset();
		
		// Stop controllers
		mDrive.setNeutral();
		mHardwareUpdater.configureDriveTalons();
		mHardwareUpdater.disableTalons();
		DashboardManager.getInstance().toggleCANTable(false);
		Logger.getInstance().logRobotThread("End disabledInit()");
		Logger.getInstance().cleanup();
		System.out.println("Log file: "+Logger.getInstance().getLogPath());

		// Manually run garbage collector
		System.gc();
		System.out.println("Gyro: "+robotState.drivePose.heading);
		System.out.println("End disabledInit()");
	}

	@Override
	public void disabledPeriodic() {
	}

	// Call during tele and auto periodic
	private void logPeriodic() {
		Logger.getInstance().logRobotThread("Match time", DriverStation.getInstance().getMatchTime());
		Logger.getInstance().logRobotThread("DS Connected", DriverStation.getInstance().isDSAttached());
		Logger.getInstance().logRobotThread("DS Voltage", DriverStation.getInstance().getBatteryVoltage());
		Logger.getInstance().logRobotThread("Outputs disabled", DriverStation.getInstance().isSysActive());
		Logger.getInstance().logRobotThread("FMS connected: "+DriverStation.getInstance().isFMSAttached());
		if (DriverStation.getInstance().isAutonomous()) {
			Logger.getInstance().logRobotThread("Game period: Auto");
		} else if (DriverStation.getInstance().isDisabled()) {
			Logger.getInstance().logRobotThread("Game period: Disabled");
		} else if (DriverStation.getInstance().isOperatorControl()) {
			Logger.getInstance().logRobotThread("Game period: Teleop");
		} else if (DriverStation.getInstance().isTest()) {
			Logger.getInstance().logRobotThread("Game period: Test");
		}
		if (DriverStation.getInstance().isBrownedOut()) Logger.getInstance().logRobotThread("Browned out");
		if (!DriverStation.getInstance().isNewControlData()) Logger.getInstance().logRobotThread("Didn't receive new control packet!");
	}

	private void startSubsystems() {
		mDrive.start();
		mSlider.start();
		mSpatula.start();
		mIntake.start();
		mClimber.start();
	}

	private void updateSubsystems() {
		mDrive.update(commands, robotState);
		mSlider.update(commands, robotState);
		mSpatula.update(commands, robotState);
		mIntake.update(commands, robotState);
		mClimber.update(commands, robotState);
	}

	private void stopSubsystems() {
		mDrive.stop();
		mSlider.stop();
		mSpatula.stop();
		mIntake.stop();
		mClimber.stop();
	}
}