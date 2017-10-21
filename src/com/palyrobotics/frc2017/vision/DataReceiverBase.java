package com.palyrobotics.frc2017.vision;

import com.palyrobotics.frc2017.vision.ReceiverSelector.VisionReceiverType;

public abstract class DataReceiverBase extends AbstractVisionThread{
	
	ReceiverSelector mReceiverSelector;
	String fileName;
	int port;
	int updateRate;
	
	protected DataReceiverBase(String k_threadName, String fileName, int port, int updateRate) {
		
		super(k_threadName);
		this.fileName = fileName;
		this.port = port;
		this.updateRate = updateRate;
	}
	
	@Override
	protected void init() {
		
		mReceiverSelector = new ReceiverSelector(fileName, port, updateRate);
		mReceiverSelector.setReceiver(VisionReceiverType.JSON);
	}
	//File name, Port pass to constructor
	//

}
