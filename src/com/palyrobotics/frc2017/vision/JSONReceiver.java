package com.palyrobotics.frc2017.vision;

public class JSONReceiver implements VisionReceiverBase{
	
	String fileName;
	
	public JSONReceiver(String fileName) {
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

