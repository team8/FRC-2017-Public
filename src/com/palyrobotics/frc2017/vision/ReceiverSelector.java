package com.palyrobotics.frc2017.vision;

import java.util.HashMap;

public class ReceiverSelector {

    public enum VisionReceiverType {
        JSON, SOCKET
    }

    private HashMap<VisionReceiverType, VisionReceiverBase> receiver_map = new HashMap<VisionReceiverType, VisionReceiverBase>();

    private VisionReceiverType receiver;

    public VisionReceiverBase getReciever() {
        if(receiver == null){
            this.setReceiver(VisionReceiverType.JSON);
        }
        return receiver_map.get(this.receiver);
    }

    public void setReceiver(VisionReceiverType type){
        this.receiver = type;
        if(!receiver_map.containsKey(type)){
            switch (type){
                case JSON:
                    receiver_map.put(type, new JSONReceiver());
                    break;
                case SOCKET:
                    receiver_map.put(type, new SocketReceiver());
                    break;
            }
        }
    }
}
