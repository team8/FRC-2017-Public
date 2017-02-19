package com.palyrobotics.frc2017.util.logger;

import java.util.ArrayList;

public class Log {
	private String filePath;
	private ArrayList<LogMessage> messages = new ArrayList<LogMessage>();
//	public String name = "";
//	public boolean print = false;
//	public boolean logAll = false;
	
	public Log(String fileName) {
		this.filePath = fileName;
	}
	
//	public Log(String name, boolean print, boolean logAll) {
//		this.name = name;
//		this.print = print;
//		this.logAll = logAll;
//	}
	
	public void queueMessage(LogMessage message) {
//		messages.add(new LogMessage(name, message));
		messages.add(message);
	}
	
	public void clear() {
		messages.clear();
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public ArrayList<LogMessage> getMessages() {
		return messages;
	}
}
