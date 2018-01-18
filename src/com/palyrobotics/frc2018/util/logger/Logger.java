package com.palyrobotics.frc2018.util.logger;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

/**
 * Log is at /home/lvuser/logs/ directory
 * Unit test safe, creates diff file directory for Mac/Windows/Linux
 * Can set desired filename manually
 * If log file exists on first start, automatically creates new file
 *
 * */
public class Logger {
	private static Logger instance = new Logger();
	public static Logger getInstance() {
		return instance;
	}
	
	// Default filename
	private String fileName = "DEFAULT";

	private boolean isEnabled = false;
	
	//Array containing current log calls
	private ArrayList<TimestampedString> mRobotThreadLogs = new ArrayList<>();

	// synchronized lock for writing out the latest data
	private final Object writingLock = new Object();
	private Thread mWritingThread = null;
	// Stores the runnable for the thread to be restarted
	private Runnable mRunnable;

	StringWriter sw = new StringWriter();
	PrintWriter pw = new PrintWriter(sw);
	
	private int duplicatePrevent = 0;
	private File mainLog;

	public boolean setFileName(String fileName) {
		if (mainLog != null) {
			System.err.println("Already created log file");
			return false;
		}
		//Attempts to sanitize file names for ease of use
		this.fileName = fileName.replaceAll(File.separator, ":");
		this.fileName = fileName.replaceAll(" ", "_");
		this.fileName = fileName.replaceAll("/n", "_");
		return true;
	}
	
	/**
	 * Creates the file at desired filepath, avoids file collision, will not recreate if logger already there
	 * Also sanitizes inputs to prevent unwanted directory creation
	 */
	public void start() {
		if(fileName == "DEFAULT") {
			System.err.println("WARNING: Using default filename!");
		}
		// If initialized before, then recreate the buffered writer and re-enable
		if (mWritingThread != null) {
			isEnabled = true;
			mWritingThread = new Thread(mRunnable);
			mWritingThread.start();
			return;
		}
		String cDate = ZonedDateTime.now(LoggerConstants.tZone).format(DateTimeFormatter.ofPattern("MM-dd-yy"));
		String cTime = ZonedDateTime.now(LoggerConstants.tZone).format(DateTimeFormatter.ofPattern("HH:mm"));
		String os = System.getProperty("os.name");
		String filePath = fileName + File.separatorChar + cDate + File.separatorChar + fileName + " " + cTime;
		//Checks if the robot is in competition mode or not
		//Logs in different locations
		if(LoggerConstants.compStatus) {
			filePath = "COMPETITION" + File.separatorChar + filePath;
		}
		else {
			filePath = "PRACTICE" + File.separatorChar + filePath;
		}
		//Checks the OS name to determine where to save the log
		if (os.startsWith("Mac")) {
			filePath = "logs" + File.separatorChar + filePath;
		}
		else if (os.startsWith("Windows")) {
			filePath = "C:" + File.separatorChar + "logs" + File.separatorChar + filePath;
		} else if (os.startsWith("NI")){
			// Pray that this is a roborio
			filePath = "/home/lvuser/logs/" + filePath;
		}
		else {
			System.err.println("Error in determining OS name, reverting to roboRIO base");
			filePath = "/home/lvuser/logs/" + filePath;
		}
		mainLog = new File(filePath + ".log");
		//Prevents the creation of duplicate logs
		while (mainLog.exists()) {
			duplicatePrevent++;
			mainLog = new File(filePath + duplicatePrevent + ".log");
		}
		try {
			// File header
			Files.createParentDirs(mainLog);
			Files.append("Robot log:"+ "\n", mainLog, Charsets.UTF_8);
			Files.append(filePath + "\n", mainLog, Charsets.UTF_8);
			System.out.println("Created new log at " + filePath);
		} catch (IOException e) {
			System.err.println("Failed to create log at "+filePath);
			e.printStackTrace();
		}

		// Create thread to write out logger
		mWritingThread = new Thread(mRunnable);
		isEnabled = true;
		mWritingThread.start();
	}

	/**
	 * Called on robot thread
	 * @param value Object used for input; stores .toString() value
	 */
	@Deprecated
	public void logRobotThread(Object value) {
		try {
			if(LoggerConstants.writeStackTrace && value instanceof Throwable) {
				((Throwable) value).printStackTrace(pw);
				mRobotThreadLogs.add(new TimestampedString(pw.toString()));
			}
			else {
				mRobotThreadLogs.add(new TimestampedString(value.toString()));
			}
		} catch (ConcurrentModificationException e) {
			System.err.println("Attempted concurrent modification on robot logger");
		}
		pw.flush();
	}
	
	/**
	 * Called on robot thread
	 * @param l Sets level of log message; determines writing to console and file
	 * @param value Object used for input; stores .toString() value
	 */
	public void logRobotThread(Level l, Object value) {
		try {
			if(LoggerConstants.writeStackTrace && value instanceof Throwable && l.intValue() <= LoggerConstants.traceLevel.intValue()) {
				((Throwable) value).printStackTrace(pw);
				mRobotThreadLogs.add(new LeveledString(l, pw.toString()));
			}
			else {
				mRobotThreadLogs.add(new LeveledString(l, value.toString()));
			}
		} catch (ConcurrentModificationException e) {
			System.err.println("Attempted concurrent modification on robot logger");
		}
		pw.flush();
	}
	
	
	/**
	 * Called on robot thread
	 * @param key String added to input object
	 * @param value Object used for input; stores .toString() value
	 */
	@Deprecated
	public void logRobotThread(String key, Object value) {
		try {
			if(LoggerConstants.writeStackTrace && value instanceof Throwable) {
				((Throwable) value).printStackTrace(pw);
				mRobotThreadLogs.add(new TimestampedString(key + ": " + pw.toString()));
			}
			else {
				mRobotThreadLogs.add(new TimestampedString(key + ": " + value.toString()));
			}
		} catch (ConcurrentModificationException e) {
			System.err.println("Attempted concurrent modification on robot logger");
		}
	}
	
	/**
	 * Called on robot thread
	 * @param l Sets level of log message; determines writing to console and file
	 * @param key String added to input object
	 * @param value Object used for input; stores .toString() value
	 */
	public void logRobotThread(Level l, String key, Object value) {
		try {
			if(LoggerConstants.writeStackTrace && value instanceof Throwable && l.intValue() <= LoggerConstants.traceLevel.intValue()) {
				((Throwable) value).printStackTrace(pw);
				mRobotThreadLogs.add(new LeveledString(l, key + ": " + pw.toString()));
			}
			else {
				mRobotThreadLogs.add(new LeveledString(l, key + ": " + value.toString()));
			}
		} catch (ConcurrentModificationException e) {
			System.err.println("Attempted concurrent modification on robot logger");
		}
		pw.flush();
	}
	
	public synchronized void cleanup() {
		mWritingThread.interrupt();
	}
	private Logger() {
		mRunnable = () -> {
			while (true) {
				writeLogs();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e){
					shutdown();
					return;
				}
				// If thread is interrupted, cleanup
				if (Thread.currentThread().isInterrupted()) {
					shutdown();
					return;
				}
			}
		};
	}
	
	/**
	 * Writes current log messages to file and console according to level
	 * Still supports deprecated log messages, will log all of them
	 */
	private void writeLogs() {
		synchronized(writingLock) {
			if(isEnabled) {
				mRobotThreadLogs.sort(TimestampedString::compareTo);
				mRobotThreadLogs.forEach((TimestampedString c) -> {
					try {
						if(c instanceof LeveledString && ((LeveledString) c).getLevel().intValue() >= LoggerConstants.writeLevel.intValue()) {
							Files.append(((LeveledString) c).getLeveledString(), mainLog, Charsets.UTF_8);
							//Only writes to console if above required level
							if(((LeveledString) c).getLevel().intValue() >= LoggerConstants.displayLevel.intValue()) {
								System.out.println(c.toString());
							}
						}
						//Support for deprecated methods
						else if(!(c instanceof LeveledString) && c instanceof TimestampedString) {
							System.out.println(c.toString());
							Files.append(c.getTimestampedString(), mainLog, Charsets.UTF_8);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				mRobotThreadLogs.clear();
			}
		}
	}
	
	//Used to get current path
	public String getLogPath() {
		if (mainLog != null) {
			return mainLog.getAbsolutePath();
		} else {
			return "NoLogYet";
		}
	}

	// Used to cleanup internally, write out last words, etc
	private synchronized void shutdown() {
		System.out.println("Shutting down");
		synchronized (writingLock) {
			writeLogs();
			try {
				Files.append("Logger stopped \n", mainLog, Charsets.UTF_8);
			} catch (IOException e) {
				System.out.println("Unable to write, logger stopped");
				e.printStackTrace();
			}
			isEnabled = false;
		}
	}
}