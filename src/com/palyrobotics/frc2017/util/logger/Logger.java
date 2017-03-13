package com.palyrobotics.frc2017.util.logger;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;

/**
 * Log is at /home/lvuser/logs/fileName directory
 * fileName defaults to
 */
public class Logger {
	/*
	 TODO: unit test
	 check filepath
	 create directory
	 make sure thread flushes correctly
	 make sure writes are not causing concurrent exception
	 lots to do
	 TODO: mkdir if dir not exist
	 */
	private static Logger instance = new Logger();
	public static Logger getInstance() {
		return instance;
	}

	private String fileName = null;
	// Buffered writer is flushed every 500ms and should be thread safe
	private BufferedWriter bufferedWriter;
	
	private ArrayList<TimestampedString> mData;
	// Separates to prevent concurrent modification exception
	private ArrayList<TimestampedString> mSubsystemThreadLogs;
	private ArrayList<TimestampedString> mRobotThreadLogs;

	// synchronized lock for writing out the latest data
	private final Object writingLock = new Object();
	private Thread mWritingThread = null;

	private Logger() {
		mData = new ArrayList<>();
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
	public void init() {
		if (bufferedWriter != null && mWritingThread != null) {
			return;
		}
		Date date = new Date();
		if (fileName == null) {
			fileName = new SimpleDateFormat("MM-dd-yyyy HH-mm-ss").format(date);
		}
		String os = System.getProperty("os.name");
		String filePath;
		if (os.startsWith("Mac")) {
			filePath = "logs/"+ fileName;
			System.out.println(filePath);
		}
		else if (os.startsWith("Windows")) {
			filePath = "C:\\logs\\" + fileName;
		} else {
			// Pray that this is a roborio
			// TODO: Maybe find the exact OS name
			filePath = "/home/lvuser/logs/"+ fileName;
		}
		File mainLog = new File(filePath+"/log.log");
		int duplicatePrevent = 0;
		while (mainLog.exists()) {
			duplicatePrevent++;
			mainLog = new File(filePath+"/log"+duplicatePrevent+".log");
		}

		// TODO: try w/ resources might be better suited for automatically closing
		try {
			// File header
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
		mWritingThread = new Thread(() -> {
			// If thread is killed, stop trying to write
			if (Thread.currentThread().isInterrupted()) {
				try {
					synchronized (writingLock) {
						bufferedWriter.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}
			try {
				synchronized (writingLock) {
					mData = new ArrayList<>(mSubsystemThreadLogs);
					mSubsystemThreadLogs.addAll(mRobotThreadLogs);
					mSubsystemThreadLogs.sort(TimestampedString::compareTo);

					bufferedWriter.flush();
				}
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

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
	 * @param value
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
	 * @param key
	 * @param value
	 */
	public void logRobotThread(String key, Object value) {
		try {
			mRobotThreadLogs.add(new TimestampedString(key + ": " + value.toString()));
		} catch (ConcurrentModificationException e) {
			System.err.println("Attempted concurrent modification on robot logger");
		}
	}
	
	public void end() {	
		for (TimestampedString m : mData) {
			try {
				bufferedWriter.write(m.toString());
				bufferedWriter.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void cleanup() {
		try {
			// FYI, buffered writer closes the underlying filewriter and flushes the buffer
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}