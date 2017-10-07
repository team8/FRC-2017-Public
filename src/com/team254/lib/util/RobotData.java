package com.team254.lib.util;

import com.palyrobotics.frc2016.HardwareAdaptor;
import edu.wpi.first.wpilibj.Timer;

public class RobotData implements Tappable {

    @Override
    public String getName() {
        return "robot";
    }

    @Override
    public void getState(StateHolder states) {
        states.put("voltage", HardwareAdaptor.kPDP.getVoltage());
        states.put("robotTime", Timer.getFPGATimestamp());
    }
}
