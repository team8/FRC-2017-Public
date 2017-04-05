package com.palyrobotics.frc2017.auto;

import com.team254.lib.trajectory.Path;
import org.junit.Test;

/**
 * Created by Nihar on 4/5/17.
 */
public class AutoPathLoaderTest {
	@Test
	public void testLoading() throws Exception {
		AutoPathLoader.loadPaths();
		Path path = AutoPathLoader.get(AutoPathLoader.kPathNames[1]);
		System.out.println("Printing sample trajectory");
		System.out.println(path.getLeftWheelTrajectory().toString());
	}
}
