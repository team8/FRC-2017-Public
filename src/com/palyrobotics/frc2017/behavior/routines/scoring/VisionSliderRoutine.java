package com.palyrobotics.frc2017.behavior.routines.scoring;

import java.util.Optional;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.robot.Robot;
import com.palyrobotics.frc2017.subsystems.Slider;
import com.palyrobotics.frc2017.subsystems.Spatula;
import com.palyrobotics.frc2017.subsystems.Slider.SliderState;
import com.palyrobotics.frc2017.subsystems.Slider.SliderTarget;
import com.palyrobotics.frc2017.util.Subsystem;
import com.palyrobotics.frc2017.vision.AndroidConnectionHelper;

public class VisionSliderRoutine extends Routine {
	private double startTime = 0;
	
	// Used to make sure vision setpoint is only sent once
	private enum VisionPositioningState {
		START, SENT
	}
	private VisionPositioningState mState = VisionPositioningState.START;
	double offset = 7.5;
	public VisionSliderRoutine() {
	}
	
	@Override
	public void start() {
		startTime = System.currentTimeMillis();
	}

	@Override
	public Commands update(Commands commands) {
		commands.robotSetpoints.sliderSetpoint = SliderTarget.CUSTOM;
		commands.wantedSpatulaState = Spatula.SpatulaState.UP;
		double visionSetpoint = AndroidConnectionHelper.getInstance().getXDist();
		// out of range of motion, probably false positive, might be on left side
		if (visionSetpoint >= 1.5) {
			visionSetpoint = -7;
		} 
		else if (visionSetpoint <= -7-offset) {
			visionSetpoint = -7;
		} // extend motion
		else if (visionSetpoint < 0) {
			visionSetpoint += offset;
		} else {
			visionSetpoint += offset;
		}
		visionSetpoint = Math.max(-7, Math.min(visionSetpoint, 7));
		if (commands.robotSetpoints.sliderCustomSetpoint.isPresent()) {
			System.out.println("Vision setpoint: "+visionSetpoint);
		}
		commands.robotSetpoints.sliderCustomSetpoint =
				Optional.of(visionSetpoint * Constants.kSliderRevolutionsPerInch);
		switch(mState) {
		case START:
			commands.wantedSliderState = Slider.SliderState.CUSTOM_POSITIONING;
			try {
				slider.run(commands, this);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			mState = VisionPositioningState.SENT;
			break;
		case SENT:
			commands.wantedSliderState = Slider.SliderState.CUSTOM_POSITIONING;
			break;
		}		
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		commands.wantedSliderState = SliderState.IDLE;
		commands.robotSetpoints.sliderCustomSetpoint = Optional.empty();
		try {
			slider.run(commands, this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return commands;
	}

	@Override
	public boolean finished() {
		return mState==VisionPositioningState.SENT && 
				(System.currentTimeMillis() - startTime > 200) &&
				Robot.getRobotState().sliderVelocity == 0;
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{Slider.getInstance(), Spatula.getInstance()};
	}

	@Override
	public String getName() {
		return "SliderVisionPositioningRoutine";
	}
}