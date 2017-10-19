package com.palyrobotics.frc2017.vision;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

import com.palyrobotics.frc2017.config.Constants;

public class SocketReceiver extends AbstractVisionServer implements VisionReceiverBase{
	ServerSocket mServerSocket;

	public SocketReceiver() {
		super("Socket Reciever");
		// TODO Auto-generated constructor stub
	}

    public void open() {
    	if(!this.m_serverState.equals(ServerState.PRE_INIT)) {
    		System.out.println("Error: in DataServerThread.start(), " +
                    "socket is already initialized");
    	}
    	try {
			mServerSocket = new ServerSocket(Constants.kAndroidDataSocketPort);
			mServerSocket.setReuseAddress(true);
		} catch (IOException e) {
			System.out.println("Could not instantiate server socket");
			e.printStackTrace();
		}
    }

	@Override
	public String extractData() {
		String data = "";
		try {
			String line;
			BufferedReader mReader = new BufferedReader(new InputStreamReader(m_client.getInputStream()));
			while((line = mReader.readLine()) != null) {
				data += line + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return data;
	}
	

	@Override
	protected void tearDown() {
		
	}
}
