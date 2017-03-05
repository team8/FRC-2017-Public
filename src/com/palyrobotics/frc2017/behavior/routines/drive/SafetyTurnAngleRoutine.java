package com.palyrobotics.frc2017.behavior.routines.drive;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.Robot;
import com.palyrobotics.frc2017.util.Subsystem;

/**
 * Created by EricLiu on 3/5/17.
 */
public class SafetyTurnAngleRoutine extends Routine {
    private EncoderTurnAngleRoutine encoderTurnAngle;
    private BBTurnAngleRoutine gyroTurnAngle;

    public SafetyTurnAngleRoutine(double angle) {
        if(Robot.getRobotState().drivePose.heading != -0.0) {
            gyroTurnAngle = new BBTurnAngleRoutine(angle);
        } else {
            encoderTurnAngle = new EncoderTurnAngleRoutine(angle);
        }
    }

    @Override
    public void start() {
        if(gyroTurnAngle != null) {
            gyroTurnAngle.start();
        } else {
            encoderTurnAngle.start();
        }

    }

    @Override
    public Commands update(Commands commands) {
        if(gyroTurnAngle != null) {
            return gyroTurnAngle.update(commands);
        } else {
            return encoderTurnAngle.update(commands);
        }
    }

    @Override
    public Commands cancel(Commands commands) {
        if(gyroTurnAngle != null) {
            return gyroTurnAngle.cancel(commands);
        } else {
            return encoderTurnAngle.cancel(commands);
        }
    }

    @Override
    public boolean finished() {
        if(gyroTurnAngle != null) {
            return gyroTurnAngle.finished();
        } else {
            return encoderTurnAngle.finished();
        }
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{drive};
    }

    @Override
    public String getName() {
        String name = "Safety Turn Angle with ";
        if(gyroTurnAngle != null) {
            name += "Gyro Turn Angle";
        } else {
            name += "Encoder Turn Angle";
        }
        return name;
    }
}
