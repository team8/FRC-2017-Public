package com.palyrobotics.frc2017.behavior.routines.scoring;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.subsystems.Slider.SliderState;
import com.palyrobotics.frc2017.subsystems.Spatula.SpatulaState;
import com.palyrobotics.frc2017.util.Subsystem;

public class SilderDistancePositioningRoutine extends Routine {
	
	private Subsystem[] required = {slider, spatula};
	
	@Override
	public void start() {		
	}

	@Override
	public Commands update(Commands commands) {
		try {
			slider.update(commands, this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		if(spatula.getState() == SpatulaState.DOWN) {
			return commands;
		}
		try {
			slider.update(commands, this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return commands;
	}

	@Override
	public boolean finished() {
		return spatula.getState() == SpatulaState.DOWN || slider.getSliderState() == SliderState.IDLE;
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return required;
	}

	@Override
	public String getName() {
		return "Slider Distance Positioning Routine";
	}

}
