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
		CANTalonOutput neutral = new CANTalonOutput();
		neutral.setPercentVBus(0);

		return new DriveSignal(neutral, neutral);
	}

	@Override
	public boolean equals(Object obj) {
		return ((DriveSignal) obj).leftMotor.equals(this.leftMotor) &&
				((DriveSignal) obj).rightMotor.equals(this.rightMotor);
	}
}