package com.palyrobotics.frc2017.behavior.routines.scoring;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.subsystems.Slider;
import com.palyrobotics.frc2017.subsystems.Slider.SliderState;
import com.palyrobotics.frc2017.util.Subsystem;

public class ManualSliderControlRoutine extends Routine {	
	@Override
	public void start() {	
		System.out.println("Manually controlling slider");
	}

	@Override
	public Commands update(Commands commands) {
		try {
			commands.wantedSliderState = SliderState.MANUAL;
			slider.run(commands, this);
		} catch (IllegalAccessException e) {
			System.err.println("Manual Slider Routine rejected!");
			e.printStackTrace();
		}	
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		commands.wantedSliderState = SliderState.IDLE;
		System.out.println("Canceling manual slider control");
		try {
			slider.run(commands, this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return commands;
	}

	@Override
	public boolean finished() {
		return false;
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{Slider.getInstance()};
	}

	@Override
	public String getName() {
		return "Manual Slider Control Routine";
	}

}