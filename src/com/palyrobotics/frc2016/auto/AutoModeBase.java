package com.palyrobotics.frc2016.auto;

import com.palyrobotics.frc2016.behavior.Routine;
import com.palyrobotics.frc2016.behavior.RoutineManager;

public abstract class AutoModeBase {
    protected double m_update_rate = 1.0 / 50.0;
    protected boolean m_active = false;

    protected abstract void routine() throws AutoModeEndedException;
    public abstract String toString();
    public abstract void prestart();
    private RoutineManager routineManager;

    public void run(RoutineManager routineManager) {
    	this.routineManager = routineManager;
        m_active = true;
        try {
            routine();
        } catch (AutoModeEndedException e) {
            System.out.println("Auto mode done, ended early");
            return;
        }
        System.out.println("Auto mode done");
    }

    public void stop() {
        m_active = false;
    }

    public boolean isActive() {
        return m_active;
    }

    public boolean isActiveWithThrow() throws AutoModeEndedException {
        if (!isActive()) {
            throw new AutoModeEndedException();
        }
        return isActive();
    }

    public void runRoutine(Routine routine) {
    	routineManager.addNewRoutine(routine);
    }
    
}