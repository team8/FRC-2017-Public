package com.palyrobotics.frc2017.behavior.routines;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.subsystems.Slider;
import com.palyrobotics.frc2017.subsystems.Slider.SliderState;
import com.palyrobotics.frc2017.util.Subsystem;

public class ManualSliderControlRoutine extends Routine {
	
	private static final Subsystem[] required = {Slider.getInstance()};
	
	@Override
	public void start() {	
		System.out.println("Starting manual");
	}

	@Override
	public Commands update(Commands commands) {
		try {
			Commands newCommands = commands.copy();
			newCommands.wantedSliderState = SliderState.MANUAL;
			slider.run(newCommands, this);
		} catch (IllegalAccessException e) {
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
		return required;
	}

	@Override
	public String getName() {
		return "Manual Slider Control Routine";
	}

}