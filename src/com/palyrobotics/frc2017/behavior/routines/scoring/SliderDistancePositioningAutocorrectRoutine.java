package com.palyrobotics.frc2017.behavior.routines.scoring;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.subsystems.Slider;
import com.palyrobotics.frc2017.subsystems.Spatula;
import com.palyrobotics.frc2017.subsystems.Slider.SliderState;
import com.palyrobotics.frc2017.subsystems.Spatula.SpatulaState;
import com.palyrobotics.frc2017.util.Subsystem;

/** 
 * Autocorrects -> only tells the slider to move once safe (spatula up)
 * @author Prashanti, Nihar, Ailyn
 *
 */
public class SliderDistancePositioningAutocorrectRoutine extends Routine {	
	private enum DistancePositioningState {
		RAISING,
		MOVING
	}
	private DistancePositioningState mState = DistancePositioningState.RAISING;
	
	private double startTime;
	private static final double raiseTime = 1000;
	
	@Override
	public void start() {
		if (spatula.getState() == SpatulaState.DOWN) {
			System.out.println("Autocorrecting spatula!");
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
			commands.wantedSliderState = Slider.SliderState.AUTOMATIC_POSITIONING;
			try {
				slider.run(commands, this);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			break;
		case RAISING:
			if(System.currentTimeMillis() - startTime > raiseTime) {
				System.exit(1);
				mState = DistancePositioningState.MOVING;
			}
			commands.wantedSpatulaState = Spatula.SpatulaState.UP;
			commands.wantedSliderState = Slider.SliderState.WAITING;
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
		return slider.onTarget();
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{Slider.getInstance(), Spatula.getInstance()};
	}

	@Override
	public String getName() {
		return "Slider Distance Positioning Autocorrect Routine";
	}

}
