package com.palyrobotics.frc2017.vision;

import com.palyrobotics.frc2017.vision.util.DataExistsCallback;
import com.palyrobotics.frc2017.vision.util.VisionDataUnit;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public interface VisionReceiverBase {

    public class JSONExistsCallback extends DataExistsCallback<JSONObject> {

        public boolean doesExist(JSONObject data) {
            return data.isEmpty();
        }
    }

    public class SocketExistsCallback extends DataExistsCallback<Socket> {

        public boolean doesExist(Socket data) {
            boolean exists = true;

            try {
                if(data.getOutputStream().equals(null)) {
                    exists = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return exists;
        }
    }

    public abstract String extractData();

}
