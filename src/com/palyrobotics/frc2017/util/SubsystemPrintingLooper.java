package com.palyrobotics.frc2017.util;

import java.util.ArrayList;
import java.util.List;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.Robot;
import com.palyrobotics.frc2017.robot.team254.lib.util.CrashTrackingRunnable;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SubsystemPrintingLooper {
	// SubsystemLoop update rate
		private final double kPeriod = Constants.kSubsystemPrintingLooperDt;

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
							loop.printStatus();
						}
						mDt = now - mTimeStamp;
						mTimeStamp = now;
					}
				}
			}
		};

		public SubsystemPrintingLooper() {
			mNotifier = new Notifier((Runnable) mRunnable);
			mRunning = false;
			mLoops = new ArrayList<>();
		}

		public synchronized void register(SubsystemLoop loop) {
			synchronized (mTaskRunningLock) {
				System.out.println("Added loop: "+loop.toString());
				mLoops.add(loop);
			}
		}

		public synchronized void start() {
			if (!mRunning) {
				System.out.println("Starting loops");
				synchronized (mTaskRunningLock) {
					mTimeStamp = Timer.getFPGATimestamp();
					for (SubsystemLoop loop : mLoops) {
						System.out.println("Starting " + loop.toString());
						loop.start();
					}
					mRunning = true;
				}
				mNotifier.startPeriodic(kPeriod);
			} else {
				System.out.println("SubsystemLooper already running");
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
