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
 * 				<li>{@link MJPEGServerSocket#m_MJPEGServerState}: Current state of socket connection (private)</li>
 * 				<li><b>See:</b>{@link MJPEGServerState}</li>
 * 			</ul>
 * 		</li>
 * 		<li>Utility variables:
 * 			<ul>
 * 				<li>{@link MJPEGServerSocket#m_secondsAlive}: Private count of seconds the program has run for</li>
 * 				<li>{@link MJPEGServerSocket#m_stateAliveTime}: Private count of seconds the state has run for</li>
 * 				<li>{@link MJPEGServerSocket#m_port}: Port that the ServerSocket listens on (private)</li>
 * 				<li>{@link MJPEGServerSocket#m_running}: Private boolean representing whether the thread is running</li>
 * 				<li>{@link MJPEGServerSocket#m_server}: Server Socket object that listens for android client and receives data (private)</li>
 * 			</ul>
 * 		</li>
 * 	</ul>
 *
 * <h1><b>Accessors and Mutators</b></h1>
 * 	<ul>
 * 		<li>{@link AndroidConnectionHelper#getInstance()}</li>
 * 		<li>{@link MJPEGServerSocket#SetState(MJPEGServerState)}</li>
 * 	</ul>
 *
 * <h1><b>External Access Functions</b>
 * 	<br><BLOCKQUOTE>For using as a wrapper for RIOdroid</BLOCKQUOTE></h1>
 * 	<ul>
 * 		<li>{@link MJPEGServerSocket#start()}</li>
 * 	</ul>
 *
 * 	<h1><b>Internal Functions</b>
 * 	 <br><BLOCKQUOTE>Paired with external access functions. These compute the actual function for the external access</BLOCKQUOTE></h1>
 * 	 <ul>
 * 	     <li>{@link MJPEGServerSocket#AcceptConnection()}</li>
 * 	 </ul>
 *
 * @see MJPEGServerState
 * @author Alvin
 *
 */
public class MJPEGServerSocket implements Runnable{

	/**
	 * State of connection between the roboRIO and nexus
	 *
	 * <ul>
	 *     <li>{@link MJPEGServerState#PREINIT}</li>
	 *     <li>{@link MJPEGServerState#RECEIVING}</li>
	 *     <li>{@link MJPEGServerState#CONNECTING}</li>
	 * </ul>
	 */
	public enum MJPEGServerState {
		PREINIT, CONNECTING, RECEIVING
	}

	public enum SocketConnectionState {
		ALIVE, CLOSED
	}

	// Instance and state variables
	private MJPEGServerState m_MJPEGServerState = MJPEGServerState.PREINIT;
	private SocketConnectionState m_socketConnectionState = SocketConnectionState.CLOSED;

	// Utility variables
	private double m_secondsAlive = 0;
	private double m_stateAliveTime = 0;
	private int m_port;
	private boolean m_running = false;
	private byte[] m_defaultImage;
	private ServerSocket m_server;
	private Socket m_client;
	private String m_boundary = "team8robotics";

	/**
	 * Creates a DataServerThread instance
	 * Cannot be called outside as a Singleton
	 */
	public MJPEGServerSocket(int port, String path){
		m_port = port;

//		try {
//			BufferedImage img = ImageIO.read(new File(path));
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			ImageIO.write(img, "JPEG", baos);
//			baos.close();
//
//			m_defaultImage = baos.toByteArray();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * Sets the state of socket connection
	 * @param state State to switch to
	 */
	private void SetState(MJPEGServerState state){
		m_MJPEGServerState = state;
	}

	private void SetConnectionState(SocketConnectionState state){
		m_socketConnectionState = state;
	}

	/**
	 * (DEBUG) Logs the Socket state
	 */
	private void logSocketState(){
		System.out.println("Debug: DataServerThread AndroidServerState - "+ m_MJPEGServerState);
	}

	/**
	 * Starts the DataServerThread thread
	 * <br>Created server socket opens on given port
	 */
	public void start(){

		if(!m_MJPEGServerState.equals(MJPEGServerState.PREINIT)){ // This should never happen
			System.out.println("Error: in DataServerThread.start(), " +
					"socket is already initialized");
		}

		if(m_running){  // This should never happen
			System.out.println("Error: in DataServerThread.start(), " +
					"thread is already running");
		}

		try {
			m_server = new ServerSocket(m_port);
			m_server.setReuseAddress(true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.SetState(MJPEGServerState.CONNECTING);
		m_running = true;

		System.out.println("Starting Thread: DataServerThread on port "+m_port);
		(new Thread(this, "DataServerThread")).start();
	}

	/**
	 * Respond to a request from a client.
	 *
	 * @throws IOException
	 */
	public void handle(byte[] data) throws IOException {
		m_client = m_server.accept();
//		System.out.println("Connected to image socket on port: "+m_client.getPort());

		if(m_client == null){
//			System.out.println("Warning: in MJPEGServerSocket.handle(), " +
//					"socket not connected");
			return;
		}

		BufferedReader reader = null;
		PrintStream output = null;
		try {
			String route = null;

			// Read HTTP headers and parse out the route.
			reader = new BufferedReader(new InputStreamReader(m_client.getInputStream()));
			String line;
			while (!(line = reader.readLine()).isEmpty()) {
//				System.out.println(line);
				if (line.startsWith("GET /")) {
					int start = line.indexOf('/') + 1;
					int end = line.indexOf(' ', start);
					route = line.substring(start, end);
					break;
				}
			}

			// Output stream that we send the response to
			output = new PrintStream(m_client.getOutputStream());

			// Prepare the content to send.
			if (null == route) {
				writeServerError(output);
				return;
			}
			if (null == data) {
				writeServerError(output);
				return;
			}

//			System.out.println("Solid: "+data.length);

			// Send out the content.
			output.println("HTTP/1.1 200 OK");
			output.println("Cache-Control: no-cache");
//			output.println("Content-Encoding: gzip");
			output.println("Content-Type: image/jpeg"   );
			output.println("Content-Length: " + data.length);
			output.println();
			output.write(data);
			output.flush();

//			output.println("<p>Hello</p>");
//			output.println("<p>"+data.length+"</p>");
//			output.flush();
			
		} catch (SocketException e) {
//			System.out.println("Error: in MJPEGServerSocket.handle(), " +
//					"socket connection broken");
//			m_client.close();
//			this.SetConnectionState(SocketConnectionState.CLOSED);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			if (null != output) {
				output.close();
			}
			if (null != reader) {
				reader.close();
			}

			m_client.close();
		}
	}

	/**
	 * Writes a server error response (HTTP/1.0 500) to the given output stream.
	 *
	 * @param output The output stream.
	 */
	private void writeServerError(PrintStream output) {
		output.println("HTTP/1.0 500 Internal Server Error");
		output.flush();
	}

	/**
	 * Detects the MIME type from the {@code fileName}.
	 *
	 * @param fileName The name of the file.
	 * @return A MIME type.
	 */
	private String detectMimeType(String fileName) {
		if ((fileName).isEmpty()) {
			return null;
		} else if (fileName.endsWith(".html")) {
			return "text/html";
		} else if (fileName.endsWith(".js")) {
			return "application/javascript";
		} else if (fileName.endsWith(".css")) {
			return "text/css";
		} else {
			return "application/octet-stream";
		}
	}

	public void WriteImage(byte[] data) throws IOException{
		PrintStream writer = null;
		try{
			writer = new PrintStream(m_client.getOutputStream());

			writer.println();
			writer.println("--" + m_boundary);
			writer.println("Content-Type: image/jpeg");
			writer.println("Content-Length: " + data.length);
			writer.println();
			writer.write(data);
			writer.println();
			writer.println();

			writer.flush();
		} catch (SocketException e) {
//			System.out.println("Error: in MJPEGServerSocket.WriteImage(), " +
//					"socket connection broken");
//			m_client.close();
//			this.SetConnectionState(SocketConnectionState.CLOSED);
			e.printStackTrace();
		} catch (NullPointerException e) {
//			e.printStackTrace();
		} finally {
			if (null != writer) {
				writer.close();
			}
		}
	}

	private MJPEGServerState CheckConnection() {
		if(m_socketConnectionState.equals(SocketConnectionState.CLOSED)){
			return MJPEGServerState.CONNECTING;
		}
		return m_MJPEGServerState;
	}

	/**
	 * Accept connection from a client
	 * @return The state after execution
	 */
	private MJPEGServerState AcceptConnection(){
		if(m_socketConnectionState.equals(SocketConnectionState.ALIVE)){
			System.out.println("Error in VisionServerThread.AcceptConnection(), " +
					"Socket connection is already Alive");
			return MJPEGServerState.RECEIVING;
		}

		try {
			// Accept client
//			System.out.println("Trying to connect to client");
			m_client = m_server.accept();
			System.out.println("Connected to client: " + m_client.getPort());

			PrintStream writer = new PrintStream(m_client.getOutputStream());
			writer.println("HTTP/1.0 200 OK");
			writer.println("Server: Team8NexusVision");
			writer.println("Connection: close");
			writer.println("Cache-Control: no cache, private");
			writer.println("Pragma: no-cache");
			writer.println("Content-Type: multipart/x-mixed-replace;boundary=--" + m_boundary);

			this.SetConnectionState(SocketConnectionState.ALIVE);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return MJPEGServerState.RECEIVING;
	}

	/**
	 * Updates the thread at {@link Constants#kAndroidConnectionUpdateRate} ms
	 */
	@Override
	public void run() {
		while(m_running){
			MJPEGServerState initState = m_MJPEGServerState;
			switch (m_MJPEGServerState){

				case PREINIT:   // This should never happen
					System.out.println("Error: in DataServerThread.run(), " +
							"thread running on preinit state");
					break;

				case RECEIVING:
//					this.SetState(this.CheckConnection());
					break;

				case CONNECTING:
//					this.SetState(this.AcceptConnection());
					break;
			}

			// Reset state start time if state changed
			if(!initState.equals(m_MJPEGServerState)){
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
