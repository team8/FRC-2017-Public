package com.palyrobotics.frc2016.behavior;

import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.subsystems.*;
import com.palyrobotics.frc2016.util.Subsystem;

import java.util.ArrayList;

/**
 * Abstract superclass for a routine, which specifies an autonomous series of actions <br />
 * Each routine takes in Commands and returns modified Setpoints
 * Requires the specific subsystems
 * @author Nihar; Team 254
 *
 */
public abstract class Routine {
    /**
     * Keeps access to all subsystems to modify their output and read their status like
     * {@link Drive#controllerOnTarget()}
     */
    protected final Drive drive = Drive.getInstance();

    // Called to start a routine
    public abstract void start();
    // Update method, returns modified commands
    public abstract Commands update(Commands commands);
    // Called to stop a routine, should return modified commands if needed
    public abstract Commands cancel(Commands commands);
    // Notifies routine manager when routine is complete
    public abstract boolean isFinished();
    // Store subsystems which are required by this routine, preventing routines from overlapping
    public abstract Subsystem[] getRequiredSubsystems();
    // Force override of getName()
    public abstract String getName();
}
