package com.palyrobotics.frc2017.util.archive;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.team254.lib.util.Loop;

/**
 * Created by Nihar on 12/4/16.
 * based on Team 254 Loop {@link Loop}
 * @author Nihar
 */
public interface SubsystemLoop {
	public void start();
	public void update(Commands commands, RobotState robotState);
	public void stop();
	public void printStatus();
}
