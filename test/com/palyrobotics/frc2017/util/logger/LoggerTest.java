package com.palyrobotics.frc2017.util.logger;

import org.junit.Test;

public class LoggerTest {
	Logger logger = Logger.getInstance();
	
	@Test
	public void testWriting() {
		logger.newLog();
		logger.log("Testing");
		logger.log("asdf");
		logger.log("number", 1);
		
		logger.end();
		
		// messages should be flushed
		logger.end();
	}
}
