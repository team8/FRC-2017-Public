package com.palyrobotics.frc2017.vision;

import com.palyrobotics.frc2017.vision.ReceiverSelector.VisionReceiverType;

public abstract class DataReceiverBase extends AbstractVisionThread {
	
	VisionReceiverType mReceiverType;
	ReceiverSelector mReceiverSelector;
	String fileName;
	int port;
	int updateRate;
	boolean isRestartingServer;
	
	protected DataReceiverBase(String k_threadName, String fileName, int port, int updateRate, boolean isRestartingServer) {

		super(k_threadName);
		this.fileName = fileName;
		this.port = port;
		this.updateRate = updateRate;
		this.isRestartingServer = isRestartingServer;
	}
	
	@Override
	protected void init() {

		mReceiverSelector = new ReceiverSelector(fileName, port, updateRate, isRestartingServer);
		mReceiverSelector.setReceiver(mReceiverType);
	}
	
	@Override
	@Deprecated
	public void start(int updateRate) {

		super.start(updateRate);


	}
	public void start(int updateRate, VisionReceiverType receiverType) {

		mReceiverType = receiverType;
		super.start(updateRate);
	}
}
