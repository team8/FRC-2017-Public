package com.palyrobotics.frc2016.auto;

import com.palyrobotics.frc2016.behavior.RoutineManager;

public class AutoModeExecuter {
    private AutoModeBase m_auto_mode;
    private Thread m_thread = null;
    
    private RoutineManager routineManager;
    
    public AutoModeExecuter(RoutineManager routineManager) {
    	this.routineManager = routineManager;
    }
    
    public void setAutoMode(AutoModeBase new_auto_mode) {
        m_auto_mode = new_auto_mode;
    }

    public void start() {
        if (m_thread == null) {
            m_thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (m_auto_mode != null) {
                        m_auto_mode.run(routineManager);
                    }
                }
            });
            m_thread.start();
        }

    }

    public void stop() {
        if (m_auto_mode != null) {
            m_auto_mode.stop();
        }
        m_thread = null;
    }

}
