package com.palyrobotics.frc2017.vision;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Supplies wrapper methods for using adb to control the Android
 *
 * <h1><b>Fields</b></h1>
 * 	<ul>
 * 		<li>Instance and State variables:
 * 			<ul>
 * 				<li>{@link RuntimeExecutor#s_instance}: Private static instance of this class (Singleton)</li>
 * 			</ul>
 * 		</li>
 * 	</ul>
 *
 * <h1><b>Accessors and Mutators</b></h1>
 * 	<ul>
 * 		<li>{@link RuntimeExecutor#getInstance()}</li>
 * 	</ul>
 *
 * <h1><b>External Access Functions</b>
 * 	<br><BLOCKQUOTE>For using as a wrapper for RIOdroid</BLOCKQUOTE></h1>
 * 	<ul>
 * 		<li>{@link RuntimeExecutor#exec(String)}</li>
 * 		<li>{@link RuntimeExecutor#init()}</li>
 * 	</ul>
 *
 * @author Alvin
 *
 */
public class RuntimeExecutor {

	// Instance and state variables
	private static RuntimeExecutor s_instance;

	/**
	 * Creates an RuntimeExecutor instance
	 * Cannot be called outside as a Singleton
	 */
	private RuntimeExecutor(){}

	/**
	 * @return The instance of the ACB
	 */
	public static RuntimeExecutor getInstance(){
		if(s_instance == null){
			s_instance = new RuntimeExecutor();
		}
		return s_instance;
	}

	/**
	 * Executes a command in the command line during
	 * runtime
	 * @param command Command to execute
	 * @return Console output of executing the command
	 */
	public String exec(String command){

		// Builds the output of the console
		StringBuilder out = new StringBuilder();

		try {
			String line;

			// Execute the command as a process
			Process p = Runtime.getRuntime().exec(command);

			// Read in console output from the process object
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				out.append(line);
				out.append("\n");
			}
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return out.toString();
	}

	/**
	 * Initializes this computer as an adb server
	 */
	public void init(){
		exec("adb start-server");
		CommandExecutor.restartAdbServer();
	}
}
