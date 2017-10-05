package com.palyrobotics.frc2017.vision;

import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.util.logger.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.spectrum3847.RIOdroid.RIOdroid;


public class DataServerThread extends AbstractVisionThread {

	public enum ConnectionState {
		PRE_INIT, STARTING_SERVER, IDLE, START_VISION_APP, STREAMING
	}

    protected DataServerThread() {
		super("DataServerThread");
	}

    private static DataServerThread s_instance;

    private double m_x_dist;

    private ConnectionState m_connectionState;

    public static DataServerThread getInstance() {

        if (s_instance == null)
            s_instance = new DataServerThread();
        
        return s_instance;
    }

    private ConnectionState setConnectionState(ConnectionState newState) {

    	m_connectionState = newState;
	}
    
    @Override
    protected void init(){

    	setConnectionState(ConnectionState.STARTING_SERVER);
    }

	@Override
    protected void update() {

		switch (m_connectionState){

			case PRE_INIT:
				// Error here
				break;

			case STARTING_SERVER:	// Triggered by start(), should be called externally
				this.SetState(this.InitializeServer());
				break;

			case START_VISION_APP:	// Triggered by StartVisionApp(), should be called externally
				this.SetState(this.VisionInit());
				break;

			case STREAMING:
				this.SetState(this.StreamVision());
				break;

			case IDLE:
				break;
		}
	}

	private DataServerThread.ConnectionState InitializeServer() {

		if(!this.isNexusConnected()){
//			System.out.println("Error: in JSONDataReceiver.InitializeServer(), " +
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
				return JSONDataReceiver.ConnectionState.STREAMING;
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
			// Initializes RIOdroid usb and RIOadb adb daemon
			if (!this.mTesting) {
				RIOdroid.init();

				// Forward the port and start the server socket for vision
				RIOdroid.executeCommand("adb reverse tcp:" +
						Constants.kAndroidVisionSocketPort + " tcp:" +
						Constants.kAndroidVisionSocketPort);
				System.out.println("Starting VideoManager");
				//VideoManager.getInstance().start();
			} else {
				RuntimeExecutor.getInstance().init();

				// Forward the port and start the server socket for vision
				RuntimeExecutor.getInstance().exec("adb reverse tcp:" +
						Constants.kAndroidVisionSocketPort + " tcp:" +
						Constants.kAndroidVisionSocketPort);
				System.out.println("Starting VideoManager");
				//VideoManager.getInstance().start(mTesting);
			}

			connected = true;
		} catch (Exception e) {
			System.out.println("Error: in JSONDataReceiver.InitializeServer(), "
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

//		// Let it retry connection for 10 seconds, then give in
//		if (m_secondsAlive - m_stateAliveTime > 10 && !connected) {
//			System.out.println("Error: in JSONDataReceiver.InitializeServer(), "
//					+ "connection timed out");
//		}

	}

	/**
	 * Sends command to boot up the vision app
	 * @return The state after execution
	 */
	private DataServerThread.ConnectionState VisionInit(){
		boolean connected = false;
		try {
			// Starts app through adb shell, and stores the returned console message (for debugging)
			String outp = null;
			if(!this.mTesting) {
				outp = RIOdroid.executeCommand(
						"adb shell am start -n " + Constants.kPackageName + "/" +
								Constants.kPackageName + "." + Constants.kActivityName);
			}else{
				outp = RuntimeExecutor.getInstance().exec(
						"adb shell am start -n " + Constants.kPackageName + "/" +
								Constants.kPackageName + "." + Constants.kActivityName);
			}

			connected = true;
		}catch (Exception e) {
			System.out.println("Error: in JSONDataReceiver.VisionInit(), "
					+ "could not connect.\n" + e.getStackTrace());
		}

		if(connected) {     // App started successfully
			m_visionRunning = true;
			System.out.println("Starting Vision Stream");
			return JSONDataReceiver.ConnectionState.STREAMING;
		} else {            // Failed to start app
			return m_connectionState;
		}
	}

	/**
	 * Streams in the vision data
	 * @return The state after execution
	 */
	private DataServerThread.ConnectionState StreamVision(){
		if(!m_visionRunning){	// This should never happen
			System.out.println("Error: in JSONDataReceiver.StreamVision(), "
					+ "vision program i not running (or has not been initialized inside this program)");
		}

		switch (m_streamState){
			case IDLE:
				System.out.println("Error: in JSONDataReceiver.StreamVision(), "
						+ "streaming in IDLE state, nothing streaming");
				break;
			case JSON:
				this.extractData(this.StreamJSON());
				break;
		}

		return m_connectionState;
	}

	/**
	 * Streams vision data via pulling a JSON file with
	 * data written to it
	 */
	private JSONObject StreamJSON(){
		String raw_data;

		// Read the JSON file which stores the vision data
		if(!this.mTesting){
			raw_data = RIOdroid.executeCommand("adb shell run-as "+Constants.kPackageName+" cat /data/data/"+ Constants.kPackageName
					+ "/files/data.json");
		}else{
			raw_data = RuntimeExecutor.getInstance().exec("adb shell run-as "+Constants.kPackageName+" cat /data/data/"+ Constants.kPackageName
					+ "/files/data.json");
		}

		return parseJSON(raw_data);
	}

	private JSONObject parseJSON(String raw_data){

		if(raw_data == null  || raw_data.isEmpty() || raw_data.equals("error: no devices/emulators found"))
			return null;

		try {
			JSONParser parser = new JSONParser();
			return (JSONObject) parser.parse(raw_data);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}


	private void SetState(ConnectionState state){
		m_connectionState = state;
	}

	private JSONDataReceiver.ConnectionState InitializeServer() {

		if(!this.isNexusConnected()){
//			System.out.println("Error: in JSONDataReceiver.InitializeServer(), " +
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
				return JSONDataReceiver.ConnectionState.STREAMING;
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

	public boolean isNexusConnected(){
		boolean hasDevice = false;
		String[] outp = RuntimeExecutor.getInstance().exec("adb devices").split("\\n");

		for(int i=1; i<outp.length && !hasDevice; i++){
			hasDevice = outp[i].contains("device");
		}
		return hasDevice;
	}

	public boolean isAppStarted(){
		JSONObject json = this.StreamJSON();

		if (json != null) {
			String state = (String) json.get("state");
			if (state != null && !state.equals("")) {
				if(state.equals("STREAMING")){
					return true;
				}
			}
		}

		return false;
	}

	private void extractData(JSONObject json){

		if (json != null) {
			String state = (String) json.get("state");
			if (state != null && !state.isEmpty()) {
				if (state.equals("STREAMING")) {
					// Get image data
					Number data_x = ((Number) json.get("x_displacement"));
					if (data_x != null) {
						this.m_x_dist = data_x.doubleValue();
					}
				}
			}
		}
	}

	protected void tearDown() {
		
	}
}
