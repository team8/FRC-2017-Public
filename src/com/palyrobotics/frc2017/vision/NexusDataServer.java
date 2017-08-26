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
 * 				<li>{@link NexusDataServer#s_instance}: Private instance of this class (Singleton)</li>
 * 				<li>{@link NexusDataServer#m_socketState}: Current state of socket connection (private)</li>
 * 				<li><b>See:</b>{@link SocketState}</li>
 * 			</ul>
 * 		</li>
 * 		<li>Utility variables:
 * 			<ul>
 * 				<li>{@link NexusDataServer#m_secondsAlive}: Private count of seconds the program has run for</li>
 * 				<li>{@link NexusDataServer#m_stateAliveTime}: Private count of seconds the state has run for</li>
 * 				<li>{@link NexusDataServer#m_port}: Port that the ServerSocket listens on (private)</li>
 * 				<li>{@link NexusDataServer#m_running}: Private boolean representing whether the thread is running</li>
 * 				<li>{@link NexusDataServer#m_awaitingOutput}: Private boolean representing whether the thread is waiting to output data</li>
 * 				<li>{@link NexusDataServer#m_server}: Server Socket object that listens for android client and receives data (private)</li>
 * 				<li>{@link NexusDataServer#mOutput}: Stores output from the client (private)</li>
 * 			</ul>
 * 		</li>
 * 	</ul>
 *
 * <h1><b>Accessors and Mutators</b></h1>
 * 	<ul>
 * 		<li>{@link VisionManager#getInstance()}</li>
 * 		<li>{@link NexusDataServer#SetState(SocketState)}</li>
 * 	</ul>
 *
 * <h1><b>External Access Functions</b>
 * 	<br><BLOCKQUOTE>For using as a wrapper for RIOdroid</BLOCKQUOTE></h1>
 * 	<ul>
 * 		<li>{@link NexusDataServer#start(int)}</li>
 * 		<li>{@link NexusDataServer#AwaitClient()}</li>
 * 		<li>{@link NexusDataServer#AwaitOutput()} </li>
 * 	</ul>
 *
 * 	<h1><b>Internal Functions</b>
 * 	 <br><BLOCKQUOTE>Paired with external access functions. These compute the actual function for the external access</BLOCKQUOTE></h1>
 * 	 <ul>
 * 	     <li>{@link NexusDataServer#AcceptConnection()}</li>
 * 	 </ul>
 *
 * @see SocketState
 * @author Alvin
 *
 */
public class NexusDataServer implements Runnable{

	/**
	 * State of connection between the roboRIO and nexus
	 *
	 * <ul>
	 *     <li>{@link SocketState#PREINIT}</li>
	 *     <li>{@link SocketState#IDLE}</li>
	 *     <li>{@link SocketState#RECEIVING}</li>
	 *     <li>{@link SocketState#OUTPUTTING}</li>
	 * </ul>
	 */
    public enum SocketState{
        PREINIT, IDLE, RECEIVING, OUTPUTTING
    }

    // Instance and state variables
    private static NexusDataServer s_instance;
    private SocketState m_socketState = SocketState.PREINIT;

    // Utility variables
    private double m_secondsAlive = 0;
    private double m_stateAliveTime = 0;
    private int m_port;
    private boolean m_running = false;
    private boolean m_awaitingOutput = false;
    private boolean m_awaitingConnection = false;
    private ServerSocket m_server;
    private String mOutput = "";

	/**
	 * Creates a NexusDataServer instance
	 * Cannot be called outside as a Singleton
	 */
	private NexusDataServer(){}

	/**
	 * @return The instance of the BST
	 */
    public static NexusDataServer getInstance(){
        if(s_instance == null){
            s_instance = new NexusDataServer();
        }
        return s_instance;
    }

	/**
	 * Sets the state of socket connection
	 * @param state State to switch to
	 */
	private void SetState(SocketState state){
        m_socketState = state;
    }

	/**
	 * (DEBUG) Logs the Socket state
	 */
	private void logSocketState(){
        System.out.println("Debug: NexusDataServer AndroidServerState - "+m_socketState);
    }

	/**
	 * Starts the NexusDataServer thread
	 * <br>Created server socket opens on given port
	 * @param port Port to start Server on
	 */
	public void start(int port){

        if(!m_socketState.equals(SocketState.PREINIT)){ // This should never happen
            System.out.println("Error: in NexusDataServer.start(), " +
                    "socket is already initialized");
        }

        if(m_running){  // This should never happen
            System.out.println("Error: in NexusDataServer.start(), " +
                    "thread is already running");
        }

        m_port = port;
        try {
            m_server = new ServerSocket(m_port);
            m_server.setReuseAddress(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.SetState(SocketState.IDLE);
        m_running = true;

        System.out.println("Starting Thread: NexusDataServer on port "+port);
        (new Thread(this, "NexusDataServer")).start();
    }

	/**
	 * Tells the Server to begin awaiting a client to connect
	 */
	public void AwaitClient() {
        if(!m_socketState.equals(SocketState.IDLE)){
            System.out.println("Error: in NexusDataServer.AwaitClient(), " +
                    "thread is not in idle state, cannot await for client");
           this.logSocketState();
            return;
        }

        if(m_awaitingOutput){
            System.out.println("Error: in NexusDataServer.AwaitClient(), " +
                    "already awaiting output, cannot await another client");
            return;
        }

        m_awaitingOutput = true;
        this.SetState(SocketState.RECEIVING);

        while(!m_awaitingConnection){
	        try {
		        Thread.sleep(100);
//		        System.out.println("Waiting for Thread to await client");
	        } catch (InterruptedException e) {
		        e.printStackTrace();
	        }
        }
	}

	/**
	 * Waits for the Server to receive data from the client, then
	 * grabs the data
	 * @return The data received from the client
	 */
	public String AwaitOutput() {
        String outp = null;
        if(!m_awaitingOutput){
            System.out.println("Error in NexusDataServer.AwaitOutput(), " +
                    "thread is not awaiting an output");
            return null;
        }

        while(m_awaitingOutput){
            switch(m_socketState){
                case IDLE:
                    System.out.println("Error in NexusDataServer.AwaitOutput(), " +
                            "thread is in idle state, not awaiting an output");
                    return null;

                case OUTPUTTING:
                    m_awaitingOutput = false;
                    outp = this.mOutput;
                    this.SetState(SocketState.RECEIVING);
                    break;

				case RECEIVING:
                    break;
            }
        }

        this.SetState(SocketState.IDLE);
        return outp;
    }

	/**
	 * Accept connection from a client
	 * @return The state after execution
	 */
	private SocketState AcceptConnection(){
		// Builds the String representation of the data
        StringBuilder builder = new StringBuilder();

        try{
        	// Accept client, then form into a readable object
	        m_awaitingConnection = true;
//	        System.out.println("Trying to connect to client");
	        Socket client = m_server.accept();
//	        System.out.println("Connected to client: "+client.getPort());
            m_awaitingConnection = false;
			BufferedReader client_reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

			// Read the data
			String input;
			while((input = client_reader.readLine()) != null){
				builder.append(input);
			}

			client_reader.close();
			client.close();
        } catch (IOException e) {
                e.printStackTrace();
                return SocketState.RECEIVING;
        }

        // Set up for outputting the received data
        this.mOutput = builder.toString();
        return SocketState.OUTPUTTING;
    }

	/**
	 * Updates the thread at {@link Constants#kAndroidConnectionUpdateRate} ms
	 */
	@Override
    public void run() {
        while(m_running){
            SocketState initState = m_socketState;
            switch (m_socketState){

                case PREINIT:   // This should never happen
                    System.out.println("Error: in NexusDataServer.run(), " +
                            "thread running on preinit state");
                    break;

				case RECEIVING:
                    this.SetState(this.AcceptConnection());
                    break;

                case OUTPUTTING:
                    break;

                case IDLE:
                    break;
            }

            // Reset state start time if state changed
            if(!initState.equals(m_socketState)){
                m_stateAliveTime = m_secondsAlive;
            }

            // Handle thread sleeping, sleep for set constant update delay
            try {
                Thread.sleep(Constants.kAndroidDataSocketUpdateRate);
                m_secondsAlive += Constants.kAndroidDataSocketUpdateRate /1000.0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
