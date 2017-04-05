package com.team254.lib.trajectory;

import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Gains;
import com.team254.lib.trajectory.io.TextFileSerializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Jared341
 * Auto paths configured for Team 8 2017 by Nihar Mitra
 * Minimum distance exists
 */
public class Main {
	// default values
	private static final double kMaxAcc = 120/12;
	private static final double kMaxJerk = 60.0;
	private static final double kMaxVel = 200/12;
	private static final double kDt = 0.005;

	// Values pulled from gains
	private static final double kShortVel = Gains.kSteikShortDriveMotionMagicCruiseVelocity/(Constants.kDriveSpeedUnitConversion*12);
	private static final double kShortAccel = Gains.kSteikShortDriveMotionMagicMaxAcceleration/(Constants.kDriveSpeedUnitConversion*12);
	private static final double kLongVel = Gains.kSteikLongDriveMotionMagicCruiseVelocity/(Constants.kDriveSpeedUnitConversion*12);
	private static final double kLongAccel = Gains.kSteikLongDriveMotionMagicMaxAcceleration/(Constants.kDriveSpeedUnitConversion*12);

	// Distances in feet, angles in radians
	public static double kBackup = -0.8; // TODO: Why doesn't 10/12 work?
	public static double kRedCenter = 82/12;
	public static double kBlueCenter = 82/12;
	public static double kTurnAngle = Math.PI/3;

	// Blue right loading station
	public static double kBlueLoadingStationForward = 79.5/12; // 79.5
	public static double kBlueLoadingStationAirship = 73/12; // 66, 70, 73

	// Red left loading station
	public static double kRedLoadingStationForward = 79/12; // 79
	public static double kRedLoadingStationAirship = 73/12; // 66, 70, 73

	// Blue left boiler
	public static double kBlueBoilerForward = 82.5/12; // 79.5, 82.5
	public static double kBlueBoilerAirship = 73/12; // 73,

	// Red right boiler
	public static double kRedBoilerForward = 84/12; // 79, 84
	public static double kRedBoilerAirship = 73/12; // 73,


	public static void main(String[] args) {
		String directory = "../FRC-2017/paths";
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
			p.addWaypoint(new WaypointSequence.Waypoint(kRedLoadingStationForward, 0, kTurnAngle));
			p.addWaypoint(new WaypointSequence.Waypoint(
					(kRedLoadingStationForward +
							Math.cos(kTurnAngle)*kRedLoadingStationAirship),
					(Math.sin(kTurnAngle)* kRedLoadingStationAirship), kTurnAngle));

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
			final String path_name = "RedBoiler";

			// Description of this auto mode path.
			// turn left
			WaypointSequence p = new WaypointSequence(10);
			p.addWaypoint(new WaypointSequence.Waypoint(0, 0, 0));
			p.addWaypoint(new WaypointSequence.Waypoint(kRedBoilerForward, 0, -kTurnAngle));
			p.addWaypoint(new WaypointSequence.Waypoint(
					kRedBoilerForward+Math.cos(kTurnAngle)*kRedBoilerAirship,
					-Math.sin(kTurnAngle)*kRedBoilerAirship, -kTurnAngle));

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
			p.addWaypoint(new WaypointSequence.Waypoint(kRedCenter, 0, 0));

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
			p.addWaypoint(new WaypointSequence.Waypoint(kBlueLoadingStationForward, 0, -kTurnAngle));
			p.addWaypoint(new WaypointSequence.Waypoint(
					kBlueLoadingStationForward+Math.cos(kTurnAngle)*kBlueLoadingStationAirship,
					kBlueLoadingStationAirship*Math.sin(kTurnAngle), -kTurnAngle));

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
			p.addWaypoint(new WaypointSequence.Waypoint(kBlueBoilerForward, 0, kTurnAngle));
			p.addWaypoint(new WaypointSequence.Waypoint(
					kBlueBoilerForward+Math.cos(kTurnAngle)*kBlueBoilerAirship,
					kBlueBoilerAirship*Math.sin(kTurnAngle), kTurnAngle));

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
			// Remember that this is for the GO LEFT CASE!
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
			config.max_acc = kShortAccel;
			config.max_jerk = 50.0;
			config.max_vel = kShortVel;
			// Path name must be a valid Java class name.
			final String path_name = "Backup";

			// Description of this auto mode path.
			WaypointSequence p = new WaypointSequence(10);
			p.addWaypoint(new WaypointSequence.Waypoint(0, 0, 0));
			p.addWaypoint(new WaypointSequence.Waypoint(-0.8, 0, 0));
			p.addWaypoint(new WaypointSequence.Waypoint(0, 0, 0));
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
			p.addWaypoint(new WaypointSequence.Waypoint(100/12, 0, 0));

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
