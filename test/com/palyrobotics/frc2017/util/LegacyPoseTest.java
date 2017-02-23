package com.palyrobotics.frc2017.util;

import com.palyrobotics.frc2017.robot.team254.lib.util.LegacyPose;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Nihar on 2/12/17.
 */
public class LegacyPoseTest {
	/**
	 * Test {@link LegacyPose#equals(Object)}
	 * @throws Exception
	 */
	@Test
	public void equalsTest() throws Exception {
		LegacyPose one = new LegacyPose(0, 0, 0, 0, 0, 0);
		LegacyPose two = new LegacyPose(0, 0, 0, 0, 0, 0);
		assertTrue("Zero poses aren't equal!", one.equals(two) && two.equals(one));
	}

	/**
	 * @author Team 254
	 */
	@Test
	public void testZeroRelativePose() {
		LegacyPose base_pose = new LegacyPose(0,0,0,0,0,0);
		LegacyPose.RelativePoseGenerator rel_pose = base_pose.new RelativePoseGenerator();
		LegacyPose new_pose = new LegacyPose(10,11,1,2,5,6);
		LegacyPose diff_pose = rel_pose.get(new_pose);
		assertTrue("Poses should be the same with a zero base", diff_pose.equals(new_pose));
	}

	/**
	 * @author Team 254
	 */
	@Test
	public void testNonZeroRelativePose() {
		LegacyPose base_pose = new LegacyPose(1,2,3,4,5,6);
		LegacyPose.RelativePoseGenerator rel_pose = base_pose.new RelativePoseGenerator();
		LegacyPose new_pose = new LegacyPose(11,12,13,14,15,16);
		LegacyPose diff_pose = rel_pose.get(new_pose);
		LegacyPose expected_pose = new LegacyPose(10,10,13,14,10,16); // uses new pose velocity
		assertTrue("Poses should be the same with a non zero base", diff_pose.equals(expected_pose));

	}
}