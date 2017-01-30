package com.palyrobotics.frc2017.robot;

import com.ctre.CANTalon;
import com.mindsensors.CANSD540;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.robot.team254.lib.util.ADXRS453_Gyro;

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
		private static DrivetrainHardware instance = new DrivetrainHardware();

		protected static DrivetrainHardware getInstance() {
			return instance;
		}
		public final CANTalon leftSlaveTalon;
		public final CANTalon leftMasterTalon;
		public final CANTalon rightSlaveTalon;
		public final CANTalon rightMasterTalon;

		// If encoders are wired directly to RIO use the following objects
//		public final Encoder leftEncoder;
//		public final Encoder rightEncoder;
		public final ADXRS453_Gyro gyro;

		private DrivetrainHardware() {
			if (Constants.kRobotName == Constants.RobotName.TYR) {
				leftSlaveTalon = new CANTalon(Constants2016.kTyrLeftDriveFrontMotorDeviceID);
				leftMasterTalon = new CANTalon(Constants2016.kTyrLeftDriveBackMotorDeviceID);
				rightSlaveTalon = new CANTalon(Constants2016.kTyrRightDriveFrontMotorDeviceID);
				rightMasterTalon = new CANTalon(Constants2016.kTyrRightDriveBackMotorDeviceID);
				gyro = new ADXRS453_Gyro();
			} else if(Constants.kRobotName == Constants.RobotName.DERICA) {
				leftMasterTalon = new CANTalon(Constants2016.kDericaLeftDriveMasterDeviceID);
				leftSlaveTalon = new CANTalon(Constants2016.kDericaLeftDriveFrontMotorDeviceID);
				rightMasterTalon = new CANTalon(Constants2016.kDericaRightDriveBackMotorDeviceID);
				rightSlaveTalon = new CANTalon(Constants2016.kDericaRightDriveFrontMotorDeviceID);

				gyro = new ADXRS453_Gyro();
				// no shifter solenoid
			} else if (Constants.kRobotName == Constants.RobotName.AEGIR) {
				leftMasterTalon = new CANTalon(Constants.kAegirLeftDriveMasterDeviceID);
				leftSlaveTalon = new CANTalon(Constants.kAegirLeftDriveSlaveDeviceID);
				rightMasterTalon = new CANTalon(Constants.kAegirRightDriveMasterDeviceID);
				rightSlaveTalon = new CANTalon(Constants.kAegirRightDriveSlaveDeviceID);
				gyro = new ADXRS453_Gyro();
			} else {
				leftMasterTalon = new CANTalon(Constants.kSteikLeftDriveMasterDeviceID);
				leftSlaveTalon = new CANTalon(Constants.kSteikLeftDriveSlaveDeviceID);
				rightMasterTalon = new CANTalon(Constants.kSteikRightDriveMasterDeviceID);
				rightSlaveTalon = new CANTalon(Constants.kSteikRightDriveSlaveDeviceID);
				gyro = new ADXRS453_Gyro();
			}
		}
	}

	/**
	 * FLIPPERS - 2 double solenoids
	 */
	public static class FlippersHardware {
		private static FlippersHardware instance = new FlippersHardware();
		public static FlippersHardware getInstance() {
			return instance;
		}
		public final DoubleSolenoid leftSolenoid, rightSolenoid;

		private FlippersHardware() {
			if (Constants.kRobotName == Constants.RobotName.STEIK) {
				leftSolenoid = new DoubleSolenoid(
						Constants.kSteikLeftFlipperPortExtend, Constants.kSteikLeftFlipperPortRetract);
				rightSolenoid = new DoubleSolenoid(
						Constants.kSteikRightFlipperPortExtend, Constants.kSteikRightFlipperPortRetract);
			} else if (Constants.kRobotName == Constants.RobotName.AEGIR) {
				leftSolenoid = new DoubleSolenoid(
						Constants.kAegirLeftFlipperPortExtend, Constants.kAegirLeftFlipperPortRetract);
				rightSolenoid = new DoubleSolenoid(
						Constants.kAegirRightFlipperPortExtend, Constants.kAegirRightFlipperPortRetract);
			} else {
				leftSolenoid = null;
				rightSolenoid = null;
			}
		}
	}

	/**
	 * SLIDER - 1 TalonSRX
	 */
	public static class SliderHardware {
		private static SliderHardware instance_ = new SliderHardware();
		public static SliderHardware getInstance() {
			return instance_;
		}
		public final CANTalon sliderTalon;
		public final DigitalInput leftHallEffect;
		public final DigitalInput middleHallEffect;
		public final DigitalInput rightHallEffect;

		private SliderHardware() {
			if (Constants.kRobotName == Constants.RobotName.AEGIR) {
				sliderTalon = new CANTalon(Constants.kAegirSliderMotorDeviceID);
				leftHallEffect = new DigitalInput(Constants.kAegirSliderLeftHallEffect);
				middleHallEffect = new DigitalInput(Constants.kAegirSliderMiddleHallEffect);
				rightHallEffect = new DigitalInput(Constants.kAegirSliderRightHallEffect);
			} else if (Constants.kRobotName == Constants.RobotName.STEIK) {
				sliderTalon = new CANTalon(Constants.kSteikSliderMotorDeviceID);
				leftHallEffect = new DigitalInput(Constants.kSteikSliderLeftHallEffect);
				middleHallEffect = new DigitalInput(Constants.kSteikSliderMiddleHallEffect);
				rightHallEffect = new DigitalInput(Constants.kSteikSliderRightHallEffect);
			} else {
				sliderTalon = null;
				leftHallEffect = null;
				middleHallEffect = null;
				rightHallEffect = null;
			}
		}
	}
	/**
	 * SPATULA - one double solenoid
	 */
	public static class SpatulaHardware {
		private static SpatulaHardware instance = new SpatulaHardware();
		public static SpatulaHardware getInstance() {
			return instance;
		}
		public final DoubleSolenoid spatulaSolenoid;

		private SpatulaHardware() {
			if (Constants.kRobotName == Constants.RobotName.STEIK) {
				spatulaSolenoid = new DoubleSolenoid(Constants.kSteikSpatulaPortExtend, Constants.kSteikSpatulaPortRetract);
			} else if (Constants.kRobotName == Constants.RobotName.AEGIR) {
				spatulaSolenoid = new DoubleSolenoid(Constants.kAegirSpatulaPortExtend, Constants.kAegirSpatulaPortRetract);
			} else {
				spatulaSolenoid = null;
			}
		}
	}
	/*
	 * INTAKE - two SD540C motors
	 */
	public static class IntakeHardware {
		private static IntakeHardware instance = new IntakeHardware();

		protected static IntakeHardware getInstance() {
			return instance;
		}
		public final CANSD540 leftIntakeMotor;
		public final CANSD540 rightIntakeMotor;

		private IntakeHardware() {
			if (Constants.kRobotName == Constants.RobotName.AEGIR) {
				leftIntakeMotor = new CANSD540(Constants.kAegirLeftIntakeMotorDeviceID);
				rightIntakeMotor = new CANSD540(Constants.kAegirRightIntakeMotorDeviceID);
			} else if (Constants.kRobotName == Constants.RobotName.STEIK) {
				leftIntakeMotor = new CANSD540(Constants.kSteikLeftIntakeMotorDeviceID);
				rightIntakeMotor = new CANSD540(Constants.kSteikRightIntakeMotorDeviceID);
			} else {
				leftIntakeMotor = null;
				rightIntakeMotor = null;
			}
		}
	}

	/*
	 * CLIMBER - one SD540C motor
	 */
	public static class ClimberHardware {
		private static ClimberHardware instance = new ClimberHardware();

		public static ClimberHardware getInstance() {
			return instance;
		}

		public final CANSD540 climberMotor;
		public final Encoder climberEncoder;

		private ClimberHardware() {
			if (Constants.kRobotName == Constants.RobotName.AEGIR) {
				climberMotor = new CANSD540(Constants.kAegirClimberMotorDeviceID);
				climberEncoder = new Encoder(Constants.kAegirClimberEncoderDIOA, Constants.kAegirClimberEncoderDIOB);
			} else if (Constants.kRobotName == Constants.RobotName.STEIK) {
				climberMotor = new CANSD540(Constants.kSteikClimberMotorDeviceID);
				climberEncoder = new Encoder(Constants.kSteikClimberEncoderDIOA, Constants.kSteikClimberEncoderDIOB);
			} else {
				climberMotor = null;
				climberEncoder = null;
			}
		}
	}

	public final PowerDistributionPanel kPDP = new PowerDistributionPanel();

	// Joysticks for operator interface
	protected static class Joysticks {
		private static Joysticks instance = new Joysticks();

		public static Joysticks getInstance() {
			return instance;
		}

		public final Joystick leftStick = new Joystick(0);
		public final Joystick rightStick = new Joystick(1);
		public final Joystick operatorStick = new Joystick(2);

		private Joysticks() {
		}
	}

	// Wrappers to access hardware groups
	public DrivetrainHardware getDrivetrain() {
		return DrivetrainHardware.getInstance();
	}
	public FlippersHardware getFlippers() {
		return FlippersHardware.getInstance();
	}
	public SliderHardware getSlider() {
		return SliderHardware.getInstance();
	}
	public SpatulaHardware getSpatula() {
		return SpatulaHardware.getInstance();
	}
	public IntakeHardware getIntake() {
		return IntakeHardware.getInstance();
	}
	public ClimberHardware getClimber() {
		return ClimberHardware.getInstance();
	}
	public Joysticks getJoysticks() {
		return Joysticks.getInstance();
	}

	// Singleton set up
	private static final HardwareAdapter instance = new HardwareAdapter();

	public static HardwareAdapter getInstance() {
		return instance;
	}
}