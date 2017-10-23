package com.palyrobotics.frc2017.behavior.routines.scoring;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.behavior.routines.drive.CANTalonRoutine;
import com.palyrobotics.frc2017.config.AutoDistances;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Gains;
import com.palyrobotics.frc2017.subsystems.Subsystem;
import com.palyrobotics.frc2017.util.archive.DriveSignal;
import com.palyrobotics.frc2017.vision.VisionData;

/**
 * Created by EricLiu on 9/5/17.
 */
public class VisionDriveForwardRoutine extends Routine {

    //How far forward to drive
    private double forwardDist = 0;

    //Used for
    private double startTime = 0;

    //How long to wait before moving forward(waiting for slider to finish moving)
    private double delay;

    //Whether or not the routine has started(finished waiting)
    private boolean hasStarted = false;

    //The sub-routine that controls movement
    private CANTalonRoutine routine;

    /**
     * Creates a new drive forward routine using vision values without a delay
     */
    public VisionDriveForwardRoutine() {
        this.delay = 0;
    }

    /**
     * Creates a new drive forward routine using vision values with a delay
     *
     * @param delay time to wait in seconds
     */
    public VisionDriveForwardRoutine(double delay) {
        this.delay = delay;
    }

    @Override
    public void start() {

        this.startTime = System.currentTimeMillis();

        if(VisionData.getZData().exists()) {
            forwardDist = VisionData.getZDataValue();
        } else {
            forwardDist = AutoDistances.kVisionDistanceInches;
        }

        if(forwardDist > Constants.kMaxVisionZ || forwardDist < Constants.kMinVisionZ) {
            System.out.println("Faulty Vision Z Dist, reverting to hard-coded value");
            forwardDist = AutoDistances.kVisionDistanceInches - 0.5*Constants.kRobotLengthInches;
        }

        forwardDist += Constants.kDriveForwardBufferInches;

        forwardDist = forwardDist * Constants.kDriveTicksPerInch;

        Gains mShortGains = Gains.steikShortDriveMotionMagicGains;

        DriveSignal driveScore = DriveSignal.getNeutralSignal();
        driveScore.leftMotor.setMotionMagic(forwardDist, mShortGains,
                Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);
        driveScore.rightMotor.setMotionMagic(forwardDist, mShortGains,
                Gains.kSteikShortDriveMotionMagicCruiseVelocity, Gains.kSteikShortDriveMotionMagicMaxAcceleration);

        routine =  new CANTalonRoutine(driveScore, true);
    }

    @Override
    public Commands update(Commands commands) {

        //Check delay and start routine
        if(!hasStarted && System.currentTimeMillis() >= (startTime + 1000*this.delay)) {
            routine.start();
            hasStarted = true;
        }

        if(!hasStarted) {
            return commands;
        }

        return routine.update(commands);
    }

    @Override
    public Commands cancel(Commands commands) {
        if(!hasStarted) {
            return commands;
        }

        return routine.cancel(commands);
    }

    @Override
    public boolean finished() {
        if(!hasStarted) {
            return false;
        }

        return routine.finished();
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return routine == null ? new Subsystem[0] : routine.getRequiredSubsystems();
    }

    @Override
    public String getName() {
        return routine == null ? "" : routine.getName();
    }
}
