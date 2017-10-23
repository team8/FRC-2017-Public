package com.palyrobotics.frc2017.behavior.routines.scoring;

import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.robot.Robot;
import com.palyrobotics.frc2017.subsystems.Slider;
import com.palyrobotics.frc2017.subsystems.Spatula;
import com.palyrobotics.frc2017.subsystems.Subsystem;
import com.palyrobotics.frc2017.vision.VisionData;

import java.util.Arrays;
import java.util.Optional;

/**
 * Moves slider to 3 positions to sample vision data and finds the best target
 * @author Nihar, Alvin
 *
 */
public class MultiSampleVisionSliderRoutine extends Routine {
	private enum SamplingState {
		LEFT, CENTER, RIGHT, SCORE
	}
	private SamplingState mState;
	private boolean newState = false;
	private double startTime = 0;
	private double[] visionSetpoints = new double[3];
	private double threshold = 1;

	@Override
	public void start() {
		mState = SamplingState.LEFT;
		startTime = System.currentTimeMillis();
		newState = true;
	}

	@Override
	public Commands update(Commands commands) {
		switch (mState) {
			case LEFT:
				if (newState) {
					commands.robotSetpoints.sliderCustomSetpoint = Optional.of(-7.0);
					commands.wantedSliderState = Slider.SliderState.CUSTOM_POSITIONING;
					startTime = System.currentTimeMillis();
					newState = false;
				}
				if (Robot.getRobotState().sliderVelocity==0 && System.currentTimeMillis()-startTime > 200) {
					visionSetpoints[0] = VisionData.getXDataValue();
					mState = SamplingState.CENTER;
					newState = true;
				}
				break;
			case CENTER:
				if (newState) {
					commands.robotSetpoints.sliderCustomSetpoint = Optional.of(0.0);
					commands.wantedSliderState = Slider.SliderState.CUSTOM_POSITIONING;
					startTime = System.currentTimeMillis();
					newState = false;
				}
				if (Robot.getRobotState().sliderVelocity==0 && System.currentTimeMillis()-startTime > 200) {					
					mState = SamplingState.RIGHT;
					visionSetpoints[1] = VisionData.getXDataValue();
					mState = SamplingState.RIGHT;
					newState = true;
				}
				break;
			case RIGHT:
				if (newState) {
					commands.robotSetpoints.sliderCustomSetpoint = Optional.of(7.0);
					commands.wantedSliderState = Slider.SliderState.CUSTOM_POSITIONING;
					startTime = System.currentTimeMillis();
					newState = false;
				}
				if (Robot.getRobotState().sliderVelocity==0 && System.currentTimeMillis()-startTime > 200) {					
					mState = SamplingState.SCORE;
					visionSetpoints[2] = VisionData.getXDataValue();
					mState = SamplingState.SCORE;
					newState = true;
				}
				break;
			case SCORE:
				commands.wantedSliderState = Slider.SliderState.CUSTOM_POSITIONING;
				if (newState) {
					newState = false;
					Arrays.sort(visionSetpoints);
					startTime = System.currentTimeMillis();
					System.out.println("Vision setpoints: " + Arrays.toString(visionSetpoints));
					if (visionSetpoints[1] - visionSetpoints[0] < threshold) {
						double setpoint = (visionSetpoints[1] + visionSetpoints[0]) / 2;
						System.out.println("Chosen one: " + setpoint);
						commands.robotSetpoints.sliderCustomSetpoint = Optional.of(setpoint);
						break;
					}
					else if (visionSetpoints[2] - visionSetpoints[1] > threshold) {
						double setpoint = (visionSetpoints[2] + visionSetpoints[1]) / 2;
						System.out.println("Chosen one: " + setpoint);
						commands.robotSetpoints.sliderCustomSetpoint = Optional.of(setpoint);
						break;
					}
					// good value on right side but out of bounds on left side
					else if (visionSetpoints[0] <= -7 && visionSetpoints[2] < 7 && visionSetpoints[2] > -7) {
						double setpoint = -7;
						System.out.println("Chosen one: " + setpoint);
						commands.robotSetpoints.sliderCustomSetpoint = Optional.of(setpoint);
						break;
					}
					// good value on left side but out of bounds on right side
					else if (visionSetpoints[2] >= 7 && visionSetpoints[0] < 7 && visionSetpoints[0] > -7) {
						double setpoint = 7;
						System.out.println("Chosen one: " + setpoint);
						commands.robotSetpoints.sliderCustomSetpoint = Optional.of(setpoint);
						break;
					}
				}
			}
			System.out.println(commands.robotSetpoints.sliderCustomSetpoint.get());
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		commands.wantedSliderState = Slider.SliderState.IDLE;
		commands.robotSetpoints.sliderCustomSetpoint = Optional.empty();
		return commands;
	}

	@Override
	public boolean finished() {
		return mState==SamplingState.SCORE &&
				Robot.getRobotState().sliderVelocity==0 &&
				System.currentTimeMillis() - startTime > 200;
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[]{Slider.getInstance(), Spatula.getInstance()};
	}

	@Override
	public String getName() {
		return "MultiSampleVisionSliderRoutine";
	}
}
