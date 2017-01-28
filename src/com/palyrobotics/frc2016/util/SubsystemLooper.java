package com.palyrobotics.frc2016.util;

import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.config.Constants;
import com.palyrobotics.frc2016.config.RobotState;
import com.palyrobotics.frc2016.robot.Robot;
import com.palyrobotics.frc2016.robot.team254.lib.util.CrashTrackingRunnable;
import com.palyrobotics.frc2016.robot.team254.lib.util.Looper;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nihar on 12/4/16.
 * Based on Team 254 {@link Looper}
 */
public class SubsystemLooper {
	// SubsystemLoop update rate
	private final double kPeriod = Constants.kSubsystemLooperDt;

	private boolean mRunning;

	private final Notifier mNotifier;
	private final List<SubsystemLoop> mLoops;
	private final Object mTaskRunningLock = new Object();
	private double mTimeStamp = 0;
	private double mDt = 0;
	// Main method that is run at the update rate
	private final CrashTrackingRunnable mRunnable = new CrashTrackingRunnable() {
		@Override
		public void runCrashTracked() {
			synchronized (mTaskRunningLock) {
				if (mRunning) {
					double now = Timer.getFPGATimestamp();
					Commands commands = Robot.getCommands();
					RobotState robotState = Robot.getRobotState();
					for (SubsystemLoop loop : mLoops) {
						loop.update(commands, robotState);
					}
					mDt = now - mTimeStamp;
					mTimeStamp = now;
				}
			}
		}
	};

	public SubsystemLooper() {
		mNotifier = new Notifier((Runnable) mRunnable);
		mRunning = false;
		mLoops = new ArrayList<>();
	}

	public synchronized void register(SubsystemLoop loop) {
		synchronized (mTaskRunningLock) {
			mLoops.add(loop);
		}
	}

	public synchronized void start() {
		if (!mRunning) {
			System.out.println("Starting loops");
			synchronized (mTaskRunningLock) {
				mTimeStamp = Timer.getFPGATimestamp();
				for (SubsystemLoop loop : mLoops) {
					loop.start();
				}
				mRunning = true;
			}
			mNotifier.startPeriodic(kPeriod);
		}
	}

	public synchronized void stop() {
		if (mRunning) {
			System.out.println("Stopping loops");
			mNotifier.stop();
			synchronized (mTaskRunningLock) {
				mRunning = false;
				for (SubsystemLoop loop : mLoops) {
					System.out.println("Stopping " + loop.toString());
					loop.stop();
				}
			}
		}
	}

	public void outputToSmartDashboard() {
		SmartDashboard.putNumber("looper_dt", mDt);
	}
}