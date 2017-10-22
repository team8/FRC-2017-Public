package com.palyrobotics.frc2017.vision;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.vision.ReceiverSelector.VisionReceiverType;
import com.palyrobotics.frc2017.vision.util.VisionUtil;

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
		
		try {
			String raw_data = mReceiverSelector.getReciever().extractData();
			JSONObject json = VisionUtil.parseJSON(raw_data);
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	@Override
	protected void tearDown() {

	}

}
