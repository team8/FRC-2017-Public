package com.palyrobotics.frc2017.util;

import java.util.Optional;

/**
 * Created by Nihar on 2/12/17.
 * Represents the drivetrain state <br />
 * Holds sensor data through CANTalon and gyroscope <br />
 * Optional used for values that may not always be present
 */
public class Pose {
	public double heading;
	public double headingVelocity;

	public double leftEnc;
	public double leftEncVelocity;
	public double leftSpeed;

	public double rightEnc;
	public double rightEncVelocity;
	public double rightSpeed;

	public Optional<Double> leftError;
	public Optional<Double> rightError;
	
	public Optional<Double> leftMotionMagicPos = Optional.empty();
	public Optional<Double> rightMotionMagicPos = Optional.empty();
	public Optional<Double> leftMotionMagicVel = Optional.empty();
	public Optional<Double> rightMotionMagicVel = Optional.empty();


	public Pose() {
		this.leftEnc = 0; this.leftEncVelocity = 0; this.leftSpeed = 0;
		this.rightEnc = 0; this.rightEncVelocity = 0; this.rightSpeed = 0;
		this.heading = 0; this.headingVelocity = 0;
		this.leftError = Optional.empty();
		this.rightError = Optional.empty();
	}
	public Pose(double leftEnc, double leftEncVelocity, double leftSpeed,
				double rightEnc, double rightEncVelocity, double rightSpeed,
				double leftError, double rightError, double heading, double headingVelocity) {
		this.leftEnc = leftEnc;
		this.leftEncVelocity = leftEncVelocity;
		this.leftSpeed = leftSpeed;
		this.rightEnc = rightEnc;
		this.rightEncVelocity = rightEncVelocity;
		this.rightSpeed = rightSpeed;
		this.heading = heading;
		this.headingVelocity = headingVelocity;
		this.leftError = Optional.of(leftError);
		this.rightError = Optional.of(rightError);
	}
	public Pose(double leftEnc, double leftEncVelocity, double leftSpeed,
				double rightEnc, double rightEncVelocity, double rightSpeed,
				double heading, double headingVelocity) {
		this.leftEnc = leftEnc;
		this.leftEncVelocity = leftEncVelocity;
		this.leftSpeed = leftSpeed;
		this.rightEnc = rightEnc;
		this.rightEncVelocity = rightEncVelocity;
		this.rightSpeed = rightSpeed;
		this.heading = heading;
		this.headingVelocity = headingVelocity;
		this.leftError = Optional.empty();
		this.rightError = Optional.empty();
	}

	// TODO: Copy and equals methods
	public Pose copy() {
		Pose copy = new Pose();
		copy.leftEnc = this.leftEnc;
		copy.leftEncVelocity = this.leftEncVelocity;
		copy.leftSpeed = this.leftSpeed;
		copy.heading = this.heading;
		copy.headingVelocity = this.headingVelocity;
		copy.rightEnc = this.rightEnc;
		copy.rightEncVelocity = this.rightEncVelocity;
		copy.rightSpeed = this.rightSpeed;
		copy.leftError = (this.leftError.isPresent()) ? Optional.of(this.leftError.get()) : Optional.empty();
		copy.rightError = (this.rightError.isPresent()) ? Optional.of(this.rightError.get()) : Optional.empty();
		copy.leftMotionMagicPos = (this.leftMotionMagicPos.isPresent()) ? Optional.of(this.leftMotionMagicPos.get()) : Optional.empty();
		copy.rightMotionMagicPos = (this.rightMotionMagicPos.isPresent()) ? Optional.of(this.rightMotionMagicPos.get()) : Optional.empty();
		copy.leftMotionMagicVel = (this.leftMotionMagicVel.isPresent()) ? Optional.of(this.leftMotionMagicVel.get()) : Optional.empty();
		copy.rightMotionMagicVel = (this.rightMotionMagicVel.isPresent()) ? Optional.of(this.rightMotionMagicVel.get()) : Optional.empty();

		return copy;
	}
	
	public boolean equals(Pose other) {
		return this.leftEnc == other.leftEnc &&
				this.leftEncVelocity == other.leftEncVelocity &&
				this.leftSpeed == other.leftSpeed &&
				this.rightEnc == other.rightEnc &&
				this.rightEncVelocity == other.rightEncVelocity &&
				this.rightSpeed == other.rightSpeed &&
				this.leftError.equals(other.leftError) &&
				this.rightError.equals(other.rightError) &&
				this.heading == other.heading &&
				this.headingVelocity == other.headingVelocity &&
				this.leftMotionMagicPos == other.leftMotionMagicPos &&
				this.rightMotionMagicPos == other.rightMotionMagicPos &&
				this.leftMotionMagicVel == other.leftMotionMagicVel &&
				this.rightMotionMagicVel == other.rightMotionMagicVel;
	}
}