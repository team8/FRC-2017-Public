package com.palyrobotics.frc2017.behavior.routines.scoring;

import java.util.Optional;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.robot.HardwareAdapter;
import com.palyrobotics.frc2017.robot.Robot;
import com.palyrobotics.frc2017.subsystems.Slider;
import com.palyrobotics.frc2017.subsystems.Spatula;
import com.palyrobotics.frc2017.subsystems.Spatula.SpatulaState;
import com.palyrobotics.frc2017.util.Subsystem; 

/**
 * Moves the slider to a setpoint
 * NOTE: When unit testing, set Robot.RobotState appropriately
 * @author Prashanti
 */
public class CustomPositioningSliderRoutine extends Routine {
	private enum DistancePositioningState {
		RAISING,
		MOVING,
		WAITING
	}
	private DistancePositioningState mState = DistancePositioningState.RAISING;
	// Use to make sure routine ran at least once before "finished"
	private boolean updated = false;
	
	private double target;
	
	private double startTime;
	private static final double raiseTime = 1700;
	
	// Target should be absolute position in inches
	public CustomPositioningSliderRoutine(double target) {
		this.target = target;
	}
	
	@Override
	public void start() {
		if (spatula.getState() == SpatulaState.DOWN || slider.getSliderState() == Slider.SliderState.WAITING) {
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
		commands.robotSetpoints.sliderSetpoint = Slider.SliderTarget.CUSTOM;
		commands.robotSetpoints.sliderCustomSetpoint = Optional.of(target * Constants.kSliderRevolutionsPerInch);
		updated = true;
		switch(mState) {
		case MOVING:
			commands.wantedSliderState = Slider.SliderState.CUSTOM_POSITIONING;
			try {
				slider.run(commands, this);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
			}
			break;
		case WAITING:

			break;
			case RAISING:
			if(System.currentTimeMillis() > (raiseTime+startTime)) {
				mState = DistancePositioningState.MOVING;
				break;
			}
			commands.wantedSpatulaState = Spatula.SpatulaState.UP;
			commands.wantedSliderState = Slider.SliderState.WAITING;
			break;
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
		RobotState robotState = Robot.getRobotState();

		if(!HardwareAdapter.getInstance().getSlider().sliderTalon.getControlMode().isPID()) {
			return false;
		}
		System.out.println("Slider on target: "+slider.onTarget());
		return updated && mState==DistancePositioningState.MOVING &&
				(System.currentTimeMillis() - startTime > 1000) && (robotState.sliderVelocity == 0) && slider.onTarget();
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{Slider.getInstance(), Spatula.getInstance()};
	}

	@Override
	public String getName() {
		return "SliderDistanceCustomPositioningRoutine";
	}

}