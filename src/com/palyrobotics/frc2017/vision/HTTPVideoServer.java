package com.palyrobotics.frc2017.vision;

import com.palyrobotics.frc2017.config.Constants;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;

/**
 * Sends video from the robot to the dashboard
 *
 * @author Quintin
 */
public class HTTPVideoServer extends AbstractVisionThread {

	private final String k_defaultImagePath = "/home/lvuser/mjpg/frame.jpg", k_defaultImagePathTesting = "FRC-2017-Private/test/com/palyrobotics/frc2017/vision/frame.jpg";

	/**
	 * State of connection between the rio and the javascript client
	 */
	public enum HTTPServerState {
		PRE_INIT, SENDING
	}

	// Instance and state variables
	private HTTPServerState m_httpServerState = HTTPServerState.PRE_INIT;
	public static HTTPVideoServer s_instance;

	/**
	 * Gets the singleton for this class from a static context.
	 * If the instance is null, create a new one
	 * @return The singleton
	 */
	public static HTTPVideoServer getInstance()
	{
		if (s_instance == null)
			s_instance = new HTTPVideoServer(false, Constants.kMJPEGServerSocketPort);
		return s_instance;
	}

	private final int k_port;
	private byte[] m_defaultImage;
	private ServerSocket m_server;
	private Socket m_client;

	/**
	 * Creates an HTTP Video Server
	 *
	 * @param k_testing Whether or not we are testing
	 * @param k_port The port to open the connection to the dashboard
	 */
	public HTTPVideoServer(final boolean k_testing, final int k_port) {

		super(Constants.kAndroidDataSocketUpdateRate, "HTTPVideoServer");

		this.k_port = k_port;

		final String k_imagePath = k_testing ? k_defaultImagePathTesting : k_defaultImagePath;

		// Try to read the default image file
		try {
			final File imageFile = new File(k_imagePath);
			m_defaultImage = Files.readAllBytes(imageFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the state of socket connection
	 * @param state State to switch to
	 */
	private void setState(HTTPServerState state){
		m_httpServerState = state;
	}

	@Override
	public void init(){

		if (m_httpServerState != HTTPServerState.PRE_INIT) {
			log("Thread has already been initialized. Aborting...");
			return;
		}

		// Try to create server
		try {
			// Create the image server
			m_server = new ServerSocket(k_port);
			m_server.setReuseAddress(true);
			log("HTTP video server started.");
		} catch (IOException e) {
			e.printStackTrace();
		}

		setState(HTTPServerState.SENDING);
	}


	/**
	 * Writes the image given in a byte array to the output stream for the javascript client to read and display on the dashboard.
	 *
	 * @throws IOException Thrown by socket
	 */
	public void writeImageToServer(byte[] data) throws IOException {

		// Wait until the javascript client requests a new frame.
		// This pauses the thread completely.
		m_client = m_server.accept();

		//System.out.println("Connected to javascript HTTP image socket on port: " + m_client.getPort());

		if(m_client == null) {
			log("Client is null somehow. Aborting...");
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
			if (null == route) {
				writeServerError(output);
				return;
			}
			if (null == data) {
				writeServerError(output);
				return;
			}

			// Send out the content to the javascript client
			output.println("HTTP/1.1 200 OK");
			output.println("Cache-Control: no-cache");
			//output.println("Content-Encoding: gzip");
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

		switch (m_httpServerState) {

			case PRE_INIT:
				log("Thread is not initialized while in update.");
				break;

			case SENDING:
				try {
					writeImageToServer(null);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
		}
	}

	@Override
	protected void tearDown() {

	}
}
