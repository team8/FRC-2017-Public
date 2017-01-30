package com.palyrobotics.frc2017.util;

public class DriveSignal {
    public CANTalonOutput leftMotor;
    public CANTalonOutput rightMotor;

    public DriveSignal(CANTalonOutput left, CANTalonOutput right) {
        this.leftMotor = left;
        this.rightMotor = right;
    }

    private static CANTalonOutput neutral;
    static {
        neutral = new CANTalonOutput();
        neutral.setPercentVBus(0);
    }
    public static DriveSignal getNeutralSignal() {
        return new DriveSignal(neutral, neutral);
    }
}