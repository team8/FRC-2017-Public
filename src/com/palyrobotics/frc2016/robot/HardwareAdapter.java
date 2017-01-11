package com.palyrobotics.frc2016.robot;

import com.palyrobotics.frc2016.config.RobotState;
import com.ctre.CANTalon;
import com.palyrobotics.frc2016.config.Constants;
import com.palyrobotics.frc2016.util.XboxController;
import com.palyrobotics.frc2016.robot.team254.lib.util.CheesySpeedController;

import edu.wpi.first.wpilibj.*;
//TODO: Set the DPP's somehow

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
	 * DRIVETRAIN
	 */
	public static class DrivetrainHardware {
		private static DrivetrainHardware mInstance = new DrivetrainHardware();

		protected static DrivetrainHardware getInstance() {
			return mInstance;
		}

		public final CheesySpeedController kLeftDriveMotor;
		public final CheesySpeedController kRightDriveMotor;
		public final Encoder kLeftDriveEncoder;
		public final Encoder kRightDriveEncoder;
		public final ADXRS450_Gyro kGyro;
		public final DoubleSolenoid kShifterSolenoid;

		private DrivetrainHardware() {
			if (Constants.kRobotName == Constants.RobotName.TYR) {
				kLeftDriveMotor = new CheesySpeedController(
						new SpeedController[]{new CANTalon(Constants.kTyrLeftDriveFrontMotorDeviceID),
								new CANTalon(Constants.kTyrLeftDriveBackMotorDeviceID)},
						new int[]{Constants.kTyrLeftDriveFrontMotorPDP, Constants.kTyrLeftDriveBackMotorPDP});
				kRightDriveMotor = new CheesySpeedController(
						new SpeedController[]{new CANTalon(Constants.kTyrRightDriveFrontMotorDeviceID),
								new CANTalon(Constants.kTyrRightDriveBackMotorDeviceID)},
						new int[]{Constants.kTyrRightDriveBackMotorPDP, Constants.kTyrRightDriveBackMotorPDP});
				kLeftDriveEncoder = new Encoder(
						Constants.kTyrLeftDriveEncoderDIOA, Constants.kTyrLeftDriveEncoderDIOB);
				kRightDriveEncoder = new Encoder(
						Constants.kTyrRightDriveEncoderDIOA, Constants.kTyrRightDriveEncoderDIOB);
				kGyro = new ADXRS450_Gyro();
				kShifterSolenoid = new DoubleSolenoid(
						Constants.kTyrDriveSolenoidExtend, Constants.kTyrDriveSolenoidRetract);
			} else {
				CANTalon leftDriveBackMotor = new CANTalon(Constants.kDericaLeftDriveBackMotorDeviceID);
				leftDriveBackMotor.enableBrakeMode(true);
				CANTalon leftDriveFrontMotor = new CANTalon(Constants.kDericaLeftDriveFrontMotorDeviceID);
				leftDriveFrontMotor.enableBrakeMode(true);
				CANTalon rightDriveBackMotor = new CANTalon(Constants.kDericaRightDriveBackMotorDeviceID);
				rightDriveBackMotor.enableBrakeMode(true);
				CANTalon rightDriveFrontMotor = new CANTalon(Constants.kDericaRightDriveFrontMotorDeviceID);
				rightDriveFrontMotor.enableBrakeMode(true);
				kLeftDriveMotor = new CheesySpeedController(
						new SpeedController[]{leftDriveFrontMotor, leftDriveBackMotor},
						new int[]{Constants.kDericaLeftDriveFrontMotorPDP, Constants.kDericaLeftDriveBackMotorPDP});
				kRightDriveMotor = new CheesySpeedController(
						new SpeedController[]{rightDriveFrontMotor, rightDriveBackMotor},
						new int[]{Constants.kDericaRightDriveBackMotorPDP, Constants.kDericaRightDriveBackMotorPDP});
				kLeftDriveEncoder = new Encoder(
						Constants.kDericaLeftDriveEncoderDIOA, Constants.kDericaLeftDriveEncoderDIOB, true);
				kRightDriveEncoder = new Encoder(
						Constants.kDericaRightDriveEncoderDIOA, Constants.kDericaRightDriveEncoderDIOB);
				kGyro = new ADXRS450_Gyro();
				// no shifter solenoid
				kShifterSolenoid = null;
			}
		}
	}

	/*
	 * INTAKE - has some null hardware components
	 */
	public static class IntakeHardware {
		private static IntakeHardware mInstance = new IntakeHardware();

		protected static IntakeHardware getInstance() {
			return mInstance;
		}
		public final CheesySpeedController kLeftIntakeMotor;
		public final CheesySpeedController kRightIntakeMotor;
		public final CheesySpeedController kIntakeArmMotor;
		public final AnalogPotentiometer kIntakeArmPotentiometer;

		private IntakeHardware() {
			if (Constants.kRobotName == Constants.RobotName.TYR) {
				kLeftIntakeMotor = new CheesySpeedController(
						new VictorSP(Constants.kTyrLeftIntakeMotorDeviceID),
						Constants.kTyrLeftIntakeMotorPDP);
				kRightIntakeMotor = new CheesySpeedController(
						new VictorSP(Constants.kTyrRightIntakeMotorDeviceID),
						Constants.kTyrRightIntakeMotorPDP);
				kIntakeArmMotor = null;
				kIntakeArmPotentiometer = null;
			} else {
				kLeftIntakeMotor = new CheesySpeedController(
						new CANTalon(Constants.kDericaIntakeMotorPWM),
						Constants.kDericaIntakeMotorPDP);
				kRightIntakeMotor = kLeftIntakeMotor;
				kIntakeArmMotor = new CheesySpeedController(
						new CANTalon(Constants.kDericaArmIntakeMotorPWM),
						Constants.kDericaArmIntakeMotorPDP);
				kIntakeArmPotentiometer = null;
			}
		}
	}

	/*
	 * SHOOTER/CATAPULT
	 * TyrShooter comes with Grabber
	 */
	public static class ShooterHardware {
		private static ShooterHardware mInstance = new ShooterHardware();

		public static ShooterHardware getInstance() {
			return mInstance;
		}

		// Pneumatic solenoids, only instantiate if Tyr
		public final DoubleSolenoid kPistonSolenoid;
		public final DoubleSolenoid kLatchSolenoid;
		public final DoubleSolenoid kGrabberSolenoid;
		public final CheesySpeedController kShooterMotor;
		public final AnalogPotentiometer kShooterPotentiometer;

		private ShooterHardware() {
			if (Constants.kRobotName == Constants.RobotName.TYR) {
				kPistonSolenoid = new DoubleSolenoid(
						Constants.kShooterSolenoidPortExtend, Constants.kShooterSolenoidPortRetract);
				kLatchSolenoid = new DoubleSolenoid(
						Constants.kLatchSolenoidPortExtend, Constants.kLatchSolenoidPortRetract);
				kGrabberSolenoid = new DoubleSolenoid(
						Constants.kGrabberSolenoidPortExtend, Constants.kGrabberSolenoidPortRetract);
				kShooterMotor = new CheesySpeedController(new CANTalon(Constants.kTyrShooterMotorDeviceID),
						Constants.kTyrShooterMotorPDP);
				kShooterPotentiometer = new AnalogPotentiometer(Constants.kTyrShooterPotentiometerPort);
			} else {
				kPistonSolenoid = null;
				kLatchSolenoid = null;
				kGrabberSolenoid = null;
				kShooterMotor = null;
				kShooterPotentiometer = null;
			}

		}
	}

	/*
	 * BREACHER
	 */
	public static class BreacherHardware {
		private static BreacherHardware mInstance = new BreacherHardware();

		public static BreacherHardware getInstance() {
			return mInstance;
		}
		public final CheesySpeedController kBreacherMotor;

		private BreacherHardware() {
			if (Constants.kRobotName == Constants.RobotName.TYR) {
				kBreacherMotor = new CheesySpeedController(new CANTalon(Constants.kBreacherMotorDeviceID), Constants.kBreacherMotorPDP);
			} else {
				kBreacherMotor = null;
			}
		}
	}
	
	public static class LowGoalShooterHardware {
		private static LowGoalShooterHardware mInstance = new LowGoalShooterHardware();
		
		public static LowGoalShooterHardware getInstance() {
			return mInstance;
		}
		public final CheesySpeedController kLowGoalShooterMotor;
		
		private LowGoalShooterHardware() {
			if(Constants.kRobotName == Constants.RobotName.DERICA) {
				kLowGoalShooterMotor = new CheesySpeedController(new Victor(Constants.kDericaLowGoalShooterPWM), Constants.kDericaLowGoalShooterPDP);
			} else {
				kLowGoalShooterMotor = null;
			}
		}
	}
	
	public final static PowerDistributionPanel kPDP = new PowerDistributionPanel();

	// Joysticks for operator interface
	protected static class Joysticks {
		private static Joysticks mInstance = new Joysticks();

		public static Joysticks getInstance() {
			return mInstance;
		}

		public final Joystick kLeftStick = new Joystick(0);
		public final Joystick kRightStick = new Joystick(1);
		public final Joystick kOperatorStick;

		public Joysticks() {
			if (Constants.kRobotName == Constants.RobotName.TYR) {
				kOperatorStick = new XboxController(2);
			} else {
				kOperatorStick = new Joystick(2);
			}
		}
	}

	// Wrappers to access hardware groups
	public DrivetrainHardware getDrivetrain() {
		return DrivetrainHardware.getInstance();
	}

	public IntakeHardware getIntake() {
		return IntakeHardware.getInstance();
	}

	public ShooterHardware getShooter() {
		return ShooterHardware.getInstance();
	}

	public BreacherHardware getBreacher() {
		return BreacherHardware.getInstance();
	}

	public LowGoalShooterHardware getLowGoalShooter() {
		return LowGoalShooterHardware.getInstance();
	}
	
	public Joysticks getJoysticks() {
		return Joysticks.getInstance();
	}

	// Singleton set up
	private static final HardwareAdapter mInstance = new HardwareAdapter();

	public static HardwareAdapter getInstance() {
		return mInstance;
	}
}