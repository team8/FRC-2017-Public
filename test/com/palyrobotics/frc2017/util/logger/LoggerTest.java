package com.palyrobotics.frc2017.util.logger;

import org.junit.Test;

public class LoggerTest {
	Logger logger = Logger.getInstance();
	
	@Test
	public void testWriting() {
		logger.newLog();
		System.out.println(logger.getLog().getFilePath());
		logger.log("Testing");
		logger.log("asdf");
		logger.log("number", 1);
		
		logger.update();
		
		// messages should be flushed
		logger.update();
	}
}
