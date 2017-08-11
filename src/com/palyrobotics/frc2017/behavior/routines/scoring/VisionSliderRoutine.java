package com.palyrobotics.frc2017.behavior.routines.scoring;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.robot.Robot;
import com.palyrobotics.frc2017.subsystems.Slider;
import com.palyrobotics.frc2017.subsystems.Slider.SliderState;
import com.palyrobotics.frc2017.subsystems.Slider.SliderTarget;
import com.palyrobotics.frc2017.subsystems.Spatula;
import com.palyrobotics.frc2017.subsystems.Subsystem;
import com.palyrobotics.frc2017.vision.VisionData;

import java.util.Optional;

public class VisionSliderRoutine extends Routine {
	private double startTime = 0;
	private double visionSetpoint = 0;

	// Used to make sure vision setpoint is only sent once
	private enum VisionPositioningState {
		START, SENT
	}

	private VisionPositioningState mState = VisionPositioningState.START;

	@Override
	public void start() {
		startTime = System.currentTimeMillis();
	}

	@Override
	public Commands update(Commands commands) {

		commands.robotSetpoints.sliderSetpoint = SliderTarget.CUSTOM;
		commands.wantedSpatulaState = Spatula.SpatulaState.UP;

		switch(mState) {
		case START:
			commands.wantedSliderState = Slider.SliderState.CUSTOM_POSITIONING;

			if(VisionData.getXData().exists()) {
				visionSetpoint = VisionData.getXDataValue();
			} else {
				visionSetpoint = 0.0;
			}

			if(Math.abs(visionSetpoint) > Constants.kMaxVisionXOffset) {
				System.out.println("Faulty Vision X Dist, reverting to hard-coded value");

				//Peg is likely far left, nexus can't see it
				visionSetpoint = -7.0;
			}

			visionSetpoint = Math.max(-7, Math.min(visionSetpoint, 7));

			commands.robotSetpoints.sliderCustomSetpoint =
					Optional.of(visionSetpoint * Constants.kSliderRevolutionsPerInch);
			mState = VisionPositioningState.SENT;
			break;
		case SENT:
			commands.wantedSliderState = Slider.SliderState.CUSTOM_POSITIONING;
			commands.robotSetpoints.sliderCustomSetpoint = Optional.of(visionSetpoint * Constants.kSliderRevolutionsPerInch);
			break;
		}		
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		commands.wantedSliderState = SliderState.IDLE;
		commands.robotSetpoints.sliderCustomSetpoint = Optional.empty();
		return commands;
	}

	@Override
	public boolean finished() {
		return mState==VisionPositioningState.SENT && 
				(System.currentTimeMillis() - startTime > 200) &&
				Robot.getRobotState().sliderVelocity == 0;
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{Slider.getInstance(), Spatula.getInstance()};
	}

	@Override
	public String getName() {
		return "SliderVisionPositioningRoutine";
	}
}