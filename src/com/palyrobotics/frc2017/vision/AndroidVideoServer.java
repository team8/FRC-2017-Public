package com.palyrobotics.frc2017.vision;

import java.io.*;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Receives data from the android and stores it into a queue.
 *
 * @author Quintin
 */
public class AndroidVideoServer extends AbstractVisionServer {

	private static AndroidVideoServer s_instance;

	/**
	 * Gets the singleton for this class from a static context.
	 * If the instance is null, create a new one
	 *
	 * @return The singleton
	 */
	public static AndroidVideoServer getInstance() {
		if (s_instance == null)
			s_instance = new AndroidVideoServer();
		return s_instance;
	}

	// Queue of frames that have been received from the android
	public static ConcurrentLinkedQueue<byte[]> s_frameQueue;

	private final Object lock = new Object();

	private AndroidVideoServer() {

		super("AndroidVideoServer");
	}

	/**
	 * Reads a frame from the connection to the android
	 *
	 * @return The frame obtained from reading the socket to the phone
	 */
	private byte[] readBytes(){

		if (m_client == null || m_client.isClosed() || !m_client.isConnected()){
			setServerState(ServerState.ATTEMPTING_CONNECTION);
			return null;
		}

		try {
			// Initialize client inputs
			DataInputStream dis = new DataInputStream(m_client.getInputStream());

			// Get byte data
			int len = dis.readInt();
			byte[] data = new byte[len];
			if (len > 0) {
				dis.readFully(data);
			}

			return data;
		} catch (SocketException e) {
			log("Broken connection, attempting to reconnect...");
			e.printStackTrace();
			setServerState(ServerState.ATTEMPTING_CONNECTION);
			return null;
		} catch (EOFException e) {
			setServerState(ServerState.ATTEMPTING_CONNECTION);
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			setServerState(ServerState.ATTEMPTING_CONNECTION);
			return null;
		}
	}

//	private byte[] compressGZIP(byte[] data){
//		if(null == data || data.length == 0){
//			return null;
//		}
//
//		// Initialize data streams
//		ByteArrayInputStream in = new ByteArrayInputStream(data);
//		ByteArrayOutputStream out = new ByteArrayOutputStream(data.length/2);
//		OutputStream compress = null;
//
//		try {
//			compress = new GZIPOutputStream(out);
//			ByteStreams.copy(in, compress);
//			compress.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//
//		return out.toByteArray();
//	}

	/**
	 * Tries to read the data from the socket to the android
	 */
	private void updateData() {

		synchronized (lock) {
			// Read data from socket
			byte[] data = readBytes();

			// Check if data is legit
			if (data != null && data.length != 0) {

				// Make sure queue does not get too big
				if (s_frameQueue.size() < 10)
					s_frameQueue.remove();

				s_frameQueue.add(data);
			}
		}
	}

	@Override
	protected void afterUpdate() {

		switch (m_serverState){

			case OPEN:
				updateData();
				break;
		}
	}

	@Override
	protected void tearDown() {

	}
}
