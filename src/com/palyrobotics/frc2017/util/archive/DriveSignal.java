package com.palyrobotics.frc2017.util.archive;

import com.palyrobotics.frc2017.util.CANTalonOutput;

public class DriveSignal {
	public CANTalonOutput leftMotor;
	public CANTalonOutput rightMotor;

	public DriveSignal(CANTalonOutput left, CANTalonOutput right) {
		this.leftMotor = left;
		this.rightMotor = right;
	}

	public static DriveSignal getNeutralSignal() {
		CANTalonOutput leftNeutral = new CANTalonOutput();
		CANTalonOutput rightNeutral = new CANTalonOutput();
		leftNeutral.setPercentVBus(0);
		rightNeutral.setPercentVBus(0);

		return new DriveSignal(leftNeutral, rightNeutral);
	}

	@Override
	public boolean equals(Object obj) {
		return ((DriveSignal) obj).leftMotor.equals(this.leftMotor) &&
				((DriveSignal) obj).rightMotor.equals(this.rightMotor);
	}

	@Override
	public String toString() {
		return "left:"+leftMotor.toString()+" right:"+rightMotor.toString();
	}
}