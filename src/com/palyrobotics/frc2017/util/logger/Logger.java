package com.palyrobotics.frc2017.util.logger;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;

/**
 * Log is at /home/lvuser/logs/fileName directory
 * Unit test safe, creates diff file directory for Mac/Windows/Linux
 * fileName defaults to ex: "Mar13 13-29" using 24-hr-time
 * Can set desired filename manually
 * If log file exists on first start, automatically creates new file
 */
public class Logger {
	private static Logger instance = new Logger();
	public static Logger getInstance() {
		return instance;
	}

	private String fileName = null;
	// Buffered writer is flushed every 500ms and should be thread safe
	private BufferedWriter bufferedWriter;
	
	private boolean isEnabled = false;
	
	private ArrayList<TimestampedString> mData;
	// Separates to prevent concurrent modification exception
	private ArrayList<TimestampedString> mSubsystemThreadLogs = new ArrayList<>();
	private ArrayList<TimestampedString> mRobotThreadLogs = new ArrayList<>();

	// synchronized lock for writing out the latest data
	private final Object writingLock = new Object();
	private Thread mWritingThread = null;
	// Stores the runnable for the thread to be restarted
	private Runnable mRunnable;
	private File mainLog;


	private Logger() {
		mData = new ArrayList<>();
		mRunnable = () -> {
			while (true) {
				// If thread is killed, stop trying to write
				if (Thread.currentThread().isInterrupted()) {
					try {
						synchronized (writingLock) {
							System.out.println("Logger interrupted, closing");
							bufferedWriter.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
				try {
					synchronized (writingLock) {
						if (isEnabled) {
							mData = new ArrayList<>(mSubsystemThreadLogs);
							mData.addAll(mRobotThreadLogs);
							mSubsystemThreadLogs.clear();
							mRobotThreadLogs.clear();
							mData.sort(TimestampedString::compareTo);
							mData.forEach((TimestampedString c) -> {
								try {
									bufferedWriter.write(c.getTimestampedString());
								} catch (IOException e) {
									e.printStackTrace();
								}
							});
							mData.clear();
							bufferedWriter.flush();
						}
					}
				Thread.sleep(500);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e){
					// No value in stack trace
					//e.printStackTrace();
					return;
				}
			}
		};
	}

	public boolean setFileName(String fileName) {
		if (bufferedWriter != null) {
			System.err.println("Already created log file");
			return false;
		}
		this.fileName = fileName;
		return true;
	}

	/**
	 * Creates the file at desired filepath, avoids file collision, will not recreate if logger already there
	 */
	public void start() {
		// If initialized before, then recreate the buffered writer and re-enable
		if (bufferedWriter != null && mWritingThread != null) {
			isEnabled = true;
			try {
				bufferedWriter = Files.newWriter(mainLog, Charsets.UTF_8);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			mWritingThread = new Thread(mRunnable);
			mWritingThread.start();
			return;
		}
		Date date = new Date();
		if (fileName == null) {
			fileName = new SimpleDateFormat("MMMdd HH-mm").format(date);
		}
		String os = System.getProperty("os.name");
		String filePath;
		if (os.startsWith("Mac")) {
			filePath = "logs" + File.separatorChar + fileName;
		}
		else if (os.startsWith("Windows")) {
			filePath = "C:" + File.separatorChar + "logs" + File.separatorChar + fileName;
		} else {
			// Pray that this is a roborio
			// TODO: Maybe find the exact OS name
			filePath = "/home/lvuser/logs/" + fileName;
		}
		int duplicatePrevent = 0;
		mainLog = new File(filePath+File.separatorChar+"log.log");
		while (mainLog.exists()) {
			duplicatePrevent++;
			mainLog = new File(filePath+File.separatorChar+"log"+duplicatePrevent+".log");
		}
		
		// TODO: try w/ resources might be better suited for automatically closing
		try {
			// File header
			Files.createParentDirs(mainLog);
			bufferedWriter = Files.newWriter(mainLog, Charsets.UTF_8);
			bufferedWriter.write(date.toString()+ "\n");
			bufferedWriter.newLine();
			bufferedWriter.flush();
			System.out.println("Created new log at " + filePath);
		} catch (IOException e) {
			if (bufferedWriter != null) {
				try {
					bufferedWriter.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
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
	public void logSubsystemThread(Object value) {
		try {
			mSubsystemThreadLogs.add(new TimestampedString(value.toString()));
		} catch (ConcurrentModificationException e) {
			System.err.println("Attempted concurrent modification on subsystem logger");
		}
	}

	/**
	 * Called on subsystem thread
	 * @param key
	 * @param value will call .toString()
	 */
	public void logSubsystemThread(String key, Object value) {
		try {
			mSubsystemThreadLogs.add(new TimestampedString(key + ": " + value.toString()));
		} catch (ConcurrentModificationException e) {
			System.err.println("Attempted concurrent modification on subsystem logger");
		}
	}

	/**
	 * Called on robot thread
	 * @param value
	 */
	public void logRobotThread(Object value) {
		try {
			mRobotThreadLogs.add(new TimestampedString(value.toString()));
		} catch (ConcurrentModificationException e) {
			System.err.println("Attempted concurrent modification on robot logger");
		}
	}

	/**
	 * Called on robot thread
	 * @param key will be paired with the object
	 * @param value will call .toString()
	 */
	public void logRobotThread(String key, Object value) {
		try {
			mRobotThreadLogs.add(new TimestampedString(key + ": " + value.toString()));
		} catch (ConcurrentModificationException e) {
			System.err.println("Attempted concurrent modification on robot logger");
		}
	}
	
	public synchronized void cleanup() {
		System.out.println("Log file: "+mainLog.getAbsolutePath());
		if (!isEnabled) {
			System.err.println("Already cleaned up");
			return;
		}
		try {
			// FYI, buffered writer closes the underlying filewriter and flushes the buffer
			mWritingThread.interrupt();
			mData = new ArrayList<>(mSubsystemThreadLogs);
			mData.addAll(mRobotThreadLogs);
			mData.sort(TimestampedString::compareTo);
			synchronized (writingLock) {
				mData.forEach((TimestampedString c) -> {
					try {
						bufferedWriter.write(c.getTimestampedString());
					} catch (IOException e) {
						System.out.println("Unable to write last strings");
						e.printStackTrace();
					}
				});
				mData.clear();
				try {
					bufferedWriter.write("Logger stopped");
				} catch (IOException e) {
					System.out.println("Unable to write logger stopped");
					e.printStackTrace();
				}
				bufferedWriter.flush();
			}
			bufferedWriter.close();
			isEnabled = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}