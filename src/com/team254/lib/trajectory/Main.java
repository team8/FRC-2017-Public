package com.team254.lib.trajectory;

import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Gains;
import com.team254.lib.trajectory.io.TextFileSerializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.palyrobotics.frc2017.config.AutoDistances.*;

/**
 * @author Jared341
 * Auto paths configured for Team 8 2017 by Nihar Mitra
 */
public class Main {
	// default values
	private static final double kMaxVel = 180.0/12;
	private static final double kMaxAcc = 120.0/12;
	private static final double kMaxJerk = 50.0;
	private static final double kDt = 0.01;

	// Values pulled from gains
	private static final double kShortVel = Gains.kSteikShortDriveMotionMagicCruiseVelocity/(Constants.kDriveSpeedUnitConversion*12);
	private static final double kShortAccel = Gains.kSteikShortDriveMotionMagicMaxAcceleration/(Constants.kDriveSpeedUnitConversion*12);
	// 180, 120 in/s
	private static final double kLongVel = 60.0/12;
	private static final double kLongAccel = 60.0/12;
	public static double kTurnAngle = Math.PI/3;

	// Forward distance needs to go 110-30 minimum

	public static WaypointSequence.Waypoint getWaypoint(double x, double y, double angle) {
		return new WaypointSequence.Waypoint(y, x, angle);
	}
	
	
	public static void main(String[] args) {
		String directory = "paths";
		if (args.length >= 1) {
			directory = args[0];
		}

		TrajectoryGenerator.Config config = new TrajectoryGenerator.Config();

		// 100 Hz is 0.01, 200 Hz currently in SubsystemLooper
		config.dt = kDt;
		// feet/s/s feet/s/s/s
		config.max_acc = kMaxAcc;
		config.max_jerk = kMaxJerk;
		// 180 inches per second
		config.max_vel = kMaxVel;

		// from Steik CAD, 26.375 inches roughly
		final double kWheelbaseWidth = 26.375 / 12;

		/*
			RED AUTONOMOUS PATHS
		 */
		{
			config.dt = kDt;
			config.max_acc = kLongAccel;
			config.max_jerk = 50.0;
			config.max_vel = kLongVel;
			// Path name must be a valid Java class name.
			final String path_name = "RedLoading";
			// turn right

			// Description of this auto mode path.
			WaypointSequence p = new WaypointSequence(10);
			p.addWaypoint(new WaypointSequence.Waypoint(0, 0, 0));
//			p.addWaypoint(new WaypointSequence.Waypoint(50.0/12, 0, 0));
			p.addWaypoint(new WaypointSequence.Waypoint(
					kRedLoadingPegX/12.0,
					-kRedLoadingPegY/12.0, -kTurnAngle));

			Path path = PathGenerator.makePath(p, config,
					kWheelbaseWidth, path_name);

			// Outputs to the directory supplied as the first argument.
			TextFileSerializer js = new TextFileSerializer();
			String serialized = js.serialize(path);
//			System.out.print(serialized);
			String fullpath = joinPath(directory, path_name + ".txt");
			if (!writeFile(fullpath, serialized)) {
				System.err.println(fullpath + " could not be written!!!!");
				System.exit(1);
			} else {
				System.out.println("Wrote " + fullpath);
			}
		}
		
		{
			config.dt = kDt;
			config.max_acc = kLongAccel;
			config.max_jerk = 50.0;
			config.max_vel = kLongVel;
			// Path name must be a valid Java class name.
			final String path_name = "CenterGoToNeutral";
			// turn right
	
			// Description of this auto mode path.
			WaypointSequence p = new WaypointSequence(10);

		    p.addWaypoint(new WaypointSequence.Waypoint(0, 0, 0));
		    p.addWaypoint(getWaypoint(8, -6, Math.PI/3));
		    p.addWaypoint(getWaypoint(10, -3,0));
		    p.addWaypoint(getWaypoint(10, 10, 0));
//		    p.addWaypoint(getWaypoint(6,17, 0));
//			p.addWaypoint(new WaypointSequence.Waypoint(7, 15, 0));
	
			Path path = PathGenerator.makePath(p, config,
					kWheelbaseWidth, path_name);
	
			// Outputs to the directory supplied as the first argument.
			TextFileSerializer js = new TextFileSerializer();
			String serialized = js.serialize(path);
	//		System.out.print(serialized);
			String fullpath = joinPath(directory, path_name + ".txt");
			if (!writeFile(fullpath, serialized)) {
				System.err.println(fullpath + " could not be written!!!!");
				System.exit(1);
			} else {
				System.out.println("Wrote " + fullpath);
			}
		}

		{
			config.dt = kDt;
			config.max_acc = kLongAccel;
			config.max_jerk = 50.0;
			config.max_vel = kLongVel;
			// Path name must be a valid Java class name.
			final String path_name = "RedBoiler";

			// Description of this auto mode path.
			// turn left
			WaypointSequence p = new WaypointSequence(10);
			p.addWaypoint(new WaypointSequence.Waypoint(0, 0, 0));
			p.addWaypoint(new WaypointSequence.Waypoint(
					kRedBoilerPegX/12.0,
					kRedBoilerPegY/12.0, kTurnAngle));

			Path path = PathGenerator.makePath(p, config,
					kWheelbaseWidth, path_name);

			// Outputs to the directory supplied as the first argument.
			TextFileSerializer js = new TextFileSerializer();
			String serialized = js.serialize(path);
			//System.out.print(serialized);
			String fullpath = joinPath(directory, path_name + ".txt");
			if (!writeFile(fullpath, serialized)) {
				System.err.println(fullpath + " could not be written!!!!");
				System.exit(1);
			} else {
				System.out.println("Wrote " + fullpath);
			}
		}


		{
			config.dt = kDt;
			config.max_acc = kLongAccel;
			config.max_jerk = 50.0;
			config.max_vel = kLongVel;
			// Path name must be a valid Java class name.
			final String path_name = "RedCenter";

			// Description of this auto mode path.
			WaypointSequence p = new WaypointSequence(10);
			p.addWaypoint(new WaypointSequence.Waypoint(0, 0, 0));
			p.addWaypoint(new WaypointSequence.Waypoint(10, 0, 0));

			Path path = PathGenerator.makePath(p, config,
					kWheelbaseWidth, path_name);

			// Outputs to the directory supplied as the first argument.
			TextFileSerializer js = new TextFileSerializer();
			String serialized = js.serialize(path);
			//System.out.print(serialized);
			String fullpath = joinPath(directory, path_name + ".txt");
			if (!writeFile(fullpath, serialized)) {
				System.err.println(fullpath + " could not be written!!!!1");
				System.exit(1);
			} else {
				System.out.println("Wrote " + fullpath);
			}
		}

		/*
			BLUE AUTONOMOUS PATHS
		 */
		{
			config.dt = kDt;
			config.max_acc = kLongAccel;
			config.max_jerk = 50.0;
			config.max_vel = kLongAccel;
			// Path name must be a valid Java class name.
			final String path_name = "BlueLoading";
			// Description of this auto mode path.
			// turn left
			WaypointSequence p = new WaypointSequence(10);
			p.addWaypoint(new WaypointSequence.Waypoint(0, 0, 0));
			p.addWaypoint(new WaypointSequence.Waypoint(kBlueLoadingPegX/12.0,
					kBlueLoadingPegY/12.0, kTurnAngle));

			Path path = PathGenerator.makePath(p, config,
					kWheelbaseWidth, path_name);

			// Outputs to the directory supplied as the first argument.
			TextFileSerializer js = new TextFileSerializer();
			String serialized = js.serialize(path);
//			System.out.print(serialized);
			String fullpath = joinPath(directory, path_name + ".txt");
			if (!writeFile(fullpath, serialized)) {
				System.err.println(fullpath + " could not be written!!!!");
				System.exit(1);
			} else {
				System.out.println("Wrote " + fullpath);
			}
		}

		{
			config.dt = kDt;
			config.max_acc = kLongAccel;
			config.max_jerk = 50.0;
			config.max_vel = kLongVel;
			// Path name must be a valid Java class name.
			final String path_name = "BlueBoiler";

			// Description of this auto mode path.
			// turn right
			WaypointSequence p = new WaypointSequence(10);
			p.addWaypoint(new WaypointSequence.Waypoint(0, 0, 0));
			p.addWaypoint(new WaypointSequence.Waypoint(kBlueBoilerPegX/12.0,
					-kBlueBoilerPegY/12.0, -kTurnAngle));

			Path path = PathGenerator.makePath(p, config,
					kWheelbaseWidth, path_name);

			// Outputs to the directory supplied as the first argument.
			TextFileSerializer js = new TextFileSerializer();
			String serialized = js.serialize(path);
			//System.out.print(serialized);
			String fullpath = joinPath(directory, path_name + ".txt");
			if (!writeFile(fullpath, serialized)) {
				System.err.println(fullpath + " could not be written!!!!");
				System.exit(1);
			} else {
				System.out.println("Wrote " + fullpath);
			}
		}


		{
			config.dt = kDt;
			config.max_acc = kLongAccel;
			config.max_jerk = 50.0;
			config.max_vel = kLongVel;
			// Path name must be a valid Java class name.
			final String path_name = "BlueCenter";

			// Description of this auto mode path.
			WaypointSequence p = new WaypointSequence(10);
			p.addWaypoint(new WaypointSequence.Waypoint(0, 0, 0));
			p.addWaypoint(new WaypointSequence.Waypoint(kBlueCenter, 0, 0));

			Path path = PathGenerator.makePath(p, config,
					kWheelbaseWidth, path_name);

			// Outputs to the directory supplied as the first argument.
			TextFileSerializer js = new TextFileSerializer();
			String serialized = js.serialize(path);
			//System.out.print(serialized);
			String fullpath = joinPath(directory, path_name + ".txt");
			if (!writeFile(fullpath, serialized)) {
				System.err.println(fullpath + " could not be written!!!!1");
				System.exit(1);
			} else {
				System.out.println("Wrote " + fullpath);
			}
		}

		/*
			OTHER PATHS
		 */

		{
			config.dt = kDt;
			config.max_acc = kLongAccel;
			config.max_jerk = 50.0;
			config.max_vel = kLongVel;
			// Path name must be a valid Java class name.
			final String path_name = "RightSideDriveToNeutral";

			// Description of this auto mode path.
			WaypointSequence p = new WaypointSequence(10);
			p.addWaypoint(new WaypointSequence.Waypoint(0,0,0));
			p.addWaypoint(new WaypointSequence.Waypoint(6,-8,-Math.PI/3));
			p.addWaypoint(new WaypointSequence.Waypoint(8,-14,-Math.PI/2));
			Path path = PathGenerator.makePath(p, config,
					kWheelbaseWidth, path_name);

			// Outputs to the directory supplied as the first argument.
			TextFileSerializer js = new TextFileSerializer();
			String serialized = js.serialize(path);
			//System.out.print(serialized);
			String fullpath = joinPath(directory, path_name + ".txt");
			if (!writeFile(fullpath, serialized)) {
				System.err.println(fullpath + " could not be written!!!!1");
				System.exit(1);
			} else {
				System.out.println("Wrote " + fullpath);
			}
		}

		{
			config.dt = kDt;
			config.max_acc = kLongAccel;
			config.max_jerk = 50.0;
			config.max_vel = kLongVel;
			// Path name must be a valid Java class name.
			final String path_name = "LeftSideDriveToNeutral";

			// Description of this auto mode path.
			WaypointSequence p = new WaypointSequence(10);
			p.addWaypoint(new WaypointSequence.Waypoint(0, 0, 0+Math.PI/3));
//			p.addWaypoint(new WaypointSequence.Waypoint(-50*Math.sin(Math.PI/6), 50*Math.cos(Math.PI/6), 0));
//			p.addWaypoint(new WaypointSequence.Waypoint(20/12, 20/12, 5*Math.PI/12));
			p.addWaypoint(new WaypointSequence.Waypoint(100/12, 40/12, Math.PI/3-Math.PI/3));
			p.addWaypoint(new WaypointSequence.Waypoint(200/12, 40/12, Math.PI/3-Math.PI/3));
			Path path = PathGenerator.makePath(p, config,
					kWheelbaseWidth, path_name);

			// Outputs to the directory supplied as the first argument.
			TextFileSerializer js = new TextFileSerializer();
			String serialized = js.serialize(path);
			//System.out.print(serialized);
			String fullpath = joinPath(directory, path_name + ".txt");
			if (!writeFile(fullpath, serialized)) {
				System.err.println(fullpath + " could not be written!!!!1");
				System.exit(1);
			} else {
				System.out.println("Wrote " + fullpath);
			}
		}

		{
			config.dt = kDt;
			config.max_acc = kLongAccel;
			config.max_jerk = 50.0;
			config.max_vel = kLongVel;
			// Path name must be a valid Java class name.
			final String path_name = "Baseline";

			// Description of this auto mode path.
			WaypointSequence p = new WaypointSequence(10);
			p.addWaypoint(new WaypointSequence.Waypoint(0, 0, 0));
			p.addWaypoint(new WaypointSequence.Waypoint(100.0/12, 0, 0));

			Path path = PathGenerator.makePath(p, config,
					kWheelbaseWidth, path_name);

			// Outputs to the directory supplied as the first argument.
			TextFileSerializer js = new TextFileSerializer();
			String serialized = js.serialize(path);
			//System.out.print(serialized);
			String fullpath = joinPath(directory, path_name + ".txt");
			if (!writeFile(fullpath, serialized)) {
				System.err.println(fullpath + " could not be written!!!!1");
				System.exit(1);
			} else {
				System.out.println("Wrote " + fullpath);
			}
		}
	}
	public static String joinPath(String path1, String path2) {
		File file1 = new File(path1);
		File file2 = new File(file1, path2);
		return file2.getPath();
	}

	private static boolean writeFile(String path, String data) {
		try {
			File file = new File(path);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(data);
			bw.close();
		} catch (IOException e) {
			return false;
		}

		return true;
	}
}
