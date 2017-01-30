package com.palyrobotics.frc2017.robot.team254.lib.util;

import edu.wpi.first.wpilibj.InterruptHandlerFunction;

public abstract class ChezyInterruptHandlerFunction<T> extends InterruptHandlerFunction<T> {
    public abstract void interruptFired(int interruptAssertedMask, T param);
}
