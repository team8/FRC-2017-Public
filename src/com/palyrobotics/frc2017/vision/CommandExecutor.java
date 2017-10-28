package com.palyrobotics.frc2017.vision;

import org.spectrum3847.RIOdroid.RIOdroid;

import com.palyrobotics.frc2017.config.Constants;

public class CommandExecutor{
	//these should probably be set as something else to start with?
	 static boolean isTesting = false;
	 static boolean isFlashOn = false;
	 
	 static String visionInit(){
	 	return exec("adb shell am start -n " + Constants.kPackageName + "/" +
				Constants.kPackageName + "." + Constants.kActivityName);
	 }
	 
	 static void setTesting(boolean testing) {
		 isTesting = testing;
	 }

	 static void adbServerInit() {
	 	 // Initializes RIOdroid usb and RIOadb adb daemon
		 // Port forwards so that PC can accept connection from android

		 System.out.println("[Info] Initializing RIODroid...");

		 if (!isTesting) {
		 	RIOdroid.init();
		 } else {
		 	RuntimeExecutor.getInstance().init();
		 }

		 System.out.println("[Info] TCP Reversing ports...");

		 exec("adb reverse tcp:" +
				 Constants.kVideoPort + " tcp:" +
				 Constants.kVideoPort);

		 exec("adb reverse tcp:" +
				 Constants.kVisionDataPort + " tcp:" +
				 Constants.kVisionDataPort);

		 System.out.println("[Info] Starting Video Manager...");
	 }

	/**
	 * Detects if the nexus is connected through the command "adb devices" and looking at the output
	 *
	 * @return Whether or not a nexus device is connected
	 */
	public static boolean isNexusConnected() {

		System.out.println("[Info] Trying to find nexus...");

		boolean hasDevice = false;
		String[] devicesOutput = RuntimeExecutor.getInstance().exec("adb devices").split("\\n");

		for(int i = 1; i < devicesOutput.length && !hasDevice; i++) {
			hasDevice = devicesOutput[i].contains("device");
		}

		if (!hasDevice) System.out.println("[Warning] No device found with USB scan!");

		return hasDevice;
	}

	 static void restartAdbServer() {

		 String restartOut;
		 do {
			 System.out.println("[Info] Restarting server...");

			 System.out.println(exec("adb kill-server"));
			 restartOut = exec("adb start-server");

			 System.out.println(restartOut);
		 }
		 while(!restartOut.contains("daemon started successfully"));
	 }
	 
	 static String toggleFlash(){
		return exec("adb shell am broadcast -a "+Constants.kPackageName
				+".GET_DATA --es type flash --ez isFlash "+isFlashOn);
	 }
	 
	 //first version that seemed to be used
	 static String catFile(String fileName){
	 	return exec("adb shell run-as "+Constants.kPackageName+
				" cat /data/data/"+ Constants.kPackageName + "/files/" + fileName);
	 }

	/**
	 * @returns the PID of the vision app
	 */
	static String appPID(){
	 	return exec("adb shell pidof " + Constants.kPackageName);

	}
	 
	 //second version that takes in a string
	 //can be used in different versions
	 static String exec(String string){
		 if(!isTesting){
			 return RIOdroid.executeCommand(string);
		 }
		 else{
			 return RuntimeExecutor.getInstance().exec(string);
		 }
	 }
	 

}
