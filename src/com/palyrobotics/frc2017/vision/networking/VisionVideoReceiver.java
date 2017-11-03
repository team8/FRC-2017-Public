package com.palyrobotics.frc2017.vision.networking;

import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.vision.VisionData;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VisionVideoReceiver extends ReceiverBase {
	
	byte[] image;

	public VisionVideoReceiver() {

		super("Video Receiver", Constants.kVisionVideoFileName, Constants.kVideoPort, Constants.kAndroidVisionSocketUpdateRate, false);
	}
	
	@Override
	protected void update() {

		ConcurrentLinkedQueue<byte[]> frameQueue = VisionData.getVideoQueue();
		try {
			image = mReceiverSelector.getReceiver().extractDataBytes();
			if (image != null && image.length != 0) {

				// Make sure queue does not get too big
				while (frameQueue.size() > 10)
					frameQueue.remove();

				frameQueue.add(image);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void tearDown() {

	}
	
	public byte[] getImage() {
		return image;
	}

}
