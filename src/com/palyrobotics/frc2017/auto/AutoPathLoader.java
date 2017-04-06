package com.palyrobotics.frc2017.auto;

import com.palyrobotics.frc2017.util.archive.team254.TextFileReader;
import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.io.TextFileDeserializer;

import java.io.File;
import java.util.Hashtable;

/**
 * Load all autonomous mode paths.
 * Revised for Team 8 2017 by Nihar
 * @author Jared341
 * @author Stephen Pinkerton
 */
public class AutoPathLoader {
	// Make sure these match up!
	public final static String[] kPathNames = { "Backup",
			"Baseline",
			"BlueBoiler",
			"BlueCenter",
			"BlueLoading",
			"RedBoiler",
			"RedCenter",
			"RedLoading"
	};
	public final static String[] kPathDescriptions = { "Backup", "Baseline",
			"Blue boiler",
			"Blue center",
			"Blue loading",
			"Red boiler",
			"Red center",
			"Red loading"
	};
	static Hashtable paths_ = new Hashtable();

	public static void loadPaths() {
		double startTime = System.currentTimeMillis();
		String os = System.getProperty("os.name");
		String sourceDir;
		if (os.startsWith("Mac") || os.startsWith("Windows")) {
			sourceDir = "."+File.separatorChar+"paths"+File.separatorChar;
		} else {
			// Pray that this is a roborio because I don't know a programmer using Linux
			sourceDir = "/home/lvuser/paths/";
		}

		TextFileDeserializer deserializer = new TextFileDeserializer();
		for (int i = 0; i < kPathNames.length; ++i) {
			TextFileReader reader = new TextFileReader(sourceDir + kPathNames[i] +
					".txt");

			Path path = deserializer.deserialize(reader.readWholeFile());
			paths_.put(kPathNames[i], path);
		}
		System.out.println("Parsing paths took: " + (System.currentTimeMillis()-startTime/1000));
	}

	public static Path get(String name) {
		return (Path) paths_.get(name);
	}

	public static Path getByIndex(int index) {
		return (Path) paths_.get(kPathNames[index]);
	}
}