package com.palyrobotics.frc2018.vision.networking;

import com.palyrobotics.frc2018.util.logger.Logger;
import com.palyrobotics.frc2018.vision.VisionData;
import com.palyrobotics.frc2018.vision.util.VisionServerBase;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

/**
 * Sends video from the robot to the dashboard.
 * Video data is obtained from queue in {@link VisionData}
 *
 * @author Quintin Dwight
 */
public class VisionVideoServer extends VisionServerBase {

	private final byte[] k_defaultImage;

	public VisionVideoServer() {

		super("Android Video Server");

		byte[] imageInBytes = null;

		try {
			BufferedImage image = ImageIO.read(new File("default.jpeg"));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", baos);

			imageInBytes = baos.toByteArray();

		} catch (IOException e) {

			Logger.getInstance().logRobotThread(Level.FINEST, e);
		}

		k_defaultImage = imageInBytes;
	}

	@Override
	public void init() {

		setServerState(ServerState.ATTEMPTING_CONNECTION);
	}

	/**
	 * Writes the image given in a byte array to the output stream for the javascript client to read and display on the dashboard.
	 *
	 * @throws IOException Thrown by socket
	 */
	private void writeImageToServer(byte[] data) {

		try {

			final OutputStream clientOutputStream = m_client.getOutputStream();
			final DataOutputStream writer = new DataOutputStream(clientOutputStream);

			writer.writeInt(data.length);
			writer.write(data);

		} catch (IOException e) {

			Logger.getInstance().logRobotThread(Level.FINEST, e);

			closeClient();
		} catch (NullPointerException e){
			Logger.getInstance().logRobotThread(Level.FINEST, e);
		}
	}

	@Override
	protected void afterUpdate() {

		switch (m_serverState) {

			case OPEN: {

				final ConcurrentLinkedQueue<byte[]> videoFrames = VisionData.getVideoQueue();

				// Send frame from nexus if they exist, else show default image
				final byte[] imageToSend = videoFrames.size() > 0 ? videoFrames.remove() : k_defaultImage;

				writeImageToServer(imageToSend);

				break;
			}
		}
	}

	@Override
	protected void tearDown() {

	}
}
