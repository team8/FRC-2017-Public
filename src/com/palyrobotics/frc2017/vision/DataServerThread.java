package com.palyrobotics.frc2017.vision;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.palyrobotics.frc2017.config.Constants;

/**
 * Supplies wrapper methods for using adb to control the Android
 *
 * <h1><b>Fields</b></h1>
 * 	<ul>
 * 		<li>Instance and State variables:
 * 			<ul>
 * 				<li>{@link DataServerThread#s_instance}: Private instance of this class (Singleton)</li>
 * 				<li>{@link DataServerThread#m_socketState}: Current state of socket connection (private)</li>
 * 				<li><b>See:</b>{@link SocketState}</li>
 * 			</ul>
 * 		</li>
 * 		<li>Utility variables:
 * 			<ul>
 * 				<li>{@link DataServerThread#m_secondsAlive}: Private count of seconds the program has run for</li>
 * 				<li>{@link DataServerThread#m_stateAliveTime}: Private count of seconds the state has run for</li>
 * 				<li>{@link DataServerThread#m_port}: Port that the ServerSocket listens on (private)</li>
 * 				<li>{@link DataServerThread#m_running}: Private boolean representing whether the thread is running</li>
 * 				<li>{@link DataServerThread#m_awaitingOutput}: Private boolean representing whether the thread is waiting to output data</li>
 * 				<li>{@link DataServerThread#m_server}: Server Socket object that listens for android client and receives data (private)</li>
 * 				<li>{@link DataServerThread#mOutput}: Stores output from the client (private)</li>
 * 			</ul>
 * 		</li>
 * 	</ul>
 *
 * <h1><b>Accessors and Mutators</b></h1>
 * 	<ul>
 * 		<li>{@link VisionManager#getInstance()}</li>
 * 		<li>{@link DataServerThread#setState(SocketState)}</li>
 * 	</ul>
 *
 * <h1><b>External Access Functions</b>
 * 	<br><BLOCKQUOTE>For using as a wrapper for RIOdroid</BLOCKQUOTE></h1>
 * 	<ul>
 * 		<li>{@link DataServerThread#start(int)}</li>
 * 		<li>{@link DataServerThread#awaitClient()}</li>
 * 		<li>{@link DataServerThread#AwaitOutput()} </li>
 * 	</ul>
 *
 * 	<h1><b>Internal Functions</b>
 * 	 <br><BLOCKQUOTE>Paired with external access functions. These compute the actual function for the external access</BLOCKQUOTE></h1>
 * 	 <ul>
 * 	     <li>{@link DataServerThread#AcceptConnection()}</li>
 * 	 </ul>
 *
 * @see SocketState
 * @author Alvin
 *
 */
public class DataServerThread extends AbstractVisionServer {

    protected DataServerThread() {
		super("DataServerThread");
	}

	// Instance and state variables
    private static DataServerThread s_instance;

    // Utility variables
    private boolean m_awaitingOutput = false;
    private boolean m_awaitingConnection = false;
    private String mOutput = "";

	/**
	 * Creates a AndroidDataServer instance
	 * Cannot be called outside as a Singleton
	 */
//	private AbstractAndroidDataServer(){
//		super("AndroidDataServer");
//	}

	/**
	 * @return The instance of the BST
	 */
    public static DataServerThread getInstance(){
        if (s_instance == null)
            s_instance = new DataServerThread();
        
        return s_instance;
    }
    
    @Override
    protected void init() {
    	
    	super.init();
    }

	@Override
    protected void update() {

		super.update();
		
		switch (m_serverState) {
			case OPEN: {
				// Do ur stuff her json
				break;
			}
		}
    }

	@Override
	protected void tearDown() {
		
	}
}
