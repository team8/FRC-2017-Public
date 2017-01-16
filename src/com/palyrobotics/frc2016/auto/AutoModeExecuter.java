package com.palyrobotics.frc2016.auto;

import com.palyrobotics.frc2016.behavior.RoutineManager;

public class AutoModeExecuter {
    private AutoModeBase mAutoMode;
    private Thread mThread = null;
    
    private RoutineManager routineManager;
    
    public AutoModeExecuter(RoutineManager routineManager) {
    	this.routineManager = routineManager;
    }
    
    public void setAutoMode(AutoModeBase newAutoMode) {
        mAutoMode = newAutoMode;
    }

    public void start() {
        if (mThread == null) {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (mAutoMode != null) {
                        mAutoMode.run(routineManager);
                    }
                }
            });
            mThread.start();
        }

    }

    public void stop() {
        if (mAutoMode != null) {
            mAutoMode.stop();
        }
        mThread = null;
    }

}
