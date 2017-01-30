package com.palyrobotics.frc2017.auto;

import com.palyrobotics.frc2017.behavior.RoutineManager;
import com.palyrobotics.frc2017.config.Commands;

public class AutoModeExecuter {
    private AutoModeBase mAutoMode;
    private Thread mThread = null;
    
    private RoutineManager mRoutineManager;
    private Commands mCommands;
    
    public AutoModeExecuter(Commands initialCommands, RoutineManager routineManager) {
    	this.mRoutineManager = routineManager;

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
                        mAutoMode.run(mCommands, mRoutineManager);
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
