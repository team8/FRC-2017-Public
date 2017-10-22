package com.palyrobotics.frc2017.vision;

import com.palyrobotics.frc2017.config.Constants;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VideoReceiver extends DataReceiverBase{
	
	byte[] image;
	protected VideoReceiver() {
		super("Video Receiver", Constants.kVisionVideoFileName, Constants.kAndroidVisionSocketPort, Constants.kAndroidVisionSocketUpdateRate);
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
