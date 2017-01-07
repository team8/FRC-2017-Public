package com.palyrobotics.frc2016.util;

import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.config.Constants;
import com.palyrobotics.frc2016.config.RobotState;
import com.palyrobotics.frc2016.robot.OperatorInterface;
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

	private boolean running_;

	private final Notifier notifier_;
	private final List<SubsystemLoop> loops_;
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
					Commands commands = Commands.getInstance();
					RobotState robotState = Robot.getRobotState();
					for (SubsystemLoop loop : loops_) {
						loop.update(commands, robotState);
					}
					dt_ = now - timestamp_;
					timestamp_ = now;
				}
			}
		}
	};

	public SubsystemLooper() {
		notifier_ = new Notifier((Runnable) runnable_);
		running_ = false;
		loops_ = new ArrayList<>();
	}

	public synchronized void register(SubsystemLoop loop) {
		synchronized (taskRunningLock_) {
			loops_.add(loop);
		}
	}

	public synchronized void start() {
		if (!running_) {
			System.out.println("Starting loops");
			synchronized (taskRunningLock_) {
				timestamp_ = Timer.getFPGATimestamp();
				for (SubsystemLoop loop : loops_) {
					loop.start();
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
				for (SubsystemLoop loop : loops_) {
					System.out.println("Stopping " + loop.toString());
					loop.stop();
				}
			}
		}
	}

	public void outputToSmartDashboard() {
		SmartDashboard.putNumber("looper_dt", dt_);
	}
}