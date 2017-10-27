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

		 exec("adb reverse tcp:" +
			 Constants.kVideoPort + " tcp:" +
			 Constants.kVideoPort);

		 System.out.println("Starting VideoManager");
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
