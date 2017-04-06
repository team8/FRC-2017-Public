package com.palyrobotics.frc2017.subsystems;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.config.dashboard.DashboardManager;
import com.palyrobotics.frc2017.config.dashboard.DashboardValue;
import com.palyrobotics.frc2017.util.Subsystem;
import com.palyrobotics.frc2017.util.archive.SubsystemLoop;

import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * STEIK FLIPPERS
 * @author Ailyn Tong
 * Consists of two rods for the purpose of wall alignment during gear scoring
 * Each rod is controlled independently by a DoubleSolenoid
 */
public class Flippers extends Subsystem implements SubsystemLoop {
	
	private static Flippers instance = new Flippers();
	
	
	public static Flippers getInstance() {
		return instance;
	}
	
	/**
	 * FlipperSignal stores values for left and right flippers
	 * Uses a toggle system
	 */
	public static class FlipperSignal {
		public DoubleSolenoid.Value leftFlipper, rightFlipper;
		
		public FlipperSignal(DoubleSolenoid.Value leftFlipper, DoubleSolenoid.Value rightFlipper) {
			this.leftFlipper = leftFlipper;
			this.rightFlipper = rightFlipper;
		}
	}
	
	private FlipperSignal mFlipperSignal;
	
	private DashboardValue mDv;
	
	private Flippers() {
		super("Flippers");
		
		mDv = new DashboardValue("flipperstatus");
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public void update(Commands commands, RobotState robotState) {
		mFlipperSignal = commands.wantedFlipperSignal;
		
		mDv.updateValue("NO FLIPPERS");
		DashboardManager.getInstance().publishKVPair(mDv);
	}

	public FlipperSignal getFlipperSignal() {
		return mFlipperSignal;
	}

	@Override
	public String getStatus() {
		return "";
	}

}
