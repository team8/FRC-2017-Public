package com.palyrobotics.frc2017.vision;

import com.palyrobotics.frc2017.config.Constants;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Receives data from the android and stores it into a queue.
 * {@link HTTPVideoServer} uses the queue to send the image to the dashboard.
 *
 * @author Quintin
 */
public class AndroidVideoServer extends AbstractVisionThread {

	public enum AndroidServerState {
		PRE_INIT, CONNECTING, RECEIVING
	}

	public enum SocketConnectionState {
		ALIVE, CLOSED
	}

	private static AndroidVideoServer s_instance;

	/**
	 * Gets the singleton for this class from a static context.
	 * If the instance is null, create a new one
	 * @return The singleton
	 * @return The singleton
	 */
	public static AndroidVideoServer getInstance() {
		if (s_instance == null)
			s_instance = new AndroidVideoServer(false, Constants.kAndroidVisionSocketPort);
		return s_instance;
	}

	public static ConcurrentLinkedQueue<byte[]> m_frameQueue;

	private AndroidServerState m_androidServerState = AndroidServerState.PRE_INIT;
	private SocketConnectionState m_socketConnectionState = SocketConnectionState.CLOSED;

	private final int k_port;
	private final boolean k_testing;

	private ServerSocket m_server;
	private Socket m_client;
	private final Object lock = new Object();

	/**
	 * Creates an Android Video Server
	 *
	 * @param k_testing Whether or not we are testing
	 * @param k_port The port to open the socket
	 */
	public AndroidVideoServer(final boolean k_testing, final int k_port){

		super(Constants.kAndroidVisionSocketUpdateRate, "AndroidVideoServer");

		this.k_testing = k_testing;
		this.k_port = k_port;
	}

	/**
	 * Sets the state of socket connection
	 * @param state State to switch to
	 */
	private void setState(AndroidServerState state){
		m_androidServerState = state;
	}

	private void setConnectionState(SocketConnectionState state){
		m_socketConnectionState = state;
	}

	@Override
	protected void init() {

		if (m_androidServerState != AndroidServerState.PRE_INIT) {
			log("Thread has already been initialized. Aborting...");
		}

		// Try to create the server
		try {
			m_server = new ServerSocket(k_port);
			m_server.setReuseAddress(true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		setState(AndroidServerState.CONNECTING);
	}

	/**
	 * Reads a frame from the connection to the android
	 *
	 * @return The frame obtained from reading the socket to the phone
	 */
	private byte[] readBytes(){
		if (m_client == null || m_client.isClosed() || !m_client.isConnected()){
			setConnectionState(SocketConnectionState.CLOSED);
		}

		if (m_socketConnectionState == SocketConnectionState.CLOSED) {
			log("Socket is closed, cannot read.");
			return null;
		}

		try {
			// Initialize client inputs
			DataInputStream dis = new DataInputStream(m_client.getInputStream());

			// Get byte data
			int len = dis.readInt();
			byte[] data = new byte[len];
			if(len > 0){
				dis.readFully(data);
			}

			return data;
		} catch (SocketException e) {
			log("Broken connection, attempting to reconnect...");
			e.printStackTrace();
			setConnectionState(SocketConnectionState.CLOSED);
			return null;
		} catch (EOFException e) {
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			setConnectionState(SocketConnectionState.CLOSED);
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
			if(!(null == data || data.length == 0)){

				// Make sure queue does not get too big
				if (m_frameQueue.size() < 10)
					m_frameQueue.remove();

				m_frameQueue.add(data);
			}
		}
	}

	private AndroidServerState checkConnection() {
		if(m_socketConnectionState == SocketConnectionState.CLOSED){
			return AndroidServerState.CONNECTING;
		}
		return m_androidServerState;
	}

	/**
	 * Pauses the thread until a connection to the android is established
	 *
	 * @return The state after execution
	 */
	private AndroidServerState acceptConnection(){
		if (m_socketConnectionState == SocketConnectionState.ALIVE){
			log("Socket connection is already alive!");
			return AndroidServerState.RECEIVING;
		}

		try {
			// Pause thread until we accept from the client
			log("Trying to connect to client...");
			m_client = m_server.accept();
			log("Connected to client: " + m_client.getPort());

			setConnectionState(SocketConnectionState.ALIVE);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return AndroidServerState.RECEIVING;
	}

	@Override
	protected void update() {

		switch (m_androidServerState){

			case PRE_INIT:
				log("Thread is not initialized while in update.");
				break;

			case RECEIVING:
				setState(checkConnection());
				updateData();
				break;

			case CONNECTING:
				setState(acceptConnection());
				break;
		}
	}

	@Override
	protected void tearDown() {

	}
}
