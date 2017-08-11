package com.palyrobotics.frc2017.robot.team254.lib.util;

import com.palyrobotics.frc2017.config.Constants;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;
import java.util.List;

/**
 * This code runs all of the robot's loops. Loop objects are stored in a List
 * object. They are started when the robot powers up and stopped after the
 * match.
 * @author Team 254
 */
public class Looper {
	// SubsystemLoop update rate
	private final double kPeriod = Constants.kNormalLoopsDt;

	private boolean running_;

	private final Notifier notifier_;
	private final List<Loop> loops_;
	private final Object taskRunningLock_ = new Object();
	private double timestamp_ = 0;
	private double dt_ = 0;
	// Main method that is run at the update rate
	private final CrashTrackingRunnable runnable_ = new CrashTrackingRunnable() {
		@Override
		public void runCrashTracked() {
			synchronized (taskRunningLock_) {
				if (running_) {
					double now = Timer.getFPGATimestamp();
					for (Loop loop : loops_) {
						loop.update();
					}
					dt_ = now - timestamp_;
					timestamp_ = now;
				}
			}
		}
	};

	public Looper() {
		notifier_ = new Notifier((Runnable) runnable_);
		running_ = false;
		loops_ = new ArrayList<>();
	}

	public synchronized void register(Loop loop) {
		synchronized (taskRunningLock_) {
			loops_.add(loop);
		}
	}

	public synchronized void start() {
		if (!running_) {
			System.out.println("Starting loops");
			synchronized (taskRunningLock_) {
				timestamp_ = Timer.getFPGATimestamp();
				for (Loop loop : loops_) {
					loop.onStart();
				}
				running_ = true;
			}
			notifier_.startPeriodic(kPeriod);
		}
	}

	public synchronized void stop() {
		if (running_) {
			System.out.println("Stopping loops");
			notifier_.stop();
			synchronized (taskRunningLock_) {
				running_ = false;
				for (Loop loop : loops_) {
					System.out.println("Stopping " + loop.toString());
					loop.onStop();
				}
			}
		}
	}

	public void outputToSmartDashboard() {
		SmartDashboard.putNumber("looper_dt", dt_);
	}
}