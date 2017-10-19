package com.palyrobotics.frc2017.vision;

public class JSONReceiver implements VisionReceiverBase{

	@Override
	public String extractData() {
		String raw_data = CommandExecutor.getJSONPrintOut();
		return raw_data;
	}
}

