package com.palyrobotics.frc2018.vision.networking;

import org.json.simple.JSONObject;

import com.palyrobotics.frc2018.config.Constants;
import com.palyrobotics.frc2018.vision.VisionData;
import com.palyrobotics.frc2018.vision.util.VisionUtil;

import java.io.IOException;

public class VisionDataReceiver extends ReceiverBase {

	public  VisionDataReceiver() {
		super("Data Thread", Constants.kVisionDataFileName,Constants.kVisionDataPort, Constants.kAndroidDataSocketUpdateRate, false);
	}

	private static VisionDataReceiver s_instance;

	public static VisionDataReceiver getInstance() {

		if (s_instance == null)
			s_instance = new VisionDataReceiver();

		return s_instance;
	}

	@Override
	protected void update() {
		
		try {
			String raw_data = mReceiverSelector.getReceiver().extractData();
			JSONObject json = VisionUtil.parseJSON(raw_data);

			if (json != null) {
				String state = (String)json.get("state");
				if (!(state == null) && !state.equals("")) {	// Handle based on state
					switch (state) {

						case "STREAMING": {

							// Get image data
							final double
									data_x = Double.parseDouble((String)json.get("x_displacement")),
									data_z = Double.parseDouble((String)json.get("z_displacement"));

							VisionData.setXDataValue(data_x);
							VisionData.setZDataValue(data_z);

							break;
						}

						case "STOPPED":
						case "PAUSED": {

							VisionData.getXData().setToDefault();
							VisionData.getZData().setToDefault();

							break;
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
