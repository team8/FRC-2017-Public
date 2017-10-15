package com.palyrobotics.frc2017.vision;

import com.palyrobotics.frc2017.vision.util.VisionDataUnit;

public class DataThread extends AbstractVisionThread {


	VisionReceiverBase receiver;
	VisionDataUnit data;

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
    	receiver = new JSONReceiver();
	}

	@Override
    protected void update() {
    	data = receiver.extractData();
	}

	@Override
	protected void tearDown() {

	}



}
