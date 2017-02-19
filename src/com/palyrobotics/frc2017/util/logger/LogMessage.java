package com.palyrobotics.frc2017.util.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogMessage implements Comparable<LogMessage> {
	public Date date = new Date();
	public String time, key, value;
	
	public LogMessage(String value) {
		this.time = new SimpleDateFormat("HH:mm:ss").format(date);
		this.key = "";
		this.value = value;
	}
	
	public LogMessage(String key, String value) {
		this.time = new SimpleDateFormat("HH:mm:ss").format(date);
		this.key = key;
		this.value = value;
	}
	
	public String toString() {
		return time + " " + ((key != "") ? (key + ": ") : "") + value;
	}

	@Override
	public int compareTo(LogMessage other) {
		return this.date.compareTo(other.date);
	}
}
