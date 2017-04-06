package com.palyrobotics.frc2017.vision;

/**
 * Created by Alvin on 2/15/2017.
 */
public class AndroidComputerTest {

	public static void main(String[] args){
//		AndroidConnectionHelper.getInstance().start(true, AndroidConnectionHelper.StreamState.JSON);
//		AndroidConnectionHelper.getInstance().StartVisionApp();
		String inp = RuntimeExecutor.getInstance().exec("adb devices");
		String[] lines = inp.split("\\n");
		System.out.println(inp);
		System.out.println(lines.length > 1);
	}

}
