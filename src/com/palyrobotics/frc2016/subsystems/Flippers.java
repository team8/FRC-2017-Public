package com.palyrobotics.frc2016.subsystems;

import com.palyrobotics.frc2016.config.Commands;
import com.palyrobotics.frc2016.config.RobotState;
import com.palyrobotics.frc2016.util.Subsystem;
import com.palyrobotics.frc2016.util.SubsystemLoop;

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
		
		public void toggleLeft() {
			leftFlipper = (leftFlipper == DoubleSolenoid.Value.kForward) ? DoubleSolenoid.Value.kReverse : DoubleSolenoid.Value.kForward;
		}
		
		public void toggleRight() {
			rightFlipper = (rightFlipper == DoubleSolenoid.Value.kForward) ? DoubleSolenoid.Value.kReverse : DoubleSolenoid.Value.kForward;
		}
	}
	
	private FlipperSignal mFlipperSignal;
	
	private Flippers() {
		super("Flippers");
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
	}

	public FlipperSignal getFlipperSignal() {
		return mFlipperSignal;
	}
}
