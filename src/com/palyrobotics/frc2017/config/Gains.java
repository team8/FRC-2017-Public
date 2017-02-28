package com.palyrobotics.frc2017.config;

public class Gains {
	/*
	 * STEIK
	 */
	// Drive Position offboard control loop
	public static final double kSteikDriveDistancekP = 0;
	public static final double kSteikDriveDistancekI = 0;
	public static final double kSteikDriveDistancekD = 0;
	public static final double kSteikDriveDistancekF = 0;
	public static final int kSteikDriveDistancekIzone = 0;
	public static final double kSteikDriveDistancekRampRate = 0.0;

	// Drive Velocity offboard control loop
	public static final double kSteikDriveVelocitykP = 0.0;
	public static final double kSteikDriveVelocitykI = 0;
	public static final double kSteikDriveVelocitykD = 0.0;
	public static final double kSteikDriveVelocitykF = 0;
	public static final int kSteikDriveVelocitykIzone = 0;
	public static final double kSteikDriveVelocitykRampRate = 0.0;

	// Slider motion magic offboard control loop
	
	// Slider position offboard control loop
	public static final double kSteikSliderEncoderkP = 0.8;
	public static final double kSteikSliderEncoderkI = 0.0066;
	public static final double kSteikSliderEncoderkD = 8;
	public static final double kSteikSliderEncoderkF = 0;
	public static final int kSteikSliderEncoderkIzone = 120;
	public static final double kSteikSliderEncoderkRampRate = 0;

	// Slider potentiometer position onboard control loop
	public static final double kSteikSliderPotentiometerkP = 0.1;
	public static final double kSteikSliderPotentiometerkI = 0;
	public static final double kSteikSliderPotentiometerkD = 0;
	public static final double kSteikSliderPotentiometerkF = 0;
	public static final int kSteikSliderPotentiometerkIzone = 0;
	public static final double kSteikSliderPotentiometerkRampRate = 0;

	/*
	 * AEGIR
	 */
	// Drive Position offboard control loop
	public static final double kAegirDriveDistancekP = 0.1;
	public static final double kAegirDriveDistancekI = 0;
	public static final double kAegirDriveDistancekD = 0;
	public static final double kAegirDriveDistancekF = 0;
	public static final int kAegirDriveDistancekIzone = 0;
	public static final double kAegirDriveDistancekRampRate = 0.0;

	// Drive Velocity offboard control loop
	public static final double kAegirDriveVelocitykP = 0.0;
	public static final double kAegirDriveVelocitykI = 0;
	public static final double kAegirDriveVelocitykD = 0.0;
	public static final double kAegirDriveVelocitykF = 0;
	public static final int kAegirDriveVelocitykIzone = 0;
	public static final double kAegirDriveVelocitykRampRate = 0.0;
	
	// Slider motion magic offboard control loop

	// Slider position offboard control loop
	public static final double kAegirSliderEncoderkP = 0.8;
	public static final double kAegirSliderEncoderkI = 0.0066;
	public static final double kAegirSliderEncoderkD = 8;
	public static final double kAegirSliderEncoderkF = 0;
	public static final int kAegirSliderEncoderkIzone = 120;
	public static final double kAegirSliderEncoderkRampRate = 0;

	// Slider potentiometer position onboard control loop
	public static final double kAegirSliderPotentiometerkP = 0.1;
	public static final double kAegirSliderPotentiometerkI = 0;
	public static final double kAegirSliderPotentiometerkD = 0;
	public static final double kAegirSliderPotentiometerkF = 0;
	public static final int kAegirSliderPotentiometerkIzone = 0;
	public static final double kAegirSliderPotentiometerkRampRate = 0;

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

	// Velocity control loop
	public static final double kDericaVelocitykP = 3.0;
	public static final double kDericaVelocitykI = 0;
	public static final double kDericaVelocitykD = 50.0;
	public static final double kDericaVelocitykF = 2.122;
	public static final int kDericaVelocitykIzone = 0;
	public static final double kDericaVelocitykRampRate = 0.0;

	public final double P,I,D, F, rampRate;
	public final int izone;

	public static final Gains steikPosition = new Gains(kSteikDriveDistancekP, kSteikDriveDistancekI, kSteikDriveDistancekD,
			kSteikDriveDistancekF, kSteikDriveDistancekIzone, kSteikDriveDistancekRampRate);
	public static final Gains steikVelocity = new Gains(kSteikDriveVelocitykP, kSteikDriveVelocitykI, kSteikDriveVelocitykD,
			kSteikDriveVelocitykF, kSteikDriveVelocitykIzone, kSteikDriveVelocitykRampRate);
	public static final Gains steikSliderEncoder = new Gains(kSteikSliderEncoderkP, kSteikSliderEncoderkI, kSteikSliderEncoderkD,
			kSteikSliderEncoderkF, kSteikSliderEncoderkIzone, kSteikSliderEncoderkRampRate);
	public static final Gains steikSliderPotentiometer = new Gains(kSteikSliderPotentiometerkP, kSteikSliderPotentiometerkI, kSteikSliderPotentiometerkD,
			kSteikSliderPotentiometerkF, kSteikSliderPotentiometerkIzone, kSteikSliderPotentiometerkRampRate);
	
	public static final Gains aegirPosition = new Gains(kAegirDriveDistancekP, kAegirDriveDistancekI, kAegirDriveDistancekD,
			kAegirDriveDistancekF, kAegirDriveDistancekIzone, kAegirDriveDistancekRampRate);
	public static final Gains aegirVelocity = new Gains(kAegirDriveVelocitykP, kAegirDriveVelocitykI, kAegirDriveVelocitykD,
			kAegirDriveVelocitykF, kAegirDriveVelocitykIzone, kAegirDriveVelocitykRampRate);
	public static final Gains aegirSliderEncoder = new Gains(kAegirSliderEncoderkP, kAegirSliderEncoderkI, kAegirSliderEncoderkD,
			kAegirSliderEncoderkF, kAegirSliderEncoderkIzone, kAegirSliderEncoderkRampRate);
	public static final Gains aegirSliderPotentiometer = new Gains(kAegirSliderPotentiometerkP, kAegirSliderPotentiometerkI, kAegirSliderPotentiometerkD,
			kAegirSliderPotentiometerkF, kAegirSliderPotentiometerkIzone, kAegirSliderPotentiometerkRampRate);
	
	public static final Gains dericaPosition = new Gains(kDericaPositionkP, kDericaPositionkI, kDericaPositionkD,
			kDericaPositionkF, kDericaPositionkIzone, kDericaPositionkRampRate);
	public static final Gains dericaVelocity = new Gains(kDericaVelocitykP, kDericaVelocitykI, kDericaVelocitykD,
			kDericaVelocitykF, kDericaVelocitykIzone, kDericaVelocitykRampRate);

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
}
