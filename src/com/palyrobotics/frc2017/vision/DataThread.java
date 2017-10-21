package com.palyrobotics.frc2017.vision;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.vision.ReceiverSelector.VisionReceiverType;

public class DataThread extends DataReceiverBase {

	protected DataThread() {
		super("Data Thread", Constants.kVisionDataFileName,Constants.kAndroidDataSocketPort, Constants.kAndroidDataSocketUpdateRate);
	}

	private static DataThread s_instance;

	public static DataThread getInstance() {

		if (s_instance == null)
			s_instance = new DataThread();

		return s_instance;
	}

	@Override
	protected void update() {
		
		String raw_data = mReceiverSelector.getReciever().extractData();
		JSONObject json = parseJSON(raw_data);
		
		if (json != null) {
			String state = (String)json.get("state");
			if (!(state == null) && !state.equals("")) {	// Handle based on state
				if (state.equals("STREAMING")) {
					// Get image data
					Number data_x = ((Number) json.get("x_displacement"));
					Number data_z = ((Number) json.get("z_displacement"));
					
					if (data_x != null) {
						VisionData.setXData(data_x.doubleValue());
					}
					if(data_z != null) {
						VisionData.setZData(data_z.doubleValue());
					}
				}
			}
		}
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

	@Override
	protected void tearDown() {

	}

}
