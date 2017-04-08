package com.palyrobotics.frc2017.vision;

import com.palyrobotics.frc2017.config.Constants;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Supplies wrapper methods for using adb to control the Android
 *
 * <h1><b>Fields</b></h1>
 * 	<ul>
 * 		<li>Instance and State variables:
 * 			<ul>
 * 				<li>{@link VisionServerThread#s_instance}: Private instance of this class (Singleton)</li>
 * 				<li>{@link VisionServerThread#m_visionServerState}: Current state of socket connection (private)</li>
 * 				<li><b>See:</b>{@link VisionServerState}</li>
 * 			</ul>
 * 		</li>
 * 		<li>Utility variables:
 * 			<ul>
 * 				<li>{@link VisionServerThread#m_secondsAlive}: Private count of seconds the program has run for</li>
 * 				<li>{@link VisionServerThread#m_stateAliveTime}: Private count of seconds the state has run for</li>
 * 				<li>{@link VisionServerThread#m_running}: Private boolean representing whether the thread is running</li>
 * 			</ul>
 * 		</li>
 * 	</ul>
 *
 * <h1><b>Accessors and Mutators</b></h1>
 * 	<ul>
 * 		<li>{@link AndroidConnectionHelper#getInstance()}</li>
 * 		<li>{@link VisionServerThread#SetState(VisionServerState)}</li>
 * 	</ul>
 *
 * <h1><b>External Access Functions</b>
 * 	<br><BLOCKQUOTE>For using as a wrapper for RIOdroid</BLOCKQUOTE></h1>
 * 	<ul>
 * 		<li>{@link VisionServerThread#start(boolean)}</li>
 * 	</ul>
 *
 * 	<h1><b>Internal Functions</b>
 * 	 <br><BLOCKQUOTE>Paired with external access functions. These compute the actual function for the external access</BLOCKQUOTE></h1>
 * 	 <ul>
 * 	     <li>{@link VisionServerThread#InitializeConnections()}</li>
 * 	 </ul>
 *
 * @see VisionServerState
 * @author Alvin
 *
 */
public class VisionServerThread implements Runnable{

	/**
	 * State of connection between the roboRIO and nexus
	 *
	 * <ul>
	 *     <li>{@link VisionServerState#PREINIT}</li>
	 *     <li>{@link VisionServerState#STREAMING}</li>
	 * </ul>
	 */
	public enum VisionServerState {
		PREINIT, STREAMING, INITIALIZE_SOCKETS
	}

	// Instance and state variables
	private static VisionServerThread s_instance;
	private VisionServerState m_visionServerState = VisionServerState.PREINIT;

	// Utility variables
	private double m_secondsAlive = 0;
	private double m_stateAliveTime = 0;
	private boolean m_running = false;
	private boolean m_testing = false;
	private String m_defaultJPEGPath;
	private AndroidServerSocket m_androidServer;
	private MJPEGServerSocket m_mjpegServer;

	/**
	 * Creates a VisionServerThread instance
	 * Cannot be called outside as a Singleton
	 */
	private VisionServerThread(){}

	/**
	 * @return The instance of the BST
	 */
	public static VisionServerThread getInstance(){
		if(s_instance == null){
			s_instance = new VisionServerThread();
		}
		return s_instance;
	}

	/**
	 * Sets the state of socket connection
	 * @param state State to switch to
	 */
	private void SetState(VisionServerState state){
		m_visionServerState = state;
	}

	/**
	 * (DEBUG) Logs the Socket state
	 */
	private void logSocketState(){
		System.out.println("Debug: VisionServerThread AndroidServerState - "+ m_visionServerState);
	}

	/**
	 * Starts the VisionServerThread thread
	 * <br>Created server socket opens on given port
	 */
	public void start(boolean testing){

		if(!m_visionServerState.equals(VisionServerState.PREINIT)){ // This should never happen
			System.out.println("Error: in VisionServerThread.start(), " +
					"socket is already initialized");
		}

		if(m_running){  // This should never happen
			System.out.println("Error: in VisionServerThread.start(), " +
					"thread is already running");
		}

		m_testing = testing;

		if(!m_testing){
			m_defaultJPEGPath = "/home/lvuser/mjpg/frame.jpg";
		} else {
			m_defaultJPEGPath = "FRC-2017-Private/test/com/palyrobotics/frc2017/vision/frame.jpg";
		}

		this.SetState(VisionServerState.INITIALIZE_SOCKETS);
		m_running = true;

		System.out.println("Starting Thread: VisionServerThread");
		(new Thread(this, "VisionServerThread")).start();
	}

	private VisionServerState InitializeConnections() {
		m_androidServer = new AndroidServerSocket(m_testing, Constants.kAndroidVisionSocketPort);
		m_mjpegServer = new MJPEGServerSocket(Constants.kMJPEGServerSocketPort, m_defaultJPEGPath);

		m_androidServer.start();
		m_mjpegServer.start();

		return VisionServerState.STREAMING;
	}

	private VisionServerState WriteJPEG(){
		// Initialize data
		byte[] data = m_androidServer.getData();

		// Write Image to JPEG stream
		try {
			m_mjpegServer.handle(data);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return m_visionServerState;
	}

	/**
	 * Updates the thread at {@link Constants#kAndroidConnectionUpdateRate} ms
	 */
	@Override
	public void run() {
		while(m_running){
			VisionServerState initState = m_visionServerState;
			switch (m_visionServerState){

				case PREINIT:   // This should never happen
					System.out.println("Error: in VisionServerThread.run(), " +
							"thread running on preinit state");
					break;

				case STREAMING:
					this.SetState(this.WriteJPEG());
					break;

				case INITIALIZE_SOCKETS:
					this.SetState(this.InitializeConnections());
					break;
			}

			// Reset state start time if state changed
			if(!initState.equals(m_visionServerState)){
				m_stateAliveTime = m_secondsAlive;
			}

			// Handle thread sleeping, sleep for set constant update delay
			try {
				Thread.sleep(Constants.kMJPEGVisionSocketUpdateRate);
				m_secondsAlive += (Constants.kMJPEGVisionSocketUpdateRate) /1000.0;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
