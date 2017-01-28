package com.palyrobotics.frc2016.auto;

import com.palyrobotics.frc2016.behavior.Routine;
import com.palyrobotics.frc2016.behavior.RoutineManager;
import com.palyrobotics.frc2016.config.Commands;

public abstract class AutoModeBase {
    protected double updateRate = 1.0 / 50.0;
    protected boolean active = false;

    protected abstract void routine() throws AutoModeEndedException;
    public abstract String toString();
    public abstract void prestart();
    private RoutineManager mRoutineManager;
    private Commands mCommands;

    public void run(Commands commands, RoutineManager routineManager) {
    	this.mRoutineManager = routineManager;
    	this.mCommands = commands;
        active = true;
        try {
            routine();
        } catch (AutoModeEndedException e) {
            System.out.println("Auto mode done, ended early");
            return;
        }
        System.out.println("Auto mode done");
    }

    public void stop() {
        active = false;
    }

    public boolean active() {
        return active;
    }

    public boolean activeWithThrow() throws AutoModeEndedException {
        if (!active()) {
            throw new AutoModeEndedException();
        }
        return active();
    }

    public void runRoutine(Routine routine) {
    	mRoutineManager.addNewRoutine(mCommands, routine);
    }
    
}