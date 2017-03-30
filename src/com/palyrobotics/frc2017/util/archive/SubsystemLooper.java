package com.palyrobotics.frc2017.util.archive;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.Robot;
import com.palyrobotics.frc2017.robot.team254.lib.util.CrashTrackingRunnable;
import com.palyrobotics.frc2017.util.Subsystem;
import com.palyrobotics.frc2017.util.logger.Logger;

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
	// Whether to print or not
	private boolean mAllowPrinting = false;
	private final List<SubsystemLoop> mLoops;

	// Used for the main loop
	private boolean mRunning;
	private final double kPeriod = Constants.kSubsystemLooperDt;
	private final Notifier mNotifier;
	private final Object mTaskRunningLock = new Object();
	private double mTimeStamp = 0;
	private double mDt = 0;

	// Used for secondary printer loop
	private boolean mPrinting = false;
	private final double kPrintRate = Constants.kSubsystemPrintLooperDt;
	private final Notifier mPrintNotifier;
	private final Object mPrintingLock = new Object();
	private double mPrintTimeStamp = 0;
	private double mPrintDt = 0;

	// Main method that is run at the update rate
	private final SubsystemRunnable mRunnable = new SubsystemRunnable() {
		@Override
		public void runCrashTracked() {
			synchronized (mTaskRunningLock) {
				if (mRunning) {
					double now = Timer.getFPGATimestamp();
					Commands commands = Robot.getCommands();
					RobotState robotState = Robot.getRobotState();
					for (SubsystemLoop loop : mLoops) {
						loop.update(commands, robotState);
//						Logger.getInstance().logSubsystemThread(loop.printStatus());
					}
					mDt = now - mTimeStamp;
					mTimeStamp = now;
				}
			}
		}
	};
	// Secondary method that is run at a slower update rate to print to console
	private final CrashTrackingRunnable mPrinterRunnable = new CrashTrackingRunnable() {
		@Override
		public void runCrashTracked() {
			synchronized (mPrintingLock) {
				if (mPrinting && mAllowPrinting) {
					double now = Timer.getFPGATimestamp();
					for (SubsystemLoop loop : mLoops) {
						System.out.println(loop.printStatus());
					}
					mPrintDt = now - mPrintTimeStamp;
					mPrintTimeStamp = now;
				}
			}
		}
	};


	public SubsystemLooper() {
		mLoops = new ArrayList<>();
		mNotifier = new Notifier(mRunnable);
		mRunning = false;

		mPrintNotifier = new Notifier(mPrinterRunnable);
		mPrinting = false;
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
		if (!mPrinting) {
			System.out.println("Starting subsystem printer");
			mPrintTimeStamp = Timer.getFPGATimestamp();
			mPrinting = true;
			mPrintNotifier.startPeriodic(kPrintRate);
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
//		if (mPrinting) {
//			System.out.println("Stopping subsystem printer");
//			mPrinting = false;
//		}
	}

	public void outputToSmartDashboard() {
		SmartDashboard.putNumber("looper_dt", mDt);
	}
}