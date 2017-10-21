package com.palyrobotics.frc2017.vision;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

import com.palyrobotics.frc2017.config.Constants;

public class SocketReceiver extends AbstractVisionServer implements VisionReceiverBase{
	
	ServerSocket mServerSocket;

	public SocketReceiver() {
		super("Socket Reciever");
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

	@Override
	public byte[] extractDataBytes() {
		
		try {
			DataInputStream dis = new DataInputStream(m_client.getInputStream());
			int len = dis.readInt();
			byte[] data = new byte[len];
			if(len > 0) {
				dis.readFully(data);
			}
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
