package com.palyrobotics.frc2017.util.logger;

import java.sql.Time;

/**
 * Created by Nihar on 3/13/17.
 * Stores a String with the timestamp on construction
 * Allows data structures to sort the strings by timestamp
 * And then retrieve the String
 * Also automatically adds a newline to the end
 */
public class TimestampedString implements Comparable<TimestampedString> {
	private String mString;
	private long mTime;

	public TimestampedString(String string) {
		mString = string;
		mTime = System.currentTimeMillis();
	}

	public long getTimestamp() {
		return mTime;
	}

	/**
	 * Converts the millisecond timestamp to seconds
	 * @return
	 */
	public String getTimestampedString() {
		return (mTime/1000)+": "+mString+"\n";
	}

	@Override
	public String toString() {
		return getTimestampedString();
	}

	@Override
	public int compareTo(TimestampedString o) {
		return Long.compare(this.getTimestamp(), o.getTimestamp());
	}
}
