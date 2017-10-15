package com.palyrobotics.frc2017.vision;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.spectrum3847.RIOdroid.RIOdroid;

import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.util.logger.Logger;

public class JSONReceiver extends VisionReceiverBase{

    public JSONObject extractData(String raw_data){

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
}
