package com.palyrobotics.frc2017.vision;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Sends video from the robot to the dashboard.
 * Video data is obtained from queue in {@link VisionData}
 *
 * @author Quintin Dwight
 */
public class HTTPVideoServer extends AbstractVisionServer {

	public static HTTPVideoServer s_instance;

	/**
	 * Gets the singleton for this class from a static context.
	 * If the instance is null, create a new one
	 *
	 * @return The singleton
	 */
	public static HTTPVideoServer getInstance()
	{
		if (s_instance == null)
			s_instance = new HTTPVideoServer();

		return s_instance;
	}

	private final byte[] k_defaultImage;

	private HTTPVideoServer() {

		super("HTTP Video Server");

		byte[] imageInBytes = null;

		try {
			BufferedImage image = ImageIO.read(new File("C:/Users/qhdwi/Documents/GitHub/FRC-2017-Private/test/com/palyrobotics/frc2017/vision/frame.jpg"));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", baos);

			imageInBytes = baos.toByteArray();

		} catch (IOException e) {

			e.printStackTrace();
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

			// Output stream that we send the response to
			final PrintStream output = new PrintStream(m_client.getOutputStream());

			System.out.println(data.length);

			// Send out the content to the javascript client
			output.println("HTTP/1.1 200 OK");
			output.println("Cache-Control: no-cache");
			output.println("Content-Type: image/jpeg");
			output.println("Content-Length: " + Integer.toString(data.length));
			output.println();
			output.write(data);
			output.flush();
			output.close();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			closeClient();
		}
	}

	/**
	 * Writes a server error response (HTTP/1.0 500) to the given output stream.
	 *
	 * @param output The output stream.
	 */
	private void writeServerError(PrintStream output) {

		output.println("HTTP/1.0 500 Internal Server Error");
		output.flush();
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
