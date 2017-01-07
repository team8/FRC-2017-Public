package com.palyrobotics.frc2016.robot.team254.lib.util;

public class Pose {
	public Pose(double leftDistance, double rightDistance, double leftVelocity,
				double rightVelocity, double heading, double headingVelocity) {
		this.m_left_distance = leftDistance;
		this.m_right_distance = rightDistance;
		this.m_left_velocity = leftVelocity;
		this.m_right_velocity = rightVelocity;
		this.m_heading = heading;
		this.m_heading_velocity = headingVelocity;
	}

	public double m_left_distance;
	public double m_right_distance;
	public double m_left_velocity;
	public double m_right_velocity;
	public double m_heading;
	public double m_heading_velocity;

	/**
	 * Resets the current pose to use new parameters
	 *
	 * @param All the components of the pose
	 */
	public void reset(double leftDistance, double rightDistance, double leftVelocity,
					  double rightVelocity, double heading, double headingVelocity) {
		this.m_left_distance = leftDistance;
		this.m_right_distance = rightDistance;
		this.m_left_velocity = leftVelocity;
		this.m_right_velocity = rightVelocity;
		this.m_heading = heading;
		this.m_heading_velocity = headingVelocity;
	}

	public double getLeftDistance() {
		return m_left_distance;
	}

	public double getHeading() {
		return m_heading;
	}

	public double getRightDistance() {
		return m_right_distance;
	}

	public double getLeftVelocity() {
		return m_left_velocity;
	}

	public double getRightVelocity() {
		return m_right_velocity;
	}

	public double getHeadingVelocity() {
		return m_heading_velocity;
	}

	public class RelativePoseGenerator {
		private Pose m_base_pose;

		public RelativePoseGenerator() {
			m_base_pose = Pose.this;
		}

		/**
		 * Creates a relative pose representing the difference between this pose object
		 * and the pose passed to it
		 *
		 * @author Team 254
		 */
		public Pose get(Pose pose) {
			return new Pose(
					pose.getLeftDistance() - m_base_pose.getLeftDistance(),
					pose.getRightDistance() - m_base_pose.getRightDistance(),
					m_base_pose.getLeftVelocity() - pose.getLeftVelocity(),
					m_base_pose.getRightVelocity() - pose.getRightVelocity(),
					pose.getHeading() - m_base_pose.getHeading(),
					m_base_pose.getHeadingVelocity()
							- pose.getHeadingVelocity());
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pose))
			return false;
		if (obj == this)
			return true;
		Pose other_pose = (Pose) obj;
		return other_pose.getLeftDistance() == getLeftDistance()
				&& other_pose.getRightDistance() == getRightDistance()
				&& other_pose.getLeftVelocity() == getLeftVelocity()
				&& other_pose.getRightVelocity() == getRightVelocity()
				&& other_pose.getHeading() == getHeading()
				&& other_pose.getHeadingVelocity() == getHeadingVelocity();
	}

	/**
	 * Create a copy of the Pose (to not use the same reference)
	 *
	 * @return A copy of the pose
	 */
	public Pose copy() {
		return new Pose(
				m_left_distance,
				m_right_distance,
				m_left_velocity,
				m_right_velocity,
				m_heading,
				m_heading_velocity);
	}
}