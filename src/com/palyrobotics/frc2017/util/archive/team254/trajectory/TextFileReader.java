package com.palyrobotics.frc2017.util.archive.team254.trajectory;

import java.io.*;

import java.io.IOException;
import java.nio.file.Files;

/**
 * Read a text file into a string.
 *
 * @author Jared341
 */
public class TextFileReader {

	private BufferedReader reader_ = null;

	public TextFileReader(String uri) {
		try {
			// Open the new file
			File file = new File(uri);
			if (!file.exists()) {
				System.err.println("Could not find specified file!");
				return;
			}

			// Make an I/O adapter sandwich to actually get some text out
			reader_ = Files.newBufferedReader(file.toPath());

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Could not open file connection!");
			closeFile();
		}
	}

	private void closeFile() {
		try {
			// If we have a file open, close it
			reader_.close();
		} catch (IOException e) {
			System.err.println("Could not close file");
		}
	}

	// Returns null at end of file
	public String readLine() {
		String line = null;
		try {
			line = reader_.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			closeFile();
		}
		return line;
	}

	public String readWholeFile() {
		StringBuffer buffer = new StringBuffer();
		String line;
		while ((line = readLine()) != null) {
			buffer.append(line);
			buffer.append("\n");
		}
		return buffer.toString();
	}
}