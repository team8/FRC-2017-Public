package com.palyrobotics.frc2017.vision;

import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.util.logger.Logger;
import com.palyrobotics.frc2017.vision.ReceiverSelector.VisionReceiverType;

/**
 * @author Alvin
 */
public class VisionManager extends AbstractVisionThread {

	public enum ConnectionState{
		PRE_INIT, STARTING_ADB, STARTING_SUB_PROCESSES, IDLE, START_VISION_APP, STREAMING;
	}


	// Instance and state variables
	private static VisionManager s_instance;
	private ConnectionState m_connectionState = ConnectionState.PRE_INIT;

	// Utility variables
	private DataReceiverBase mReceiverBaseVideo = new VideoReceiver();
	private DataReceiverBase mReceiverBaseData = new DataThread();
	private boolean m_adbServerCreated = false;
	private boolean m_visionRunning = false;

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
			System.out.println("Error: in VisionManager.start(), "
					+ "connection is already initialized");
		}

		// Initialize Thread Variables
		this.SetState(ConnectionState.STARTING_ADB);
	}

	private ConnectionState StartSubprocesses() {

		mReceiverBaseData.start(Constants.kAndroidDataSocketUpdateRate, VisionReceiverType.JSON);
		mReceiverBaseVideo.start(Constants.kAndroidVisionSocketUpdateRate, VisionReceiverType.SOCKET);
		HTTPVideoServer.getInstance().start(Constants.kMJPEGVisionSocketUpdateRate, Constants.kMJPEGServerSocketPort, true);

		return ConnectionState.STREAMING;
	}

	/**
	 * Initializes RIOdroid and RIOadb
	 * @return The state after execution
	 */
	private ConnectionState StartADB() {

		if(!CommandExecutor.isNexusConnected()){
			//			System.out.println("Error: in VisionManager.StartADB(), " +
			//					"nexus is not connected");
			return this.m_connectionState;
		}

		if(m_adbServerCreated){
			if(!this.isAppStarted()){
				System.out.println("[Warning] Vision app not started, starting app");
				Logger.getInstance().logRobotThread("[Warning] Vision app not started, starting app");

				this.VisionInit();
			} else {
				this.m_visionRunning = true;
			}

			if (this.m_visionRunning) {
				System.out.println("[Info] Connected to vision app");
				Logger.getInstance().logRobotThread("Connected to vision app");
				return ConnectionState.STARTING_SUB_PROCESSES;
			} else {
				System.out.println("[Warning] Could not start vision app, retrying");
				Logger.getInstance().logRobotThread("Could not start vision app, retrying");
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
			System.out.println("[Error] in VisionManager.StartADB(), "
					+ "could not connect..");
			e.printStackTrace();
		}

		if(connected){      // Adb server started successfully
			m_adbServerCreated = true;
			System.out.println("[Info] Adb server started");
			Logger.getInstance().logRobotThread("[Info] Adb server started");
		} else {            // Adb server failed to start
			System.out.println("[Error] Failed to start adb server");
			Logger.getInstance().logRobotThread("[Error] Failed to start adb server");
		}
	}

//	/**
//	 * Starts up the vision app
//	 */
//	public void StartVisionApp(){
//		//		if(!m_adbServerCreated){    // No abd server, can't start app
//		//			System.out.println("Warning: on call VisionManager.StartVisionApp(), " +
//		//					"adb server not started, abandoning app startup");
//		////			return;
//		//		}
//
//		if(m_visionRunning){	// This should never happen, but easily can due to outside calling
//			System.out.println("Warning: On call VisionManager.StartVisionApp(), "
//					+ "vision app already running (or function has been called before)");
//		}else{
//			if(m_connectionState.equals(ConnectionState.STARTING_ADB)){
//				int limit = 40;
//				int count = 0;
//				while(!m_connectionState.equals(ConnectionState.IDLE)){
//					try {
//						Thread.sleep(50);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					if(count >= limit){
//						break;
//					}
//					count++;
//				}
//
//			}else if(!m_connectionState.equals(ConnectionState.IDLE)){
//				System.out.println("Error: in VisionManager.StartVisionApp(), "
//						+ "connection not in a state to start app");
//			}
//
//			this.SetState(ConnectionState.START_VISION_APP);
//		}
//	}

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
			log("Could not connect in initialization.");
			e.printStackTrace();
		}

		if(connected) {     // App started successfully
			m_visionRunning = true;
			System.out.println("[Info] Starting Vision Stream");
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
			System.out.println("Error: in VisionManager.run(), "
					+ "thread running on preinit state");
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
