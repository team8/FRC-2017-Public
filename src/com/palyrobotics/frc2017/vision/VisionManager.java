package com.palyrobotics.frc2017.vision;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import org.json.simple.parser.ParseException;
import org.spectrum3847.RIOdroid.RIOdroid;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.util.logger.Logger;
import com.palyrobotics.frc2017.vision.ReceiverSelector.VisionReceiverType;
import com.palyrobotics.frc2017.vision.util.VisionUtil;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Supplies wrapper methods for using adb to control the Android
 *
 * <h1><b>Fields</b></h1>
 * 	<ul>
 * 		<li>Instance and State variables:
 * 			<ul>
 * 				<li>{@link VisionManager#s_instance}: Private instance of this class (Singleton)</li>
 * 				<li>{@link VisionManager#m_connectionState}: Current state of connection (private)</li>
 * 				<li>{@link VisionManager#m_streamState}: Current state of streaming</li>
 * 				<li><b>See:</b>{@link ConnectionState}</li>
 * 			</ul>
 * 		</li>
 * 		<li>Utility variables:
 * 			<ul>
 * 				<li>{@link VisionManager#m_secondsAlive}: Private count of seconds the program has run for</li>
 * 				<li>{@link VisionManager#m_stateAliveTime}: Private count of seconds the state has run for</li>
 * 				<li>{@link VisionManager#m_adbServerCreated}: Private boolean representing existence an adb server</li>
 * 				<li>{@link VisionManager#m_visionRunning}: Private boolean representing whether vision program is currently running</li>
 * 				<li>{@link VisionManager#m_running}: Private boolean representing whether the thread is running</li>
 * 				<li>{@link VisionManager#mTesting}: Private boolean representing whether program is testing on a pc with
 * 																adb installed and included in the path	</li>
 * 			</ul>
 * 		</li>
 * 	</ul>
 *
 * <h1><b>Accessors and Mutators</b></h1>
 * 	<ul>
 * 		<li>{@link VisionManager#getInstance()}</li>
 * 		<li>{@link VisionManager#SetState(ConnectionState)}</li>
 * 		<li>{@link VisionManager#SetStreamState(StreamState)}</li>
 * 	</ul>
 *
 * <h1><b>External Access Functions</b>
 * 	<br><BLOCKQUOTE>For using as a wrapper for RIOdroid</BLOCKQUOTE></h1>
 * 	<ul>
 * 		<li>{@link VisionManager#start()}</li>
 * 		<li>{@link VisionManager#StartVisionApp()}</li>
 * 	</ul>
 *
 * 	<h1><b>Internal Functions</b>
 * 	 <br><BLOCKQUOTE>Paired with external access functions. These compute the actual function for the external access</BLOCKQUOTE></h1>
 * 	 <ul>
 * 	     <li>{@link VisionManager#InitializeServer()}</li>
 * 	     <li>{@link VisionManager#VisionInit()}</li>
 * 	     <li>{@link VisionManager#StreamVision()}</li>
 * 	 </ul>
 *
 * @see ConnectionState
 * @see StreamState
 * @author Alvin
 *
 */
public class VisionManager extends AbstractVisionThread {

	/**
	 * State of connection between the roboRIO and nexus
	 *
	 * <ul>
	 *     <li>{@link ConnectionState#PRE_INIT}</li>
	 *     <li>{@link ConnectionState#STARTING_SERVER}</li>
	 *     <li>{@link ConnectionState#IDLE}</li>
	 *     <li>{@link ConnectionState#START_VISION_APP}</li>
	 * </ul>
	 */
	public enum ConnectionState{
		PRE_INIT, STARTING_SERVER, IDLE, START_VISION_APP, STREAMING;
	}


	// Instance and state variables
	private static VisionManager s_instance;
	private ConnectionState m_connectionState = ConnectionState.PRE_INIT;

	// Utility variables
	private DataReceiverBase mReceiverBaseVideo;
	private DataReceiverBase mReceiverBaseData;
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
		this.start(updateRate);
		CommandExecutor.setTesting(isTesting);
	}
	/**
	 * Starts the VisionManager thread
	 * <br>(accounts for running program for testing)
	 * @param isTesting
	 */
	@Override
	public void init() {
		mReceiverBaseData.start(Constants.kAndroidDataSocketUpdateRate, VisionReceiverType.JSON);
		mReceiverBaseVideo.start(Constants.kAndroidVisionSocketUpdateRate, VisionReceiverType.SOCKET);
		HTTPVideoServer.getInstance().start(Constants.kMJPEGVisionSocketUpdateRate, Constants.kMJPEGServerSocketPort);
		
		if(m_connectionState != ConnectionState.PRE_INIT) {    // This should never happen
			System.out.println("Error: in VisionManager.start(), "
					+ "connection is already initialized");
		}

		// Initialize Thread Variables
		this.SetState(ConnectionState.STARTING_SERVER);
	}

	/**
	 * Initializes RIOdroid and RIOadb
	 * @return The state after execution
	 */
	private ConnectionState InitializeServer() {

		if(!this.isNexusConnected()){
			//			System.out.println("Error: in VisionManager.InitializeServer(), " +
			//					"nexus is not connected");
			return this.m_connectionState;
		}

		if(m_adbServerCreated){
			if(!this.isAppStarted()){
				System.out.println("Vision app not started, starting app");
				Logger.getInstance().logRobotThread("Vision app not started, starting app");

				this.VisionInit();
			} else {
				this.m_visionRunning = true;
			}

			if (this.m_visionRunning) {
				System.out.println("Connected to vision app");
				Logger.getInstance().logRobotThread("Connected to vision app");
				return ConnectionState.STREAMING;
			} else {
				System.out.println("Could not start vision app, retrying");
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
			CommandExecutor.addServerInit();
			connected = true;
		} catch (Exception e) {
			System.out.println("Error: in VisionManager.InitializeServer(), "
					+ "could not connect.\n" + e.getStackTrace());
		}

		if(connected){      // Adb server started successfully
			m_adbServerCreated = true;
			System.out.println("Adb server started");
			Logger.getInstance().logRobotThread("Adb server started");
		} else {            // Adb server failed to start
			System.out.println("Failed to start adb server");
			Logger.getInstance().logRobotThread("Failed to start adb server");
		}
	}

	/**
	 * Starts up the vision app
	 */
	public void StartVisionApp(){
		//		if(!m_adbServerCreated){    // No abd server, can't start app
		//			System.out.println("Warning: on call VisionManager.StartVisionApp(), " +
		//					"adb server not started, abandoning app startup");
		////			return;
		//		}

		if(m_visionRunning){	// This should never happen, but easily can due to outside calling
			System.out.println("Warning: On call VisionManager.StartVisionApp(), "
					+ "vision app already running (or function has been called before)");
		}else{
			if(m_connectionState.equals(ConnectionState.STARTING_SERVER)){
				int limit = 40;
				int count = 0;
				while(!m_connectionState.equals(ConnectionState.IDLE)){
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(count >= limit){
						break;
					}
					count++;
				}

			}else if(!m_connectionState.equals(ConnectionState.IDLE)){
				System.out.println("Error: in VisionManager.StartVisionApp(), "
						+ "connection not in a state to start app");
			}

			this.SetState(ConnectionState.START_VISION_APP);
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
			log("Could not connect in initialization.");
			e.printStackTrace();
		}

		if(connected) {     // App started successfully
			m_visionRunning = true;
			System.out.println("Starting Vision Stream");
			return ConnectionState.STREAMING;
		} else {            // Failed to start app
			return m_connectionState;
		}
	}

	public void setFlash(boolean isFlashOn) {

		String out = CommandExecutor.toggleFlash();
	}

	public boolean isNexusConnected(){
		boolean hasDevice = false;
		String[] outp = RuntimeExecutor.getInstance().exec("adb devices").split("\\n");

		for(int i=1; i<outp.length && !hasDevice; i++){
			hasDevice = outp[i].contains("device");
		}
		return hasDevice;
	}

	public boolean isAppStarted(){
		String data;
		try {
			data = mReceiverBaseData.mReceiverSelector.getReciever().extractData();
			JSONObject json = VisionUtil.parseJSON(data);

			if (json != null) {
				String state = (String) json.get("state");
				if (state != null && !state.equals("")) {
					if(state.equals("STREAMING")){
						return true;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
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

		case STARTING_SERVER:	// Triggered by start(), should be called externally
			this.SetState(this.InitializeServer());
			break;

		case START_VISION_APP:	// Triggered by StartVisionApp(), should be called externally
			this.SetState(this.VisionInit());
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
