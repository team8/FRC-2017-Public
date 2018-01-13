package com.palyrobotics.frc2018.config;

import com.palyrobotics.frc2018.config.dashboard.DashboardManager;

public class Gains {
	// Onboard motion profile aka trajectory follower
	public static double k2018_UnnamedTrajectorykV = 0.077;
	public static double k2018_UnnamedLeftTrajectorykV = 0.0489;
	public static double k2018_UnnamedRightTrajectorykV = 0.0499;
	public static double k2018_UnnamedLeftTrajectorykV_0 = 0.0969;
	public static double k2018_UnnamedRightTrajectorykV_0 = 0.0946;
	public static double k2018_UnnamedTrajectorykA = 0.025;
	
	public static final double k2018_UnnamedDriveVelocitykP = 6.0;
	public static final double k2018_UnnamedDriveVelocitykI = 0.002;
	public static final double k2018_UnnamedDriveVelocitykD = 85;
	public static final double k2018_UnnamedDriveVelocitykF = 2.624;
	public static final int k2018_UnnamedDriveVelocitykIzone = 800;
	public static final double k2018_UnnamedDriveVelocitykRampRate = 0.0;
	public static final Gains unnamedVelocity = new Gains(k2018_UnnamedDriveVelocitykP, k2018_UnnamedDriveVelocitykI, k2018_UnnamedDriveVelocitykD,
			k2018_UnnamedDriveVelocitykF, k2018_UnnamedDriveVelocitykIzone, k2018_UnnamedDriveVelocitykRampRate);


	// Drive Distance PID control loop
	public static final double k2018_UnnamedDriveStraightTurnkP = -0.06;
	public static final double k2018_UnnamedDriveDistancekP = 0.5;
	public static final double k2018_UnnamedDriveDistancekI = 0.0025;
	public static final double k2018_UnnamedDriveDistancekD = 12.0;
	public static final int k2018_UnnamedDriveDistancekIzone = 125;
	public static final double k2018_UnnamedDriveDistancekRampRate = 0.0;
	public static final Gains unnamedDriveDistance = new Gains(k2018_UnnamedDriveDistancekP, k2018_UnnamedDriveDistancekI, k2018_UnnamedDriveDistancekD,
			0, k2018_UnnamedDriveDistancekIzone, k2018_UnnamedDriveDistancekRampRate);

	// Drive Motion Magic offboard control loop
	// Short distance max speed 45 in/s Max accel 95 in/s^2
	public static final double k2018_UnnamedShortDriveMotionMagicCruiseVelocity = 60 * Constants.kDriveSpeedUnitConversion;
	public static final double k2018_UnnamedShortDriveMotionMagicMaxAcceleration = 100 * Constants.kDriveSpeedUnitConversion;
	public static final double k2018_UnnamedShortDriveMotionMagickP = 2.40;
	public static final double k2018_UnnamedShortDriveMotionMagickI = 0.00040;
	public static final double k2018_UnnamedShortDriveMotionMagickD = 275;
	public static final double k2018_UnnamedShortDriveMotionMagickF = 2.075;
	public static final int k2018_UnnamedShortDriveMotionMagickIzone = 150;
	public static final double k2018_UnnamedShortDriveMotionMagickRampRate = 0.0;
	public static final Gains unnamedShortDriveMotionMagicGains = new Gains(k2018_UnnamedShortDriveMotionMagickP, k2018_UnnamedShortDriveMotionMagickI, k2018_UnnamedShortDriveMotionMagickD,
			k2018_UnnamedShortDriveMotionMagickF, k2018_UnnamedShortDriveMotionMagickIzone, k2018_UnnamedShortDriveMotionMagickRampRate);
	
	// Long distance more aggressive, 180 in/s, 120 in/s^2 accel
	public static final double k2018_UnnamedLongDriveMotionMagicCruiseVelocity = 180 * Constants.kDriveSpeedUnitConversion;
	public static final double k2018_UnnamedLongDriveMotionMagicMaxAcceleration = 120 * Constants.kDriveSpeedUnitConversion;
	public static final double k2018_UnnamedLongDriveMotionMagickP = 4.0;
	public static final double k2018_UnnamedLongDriveMotionMagickI = 0.01;
	public static final double k2018_UnnamedLongDriveMotionMagickD = 400;
	public static final double k2018_UnnamedLongDriveMotionMagickF = 2.0;
	public static final int k2018_UnnamedLongDriveMotionMagickIzone = 50;
	public static final double k2018_UnnamedLongDriveMotionMagickRampRate = 0.0;
	public static final Gains unnamedLongDriveMotionMagicGains = new Gains(k2018_UnnamedLongDriveMotionMagickP, k2018_UnnamedLongDriveMotionMagickI, k2018_UnnamedLongDriveMotionMagickD,
			k2018_UnnamedLongDriveMotionMagickF, k2018_UnnamedLongDriveMotionMagickIzone, k2018_UnnamedLongDriveMotionMagickRampRate);

	// Drive Motion Magic turn angle gains
	public static final double k2018_UnnamedTurnMotionMagicCruiseVelocity = 72 * Constants.kDriveSpeedUnitConversion;
	public static final double k2018_UnnamedTurnMotionMagicMaxAcceleration = 36 * Constants.kDriveSpeedUnitConversion;
	public static final double k2018_UnnamedTurnMotionMagickP = 6.0;
	public static final double k2018_UnnamedTurnMotionMagickI = 0.01;
	public static final double k2018_UnnamedTurnMotionMagickD = 210;
	public static final double k2018_UnnamedTurnMotionMagickF = 2.0;
	public static final int k2018_UnnamedTurnMotionMagickIzone = 50;
	public static final double k2018_UnnamedTurnMotionMagickRampRate = 0.0;
	public static final Gains unnamedTurnMotionMagicGains = new Gains(k2018_UnnamedTurnMotionMagickP, k2018_UnnamedTurnMotionMagickI, k2018_UnnamedTurnMotionMagickD,
			k2018_UnnamedTurnMotionMagickF, k2018_UnnamedTurnMotionMagickIzone, k2018_UnnamedTurnMotionMagickRampRate);


	public static class TrajectoryGains {
		public final double P,D,V,A, turnP, turnD;
		public TrajectoryGains(double p, double d, double v, double a, double turnP, double turnD) {
			this.P = p;
			this.D = d;
			this.V = v;
			this.A = a;
			this.turnP = turnP;
			this.turnD = turnD;
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
			System.out.println("Dashboard tuning currently removed");
		}
	}
	
	public static void updateNetworkTableGains() {
		if (DashboardManager.getInstance().pidTuning) {
			System.out.println("Dashboard tuning currently removed");
		}
	}
}
