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

	public Optional<Integer> leftError;
	public Optional<Integer> rightError;

	public Pose() {
		this.leftEnc = 0; this.leftEncVelocity = 0; this.leftSpeed = 0;
		this.rightEnc = 0; this.rightEncVelocity = 0; this.rightSpeed = 0;
		this.heading = 0; this.headingVelocity = 0;
	}
	public Pose(double leftEnc, double leftEncVelocity, double leftSpeed,
				double rightEnc, double rightEncVelocity, double rightSpeed,
				int leftError, int rightError, double heading, double headingVelocity) {
		this.leftEnc = leftEnc;
		this.leftEncVelocity = leftEncVelocity;
		this.leftSpeed = leftSpeed;
		this.rightEnc = rightEnc;
		this.rightEncVelocity = rightEncVelocity;
		this.rightSpeed = rightSpeed;
		this.leftError = Optional.of(leftError);
		this.rightError = Optional.of(rightError);
		this.heading = heading;
		this.headingVelocity = headingVelocity;
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
	}

	// TODO: Copy and equals methods
	public Pose copy() {
		Pose copy = new Pose();
		copy.leftEnc = this.leftEnc;
		copy.leftEncVelocity = this.leftEncVelocity;
		copy.leftSpeed = this.leftSpeed;

		copy.rightEnc = this.rightEnc;
		copy.rightEncVelocity = this.rightEncVelocity;
		copy.rightSpeed = this.rightSpeed;
		copy.leftError = (this.leftError.isPresent()) ? Optional.of(this.leftError.get()) : Optional.empty();
		copy.rightError = (this.rightError.isPresent()) ? Optional.of(this.rightError.get()) : Optional.empty();
		return copy;
	}
}