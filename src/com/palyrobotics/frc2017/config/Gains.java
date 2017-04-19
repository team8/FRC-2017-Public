package com.palyrobotics.frc2017.config;

import com.palyrobotics.frc2017.config.dashboard.DashboardManager;

public class Gains {
	/*
	 * STEIK
	 */
	// Onboard motion profile aka trajectory follower

	public static double kSteikTrajectorykP = 1; //0.7, 0.8, 1.00, 0.05
	public static double kSteikTrajectorykD = 0.2; // 0.1, 0.015
	public static double kSteikTrajectorykV = 0.069;
	public static double kSteikTrajectorykA = 0.037;
	public static double kSteikTrajectoryTurnkP = 0.01;//0.01; //-0.01
	public static double kSteikTrajectoryTurnkD = 0.0;
	public static final Gains.TrajectoryGains kTrajectoryGains = new TrajectoryGains(kSteikTrajectorykP,
			kSteikTrajectorykD, kSteikTrajectorykV, kSteikTrajectorykA);
	public static final double kSteikTrajectoryStraightkP = 0.38; // 1
	public static final double kSteikTrajectoryStraightkD = 0.0; // 0.02
	public static final TrajectoryGains kStraightTrajectoryGains = new TrajectoryGains(kSteikTrajectoryStraightkP,
			kSteikTrajectoryStraightkD, kSteikTrajectorykV, kSteikTrajectorykA);

	// Drive Distance PID control loop
	public static final double kSteikDriveStraightTurnkP = -0.06;
	public static final double kSteikDriveDistancekP = 0.5;
	public static final double kSteikDriveDistancekI = 0.0025;
	public static final double kSteikDriveDistancekD = 12.0;
	public static final int kSteikDriveDistancekIzone = 125;
	public static final double kSteikDriveDistancekRampRate = 0.0;
	public static final Gains steikDriveDistance = new Gains(kSteikDriveDistancekP, kSteikDriveDistancekI, kSteikDriveDistancekD,
			0, kSteikDriveDistancekIzone, kSteikDriveDistancekRampRate);

	// Drive Velocity offboard control loop
	public static final double kSteikDriveVelocitykP = 6.0;
	public static final double kSteikDriveVelocitykI = 0.002;
	public static final double kSteikDriveVelocitykD = 85;
	public static final double kSteikDriveVelocitykF = 2.624;
	public static final int kSteikDriveVelocitykIzone = 800;
	public static final double kSteikDriveVelocitykRampRate = 0.0;
	public static final Gains steikVelocity = new Gains(kSteikDriveVelocitykP, kSteikDriveVelocitykI, kSteikDriveVelocitykD,
			kSteikDriveVelocitykF, kSteikDriveVelocitykIzone, kSteikDriveVelocitykRampRate);

	// Drive Motion Magic offboard control loop
	// Short distance max speed 45 in/s Max accel 95 in/s^2
	public static final double kSteikShortDriveMotionMagicCruiseVelocity = 45 * Constants.kDriveSpeedUnitConversion;
	public static final double kSteikShortDriveMotionMagicMaxAcceleration = 95 * Constants.kDriveSpeedUnitConversion;
	public static final double kSteikShortDriveMotionMagickP = 2.5;
	public static final double kSteikShortDriveMotionMagickI = 0.01;
	public static final double kSteikShortDriveMotionMagickD = 250;
	public static final double kSteikShortDriveMotionMagickF = 2;
	public static final int kSteikShortDriveMotionMagickIzone = 50;
	public static final double kSteikShortDriveMotionMagickRampRate = 0.0;
	public static final Gains steikShortDriveMotionMagicGains = new Gains(kSteikShortDriveMotionMagickP, kSteikShortDriveMotionMagickI, kSteikShortDriveMotionMagickD,
			kSteikShortDriveMotionMagickF, kSteikShortDriveMotionMagickIzone, kSteikShortDriveMotionMagickRampRate);
	
	// Long distance more aggressive, 180 in/s, 120 in/s^2 accel
	public static final double kSteikLongDriveMotionMagicCruiseVelocity = 180 * Constants.kDriveSpeedUnitConversion;
	public static final double kSteikLongDriveMotionMagicMaxAcceleration = 120 * Constants.kDriveSpeedUnitConversion;
	public static final double kSteikLongDriveMotionMagickP = 4.0;
	public static final double kSteikLongDriveMotionMagickI = 0.01;
	public static final double kSteikLongDriveMotionMagickD = 400;
	public static final double kSteikLongDriveMotionMagickF = 2.0;
	public static final int kSteikLongDriveMotionMagickIzone = 50;
	public static final double kSteikLongDriveMotionMagickRampRate = 0.0;
	public static final Gains steikLongDriveMotionMagicGains = new Gains(kSteikLongDriveMotionMagickP, kSteikLongDriveMotionMagickI, kSteikLongDriveMotionMagickD,
			kSteikLongDriveMotionMagickF, kSteikLongDriveMotionMagickIzone, kSteikLongDriveMotionMagickRampRate);

	// Drive Motion Magic turn angle gains
	public static final double kSteikTurnMotionMagicCruiseVelocity = 72 * Constants.kDriveSpeedUnitConversion;
	public static final double kSteikTurnMotionMagicMaxAcceleration = 36 * Constants.kDriveSpeedUnitConversion;
	public static final double kSteikTurnMotionMagickP = 6.0;
	public static final double kSteikTurnMotionMagickI = 0.01;
	public static final double kSteikTurnMotionMagickD = 210;
	public static final double kSteikTurnMotionMagickF = 2.0;
	public static final int kSteikTurnMotionMagickIzone = 50;
	public static final double kSteikTurnMotionMagickRampRate = 0.0;
	public static final Gains steikTurnMotionMagicGains = new Gains(kSteikTurnMotionMagickP, kSteikTurnMotionMagickI, kSteikTurnMotionMagickD,
			kSteikTurnMotionMagickF, kSteikTurnMotionMagickIzone, kSteikTurnMotionMagickRampRate);

	// Slider motion magic offboard control loop

	// Slider position offboard control loop
	public static final double kSteikSliderEncoderkP = 0.8;
	public static final double kSteikSliderEncoderkI = 0.0066;
	public static final double kSteikSliderEncoderkD = 8;
	public static final double kSteikSliderEncoderkF = 0;
	public static final int kSteikSliderEncoderkIzone = 120;
	public static final double kSteikSliderEncoderkRampRate = 0;
	public static final Gains steikSliderEncoder = new Gains(kSteikSliderEncoderkP, kSteikSliderEncoderkI, kSteikSliderEncoderkD,
			kSteikSliderEncoderkF, kSteikSliderEncoderkIzone, kSteikSliderEncoderkRampRate);

	// Slider potentiometer position onboard control loop
	public static final double kSteikSliderPotentiometerkP = 0.1;
	public static final double kSteikSliderPotentiometerkI = 0;
	public static final double kSteikSliderPotentiometerkD = 0;
	public static final double kSteikSliderPotentiometerkF = 0;
	public static final int kSteikSliderPotentiometerkIzone = 0;
	public static final double kSteikSliderPotentiometerkRampRate = 0;
	public static final Gains steikSliderPotentiometer = new Gains(kSteikSliderPotentiometerkP, kSteikSliderPotentiometerkI, kSteikSliderPotentiometerkD,
			kSteikSliderPotentiometerkF, kSteikSliderPotentiometerkIzone, kSteikSliderPotentiometerkRampRate);

	/*
	 * DERICA
	 */
	// Position control loop
	public static final double kDericaPositionkP = 0.4;
	public static final double kDericaPositionkI = 0;
	public static final double kDericaPositionkD = 4;
	public static final double kDericaPositionkF = 0;
	public static final int kDericaPositionkIzone = 0;
	public static final double kDericaPositionkRampRate = 0.0;
	public static final Gains dericaPosition = new Gains(kDericaPositionkP, kDericaPositionkI, kDericaPositionkD,
			kDericaPositionkF, kDericaPositionkIzone, kDericaPositionkRampRate);

	// Velocity control loop
	public static final double kDericaVelocitykP = 3.0;
	public static final double kDericaVelocitykI = 0;
	public static final double kDericaVelocitykD = 50.0;
	public static final double kDericaVelocitykF = 2.122;
	public static final int kDericaVelocitykIzone = 0;
	public static final double kDericaVelocitykRampRate = 0.0;
	public static final Gains dericaVelocity = new Gains(kDericaVelocitykP, kDericaVelocitykI, kDericaVelocitykD,
			kDericaVelocitykF, kDericaVelocitykIzone, kDericaVelocitykRampRate);
	
	public static final double kDericaTurnMotionMagicCruiseVelocity = 1;
	public static final double kDericaTurnMotionMagicCruiseAccel = 1;

	public static class TrajectoryGains {
		public final double P,D,V,A;
		public TrajectoryGains(double p, double d, double v, double a) {
			this.P = p;
			this.D = d;
			this.V = v;
			this.A = a;
		}
	}

	public final double P,I,D, F, rampRate;
	public final int izone;

	public Gains(double p, double i, double d, double f, int izone, double rampRate) {
		this.P = p;
		this.I = i;
		this.D = d;
		this.F = f;
		this.izone = izone;
		this.rampRate = rampRate;
	}

	@Override
	public boolean equals(Object other) {
		return ((Gains)other).P == this.P &&
				((Gains) other).I == this.I &&
				((Gains) other).D == this.D &&
				((Gains) other).F == this.F &&
				((Gains) other).izone == this.izone &&
				((Gains) other).rampRate == this.rampRate;
	}
	
	public static void initNetworkTableGains() {
		if (DashboardManager.getInstance().pidTuning) {
			DashboardManager.getInstance().robotTable.putNumber("kP", kSteikTrajectorykP);
			DashboardManager.getInstance().robotTable.putNumber("kD", kSteikTrajectorykD);
			DashboardManager.getInstance().robotTable.putNumber("kV", kSteikTrajectorykV);
			DashboardManager.getInstance().robotTable.putNumber("kA", kSteikTrajectorykA);
			DashboardManager.getInstance().robotTable.putNumber("TurnKp", kSteikTrajectoryTurnkP);
		}
	}
	
	public static void updateNetworkTableGains() {
		if (DashboardManager.getInstance().pidTuning) {
			kSteikTrajectorykP = DashboardManager.getInstance().robotTable.getNumber("kP", kSteikTrajectorykP);
			kSteikTrajectorykD = DashboardManager.getInstance().robotTable.getNumber("kD", kSteikTrajectorykD);
			kSteikTrajectorykV = DashboardManager.getInstance().robotTable.getNumber("kV", kSteikTrajectorykV);
			kSteikTrajectorykA = DashboardManager.getInstance().robotTable.getNumber("kA", kSteikTrajectorykA);
			kSteikTrajectoryTurnkP = DashboardManager.getInstance().robotTable.getNumber("TurnkP", kSteikTrajectoryTurnkP);
		}
	}
}
