package com.palyrobotics.frc2017.robot.team254.lib.util;

/**
 * Represents the state of the drivetrain
 */
public class LegacyPose {
	public LegacyPose(double leftDistance, double rightDistance, double leftVelocity,
					  double rightVelocity, double heading, double headingVelocity) {
		this.leftDistance = leftDistance;
		this.rightDistance = rightDistance;
		this.leftVelocity = leftVelocity;
		this.rightVelocity = rightVelocity;
		this.heading = heading;
		this.headingVelocity = headingVelocity;
	}

	public double leftDistance;
	public double rightDistance;
	public double leftVelocity;
	public double rightVelocity;
	public double heading;
	public double headingVelocity;

	/**
	 * Resets the current pose to use new parameters
	 *
	 * Parameters all the components of the pose
	 */
	public void reset(double leftDistance, double rightDistance, double leftVelocity,
					  double rightVelocity, double heading, double headingVelocity) {
		this.leftDistance = leftDistance;
		this.rightDistance = rightDistance;
		this.leftVelocity = leftVelocity;
		this.rightVelocity = rightVelocity;
		this.heading = heading;
		this.headingVelocity = headingVelocity;
	}

	public double getLeftDistance() {
		return leftDistance;
	}

	public double getHeading() {
		return heading;
	}

	public double getRightDistance() {
		return rightDistance;
	}

	public double getLeftVelocity() {
		return leftVelocity;
	}

	public double getRightVelocity() {
		return rightVelocity;
	}

	public double getHeadingVelocity() {
		return headingVelocity;
	}

	public class RelativePoseGenerator {
		private LegacyPose _basePose;

		public RelativePoseGenerator() {
			_basePose = LegacyPose.this;
		}

		/**
		 * Creates a relative pose representing the difference between this pose object
		 * and the pose passed to it
		 *
		 * @author Team 254
		 */
		public LegacyPose get(LegacyPose pose) {
			return new LegacyPose(
					pose.getLeftDistance() - _basePose.getLeftDistance(),
					pose.getRightDistance() - _basePose.getRightDistance(),
					_basePose.getLeftVelocity() - pose.getLeftVelocity(),
					_basePose.getRightVelocity() - pose.getRightVelocity(),
					pose.getHeading() - _basePose.getHeading(),
					_basePose.getHeadingVelocity()
							- pose.getHeadingVelocity());
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LegacyPose))
			return false;
		if (obj == this)
			return true;
		LegacyPose other_pose = (LegacyPose) obj;
		return other_pose.getLeftDistance() == getLeftDistance()
				&& other_pose.getRightDistance() == getRightDistance()
				&& other_pose.getLeftVelocity() == getLeftVelocity()
				&& other_pose.getRightVelocity() == getRightVelocity()
				&& other_pose.getHeading() == getHeading()
				&& other_pose.getHeadingVelocity() == getHeadingVelocity();
	}

	/**
	 * Create a copy of the LegacyPose (to not use the same reference)
	 *
	 * @return A copy of the pose
	 */
	public LegacyPose copy() {
		return new LegacyPose(
				leftDistance,
				rightDistance,
				leftVelocity,
				rightVelocity,
				heading,
				headingVelocity);
	}
}