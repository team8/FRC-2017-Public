package com.palyrobotics.frc2017.vision;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.vision.util.VisionDataUnit;

public class SocketReceiver extends AbstractVisionServer implements VisionReceiverBase{
	ServerSocket mServerSocket;

	protected SocketReceiver(String k_threadName) {
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
		return null;
	}

	@Override
	protected void tearDown() {
		
	}
}
