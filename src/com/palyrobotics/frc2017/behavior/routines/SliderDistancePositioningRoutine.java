package com.palyrobotics.frc2017.behavior.routines;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.subsystems.Slider;
import com.palyrobotics.frc2017.subsystems.Spatula;
import com.palyrobotics.frc2017.subsystems.Slider.SliderState;
import com.palyrobotics.frc2017.subsystems.Spatula.SpatulaState;
import com.palyrobotics.frc2017.util.Subsystem;

public class SliderDistancePositioningRoutine extends Routine {
	// Whether this routine is allowed to run or not
	private boolean mAllowed;
	
	@Override
	public void start() {
		if (spatula.getState() == SpatulaState.DOWN) {
			mAllowed = false;
		} else {
			mAllowed = true;
		}
	}

	@Override
	public Commands update(Commands commands) {
		if (mAllowed) {
			commands.wantedSliderState = Slider.SliderState.ENCODER_POSITIONING;
		} else {
			commands.wantedSliderState = Slider.SliderState.IDLE;
		}
		try {
			slider.run(commands, this);
		} catch (IllegalAccessException e) {
			System.err.println("Slider position routine rejected!");
			e.printStackTrace();
		}
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		commands.wantedSliderState = Slider.SliderState.IDLE;
		try {
			slider.run(commands, this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return commands;
	}

	@Override
	public boolean finished() {
		return !mAllowed || slider.onTarget();
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{Slider.getInstance(), Spatula.getInstance()};
	}

	@Override
	public String getName() {
		return "Slider Distance Positioning Routine";
	}

}
