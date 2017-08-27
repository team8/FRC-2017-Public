package com.palyrobotics.frc2017.vision;

import com.palyrobotics.frc2017.config.Constants;

import java.io.*;

/**
 * Starts {@link HTTPVideoServer} and {@link NexusVideoServer}
 *
 * @author Quintin
 */
public class VideoManager implements Runnable{

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
	private static VideoManager s_instance;
	private VisionServerState m_visionServerState = VisionServerState.PREINIT;

	// Utility variables
	private double m_secondsAlive = 0;
	private double m_stateAliveTime = 0;
	private boolean m_running = false;
	private boolean m_testing = false;
	private String m_defaultJPEGPath;
	private NexusVideoServer m_androidServer;
	private HTTPVideoServer m_mjpegServer;

	/**
	 * Creates a VideoManager instance
	 * Cannot be called outside as a Singleton
	 */
	private VideoManager(){}

	/**
	 * @return The instance of the BST
	 */
	public static VideoManager getInstance(){
		if(s_instance == null){
			s_instance = new VideoManager();
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
		System.out.println("Debug: VideoManager AndroidServerState - "+ m_visionServerState);
	}

	/**
	 * Starts the VideoManager thread
	 * <br>Created server socket opens on given port
	 */
	public void start(boolean testing){

		if(!m_visionServerState.equals(VisionServerState.PREINIT)){ // This should never happen
			System.out.println("Error: in VideoManager.start(), " +
					"socket is already initialized");
		}

		if(m_running){  // This should never happen
			System.out.println("Error: in VideoManager.start(), " +
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

		System.out.println("Starting Thread: VideoManager");
		(new Thread(this, "VideoManager")).start();
	}

	private VisionServerState InitializeConnections() {
		m_androidServer = new NexusVideoServer(m_testing, Constants.kAndroidVisionSocketPort);
		m_mjpegServer = new HTTPVideoServer(Constants.kMJPEGServerSocketPort);

		m_androidServer.start();
		m_mjpegServer.start();

		return VisionServerState.STREAMING;
	}

	private VisionServerState WriteJPEG(){
		// Initialize data
		byte[] data = m_androidServer.getData();

		// Write Image to JPEG stream
		try {
			m_mjpegServer.writeImageToServer(data);

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
					System.out.println("Error: in VideoManager.run(), " +
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
