package com.palyrobotics.frc2017.util.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

public class Logger {
	private static Logger instance = new Logger();
	public synchronized static Logger getInstance() {
		return instance;
	}
	
	private FileWriter fileWriter;
	private BufferedWriter bufferedWriter;
	
	private ArrayList<String> data;
	public Logger() {
		data = new ArrayList<String>();
	}
	
	public void newLog() {
		Date date = new Date();
		
		String filePath = "C:\\logfiles\\" + new SimpleDateFormat("MM-dd-yyyy HH-mm-ss").format(date) + ".log";
		
		try {
			fileWriter = new FileWriter(filePath);
			bufferedWriter = new BufferedWriter(fileWriter);
			
			// File header
			bufferedWriter.write(new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(date) + "\n");
			bufferedWriter.newLine();
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Created new log at " + filePath.toString());
	}
	
	public void log(Object value) {
//		log("", value, false, false);
		data.add(Timer.getFPGATimestamp() + ": " + value.toString() + "\n");
	}
	public void log(String key, Object value) {
		data.add(Timer.getFPGATimestamp() + ": " + key + ": " + value.toString() + "\n");
	}

	
	public void end() {	
		for (String m : this.data) {
			try {
				bufferedWriter.write(m.toString());
				bufferedWriter.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			fileWriter.flush();
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void cleanup() {
		try {
			fileWriter.close();
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}