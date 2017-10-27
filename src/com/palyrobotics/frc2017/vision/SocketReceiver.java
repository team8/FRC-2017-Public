package com.palyrobotics.frc2017.vision;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class SocketReceiver extends AbstractVisionServer implements VisionReceiverBase{

	public SocketReceiver() {

		super("Socket Receiver");
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

		if (m_client.isConnected()) {

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
		}
		return null;
	}
}
