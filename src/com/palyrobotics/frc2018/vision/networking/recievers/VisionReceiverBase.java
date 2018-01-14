package com.palyrobotics.frc2018.vision.networking.recievers;

import com.palyrobotics.frc2018.vision.util.synchronization.DataExistsCallback;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.Socket;

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

    public abstract String extractData() throws IOException;
    
    public abstract byte[] extractDataBytes() throws IOException;
    
}
