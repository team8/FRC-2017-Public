package com.palyrobotics.frc2017.behavior.routines.scoring;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.subsystems.Slider.SliderState;
import com.palyrobotics.frc2017.subsystems.Spatula.SpatulaState;
import com.palyrobotics.frc2017.util.Subsystem;

public class SliderDistancePositioningAutocorrectRoutine extends Routine {
	
	private Subsystem[] required = {slider, spatula};
	enum DistancePositioningState {
		RAISING,
		MOVING
	}
	private DistancePositioningState mState;
	
	private double startTime;
	private static final double raiseTime = 2000;
	
	@Override
	public void start() {
		if(spatula.getState() == SpatulaState.DOWN) {
			mState = DistancePositioningState.RAISING;
		}
		else {
			mState = DistancePositioningState.MOVING;
		}
		startTime = System.currentTimeMillis();
		
	}

	@Override
	public Commands update(Commands commands) {
		switch(mState) {
		case MOVING:
			try {
				slider.run(commands, this);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			break;
		case RAISING:
			if(System.currentTimeMillis() - startTime > raiseTime) {
				mState = DistancePositioningState.MOVING;
			}
			//TODO: spatula code (is it supposed to have an update method like slider???? that takes a routine???
			break;
		default:
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
		return slider.getSliderState() == SliderState.IDLE;
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return required;
	}

	@Override
	public String getName() {
		return "Slider Distance Positioning Autocorrect Routine";
	}

}
