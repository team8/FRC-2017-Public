package com.palyrobotics.frc2018.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.palyrobotics.frc2018.config.Constants;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
/**
 * Represents all hardware components of the robot.
 * Singleton class. Should only be used in robot package, and 254lib.
 * Subdivides hardware into subsystems.
 * Example call: HardwareAdapter.getInstance().getDrivetrain().getLeftMotor()
 *
 * @author Nihar
 */
public class HardwareAdapter {
	// Hardware components at the top for maintenance purposes, variables and getters at bottom
	/* 
	 * DRIVETRAIN - 6 WPI_TalonSRX's
	 */
	public static class DrivetrainHardware {
		private static DrivetrainHardware instance = new DrivetrainHardware();

		protected static DrivetrainHardware getInstance() {
			return instance;
		}
		public final WPI_TalonSRX leftSlave1Talon;
		public final WPI_TalonSRX leftMasterTalon;
		public final WPI_TalonSRX leftSlave2Talon;
		public final WPI_TalonSRX rightSlave1Talon;
		public final WPI_TalonSRX rightMasterTalon;
		public final WPI_TalonSRX rightSlave2Talon;

		public final PigeonIMU gyro;

		public static void resetSensors() {
			instance.gyro.setYaw(0, 0);
			instance.gyro.setFusedHeading(0, 0);
			instance.gyro.setCompassAngle(0, 0);
			instance.gyro.setCompassDeclination(0, 0);
			instance.gyro.setAccumZAngle(0, 0);
			instance.leftMasterTalon.setSelectedSensorPosition(0, 0, 0);
			instance.rightMasterTalon.setSelectedSensorPosition(0, 0, 0);
		}

		private DrivetrainHardware() {
				leftMasterTalon = new WPI_TalonSRX(Constants.k2018_UnnamedLeftDriveMasterDeviceID);
				leftSlave1Talon = new WPI_TalonSRX(Constants.k2018_UnnamedLeftDriveSlaveDeviceID);
				leftSlave2Talon = new WPI_TalonSRX(Constants.k2018_UnnamedLeftDriveOtherSlaveDeviceID);
				rightMasterTalon = new WPI_TalonSRX(Constants.k2018_UnnamedRightDriveMasterDeviceID);
				rightSlave1Talon = new WPI_TalonSRX(Constants.k2018_UnnamedRightDriveSlaveDeviceID);
				rightSlave2Talon = new WPI_TalonSRX(Constants.k2018_UnnamedRightDriveOtherSlaveDeviceID);
				gyro = new PigeonIMU(leftSlave2Talon);
		}
	}


	// Joysticks for operator interface
	protected static class Joysticks {
		private static Joysticks instance = new Joysticks();

		public static Joysticks getInstance() {
			return instance;
		}

		public final Joystick driveStick = new Joystick(0);
		public final Joystick turnStick = new Joystick(1);

		private Joysticks() {
		}
	}

	// Wrappers to access hardware groups
	public DrivetrainHardware getDrivetrain() {
		return DrivetrainHardware.getInstance();
	}

	public Joysticks getJoysticks() {
		return Joysticks.getInstance();
	}
	
	public final PowerDistributionPanel kPDP = new PowerDistributionPanel();

	// Singleton set up
	private static final HardwareAdapter instance = new HardwareAdapter();

	public static HardwareAdapter getInstance() {
		return instance;
	}
}