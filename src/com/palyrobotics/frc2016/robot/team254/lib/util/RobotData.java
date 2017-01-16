package com.palyrobotics.frc2016.robot.team254.lib.util;

import com.palyrobotics.frc2016.robot.HardwareAdapter;

import edu.wpi.first.wpilibj.Timer;

public class RobotData implements Tappable {

    @Override
    public String getName() {
        return "robot";
    }

    @Override
    public void getState(StateHolder states) {
        states.put("voltage", HardwareAdapter.PDP.getVoltage());
        states.put("robotTime", Timer.getFPGATimestamp());
    }
}
