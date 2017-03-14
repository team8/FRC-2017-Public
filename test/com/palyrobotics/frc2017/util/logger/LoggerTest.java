package com.palyrobotics.frc2017.util.logger;

import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.Robot;
import com.palyrobotics.frc2017.subsystems.Drive;
import com.palyrobotics.frc2017.subsystems.Slider;
import org.junit.Test;

public class LoggerTest {
	@Test
	public void testFileCreation() {
		Logger logger = Logger.getInstance();
		logger.setFileName("test");
		logger.start();
	}

	@Test
	public void testWriting() {
		Logger logger = Logger.getInstance();
		logger.start();
		logger.logRobotThread("Testing");
		logger.logRobotThread("asdf");
		logger.logRobotThread("number", 1);
		logger.cleanup();
		logger.start();
		logger.logRobotThread("New message");
		// messages should be flushed
		logger.cleanup();
	}

	@Test
	public void testLogRunThrough() {
		Logger logger = Logger.getInstance();
		logger.start();
		Drive.getInstance().start();
		Robot.getRobotState().gamePeriod = RobotState.GamePeriod.TELEOP;
		Slider.getInstance().start();
		Slider.getInstance().update(Robot.getCommands(), Robot.getRobotState());
		Drive.getInstance().update(Robot.getCommands(), Robot.getRobotState());
		logger.logSubsystemThread(Drive.getInstance().printStatus());
		logger.logSubsystemThread(Slider.getInstance().printStatus());
		logger.logRobotThread("Robot start");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.cleanup();
	}

	@Test
	public void testRandomFileStuff() {
	}

	@Test
	public void testCrashTracker() {
		// Crash tracker crashes on non roboRIO
		//CrashTracker.logRobotInit();
	}
}
