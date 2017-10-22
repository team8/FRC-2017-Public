package com.palyrobotics.frc2017.vision;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VideoReceiver extends DataReceiverBase{
	
	byte[] image;
	protected VideoReceiver(String k_threadName, String fileName, int port, int updateRate) {
		
		super(k_threadName, fileName, port, updateRate);	
	}
	
	@Override
	protected void update() {
		ConcurrentLinkedQueue<byte[]> frameQueue = VisionData.getVideoQueue();
		try {
			image = mReceiverSelector.getReciever().extractDataBytes();
			if (image != null && image.length != 0) {

				// Make sure queue does not get too big
				if (frameQueue.size() < 10)
					frameQueue.remove();

				frameQueue.add(image);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void tearDown() {
		// TODO Auto-generated method stub
		
	}
	
	public byte[] getImage() {
		return image;
	}

}
