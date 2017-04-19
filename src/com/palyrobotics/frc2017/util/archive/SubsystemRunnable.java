package com.palyrobotics.frc2017.util.archive;

import com.palyrobotics.frc2017.robot.team254.lib.util.CrashTracker;

/**
 * Runnable class with reports all uncaught throws to CrashTracker and restarts robot code
 */
public abstract class SubsystemRunnable implements Runnable {
    @Override
    public final void run() {
        try {
            runCrashTracked();
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            System.out.println("Subsystem crash, restarting robot code");
            System.exit(1);
            throw t;
        }
    }

    public abstract void runCrashTracked();
}
