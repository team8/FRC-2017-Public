package com.palyrobotics.frc2017.behavior.routines.drive;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.subsystems.Subsystem;

/**
 * @author Nihar
 */
public class SafetyTurnAngleRoutine extends Routine {
	private double targetAngle;
	private Routine mRoutine;
	private RobotState robotState;

	public SafetyTurnAngleRoutine(double angle, RobotState robotState) {
		this.targetAngle = angle;
	}

	@Override
	public void start() {
		if(robotState.drivePose.heading == -0.0) {
			System.out.println("Gyro broken");
			mRoutine = new EncoderTurnAngleRoutine(targetAngle);
        } else {
        	System.out.println("Gyro working!");
            mRoutine = new BBTurnAngleRoutine(targetAngle);
        }
		mRoutine.start();
	}

	@Override
	public Commands update(Commands commands) {
		System.out.println("angle: "+ robotState.drivePose.heading);
		return mRoutine.update(commands);
	}

	@Override
	public Commands cancel(Commands commands) {
		return mRoutine.cancel(commands);
	}

	@Override
	public boolean finished() {
		return mRoutine.finished();
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{drive};
	}

	@Override
	public String getName() {
		String name = "Safety Turn Angle with ";
		if (mRoutine instanceof BBTurnAngleRoutine) {
			name += "Gyro Turn Angle";
		} else {
			name += "Encoder Turn Angle";
		}
		return name;
	}
}
