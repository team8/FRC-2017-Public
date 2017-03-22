package com.palyrobotics.frc2017.behavior.routines.scoring;

import java.util.Optional;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.subsystems.Slider;
import com.palyrobotics.frc2017.subsystems.Spatula;
import com.palyrobotics.frc2017.subsystems.Spatula.SpatulaState;
import com.palyrobotics.frc2017.util.Subsystem; 

/**
 * Moves the slider to a setpoint
 * NOTE: When unit testing, set Robot.RobotState appropriately
 * @author Prashanti
 */
public class SliderDistanceCustomPositioningRoutine extends Routine {
	// Whether this routine is allowed to run or not
	private boolean mAllowed;
	private double target;
	
	public SliderDistanceCustomPositioningRoutine(double target) {
		this.target = target;
	}
	
	@Override
	public void start() {
		if (spatula.getState() == SpatulaState.DOWN) {
			mAllowed = false;
		} else {
			mAllowed = true;
		}
		System.out.println("Non autocorrect");
	}

	@Override
	public Commands update(Commands commands) {
		if (mAllowed) {
			commands.wantedSliderState = Slider.SliderState.CUSTOM_POSITIONING;
			commands.robotSetpoints.sliderCustomSetpoint = Optional.of(target);
		} else {
			commands.wantedSliderState = Slider.SliderState.IDLE;
			commands.robotSetpoints.sliderCustomSetpoint = Optional.empty();
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
		return !mAllowed || slider.onTarget();
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{Slider.getInstance(), Spatula.getInstance()};
	}

	@Override
	public String getName() {
		return "Slider Distance Custom Positioning Routine";
	}

}