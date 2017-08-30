package com.palyrobotics.frc2017.vision;

import java.io.*;
import java.nio.file.Files;

/**
 * Sends video from the robot to the dashboard.
 * Video data is obtained from queue in {@link AndroidVideoServer}
 *
 * @author Quintin
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

	private final String k_defaultImagePath = "/home/lvuser/mjpg/frame.jpg", k_defaultImagePathTesting = "FRC-2017-Private/test/com/palyrobotics/frc2017/vision/frame.jpg";

	private byte[] m_defaultImage;

	private HTTPVideoServer() {

		super("HTTPVideoServer");
	}

	@Override
	public void init(){

		super.init();

		final String k_imagePath = m_testing ? k_defaultImagePathTesting : k_defaultImagePath;

		// Try to read the default image file
		try {
			final File imageFile = new File(k_imagePath);
			m_defaultImage = Files.readAllBytes(imageFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the image given in a byte array to the output stream for the javascript client to read and display on the dashboard.
	 *
	 * @throws IOException Thrown by socket
	 */
	public void writeImageToServer(byte[] data) throws IOException {

		if (m_client == null) {
			log("Client is null somehow. Aborting...");
			setServerState(ServerState.ATTEMPTING_CONNECTION);
			return;
		}

		BufferedReader reader = null;
		PrintStream output = null;
		try {
			String route = null;

			// Read HTTP headers and parse out the route.
			reader = new BufferedReader(new InputStreamReader(m_client.getInputStream()));
			String line;
			while (!(line = reader.readLine()).isEmpty()) {
				if (line.startsWith("GET /")) {
					int start = line.indexOf('/') + 1;
					int end = line.indexOf(' ', start);
					route = line.substring(start, end);
					break;
				}
			}

			// Output stream that we send the response to
			output = new PrintStream(m_client.getOutputStream());

			// Prepare the content to send
			if (route == null) {
				writeServerError(output);
				setServerState(ServerState.ATTEMPTING_CONNECTION);
				return;
			}
			if (data == null) {
				writeServerError(output);
				setServerState(ServerState.ATTEMPTING_CONNECTION);
				return;
			}

			// Send out the content to the javascript client
			output.println("HTTP/1.1 200 OK");
			output.println("Cache-Control: no-cache");
			output.println("Content-Type: image/jpeg"   );
			output.println("Content-Length: " + data.length);
			output.println();
			output.write(data);
			output.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (output != null)
				output.close();
			if (reader != null)
				reader.close();

			m_client.close();
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
	protected void update() {

		super.update();

		switch (m_serverState) {

			case OPEN:
				// Make sure queue has something in it
				if (AndroidVideoServer.s_frameQueue.size() > 0) {
					// Get next frame
					byte[] frame = AndroidVideoServer.s_frameQueue.remove();
					try {
						writeImageToServer(frame);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					log("Frame queue is empty!");
				}
				break;
		}
	}

	@Override
	protected void tearDown() {

	}
}
