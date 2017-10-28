package com.palyrobotics.frc2017.vision;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

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
	private void writeImageToServer(byte[] data) throws IOException {

		PrintStream output;
		try {

			// Output stream that we send the response to
			output = new PrintStream(m_client.getOutputStream());

			// Send out the content to the javascript client
			output.println("HTTP/1.1 200 OK");
			output.println("Cache-Control: no-cache, no-store, must-revalidate");
			output.println("Content-Type: image/jpeg"   );
			output.println("Content-Length: " + data.length);
			output.println();
			output.write(data);
			output.flush();

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

				try {
					writeImageToServer(k_defaultImage);
				} catch (IOException e) {
					e.printStackTrace();
				}

//				// Make sure queue has something in it
//				if (VisionData.getVideoQueue().size() > 0) {
//					// Get next frame
//					byte[] frame = VisionData.getVideoQueue().remove();
//					try {
//						writeImageToServer(frame);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
				break;
			}
		}
	}

	@Override
	protected void tearDown() {

	}
}
