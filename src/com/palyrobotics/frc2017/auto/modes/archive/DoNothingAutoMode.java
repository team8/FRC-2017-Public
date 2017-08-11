package com.palyrobotics.frc2017.auto.modes.archive;

import com.palyrobotics.frc2017.auto.AutoModeBase;
import com.palyrobotics.frc2017.behavior.Routine;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.subsystems.Subsystem;

public class DoNothingAutoMode extends AutoModeBase {
	@Override
	public Routine getRoutine() {
		return new DoNothingRoutine();
	}

	@Override
	public void prestart() {
		System.out.println("Starting DoNothingAutoMode");
	}

	@Override
	public String toString() {
		return "DoNothing";
	}

	public class DoNothingRoutine extends Routine {
		@Override
		public void start() {

		}

		@Override
		public Commands update(Commands commands) {
			return commands;
		}

		@Override
		public Commands cancel(Commands commands) {
			return commands;
		}

		@Override
		public boolean finished() {
			return false;
		}

		@Override
		public Subsystem[] getRequiredSubsystems() {
			return new Subsystem[0];
		}

		@Override
		public String getName() {
			return "DoNothingRoutine";
		}
	}
}
