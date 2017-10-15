package com.palyrobotics.frc2017.vision;

import org.spectrum3847.RIOdroid.RIOdroid;

import com.palyrobotics.frc2017.config.Constants;

interface CommandExecutor{
	//these should probably be set as something else to start with?
	 static boolean isTesting = false;
	 static boolean isFlashOn = false;
	 
	 static String visionInit(){
		 if(!isTesting){
			return RIOdroid.executeCommand(
						"adb shell am start -n " + Constants.kPackageName + "/" +
								Constants.kPackageName + "." + Constants.kActivityName);
		 }
		 else{
			 return RuntimeExecutor.getInstance().exec(
						"adb shell am start -n " + Constants.kPackageName + "/" +
								Constants.kPackageName + "." + Constants.kActivityName);
		 }
	 }
	 
	 static void adbServerInit() {
		// Initializes RIOdroid usb and RIOadb adb daemon
					if (!isTesting) {
						RIOdroid.init();

						// Forward the port and start the server socket for vision
						RIOdroid.executeCommand("adb reverse tcp:" +
								Constants.kAndroidVisionSocketPort + " tcp:" +
								Constants.kAndroidVisionSocketPort);
						System.out.println("Starting VideoManager");
						//VideoManager.getInstance().start();
					} else {
						RuntimeExecutor.getInstance().init();

						// Forward the port and start the server socket for vision
						RuntimeExecutor.getInstance().exec("adb reverse tc"
								+ "p:" +
								Constants.kAndroidVisionSocketPort + " tcp:" +
								Constants.kAndroidVisionSocketPort);
						System.out.println("Starting VideoManager");
						//VideoManager.getInstance().start(mTesting);
					}
	 }
	 
	 static String toggleFlash(){
			if(!isTesting){
				return RIOdroid.executeCommand("adb shell am broadcast -a "+Constants.kPackageName
						+".GET_DATA --es type flash --ez isFlash "+isFlashOn);
			}else{
				return RuntimeExecutor.getInstance().exec("adb shell am broadcast -a "+Constants.kPackageName
						+".GET_DATA --es type flash --ez isFlash "+isFlashOn);
			}
	 }
	 
	 //first version that seemed to be used
	 static String exec(){
		 if(!isTesting){
				return RIOdroid.executeCommand("adb shell run-as "+Constants.kPackageName+
						" cat /data/data/"+ Constants.kPackageName + "/files/data.json");
			}else{
				return RuntimeExecutor.getInstance().exec("adb shell run-as "+Constants.kPackageName+
						" cat /data/data/"+ Constants.kPackageName + "/files/data.json");
			}
	 }
	 
	 //second version that takes in a string
	 //can be used in different versions
	 static void exec(String string){
		 if(!isTesting){
			 RIOdroid.executeCommand(string);
		 }
		 else{
			 RuntimeExecutor.getInstance().exec(string);
		 }
	 }
	 

}
