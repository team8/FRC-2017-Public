package com.palyrobotics.frc2017.behavior.routines.scoring;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.subsystems.Slider;
import com.palyrobotics.frc2017.subsystems.Spatula;
import com.palyrobotics.frc2017.subsystems.Slider.SliderState;
import com.palyrobotics.frc2017.subsystems.Slider.SliderTarget;
import com.palyrobotics.frc2017.util.Subsystem;

public class VisionSliderRoutine extends Routine {
	private enum VisionPositioningState {
		START, SENT
	}
	private VisionPositioningState mState = VisionPositioningState.START;
	
	private static final double raiseTime = 1000;
	
	public VisionSliderRoutine() {
	}
	
	@Override
	public void start() {
	}

	@Override
	public Commands update(Commands commands) {
		commands.robotSetpoints.sliderSetpoint = SliderTarget.VISION;
		commands.wantedSpatulaState = Spatula.SpatulaState.UP;
		switch(mState) {
		case START:
			commands.wantedSliderState = Slider.SliderState.VISION_POSITIONING;
			try {
				slider.run(commands, this);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			break;
		case SENT:
			commands.wantedSliderState = Slider.SliderState.VISION_POSITIONING;
			break;
		}		
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		commands.wantedSliderState = SliderState.IDLE;
		try {
			slider.run(commands, this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return commands;
	}

	@Override
	public boolean finished() {
		return mState==VisionPositioningState.SENT && slider.onTarget();
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