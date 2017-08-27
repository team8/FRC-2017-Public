package com.palyrobotics.frc2017.vision;

import com.palyrobotics.frc2017.config.Constants;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Sends video from the robot to the dashboard
 *
 * @author Quintin
 */
public class HTTPVideoServer extends AbstractVisionThread {

	/**
	 * State of connection between the rio and the javascript client
	 */
	public enum HTTPServerState {
		PRE_INIT, SENDING
	}


	// Instance and state variables
	private HTTPServerState m_httpServerState = HTTPServerState.PRE_INIT;

	// Utility variables
	private final int k_port;
	private byte[] m_defaultImage;
	private ServerSocket m_server;
	private Socket m_client;
	private String m_boundary = "team8robotics";

	/**
	 * Creates a NexusDataServer instance
	 * Cannot be called outside as a Singleton
	 */
	public HTTPVideoServer(final int k_port) {

		super(Constants.kAndroidDataSocketUpdateRate, "HTTPVideoServer");

		this.k_port = k_port;

//		try {
//			BufferedImage img = ImageIO.read(new File(path));
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			ImageIO.write(img, "JPEG", baos);
//			baos.close();
//
//			m_defaultImage = baos.toByteArray();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
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
	 * Writes the image to the output stream for the client.
	 *
	 * @throws IOException
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

			// Prepare the content to send.
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
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
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
