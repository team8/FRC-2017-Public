package com.palyrobotics.frc2018.vision.networking.recievers;

import com.palyrobotics.frc2018.vision.util.commandline.CommandExecutor;

public class FileReceiver implements VisionReceiverBase {
	
	String fileName;
	
	public FileReceiver(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String extractData() {
		String raw_data = CommandExecutor.catFile(fileName);
		return raw_data;
	}

	@Override
	public byte[] extractDataBytes() {
		return this.extractData().getBytes();
	}
}

