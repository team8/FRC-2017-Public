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
import com.palyrobotics.frc2017.subsystems.Spatula.SpatulaState;
import com.palyrobotics.frc2017.util.Subsystem;
import com.palyrobotics.frc2017.vision.AndroidConnectionHelper;

public class VisionSliderRoutine extends Routine {
	// Use to make sure routine ran at least once before "finished"
	private boolean updated = false;
	private double startTime = 0;
	
	private enum VisionPositioningState {
		START, SENT
	}
	private VisionPositioningState mState = VisionPositioningState.START;
	
	public VisionSliderRoutine() {
	}
	
	@Override
	public void start() {
	}

	@Override
	public Commands update(Commands commands) {
		commands.robotSetpoints.sliderSetpoint = SliderTarget.CUSTOM;
		commands.wantedSpatulaState = Spatula.SpatulaState.UP;
		commands.robotSetpoints.sliderCustomSetpoint = 
				Optional.of(AndroidConnectionHelper.getInstance().x_dist * Constants.kSliderRevolutionsPerInch);
		
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
				Math.abs(Robot.getRobotState().sliderClosedLoopError.get()) < 40 &&
				Robot.getRobotState().sliderVelocity == 0;
		//return false;
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{Slider.getInstance(), Spatula.getInstance()};
	}

	@Override
	public String getName() {
		return "Slider Vision Positioning Routine";
	}
}