package com.palyrobotics.frc2018.vision.util.commandline;

import com.palyrobotics.frc2018.config.Constants;
import com.palyrobotics.frc2018.util.logger.Logger;

import java.util.logging.Level;

import org.spectrum3847.RIOdroid.RIOdroid;

public class CommandExecutor{
	 private static boolean isTesting = false;
	 private static boolean isFlashOn = false;

	 public static String visionInit(){
	 	return exec("adb shell am start -n " + Constants.kPackageName + "/" +
				Constants.kPackageName + "." + Constants.kActivityName);
	 }
	 
	 public static void setTesting(boolean testing) {
		 isTesting = testing;
	 }

	 public static void adbServerInit() {
		 Logger.getInstance().logRobotThread(Level.INFO, "Initializing RIODroid...");

		 if (!isTesting) {
		 	RIOdroid.init();
		 } else {
		 	RuntimeExecutor.getInstance().init();
		 }

//		 System.out.println("[Info] TCP Reversing ports...");

		 exec("adb reverse tcp:" +
				 Constants.kVideoPort + " tcp:" +
				 Constants.kVideoPort);

		 exec("adb reverse tcp:" +
				 Constants.kVisionDataPort + " tcp:" +
				 Constants.kVisionDataPort);

//		 System.out.println("[Info] Starting Video Manager...");
	 }

	/**
	 * Detects if the nexus is connected through the command "adb devices" and looking at the output
	 *
	 * @return Whether or not a nexus device is connected
	 */
	public static boolean isNexusConnected() {

		//System.out.println("[Info] Trying to find nexus...");

		boolean hasDevice = false;
		String[] devicesOutput = RuntimeExecutor.getInstance().exec("adb devices").split("\\n");
//		System.out.println("[Info] Restarting server...");

		for(int i = 1; i < devicesOutput.length && !hasDevice; i++) {
			hasDevice = devicesOutput[i].contains("device");
		}

		//if (!hasDevice) System.out.println("[Warning] No device found with USB scan!");

		return hasDevice;
	}

	 public static void restartAdbServer() {

		 String restartOut;
		 do {
			 Logger.getInstance().logRobotThread(Level.INFO, "Restarting server...");

			 Logger.getInstance().logRobotThread(Level.FINER, exec("adb kill-server"));
			 restartOut = exec("adb start-server");

			 Logger.getInstance().logRobotThread(Level.FINER, restartOut);
		 }
		 while(!restartOut.contains("daemon started successfully"));
	 }
	 
	 public static String toggleFlash(){
		return exec("adb shell am broadcast -a "+Constants.kPackageName
				+".GET_DATA --es type flash --ez isFlash "+isFlashOn);
	 }
	 
	 public static String catFile(String fileName){
	 	return exec("adb shell run-as "+Constants.kPackageName+
				" cat /data/data/"+ Constants.kPackageName + "/files/" + fileName);
	 }

	/**
	 * @returns the PID of the vision app
	 */
	public static String appPID(){
	 	return exec("adb shell pidof " + Constants.kPackageName);

	}
	 
	 public static String exec(String string){
		 if(!isTesting){
			 return RIOdroid.executeCommand(string);
		 }
		 else{
			 return RuntimeExecutor.getInstance().exec(string);
		 }
	 }
	 

}
