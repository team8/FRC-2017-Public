package com.palyrobotics.frc2017.util.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Logger {
	private static Logger instance = new Logger();
	public synchronized static Logger getInstance() {
		return instance;
	}
	
	private Log log;
	private FileWriter fileWriter;
	private BufferedWriter bufferedWriter;
	
//	private ArrayList<Log> logs = new ArrayList<Log>();
//	private ArrayList<FileWriter> fileWriters = new ArrayList<FileWriter>();
//	private ArrayList<BufferedWriter> bufferedWriters = new ArrayList<BufferedWriter>();
	
	public Logger() {
		
	}
	
	public synchronized void newLog() {
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
		
		log = new Log(filePath);
		System.out.println("Created new log at " + filePath.toString());
	}
	
	public synchronized void log(Object value) {
//		log("", value, false, false);
		log.queueMessage(new LogMessage(value.toString()));
	}
	public synchronized void log(String key, Object value) {
//		log(key, value, false, false);
		log.queueMessage(new LogMessage(key, value.toString()));
	}
//	public synchronized void log(String key, Object value, boolean logAll) {
//		log(key, value, logAll, false);
//	}
//	private synchronized void log(String key, Object value, boolean logAll, boolean print) {
//		boolean createLoggable = true;
//		for (Log l: loggables) {
//			if (l.name == name) {
//				l.queueMessage(value.toString());
//				createLoggable = false;
//				break;
//			}
//		}
//		if (createLoggable) {
//			loggables.add(new Log(name, print, logAll));
//			loggables.get(loggables.size()-1).queueMessage(value.toString());
//		}
//	}
	
	public synchronized void update() {		
//		//Write to console
//		for (Log l: loggables) {
//			if (l.print) {
//				if (l.logAll) {
//					messages.addAll(messages.size()-1, l.messages);
//				}
//				else {
//					messages.add(l.messages.get(l.messages.size()-1));
//				}
//			}
//		}
//		messages.sort(null);
//		for (LogMessage m: messages) {
//			System.out.println(m.output());
//		}
//		messages.clear();
//		
//		//Write to log file
//		for (Log l: loggables) {
//			if (l.logAll) {
//				messages.addAll(messages.size()-1, l.messages);
//			}
//			else {
//				messages.add(l.messages.get(l.messages.size()-1));
//			}
//		}
//		messages.sort(null);
//		try {
//			for (LogMessage m: messages) {
//				bufferedWriter.write(m.outputWithTimestamp());
//				bufferedWriter.newLine();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		ArrayList<LogMessage> messages = log.getMessages();
		messages.sort(null);
		
		for (LogMessage m : messages) {
			System.out.println(m.toString());
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
		
		log.clear();
	}
	
	public synchronized void cleanup() {
		try {
			fileWriter.close();
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.clear();
	}
	
	public Log getLog() {
		return log;
	}
}