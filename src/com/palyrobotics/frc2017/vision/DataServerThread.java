package com.palyrobotics.frc2017.vision;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.ctre.CANTalon;
import com.palyrobotics.frc2017.config.Constants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 */
public class DataServerThread extends AbstractVisionServer {

    protected DataServerThread() {
		super("DataServerThread");
	}

    private static DataServerThread s_instance;

    public static DataServerThread getInstance() {

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
			case OPEN:

				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(m_client.getInputStream()));
					String jsonString = "", line;
					while (!(line = reader.readLine()).isEmpty()) {
						jsonString += line;
					}
					JSONObject json = parseJSON(jsonString);

				} catch (IOException e) {
					e.printStackTrace();
				}
		}
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

	private void extractData(JSONObject json){

		if (json != null) {
			String state = (String) json.get("state");
			if (state != null && !state.isEmpty()) {

				synchronized (m_android_lock) {
					if (state.equals("STREAMING")) {
						// Get image data
						Number data_x = ((Number) json.get("x_displacement"));
						if (data_x != null) {
							this.m_x_dist = data_x.doubleValue();
						}
					}
					m_androidState = state;
				}
			}
		}
	}

	protected void tearDown() {
		
	}
}
