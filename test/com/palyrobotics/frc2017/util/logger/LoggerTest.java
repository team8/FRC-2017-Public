package com.palyrobotics.frc2017.util.logger;

import org.junit.Test;

public class LoggerTest {
	Logger logger = Logger.getInstance();
	
	@Test
	public void testWriting() {
		logger.init();
		logger.log("Testing");
		logger.log("asdf");
		logger.log("number", 1);
		
		logger.end();
		
		// messages should be flushed
		logger.end();
	}

	@Test
	public void testRandomFileStuff() {
		System.out.println(System.getProperty("os.name"));
	}
	@Test
	public void testCrashTracker() {
		// Crash tracker crashes on non roboRIO
		//CrashTracker.logRobotInit();
	}
}
