package com.palyrobotics.frc2018.util.logger;

import java.time.ZoneId;
import java.util.logging.Level;

/**
 * Created on 10/27/17
 * @author Joseph Rumelhart
 * Replaces constant file for logger constants
 * Avoids interference with other code, can be changed easily to account for time zone changes
 */
public class LoggerConstants {
	/*
	 * Explanation of logger levels:
	 * SEVERE: Errors which will cause a major part of the program to stop working (1000)
	 * WARNING: Errors which will cause a minor failure or indicate a risk of serious failure (900)
	 * INFO: Information on the normal functioning of the robot; Expect this level and above to be printed to console (800)
	 * CONFIG: Static configuration information (700)
	 * FINE: Basic tracing information (500)
	 * FINER: More detailed tracing information (400)
	 * FINEST: Most detailed tracing information, highest volume (300)
	 * ALL: Not actually a level, used to display information from all levels (Integer.MIN_VALUE)
	 */
	 public static Level displayLevel = Level.INFO; 
	 public static Level writeLevel = Level.CONFIG;
	 
	 //Used to control display times
	 //Need to change for out of state competitions
	 public static ZoneId tZone = ZoneId.of("UTC-8");
	 
	 //Controls if stack traces will be written
	 public static boolean writeStackTrace = true;
	 
	 //Only set to TRUE when in a match
	 public static final boolean compStatus = false;
}