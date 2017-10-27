package com.palyrobotics.frc2017.vision;

import java.util.HashMap;

public class ReceiverSelector {
	
	String fileName;
	int port;
	int updateRate;
	
	public ReceiverSelector(String fileName, int port, int updateRate) {
		this.fileName = fileName;
		this.port = port;
		this.updateRate = updateRate;
	}
	
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
                    receiver_map.put(type, new JSONReceiver(fileName));
                    break;
                case SOCKET:
                    SocketReceiver socketReceiver = new SocketReceiver();
                    receiver_map.put(type, socketReceiver);
                    socketReceiver.start(updateRate, port);
                    break;
            }
        }
    }
}
