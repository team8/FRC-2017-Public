package com.palyrobotics.frc2017.vision;

import com.google.common.io.ByteStreams;
import com.palyrobotics.frc2017.config.Constants;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.zip.GZIPOutputStream;

/**
 * Supplies wrapper methods for using adb to control the Android
 *
 * <h1><b>Fields</b></h1>
 * 	<ul>
 * 		<li>Instance and State variables:
 * 			<ul>
 * 				<li>{@link NexusVideoServer#m_androidServerState}: Current state of socket connection (private)</li>
 * 				<li><b>See:</b>{@link AndroidServerState}</li>
 * 			</ul>
 * 		</li>
 * 		<li>Utility variables:
 * 			<ul>
 * 				<li>{@link NexusVideoServer#m_secondsAlive}: Private count of seconds the program has run for</li>
 * 				<li>{@link NexusVideoServer#m_stateAliveTime}: Private count of seconds the state has run for</li>
 * 				<li>{@link NexusVideoServer#m_port}: Port that the ServerSocket listens on (private)</li>
 * 				<li>{@link NexusVideoServer#m_running}: Private boolean representing whether the thread is running</li>
 * 				<li>{@link NexusVideoServer#m_server}: Server Socket object that listens for android client and receives data (private)</li>
 * 			</ul>
 * 		</li>
 * 	</ul>
 *
 * <h1><b>Accessors and Mutators</b></h1>
 * 	<ul>
 * 		<li>{@link VisionManager#getInstance()}</li>
 * 		<li>{@link NexusVideoServer#SetState(AndroidServerState)}</li>
 * 	</ul>
 *
 * <h1><b>External Access Functions</b>
 * 	<br><BLOCKQUOTE>For using as a wrapper for RIOdroid</BLOCKQUOTE></h1>
 * 	<ul>
 * 		<li>{@link NexusVideoServer#start()}</li>
 * 	</ul>
 *
 * 	<h1><b>Internal Functions</b>
 * 	 <br><BLOCKQUOTE>Paired with external access functions. These compute the actual function for the external access</BLOCKQUOTE></h1>
 * 	 <ul>
 * 	     <li>{@link NexusVideoServer#AcceptConnection()}</li>
 * 	 </ul>
 *
 * @see AndroidServerState
 * @author Alvin
 *
 */
public class NexusVideoServer implements Runnable{

	/**
	 * State of connection between the roboRIO and nexus
	 *
	 * <ul>
	 *     <li>{@link AndroidServerState#PREINIT}</li>
	 *     <li>{@link AndroidServerState#RECEIVING}</li>
	 *     <li>{@link AndroidServerState#CONNECTING}</li>
	 * </ul>
	 */
	public enum AndroidServerState {
		PREINIT, CONNECTING, RECEIVING
	}

	public enum SocketConnectionState {
		ALIVE, CLOSED
	}

	// Instance and state variables
	private AndroidServerState m_androidServerState = AndroidServerState.PREINIT;
	private SocketConnectionState m_socketConnectionState = SocketConnectionState.CLOSED;

	// Utility variables
	private double m_secondsAlive = 0;
	private double m_stateAliveTime = 0;
	private int m_port;
	private boolean m_running = false;
	private boolean m_testing = false;
	private ServerSocket m_server;
	private Socket m_client;
	private byte[] frame;
	private Object lock = new Object();

	/**
	 * Creates a NexusVideoServer instance
	 * Cannot be called outside as a Singleton
	 */
	public NexusVideoServer(boolean testing, int port){
		m_testing = testing;
		m_port = port;
	}

	/**
	 * Sets the state of socket connection
	 * @param state State to switch to
	 */
	private void SetState(AndroidServerState state){
		m_androidServerState = state;
	}

	private void SetConnectionState(SocketConnectionState state){
		m_socketConnectionState = state;
	}

	/**
	 * (DEBUG) Logs the Socket state
	 */
	private void logSocketState(){
		System.out.println("Debug: NexusVideoServer AndroidServerState - "+ m_androidServerState);
	}

	/**
	 * Starts the NexusVideoServer thread
	 * <br>Created server socket opens on given port
	 */
	public void start(){
		if(!m_androidServerState.equals(AndroidServerState.PREINIT)){ // This should never happen
			System.out.println("Error: in NexusVideoServer.start(), " +
					"socket is already initialized");
		}

		if(m_running){  // This should never happen
			System.out.println("Error: in NexusVideoServer.start(), " +
					"thread is already running");
		}

		try {
			m_server = new ServerSocket(m_port);
			m_server.setReuseAddress(true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.SetState(AndroidServerState.CONNECTING);
		m_running = true;

		System.out.println("Starting Thread: NexusVideoServer on port "+m_port);
		(new Thread(this, "NexusVideoServer")).start();
	}

	private byte[] readBytes(){
		if(m_client == null || m_client.isClosed() || !m_client.isConnected()){
			this.SetConnectionState(SocketConnectionState.CLOSED);
		}

		if(m_socketConnectionState.equals(SocketConnectionState.CLOSED)){
//			System.out.println("Error: in NexusVideoServer.readBytes(), " +
//					"socket is closed");
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

//			return compressGZIP(data);
			return data;
		} catch (SocketException e) {
			System.out.println("Error VideoManager.readBytes(), " +
					"Broken connection, attempting to reconnect: " + e.getStackTrace().toString());
			this.SetConnectionState(SocketConnectionState.CLOSED);
			return null;
		} catch (EOFException e) {
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			this.SetConnectionState(SocketConnectionState.CLOSED);
			return null;
		}
	}

	private byte[] compressGZIP(byte[] data){
		if(null == data || data.length == 0){
			return null;
		}

		// Initialize data streams
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ByteArrayOutputStream out = new ByteArrayOutputStream(data.length/2);
		OutputStream compress = null;

		try {
			compress = new GZIPOutputStream(out);
			ByteStreams.copy(in, compress);
			compress.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return out.toByteArray();
	}

	private void updateData() {
		synchronized (lock) {
			byte[] data = readBytes();

			if(!(null == data || data.length == 0)){
				frame = data;
			}
		}
	}

	public byte[] getData() {
		return frame;
	}

	private AndroidServerState CheckConnection() {
		if(m_socketConnectionState.equals(SocketConnectionState.CLOSED)){
			return AndroidServerState.CONNECTING;
		}
		return m_androidServerState;
	}

	/**
	 * Accept connection from a client
	 * @return The state after execution
	 */
	private AndroidServerState AcceptConnection(){
		if(m_socketConnectionState.equals(SocketConnectionState.ALIVE)){
			System.out.println("Error in VideoManager.AcceptConnection(), " +
					"Socket connection is already Alive");
			return AndroidServerState.RECEIVING;
		}

		try {
			// Accept client
			System.out.println("Trying to connect to client");
			m_client = m_server.accept();
			System.out.println("Connected to client: " + m_client.getPort());

			this.SetConnectionState(SocketConnectionState.ALIVE);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return AndroidServerState.RECEIVING;
	}

	/**
	 * Updates the thread at {@link Constants#kAndroidConnectionUpdateRate} ms
	 */
	@Override
	public void run() {
		while(m_running){
			AndroidServerState initState = m_androidServerState;
			switch (m_androidServerState){

				case PREINIT:   // This should never happen
					System.out.println("Error: in NexusVideoServer.run(), " +
							"thread running on preinit state");
					break;

				case RECEIVING:
					this.SetState(this.CheckConnection());
					this.updateData();
					break;

				case CONNECTING:
					this.SetState(this.AcceptConnection());
					break;
			}

			// Reset state start time if state changed
			if(!initState.equals(m_androidServerState)){
				m_stateAliveTime = m_secondsAlive;
			}

			// Handle thread sleeping, sleep for set constant update delay
			try {
				Thread.sleep(Constants.kAndroidVisionSocketUpdateRate);
				m_secondsAlive += Constants.kAndroidVisionSocketUpdateRate /1000.0;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
