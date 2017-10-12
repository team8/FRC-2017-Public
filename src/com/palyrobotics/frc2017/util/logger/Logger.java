package com.palyrobotics.frc2017.util.logger;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.palyrobotics.frc2017.util.logger.LoggerConstants;

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
 * Log is at /home/lvuser/logs/fileName directory
 * Unit test safe, creates diff file directory for Mac/Windows/Linux
 * fileName defaults to ex: "Mar13 13-29" using 24-hr-time
 * Can set desired filename manually
 * If log file exists on first start, automatically creates new file
 *
 * If run on roboRIO, will attempt to copy the driverstation console log to this directory to save it
 * No longer uses bufferedwriter, uses Guava Files to append to file
 *
 * FYI, buffered writer closes the underlying filewriter and flushes the buffer
 */
public class Logger {
	private static Logger instance = new Logger();
	public static Logger getInstance() {
		return instance;
	}

	private String fileName = null;

	private boolean isEnabled = false;
	
	private ArrayList<TimestampedString> mData;
	// Separates to prevent concurrent modification exception
	private ArrayList<TimestampedString> mSubsystemThreadLogs = new ArrayList<>();
	private ArrayList<TimestampedString> mRobotThreadLogs = new ArrayList<>();
	private ConcurrentLinkedQueue<TimestampedString> mUnknownThreadLogs = new ConcurrentLinkedQueue<TimestampedString>();

	// synchronized lock for writing out the latest data
	private final Object writingLock = new Object();
	private Thread mWritingThread = null;
	// Stores the runnable for the thread to be restarted
	private Runnable mRunnable;

	StringWriter sw = new StringWriter();
	PrintWriter pw = new PrintWriter(sw);
	
	private int duplicatePrevent = 0;
	private File mainLog;

	// Finds the driver station console output
	private File rioLog;

	public boolean setFileName(String fileName) {
		if (mainLog != null) {
			System.err.println("Already created log file");
			return false;
		}
		this.fileName = fileName.replaceAll(File.separator, ":");
		return true;
	}
	
	/**
	 * Creates the file at desired filepath, avoids file collision, will not recreate if logger already there
	 * Also sanitizes inputs to prevent unwanted directory creation
	 */
	public void start() {
		// If initialized before, then recreate the buffered writer and re-enable
		if (mWritingThread != null) {
			isEnabled = true;
			mWritingThread = new Thread(mRunnable);
			mWritingThread.start();
			return;
		}
		String cDate = ZonedDateTime.now(LoggerConstants.tZone).format(DateTimeFormatter.ofPattern(" MM-dd-yy"));
		String cTime = ZonedDateTime.now(LoggerConstants.tZone).format(DateTimeFormatter.ofPattern(" HH:mm"));
		String os = System.getProperty("os.name");
		//TODO: Check if this is a useable system or if it results in excessive clutter
		String filePath = fileName + File.separatorChar + cDate + File.separatorChar + fileName + cTime;
		if(LoggerConstants.compStatus) {
			filePath = "COMPETITIONS" + File.separatorChar + filePath;
		}
		else {
			filePath = "PRACTICE" + File.separatorChar + filePath;
		}
		if (os.startsWith("Mac")) {
			filePath = "logs" + File.separatorChar + filePath;
		}
		else if (os.startsWith("Windows")) {
			filePath = "C:" + File.separatorChar + "logs" + File.separatorChar + filePath;
		} else  if (os.startsWith("NI")){
			// Pray that this is a roborio
			// TODO: Maybe find the exact OS name
			filePath = "/home/lvuser/logs/" + filePath;
//			// TODO:
//			rioLog = new File("/var/local/natinst/log/FRC_UserProgram.log");
		}
		else {
			System.err.println("Error in determining OS name, reverting to RIO base");
			filePath = "/home/lvuser/logs/" + filePath;
		}
		mainLog = new File(filePath + ".log");
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
	 * Called on subsystem thread
	 * @param value
	 */
	@Deprecated
	public void logSubsystemThread(Object value) {
		try {
			if(LoggerConstants.writeStackTrace && value instanceof Throwable) {
				((Throwable) value).printStackTrace(pw);
				mSubsystemThreadLogs.add(new TimestampedString(pw.toString()));
			}
			else {
				mSubsystemThreadLogs.add(new TimestampedString(value.toString()));
			}
		} catch (ConcurrentModificationException e) {
			System.err.println("Attempted concurrent modification on subsystem logger");
		}
		pw.flush();
	}

	public void logSubsystemThread(Level l, Object value) {
		try {
			if(LoggerConstants.writeStackTrace && value instanceof Throwable && l.intValue() >= 900) {
				((Throwable) value).printStackTrace(pw);
				mSubsystemThreadLogs.add(new LeveledString(l, pw.toString()));
			}
			else {
				mSubsystemThreadLogs.add(new LeveledString(l, value.toString()));
			}
		} catch (ConcurrentModificationException e) {
			System.err.println("Attempted concurrent modification on subsystem logger");
		}
		pw.flush();
	}
	
	/**
	 * Called on subsystem thread
	 * @param key
	 * @param value will call .toString()
	 */
	@Deprecated
	public void logSubsystemThread(String key, Object value) {
		try {
			if(LoggerConstants.writeStackTrace && value instanceof Throwable) {
				((Throwable) value).printStackTrace(pw);
				mSubsystemThreadLogs.add(new TimestampedString(key + ": /n" + pw.toString()));
			}
			else {
				mSubsystemThreadLogs.add(new TimestampedString(key + ": " + value.toString()));
			}
		} catch (ConcurrentModificationException e) {
			System.err.println("Attempted concurrent modification on subsystem logger");
		}
		pw.flush();
	}
	

	public void logSubsystemThread(Level l, String key, Object value) {
		try {
			if(LoggerConstants.writeStackTrace && value instanceof Throwable && l.intValue() >= 900) {
				((Throwable) value).printStackTrace(pw);
				mSubsystemThreadLogs.add(new LeveledString(l, key + ": /n" + pw.toString()));
			}
			else {
				mSubsystemThreadLogs.add(new LeveledString(l, key + ": " + value.toString()));
			}
		} catch (ConcurrentModificationException e) {
			System.err.println("Attempted concurrent modification on subsystem logger");
		}
		pw.flush();
	}

	/**
	 * Called on robot thread
	 * @param value
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
			System.err.println("Attempted concurrent modification on subsystem logger");
		}
		pw.flush();
	}
	
	public void logRobotThread(Level l, Object value) {
		try {
			if(LoggerConstants.writeStackTrace && value instanceof Throwable && l.intValue() <= 900) {
				((Throwable) value).printStackTrace(pw);
				mRobotThreadLogs.add(new LeveledString(l, pw.toString()));
			}
			else {
				mRobotThreadLogs.add(new LeveledString(l, value.toString()));
			}
		} catch (ConcurrentModificationException e) {
			System.err.println("Attempted concurrent modification on subsystem logger");
		}
		pw.flush();
	}
	
	
	/**
	 * Called on robot thread
	 * @param key will be paired with the object
	 * @param value will call .toString()
	 */
	@Deprecated
	public void logRobotThread(String key, Object value) {
		try {
			if(LoggerConstants.writeStackTrace && value instanceof Throwable) {
				((Throwable) value).printStackTrace(pw);
				mRobotThreadLogs.add(new TimestampedString(key + ": /n" + pw.toString()));
			}
			else {
				mRobotThreadLogs.add(new TimestampedString(key + ": " + value.toString()));
			}
		} catch (ConcurrentModificationException e) {
			System.err.println("Attempted concurrent modification on subsystem logger");
		}
	}
	
	/**
	 * Overloaded function to submit messages to the logger
	 * SubsystemThread should be used in subsystem package, use robotThread everywhere else
	 * @param l The level of logger call to be sent 
	 * @param key Text to provide context to object
	 * @param value Object submitted, call toString() on
	 */
	public void logRobotThread(Level l, String key, Object value) {
		try {
			if(LoggerConstants.writeStackTrace && value instanceof Throwable && l.intValue() <= 900) {
				((Throwable) value).printStackTrace(pw);
				mRobotThreadLogs.add(new LeveledString(l, key + ": /n" + pw.toString()));
			}
			else {
				mRobotThreadLogs.add(new LeveledString(l, key + ": " + value.toString()));
			}
		} catch (ConcurrentModificationException e) {
			System.err.println("Attempted concurrent modification on subsystem logger");
		}
		pw.flush();
	}
	
	public synchronized void cleanup() {
		mWritingThread.interrupt();
	}
	private Logger() {
		mData = new ArrayList<>();
		mRunnable = () -> {
			while (true) {
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
				mData = new ArrayList<>(mRobotThreadLogs);
				mData.addAll(mSubsystemThreadLogs);
				mData.sort(TimestampedString::compareTo);
				mSubsystemThreadLogs.clear();
				mRobotThreadLogs.clear();
				mData.forEach((TimestampedString c) -> {
					try {
						if(c instanceof LeveledString && ((LeveledString) c).getLevel().intValue() >= LoggerConstants.displayLevel.intValue()) {
							System.out.println(c.toString());
							if(((LeveledString) c).getLevel().intValue() >= LoggerConstants.writeLevel.intValue()) {
								Files.append(((LeveledString) c).getLeveledString(), mainLog, Charsets.UTF_8);
							}
						}
						else if(!(c instanceof LeveledString) && c instanceof TimestampedString) {
							System.out.println(c.toString());
							Files.append(c.getTimestampedString(), mainLog, Charsets.UTF_8);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				mData.clear();
			}
		}
	}
	
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
			mRobotThreadLogs.clear();
			mSubsystemThreadLogs.clear();
			try {
				Files.append("Logger stopped \n", mainLog, Charsets.UTF_8);
			} catch (IOException e) {
				System.out.println("Unable to write, logger stopped");
				e.printStackTrace();
			}
			isEnabled = false;
		}
		// Try to copy riolog to logging directory if it exists
//		if (rioLog != null) {
//			try {
//				Files.copy(rioLog, new File("/home/lvuser/logs/" + fileName+"/riolog"+duplicatePrevent+".log"));
//			} catch (IOException e) {
//				System.out.println("Unable to copy riolog");
//				e.printStackTrace();
//			}
//		}
	}
}