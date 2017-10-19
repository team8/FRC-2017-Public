package com.palyrobotics.frc2017.vision;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.palyrobotics.frc2017.vision.ReceiverSelector.VisionReceiverType;
import com.palyrobotics.frc2017.vision.util.VisionDataUnit;

public class DataThread extends AbstractVisionThread {


	ReceiverSelector mReceiverSelector;
	ArrayList<VisionDataUnit> mData;

	protected DataThread() {
		super("DataServerThread");
	}

	private static DataThread s_instance;


	public static DataThread getInstance() {

		if (s_instance == null)
			s_instance = new DataThread();

		return s_instance;
	}


	@Override
	protected void init(){
		mReceiverSelector = new ReceiverSelector();
		mReceiverSelector.setReceiver(VisionReceiverType.JSON);
	}

	@Override
	protected void update() {
		String raw_data = mReceiverSelector.getReciever().extractData();
		JSONObject data = parseJSON(raw_data);

	}

	/**
	 * Computes parsing of streamed data (for now just prints to console)
	 * @param raw_data Raw JSON formatted data (String)
	 */
	private JSONObject parseJSON(String raw_data){
		if(raw_data == null  || raw_data.equals("") || raw_data.equals("error: no devices/emulators found")){
			return null;
		}

		// Create JSONObject from the raw String data
		JSONObject json = null;

		try {
			JSONParser parser = new JSONParser();
			json = (JSONObject) parser.parse(raw_data);
		} catch (ParseException e) {
			// This is spammy
			//			e.printStackTrace();
		}

		return json;
	}

	private void extractData(JSONObject json){
		// Compute based on app state (given in the data)
		if(json != null){
			String state = (String) json.get("state");
			if(!(state == null) && !state.equals("")){	// Handle based on state
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

	@Override
	protected void tearDown() {

	}



}
