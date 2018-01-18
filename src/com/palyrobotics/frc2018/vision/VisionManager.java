package com.palyrobotics.frc2018.vision;

import java.util.logging.Level;

import com.palyrobotics.frc2018.config.Constants;
import com.palyrobotics.frc2018.util.logger.Logger;
import com.palyrobotics.frc2018.vision.networking.ReceiverBase;
import com.palyrobotics.frc2018.vision.networking.VisionDataReceiver;
import com.palyrobotics.frc2018.vision.networking.VisionVideoReceiver;
import com.palyrobotics.frc2018.vision.networking.VisionVideoServer;
import com.palyrobotics.frc2018.vision.networking.recievers.ReceiverSelector.VisionReceiverType;
import com.palyrobotics.frc2018.vision.util.VisionThreadBase;
import com.palyrobotics.frc2018.vision.util.commandline.CommandExecutor;

/**
 * @author Alvin
 */
public class VisionManager extends VisionThreadBase {

	public enum ConnectionState{
		PRE_INIT, STARTING_ADB, STARTING_SUB_PROCESSES, IDLE, START_VISION_APP, STREAMING, GIVEN_UP
	}


	// Instance and state variables
	private static VisionManager s_instance;
	private ConnectionState m_connectionState = ConnectionState.PRE_INIT;

	// Utility variables
	private ReceiverBase mReceiverBaseVideo = new VisionVideoReceiver();
	private ReceiverBase mReceiverBaseData = new VisionDataReceiver();
	private boolean m_adbServerCreated = false;
	private boolean m_visionRunning = false;

	private int initAdbRetryCount = 0;
	private boolean useTimeout = true;

	/**
	 * Creates an VisionManager instance
	 * Cannot be called outside as a Singleton
	 */
	private VisionManager(){
		super("Vision Manager");
	}

	/**
	 * @return The instance of the ACH
	 */
	public static VisionManager getInstance(){

		if(s_instance == null){
			s_instance = new VisionManager();
		}
		return s_instance;
	}

	/**
	 * Sets the state of connection
	 * @param state State to switch to
	 */
	private void SetState(ConnectionState state){
		m_connectionState = state;
	}

	@Override
	@Deprecated
	public void start(int updateRate) {
		super.start(updateRate);
	}
	
	public void start(int updateRate, boolean isTesting) {

		super.start(updateRate);
		CommandExecutor.setTesting(isTesting);
	}
	/**
	 * Starts the VisionManager thread
	 * <br>(accounts for running program for testing)</br>
	 */
	@Override
	public void init() {
		
		if(m_connectionState != ConnectionState.PRE_INIT) {    // This should never happen
//			System.out.println("Error: in VisionManager.start(), "
//					+ "connection is already initialized");
		}

		// Initialize Thread Variables
		this.SetState(ConnectionState.STARTING_ADB);
	}

	private ConnectionState StartSubprocesses() {

		mReceiverBaseData.start(Constants.kAndroidDataSocketUpdateRate, VisionReceiverType.JSON);
		mReceiverBaseVideo.start(Constants.kAndroidVisionSocketUpdateRate, VisionReceiverType.SOCKET);
		new VisionVideoServer().start(Constants.kMJPEGVisionSocketUpdateRate, Constants.kMJPEGServerSocketPort, false);

		return ConnectionState.STREAMING;
	}

	/**
	 * Initializes RIOdroid and RIOadb
	 * @return The state after execution
	 */
	private ConnectionState StartADB() {

		if (!CommandExecutor.isNexusConnected()){

			initAdbRetryCount++;

			if (useTimeout) {
				try {
					Thread.sleep(initAdbRetryCount*40);
				} catch (InterruptedException e) {
					Logger.getInstance().logRobotThread(Level.FINEST, e);

					return ConnectionState.GIVEN_UP;
				}
			}

			return this.m_connectionState;
		}

		if(m_adbServerCreated){
			if(!this.isAppStarted()){
				this.VisionInit();
			} else {
				this.m_visionRunning = true;
			}

			if (this.m_visionRunning) {
//				System.out.println("[Info] Connected to vision app");
//				Logger.getInstance().logRobotThread("Connected to vision app");
				return ConnectionState.STARTING_SUB_PROCESSES;
			} else {
//				System.out.println("[Warning] Could not start vision app, retrying");
//				Logger.getInstance().logRobotThread("Could not start vision app, retrying");
				return this.m_connectionState;
			}
		} else {
			this.InitializeAdbServer();
			return this.m_connectionState;
		}

	}

	private void InitializeAdbServer() {

		boolean connected = false;

		try {
			CommandExecutor.adbServerInit();
			connected = true;
		} catch (Exception e) {
//			System.out.println("[Error] in VisionManager.StartADB(), "
//					+ "could not connect..");
			Logger.getInstance().logRobotThread(Level.FINEST, e);
		}

		if(connected){      // Adb server started successfully
			m_adbServerCreated = true;
//			System.out.println("[Info] Adb server started");
//			Logger.getInstance().logRobotThread("[Info] Adb server started");
		} else {            // Adb server failed to start
//			System.out.println("[Error] Failed to start adb server");
//			Logger.getInstance().logRobotThread("[Error] Failed to start adb server");
		}
	}

	/**
	 * Sends command to boot up the vision app
	 * @return The state after execution
	 */
	private ConnectionState VisionInit(){
		boolean connected = false;
		try {
			String outp = CommandExecutor.visionInit();

			connected = true;
		} catch (Exception e) {
//			log("Could not connect in initialization.");
			Logger.getInstance().logRobotThread(Level.FINEST, e);
		}

		if(connected) {     // App started successfully
			m_visionRunning = true;
//			System.out.println("[Info] Starting Vision Stream");
			return ConnectionState.STREAMING;
		} else {            // Failed to start app
			return m_connectionState;
		}
	}

	public boolean isAppStarted(){

		String pidret = CommandExecutor.appPID();
		return pidret != null && !pidret.isEmpty() && pidret != "";
	}

	public boolean isServerStarted(){
		return m_adbServerCreated;
	}

	/**
	 * Updates the thread at {@link Constants#kAndroidConnectionUpdateRate} ms
	 */
	@Override
	public void update() {

		switch(m_connectionState){

		case PRE_INIT:	// Shouldn't happen, but can due to error
//			System.out.println("Error: in VisionManager.run(), "
//					+ "thread running on preinit state");
			break;

		case STARTING_ADB:	// Triggered by start(), should be called externally
			SetState(StartADB());
			break;

		case STARTING_SUB_PROCESSES:
			SetState(StartSubprocesses());
			break;

		case START_VISION_APP:	// Triggered by StartVisionApp(), should be called externally
			SetState(VisionInit());
			break;

		case STREAMING:
			break;
			
		case IDLE:
			break;
		}
	}
	
	@Override
	protected void tearDown() {
		// TODO Auto-generated method stub

	}

}
