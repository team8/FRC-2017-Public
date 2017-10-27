package com.palyrobotics.frc2017.vision;

import com.palyrobotics.frc2017.config.Constants;

/**
 * Created by Alvin on 2/15/2017.
 */
public class AndroidComputerTest {

	public static void main(String[] args) throws InterruptedException {

		VisionManager.getInstance().start(Constants.kAndroidConnectionUpdateRate,true);

//		VisionManager.getInstance().StartVisionApp();
//		String inp = RuntimeExecutor.getInstance().exec("adb devices");
//		String[] lines = inp.split("\\n");
//		boolean hasDevice = false;
//		for(int i=1; i<lines.length && !hasDevice; i++){
//			hasDevice = lines[i].contains("device");
//		}
//		System.out.println(inp);
//		System.out.println(hasDevice);

		while (true) {

			if (VisionManager.getInstance().isAppStarted()) {

				//System.out.println(String.format("X: %b, Y: %b", VisionData.getXData().exists(), VisionData.getZData().exists()));

//				final String x = Double.toString(VisionData.getXData()), y = Double.toString(VisionData.getZData());
//
//				System.out.println(String.format("X: %s, Y: %s", x, y));


			}
			Thread.sleep(500);
		}
	}
}
